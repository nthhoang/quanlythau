package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.luong.BangLuongResponse;
import com.quanlythau.backend.dto.luong.LuongPreviewResponse;
import com.quanlythau.backend.dto.luong.LuongRequest;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.LuongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/luong")
@RequiredArgsConstructor
public class LuongController {

    private final LuongService luongService;

    @GetMapping("/xem-truoc")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<LuongPreviewResponse> xemTruoc(
            @RequestParam Long nguoiDungId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        return ResponseEntity.ok(luongService.tinhLuong(nguoiDungId, tuNgay, denNgay, chuThau.getId()));
    }

    @GetMapping("/xem-truoc-tat-ca")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<List<LuongPreviewResponse>> xemTruocTatCa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        return ResponseEntity.ok(luongService.tinhLuongTatCa(tuNgay, denNgay, chuThau.getId()));
    }

    @PostMapping("/chot")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<BangLuongResponse> chotLuong(@Valid @RequestBody LuongRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(luongService.chotLuong(request.getNguoiDungId(), request.getTuNgay(), request.getDenNgay(), chuThau.getId()));
    }

    @GetMapping("/lich-su/{nguoiDungId}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<List<BangLuongResponse>> lichSuTheoTho(@PathVariable Long nguoiDungId, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(luongService.layLichSu(nguoiDungId, chuThau.getId()));
    }

    @GetMapping("/cua-toi")
    @PreAuthorize("hasRole('THO')")
    public ResponseEntity<List<BangLuongResponse>> luongCuaToi(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(luongService.layLichSuCuaTho(nguoiDung.getId()));
    }

    @PatchMapping("/{id}/da-thanh-toan")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<BangLuongResponse> danhDauDaThanhToan(@PathVariable Long id, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(luongService.danhDauDaThanhToan(id, chuThau.getId()));
    }
}
