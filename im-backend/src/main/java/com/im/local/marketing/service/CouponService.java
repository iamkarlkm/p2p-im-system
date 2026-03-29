package com.im.local.marketing.service;

import com.im.local.marketing.dto.*;
import com.im.common.result.Result;

import java.util.List;

/**
 * 优惠券服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
public interface CouponService {
    
    /**
     * 创建优惠券模板
     * 
     * @param dto 创建模板请求
     * @return 创建的模板ID
     */
    Result<String> createTemplate(CreateCouponTemplateDTO dto);
    
    /**
     * 发放优惠券
     * 
     * @param dto 发放请求
     * @return 发放结果
     */
    Result<Void> issueCoupons(IssueCouponDTO dto);
    
    /**
     * 领取优惠券
     * 
     * @param userId 用户ID
     * @param dto 领取请求
     * @return 用户优惠券ID
     */
    Result<String> claimCoupon(String userId, ClaimCouponDTO dto);
    
    /**
     * 获取优惠券详情
     * 
     * @param couponId 优惠券ID
     * @param userId 当前用户ID
     * @return 优惠券详情
     */
    Result<CouponDetailDTO> getCouponDetail(String couponId, String userId);
    
    /**
     * 获取附近优惠券
     * 
     * @param dto 查询条件
     * @param userId 用户ID
     * @return 优惠券列表
     */
    Result<List<CouponDetailDTO>> getNearbyCoupons(NearbyCouponDTO dto, String userId);
    
    /**
     * 获取用户优惠券列表
     * 
     * @param userId 用户ID
     * @param status 状态筛选（UNUSED/USED/EXPIRED）
     * @param page 页码
     * @param size 每页大小
     * @return 优惠券列表
     */
    Result<List<UserCouponDTO>> getMyCoupons(String userId, String status, Integer page, Integer size);
    
    /**
     * 使用优惠券
     * 
     * @param userId 用户ID
     * @param userCouponId 用户优惠券ID
     * @param orderId 订单ID
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    Result<BigDecimal> useCoupon(String userId, String userCouponId, String orderId, BigDecimal orderAmount);
    
    /**
     * 获取商户优惠券
     * 
     * @param merchantId 商户ID
     * @param userId 用户ID
     * @return 优惠券列表
     */
    Result<List<CouponDetailDTO>> getMerchantCoupons(String merchantId, String userId);
    
    /**
     * 转赠优惠券
     * 
     * @param userId 当前用户ID
     * @param userCouponId 用户优惠券ID
     * @param targetUserId 目标用户ID
     * @param message 留言
     * @return 转赠结果
     */
    Result<Void> transferCoupon(String userId, String userCouponId, String targetUserId, String message);
    
    /**
     * 分享优惠券
     * 
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 分享链接/码
     */
    Result<String> shareCoupon(String userId, String couponId);
    
    /**
     * 核销优惠券
     * 
     * @param merchantId 商户ID
     * @param userCouponId 用户优惠券ID
     * @param verifyCode 核销码
     * @return 核销结果
     */
    Result<Void> verifyCoupon(String merchantId, String userCouponId, String verifyCode);
}
