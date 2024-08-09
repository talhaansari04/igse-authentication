package com.igse.repository;

import com.igse.entity.RegistrationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationStatusRepo extends JpaRepository<RegistrationStatusEntity, Long> {
    @Query(value = """
            SELECT * FROM `igse_registration_status` rs WHERE
            rs.isWalletCreated = :wallet OR rs.isVoucherRedeemed = :voucher OR rs.isMeterDetailSave = :meter
            """, nativeQuery = true)
    List<RegistrationStatusEntity> findRegistrationStatus(String wallet, String voucher, String meter);
}
