package com.im.local.delivery.repository;

import com.im.local.delivery.entity.DeliveryOrder;
import com.im.local.delivery.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配送订单Repository
 */
@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
    
    Page<DeliveryOrder> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    Page<DeliveryOrder> findByUserIdAndStatusInOrderByCreateTimeDesc(
        Long userId, List<DeliveryStatus> statuses, Pageable pageable);
    
    Page<DeliveryOrder> findByRiderIdOrderByCreateTimeDesc(Long riderId, Pageable pageable);
    
    Page<DeliveryOrder> findByRiderIdAndStatusInOrderByCreateTimeDesc(
        Long riderId, List<DeliveryStatus> statuses, Pageable pageable);
    
    Page<DeliveryOrder> findByMerchantIdOrderByCreateTimeDesc(Long merchantId, Pageable pageable);
    
    Page<DeliveryOrder> findByMerchantIdAndStatusInOrderByCreateTimeDesc(
        Long merchantId, List<DeliveryStatus> statuses, Pageable pageable);
    
    List<DeliveryOrder> findByRiderIdAndStatusIn(Long riderId, List<DeliveryStatus> statuses);
    
    long countByRiderIdAndStatusIn(Long riderId, List<DeliveryStatus> statuses);
    
    List<DeliveryOrder> findByStatusIn(List<DeliveryStatus> statuses);
}
