package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.NguoiDung;
import com.quanlythau.backend.entity.VaiTro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NguoiDung n where n.id = :id")
    Optional<NguoiDung> findByIdForUpdate(@Param("id") Long id);

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
    List<NguoiDung> findByChuThauIdAndVaiTroOrderByHoTen(Long chuThauId, VaiTro vaiTro);
    List<NguoiDung> findByChuThauIdAndVaiTroAndActiveTrueOrderByHoTen(Long chuThauId, VaiTro vaiTro);
    boolean existsByChuThauIdAndIdAndVaiTro(Long chuThauId, Long id, VaiTro vaiTro);
    List<NguoiDung> findByVaiTro(VaiTro vaiTro);
    List<NguoiDung> findByVaiTroAndActiveTrue(VaiTro vaiTro);

    @Query(value = """
            select distinct legacy.worker_id, legacy.contractor_id
            from (
                select pc.nguoi_dung_id as worker_id, ct.nguoi_tao_id as contractor_id
                from phan_cong_cong_trinh pc
                join cong_trinh ct on ct.id = pc.cong_trinh_id
                union
                select q.tho_id as worker_id, q.chu_thau_id as contractor_id
                from quan_he_tho_chu_thau q
            ) legacy
            join nguoi_dung n on n.id = legacy.worker_id
            where n.vai_tro = 'THO' and n.chu_thau_id is null and legacy.contractor_id is not null
            """, nativeQuery = true)
    List<Object[]> findLegacyWorkerContractorPairs();

    @Modifying
    @Query("update NguoiDung n set n.chuThauId = :chuThauId where n.id = :workerId and n.chuThauId is null")
    int assignLegacyWorkerToContractor(@Param("workerId") Long workerId, @Param("chuThauId") Long chuThauId);

    @Modifying
    @Query("update NguoiDung n set n.active = false where n.id = :workerId and n.chuThauId is null")
    int disableAmbiguousLegacyWorker(@Param("workerId") Long workerId);

    @Query("""
            select distinct p.nguoiDung
            from PhanCongCongTrinh p
            where p.congTrinh.nguoiTaoId = :chuThauId
              and p.nguoiDung.vaiTro = :vaiTro
            order by p.nguoiDung.hoTen
            """)
    List<NguoiDung> findWorkersByContractorId(
            @Param("chuThauId") Long chuThauId,
            @Param("vaiTro") VaiTro vaiTro
    );

    @Query("""
            select count(p) > 0
            from PhanCongCongTrinh p
            where p.congTrinh.nguoiTaoId = :chuThauId
              and p.nguoiDung.id = :thoId
              and p.nguoiDung.vaiTro = :vaiTro
            """)
    boolean existsWorkerInContractorTeam(
            @Param("chuThauId") Long chuThauId,
            @Param("thoId") Long thoId,
            @Param("vaiTro") VaiTro vaiTro
    );
}
