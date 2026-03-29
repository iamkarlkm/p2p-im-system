package com.im.local.delivery.repository;

import com.im.local.delivery.entity.DeliveryRider;
import com.im.local.delivery.enums.RiderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 配送骑手Repository
 */
@Repository
public interface DeliveryRiderRepository extends JpaRepository<DeliveryRider, Long> {
    
    Optional<DeliveryRider> findByUserId(Long userId);
    
    List<DeliveryRider> findByStatus(RiderStatus status);
    
    List<DeliveryRider> findAllByIdInAndStatus(List<Long> ids, RiderStatus status);
    
    List<DeliveryRider> findByRegionCodeAndStatus(String regionCode, RiderStatus status);
    
    boolean existsByUserId(Long userId);
}
