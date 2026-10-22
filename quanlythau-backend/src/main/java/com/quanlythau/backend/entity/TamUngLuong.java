package com.quanlythau.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tam_ung_luong")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TamUngLuong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_trinh_id")
    private CongTrinh congTrinh;

    @Column(name = "chu_thau_id")
    private Long chuThauId;

    @Column(name = "so_tien", nullable = false, precision = 15, scale = 0)
    private BigDecimal soTien;

    @Column(name = "ngay_ung", nullable = false)
    private LocalDate ngayUng;

    @Column(length = 300)
    private String ghiChu;
}
