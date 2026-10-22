package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.VatTu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VatTuRepository extends JpaRepository<VatTu, Long> {
    List<VatTu> findByCongTrinhId(Long congTrinhId);

    @Query("""
            select v from VatTu v
            where v.id = :id and v.congTrinh.nguoiTaoId = :chuThauId
            """)
    java.util.Optional<VatTu> findByIdAndContractorId(
            @Param("id") Long id,
            @Param("chuThauId") Long chuThauId
    );
}
