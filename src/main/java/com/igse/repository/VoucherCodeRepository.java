package com.igse.repository;

import com.igse.entity.VoucherCodeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherCodeRepository extends JpaRepository<VoucherCodeEntity, String> {
    long countByStatus(String status);

    @Query(value = "SELECT *FROM `voucher_code` vc WHERE vc.status='NOT_USED' AND vc.voucher_Balance = :amount LIMIT 1", nativeQuery = true)
    Optional<VoucherCodeEntity> getVoucherCodeByAmount(@Param("amount") Double amount);
    List<VoucherCodeEntity> findByStatus(String status, Pageable pageable);
}
