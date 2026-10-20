package com.quanlythau.backend.dto.chamcong;

import com.quanlythau.backend.entity.LoaiCong;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChamCongThuCongRequest {

    @NotNull(message = "Phai chon tho")
    private Long nguoiDungId;

    @NotNull(message = "Phai chon cong trinh")
    private Long congTrinhId;

    @NotNull(message = "Phai chon ngay")
    private LocalDate ngayChamCong;

    @NotNull(message = "Phai chon loai cong")
    private LoaiCong loaiCong;

    private Boolean coTangCa = false;

    private String ghiChu;
}
