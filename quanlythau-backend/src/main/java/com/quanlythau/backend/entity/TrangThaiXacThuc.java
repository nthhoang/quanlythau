package com.quanlythau.backend.entity;

/**
 * Trang thai xac thuc cua 1 lan cham cong.
 * TU_DONG_XAC_NHAN: khuon mat khop -> he thong tu duyet.
 * CAN_DUYET: khuon mat khong khop -> cho chu thau duyet tay.
 * CHU_THAU_TAO: chu thau tao/cham cong thay truc tiep, khong qua buoc xac thuc.
 * TU_CHOI: chu thau xem va tu choi ban ghi CAN_DUYET.
 */
public enum TrangThaiXacThuc {
    TU_DONG_XAC_NHAN,
    CAN_DUYET,
    CHU_THAU_TAO,
    TU_CHOI
}
