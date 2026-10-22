package com.quanlythau.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cai_dat_chu_thau")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaiDatChuThau {

    @Id
    @Column(name = "chu_thau_id")
    private Long chuThauId;

    @Column(name = "phan_tram_tang_ca", nullable = false)
    private Integer phanTramTangCa;

    @Column(name = "nguong_canh_bao_chi_phi", nullable = false)
    private Integer nguongCanhBaoChiPhi;
}
