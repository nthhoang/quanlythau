package com.quanlythau.backend.entity;

/**
 * Don vi cong trong 1 lan cham cong. TANG_CA khong con la 1 loai rieng nua -
 * xem truong coTangCa + phanTramTangCaApDung trong ChamCong: tang ca la PHU
 * THEM % vao tien cong cua dung ngay do, khong thay the CA_NGAY/NUA_CONG.
 */
public enum LoaiCong {
    CA_NGAY,
    NUA_CONG
}
