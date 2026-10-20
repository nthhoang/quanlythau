package com.quanlythau.backend.dto.caidat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaiDatRequest {

    @NotNull(message = "Phải nhập % tăng ca")
    @Min(value = 0, message = "% tăng ca không được âm")
    private Integer phanTramTangCa;

    @NotNull(message = "Phải nhập ngưỡng cảnh báo chi phí")
    @Min(value = 1, message = "Ngưỡng cảnh báo phải lớn hơn 0")
    @Max(value = 100, message = "Ngưỡng cảnh báo không vượt quá 100%")
    private Integer nguongCanhBaoChiPhi;
}
