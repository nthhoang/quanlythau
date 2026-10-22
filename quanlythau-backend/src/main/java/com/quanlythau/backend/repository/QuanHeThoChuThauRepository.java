package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.QuanHeThoChuThau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface QuanHeThoChuThauRepository extends JpaRepository<QuanHeThoChuThau, Long> {
    List<QuanHeThoChuThau> findByChuThauId(Long chuThauId);
    Optional<QuanHeThoChuThau> findFirstByChuThauIdAndThoIdAndNgayKetThucIsNull(Long chuThauId, Long thoId);
    Optional<QuanHeThoChuThau> findFirstByChuThauIdAndThoIdOrderByNgayBatDauDescIdDesc(Long chuThauId, Long thoId);

    Optional<QuanHeThoChuThau> findFirstByThoIdAndNgayKetThucIsNull(Long thoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select q
            from QuanHeThoChuThau q
            where q.tho.id = :thoId
              and q.ngayKetThuc is null
            """)
    Optional<QuanHeThoChuThau> findActiveByWorkerIdForUpdate(@Param("thoId") Long thoId);
}
