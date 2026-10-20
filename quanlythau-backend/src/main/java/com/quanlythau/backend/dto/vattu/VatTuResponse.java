package com.quanlythau.backend.dto.vattu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VatTuResponse {
    private Long id;
    private Long congTrinhId;
    private String tenCongTrinh;
    private String tenVatTu;
    private BigDecimal soLuong;
    private String donVi;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String nhaCungCap;
    private LocalDate ngayNhap;
    private String anhHoaDon;
    private String ghiChu;
}
