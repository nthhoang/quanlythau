package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.VaiTro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NguoiDung n where n.id = :id")
    Optional<NguoiDung> findByIdForUpdate(@Param("id") Long id);

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
    List<NguoiDung> findByVaiTro(VaiTro vaiTro);
}
