package com.im.local.coupon.controller;

import com.im.local.coupon.dto.*;
import com.im.local.coupon.service.ICouponService;
import com.im.local.coupon.service.IMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 优惠券与会员控制器
 */
@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final ICouponService couponService;
    private final IMemberService memberService;

    /**
     * 创建优惠券
     */
    @PostMapping("/create")
    public Long createCoupon(@RequestBody @Valid CreateCouponRequest request,
                             @RequestAttribute("userId") Long userId) {
        return couponService.createCoupon(request, userId);
    }

    /**
     * 获取优惠券详情
     */
    @GetMapping("/{couponId}")
    public CouponDetailResponse getCouponDetail(@PathVariable Long couponId) {
        return couponService.getCouponDetail(couponId);
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/receive")
    public UserCouponResponse receiveCoupon(@RequestBody @Valid ReceiveCouponRequest request,
                                            @RequestAttribute("userId") Long userId) {
        return couponService.receiveCoupon(request, userId);
    }

    /**
     * 获取用户优惠券列表
     */
    @GetMapping("/user/list")
    public List<UserCouponResponse> getUserCoupons(@RequestAttribute("userId") Long userId,
                                                   @RequestParam(required = false) Integer status) {
        return couponService.getUserCoupons(userId, status);
    }

    /**
     * 核销优惠券
     */
    @PostMapping("/use")
    public UseCouponResult useCoupon(@RequestBody @Valid UseCouponRequest request,
                                     @RequestAttribute("userId") Long userId) {
        return couponService.useCoupon(request, userId);
    }

    /**
     * 查询附近优惠券
     */
    @PostMapping("/nearby")
    public List<NearbyCouponResponse> getNearbyCoupons(@RequestBody @Valid NearbyCouponRequest request,
                                                       @RequestAttribute(value = "userId", required = false) Long userId) {
        return couponService.getNearbyCoupons(request, userId);
    }

    /**
     * 获取商户优惠券列表
     */
    @GetMapping("/merchant/{merchantId}")
    public List<CouponDetailResponse> getMerchantCoupons(@PathVariable Long merchantId) {
        return couponService.getMerchantCoupons(merchantId);
    }

    /**
     * 获取用户会员信息
     */
    @GetMapping("/member/info")
    public UserMemberResponse getUserMemberInfo(@RequestAttribute("userId") Long userId,
                                                @RequestParam Long merchantId) {
        return memberService.getUserMemberInfo(userId, merchantId);
    }

    /**
     * 加入会员
     */
    @PostMapping("/member/join")
    public Long joinMember(@RequestAttribute("userId") Long userId,
                           @RequestParam Long merchantId) {
        return memberService.joinMember(userId, merchantId);
    }

    /**
     * 每日签到
     */
    @PostMapping("/member/signin")
    public boolean dailySignIn(@RequestAttribute("userId") Long userId,
                               @RequestParam Long merchantId) {
        return memberService.dailySignIn(userId, merchantId);
    }
}
