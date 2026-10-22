package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.caidat.CaiDatRequest;
import com.quanlythau.backend.dto.caidat.CaiDatResponse;
import com.quanlythau.backend.entity.CaiDatChuThau;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.CaiDatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cai-dat")
@RequiredArgsConstructor
public class CaiDatController {

    private final CaiDatService caiDatService;

    @GetMapping
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<CaiDatResponse> layCaiDat(@AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(toResponse(caiDatService.layCaiDat(chuThau.getId())));
    }

    @PutMapping
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<CaiDatResponse> capNhat(
            @Valid @RequestBody CaiDatRequest request,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        CaiDatChuThau c = caiDatService.capNhat(
                chuThau.getId(), request.getPhanTramTangCa(), request.getNguongCanhBaoChiPhi()
        );
        return ResponseEntity.ok(toResponse(c));
    }

    private CaiDatResponse toResponse(CaiDatChuThau c) {
        return CaiDatResponse.builder()
                .phanTramTangCa(c.getPhanTramTangCa())
                .nguongCanhBaoChiPhi(c.getNguongCanhBaoChiPhi())
                .build();
    }
}
