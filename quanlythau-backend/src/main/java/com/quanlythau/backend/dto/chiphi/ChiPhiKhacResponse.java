package com.quanlythau.backend.dto.chiphi;

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
public class ChiPhiKhacResponse {
    private Long id;
    private Long congTrinhId;
    private String loaiChiPhi;
    private BigDecimal soTien;
    private LocalDate ngay;
    private String ghiChu;
}
