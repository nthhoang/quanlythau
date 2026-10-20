package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.NhatKyThiCong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NhatKyThiCongRepository extends JpaRepository<NhatKyThiCong, Long> {
    List<NhatKyThiCong> findByCongTrinhIdOrderByNgayDesc(Long congTrinhId);
}
