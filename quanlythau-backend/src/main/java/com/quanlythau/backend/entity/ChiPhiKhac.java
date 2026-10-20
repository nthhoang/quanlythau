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
@Table(name = "chi_phi_khac")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiPhiKhac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_trinh_id", nullable = false)
    private CongTrinh congTrinh;

    @Column(name = "loai_chi_phi", length = 100)
    private String loaiChiPhi;

    @Column(name = "so_tien", nullable = false, precision = 15, scale = 0)
    private BigDecimal soTien;

    private LocalDate ngay;

    @Column(length = 300)
    private String ghiChu;
}
