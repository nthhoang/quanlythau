package com.quanlythau.backend.controller;

import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.TroLyAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Tro ly AI hoi dap nhanh - CHU_THAU-only, giong cac module tai chinh khac (du lieu ngu canh la du lieu quan ly). */
@RestController
@RequestMapping("/api/tro-ly-ai")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHU_THAU')")
public class TroLyAiController {

    private final TroLyAiService troLyAiService;

    @PostMapping("/hoi-dap")
    public ResponseEntity<Map<String, String>> hoiDap(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        String cauHoi = body.get("cauHoi");
        if (cauHoi == null || cauHoi.isBlank()) {
            throw new BadRequestException("Câu hỏi không được để trống");
        }
        return ResponseEntity.ok(Map.of("traLoi", troLyAiService.hoiDap(cauHoi, chuThau.getId())));
    }
}
