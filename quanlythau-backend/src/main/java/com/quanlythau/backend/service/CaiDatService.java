package com.quanlythau.backend.service;

import com.quanlythau.backend.entity.CaiDatChuThau;
import com.quanlythau.backend.repository.CaiDatChuThauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaiDatService {

    private static final int PHAN_TRAM_TANG_CA_MAC_DINH = 30;
    private static final int NGUONG_CANH_BAO_CHI_PHI_MAC_DINH = 80;

    private final CaiDatChuThauRepository caiDatRepository;

    public CaiDatChuThau layCaiDat(Long chuThauId) {
        return caiDatRepository.findById(chuThauId)
                .orElseGet(() -> CaiDatChuThau.builder()
                        .chuThauId(chuThauId)
                        .phanTramTangCa(PHAN_TRAM_TANG_CA_MAC_DINH)
                        .nguongCanhBaoChiPhi(NGUONG_CANH_BAO_CHI_PHI_MAC_DINH)
                        .build());
    }

    public CaiDatChuThau capNhat(Long chuThauId, Integer phanTramTangCa, Integer nguongCanhBaoChiPhi) {
        CaiDatChuThau caiDat = layCaiDat(chuThauId);
        caiDat.setPhanTramTangCa(phanTramTangCa);
        caiDat.setNguongCanhBaoChiPhi(nguongCanhBaoChiPhi);
        return caiDatRepository.save(caiDat);
    }
}
