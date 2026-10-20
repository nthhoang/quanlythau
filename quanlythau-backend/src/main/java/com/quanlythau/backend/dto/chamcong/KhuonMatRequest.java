package com.quanlythau.backend.dto.chamcong;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuonMatRequest {

    private Long nguoiDungId;

    @NotEmpty(message = "Thiếu dữ liệu khuôn mặt")
    private List<Double> faceDescriptor;
}
