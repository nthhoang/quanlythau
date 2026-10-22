package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.BangLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BangLuongRepository extends JpaRepository<BangLuong, Long> {
    List<BangLuong> findByNguoiDungId(Long nguoiDungId);
    List<BangLuong> findByNguoiDungIdOrderByTuNgayDesc(Long nguoiDungId);
    List<BangLuong> findByNguoiDungIdAndChuThauIdOrderByTuNgayDesc(Long nguoiDungId, Long chuThauId);
    List<BangLuong> findByNguoiDungIdAndChuThauId(Long nguoiDungId, Long chuThauId);
    java.util.Optional<BangLuong> findByIdAndChuThauId(Long id, Long chuThauId);
}
