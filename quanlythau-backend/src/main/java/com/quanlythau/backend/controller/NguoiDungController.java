package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.nguoidung.DonGiaCongRequest;
import com.quanlythau.backend.dto.nguoidung.TaoThoRequest;
import com.quanlythau.backend.dto.nguoidung.ThoResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    @GetMapping("/tho")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<List<ThoResponse>> getDanhSachTho(@AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(nguoiDungService.getDanhSachThoCoThePhanCong(chuThau.getId()));
    }

    @PostMapping("/tho")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ThoResponse> taoTho(
            @Valid @RequestBody TaoThoRequest request,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nguoiDungService.taoTho(chuThau.getId(), request));
    }

    @GetMapping("/tho/doi-ngu")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<List<ThoResponse>> getDoiNgu(@AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(nguoiDungService.getDoiNgu(chuThau.getId()));
    }

    @PatchMapping("/{id}/don-gia-cong")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ThoResponse> suaDonGiaCong(
            @PathVariable Long id,
            @Valid @RequestBody DonGiaCongRequest request,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        return ResponseEntity.ok(nguoiDungService.suaDonGiaCong(chuThau.getId(), id, request));
    }

    @DeleteMapping("/{id}/doi-ngu")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> choThoNghiViec(
            @PathVariable Long id,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        nguoiDungService.choThoNghiViec(chuThau.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
