package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.congtrinh.CongTrinhRequest;
import com.quanlythau.backend.dto.congtrinh.CongTrinhResponse;
import com.quanlythau.backend.dto.congtrinh.PhanCongRequest;
import com.quanlythau.backend.dto.congtrinh.PhanCongResponse;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.LoaiThongBao;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.PhanCongCongTrinh;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.CongTrinhRepository;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.PhanCongCongTrinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CongTrinhService {

    private final CongTrinhRepository congTrinhRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhanCongCongTrinhRepository phanCongRepository;
    private final ThongBaoService thongBaoService;

    public CongTrinhResponse create(CongTrinhRequest request, Long nguoiTaoId) {
        CongTrinh congTrinh = CongTrinh.builder()
                .tenCongTrinh(request.getTenCongTrinh())
                .diaChi(request.getDiaChi())
                .tenChuNha(request.getTenChuNha())
                .sdtChuNha(request.getSdtChuNha())
                .loaiHinh(request.getLoaiHinh())
                .ngayBatDau(request.getNgayBatDau())
                .ngayDuKienHoanThanh(request.getNgayDuKienHoanThanh())
                .giaTriHopDong(request.getGiaTriHopDong())
                .trangThai(TrangThaiCongTrinh.DANG_THI_CONG)
                .tienDoPhanTram(0)
                .nguoiTaoId(nguoiTaoId)
                .build();

        congTrinhRepository.save(congTrinh);
        return toResponse(congTrinh);
    }

    @Transactional(readOnly = true)
    public List<CongTrinhResponse> getAll() {
        return congTrinhRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CongTrinhResponse getById(Long id) {
        return toResponse(findCongTrinhOrThrow(id));
    }

    public CongTrinhResponse update(Long id, CongTrinhRequest request) {
        CongTrinh congTrinh = findCongTrinhOrThrow(id);
        congTrinh.setTenCongTrinh(request.getTenCongTrinh());
        congTrinh.setDiaChi(request.getDiaChi());
        congTrinh.setTenChuNha(request.getTenChuNha());
        congTrinh.setSdtChuNha(request.getSdtChuNha());
        congTrinh.setLoaiHinh(request.getLoaiHinh());
        congTrinh.setNgayBatDau(request.getNgayBatDau());
        congTrinh.setNgayDuKienHoanThanh(request.getNgayDuKienHoanThanh());
        congTrinh.setGiaTriHopDong(request.getGiaTriHopDong());
        congTrinhRepository.save(congTrinh);
        return toResponse(congTrinh);
    }

    public void updateTrangThai(Long id, TrangThaiCongTrinh trangThai) {
        CongTrinh congTrinh = findCongTrinhOrThrow(id);
        congTrinh.setTrangThai(trangThai);
        congTrinhRepository.save(congTrinh);
    }

    public void updateTienDo(Long id, Integer tienDoPhanTram) {
        if (tienDoPhanTram == null || tienDoPhanTram < 0 || tienDoPhanTram > 100) {
            throw new BadRequestException("Tien do phai trong khoang 0-100");
        }
        CongTrinh congTrinh = findCongTrinhOrThrow(id);
        congTrinh.setTienDoPhanTram(tienDoPhanTram);
        congTrinhRepository.save(congTrinh);
    }

    public void delete(Long id) {
        CongTrinh congTrinh = findCongTrinhOrThrow(id);
        congTrinhRepository.delete(congTrinh);
    }

    public PhanCongResponse ganTho(Long congTrinhId, PhanCongRequest request) {
        CongTrinh congTrinh = findCongTrinhOrThrow(congTrinhId);
        NguoiDung nguoiDung = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay tho voi id: " + request.getNguoiDungId()));

        if (phanCongRepository.existsByCongTrinhIdAndNguoiDungIdAndNgayKetThucIsNull(congTrinhId, nguoiDung.getId())) {
            throw new BadRequestException("Tho nay da duoc phan cong vao cong trinh nay roi");
        }

        PhanCongCongTrinh phanCong = PhanCongCongTrinh.builder()
                .congTrinh(congTrinh)
                .nguoiDung(nguoiDung)
                .ngayBatDau(request.getNgayBatDau() != null ? request.getNgayBatDau() : LocalDate.now())
                .build();

        phanCongRepository.save(phanCong);

        thongBaoService.tao(
                nguoiDung,
                LoaiThongBao.PHAN_CONG_MOI,
                "Được phân công công trình mới",
                "Bạn đã được phân công vào công trình \"" + congTrinh.getTenCongTrinh() + "\""
        );

        return toPhanCongResponse(phanCong);
    }

    @Transactional(readOnly = true)
    public List<PhanCongResponse> getDanhSachTho(Long congTrinhId) {
        findCongTrinhOrThrow(congTrinhId);
        return phanCongRepository.findByCongTrinhIdAndNgayKetThucIsNull(congTrinhId).stream()
                .map(this::toPhanCongResponse)
                .collect(Collectors.toList());
    }

    public void goPhanCong(Long congTrinhId, Long phanCongId) {
        PhanCongCongTrinh phanCong = phanCongRepository.findById(phanCongId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phan cong voi id: " + phanCongId));
        if (!phanCong.getCongTrinh().getId().equals(congTrinhId)) {
            throw new BadRequestException("Phan cong khong thuoc cong trinh nay");
        }
        phanCong.setNgayKetThuc(LocalDate.now());
        phanCongRepository.save(phanCong);
    }

    @Transactional(readOnly = true)
    public List<CongTrinhResponse> getCongTrinhCuaToi(Long nguoiDungId) {
        return phanCongRepository.findByNguoiDungIdAndNgayKetThucIsNull(nguoiDungId).stream()
                .map(pc -> toResponse(pc.getCongTrinh()))
                .collect(Collectors.toList());
    }

    private CongTrinh findCongTrinhOrThrow(Long id) {
        return congTrinhRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay cong trinh voi id: " + id));
    }

    private CongTrinhResponse toResponse(CongTrinh c) {
        return CongTrinhResponse.builder()
                .id(c.getId())
                .tenCongTrinh(c.getTenCongTrinh())
                .diaChi(c.getDiaChi())
                .tenChuNha(c.getTenChuNha())
                .sdtChuNha(c.getSdtChuNha())
                .loaiHinh(c.getLoaiHinh())
                .trangThai(c.getTrangThai())
                .ngayBatDau(c.getNgayBatDau())
                .ngayDuKienHoanThanh(c.getNgayDuKienHoanThanh())
                .giaTriHopDong(c.getGiaTriHopDong())
                .tienDoPhanTram(c.getTienDoPhanTram())
                .shareToken(c.getShareToken())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private PhanCongResponse toPhanCongResponse(PhanCongCongTrinh p) {
        return PhanCongResponse.builder()
                .id(p.getId())
                .congTrinhId(p.getCongTrinh().getId())
                .tenCongTrinh(p.getCongTrinh().getTenCongTrinh())
                .nguoiDungId(p.getNguoiDung().getId())
                .hoTenTho(p.getNguoiDung().getHoTen())
                .soDienThoaiTho(p.getNguoiDung().getSoDienThoai())
                .ngayBatDau(p.getNgayBatDau())
                .ngayKetThuc(p.getNgayKetThuc())
                .build();
    }
}
