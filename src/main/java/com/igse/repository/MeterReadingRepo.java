package com.igse.repository;

import com.igse.entity.MeterReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterReadingRepo extends JpaRepository<MeterReadingEntity,String> {
    List<MeterReadingEntity> findByCustomerId(String customerID);
    Optional<MeterReadingEntity> findByIdAndCustomerIdAndBillingStatus(Integer id, String customerId, String status);
}
