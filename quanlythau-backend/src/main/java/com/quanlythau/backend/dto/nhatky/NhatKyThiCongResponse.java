package com.quanlythau.backend.dto.nhatky;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhatKyThiCongResponse {
    private Long id;
    private Long congTrinhId;
    private LocalDate ngay;
    private String noiDung;
    private List<String> danhSachAnh;
    private LocalDateTime createdAt;
}
