package com.igse.repository;

import com.igse.entity.VoucherCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface VoucherRepo extends JpaRepository<VoucherCodeEntity,String> {
}
