package com.quanlythau.backend.dto.caidat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaiDatResponse {
    private Integer phanTramTangCa;
    private Integer nguongCanhBaoChiPhi;
}
