package com.quanlythau.backend.dto.vattu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VatTuRequest {

    @NotBlank(message = "Phải nhập tên vật tư")
    private String tenVatTu;

    @NotNull(message = "Phải nhập số lượng")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private BigDecimal soLuong;

    private String donVi;
    private BigDecimal donGia;
    private String nhaCungCap;
    private LocalDate ngayNhap;
    private String anhHoaDon;
    private String ghiChu;
}
