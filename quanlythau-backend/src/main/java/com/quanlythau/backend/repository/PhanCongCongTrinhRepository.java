package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.PhanCongCongTrinh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhanCongCongTrinhRepository extends JpaRepository<PhanCongCongTrinh, Long> {
    List<PhanCongCongTrinh> findByCongTrinhId(Long congTrinhId);
    List<PhanCongCongTrinh> findByCongTrinhIdAndNgayKetThucIsNull(Long congTrinhId);
    List<PhanCongCongTrinh> findByNguoiDungId(Long nguoiDungId);
    List<PhanCongCongTrinh> findByNguoiDungIdAndNgayKetThucIsNull(Long nguoiDungId);
    boolean existsByCongTrinhIdAndNguoiDungIdAndNgayKetThucIsNull(Long congTrinhId, Long nguoiDungId);
    boolean existsByNguoiDungIdAndNgayKetThucIsNull(Long nguoiDungId);
}
