package com.quanlythau.backend.dto.vattu;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DieuChuyenRequest {

    @NotNull(message = "Phải chọn công trình đích")
    private Long congTrinhDichId;

    @NotNull(message = "Phải nhập số lượng chuyển")
    @Positive(message = "Số lượng chuyển phải lớn hơn 0")
    private BigDecimal soLuong;
}
