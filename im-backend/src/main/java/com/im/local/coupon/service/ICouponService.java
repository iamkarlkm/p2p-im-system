package com.im.local.coupon.service;

import com.im.local.coupon.dto.*;
import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponService {

    /**
     * 创建优惠券
     */
    Long createCoupon(CreateCouponRequest request, Long createBy);

    /**
     * 获取优惠券详情
     */
    CouponDetailResponse getCouponDetail(Long couponId);

    /**
     * 领取优惠券
     */
    UserCouponResponse receiveCoupon(ReceiveCouponRequest request, Long userId);

    /**
     * 获取用户优惠券列表
     */
    List<UserCouponResponse> getUserCoupons(Long userId, Integer status);

    /**
     * 核销优惠券
     */
    UseCouponResult useCoupon(UseCouponRequest request, Long userId);

    /**
     * 查询附近优惠券(LBS)
     */
    List<NearbyCouponResponse> getNearbyCoupons(NearbyCouponRequest request, Long userId);

    /**
     * 校验优惠券是否可用
     */
    UseCouponResult validateCoupon(Long userCouponId, Long userId, Long orderId, java.math.BigDecimal orderAmount);

    /**
     * 获取商户优惠券列表
     */
    List<CouponDetailResponse> getMerchantCoupons(Long merchantId);
}
