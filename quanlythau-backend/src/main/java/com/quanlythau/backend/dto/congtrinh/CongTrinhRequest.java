package com.quanlythau.backend.dto.congtrinh;

import com.quanlythau.backend.entity.LoaiHinhCongTrinh;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CongTrinhRequest {

    @NotBlank(message = "Ten cong trinh khong duoc de trong")
    private String tenCongTrinh;

    @NotBlank(message = "Dia chi khong duoc de trong")
    private String diaChi;

    private String tenChuNha;
    private String sdtChuNha;

    @NotNull(message = "Loai hinh khong duoc de trong")
    private LoaiHinhCongTrinh loaiHinh;

    private LocalDate ngayBatDau;
    private LocalDate ngayDuKienHoanThanh;
    private BigDecimal giaTriHopDong;
}
