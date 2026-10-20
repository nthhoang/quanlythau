package com.quanlythau.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanlythau.backend.dto.nhatky.NhatKyThiCongRequest;
import com.quanlythau.backend.dto.nhatky.NhatKyThiCongResponse;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.NhatKyThiCong;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.CongTrinhRepository;
import com.quanlythau.backend.repository.NhatKyThiCongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Nhat ky thi cong hang ngay - noi dung + anh hien truong. Day la du lieu
 * CHU THAU ghi de bao cao tien do; danh sach anh luu duoi dang chuoi JSON
 * (giong cach ChamCongService luu faceDescriptor) vi day chi la 1 mang URL
 * don gian, khong can bang rieng.
 */
@Service
@RequiredArgsConstructor
public class NhatKyThiCongService {

    private final NhatKyThiCongRepository nhatKyThiCongRepository;
    private final CongTrinhRepository congTrinhRepository;
    private final ObjectMapper objectMapper;

    public NhatKyThiCongResponse create(Long congTrinhId, NhatKyThiCongRequest request) {
        CongTrinh congTrinh = timCongTrinh(congTrinhId);

        NhatKyThiCong nhatKy = NhatKyThiCong.builder()
                .congTrinh(congTrinh)
                .ngay(request.getNgay() != null ? request.getNgay() : LocalDate.now())
                .noiDung(request.getNoiDung())
                .danhSachAnh(ghiDanhSachAnh(request.getDanhSachAnh()))
                .build();

        nhatKyThiCongRepository.save(nhatKy);
        return toResponse(nhatKy);
    }

    @Transactional(readOnly = true)
    public List<NhatKyThiCongResponse> layDanhSachTheoCongTrinh(Long congTrinhId) {
        return nhatKyThiCongRepository.findByCongTrinhIdOrderByNgayDesc(congTrinhId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public NhatKyThiCongResponse update(Long id, NhatKyThiCongRequest request) {
        NhatKyThiCong nhatKy = timNhatKy(id);
        nhatKy.setNgay(request.getNgay() != null ? request.getNgay() : nhatKy.getNgay());
        nhatKy.setNoiDung(request.getNoiDung());
        nhatKy.setDanhSachAnh(ghiDanhSachAnh(request.getDanhSachAnh()));
        nhatKyThiCongRepository.save(nhatKy);
        return toResponse(nhatKy);
    }

    public void xoa(Long id) {
        if (!nhatKyThiCongRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy nhật ký thi công");
        }
        nhatKyThiCongRepository.deleteById(id);
    }

    private CongTrinh timCongTrinh(Long id) {
        return congTrinhRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình"));
    }

    private NhatKyThiCong timNhatKy(Long id) {
        return nhatKyThiCongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký thi công"));
    }

    private String ghiDanhSachAnh(List<String> danhSach) {
        try {
            return objectMapper.writeValueAsString(danhSach != null ? danhSach : Collections.emptyList());
        } catch (Exception e) {
            throw new BadRequestException("Danh sách ảnh không hợp lệ");
        }
    }

    private List<String> docDanhSachAnh(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private NhatKyThiCongResponse toResponse(NhatKyThiCong n) {
        return NhatKyThiCongResponse.builder()
                .id(n.getId())
                .congTrinhId(n.getCongTrinh().getId())
                .ngay(n.getNgay())
                .noiDung(n.getNoiDung())
                .danhSachAnh(docDanhSachAnh(n.getDanhSachAnh()))
                .createdAt(n.getCreatedAt())
                .build();
    }
}
