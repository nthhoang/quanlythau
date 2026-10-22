package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.ChiPhiKhac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiPhiKhacRepository extends JpaRepository<ChiPhiKhac, Long> {
    List<ChiPhiKhac> findByCongTrinhId(Long congTrinhId);

    @Query("""
            select c from ChiPhiKhac c
            where c.id = :id and c.congTrinh.nguoiTaoId = :chuThauId
            """)
    java.util.Optional<ChiPhiKhac> findByIdAndContractorId(
            @Param("id") Long id,
            @Param("chuThauId") Long chuThauId
    );
}
