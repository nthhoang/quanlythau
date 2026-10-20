package com.quanlythau.backend.util;

import com.quanlythau.backend.entity.ChamCong;
import com.quanlythau.backend.entity.LoaiCong;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TinhCongUtil {

    private static final BigDecimal HE_SO_CA_NGAY = BigDecimal.ONE;
    private static final BigDecimal HE_SO_NUA_CONG = new BigDecimal("0.5");

    private TinhCongUtil() {
    }

    public static BigDecimal heSoCong(ChamCong c) {
        return c.getLoaiCong() == LoaiCong.CA_NGAY ? HE_SO_CA_NGAY : HE_SO_NUA_CONG;
    }

    public static BigDecimal tienCoBan(ChamCong c) {
        BigDecimal donGia = c.getDonGiaApDung() != null ? c.getDonGiaApDung() : BigDecimal.ZERO;
        return donGia.multiply(heSoCong(c));
    }

    public static BigDecimal tienTangCa(ChamCong c) {
        if (!Boolean.TRUE.equals(c.getCoTangCa()) || c.getPhanTramTangCaApDung() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal tyLe = BigDecimal.valueOf(c.getPhanTramTangCaApDung()).divide(BigDecimal.valueOf(100));
        return tienCoBan(c).multiply(tyLe).setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal tongTien(ChamCong c) {
        return tienCoBan(c).add(tienTangCa(c));
    }
}
