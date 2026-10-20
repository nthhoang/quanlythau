package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.VatTu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VatTuRepository extends JpaRepository<VatTu, Long> {
    List<VatTu> findByCongTrinhId(Long congTrinhId);
}
