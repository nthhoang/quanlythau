package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
    List<NguoiDung> findByVaiTro(VaiTro vaiTro);
}
