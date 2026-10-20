package com.quanlythau.backend.dto.tamung;

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
public class TamUngResponse {
    private Long id;
    private Long nguoiDungId;
    private String hoTenTho;
    private BigDecimal soTien;
    private LocalDate ngayUng;
    private String ghiChu;
}
