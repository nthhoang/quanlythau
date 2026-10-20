package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.congkhai.CongTrinhCongKhaiResponse;
import com.quanlythau.backend.service.CongKhaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint CONG KHAI, khong yeu cau dang nhap (da permitAll trong SecurityConfig tu dau du an). */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final CongKhaiService congKhaiService;

    @GetMapping("/cong-trinh/{shareToken}")
    public ResponseEntity<CongTrinhCongKhaiResponse> xemCongTrinh(@PathVariable String shareToken) {
        return ResponseEntity.ok(congKhaiService.xemTheoShareToken(shareToken));
    }
}
