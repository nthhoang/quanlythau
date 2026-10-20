package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.chamcong.ChamCongResponse;
import com.quanlythau.backend.dto.luong.BangLuongResponse;
import com.quanlythau.backend.dto.luong.LuongPreviewResponse;
import com.quanlythau.backend.dto.tamung.TamUngResponse;
import com.quanlythau.backend.entity.BangLuong;
import com.quanlythau.backend.entity.ChamCong;
import com.quanlythau.backend.entity.LoaiThongBao;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.TamUngLuong;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.BangLuongRepository;
import com.quanlythau.backend.repository.ChamCongRepository;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.TamUngLuongRepository;
import com.quanlythau.backend.util.TinhCongUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tinh luong theo ky (tu ngay - den ngay) cho 1 tho, dua tren du lieu ChamCong
 * da "chup lai" don gia + % tang ca tai dung thoi diem cham cong - nen doi
 * don gia hay % tang ca sau nay KHONG lam sai lech luong cua ky da qua.
 *
 * Chi tinh cac ban ghi cham cong o trang thai TU_DONG_XAC_NHAN hoac
 * CHU_THAU_TAO (da duoc xac nhan la co that). Ban ghi con CAN_DUYET hoac
 * da TU_CHOI KHONG duoc tinh vao luong - chu thau phai duyet truoc.
 */
@Service
@RequiredArgsConstructor
public class LuongService {

    private final ChamCongRepository chamCongRepository;
    private final TamUngLuongRepository tamUngRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BangLuongRepository bangLuongRepository;
    private final ThongBaoService thongBaoService;

