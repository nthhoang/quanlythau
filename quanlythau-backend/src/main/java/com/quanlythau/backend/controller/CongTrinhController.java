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
        List<CongTrinhResponse> danhSach = congTrinhService.getAll(nguoiDung.getId(), nguoiDung.getVaiTro());
        if (nguoiDung.getVaiTro() == VaiTro.THO) {
            danhSach.forEach(c -> c.setGiaTriHopDong(null));
        }
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CongTrinhResponse> getById(@PathVariable Long id, @AuthenticationPrincipal NguoiDung nguoiDung) {
        CongTrinhResponse congTrinh = congTrinhService.getById(id, nguoiDung);
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
    public ResponseEntity<CongTrinhResponse> update(@PathVariable Long id, @Valid @RequestBody CongTrinhRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(congTrinhService.update(id, request, chuThau.getId()));
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> updateTrangThai(@PathVariable Long id, @RequestBody Map<String, TrangThaiCongTrinh> body, @AuthenticationPrincipal NguoiDung chuThau) {
        congTrinhService.updateTrangThai(id, body.get("trangThai"), chuThau.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/tien-do")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> updateTienDo(@PathVariable Long id, @RequestBody Map<String, Integer> body, @AuthenticationPrincipal NguoiDung chuThau) {
        congTrinhService.updateTienDo(id, body.get("tienDoPhanTram"), chuThau.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal NguoiDung chuThau) {
        congTrinhService.delete(id, chuThau.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/phan-cong")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<PhanCongResponse> ganTho(@PathVariable Long id, @Valid @RequestBody PhanCongRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(congTrinhService.ganTho(id, request, chuThau.getId()));
    }

    @GetMapping("/{id}/tho")
    public ResponseEntity<List<PhanCongResponse>> getDanhSachTho(@PathVariable Long id, @AuthenticationPrincipal NguoiDung nguoiDung) {
        if (nguoiDung.getVaiTro() == VaiTro.CHU_THAU) {
            return ResponseEntity.ok(congTrinhService.getDanhSachTho(id, nguoiDung.getId()));
        }
        return ResponseEntity.ok(congTrinhService.getDanhSachThoCuaTho(id, nguoiDung.getId()));
    }

    @GetMapping("/cua-toi")
    public ResponseEntity<List<CongTrinhResponse>> getCongTrinhCuaToi(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(congTrinhService.getCongTrinhCuaToi(nguoiDung.getId()));
    }

    @DeleteMapping("/{id}/phan-cong/{phanCongId}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> goPhanCong(@PathVariable Long id, @PathVariable Long phanCongId, @AuthenticationPrincipal NguoiDung chuThau) {
        congTrinhService.goPhanCong(id, phanCongId, chuThau.getId());
        return ResponseEntity.noContent().build();
    }
}
