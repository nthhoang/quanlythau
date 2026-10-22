package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.chamcong.ChamCongRequest;
import com.quanlythau.backend.dto.chamcong.ChamCongResponse;
import com.quanlythau.backend.dto.chamcong.ChamCongThuCongRequest;
import com.quanlythau.backend.dto.chamcong.KhuonMatRequest;
import com.quanlythau.backend.dto.chamcong.LoaiCongUpdateRequest;
import com.quanlythau.backend.dto.chamcong.TrangThaiThoResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import com.quanlythau.backend.service.ChamCongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.Map;

@RestController
@RequestMapping("/api/cham-cong")
@RequiredArgsConstructor
public class ChamCongController {

    private final ChamCongService chamCongService;

    @PostMapping
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ChamCongResponse> chamCong(@Valid @RequestBody ChamCongRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(chamCongService.chamCong(request, chuThau.getId()));
    }

    @GetMapping("/trang-thai-cong-trinh/{congTrinhId}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<List<TrangThaiThoResponse>> trangThaiTheoCongTrinh(@PathVariable Long congTrinhId, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(chamCongService.layTrangThaiTheoCongTrinh(congTrinhId, chuThau.getId()));
    }

    @GetMapping("/cua-toi")
    @PreAuthorize("hasRole('THO')")
    public ResponseEntity<List<ChamCongResponse>> lichSuCuaToi(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(chamCongService.layLichSuCuaToi(nguoiDung.getId()));
    }

    @PostMapping("/thu-cong")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ChamCongResponse> chamCongThuCong(@Valid @RequestBody ChamCongThuCongRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(chamCongService.chamCongThuCong(request, chuThau.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<List<ChamCongResponse>> layDanhSach(
            @RequestParam(required = false) Long congTrinhId,
            @RequestParam(required = false) Long nguoiDungId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            @RequestParam(required = false) TrangThaiXacThuc trangThai,
            @AuthenticationPrincipal NguoiDung chuThau
    ) {
        return ResponseEntity.ok(chamCongService.layDanhSach(chuThau.getId(), congTrinhId, nguoiDungId, ngay, trangThai));
    }

    @PatchMapping("/{id}/duyet")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ChamCongResponse> duyet(@PathVariable Long id, @RequestBody Map<String, Boolean> body, @AuthenticationPrincipal NguoiDung chuThau) {
        boolean dongY = Boolean.TRUE.equals(body.get("dongY"));
        return ResponseEntity.ok(chamCongService.duyet(id, dongY, chuThau.getId()));
    }

    @PatchMapping("/{id}/loai-cong")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ChamCongResponse> capNhatLoaiCong(@PathVariable Long id, @Valid @RequestBody LoaiCongUpdateRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(chamCongService.capNhatLoaiCong(id, request.getLoaiCong(), chuThau.getId()));
    }

    @PatchMapping("/{id}/tang-ca")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ChamCongResponse> capNhatTangCa(@PathVariable Long id, @RequestBody Map<String, Boolean> body, @AuthenticationPrincipal NguoiDung chuThau) {
        boolean coTangCa = Boolean.TRUE.equals(body.get("coTangCa"));
        return ResponseEntity.ok(chamCongService.capNhatTangCa(id, coTangCa, chuThau.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> xoa(@PathVariable Long id, @AuthenticationPrincipal NguoiDung chuThau) {
        chamCongService.xoa(id, chuThau.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/khuon-mat")
    @PreAuthorize("hasAnyRole('THO','CHU_THAU')")
    public ResponseEntity<Void> luuKhuonMat(@Valid @RequestBody KhuonMatRequest request, @AuthenticationPrincipal NguoiDung nguoiGoi) {
        chamCongService.luuKhuonMat(nguoiGoi, request.getNguoiDungId(), request.getFaceDescriptor());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/khuon-mat/trang-thai-cua-toi")
    @PreAuthorize("hasRole('THO')")
    public ResponseEntity<Map<String, Boolean>> trangThaiKhuonMatCuaToi(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(Map.of("daThietLap", chamCongService.daThietLapKhuonMat(nguoiDung)));
    }
}
