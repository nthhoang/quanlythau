package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.PhanCongCongTrinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhanCongCongTrinhRepository extends JpaRepository<PhanCongCongTrinh, Long> {
    List<PhanCongCongTrinh> findByCongTrinhId(Long congTrinhId);
    List<PhanCongCongTrinh> findByCongTrinhIdAndNgayKetThucIsNull(Long congTrinhId);
    List<PhanCongCongTrinh> findByNguoiDungId(Long nguoiDungId);
    List<PhanCongCongTrinh> findByNguoiDungIdAndNgayKetThucIsNull(Long nguoiDungId);
    boolean existsByCongTrinhIdAndNguoiDungIdAndNgayKetThucIsNull(Long congTrinhId, Long nguoiDungId);
    boolean existsByNguoiDungIdAndNgayKetThucIsNull(Long nguoiDungId);
    boolean existsByCongTrinhIdAndNguoiDungId(Long congTrinhId, Long nguoiDungId);

    @Query("""
            select p
            from PhanCongCongTrinh p
            where p.id = :id
              and p.congTrinh.nguoiTaoId = :chuThauId
            """)
    java.util.Optional<PhanCongCongTrinh> findByIdAndContractorId(
            @Param("id") Long id,
            @Param("chuThauId") Long chuThauId
    );

    @Query("""
            select p
            from PhanCongCongTrinh p
            where p.nguoiDung.id = :thoId
              and p.ngayKetThuc is null
              and p.congTrinh.nguoiTaoId = :chuThauId
            """)
    List<PhanCongCongTrinh> findActiveAssignmentsByContractorAndWorker(
            @Param("chuThauId") Long chuThauId,
            @Param("thoId") Long thoId
    );

    @Query("""
            select p
            from PhanCongCongTrinh p
            where p.congTrinh.nguoiTaoId = :chuThauId
              and p.ngayKetThuc is null
            """)
    List<PhanCongCongTrinh> findActiveAssignmentsByContractorId(@Param("chuThauId") Long chuThauId);

}
