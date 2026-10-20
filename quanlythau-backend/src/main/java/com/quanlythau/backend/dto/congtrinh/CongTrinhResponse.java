package com.quanlythau.backend.dto.congtrinh;

import com.quanlythau.backend.entity.LoaiHinhCongTrinh;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
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
public class CongTrinhResponse {
    private Long id;
    private String tenCongTrinh;
    private String diaChi;
    private String tenChuNha;
    private String sdtChuNha;
    private LoaiHinhCongTrinh loaiHinh;
    private TrangThaiCongTrinh trangThai;
    private LocalDate ngayBatDau;
    private LocalDate ngayDuKienHoanThanh;
    private BigDecimal giaTriHopDong;
    private Integer tienDoPhanTram;
    private String shareToken;
    private LocalDateTime createdAt;
}
