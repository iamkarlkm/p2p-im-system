package com.im.local.coupon.repository;

import com.im.local.coupon.entity.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 优惠券Repository
 */
@Repository
public interface CouponRepository extends MongoRepository<Coupon, String> {
    
    /**
     * 根据商户ID查询
     */
    List<Coupon> findByMerchantIdOrderByPriorityDesc(String merchantId);
    
    /**
     * 根据商户ID和状态查询
     */
    List<Coupon> findByMerchantIdAndStatusOrderByPriorityDesc(String merchantId, String status);
    
    /**
     * 根据状态查询
     */
    List<Coupon> findByStatusOrderByPriorityDesc(String status);
    
    /**
     * 查询生效中的优惠券
     */
    @Query("{'status': 'ACTIVE', 'validityStart': {$lte: ?0}, 'validityEnd': {$gte: ?0}}")
    List<Coupon> findActiveCoupons(LocalDateTime now);
    
    /**
     * 根据模板ID查询
     */
    List<Coupon> findByTemplateId(String templateId);
    
    /**
     * 查询附近优惠券
     */
    @Query("{'status': 'ACTIVE', 'location.coordinates': {$near: {$geometry: {type: 'Point', coordinates: [?0, ?1]}, $maxDistance: ?2}}}")
    List<Coupon> findNearbyCoupons(double longitude, double latitude, int maxDistance);
    
    /**
     * 查询新用户专享
     */
    List<Coupon> findByNewUserOnlyTrueAndStatus(String status);
    
    /**
     * 查询会员专享
     */
    List<Coupon> findByMemberOnlyTrueAndStatus(String status);
    
    /**
     * 查询即将过期的优惠券
     */
    @Query("{'status': 'ACTIVE', 'validityEnd': {$lte: ?0, $gte: ?1}}")
    List<Coupon> findExpiringSoon(LocalDateTime expireBefore, LocalDateTime now);
}
