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
@Table(name = "cai_dat_he_thong")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaiDatHeThong {

    @Id
    private Long id;

    @Column(name = "phan_tram_tang_ca", nullable = false)
    private Integer phanTramTangCa;

    @Column(name = "nguong_canh_bao_chi_phi", nullable = false)
    private Integer nguongCanhBaoChiPhi;
}
