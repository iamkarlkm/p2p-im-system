package com.im.local.coupon.service;

import com.im.local.coupon.dto.CouponDTO;
import com.im.local.coupon.entity.Coupon;
import com.im.local.coupon.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {
    
    /**
     * 创建优惠券
     */
    CouponDTO createCoupon(CouponDTO dto);
    
    /**
     * 根据ID查询
     */
    CouponDTO getCouponById(String id);
    
    /**
     * 查询商户优惠券列表
     */
    List<CouponDTO> getMerchantCoupons(String merchantId, String status);
    
    /**
     * 查询附近优惠券
     */
    List<CouponDTO> getNearbyCoupons(double longitude, double latitude, int radius, String userId);
    
    /**
     * 领取优惠券
     */
    UserCoupon claimCoupon(String couponId, String userId);
    
    /**
     * 使用优惠券
     */
    UserCoupon useCoupon(String userCouponId, String orderId, BigDecimal orderAmount);
    
    /**
     * 查询用户优惠券列表
     */
    List<UserCoupon> getUserCoupons(String userId, String status);
    
    /**
     * 获取用户可用的优惠券
     */
    List<UserCoupon> getUserAvailableCoupons(String userId, String merchantId, BigDecimal orderAmount);
    
    /**
     * 计算优惠金额
     */
    BigDecimal calculateDiscount(String userCouponId, BigDecimal orderAmount);
    
    /**
     * 更新优惠券状态
     */
    void updateCouponStatus(String id, String status);
    
    /**
     * 删除优惠券
     */
    void deleteCoupon(String id);
    
    /**
     * 分页查询优惠券
     */
    List<CouponDTO> getCouponList(int page, int size, String status, String couponType);
}
