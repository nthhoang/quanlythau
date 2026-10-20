package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.congkhai.CongTrinhCongKhaiResponse;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.CongTrinhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Xem tien do CONG KHAI (khong can dang nhap) qua shareToken cua cong trinh -
 * de chu thau gui link nay cho chu nha xem tien do + nhat ky thi cong, khong
 * lo thong tin tai chinh noi bo (khong gom gia tri hop dong).
 */
@Service
@RequiredArgsConstructor
public class CongKhaiService {

    private final CongTrinhRepository congTrinhRepository;
    private final NhatKyThiCongService nhatKyThiCongService;

    @Transactional(readOnly = true)
    public CongTrinhCongKhaiResponse xemTheoShareToken(String shareToken) {
        CongTrinh congTrinh = congTrinhRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình (link có thể không đúng)"));

        return CongTrinhCongKhaiResponse.builder()
                .tenCongTrinh(congTrinh.getTenCongTrinh())
                .diaChi(congTrinh.getDiaChi())
                .loaiHinh(congTrinh.getLoaiHinh())
                .trangThai(congTrinh.getTrangThai())
                .ngayBatDau(congTrinh.getNgayBatDau())
                .ngayDuKienHoanThanh(congTrinh.getNgayDuKienHoanThanh())
                .tienDoPhanTram(congTrinh.getTienDoPhanTram())
                .nhatKyThiCong(nhatKyThiCongService.layDanhSachTheoCongTrinh(congTrinh.getId()))
                .build();
    }
}
