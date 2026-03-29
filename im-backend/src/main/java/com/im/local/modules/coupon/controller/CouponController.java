package com.im.local.modules.coupon.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.common.core.result.Result;
import com.im.local.modules.coupon.dto.*;
import com.im.local.modules.coupon.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券控制器
 * @author IM Development Team
 * @since 2026-03-28
 */
@Api(tags = "优惠券管理")
@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @ApiOperation("获取优惠券详情")
    @GetMapping("/coupons/{id}")
    public Result<CouponDTO> getCouponById(@PathVariable Long id) {
        return Result.success(couponService.getCouponById(id));
    }

    @ApiOperation("获取附近优惠券列表")
    @GetMapping("/coupons/nearby")
    public Result<IPage<CouponDTO>> getNearbyCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5000") Double radius) {
        return Result.success(couponService.getNearbyCoupons(new Page<>(page, size), lat, lng, radius));
    }

    @ApiOperation("获取商户优惠券列表")
    @GetMapping("/merchant/{merchantId}/coupons")
    public Result<List<CouponDTO>> getMerchantCoupons(@PathVariable Long merchantId) {
        return Result.success(couponService.getMerchantCoupons(merchantId));
    }

    @ApiOperation("领取优惠券")
    @PostMapping("/coupons/receive")
    public Result<UserCouponDTO> receiveCoupon(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated ReceiveCouponRequestDTO request) {
        return Result.success(couponService.receiveCoupon(userId, request));
    }

    @ApiOperation("获取我的优惠券列表")
    @GetMapping("/user/coupons")
    public Result<List<UserCouponDTO>> getUserCoupons(@RequestAttribute("userId") Long userId) {
        return Result.success(couponService.getUserCoupons(userId));
    }

    @ApiOperation("获取可用优惠券列表")
    @GetMapping("/user/coupons/usable")
    public Result<IPage<UserCouponDTO>> getUsableCoupons(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(couponService.getUsableCoupons(new Page<>(page, size), userId));
    }

    @ApiOperation("使用优惠券")
    @PostMapping("/coupons/use")
    public Result<BigDecimal> useCoupon(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated UseCouponRequestDTO request) {
        return Result.success(couponService.useCoupon(userId, request));
    }

    @ApiOperation("计算订单可用优惠券")
    @GetMapping("/coupons/available")
    public Result<List<UserCouponDTO>> calculateAvailableCoupons(
            @RequestAttribute("userId") Long userId,
            @RequestParam BigDecimal orderAmount,
            @RequestParam(required = false) Long merchantId) {
        return Result.success(couponService.calculateAvailableCoupons(userId, orderAmount, merchantId));
    }

    @ApiOperation("检查优惠券是否可用")
    @GetMapping("/coupons/{userCouponId}/check")
    public Result<Boolean> checkCouponUsable(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long userCouponId,
            @RequestParam BigDecimal orderAmount) {
        return Result.success(couponService.checkCouponUsable(userCouponId, userId, orderAmount));
    }

    @ApiOperation("获取即将过期的优惠券")
    @GetMapping("/user/coupons/expiring-soon")
    public Result<List<UserCouponDTO>> getExpiringSoonCoupons(@RequestAttribute("userId") Long userId) {
        return Result.success(couponService.getExpiringSoonCoupons(userId));
    }
}
