package com.quanlythau.backend.dto.vattu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DongVatTuGoiYResponse {
    private String tenVatTu;
    private BigDecimal soLuong;
    private String donVi;
    private BigDecimal donGia;
}
