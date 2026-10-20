package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.thongbao.ThongBaoResponse;
import com.quanlythau.backend.entity.LoaiThongBao;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.ThongBao;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tao thong bao + day real-time qua WebSocket (STOMP) toi dung nguoi nhan.
 * Luon LUU xuong DB truoc (de con xem lai lich su qua REST khi nguoi dung
 * dang offline luc thong bao phat sinh), sau do moi gui qua socket cho
 * nguoi dang online - WebSocket la lop "day them" chu khong phai nguon du
 * lieu duy nhat.
 */
@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void tao(NguoiDung nguoiNhan, LoaiThongBao loai, String tieuDe, String noiDung) {
        ThongBao thongBao = ThongBao.builder()
                .nguoiNhan(nguoiNhan)
                .loai(loai)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .build();
        thongBaoRepository.save(thongBao);

        messagingTemplate.convertAndSendToUser(
                nguoiNhan.getTenDangNhap(),
                "/queue/thong-bao",
                toResponse(thongBao)
        );
    }

    @Transactional(readOnly = true)
    public List<ThongBaoResponse> layDanhSachCuaToi(Long nguoiDungId) {
        return thongBaoRepository.findByNguoiNhanIdOrderByCreatedAtDesc(nguoiDungId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long demChuaDoc(Long nguoiDungId) {
        return thongBaoRepository.countByNguoiNhanIdAndDaDocFalse(nguoiDungId);
    }

    public void danhDauDaDoc(Long id, Long nguoiDungId) {
        ThongBao thongBao = thongBaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        if (!thongBao.getNguoiNhan().getId().equals(nguoiDungId)) {
            return; // khong phai thong bao cua minh thi bo qua, khong bao loi ro nguyen nhan
        }
        thongBao.setDaDoc(true);
        thongBaoRepository.save(thongBao);
    }

    public void danhDauDaDocTatCa(Long nguoiDungId) {
        List<ThongBao> danhSach = thongBaoRepository.findByNguoiNhanIdOrderByCreatedAtDesc(nguoiDungId);
        danhSach.forEach(t -> t.setDaDoc(true));
        thongBaoRepository.saveAll(danhSach);
    }

    private ThongBaoResponse toResponse(ThongBao t) {
        return ThongBaoResponse.builder()
                .id(t.getId())
                .tieuDe(t.getTieuDe())
                .noiDung(t.getNoiDung())
                .loai(t.getLoai())
                .daDoc(t.getDaDoc())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
