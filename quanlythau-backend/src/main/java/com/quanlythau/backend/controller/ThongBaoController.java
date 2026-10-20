package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.thongbao.ThongBaoResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    @GetMapping
    public ResponseEntity<List<ThongBaoResponse>> layDanhSach(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(thongBaoService.layDanhSachCuaToi(nguoiDung.getId()));
    }

    @GetMapping("/chua-doc-so-luong")
    public ResponseEntity<Map<String, Long>> demChuaDoc(@AuthenticationPrincipal NguoiDung nguoiDung) {
        return ResponseEntity.ok(Map.of("soLuong", thongBaoService.demChuaDoc(nguoiDung.getId())));
    }

    @PatchMapping("/{id}/da-doc")
    public ResponseEntity<Void> danhDauDaDoc(@PathVariable Long id, @AuthenticationPrincipal NguoiDung nguoiDung) {
        thongBaoService.danhDauDaDoc(id, nguoiDung.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/da-doc-tat-ca")
    public ResponseEntity<Void> danhDauDaDocTatCa(@AuthenticationPrincipal NguoiDung nguoiDung) {
        thongBaoService.danhDauDaDocTatCa(nguoiDung.getId());
        return ResponseEntity.noContent().build();
    }
}
