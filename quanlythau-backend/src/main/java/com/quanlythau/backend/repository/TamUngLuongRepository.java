package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.TamUngLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TamUngLuongRepository extends JpaRepository<TamUngLuong, Long> {
    List<TamUngLuong> findByNguoiDungIdAndNgayUngBetween(Long nguoiDungId, LocalDate tuNgay, LocalDate denNgay);
    List<TamUngLuong> findByNguoiDungIdOrderByNgayUngDesc(Long nguoiDungId);
    List<TamUngLuong> findByNguoiDungIdAndChuThauIdAndNgayUngBetween(
            Long nguoiDungId, Long chuThauId, LocalDate tuNgay, LocalDate denNgay
    );
    List<TamUngLuong> findByNguoiDungIdAndChuThauIdOrderByNgayUngDesc(Long nguoiDungId, Long chuThauId);
    java.util.Optional<TamUngLuong> findByIdAndChuThauId(Long id, Long chuThauId);
}
