package com.quanlythau.backend.dto.auth;

import com.quanlythau.backend.entity.ChuyenMon;
import com.quanlythau.backend.entity.VaiTro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    private String tenDangNhap;

    @NotBlank(message = "Mat khau khong duoc de trong")
    private String matKhau;

    @NotBlank(message = "Ho ten khong duoc de trong")
    private String hoTen;

    private String soDienThoai;
    private String email;

    @NotNull(message = "Vai tro khong duoc de trong")
    private VaiTro vaiTro;

    private ChuyenMon chuyenMon;
    private BigDecimal donGiaCong;
}
