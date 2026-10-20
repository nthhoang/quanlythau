package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.congtrinh.CongTrinhRequest;
import com.quanlythau.backend.dto.congtrinh.CongTrinhResponse;
import com.quanlythau.backend.dto.congtrinh.PhanCongRequest;
import com.quanlythau.backend.dto.congtrinh.PhanCongResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.service.CongTrinhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cong-trinh")
@RequiredArgsConstructor
public class CongTrinhController {

    private final CongTrinhService congTrinhService;

    @GetMapping
    public ResponseEntity<List<CongTrinhResponse>> getAll(@AuthenticationPrincipal NguoiDung nguoiDung) {
        List<CongTrinhResponse> danhSach = congTrinhService.getAll();
        if (nguoiDung.getVaiTro() == VaiTro.THO) {
            danhSach.forEach(c -> c.setGiaTriHopDong(null));
        }
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CongTrinhResponse> getById(@PathVariable Long id, @AuthenticationPrincipal NguoiDung nguoiDung) {
        CongTrinhResponse congTrinh = congTrinhService.getById(id);
        if (nguoiDung.getVaiTro() == VaiTro.THO) {
            congTrinh.setGiaTriHopDong(null);
        }
        return ResponseEntity.ok(congTrinh);
    }

    @PostMapping
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<CongTrinhResponse> create(
            @Valid @RequestBody CongTrinhRequest request,
            @AuthenticationPrincipal NguoiDung nguoiDung
    ) {
        return ResponseEntity.ok(congTrinhService.create(request, nguoiDung.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<CongTrinhResponse> update(@PathVariable Long id, @Valid @RequestBody CongTrinhRequest request) {
        return ResponseEntity.ok(congTrinhService.update(id, request));
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> updateTrangThai(@PathVariable Long id, @RequestBody Map<String, TrangThaiCongTrinh> body) {
        congTrinhService.updateTrangThai(id, body.get("trangThai"));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/tien-do")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> updateTienDo(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        congTrinhService.updateTienDo(id, body.get("tienDoPhanTram"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        congTrinhService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/phan-cong")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<PhanCongResponse> ganTho(@PathVariable Long id, @Valid @RequestBody PhanCongRequest request) {
        return ResponseEntity.ok(congTrinhService.ganTho(id, request));
    }

    @GetMapping("/{id}/tho")
    public ResponseEntity<List<PhanCongResponse>> getDanhSachTho(@PathVariable Long id) {
        return ResponseEntity.ok(congTrinhService.getDanhSachTho(id));
    }

    @GetMapping("/cua-toi")
    public ResponseEntity<List<CongTrinhResponse>> getCongTrinhCuaToi(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(congTrinhService.getCongTrinhCuaToi(nguoiDung.getId()));
    }

    @DeleteMapping("/{id}/phan-cong/{phanCongId}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> goPhanCong(@PathVariable Long id, @PathVariable Long phanCongId) {
        congTrinhService.goPhanCong(id, phanCongId);
        return ResponseEntity.noContent().build();
    }
}
