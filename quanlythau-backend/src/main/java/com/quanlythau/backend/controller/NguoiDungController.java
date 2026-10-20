package com.quanlythau.backend.controller;

import com.quanlythau.backend.dto.nguoidung.DonGiaCongRequest;
import com.quanlythau.backend.dto.nguoidung.ThoResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.VaiTro;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.NguoiDungRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping("/tho")
    public ResponseEntity<List<ThoResponse>> getDanhSachTho() {
        List<ThoResponse> danhSach = nguoiDungRepository.findByVaiTro(VaiTro.THO).stream()
                .map(this::toThoResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(danhSach);
    }

    @PatchMapping("/{id}/don-gia-cong")
    @PreAuthorize("hasRole('CHU_THAU')")
    public ResponseEntity<ThoResponse> suaDonGiaCong(@PathVariable Long id, @Valid @RequestBody DonGiaCongRequest request) {
        NguoiDung tho = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        tho.setDonGiaCong(request.getDonGiaCong());
        nguoiDungRepository.save(tho);
        return ResponseEntity.ok(toThoResponse(tho));
    }

    private ThoResponse toThoResponse(NguoiDung n) {
        return ThoResponse.builder()
                .id(n.getId())
                .hoTen(n.getHoTen())
                .soDienThoai(n.getSoDienThoai())
                .chuyenMon(n.getChuyenMon())
                .donGiaCong(n.getDonGiaCong())
                .build();
    }
}
