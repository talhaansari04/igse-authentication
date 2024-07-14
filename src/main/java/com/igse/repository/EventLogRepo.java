package com.igse.repository;

import com.igse.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepo extends JpaRepository<EventLog,Integer> {
}
