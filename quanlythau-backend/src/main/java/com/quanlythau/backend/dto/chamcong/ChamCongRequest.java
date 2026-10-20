package com.quanlythau.backend.dto.chamcong;

import com.quanlythau.backend.entity.LoaiCong;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChamCongRequest {

    @NotNull(message = "Phải chọn thợ")
    private Long nguoiDungId;

    @NotNull(message = "Phải chọn công trình")
    private Long congTrinhId;

    @NotNull(message = "Phải chọn loại công")
    private LoaiCong loaiCong;

    private Boolean coTangCa = false;

    @NotEmpty(message = "Thiếu dữ liệu khuôn mặt, hãy thử quét lại")
    private List<Double> faceDescriptor;

    private String anhChamCong;
    private String ghiChu;
}
