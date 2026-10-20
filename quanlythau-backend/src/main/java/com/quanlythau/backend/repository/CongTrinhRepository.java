package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CongTrinhRepository extends JpaRepository<CongTrinh, Long> {
    List<CongTrinh> findByTrangThai(TrangThaiCongTrinh trangThai);
    Optional<CongTrinh> findByShareToken(String shareToken);
}
