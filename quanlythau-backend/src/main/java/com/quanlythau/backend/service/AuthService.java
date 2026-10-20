package com.quanlythau.backend.service;

import com.quanlythau.backend.dto.auth.AuthResponse;
import com.quanlythau.backend.dto.auth.LoginRequest;
import com.quanlythau.backend.dto.auth.RegisterRequest;
import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.exception.BadRequestException;
import com.quanlythau.backend.repository.NguoiDungRepository;
import com.quanlythau.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (nguoiDungRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new BadRequestException("Ten dang nhap da ton tai");
        }

        NguoiDung nguoiDung = NguoiDung.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .soDienThoai(request.getSoDienThoai())
                .email(request.getEmail())
                .vaiTro(request.getVaiTro())
                .chuyenMon(request.getChuyenMon())
                .donGiaCong(request.getDonGiaCong())
                .active(true)
                .build();

        nguoiDungRepository.save(nguoiDung);

        String token = jwtService.generateToken(nguoiDung);
        return AuthResponse.builder()
                .token(token)
                .id(nguoiDung.getId())
                .hoTen(nguoiDung.getHoTen())
                .vaiTro(nguoiDung.getVaiTro())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getTenDangNhap(), request.getMatKhau())
        );

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(request.getTenDangNhap())
                .orElseThrow(() -> new BadRequestException("Ten dang nhap khong ton tai"));

        String token = jwtService.generateToken(nguoiDung);
        return AuthResponse.builder()
                .token(token)
                .id(nguoiDung.getId())
                .hoTen(nguoiDung.getHoTen())
                .vaiTro(nguoiDung.getVaiTro())
                .build();
    }
}
