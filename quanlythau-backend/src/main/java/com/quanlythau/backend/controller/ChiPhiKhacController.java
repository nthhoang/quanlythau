package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.chiphi.ChiPhiKhacRequest;
import com.quanlythau.backend.dto.chiphi.ChiPhiKhacResponse;
import com.quanlythau.backend.service.ChiPhiKhacService;
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

@RestController
@RequestMapping("/api/chi-phi-khac")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHU_THAU')")
public class ChiPhiKhacController {

    private final ChiPhiKhacService chiPhiKhacService;

    @GetMapping("/cong-trinh/{congTrinhId}")
    public ResponseEntity<List<ChiPhiKhacResponse>> layDanhSach(@PathVariable Long congTrinhId) {
        return ResponseEntity.ok(chiPhiKhacService.layDanhSachTheoCongTrinh(congTrinhId));
    }

    @PostMapping("/cong-trinh/{congTrinhId}")
    public ResponseEntity<ChiPhiKhacResponse> create(@PathVariable Long congTrinhId, @Valid @RequestBody ChiPhiKhacRequest request) {
        return ResponseEntity.ok(chiPhiKhacService.create(congTrinhId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiPhiKhacResponse> update(@PathVariable Long id, @Valid @RequestBody ChiPhiKhacRequest request) {
        return ResponseEntity.ok(chiPhiKhacService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Long id) {
        chiPhiKhacService.xoa(id);
        return ResponseEntity.noContent().build();
    }
}
