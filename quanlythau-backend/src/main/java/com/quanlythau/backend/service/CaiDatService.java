package com.quanlythau.backend.service;

import com.quanlythau.backend.entity.CaiDatChuThau;
import com.quanlythau.backend.entity.CaiDatHeThong;
import com.quanlythau.backend.repository.CaiDatChuThauRepository;
import com.quanlythau.backend.repository.CaiDatHeThongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaiDatService {

    private static final int PHAN_TRAM_TANG_CA_MAC_DINH = 30;
    private static final int NGUONG_CANH_BAO_CHI_PHI_MAC_DINH = 80;

    private final CaiDatChuThauRepository caiDatRepository;
    private final CaiDatHeThongRepository caiDatHeThongRepository;

    public CaiDatChuThau layCaiDat(Long chuThauId) {
        return caiDatRepository.findById(chuThauId)
                .orElseGet(() -> {
                    CaiDatHeThong legacy = caiDatHeThongRepository.findById(1L).orElse(null);
                    return CaiDatChuThau.builder()
                            .chuThauId(chuThauId)
                            .phanTramTangCa(legacy != null
                                    ? legacy.getPhanTramTangCa()
                                    : PHAN_TRAM_TANG_CA_MAC_DINH)
                            .nguongCanhBaoChiPhi(legacy != null
                                    ? legacy.getNguongCanhBaoChiPhi()
                                    : NGUONG_CANH_BAO_CHI_PHI_MAC_DINH)
                            .build();
                });
    }

    public CaiDatChuThau capNhat(Long chuThauId, Integer phanTramTangCa, Integer nguongCanhBaoChiPhi) {
        CaiDatChuThau caiDat = layCaiDat(chuThauId);
        caiDat.setPhanTramTangCa(phanTramTangCa);
        caiDat.setNguongCanhBaoChiPhi(nguongCanhBaoChiPhi);
        return caiDatRepository.save(caiDat);
    }
}
