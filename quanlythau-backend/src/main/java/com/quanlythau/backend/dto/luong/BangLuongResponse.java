package com.quanlythau.backend.dto.luong;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BangLuongResponse {
    private Long id;
    private Long nguoiDungId;
    private String hoTenTho;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private BigDecimal tongCong;
    private BigDecimal tienCoBan;
    private BigDecimal tienTangCa;
    private BigDecimal tongTienCong;
    private BigDecimal tongTamUng;
    private BigDecimal thucNhan;
    private String trangThai;
    private LocalDateTime createdAt;
}
