package com.igse.repository;

import com.igse.entity.UserMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface UserMasterRepository extends JpaRepository<UserMaster, String> {

    List<UserMaster> findAllByRole(String role, Pageable pageable);
    Optional<UserMaster> findAllByUserName(String userName);
}
