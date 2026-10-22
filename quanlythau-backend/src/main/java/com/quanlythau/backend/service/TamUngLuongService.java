package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.tamung.TamUngRequest;
import com.quanlythau.backend.dto.tamung.TamUngResponse;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.TamUngLuong;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.repository.TamUngLuongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TamUngLuongService {

    private final TamUngLuongRepository tamUngRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungService nguoiDungService;

    public TamUngResponse taoTamUng(TamUngRequest request, Long chuThauId) {
        nguoiDungService.kiemTraDangLamViec(chuThauId, request.getNguoiDungId());
        NguoiDung tho = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));

        TamUngLuong tamUng = TamUngLuong.builder()
                .nguoiDung(tho)
                .chuThauId(chuThauId)
                .soTien(request.getSoTien())
                .ngayUng(request.getNgayUng())
                .ghiChu(request.getGhiChu())
                .build();

        tamUngRepository.save(tamUng);
        return toResponse(tamUng);
    }

    @Transactional(readOnly = true)
    public List<TamUngResponse> layDanhSachTheoTho(Long nguoiDungId, Long chuThauId) {
        nguoiDungService.kiemTraThuocDoiNgu(chuThauId, nguoiDungId);
        return tamUngRepository.findByNguoiDungIdAndChuThauIdOrderByNgayUngDesc(nguoiDungId, chuThauId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void xoa(Long id, Long chuThauId) {
        tamUngRepository.delete(tamUngRepository.findByIdAndChuThauId(id, chuThauId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoản tạm ứng")));
    }

    private TamUngResponse toResponse(TamUngLuong t) {
        return TamUngResponse.builder()
                .id(t.getId())
                .nguoiDungId(t.getNguoiDung().getId())
                .hoTenTho(t.getNguoiDung().getHoTen())
                .soTien(t.getSoTien())
                .ngayUng(t.getNgayUng())
                .ghiChu(t.getGhiChu())
                .build();
    }
}
