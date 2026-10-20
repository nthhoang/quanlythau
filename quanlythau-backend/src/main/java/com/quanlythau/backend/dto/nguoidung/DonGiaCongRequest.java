package com.quanlythau.backend.dto.nguoidung;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonGiaCongRequest {

    @NotNull(message = "Phải nhập đơn giá công")
    @PositiveOrZero(message = "Đơn giá công không được âm")
    private BigDecimal donGiaCong;
}
