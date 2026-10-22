package com.quanlythau.backend.service;

import com.quanlythau.backend.entity.CaiDatChuThau;
import com.quanlythau.backend.repository.CaiDatChuThauRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaiDatServiceTest {

    @Mock
    private CaiDatChuThauRepository caiDatRepository;

    @InjectMocks
    private CaiDatService caiDatService;

    @Test
    void returnsDefaultsWhenContractorHasNoSavedSettings() {
        when(caiDatRepository.findById(7L)).thenReturn(Optional.empty());

        CaiDatChuThau settings = caiDatService.layCaiDat(7L);

        assertEquals(7L, settings.getChuThauId());
        assertEquals(30, settings.getPhanTramTangCa());
        assertEquals(80, settings.getNguongCanhBaoChiPhi());
    }

    @Test
    void returnsContractorSpecificSavedSettings() {
        CaiDatChuThau settings = CaiDatChuThau.builder()
                .chuThauId(7L)
                .phanTramTangCa(45)
                .nguongCanhBaoChiPhi(90)
                .build();
        when(caiDatRepository.findById(7L)).thenReturn(Optional.of(settings));

        CaiDatChuThau actual = caiDatService.layCaiDat(7L);

        assertEquals(45, actual.getPhanTramTangCa());
        assertEquals(90, actual.getNguongCanhBaoChiPhi());
    }
}
