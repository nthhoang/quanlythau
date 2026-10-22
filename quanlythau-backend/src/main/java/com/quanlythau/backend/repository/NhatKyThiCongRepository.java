package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.NhatKyThiCong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NhatKyThiCongRepository extends JpaRepository<NhatKyThiCong, Long> {
    List<NhatKyThiCong> findByCongTrinhIdOrderByNgayDesc(Long congTrinhId);

    @Query("""
            select n from NhatKyThiCong n
            where n.id = :id and n.congTrinh.nguoiTaoId = :chuThauId
            """)
    java.util.Optional<NhatKyThiCong> findByIdAndContractorId(
            @Param("id") Long id,
            @Param("chuThauId") Long chuThauId
    );
}
