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
import java.time.LocalDateTime;

@Entity
@Table(name = "bang_luong")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BangLuong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "chu_thau_id")
    private Long chuThauId;

    @Column(name = "tu_ngay", nullable = false)
    private LocalDate tuNgay;

    @Column(name = "den_ngay", nullable = false)
    private LocalDate denNgay;

    @Column(name = "tong_cong", precision = 6, scale = 1)
    private BigDecimal tongCong;

    @Column(name = "tien_co_ban", precision = 15, scale = 0)
    private BigDecimal tienCoBan;

    @Column(name = "tien_tang_ca", precision = 15, scale = 0)
    private BigDecimal tienTangCa;

    @Column(name = "tong_tien_cong", precision = 15, scale = 0)
    private BigDecimal tongTienCong;

    @Column(name = "tong_tam_ung", precision = 15, scale = 0)
    private BigDecimal tongTamUng;

    @Column(name = "thuc_nhan", precision = 15, scale = 0)
    private BigDecimal thucNhan;

    @Builder.Default
    @Column(name = "trang_thai", length = 20)
    private String trangThai = "CHUA_THANH_TOAN";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
