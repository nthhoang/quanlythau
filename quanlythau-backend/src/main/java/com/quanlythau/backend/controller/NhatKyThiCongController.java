package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.nhatky.NhatKyThiCongRequest;
import com.quanlythau.backend.dto.nhatky.NhatKyThiCongResponse;
import com.quanlythau.backend.service.NhatKyThiCongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Nhat ky thi cong: CHU_THAU ghi, ca 2 vai tro deu xem duoc (khong phai du lieu tai chinh). */
@RestController
@RequestMapping("/api/nhat-ky-thi-cong")
@RequiredArgsConstructor
public class NhatKyThiCongController {

    private final NhatKyThiCongService nhatKyThiCongService;

    @GetMapping("/cong-trinh/{congTrinhId}")
    public ResponseEntity<List<NhatKyThiCongResponse>> layDanhSach(@PathVariable Long congTrinhId) {
        return ResponseEntity.ok(nhatKyThiCongService.layDanhSachTheoCongTrinh(congTrinhId));
    }

    @PostMapping("/cong-trinh/{congTrinhId}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<NhatKyThiCongResponse> create(@PathVariable Long congTrinhId, @Valid @RequestBody NhatKyThiCongRequest request) {
        return ResponseEntity.ok(nhatKyThiCongService.create(congTrinhId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<NhatKyThiCongResponse> update(@PathVariable Long id, @Valid @RequestBody NhatKyThiCongRequest request) {
        return ResponseEntity.ok(nhatKyThiCongService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<Void> xoa(@PathVariable Long id) {
        nhatKyThiCongService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
