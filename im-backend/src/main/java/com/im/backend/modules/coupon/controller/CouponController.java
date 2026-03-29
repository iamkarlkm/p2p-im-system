package com.im.backend.modules.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.coupon.dto.*;
import com.im.backend.modules.coupon.service.CouponService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券管理控制器 - 本地生活精准营销API
 * 
 * API功能:
 * 1. 商家/平台优惠券管理（创建/编辑/审核/暂停）
 * 2. 用户领券服务（主动领取/地理围栏触发）
 * 3. 优惠券核销与退还
 * 4. LBS附近优惠券搜索
 * 5. 数据统计分析
 * 
 * 目标指标:
 * - 领券API响应 < 100ms
 * - 附近搜索API响应 < 200ms
 * - 核销API响应 < 50ms
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Api(tags = "优惠券管理", description = "本地生活精准营销优惠券相关接口")
public class CouponController {

    private final CouponService couponService;

    // ==================== 商家/平台管理接口 ====================
    
    @PostMapping
    @ApiOperation(value = "创建优惠券", notes = "商家或平台创建新的优惠券活动")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, paramType = "header"),
    })
    public Result<Long> createCoupon(@RequestBody @Validated CreateCouponRequest request) {
        log.info("创建优惠券, name={}", request.getName());
        Long currentUserId = getCurrentUserId();
        Long couponId = couponService.createCoupon(request, currentUserId);
        return Result.success(couponId);
    }
    
    @PutMapping("/{couponId}")
    @ApiOperation(value = "更新优惠券", notes = "仅草稿状态可修改")
    public Result<Void> updateCoupon(@PathVariable Long couponId, 
                                      @RequestBody @Validated UpdateCouponRequest request) {
        Long currentUserId = getCurrentUserId();
        couponService.updateCoupon(couponId, request, currentUserId);
        return Result.success();
    }
    
    @DeleteMapping("/{couponId}")
    @ApiOperation(value = "删除优惠券", notes = "仅未领取的优惠券可删除")
    public Result<Void> deleteCoupon(@PathVariable Long couponId) {
        Long currentUserId = getCurrentUserId();
        couponService.deleteCoupon(couponId, currentUserId);
        return Result.success();
    }
    
    @GetMapping("/{couponId}")
    @ApiOperation(value = "获取优惠券详情", notes = "包含当前用户领取状态")
    public Result<CouponDetailResponse> getCouponDetail(@PathVariable Long couponId) {
        Long currentUserId = getCurrentUserId();
        CouponDetailResponse detail = couponService.getCouponDetail(couponId, currentUserId);
        return Result.success(detail);
    }
    
    @GetMapping
    @ApiOperation(value = "查询优惠券列表", notes = "支持多种筛选条件")
    public Result<Page<CouponListResponse>> listCoupons(CouponQueryRequest query) {
        Page<CouponListResponse> page = couponService.listCoupons(query);
        return Result.success(page);
    }
    
    @GetMapping("/merchant/{merchantId}")
    @ApiOperation(value = "获取商户优惠券列表", notes = "查询指定商户的所有优惠券")
    public Result<List<CouponListResponse>> listMerchantCoupons(@PathVariable Long merchantId,
                                                                 @RequestParam(required = false) Integer status) {
        List<CouponListResponse> list = couponService.listMerchantCoupons(merchantId, status);
        return Result.success(list);
    }
    
    @PostMapping("/{couponId}/audit")
    @ApiOperation(value = "审核优惠券", notes = "平台审核商家提交的优惠券")
    public Result<Void> auditCoupon(@PathVariable Long couponId,
                                     @RequestParam Boolean approved,
                                     @RequestParam(required = false) String remark) {
        Long currentUserId = getCurrentUserId();
        couponService.auditCoupon(couponId, approved, remark, currentUserId);
        return Result.success();
    }
    
    @PostMapping("/{couponId}/pause")
    @ApiOperation(value = "暂停优惠券", notes = "暂停发放优惠券")
    public Result<Void> pauseCoupon(@PathVariable Long couponId) {
        Long currentUserId = getCurrentUserId();
        couponService.pauseCoupon(couponId, currentUserId);
        return Result.success();
    }
    
    @PostMapping("/{couponId}/resume")
    @ApiOperation(value = "恢复优惠券", notes = "恢复已暂停的优惠券")
    public Result<Void> resumeCoupon(@PathVariable Long couponId) {
        Long currentUserId = getCurrentUserId();
        couponService.resumeCoupon(couponId, currentUserId);
        return Result.success();
    }
    
    @PostMapping("/{couponId}/terminate")
    @ApiOperation(value = "提前结束优惠券", notes = "提前结束优惠券活动")
    public Result<Void> terminateCoupon(@PathVariable Long couponId) {
        Long currentUserId = getCurrentUserId();
        couponService.terminateCoupon(couponId, currentUserId);
        return Result.success();
    }
    
    // ==================== 用户领券接口 ====================
    
    @PostMapping("/{couponId}/receive")
    @ApiOperation(value = "领取优惠券", notes = "用户主动领取优惠券")
    public Result<Long> receiveCoupon(@PathVariable Long couponId,
                                       @RequestParam(required = false) BigDecimal longitude,
                                       @RequestParam(required = false) BigDecimal latitude) {
        Long currentUserId = getCurrentUserId();
        Long userCouponId = couponService.receiveCoupon(couponId, currentUserId, 1, longitude, latitude);
        return Result.success(userCouponId);
    }
    
    @PostMapping("/batch-receive")
    @ApiOperation(value = "批量领取优惠券", notes = "一键领取多个优惠券")
    public Result<List<Long>> batchReceiveCoupons(@RequestBody List<Long> couponIds) {
        Long currentUserId = getCurrentUserId();
        List<Long> result = couponService.batchReceiveCoupons(couponIds, currentUserId, 1);
        return Result.success(result);
    }
    
    @GetMapping("/{couponId}/check")
    @ApiOperation(value = "检查是否可领取", notes = "检查用户是否可以领取指定优惠券")
    public Result<ReceiveCheckResult> checkCanReceive(@PathVariable Long couponId) {
        Long currentUserId = getCurrentUserId();
        ReceiveCheckResult result = couponService.checkCanReceive(couponId, currentUserId);
        return Result.success(result);
    }
    
    // ==================== 用户优惠券查询接口 ====================
    
    @GetMapping("/user/my")
    @ApiOperation(value = "获取我的优惠券列表", notes = "查询当前用户的所有优惠券")
    public Result<Page<UserCouponResponse>> listMyCoupons(@RequestParam(required = false) Integer status,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = getCurrentUserId();
        Page<UserCouponResponse> result = couponService.listUserCoupons(currentUserId, status, page, size);
        return Result.success(result);
    }
    
    @GetMapping("/user/available")
    @ApiOperation(value = "获取可用优惠券", notes = "订单结算时获取可用优惠券列表")
    public Result<List<UserCouponResponse>> listAvailableCoupons(@RequestParam Long merchantId,
                                                                  @RequestParam BigDecimal orderAmount,
                                                                  @RequestParam(required = false) List<Long> categoryIds) {
        Long currentUserId = getCurrentUserId();
        List<UserCouponResponse> list = couponService.listAvailableCoupons(currentUserId, merchantId, orderAmount, categoryIds);
        return Result.success(list);
    }
    
    @GetMapping("/user/my/{userCouponId}")
    @ApiOperation(value = "获取我的优惠券详情", notes = "查询用户优惠券详情")
    public Result<UserCouponDetailResponse> getMyCouponDetail(@PathVariable Long userCouponId) {
        Long currentUserId = getCurrentUserId();
        UserCouponDetailResponse detail = couponService.getUserCouponDetail(userCouponId, currentUserId);
        return Result.success(detail);
    }
    
    @GetMapping("/user/statistics")
    @ApiOperation(value = "获取用户优惠券统计", notes = "统计用户优惠券数量")
    public Result<UserCouponStatistics> getUserCouponStatistics() {
        Long currentUserId = getCurrentUserId();
        UserCouponStatistics stats = couponService.getUserCouponStatistics(currentUserId);
        return Result.success(stats);
    }
    
    // ==================== LBS附近优惠券接口 ====================
    
    @GetMapping("/nearby")
    @ApiOperation(value = "搜索附近优惠券", notes = "基于地理位置搜索附近可用优惠券")
    public Result<List<NearbyCouponResponse>> searchNearbyCoupons(@RequestParam BigDecimal longitude,
                                                                   @RequestParam BigDecimal latitude,
                                                                   @RequestParam(defaultValue = "5000") Integer radius,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        List<NearbyCouponResponse> list = couponService.searchNearbyCoupons(longitude, latitude, radius, page, size);
        return Result.success(list);
    }
    
    @GetMapping("/nearby/count")
    @ApiOperation(value = "附近优惠券数量", notes = "获取指定位置附近优惠券数量")
    public Result<Long> countNearbyCoupons(@RequestParam BigDecimal longitude,
                                            @RequestParam BigDecimal latitude,
                                            @RequestParam(defaultValue = "5000") Integer radius) {
        Long count = couponService.countNearbyCoupons(longitude, latitude, radius);
        return Result.success(count);
    }
    
    @GetMapping("/poi/{poiId}")
    @ApiOperation(value = "获取POI优惠券", notes = "获取指定POI位置的所有可用优惠券")
    public Result<List<CouponListResponse>> listCouponsByPoi(@PathVariable Long poiId) {
        Long currentUserId = getCurrentUserId();
        List<CouponListResponse> list = couponService.listCouponsByPoi(poiId, currentUserId);
        return Result.success(list);
    }
    
    // ==================== 优惠券核销接口 ====================
    
    @PostMapping("/use/{userCouponId}")
    @ApiOperation(value = "使用优惠券", notes = "订单结算时使用优惠券")
    public Result<BigDecimal> useCoupon(@PathVariable Long userCouponId,
                                         @RequestParam Long orderId,
                                         @RequestParam String orderNo,
                                         @RequestParam BigDecimal orderAmount,
                                         @RequestParam Long merchantId,
                                         @RequestParam(required = false) Long poiId,
                                         @RequestParam(required = false) BigDecimal longitude,
                                         @RequestParam(required = false) BigDecimal latitude) {
        Long currentUserId = getCurrentUserId();
        BigDecimal discountAmount = couponService.useCoupon(userCouponId, currentUserId, orderId, orderNo,
                                                            orderAmount, merchantId, poiId, longitude, latitude);
        return Result.success(discountAmount);
    }
    
    @PostMapping("/recommend-best")
    @ApiOperation(value = "推荐最优优惠券", notes = "为用户订单推荐最优优惠券")
    public Result<BestCouponRecommendation> recommendBestCoupon(@RequestParam Long merchantId,
                                                                 @RequestParam BigDecimal orderAmount,
                                                                 @RequestParam(required = false) List<Long> productIds) {
        Long currentUserId = getCurrentUserId();
        BestCouponRecommendation recommendation = couponService.recommendBestCoupon(currentUserId, merchantId, orderAmount, productIds);
        return Result.success(recommendation);
    }
    
    @PostMapping("/return/{userCouponId}")
    @ApiOperation(value = "退还优惠券", notes = "订单取消/退款时退还优惠券")
    public Result<Boolean> returnCoupon(@PathVariable Long userCouponId, @RequestParam Long orderId) {
        Boolean result = couponService.returnCoupon(userCouponId, orderId);
        return Result.success(result);
    }
    
    // ==================== 转赠接口 ====================
    
    @PostMapping("/transfer/{userCouponId}")
    @ApiOperation(value = "转赠优惠券", notes = "将优惠券转赠给好友")
    public Result<Boolean> transferCoupon(@PathVariable Long userCouponId, @RequestParam Long toUserId) {
        Long currentUserId = getCurrentUserId();
        Boolean result = couponService.transferCoupon(userCouponId, currentUserId, toUserId);
        return Result.success(result);
    }
    
    @PostMapping("/accept-transfer")
    @ApiOperation(value = "接受转赠", notes = "接受好友转赠的优惠券")
    public Result<Long> acceptTransferredCoupon(@RequestParam String transferCode) {
        Long currentUserId = getCurrentUserId();
        Long userCouponId = couponService.acceptTransferredCoupon(transferCode, currentUserId);
        return Result.success(userCouponId);
    }
    
    // ==================== 数据统计接口 ====================
    
    @GetMapping("/{couponId}/statistics")
    @ApiOperation(value = "获取优惠券统计", notes = "获取优惠券使用统计数据")
    public Result<CouponStatisticsResponse> getCouponStatistics(@PathVariable Long couponId) {
        CouponStatisticsResponse stats = couponService.getCouponStatistics(couponId);
        return Result.success(stats);
    }
    
    @GetMapping("/merchant/{merchantId}/statistics")
    @ApiOperation(value = "商户优惠券统计", notes = "获取商户优惠券整体统计数据")
    public Result<MerchantCouponStatistics> getMerchantStatistics(@PathVariable Long merchantId,
                                                                   @RequestParam(required = false) String startDate,
                                                                   @RequestParam(required = false) String endDate) {
        MerchantCouponStatistics stats = couponService.getMerchantStatistics(merchantId, startDate, endDate);
        return Result.success(stats);
    }
    
    @GetMapping("/platform/overview")
    @ApiOperation(value = "平台优惠券概览", notes = "获取平台整体优惠券数据概览")
    public Result<PlatformCouponOverview> getPlatformOverview() {
        PlatformCouponOverview overview = couponService.getPlatformOverview();
        return Result.success(overview);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 获取当前登录用户ID
     * 简化实现，实际应从SecurityContext获取
     */
    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        return 1L;
    }
}
