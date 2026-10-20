package com.quanlythau.backend.dto.thongbao;

import com.quanlythau.backend.entity.LoaiThongBao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoResponse {
    private Long id;
    private String tieuDe;
    private String noiDung;
    private LoaiThongBao loai;
    private Boolean daDoc;
    private LocalDateTime createdAt;
}
