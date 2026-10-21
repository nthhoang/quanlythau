package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.CongTrinh;
import com.quanlythau.backend.entity.TrangThaiCongTrinh;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CongTrinhRepository extends JpaRepository<CongTrinh, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CongTrinh c where c.id = :id")
    Optional<CongTrinh> findByIdForUpdate(@Param("id") Long id);

    List<CongTrinh> findByTrangThai(TrangThaiCongTrinh trangThai);
    Optional<CongTrinh> findByShareToken(String shareToken);
}
