package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.CaiDatChuThau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaiDatChuThauRepository extends JpaRepository<CaiDatChuThau, Long> {
}