    @Transactional(readOnly = true)
    public LuongPreviewResponse tinhLuong(Long nguoiDungId, LocalDate tuNgay, LocalDate denNgay) {
        if (denNgay.isBefore(tuNgay)) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        NguoiDung tho = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));

        List<ChamCong> tatCa = chamCongRepository.findByNguoiDungIdAndNgayChamCongBetween(nguoiDungId, tuNgay, denNgay);

        List<ChamCong> hopLe = tatCa.stream()
                .filter(c -> c.getTrangThaiXacThuc() == TrangThaiXacThuc.TU_DONG_XAC_NHAN
                        || c.getTrangThaiXacThuc() == TrangThaiXacThuc.CHU_THAU_TAO)
                .collect(Collectors.toList());

        int soBanGhiChuaXacNhan = tatCa.size() - hopLe.size();

        BigDecimal tongCong = BigDecimal.ZERO;
        BigDecimal tienCoBan = BigDecimal.ZERO;
        BigDecimal tienTangCa = BigDecimal.ZERO;

        for (ChamCong c : hopLe) {
            tongCong = tongCong.add(TinhCongUtil.heSoCong(c));
            tienCoBan = tienCoBan.add(TinhCongUtil.tienCoBan(c));
            tienTangCa = tienTangCa.add(TinhCongUtil.tienTangCa(c));
        }

        BigDecimal tongTienCong = tienCoBan.add(tienTangCa);

        List<TamUngLuong> tamUngs = tamUngRepository.findByNguoiDungIdAndNgayUngBetween(nguoiDungId, tuNgay, denNgay);
        BigDecimal tongTamUng = tamUngs.stream()
                .map(TamUngLuong::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal thucNhan = tongTienCong.subtract(tongTamUng);

        return LuongPreviewResponse.builder()
                .nguoiDungId(tho.getId())
                .hoTenTho(tho.getHoTen())
                .tuNgay(tuNgay)
                .denNgay(denNgay)
                .tongCong(tongCong)
                .tienCoBan(tienCoBan)
                .tienTangCa(tienTangCa)
                .tongTienCong(tongTienCong)
                .tongTamUng(tongTamUng)
                .thucNhan(thucNhan)
                .chiTietCham(hopLe.stream().map(this::toChamCongResponse).collect(Collectors.toList()))
                .chiTietTamUng(tamUngs.stream().map(this::toTamUngResponse).collect(Collectors.toList()))
                .soBanGhiChuaXacNhan(soBanGhiChuaXacNhan)
                .build();
    }

    @Transactional(readOnly = true)
    public List<LuongPreviewResponse> tinhLuongTatCa(LocalDate tuNgay, LocalDate denNgay) {
        return nguoiDungRepository.findByVaiTro(VaiTro.THO).stream()
                .map(tho -> tinhLuong(tho.getId(), tuNgay, denNgay))
                .collect(Collectors.toList());
    }

    /** Chot luong: tinh lai roi luu thanh 1 BangLuong co dinh. Chan neu ky nay chong lan voi ky da chot truoc do. */
    public BangLuongResponse chotLuong(Long nguoiDungId, LocalDate tuNgay, LocalDate denNgay) {
        boolean chongLan = bangLuongRepository.findByNguoiDungId(nguoiDungId).stream()
                .anyMatch(b -> !tuNgay.isAfter(b.getDenNgay()) && !b.getTuNgay().isAfter(denNgay));
        if (chongLan) {
            throw new BadRequestException("Khoảng thời gian này trùng với một kỳ lương đã chốt trước đó");
        }

        LuongPreviewResponse xemTruoc = tinhLuong(nguoiDungId, tuNgay, denNgay);
        NguoiDung tho = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));

        BangLuong bangLuong = BangLuong.builder()
                .nguoiDung(tho)
                .tuNgay(tuNgay)
                .denNgay(denNgay)
                .tongCong(xemTruoc.getTongCong())
                .tienCoBan(xemTruoc.getTienCoBan())
                .tienTangCa(xemTruoc.getTienTangCa())
                .tongTienCong(xemTruoc.getTongTienCong())
                .tongTamUng(xemTruoc.getTongTamUng())
                .thucNhan(xemTruoc.getThucNhan())
                .trangThai("CHUA_THANH_TOAN")
                .build();

        bangLuongRepository.save(bangLuong);

        thongBaoService.tao(
                tho,
                LoaiThongBao.LUONG_DA_CHOT,
                "Lương đã được chốt",
                "Kỳ " + tuNgay + " → " + denNgay + ": thực nhận " + xemTruoc.getThucNhan() + " VNĐ"
        );

        return toBangLuongResponse(bangLuong);
    }

    @Transactional(readOnly = true)
    public List<BangLuongResponse> layLichSu(Long nguoiDungId) {
        return bangLuongRepository.findByNguoiDungIdOrderByTuNgayDesc(nguoiDungId).stream()
                .map(this::toBangLuongResponse)
                .collect(Collectors.toList());
    }

    public BangLuongResponse danhDauDaThanhToan(Long id) {
        BangLuong bangLuong = bangLuongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bảng lương"));
        bangLuong.setTrangThai("DA_THANH_TOAN");
        bangLuongRepository.save(bangLuong);

        thongBaoService.tao(
                bangLuong.getNguoiDung(),
                LoaiThongBao.LUONG_DA_THANH_TOAN,
                "Đã nhận lương",
                "Kỳ " + bangLuong.getTuNgay() + " → " + bangLuong.getDenNgay() + " đã được đánh dấu thanh toán"
        );

        return toBangLuongResponse(bangLuong);
    }

    private ChamCongResponse toChamCongResponse(ChamCong c) {
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
                .trangThaiXacThuc(c.getTrangThaiXacThuc())
                .build();
    }

    private TamUngResponse toTamUngResponse(TamUngLuong t) {
        return TamUngResponse.builder()
                .id(t.getId())
                .nguoiDungId(t.getNguoiDung().getId())
                .hoTenTho(t.getNguoiDung().getHoTen())
                .soTien(t.getSoTien())
                .ngayUng(t.getNgayUng())
                .ghiChu(t.getGhiChu())
                .build();
    }

    private BangLuongResponse toBangLuongResponse(BangLuong b) {
        return BangLuongResponse.builder()
                .id(b.getId())
                .nguoiDungId(b.getNguoiDung().getId())
                .hoTenTho(b.getNguoiDung().getHoTen())
                .tuNgay(b.getTuNgay())
                .denNgay(b.getDenNgay())
                .tongCong(b.getTongCong())
                .tienCoBan(b.getTienCoBan())
                .tienTangCa(b.getTienTangCa())
                .tongTienCong(b.getTongTienCong())
                .tongTamUng(b.getTongTamUng())
                .thucNhan(b.getThucNhan())
                .trangThai(b.getTrangThai())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
