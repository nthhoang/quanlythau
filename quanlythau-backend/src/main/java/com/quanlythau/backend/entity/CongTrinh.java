package com.quanlythau.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import java.util.UUID;

@Entity
@Table(name = "cong_trinh")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongTrinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_cong_trinh", nullable = false, length = 200)
    private String tenCongTrinh;

    @Column(name = "dia_chi", nullable = false, length = 300)
    private String diaChi;

    @Column(name = "ten_chu_nha", length = 100)
    private String tenChuNha;

    @Column(name = "sdt_chu_nha", length = 15)
    private String sdtChuNha;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hinh", nullable = false, length = 20)
    private LoaiHinhCongTrinh loaiHinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiCongTrinh trangThai = TrangThaiCongTrinh.DANG_THI_CONG;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_du_kien_hoan_thanh")
    private LocalDate ngayDuKienHoanThanh;

    @Column(name = "gia_tri_hop_dong", precision = 15, scale = 0)
    private BigDecimal giaTriHopDong;

    @Builder.Default
    @Column(name = "tien_do_phan_tram")
    private Integer tienDoPhanTram = 0;

    @Column(name = "share_token", unique = true, length = 64)
    private String shareToken;

    @Column(name = "nguoi_tao_id")
    private Long nguoiTaoId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.shareToken == null) {
            this.shareToken = UUID.randomUUID().toString().replace("-", "");
        }
        if (this.trangThai == null) {
            this.trangThai = TrangThaiCongTrinh.DANG_THI_CONG;
        }
        if (this.tienDoPhanTram == null) {
            this.tienDoPhanTram = 0;
        }
    }
}
