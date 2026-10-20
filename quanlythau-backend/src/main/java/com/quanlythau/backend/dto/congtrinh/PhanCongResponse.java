package com.quanlythau.backend.dto.congtrinh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhanCongResponse {
    private Long id;
    private Long congTrinhId;
    private String tenCongTrinh;
    private Long nguoiDungId;
    private String hoTenTho;
    private String soDienThoaiTho;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
}
