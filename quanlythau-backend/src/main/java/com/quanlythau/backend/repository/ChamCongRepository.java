package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.ChamCong;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChamCongRepository extends JpaRepository<ChamCong, Long> {
    List<ChamCong> findByNguoiDungIdAndNgayChamCong(Long nguoiDungId, LocalDate ngay);
    Optional<ChamCong> findByNguoiDungIdAndNgayChamCongAndCongTrinhId(Long nguoiDungId, LocalDate ngay, Long congTrinhId);
    List<ChamCong> findByNguoiDungIdAndNgayChamCongBetween(Long nguoiDungId, LocalDate tuNgay, LocalDate denNgay);
    List<ChamCong> findByCongTrinhIdOrderByNgayChamCongDesc(Long congTrinhId);
    List<ChamCong> findByTrangThaiXacThucOrderByNgayChamCongDesc(TrangThaiXacThuc trangThai);
    List<ChamCong> findAllByOrderByNgayChamCongDesc();

    @Query("""
            select c from ChamCong c
            where c.congTrinh.nguoiTaoId = :chuThauId
            order by c.ngayChamCong desc
            """)
    List<ChamCong> findAllByContractorId(@Param("chuThauId") Long chuThauId);

    @Query("""
            select c from ChamCong c
            where c.id = :id and c.congTrinh.nguoiTaoId = :chuThauId
            """)
    Optional<ChamCong> findByIdAndContractorId(
            @Param("id") Long id,
            @Param("chuThauId") Long chuThauId
    );

    @Query("""
            select c from ChamCong c
            where c.nguoiDung.id = :thoId
              and c.congTrinh.nguoiTaoId = :chuThauId
              and c.ngayChamCong between :tuNgay and :denNgay
            """)
    List<ChamCong> findByWorkerAndContractorAndDateRange(
            @Param("thoId") Long thoId,
            @Param("chuThauId") Long chuThauId,
            @Param("tuNgay") LocalDate tuNgay,
            @Param("denNgay") LocalDate denNgay
    );

    @Query("""
            select count(c) > 0 from ChamCong c
            where c.nguoiDung.id = :thoId and c.congTrinh.nguoiTaoId = :chuThauId
            """)
    boolean existsByWorkerAndContractor(
            @Param("thoId") Long thoId,
            @Param("chuThauId") Long chuThauId
    );
}
