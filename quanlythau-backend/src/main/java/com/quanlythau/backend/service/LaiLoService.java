package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.laillo.LaiLoResponse;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.ChamCongRepository;
import com.quanlythau.backend.repository.CongTrinhRepository;
import com.quanlythau.backend.util.TinhCongUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaiLoService {

    private final CongTrinhRepository congTrinhRepository;
    private final ChamCongRepository chamCongRepository;
    private final VatTuService vatTuService;
    private final ChiPhiKhacService chiPhiKhacService;
    private final CaiDatService caiDatService;

    @Transactional(readOnly = true)
    public LaiLoResponse tinhLaiLo(Long congTrinhId) {
        CongTrinh congTrinh = congTrinhRepository.findById(congTrinhId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình"));

        BigDecimal chiPhiNhanCong = chamCongRepository.findByCongTrinhIdOrderByNgayChamCongDesc(congTrinhId).stream()
                .filter(c -> c.getTrangThaiXacThuc() == TrangThaiXacThuc.TU_DONG_XAC_NHAN
                        || c.getTrangThaiXacThuc() == TrangThaiXacThuc.CHU_THAU_TAO)
                .map(TinhCongUtil::tongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal chiPhiVatTu = vatTuService.tinhTongChiPhi(congTrinhId);
        BigDecimal chiPhiKhac = chiPhiKhacService.tinhTongChiPhi(congTrinhId);
        BigDecimal tongChiPhi = chiPhiNhanCong.add(chiPhiVatTu).add(chiPhiKhac);

        BigDecimal giaTriHopDong = congTrinh.getGiaTriHopDong();
        BigDecimal laiLoTamTinh = giaTriHopDong != null ? giaTriHopDong.subtract(tongChiPhi) : null;

        Integer nguong = caiDatService.layCaiDat().getNguongCanhBaoChiPhi();
        BigDecimal phanTramDaChi = null;
        boolean vuotNguong = false;
        if (giaTriHopDong != null && giaTriHopDong.compareTo(BigDecimal.ZERO) > 0) {
            phanTramDaChi = tongChiPhi.multiply(BigDecimal.valueOf(100))
                    .divide(giaTriHopDong, 1, RoundingMode.HALF_UP);
            vuotNguong = phanTramDaChi.compareTo(BigDecimal.valueOf(nguong)) >= 0;
        }

        return LaiLoResponse.builder()
                .congTrinhId(congTrinh.getId())
                .tenCongTrinh(congTrinh.getTenCongTrinh())
                .trangThaiCongTrinh(congTrinh.getTrangThai().name())
                .tienDoPhanTram(congTrinh.getTienDoPhanTram())
                .giaTriHopDong(giaTriHopDong)
                .chiPhiNhanCong(chiPhiNhanCong)
                .chiPhiVatTu(chiPhiVatTu)
                .chiPhiKhac(chiPhiKhac)
                .tongChiPhi(tongChiPhi)
                .laiLoTamTinh(laiLoTamTinh)
                .phanTramDaChi(phanTramDaChi)
                .vuotNguongCanhBao(vuotNguong)
                .nguongCanhBaoApDung(nguong)
                .build();
    }

    @Transactional(readOnly = true)
    public List<LaiLoResponse> tinhLaiLoTatCa() {
        return congTrinhRepository.findAll().stream()
                .map(c -> tinhLaiLo(c.getId()))
                .collect(Collectors.toList());
    }
}
