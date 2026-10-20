package com.quanlythau.backend.dto.laillo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaiLoResponse {
    private Long congTrinhId;
    private String tenCongTrinh;
    private String trangThaiCongTrinh;
    private Integer tienDoPhanTram;
    private BigDecimal giaTriHopDong;
    private BigDecimal chiPhiNhanCong;
    private BigDecimal chiPhiVatTu;
    private BigDecimal chiPhiKhac;
    private BigDecimal tongChiPhi;
    private BigDecimal laiLoTamTinh;
    private BigDecimal phanTramDaChi;
    private boolean vuotNguongCanhBao;
    private Integer nguongCanhBaoApDung;
}
