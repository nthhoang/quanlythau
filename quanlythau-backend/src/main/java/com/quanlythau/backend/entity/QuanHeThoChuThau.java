package com.quanlythau.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quan_he_tho_chu_thau")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuanHeThoChuThau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chu_thau_id", nullable = false)
    private Long chuThauId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tho_id", nullable = false)
    private NguoiDung tho;

    @Column(name = "don_gia_cong", precision = 15, scale = 0)
    private BigDecimal donGiaCong;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @PrePersist
    protected void onCreate() {
        if (ngayBatDau == null) {
            ngayBatDau = LocalDate.now();
        }
    }
}
