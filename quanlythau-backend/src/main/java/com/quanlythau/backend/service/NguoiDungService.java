package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.nguoidung.DonGiaCongRequest;
import com.quanlythau.backend.dto.nguoidung.TaoThoRequest;
import com.quanlythau.backend.dto.nguoidung.ThoResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.PhanCongCongTrinh;
import com.quanlythau.backend.entity.QuanHeThoChuThau;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.PhanCongCongTrinhRepository;
import com.quanlythau.backend.repository.QuanHeThoChuThauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PhanCongCongTrinhRepository phanCongRepository;
    private final QuanHeThoChuThauRepository quanHeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<ThoResponse> getDanhSachThoCoThePhanCong(Long chuThauId) {
        return nguoiDungRepository.findByChuThauIdAndVaiTroAndActiveTrueOrderByHoTen(chuThauId, VaiTro.THO).stream()
                .filter(tho -> !phanCongRepository.existsByNguoiDungIdAndNgayKetThucIsNull(tho.getId()))
                .map(this::toThoResponse)
                .toList();
    }

    @Transactional
    public ThoResponse taoTho(Long chuThauId, TaoThoRequest request) {
        if (nguoiDungRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new BadRequestException("Tên đăng nhập đã được sử dụng");
        }
        NguoiDung tho = nguoiDungRepository.save(NguoiDung.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .soDienThoai(request.getSoDienThoai())
                .vaiTro(VaiTro.THO)
                .chuyenMon(request.getChuyenMon())
                .donGiaCong(request.getDonGiaCong())
                .chuThauId(chuThauId)
                .active(true)
                .build());
        quanHeRepository.save(QuanHeThoChuThau.builder()
                .chuThauId(chuThauId)
                .tho(tho)
                .donGiaCong(request.getDonGiaCong())
                .ngayBatDau(LocalDate.now())
                .build());
        return toThoResponse(tho, List.of(), request.getDonGiaCong(), true);
    }

    @Transactional(readOnly = true)
    public List<ThoResponse> getDoiNgu(Long chuThauId) {
        List<QuanHeThoChuThau> quanHe = quanHeRepository.findByChuThauId(chuThauId);
        Map<Long, List<String>> congTrinhDangLamTheoTho = phanCongRepository
                .findActiveAssignmentsByContractorId(chuThauId)
                .stream()
                .collect(Collectors.groupingBy(
                        p -> p.getNguoiDung().getId(),
                        Collectors.mapping(p -> p.getCongTrinh().getTenCongTrinh(), Collectors.toList())
                ));

        List<NguoiDung> workers = nguoiDungRepository.findByChuThauIdAndVaiTroOrderByHoTen(chuThauId, VaiTro.THO);
        Map<Long, QuanHeThoChuThau> quanHeMoiNhat = quanHe.stream()
                .collect(Collectors.toMap(
                        q -> q.getTho().getId(),
                        q -> q,
                        (first, second) -> {
                            int ngayBatDauCompare = first.getNgayBatDau().compareTo(second.getNgayBatDau());
                            return ngayBatDauCompare > 0
                                    || ngayBatDauCompare == 0 && first.getId() > second.getId()
                                    ? first
                                    : second;
                        }
                ));

        return workers.stream()
                .map(tho -> {
                    QuanHeThoChuThau q = quanHeMoiNhat.get(tho.getId());
                    List<String> projects = congTrinhDangLamTheoTho.getOrDefault(tho.getId(), List.of());
                    boolean dangLamViec = Boolean.TRUE.equals(tho.getActive()) && q != null && q.getNgayKetThuc() == null;
                    return toThoResponse(tho, projects, q != null && q.getDonGiaCong() != null
                            ? q.getDonGiaCong()
                            : tho.getDonGiaCong(), dangLamViec);
                })
                .toList();
    }

    @Transactional
    public ThoResponse suaDonGiaCong(Long chuThauId, Long thoId, DonGiaCongRequest request) {
        layThoTrongDoiNgu(chuThauId, thoId);
        NguoiDung tho = nguoiDungRepository.findByIdForUpdate(thoId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        if (!Boolean.TRUE.equals(tho.getActive())) {
            throw new BadRequestException("Không thể thay đổi tài khoản thợ đã ngừng hoạt động");
        }
        kiemTraDangLamViec(chuThauId, thoId);
        QuanHeThoChuThau quanHe = layHoacTaoQuanHe(chuThauId, tho, false);
        quanHe.setDonGiaCong(request.getDonGiaCong());
        tho.setDonGiaCong(request.getDonGiaCong());
        quanHeRepository.save(quanHe);
        List<String> congTrinhDangLamViec = phanCongRepository
                .findActiveAssignmentsByContractorAndWorker(chuThauId, thoId)
                .stream()
                .map(phanCong -> phanCong.getCongTrinh().getTenCongTrinh())
                .toList();
        return toThoResponse(tho, congTrinhDangLamViec, quanHe.getDonGiaCong(), true);
    }

    @Transactional
    public void choThoNghiViec(Long chuThauId, Long thoId) {
        NguoiDung tho = nguoiDungRepository.findByIdForUpdate(thoId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        layThoTrongDoiNgu(chuThauId, thoId);
        if (!Boolean.TRUE.equals(tho.getActive())) {
            throw new BadRequestException("Tài khoản thợ đã được ngừng hoạt động");
        }
        LocalDate homNay = LocalDate.now();
        quanHeRepository.findFirstByChuThauIdAndThoIdAndNgayKetThucIsNull(chuThauId, thoId)
                .ifPresent(quanHe -> quanHe.setNgayKetThuc(homNay));
        phanCongRepository.findActiveAssignmentsByContractorAndWorker(chuThauId, thoId)
                .forEach(phanCong -> {
                    LocalDate ngayKetThuc = homNay.isBefore(phanCong.getNgayBatDau())
                            ? phanCong.getNgayBatDau()
                            : homNay;
                    phanCong.setNgayKetThuc(ngayKetThuc);
                });
        tho.setActive(false);
    }

    private NguoiDung layThoTrongDoiNgu(Long chuThauId, Long thoId) {
        if (!nguoiDungRepository.existsByChuThauIdAndIdAndVaiTro(chuThauId, thoId, VaiTro.THO)) {
            throw new ResourceNotFoundException("Không tìm thấy thợ trong đội của bạn");
        }
        return nguoiDungRepository.findById(thoId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
    }

    private ThoResponse toThoResponse(NguoiDung nguoiDung) {
        return toThoResponse(nguoiDung, List.of());
    }

    private ThoResponse toThoResponse(NguoiDung nguoiDung, List<String> congTrinhDangLamViec) {
        return toThoResponse(nguoiDung, congTrinhDangLamViec, nguoiDung.getDonGiaCong(), !congTrinhDangLamViec.isEmpty());
    }

    private ThoResponse toThoResponse(
            NguoiDung nguoiDung,
            List<String> congTrinhDangLamViec,
            java.math.BigDecimal donGiaCong,
            boolean dangLamViec
    ) {
        return ThoResponse.builder()
                .id(nguoiDung.getId())
                .tenDangNhap(nguoiDung.getTenDangNhap())
                .hoTen(nguoiDung.getHoTen())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .chuyenMon(nguoiDung.getChuyenMon())
                .donGiaCong(donGiaCong)
                .dangLamViec(dangLamViec)
                .active(nguoiDung.getActive())
                .congTrinhDangLamViec(congTrinhDangLamViec)
                .build();
    }

    @Transactional
    public QuanHeThoChuThau layHoacTaoQuanHe(Long chuThauId, NguoiDung tho, boolean batBuocMoi) {
        NguoiDung thoDaKhoa = nguoiDungRepository.findByIdForUpdate(tho.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        if (thoDaKhoa.getVaiTro() != VaiTro.THO || !Boolean.TRUE.equals(thoDaKhoa.getActive())) {
            throw new BadRequestException("Tài khoản được chọn không phải thợ đang hoạt động");
        }
        if (!chuThauId.equals(thoDaKhoa.getChuThauId())) {
            throw new BadRequestException("Thợ này không thuộc đội của bạn");
        }
        var quanHeDangHoatDong = quanHeRepository.findActiveByWorkerIdForUpdate(thoDaKhoa.getId());
        if (quanHeDangHoatDong.isPresent()) {
            QuanHeThoChuThau active = quanHeDangHoatDong.get();
            return active;
        }
        var latest = quanHeRepository.findFirstByChuThauIdAndThoIdOrderByNgayBatDauDescIdDesc(chuThauId, thoDaKhoa.getId());
        if (latest.isPresent() && !batBuocMoi) {
            throw new BadRequestException(latest.get().getNgayKetThuc() == null
                    ? "Không thể điều chỉnh đơn giá khi quan hệ làm việc chưa được khóa đúng trạng thái"
                    : "Không thể điều chỉnh đơn giá của thợ đã kết thúc làm việc");
        }
        java.math.BigDecimal donGiaBanDau = latest
                .map(QuanHeThoChuThau::getDonGiaCong)
                .orElse(thoDaKhoa.getDonGiaCong());
        return quanHeRepository.save(QuanHeThoChuThau.builder()
                .chuThauId(chuThauId)
                .tho(thoDaKhoa)
                .donGiaCong(donGiaBanDau)
                .ngayBatDau(LocalDate.now())
                .build());
    }

    @Transactional(readOnly = true)
    public java.math.BigDecimal layDonGiaCong(Long chuThauId, Long thoId) {
        return quanHeRepository.findFirstByChuThauIdAndThoIdAndNgayKetThucIsNull(chuThauId, thoId)
                .map(QuanHeThoChuThau::getDonGiaCong)
                .orElseGet(() -> quanHeRepository.findFirstByChuThauIdAndThoIdOrderByNgayBatDauDescIdDesc(chuThauId, thoId)
                        .map(QuanHeThoChuThau::getDonGiaCong)
                        .orElse(null));
    }

    @Transactional(readOnly = true)
    public void kiemTraThuocDoiNgu(Long chuThauId, Long thoId) {
        layThoTrongDoiNgu(chuThauId, thoId);
    }

    @Transactional(readOnly = true)
    public void kiemTraDangLamViec(Long chuThauId, Long thoId) {
        boolean coQuanHeHienTai = quanHeRepository
                .findFirstByChuThauIdAndThoIdAndNgayKetThucIsNull(chuThauId, thoId).isPresent();
        boolean coPhanCongHienTai = phanCongRepository
                .findActiveAssignmentsByContractorAndWorker(chuThauId, thoId).size() > 0;
        if (!coQuanHeHienTai && !coPhanCongHienTai) {
            throw new ResourceNotFoundException("Thợ không còn làm việc trong đội của bạn");
        }
    }
}
