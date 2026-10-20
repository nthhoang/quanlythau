package com.quanlythau.backend.dto.auth;

import com.quanlythau.backend.entity.VaiTro;
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
public class AuthResponse {
    private String token;
    private Long id;
    private String hoTen;
    private VaiTro vaiTro;
}
