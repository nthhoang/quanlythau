package com.quanlythau.backend.dto.chiphi;

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
public class ChiPhiKhacRequest {

    @NotBlank(message = "Phải nhập loại chi phí")
    private String loaiChiPhi;

    @NotNull(message = "Phải nhập số tiền")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal soTien;

    private LocalDate ngay;

    private String ghiChu;
}
