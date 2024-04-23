package com.igse.repository;

import com.igse.entity.UnitPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyChargesRepo extends JpaRepository<UnitPriceEntity,String> {
}
