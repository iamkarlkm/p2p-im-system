package com.im.local.delivery.repository;

import com.im.local.delivery.entity.RiderLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 骑手位置Repository
 */
@Repository
public interface RiderLocationRepository extends JpaRepository<RiderLocation, Long> {
    
    Optional<RiderLocation> findTopByRiderIdOrderByUpdateTimeDesc(Long riderId);
    
    List<RiderLocation> findByRiderIdAndUpdateTimeBetweenOrderByUpdateTimeAsc(
        Long riderId, LocalDateTime startTime, LocalDateTime endTime);
    
    List<RiderLocation> findByRiderIdOrderByUpdateTimeDesc(Long riderId);
}
