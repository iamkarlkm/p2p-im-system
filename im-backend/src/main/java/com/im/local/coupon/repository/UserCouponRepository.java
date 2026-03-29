package com.im.local.coupon.repository;

import com.im.local.coupon.entity.UserCoupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户优惠券Repository
 */
@Repository
public interface UserCouponRepository extends MongoRepository<UserCoupon, String> {
    
    /**
     * 根据用户ID查询
     */
    List<UserCoupon> findByUserIdOrderByExpireTimeAsc(String userId);
    
    /**
     * 根据用户ID和状态查询
     */
    List<UserCoupon> findByUserIdAndStatusOrderByExpireTimeAsc(String userId, String status);
    
    /**
     * 根据用户ID和优惠券ID查询
     */
    List<UserCoupon> findByUserIdAndCouponId(String userId, String couponId);
    
    /**
     * 根据状态查询
     */
    List<UserCoupon> findByStatus(String status);
    
    /**
     * 统计用户某状态优惠券数量
     */
    long countByUserIdAndStatus(String userId, String status);
}
