package com.im.backend.repository;

import com.im.backend.model.DoNotDisturbPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoNotDisturbPeriodRepository extends JpaRepository<DoNotDisturbPeriod, String> {
    
    List<DoNotDisturbPeriod> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<DoNotDisturbPeriod> findByUserIdAndIsEnabledTrue(String userId);
    
    long countByUserIdAndIsEnabledTrue(String userId);
}
