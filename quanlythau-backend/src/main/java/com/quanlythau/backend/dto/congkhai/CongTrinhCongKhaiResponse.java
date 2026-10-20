package com.quanlythau.backend.dto.congkhai;

import com.quanlythau.backend.dto.nhatky.NhatKyThiCongResponse;
import com.quanlythau.backend.entity.LoaiHinhCongTrinh;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Ban rut gon cua cong trinh de hien thi CONG KHAI cho chu nha xem (khong can
 * dang nhap, qua shareToken) - KHONG gom gia tri hop dong hay bat ky thong
 * tin tai chinh noi bo nao.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongTrinhCongKhaiResponse {
    private String tenCongTrinh;
    private String diaChi;
    private LoaiHinhCongTrinh loaiHinh;
    private TrangThaiCongTrinh trangThai;
    private LocalDate ngayBatDau;
    private LocalDate ngayDuKienHoanThanh;
    private Integer tienDoPhanTram;
    private List<NhatKyThiCongResponse> nhatKyThiCong;
}
