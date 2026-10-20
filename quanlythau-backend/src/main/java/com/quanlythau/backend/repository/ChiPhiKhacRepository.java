package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.ChiPhiKhac;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiPhiKhacRepository extends JpaRepository<ChiPhiKhac, Long> {
    List<ChiPhiKhac> findByCongTrinhId(Long congTrinhId);
}
