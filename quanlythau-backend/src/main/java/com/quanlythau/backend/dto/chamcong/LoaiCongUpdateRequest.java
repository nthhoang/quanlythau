package com.quanlythau.backend.dto.chamcong;

import com.quanlythau.backend.entity.LoaiCong;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiCongUpdateRequest {
    @NotNull
    private LoaiCong loaiCong;
}
