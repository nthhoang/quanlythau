package com.quanlythau.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cham_cong")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cong_trinh_id", nullable = false)
    private CongTrinh congTrinh;

    @Column(name = "ngay_cham_cong", nullable = false)
    private LocalDate ngayChamCong;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_cong", nullable = false, length = 20)
    private LoaiCong loaiCong;

    @Builder.Default
    @Column(name = "co_tang_ca", nullable = false)
    private Boolean coTangCa = false;

    @Column(name = "don_gia_ap_dung", precision = 15, scale = 0)
    private java.math.BigDecimal donGiaApDung;

    @Column(name = "phan_tram_tang_ca_ap_dung")
    private Integer phanTramTangCaApDung;

    @Column(name = "gio_check_in")
    private LocalDateTime gioCheckIn;

    @Column(name = "anh_cham_cong", length = 500)
    private String anhChamCong;

    @Column(name = "khoang_cach_guong")
    private Double khoangCachGuong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_xac_thuc", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiXacThuc trangThaiXacThuc = TrangThaiXacThuc.CAN_DUYET;

    @Column(length = 300)
    private String ghiChu;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
