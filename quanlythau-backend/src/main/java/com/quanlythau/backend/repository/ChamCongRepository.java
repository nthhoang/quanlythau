package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.ChamCong;
import com.quanlythau.backend.entity.TrangThaiXacThuc;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
