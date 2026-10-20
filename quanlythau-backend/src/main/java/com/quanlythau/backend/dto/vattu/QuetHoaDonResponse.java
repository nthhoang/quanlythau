package com.quanlythau.backend.dto.vattu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuetHoaDonResponse {
    private String anhHoaDon;
    private List<DongVatTuGoiYResponse> danhSachGoiY;
}
