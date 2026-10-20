package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.BangLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BangLuongRepository extends JpaRepository<BangLuong, Long> {
    List<BangLuong> findByNguoiDungId(Long nguoiDungId);
    List<BangLuong> findByNguoiDungIdOrderByTuNgayDesc(Long nguoiDungId);
}
