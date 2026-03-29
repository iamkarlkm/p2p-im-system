package com.im.local.modules.coupon.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.local.modules.coupon.dto.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface CouponService {

    /**
     * 获取优惠券详情
     */
    CouponDTO getCouponById(Long id);

    /**
     * 分页查询附近优惠券
     */
    IPage<CouponDTO> getNearbyCoupons(Page<CouponDTO> page, Double lat, Double lng, Double radius);

    /**
     * 获取商户优惠券列表
     */
    List<CouponDTO> getMerchantCoupons(Long merchantId);

    /**
     * 领取优惠券
     */
    UserCouponDTO receiveCoupon(Long userId, ReceiveCouponRequestDTO request);

    /**
     * 获取用户优惠券列表
     */
    List<UserCouponDTO> getUserCoupons(Long userId);

    /**
     * 获取用户可用优惠券
     */
    IPage<UserCouponDTO> getUsableCoupons(Page<UserCouponDTO> page, Long userId);

    /**
     * 使用优惠券
     */
    BigDecimal useCoupon(Long userId, UseCouponRequestDTO request);

    /**
     * 计算订单可用优惠券
     */
    List<UserCouponDTO> calculateAvailableCoupons(Long userId, BigDecimal orderAmount, Long merchantId);

    /**
     * 检查优惠券是否可用
     */
    boolean checkCouponUsable(Long userCouponId, Long userId, BigDecimal orderAmount);

    /**
     * 获取即将过期的优惠券
     */
    List<UserCouponDTO> getExpiringSoonCoupons(Long userId);

    /**
     * 批量标记过期优惠券
     */
    int markExpiredCoupons(Long userId);
}
