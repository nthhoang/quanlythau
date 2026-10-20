package com.quanlythau.backend.dto.luong;

import com.quanlythau.backend.dto.chamcong.ChamCongResponse;
import com.quanlythau.backend.dto.tamung.TamUngResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuongPreviewResponse {
    private Long nguoiDungId;
    private String hoTenTho;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private BigDecimal tongCong;
    private BigDecimal tienCoBan;
    private BigDecimal tienTangCa;
    private BigDecimal tongTienCong;
    private BigDecimal tongTamUng;
    private BigDecimal thucNhan;
    private List<ChamCongResponse> chiTietCham;
    private List<TamUngResponse> chiTietTamUng;
    private int soBanGhiChuaXacNhan;
}
