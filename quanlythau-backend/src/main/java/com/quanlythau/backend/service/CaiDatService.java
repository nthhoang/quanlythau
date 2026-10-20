package com.quanlythau.backend.service;

import com.quanlythau.backend.entity.CaiDatHeThong;
import com.quanlythau.backend.repository.CaiDatHeThongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaiDatService {

    private static final long ID_DUY_NHAT = 1L;
    private static final int PHAN_TRAM_TANG_CA_MAC_DINH = 30;
    private static final int NGUONG_CANH_BAO_CHI_PHI_MAC_DINH = 80;

    private final CaiDatHeThongRepository caiDatRepository;

    public CaiDatHeThong layCaiDat() {
        return caiDatRepository.findById(ID_DUY_NHAT)
                .orElseGet(() -> caiDatRepository.save(
                        CaiDatHeThong.builder()
                                .id(ID_DUY_NHAT)
                                .phanTramTangCa(PHAN_TRAM_TANG_CA_MAC_DINH)
                                .nguongCanhBaoChiPhi(NGUONG_CANH_BAO_CHI_PHI_MAC_DINH)
                                .build()
                ));
    }

    public CaiDatHeThong capNhat(Integer phanTramTangCa, Integer nguongCanhBaoChiPhi) {
        CaiDatHeThong caiDat = layCaiDat();
        caiDat.setPhanTramTangCa(phanTramTangCa);
        caiDat.setNguongCanhBaoChiPhi(nguongCanhBaoChiPhi);
        return caiDatRepository.save(caiDat);
    }
}
