package com.quanlythau.backend.dto.chamcong;

import com.quanlythau.backend.entity.LoaiCong;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamCongResponse {
    private Long id;
    private Long nguoiDungId;
    private String hoTenTho;
    private Long congTrinhId;
    private String tenCongTrinh;
    private LocalDate ngayChamCong;
    private LoaiCong loaiCong;
    private Boolean coTangCa;
    private BigDecimal donGiaApDung;
    private Integer phanTramTangCaApDung;
    private LocalDateTime gioCheckIn;
    private String anhChamCong;
    private Double khoangCachGuong;
    private TrangThaiXacThuc trangThaiXacThuc;
    private String ghiChu;
}
