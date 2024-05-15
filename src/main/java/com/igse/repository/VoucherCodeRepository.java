package com.igse.repository;

import com.igse.entity.VoucherCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherCodeRepository extends JpaRepository<VoucherCodeEntity, String> {
    long countByStatus(String status);
}
