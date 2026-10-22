package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.tamung.TamUngRequest;
import com.quanlythau.backend.dto.tamung.TamUngResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.TamUngLuongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tam-ung")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHU_THAU')")
public class TamUngLuongController {

    private final TamUngLuongService tamUngLuongService;

    @PostMapping
    public ResponseEntity<TamUngResponse> taoTamUng(@Valid @RequestBody TamUngRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(tamUngLuongService.taoTamUng(request, chuThau.getId()));
    }

    @GetMapping("/tho/{nguoiDungId}")
    public ResponseEntity<List<TamUngResponse>> layDanhSachTheoTho(@PathVariable Long nguoiDungId, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(tamUngLuongService.layDanhSachTheoTho(nguoiDungId, chuThau.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Long id, @AuthenticationPrincipal NguoiDung chuThau) {
        tamUngLuongService.xoa(id, chuThau.getId());
        return ResponseEntity.noContent().build();
    }
}
