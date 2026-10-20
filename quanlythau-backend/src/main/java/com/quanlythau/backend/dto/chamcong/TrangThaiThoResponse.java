package com.quanlythau.backend.dto.chamcong;

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
public class TrangThaiThoResponse {
    private Long nguoiDungId;
    private String hoTen;
    private boolean daThietLapKhuonMat;
    private boolean daChamCongTaiDay;
    private ChamCongResponse chamCongTaiDay;
    private List<ChamCongResponse> chamCongNoiKhacHomNay;
}
