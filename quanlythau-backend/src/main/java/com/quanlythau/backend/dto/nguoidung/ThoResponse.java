package com.quanlythau.backend.dto.nguoidung;

import com.quanlythau.backend.entity.ChuyenMon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThoResponse {
    private Long id;
    private String tenDangNhap;
    private String hoTen;
    private String soDienThoai;
    private ChuyenMon chuyenMon;
    private BigDecimal donGiaCong;
    private Boolean dangLamViec;
    private Boolean active;
    private List<String> congTrinhDangLamViec;
}
