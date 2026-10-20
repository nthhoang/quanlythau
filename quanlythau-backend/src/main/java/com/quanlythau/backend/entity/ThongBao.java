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

import java.time.LocalDateTime;

/**
 * Thong bao trong app, sinh tu dong khi co su kien lien quan (phan cong moi,
 * cham cong bi tu choi, luong da chot/da thanh toan). Frontend poll dinh ky
 * de lay danh sach + so luong chua doc (xem README ve ly do dung polling
 * thay vi WebSocket).
 */
@Entity
@Table(name = "thong_bao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_nhan_id", nullable = false)
    private NguoiDung nguoiNhan;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung", length = 500)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoaiThongBao loai;

    @Builder.Default
    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
