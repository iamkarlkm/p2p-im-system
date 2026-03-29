package com.im.local.marketing.controller;

import com.im.local.marketing.dto.*;
import com.im.local.marketing.service.CouponService;
import com.im.common.result.Result;
import com.im.common.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券管理控制器
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Validated
@Api(tags = "优惠券管理", description = "优惠券模板创建、发放、领取、使用等接口")
public class CouponController {
    
    private final CouponService couponService;
    
    /**
     * 创建优惠券模板
     */
    @PostMapping("/template")
    @ApiOperation(value = "创建优惠券模板", notes = "商户创建新的优惠券模板")
    public Result<String> createTemplate(
            @Valid @RequestBody CreateCouponTemplateDTO dto) {
        return couponService.createTemplate(dto);
    }
    
    /**
     * 发放优惠券
     */
    @PostMapping("/issue")
    @ApiOperation(value = "发放优惠券", notes = "向指定用户或用户群体发放优惠券")
    public Result<Void> issueCoupons(
            @Valid @RequestBody IssueCouponDTO dto) {
        return couponService.issueCoupons(dto);
    }
    
    /**
     * 领取优惠券
     */
    @PostMapping("/claim")
    @ApiOperation(value = "领取优惠券", notes = "用户领取优惠券")
    public Result<String> claimCoupon(
            @Valid @RequestBody ClaimCouponDTO dto) {
        String userId = UserContext.getCurrentUserId();
        return couponService.claimCoupon(userId, dto);
    }
    
    /**
     * 获取优惠券详情
     */
    @GetMapping("/{couponId}")
    @ApiOperation(value = "获取优惠券详情", notes = "获取指定优惠券的详细信息")
    public Result<CouponDetailDTO> getCouponDetail(
            @ApiParam("优惠券ID") @PathVariable String couponId) {
        String userId = UserContext.getCurrentUserId();
        return couponService.getCouponDetail(couponId, userId);
    }
    
    /**
     * 获取附近优惠券
     */
    @GetMapping("/nearby")
    @ApiOperation(value = "获取附近优惠券", notes = "基于LBS获取用户附近的优惠券")
    public Result<List<CouponDetailDTO>> getNearbyCoupons(
            @ApiParam("经度") @RequestParam Double lng,
            @ApiParam("纬度") @RequestParam Double lat,
            @ApiParam("搜索半径(米)") @RequestParam(required = false, defaultValue = "5000") Integer radius,
            @ApiParam("城市代码") @RequestParam(required = false) String cityCode,
            @ApiParam("分类") @RequestParam(required = false) String category,
            @ApiParam("排序方式") @RequestParam(required = false, defaultValue = "DISTANCE") String sortBy,
            @ApiParam("页码") @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam("每页大小") @RequestParam(required = false, defaultValue = "20") Integer size) {
        
        String userId = UserContext.getCurrentUserId();
        NearbyCouponDTO dto = NearbyCouponDTO.builder()
                .lng(lng)
                .lat(lat)
                .radius(radius)
                .cityCode(cityCode)
                .category(category)
                .sortBy(sortBy)
                .page(page)
                .size(size)
                .build();
        return couponService.getNearbyCoupons(dto, userId);
    }
    
    /**
     * 获取我的优惠券
     */
    @GetMapping("/my")
    @ApiOperation(value = "获取我的优惠券", notes = "获取当前用户的优惠券列表")
    public Result<List<UserCouponDTO>> getMyCoupons(
            @ApiParam("状态(UNUSED/USED/EXPIRED)") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @ApiParam("每页大小") @RequestParam(required = false, defaultValue = "20") @Min(1) Integer size) {
        
        String userId = UserContext.getCurrentUserId();
        return couponService.getMyCoupons(userId, status, page, size);
    }
    
    /**
     * 使用优惠券
     */
    @PostMapping("/use")
    @ApiOperation(value = "使用优惠券", notes = "在下单时使用优惠券")
    public Result<BigDecimal> useCoupon(
            @ApiParam("用户优惠券ID") @RequestParam String userCouponId,
            @ApiParam("订单ID") @RequestParam String orderId,
            @ApiParam("订单金额") @RequestParam BigDecimal orderAmount) {
        
        String userId = UserContext.getCurrentUserId();
        return couponService.useCoupon(userId, userCouponId, orderId, orderAmount);
    }
    
    /**
     * 获取商户优惠券
     */
    @GetMapping("/merchant/{merchantId}")
    @ApiOperation(value = "获取商户优惠券", notes = "获取指定商户的所有优惠券")
    public Result<List<CouponDetailDTO>> getMerchantCoupons(
            @ApiParam("商户ID") @PathVariable String merchantId) {
        String userId = UserContext.getCurrentUserId();
        return couponService.getMerchantCoupons(merchantId, userId);
    }
    
    /**
     * 转赠优惠券
     */
    @PostMapping("/transfer")
    @ApiOperation(value = "转赠优惠券", notes = "将优惠券转赠给好友")
    public Result<Void> transferCoupon(
            @ApiParam("用户优惠券ID") @RequestParam String userCouponId,
            @ApiParam("目标用户ID") @RequestParam String targetUserId,
            @ApiParam("留言") @RequestParam(required = false) String message) {
        
        String userId = UserContext.getCurrentUserId();
        return couponService.transferCoupon(userId, userCouponId, targetUserId, message);
    }
    
    /**
     * 分享优惠券
     */
    @PostMapping("/{couponId}/share")
    @ApiOperation(value = "分享优惠券", notes = "生成优惠券分享链接")
    public Result<String> shareCoupon(
            @ApiParam("优惠券ID") @PathVariable String couponId) {
        String userId = UserContext.getCurrentUserId();
        return couponService.shareCoupon(userId, couponId);
    }
    
    /**
     * 核销优惠券
     */
    @PostMapping("/verify")
    @ApiOperation(value = "核销优惠券", notes = "商户核销用户优惠券")
    public Result<Void> verifyCoupon(
            @ApiParam("用户优惠券ID") @RequestParam String userCouponId,
            @ApiParam("核销码") @RequestParam String verifyCode) {
        
        String merchantId = UserContext.getCurrentMerchantId();
        return couponService.verifyCoupon(merchantId, userCouponId, verifyCode);
    }
}
