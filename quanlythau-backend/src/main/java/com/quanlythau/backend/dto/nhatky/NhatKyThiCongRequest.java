package com.quanlythau.backend.dto.nhatky;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhatKyThiCongRequest {

    private LocalDate ngay;

    @NotBlank(message = "Phải nhập nội dung nhật ký")
    private String noiDung;

    private List<String> danhSachAnh;
}
