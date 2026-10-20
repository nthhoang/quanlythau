package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.laillo.LaiLoResponse;
import com.quanlythau.backend.service.LaiLoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lai-lo")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHU_THAU')")
public class LaiLoController {

    private final LaiLoService laiLoService;

    @GetMapping("/cong-trinh/{congTrinhId}")
    public ResponseEntity<LaiLoResponse> tinhLaiLo(@PathVariable Long congTrinhId) {
        return ResponseEntity.ok(laiLoService.tinhLaiLo(congTrinhId));
    }

    @GetMapping("/tat-ca")
    public ResponseEntity<List<LaiLoResponse>> tinhLaiLoTatCa() {
        return ResponseEntity.ok(laiLoService.tinhLaiLoTatCa());
    }
}
