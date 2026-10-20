package com.quanlythau.backend.dto.luong;

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
public class LuongRequest {

    @NotNull(message = "Phải chọn thợ")
    private Long nguoiDungId;

    @NotNull(message = "Phải chọn ngày bắt đầu")
    private LocalDate tuNgay;

    @NotNull(message = "Phải chọn ngày kết thúc")
    private LocalDate denNgay;
}
