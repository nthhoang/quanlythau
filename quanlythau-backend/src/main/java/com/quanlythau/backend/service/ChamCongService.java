package com.quanlythau.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanlythau.backend.dto.chamcong.ChamCongRequest;
import com.quanlythau.backend.dto.chamcong.ChamCongResponse;
import com.quanlythau.backend.dto.chamcong.ChamCongThuCongRequest;
import com.quanlythau.backend.dto.chamcong.TrangThaiThoResponse;
import com.quanlythau.backend.entity.ChamCong;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.LoaiCong;
import com.quanlythau.backend.entity.LoaiThongBao;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.ChamCongRepository;
import com.quanlythau.backend.repository.CongTrinhRepository;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.PhanCongCongTrinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChamCongService {

    private final ChamCongRepository chamCongRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final CongTrinhRepository congTrinhRepository;
    private final PhanCongCongTrinhRepository phanCongRepository;
    private final ObjectMapper objectMapper;
    private final CaiDatService caiDatService;
    private final ThongBaoService thongBaoService;

    @Value("${cham-cong.nguong-khuon-mat}")
    private double nguongKhuonMat;

    public ChamCongResponse chamCong(ChamCongRequest request) {
        LocalDate homNay = LocalDate.now();

        NguoiDung tho = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));

        if (chamCongRepository.findByNguoiDungIdAndNgayChamCongAndCongTrinhId(tho.getId(), homNay, request.getCongTrinhId()).isPresent()) {
            throw new BadRequestException(tho.getHoTen() + " đã chấm công tại công trình này hôm nay rồi");
        }

        CongTrinh congTrinh = congTrinhRepository.findById(request.getCongTrinhId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình"));

        boolean coPhanCong = phanCongRepository.findByNguoiDungIdAndNgayKetThucIsNull(tho.getId()).stream()
                .anyMatch(pc -> pc.getCongTrinh().getId().equals(congTrinh.getId()));
        if (!coPhanCong) {
            throw new BadRequestException(tho.getHoTen() + " chưa được phân công vào công trình này");
        }

        if (tho.getFaceDescriptor() == null) {
            throw new BadRequestException(tho.getHoTen() + " chưa thiết lập khuôn mặt");
        }

        List<Double> guongGoc = docFaceDescriptor(tho.getFaceDescriptor());
        double khoangCachGuong = tinhKhoangCachKhuonMat(guongGoc, request.getFaceDescriptor());
        TrangThaiXacThuc trangThai = khoangCachGuong <= nguongKhuonMat
                ? TrangThaiXacThuc.TU_DONG_XAC_NHAN
                : TrangThaiXacThuc.CAN_DUYET;

        boolean coTangCa = Boolean.TRUE.equals(request.getCoTangCa());

        ChamCong chamCong = ChamCong.builder()
                .nguoiDung(tho)
                .congTrinh(congTrinh)
                .ngayChamCong(homNay)
                .loaiCong(request.getLoaiCong())
                .coTangCa(coTangCa)
                .donGiaApDung(tho.getDonGiaCong())
                .phanTramTangCaApDung(coTangCa ? caiDatService.layCaiDat().getPhanTramTangCa() : null)
                .gioCheckIn(LocalDateTime.now())
                .anhChamCong(request.getAnhChamCong())
                .khoangCachGuong(khoangCachGuong)
                .trangThaiXacThuc(trangThai)
                .ghiChu(request.getGhiChu())
                .build();

        chamCongRepository.save(chamCong);

        if (trangThai == TrangThaiXacThuc.CAN_DUYET) {
            nguoiDungRepository.findByVaiTro(VaiTro.CHU_THAU).forEach(chuThau ->
                    thongBaoService.tao(
                            chuThau,
                            LoaiThongBao.CHAM_CONG_CAN_DUYET,
                            "Có chấm công cần duyệt",
                            tho.getHoTen() + " chấm công tại " + congTrinh.getTenCongTrinh() + " nhưng khuôn mặt không khớp đủ, cần bạn duyệt tay"
                    )
            );
        }

        return toResponse(chamCong);
    }

    @Transactional(readOnly = true)
    public List<TrangThaiThoResponse> layTrangThaiTheoCongTrinh(Long congTrinhId) {
        LocalDate homNay = LocalDate.now();
        return phanCongRepository.findByCongTrinhIdAndNgayKetThucIsNull(congTrinhId).stream()
                .map(pc -> pc.getNguoiDung())
                .distinct()
                .map(tho -> {
                    List<ChamCong> tatCaHomNay = chamCongRepository.findByNguoiDungIdAndNgayChamCong(tho.getId(), homNay);

                    ChamCong taiDay = tatCaHomNay.stream()
                            .filter(c -> c.getCongTrinh().getId().equals(congTrinhId))
                            .findFirst()
                            .orElse(null);

                    List<ChamCongResponse> noiKhac = tatCaHomNay.stream()
                            .filter(c -> !c.getCongTrinh().getId().equals(congTrinhId))
                            .map(this::toResponse)
                            .collect(Collectors.toList());

                    return TrangThaiThoResponse.builder()
                            .nguoiDungId(tho.getId())
                            .hoTen(tho.getHoTen())
                            .daThietLapKhuonMat(tho.getFaceDescriptor() != null)
                            .daChamCongTaiDay(taiDay != null)
                            .chamCongTaiDay(taiDay != null ? toResponse(taiDay) : null)
                            .chamCongNoiKhacHomNay(noiKhac)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChamCongResponse> layLichSuCuaToi(Long nguoiDungId) {
        return chamCongRepository.findByNguoiDungIdAndNgayChamCongBetween(
                        nguoiDungId, LocalDate.now().minusMonths(2), LocalDate.now())
                .stream()
                .sorted(java.util.Comparator.comparing(ChamCong::getNgayChamCong).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ChamCongResponse chamCongThuCong(ChamCongThuCongRequest request) {
        NguoiDung tho = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        CongTrinh congTrinh = congTrinhRepository.findById(request.getCongTrinhId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình"));

        boolean coPhanCong = phanCongRepository.findByNguoiDungId(tho.getId()).stream()
                .anyMatch(pc -> pc.getCongTrinh().getId().equals(congTrinh.getId()));
        if (!coPhanCong) {
            throw new BadRequestException("Thợ này chưa từng được phân công vào công trình này");
        }

        ChamCong chamCong = chamCongRepository
                .findByNguoiDungIdAndNgayChamCongAndCongTrinhId(tho.getId(), request.getNgayChamCong(), congTrinh.getId())
                .orElse(ChamCong.builder()
                        .nguoiDung(tho)
                        .ngayChamCong(request.getNgayChamCong())
                        .build());

        chamCong.setCongTrinh(congTrinh);
        chamCong.setLoaiCong(request.getLoaiCong());
        boolean coTangCa = Boolean.TRUE.equals(request.getCoTangCa());
        chamCong.setCoTangCa(coTangCa);
        chamCong.setDonGiaApDung(tho.getDonGiaCong());
        chamCong.setPhanTramTangCaApDung(coTangCa ? caiDatService.layCaiDat().getPhanTramTangCa() : null);
        chamCong.setGhiChu(request.getGhiChu());
        chamCong.setTrangThaiXacThuc(TrangThaiXacThuc.CHU_THAU_TAO);
        if (chamCong.getGioCheckIn() == null) {
            chamCong.setGioCheckIn(LocalDateTime.now());
        }

        chamCongRepository.save(chamCong);
        return toResponse(chamCong);
    }

    public ChamCongResponse duyet(Long id, boolean dongY) {
        ChamCong chamCong = chamCongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi chấm công"));
        chamCong.setTrangThaiXacThuc(dongY ? TrangThaiXacThuc.TU_DONG_XAC_NHAN : TrangThaiXacThuc.TU_CHOI);
        chamCongRepository.save(chamCong);

        if (!dongY) {
            thongBaoService.tao(
                    chamCong.getNguoiDung(),
                    LoaiThongBao.CHAM_CONG_TU_CHOI,
                    "Chấm công bị từ chối",
                    "Chấm công ngày " + chamCong.getNgayChamCong() + " tại " + chamCong.getCongTrinh().getTenCongTrinh() + " đã bị từ chối"
            );
        }

        return toResponse(chamCong);
    }

    public ChamCongResponse capNhatLoaiCong(Long id, LoaiCong loaiCong) {
        ChamCong chamCong = chamCongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi chấm công"));
        chamCong.setLoaiCong(loaiCong);
        chamCongRepository.save(chamCong);
        return toResponse(chamCong);
    }

    public ChamCongResponse capNhatTangCa(Long id, boolean coTangCa) {
        ChamCong chamCong = chamCongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi chấm công"));
        chamCong.setCoTangCa(coTangCa);
        if (coTangCa && chamCong.getPhanTramTangCaApDung() == null) {
            chamCong.setPhanTramTangCaApDung(caiDatService.layCaiDat().getPhanTramTangCa());
        }
        chamCongRepository.save(chamCong);
        return toResponse(chamCong);
    }

    public void xoa(Long id) {
        if (!chamCongRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bản ghi chấm công");
        }
        chamCongRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ChamCongResponse> layDanhSach(Long congTrinhId, Long nguoiDungId, LocalDate ngay, TrangThaiXacThuc trangThai) {
        return chamCongRepository.findAllByOrderByNgayChamCongDesc().stream()
                .filter(c -> congTrinhId == null || c.getCongTrinh().getId().equals(congTrinhId))
                .filter(c -> nguoiDungId == null || c.getNguoiDung().getId().equals(nguoiDungId))
                .filter(c -> ngay == null || c.getNgayChamCong().equals(ngay))
                .filter(c -> trangThai == null || c.getTrangThaiXacThuc() == trangThai)
                .sorted(java.util.Comparator.comparing(ChamCong::getNgayChamCong).reversed())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void luuKhuonMat(NguoiDung nguoiGoi, Long nguoiDungIdChiDinh, List<Double> descriptor) {
        NguoiDung doiTuong = nguoiGoi;
        if (nguoiGoi.getVaiTro() == VaiTro.CHU_THAU && nguoiDungIdChiDinh != null) {
            doiTuong = nguoiDungRepository.findById(nguoiDungIdChiDinh)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        }
        doiTuong.setFaceDescriptor(ghiFaceDescriptor(descriptor));
        nguoiDungRepository.save(doiTuong);
    }

    @Transactional(readOnly = true)
    public boolean daThietLapKhuonMat(NguoiDung nguoiDung) {
        return nguoiDung.getFaceDescriptor() != null;
    }

    private double tinhKhoangCachKhuonMat(List<Double> a, List<Double> b) {
        int n = Math.min(a.size(), b.size());
        double tong = 0;
        for (int i = 0; i < n; i++) {
            double hieu = a.get(i) - b.get(i);
            tong += hieu * hieu;
        }
        return Math.sqrt(tong);
    }

    private String ghiFaceDescriptor(List<Double> descriptor) {
        try {
            return objectMapper.writeValueAsString(descriptor);
        } catch (Exception e) {
            throw new BadRequestException("Dữ liệu khuôn mặt không hợp lệ");
        }
    }

    private List<Double> docFaceDescriptor(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Double>>() {
            });
        } catch (Exception e) {
            throw new BadRequestException("Dữ liệu khuôn mặt đã lưu bị lỗi, hãy thiết lập lại");
        }
    }

    private ChamCongResponse toResponse(ChamCong c) {
        return ChamCongResponse.builder()
                .id(c.getId())
                .nguoiDungId(c.getNguoiDung().getId())
                .hoTenTho(c.getNguoiDung().getHoTen())
                .congTrinhId(c.getCongTrinh().getId())
                .tenCongTrinh(c.getCongTrinh().getTenCongTrinh())
                .ngayChamCong(c.getNgayChamCong())
                .loaiCong(c.getLoaiCong())
                .coTangCa(c.getCoTangCa())
                .donGiaApDung(c.getDonGiaApDung())
                .phanTramTangCaApDung(c.getPhanTramTangCaApDung())
                .gioCheckIn(c.getGioCheckIn())
                .anhChamCong(c.getAnhChamCong())
                .khoangCachGuong(c.getKhoangCachGuong())
                .trangThaiXacThuc(c.getTrangThaiXacThuc())
                .ghiChu(c.getGhiChu())
                .build();
    }
}
