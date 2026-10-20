package com.quanlythau.backend.dto.tamung;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class TamUngRequest {

    @NotNull(message = "Phải chọn thợ")
    private Long nguoiDungId;

    @NotNull(message = "Phải nhập số tiền")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal soTien;

    @NotNull(message = "Phải chọn ngày ứng")
    private LocalDate ngayUng;

    private String ghiChu;
}
