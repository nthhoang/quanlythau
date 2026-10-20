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
@Table(name = "vat_tu")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VatTu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_trinh_id", nullable = false)
    private CongTrinh congTrinh;

    @Column(name = "ten_vat_tu", nullable = false, length = 200)
    private String tenVatTu;

    @Column(name = "so_luong", nullable = false, precision = 12, scale = 2)
    private BigDecimal soLuong;

    @Column(name = "don_vi", length = 20)
    private String donVi;

    @Column(name = "don_gia", precision = 15, scale = 0)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", precision = 15, scale = 0)
    private BigDecimal thanhTien;

    @Column(name = "nha_cung_cap", length = 150)
    private String nhaCungCap;

    @Column(name = "ngay_nhap")
    private LocalDate ngayNhap;

    @Column(name = "anh_hoa_don", length = 500)
    private String anhHoaDon;

    @Column(length = 300)
    private String ghiChu;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }
}
