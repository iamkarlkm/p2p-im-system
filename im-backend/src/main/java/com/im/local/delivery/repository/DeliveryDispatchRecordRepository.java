package com.im.local.delivery.repository;

import com.im.local.delivery.entity.DispatchRecord;
import com.im.local.delivery.enums.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 派单记录Repository
 */
@Repository
public interface DeliveryDispatchRecordRepository extends JpaRepository<DispatchRecord, Long> {
    
    List<DispatchRecord> findByOrderId(Long orderId);
    
    List<DispatchRecord> findByRiderIdAndStatus(Long riderId, DispatchStatus status);
    
    boolean existsByRiderIdAndStatus(Long riderId, DispatchStatus status);
    
    List<DispatchRecord> findByStatusAndCreateTimeBefore(DispatchStatus status, LocalDateTime time);
}
