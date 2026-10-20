package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.chiphi.ChiPhiKhacRequest;
import com.quanlythau.backend.dto.chiphi.ChiPhiKhacResponse;
import com.quanlythau.backend.entity.ChiPhiKhac;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.ChiPhiKhacRepository;
import com.quanlythau.backend.repository.CongTrinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChiPhiKhacService {

    private final ChiPhiKhacRepository chiPhiKhacRepository;
    private final CongTrinhRepository congTrinhRepository;

    public ChiPhiKhacResponse create(Long congTrinhId, ChiPhiKhacRequest request) {
        CongTrinh congTrinh = congTrinhRepository.findById(congTrinhId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình"));

        ChiPhiKhac chiPhi = ChiPhiKhac.builder()
                .congTrinh(congTrinh)
                .loaiChiPhi(request.getLoaiChiPhi())
                .soTien(request.getSoTien())
                .ngay(request.getNgay() != null ? request.getNgay() : LocalDate.now())
                .ghiChu(request.getGhiChu())
                .build();

        chiPhiKhacRepository.save(chiPhi);
        return toResponse(chiPhi);
    }

    @Transactional(readOnly = true)
    public List<ChiPhiKhacResponse> layDanhSachTheoCongTrinh(Long congTrinhId) {
        return chiPhiKhacRepository.findByCongTrinhId(congTrinhId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal tinhTongChiPhi(Long congTrinhId) {
        return chiPhiKhacRepository.findByCongTrinhId(congTrinhId).stream()
                .map(ChiPhiKhac::getSoTien)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ChiPhiKhacResponse update(Long id, ChiPhiKhacRequest request) {
        ChiPhiKhac chiPhi = chiPhiKhacRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi phí"));
        chiPhi.setLoaiChiPhi(request.getLoaiChiPhi());
        chiPhi.setSoTien(request.getSoTien());
        chiPhi.setNgay(request.getNgay());
        chiPhi.setGhiChu(request.getGhiChu());
        chiPhiKhacRepository.save(chiPhi);
        return toResponse(chiPhi);
    }

    public void xoa(Long id) {
        if (!chiPhiKhacRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy chi phí");
        }
        chiPhiKhacRepository.deleteById(id);
    }

    private ChiPhiKhacResponse toResponse(ChiPhiKhac c) {
        return ChiPhiKhacResponse.builder()
                .id(c.getId())
                .congTrinhId(c.getCongTrinh().getId())
                .loaiChiPhi(c.getLoaiChiPhi())
                .soTien(c.getSoTien())
                .ngay(c.getNgay())
                .ghiChu(c.getGhiChu())
                .build();
    }
}
