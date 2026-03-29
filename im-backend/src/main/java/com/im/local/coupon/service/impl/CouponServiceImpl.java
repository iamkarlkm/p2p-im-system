package com.im.local.coupon.service.impl;

import com.im.local.coupon.dto.*;
import com.im.local.coupon.entity.Coupon;
import com.im.local.coupon.entity.UserCoupon;
import com.im.local.coupon.enums.CouponStatus;
import com.im.local.coupon.enums.CouponType;
import com.im.local.coupon.enums.UserCouponStatus;
import com.im.local.coupon.repository.CouponMapper;
import com.im.local.coupon.repository.UserCouponMapper;
import com.im.local.coupon.service.ICouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(CreateCouponRequest request, Long createBy) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(request, coupon);
        coupon.setReceivedQuantity(0);
        coupon.setUsedQuantity(0);
        coupon.setStatus(CouponStatus.DRAFT.getCode());
        coupon.setCreateBy(createBy);
        coupon.setDeleted(0);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());

        if (coupon.getLimitPerUser() == null) {
            coupon.setLimitPerUser(1);
        }
        if (coupon.getTotalQuantity() == null) {
            coupon.setTotalQuantity(-1);
        }

        couponMapper.insert(coupon);
        log.info("创建优惠券成功: id={}, name={}", coupon.getId(), coupon.getName());
        return coupon.getId();
    }

    @Override
    public CouponDetailResponse getCouponDetail(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return null;
        }
        return convertToDetailResponse(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCouponResponse receiveCoupon(ReceiveCouponRequest request, Long userId) {
        Coupon coupon = couponMapper.selectById(request.getCouponId());
        if (coupon == null || coupon.getDeleted() == 1) {
            throw new RuntimeException("优惠券不存在");
        }

        if (!CouponStatus.IN_PROGRESS.getCode().equals(coupon.getStatus())) {
            throw new RuntimeException("优惠券不在领取时间内");
        }

        int receivedCount = userCouponMapper.countByUserAndCoupon(userId, request.getCouponId());
        if (receivedCount >= coupon.getLimitPerUser()) {
            throw new RuntimeException("已达到领取上限");
        }

        if (coupon.getTotalQuantity() != -1 && coupon.getReceivedQuantity() >= coupon.getTotalQuantity()) {
            throw new RuntimeException("优惠券已领完");
        }

        if (coupon.getGeofenceEnabled() == 1 && request.getLatitude() != null && request.getLongitude() != null) {
            double distance = calculateDistance(request.getLatitude(), request.getLongitude(),
                    coupon.getFenceLatitude(), coupon.getFenceLongitude());
            if (distance > coupon.getFenceRadius()) {
                throw new RuntimeException("不在优惠券领取范围内");
            }
        }

        int updated = couponMapper.incrementReceived(coupon.getId());
        if (updated == 0) {
            throw new RuntimeException("优惠券已被领完");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(coupon.getId());
        userCoupon.setTemplateId(coupon.getTemplateId());
        userCoupon.setMerchantId(coupon.getMerchantId());
        userCoupon.setCouponCode(generateCouponCode());
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getType());
        userCoupon.setCouponValue(coupon.getValue());
        userCoupon.setMinSpend(coupon.getMinSpend());

        if (coupon.getValidityType() == 1) {
            userCoupon.setValidStartTime(coupon.getValidStartTime());
            userCoupon.setValidEndTime(coupon.getValidEndTime());
        } else {
            userCoupon.setValidStartTime(LocalDateTime.now());
            userCoupon.setValidEndTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
        }

        userCoupon.setReceiveChannel(request.getChannel());
        userCoupon.setReceiveScene(request.getScene());
        userCoupon.setReceiveLongitude(request.getLongitude());
        userCoupon.setReceiveLatitude(request.getLatitude());

        userCouponMapper.insert(userCoupon);
        log.info("用户领取优惠券成功: userId={}, couponId={}", userId, coupon.getId());

        return convertToUserCouponResponse(userCoupon);
    }

    @Override
    public List<UserCouponResponse> getUserCoupons(Long userId, Integer status) {
        List<UserCoupon> coupons;
        if (status == null) {
            coupons = userCouponMapper.selectByUserId(userId);
        } else {
            coupons = userCouponMapper.selectByUserIdAndStatus(userId, status);
        }
        return coupons.stream().map(this::convertToUserCouponResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UseCouponResult useCoupon(UseCouponRequest request, Long userId) {
        UserCoupon userCoupon = userCouponMapper.selectById(request.getUserCouponId());
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            return buildErrorResult("优惠券不存在");
        }

        UseCouponResult result = validateUserCoupon(userCoupon, request);
        if (!result.getUsable()) {
            return result;
        }

        int updated = userCouponMapper.markAsUsed(userCoupon.getId(), request.getOrderId());
        if (updated == 0) {
            return buildErrorResult("优惠券使用失败");
        }

        couponMapper.incrementUsed(userCoupon.getCouponId());
        log.info("优惠券使用成功: userCouponId={}, orderId={}", userCoupon.getId(), request.getOrderId());

        return result;
    }

    @Override
    public List<NearbyCouponResponse> getNearbyCoupons(NearbyCouponRequest request, Long userId) {
        List<Coupon> activeCoupons = couponMapper.selectActiveCoupons();

        return activeCoupons.stream()
                .filter(c -> c.getGeofenceEnabled() == 0 ||
                        calculateDistance(request.getLatitude(), request.getLongitude(),
                                c.getFenceLatitude(), c.getFenceLongitude()) <= c.getFenceRadius())
                .filter(c -> request.getCouponType() == null || c.getType().equals(request.getCouponType()))
                .map(c -> {
                    NearbyCouponResponse resp = new NearbyCouponResponse();
                    resp.setCouponId(c.getId());
                    resp.setMerchantId(c.getMerchantId());
                    resp.setCouponName(c.getName());
                    resp.setCouponType(c.getType());
                    resp.setCouponValue(c.getValue());
                    resp.setMinSpend(c.getMinSpend());
                    resp.setValidStartTime(c.getValidStartTime());
                    resp.setValidEndTime(c.getValidEndTime());
                    resp.setRemainingQuantity(c.getTotalQuantity() - c.getReceivedQuantity());

                    if (c.getGeofenceEnabled() == 1) {
                        resp.setDistance(calculateDistance(request.getLatitude(), request.getLongitude(),
                                c.getFenceLatitude(), c.getFenceLongitude()));
                    }

                    if (userId != null) {
                        int count = userCouponMapper.countByUserAndCoupon(userId, c.getId());
                        resp.setReceived(count > 0);
                    }
                    return resp;
                })
                .sorted((a, b) -> {
                    if (request.getSortBy() == 1) {
                        return Double.compare(a.getDistance() != null ? a.getDistance() : 0,
                                b.getDistance() != null ? b.getDistance() : 0);
                    }
                    return b.getCouponValue().compareTo(a.getCouponValue());
                })
                .collect(Collectors.toList());
    }

    @Override
    public UseCouponResult validateCoupon(Long userCouponId, Long userId, Long orderId, BigDecimal orderAmount) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            return buildErrorResult("优惠券不存在");
        }
        return validateUserCoupon(userCoupon, new UseCouponRequest());
    }

    @Override
    public List<CouponDetailResponse> getMerchantCoupons(Long merchantId) {
        List<Coupon> coupons = couponMapper.selectByMerchantId(merchantId);
        return coupons.stream().map(this::convertToDetailResponse).collect(Collectors.toList());
    }

    private UseCouponResult validateUserCoupon(UserCoupon userCoupon, UseCouponRequest request) {
        if (!UserCouponStatus.UNUSED.getCode().equals(userCoupon.getStatus())) {
            return buildErrorResult("优惠券已使用或已过期");
        }

        if (LocalDateTime.now().isAfter(userCoupon.getValidEndTime())) {
            return buildErrorResult("优惠券已过期");
        }

        if (userCoupon.getMinSpend() != null &&
                userCoupon.getMinSpend().compareTo(BigDecimal.ZERO) > 0 &&
                request.getOrderAmount() != null &&
                request.getOrderAmount().compareTo(userCoupon.getMinSpend()) < 0) {
            return buildErrorResult("订单金额不满足使用门槛");
        }

        BigDecimal discountAmount = calculateDiscount(userCoupon, request.getOrderAmount());
        UseCouponResult result = new UseCouponResult();
        result.setUsable(true);
        result.setDiscountAmount(discountAmount);
        result.setFinalAmount(request.getOrderAmount().subtract(discountAmount));
        return result;
    }

    private BigDecimal calculateDiscount(UserCoupon coupon, BigDecimal orderAmount) {
        if (orderAmount == null) return BigDecimal.ZERO;

        if (coupon.getCouponType() == 1) {
            return coupon.getCouponValue();
        } else if (coupon.getCouponType() == 2) {
            BigDecimal discount = orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getCouponValue()));
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                return coupon.getMaxDiscount();
            }
            return discount;
        } else if (coupon.getCouponType() == 3) {
            return coupon.getCouponValue();
        }
        return BigDecimal.ZERO;
    }

    private String generateCouponCode() {
        return "CP" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private double calculateDistance(double lat1, double lon1, Double lat2, Double lon2) {
        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private CouponDetailResponse convertToDetailResponse(Coupon coupon) {
        CouponDetailResponse resp = new CouponDetailResponse();
        BeanUtils.copyProperties(coupon, resp);
        resp.setTypeName(CouponType.getByCode(coupon.getType()) != null ?
                CouponType.getByCode(coupon.getType()).getName() : "");
        resp.setStatusName(CouponStatus.getByCode(coupon.getStatus()) != null ?
                CouponStatus.getByCode(coupon.getStatus()).getName() : "");
        resp.setRemainingQuantity(coupon.getTotalQuantity() == -1 ? -1 :
                coupon.getTotalQuantity() - coupon.getReceivedQuantity());
        return resp;
    }

    private UserCouponResponse convertToUserCouponResponse(UserCoupon userCoupon) {
        UserCouponResponse resp = new UserCouponResponse();
        BeanUtils.copyProperties(userCoupon, resp);
        resp.setCouponTypeName(CouponType.getByCode(userCoupon.getCouponType()) != null ?
                CouponType.getByCode(userCoupon.getCouponType()).getName() : "");
        UserCouponStatus status = UserCouponStatus.getByCode(userCoupon.getStatus());
        if (status != null) {
            resp.setStatusTag(status.getTag());
        }
        resp.setExpiringSoon(LocalDateTime.now().plusDays(3).isAfter(userCoupon.getValidEndTime()));
        return resp;
    }

    private UseCouponResult buildErrorResult(String reason) {
        UseCouponResult result = new UseCouponResult();
        result.setUsable(false);
        result.setReason(reason);
        return result;
    }
}
