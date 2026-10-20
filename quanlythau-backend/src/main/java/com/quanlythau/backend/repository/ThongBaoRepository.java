package com.quanlythau.backend.repository;

import com.quanlythau.backend.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    List<ThongBao> findByNguoiNhanIdOrderByCreatedAtDesc(Long nguoiNhanId);
    long countByNguoiNhanIdAndDaDocFalse(Long nguoiNhanId);
}
