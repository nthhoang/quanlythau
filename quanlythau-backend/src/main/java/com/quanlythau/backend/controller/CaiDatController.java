package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.caidat.CaiDatRequest;
import com.quanlythau.backend.dto.caidat.CaiDatResponse;
import com.quanlythau.backend.entity.CaiDatHeThong;
import com.quanlythau.backend.service.CaiDatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<CaiDatResponse> layCaiDat() {
        return ResponseEntity.ok(toResponse(caiDatService.layCaiDat()));
    }

    @PutMapping
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<CaiDatResponse> capNhat(@Valid @RequestBody CaiDatRequest request) {
        CaiDatHeThong c = caiDatService.capNhat(request.getPhanTramTangCa(), request.getNguongCanhBaoChiPhi());
        return ResponseEntity.ok(toResponse(c));
    }

    private CaiDatResponse toResponse(CaiDatHeThong c) {
        return CaiDatResponse.builder()
                .phanTramTangCa(c.getPhanTramTangCa())
                .nguongCanhBaoChiPhi(c.getNguongCanhBaoChiPhi())
                .build();
    }
}
