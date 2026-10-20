package com.quanlythau.backend.dto.vattu;

import jakarta.validation.Valid;
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
public class VatTuBatchRequest {

    @NotNull(message = "Phải chọn công trình")
    private Long congTrinhId;

    private String anhHoaDon;

    @NotEmpty(message = "Danh sách vật tư không được rỗng")
    @Valid
    private List<VatTuRequest> danhSach;
}
