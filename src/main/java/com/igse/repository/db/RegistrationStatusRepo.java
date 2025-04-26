package com.igse.repository.db;

import com.igse.entity.RegistrationStatusEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationStatusRepo extends JpaRepository<RegistrationStatusEntity, Long> {
    @Query(value = """
            SELECT * FROM igse_registration_status rs WHERE
            rs.isWalletCreated = :wallet OR rs.isVoucherRedeemed = :voucher OR rs.isMeterDetailSave = :meter
            """, nativeQuery = true)
    List<RegistrationStatusEntity> findRegistrationStatus(String wallet, String voucher, String meter);

    Optional<RegistrationStatusEntity> findByCustomerId(String customerId);

    @Modifying
    @Transactional
    @Query(value = "update igse_registration_status rs set rs.isWalletCreated = :walletStatus where rs.customerId = :customerId", nativeQuery = true)
    void updateWalletStatus(@Param("customerId") String customerId, @Param("walletStatus") String walletStatus);

    @Modifying
    @Transactional
    @Query(value = "update igse_registration_status rs set rs.isMeterDetailSave = :meterDetailStatus where rs.customerId = :customerId", nativeQuery = true)
    void updateMeterDetailStatus(@Param("customerId") String customerId, @Param("meterDetailStatus") String meterDetailStatus);

    @Modifying
    @Transactional
    @Query(value = "update igse_registration_status rs set rs.isVoucherRedeemed = :voucherStatus where rs.customerId = :customerId", nativeQuery = true)
    void updateVoucherRedeemedStatus(@Param("customerId") String customerId, @Param("voucherStatus") String voucherStatus);
}
