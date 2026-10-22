package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.vattu.DieuChuyenRequest;
import com.quanlythau.backend.dto.vattu.QuetHoaDonResponse;
import com.quanlythau.backend.dto.vattu.VatTuBatchRequest;
import com.quanlythau.backend.dto.vattu.VatTuRequest;
import com.quanlythau.backend.dto.vattu.VatTuResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.VatTuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vat-tu")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHU_THAU')")
public class VatTuController {

    private final VatTuService vatTuService;

    @GetMapping("/cong-trinh/{congTrinhId}")
    public ResponseEntity<List<VatTuResponse>> layDanhSach(@PathVariable Long congTrinhId, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(vatTuService.layDanhSachTheoCongTrinh(congTrinhId, chuThau.getId()));
    }

    @GetMapping("/cong-trinh/{congTrinhId}/tong-chi-phi")
    public ResponseEntity<Map<String, BigDecimal>> tongChiPhi(@PathVariable Long congTrinhId, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(Map.of("tongChiPhi", vatTuService.tinhTongChiPhi(congTrinhId, chuThau.getId())));
    }

    @PostMapping("/cong-trinh/{congTrinhId}")
    public ResponseEntity<VatTuResponse> create(@PathVariable Long congTrinhId, @Valid @RequestBody VatTuRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(vatTuService.create(congTrinhId, request, chuThau.getId()));
    }

    @PostMapping("/hang-loat")
    public ResponseEntity<List<VatTuResponse>> createBatch(@Valid @RequestBody VatTuBatchRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(vatTuService.createBatch(request, chuThau.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VatTuResponse> update(@PathVariable Long id, @Valid @RequestBody VatTuRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(vatTuService.update(id, request, chuThau.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Long id, @AuthenticationPrincipal NguoiDung chuThau) {
        vatTuService.xoa(id, chuThau.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/dieu-chuyen")
    public ResponseEntity<VatTuResponse> dieuChuyen(@PathVariable Long id, @Valid @RequestBody DieuChuyenRequest request, @AuthenticationPrincipal NguoiDung chuThau) {
        return ResponseEntity.ok(vatTuService.dieuChuyen(id, request, chuThau.getId()));
    }

    @PostMapping("/quet-hoa-don")
    public ResponseEntity<QuetHoaDonResponse> quetHoaDon(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(vatTuService.quetHoaDon(file));
    }
}
