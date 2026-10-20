package com.quanlythau.backend.dto.congtrinh;

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
public class PhanCongRequest {

    @NotNull(message = "Phai chon nguoi dung")
    private Long nguoiDungId;

    private LocalDate ngayBatDau;
}
