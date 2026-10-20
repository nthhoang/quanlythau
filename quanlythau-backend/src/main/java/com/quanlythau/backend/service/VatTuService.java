package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.vattu.DieuChuyenRequest;
import com.quanlythau.backend.dto.vattu.DongVatTuGoiYResponse;
import com.quanlythau.backend.dto.vattu.QuetHoaDonResponse;
import com.quanlythau.backend.dto.vattu.VatTuBatchRequest;
import com.quanlythau.backend.dto.vattu.VatTuRequest;
import com.quanlythau.backend.dto.vattu.VatTuResponse;
import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.VatTu;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.exception.ResourceNotFoundException;
import com.quanlythau.backend.repository.CongTrinhRepository;
import com.quanlythau.backend.repository.VatTuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VatTuService {

    private final VatTuRepository vatTuRepository;
    private final CongTrinhRepository congTrinhRepository;
    private final FileStorageService fileStorageService;
    private final GeminiService geminiService;

    public VatTuResponse create(Long congTrinhId, VatTuRequest request) {
        CongTrinh congTrinh = timCongTrinh(congTrinhId);
        VatTu vatTu = tuRequest(request, congTrinh);
        vatTuRepository.save(vatTu);
        return toResponse(vatTu);
    }

    public List<VatTuResponse> createBatch(VatTuBatchRequest request) {
        CongTrinh congTrinh = timCongTrinh(request.getCongTrinhId());
        return request.getDanhSach().stream()
                .map(dong -> {
                    if (dong.getAnhHoaDon() == null) {
                        dong.setAnhHoaDon(request.getAnhHoaDon());
                    }
                    VatTu vatTu = tuRequest(dong, congTrinh);
                    vatTuRepository.save(vatTu);
                    return toResponse(vatTu);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VatTuResponse> layDanhSachTheoCongTrinh(Long congTrinhId) {
        return vatTuRepository.findByCongTrinhId(congTrinhId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal tinhTongChiPhi(Long congTrinhId) {
        return vatTuRepository.findByCongTrinhId(congTrinhId).stream()
                .map(VatTu::getThanhTien)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public VatTuResponse update(Long id, VatTuRequest request) {
        VatTu vatTu = timVatTu(id);
        vatTu.setTenVatTu(request.getTenVatTu());
        vatTu.setSoLuong(request.getSoLuong());
        vatTu.setDonVi(request.getDonVi());
        vatTu.setDonGia(request.getDonGia());
        vatTu.setThanhTien(tinhThanhTien(request.getSoLuong(), request.getDonGia()));
        vatTu.setNhaCungCap(request.getNhaCungCap());
        vatTu.setNgayNhap(request.getNgayNhap());
        vatTu.setGhiChu(request.getGhiChu());
        vatTuRepository.save(vatTu);
        return toResponse(vatTu);
    }

    public void xoa(Long id) {
        if (!vatTuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy vật tư");
        }
        vatTuRepository.deleteById(id);
    }

    public VatTuResponse dieuChuyen(Long id, DieuChuyenRequest request) {
        VatTu goc = timVatTu(id);

        if (request.getSoLuong().compareTo(goc.getSoLuong()) > 0) {
            throw new BadRequestException("Số lượng chuyển không được vượt quá số lượng hiện có (" + goc.getSoLuong() + ")");
        }
        if (request.getCongTrinhDichId().equals(goc.getCongTrinh().getId())) {
            throw new BadRequestException("Công trình đích phải khác công trình hiện tại");
        }

        CongTrinh congTrinhDich = timCongTrinh(request.getCongTrinhDichId());

        BigDecimal soLuongConLai = goc.getSoLuong().subtract(request.getSoLuong());
        goc.setSoLuong(soLuongConLai);
        goc.setThanhTien(tinhThanhTien(soLuongConLai, goc.getDonGia()));
        vatTuRepository.save(goc);

        VatTu dongMoi = VatTu.builder()
                .congTrinh(congTrinhDich)
                .tenVatTu(goc.getTenVatTu())
                .soLuong(request.getSoLuong())
                .donVi(goc.getDonVi())
                .donGia(goc.getDonGia())
                .thanhTien(tinhThanhTien(request.getSoLuong(), goc.getDonGia()))
                .nhaCungCap("Điều chuyển từ " + goc.getCongTrinh().getTenCongTrinh())
                .ngayNhap(LocalDate.now())
                .ghiChu("Điều chuyển từ vật tư #" + goc.getId())
                .build();
        vatTuRepository.save(dongMoi);

        return toResponse(goc);
    }

    public QuetHoaDonResponse quetHoaDon(MultipartFile file) {
        String url = fileStorageService.luuFile(file);
        try {
            List<DongVatTuGoiYResponse> goiY = geminiService.docHoaDon(file.getBytes(), file.getContentType());
            return QuetHoaDonResponse.builder().anhHoaDon(url).danhSachGoiY(goiY).build();
        } catch (java.io.IOException e) {
            throw new BadRequestException("Không đọc được file ảnh");
        }
    }

    private VatTu tuRequest(VatTuRequest request, CongTrinh congTrinh) {
        return VatTu.builder()
                .congTrinh(congTrinh)
                .tenVatTu(request.getTenVatTu())
                .soLuong(request.getSoLuong())
                .donVi(request.getDonVi())
                .donGia(request.getDonGia())
                .thanhTien(tinhThanhTien(request.getSoLuong(), request.getDonGia()))
                .nhaCungCap(request.getNhaCungCap())
                .ngayNhap(request.getNgayNhap() != null ? request.getNgayNhap() : LocalDate.now())
                .anhHoaDon(request.getAnhHoaDon())
                .ghiChu(request.getGhiChu())
                .build();
    }

    private BigDecimal tinhThanhTien(BigDecimal soLuong, BigDecimal donGia) {
        if (soLuong == null || donGia == null) {
            return BigDecimal.ZERO;
        }
        return soLuong.multiply(donGia);
    }

    private CongTrinh timCongTrinh(Long id) {
        return congTrinhRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công trình"));
    }

    private VatTu timVatTu(Long id) {
        return vatTuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vật tư"));
    }

    private VatTuResponse toResponse(VatTu v) {
        return VatTuResponse.builder()
                .id(v.getId())
                .congTrinhId(v.getCongTrinh().getId())
                .tenCongTrinh(v.getCongTrinh().getTenCongTrinh())
                .tenVatTu(v.getTenVatTu())
                .soLuong(v.getSoLuong())
                .donVi(v.getDonVi())
                .donGia(v.getDonGia())
                .thanhTien(v.getThanhTien())
                .nhaCungCap(v.getNhaCungCap())
                .ngayNhap(v.getNgayNhap())
                .anhHoaDon(v.getAnhHoaDon())
                .ghiChu(v.getGhiChu())
                .build();
    }
}
