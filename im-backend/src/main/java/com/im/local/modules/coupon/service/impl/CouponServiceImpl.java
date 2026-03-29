package com.im.local.modules.coupon.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.common.exception.BusinessException;
import com.im.local.modules.coupon.dto.*;
import com.im.local.modules.coupon.entity.Coupon;
import com.im.local.modules.coupon.entity.UserCoupon;
import com.im.local.modules.coupon.enums.*;
import com.im.local.modules.coupon.repository.CouponRepository;
import com.im.local.modules.coupon.repository.UserCouponRepository;
import com.im.local.modules.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Override
    public CouponDTO getCouponById(Long id) {
        Coupon coupon = couponRepository.selectById(id);
        if (coupon == null || coupon.getDeleted()) {
            throw new BusinessException("优惠券不存在");
        }
        return convertToCouponDTO(coupon);
    }

    @Override
    public IPage<CouponDTO> getNearbyCoupons(Page<CouponDTO> page, Double lat, Double lng, Double radius) {
        IPage<Coupon> couponPage = couponRepository.selectNearbyCoupons(
            new Page<>(page.getCurrent(), page.getSize()),
            lat, lng, radius, LocalDateTime.now()
        );
        
        List<CouponDTO> dtoList = couponPage.getRecords().stream()
            .map(this::convertToCouponDTO)
            .collect(Collectors.toList());
        
        Page<CouponDTO> resultPage = new Page<>(couponPage.getCurrent(), couponPage.getSize(), couponPage.getTotal());
        resultPage.setRecords(dtoList);
        return resultPage;
    }

    @Override
    public List<CouponDTO> getMerchantCoupons(Long merchantId) {
        List<Coupon> coupons = couponRepository.selectByMerchantId(merchantId);
        return coupons.stream()
            .map(this::convertToCouponDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCouponDTO receiveCoupon(Long userId, ReceiveCouponRequestDTO request) {
        // 1. 检查优惠券
        Coupon coupon = couponRepository.selectById(request.getCouponId());
        if (coupon == null || coupon.getDeleted()) {
            throw new BusinessException("优惠券不存在");
        }
        
        // 2. 检查有效性
        if (!coupon.isValid()) {
            throw new BusinessException("优惠券不在有效期内");
        }
        
        // 3. 检查库存
        if (!coupon.hasStock()) {
            throw new BusinessException("优惠券已被领完");
        }
        
        // 4. 检查领取限制
        int receivedCount = userCouponRepository.countByUserAndCoupon(userId, request.getCouponId());
        if (!coupon.canReceive(receivedCount)) {
            throw new BusinessException("已达到领取上限");
        }
        
        // 5. 检查是否新用户专享
        if (coupon.getNewUserOnly()) {
            // TODO: 检查用户是否为新用户
            log.info("新用户专享优惠券校验, userId={}", userId);
        }
        
        // 6. 扣减库存（乐观锁）
        int updated = couponRepository.decrementStock(coupon.getId(), coupon.getVersion());
        if (updated == 0) {
            throw new BusinessException("优惠券领取失败，请重试");
        }
        
        // 7. 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(coupon.getId());
        userCoupon.setTemplateId(coupon.getTemplateId());
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setCouponType(coupon.getType());
        userCoupon.setCouponValue(coupon.getValue());
        userCoupon.setMinSpend(coupon.getMinSpend());
        userCoupon.setMaxDiscount(coupon.getMaxDiscount());
        userCoupon.setValidStartTime(coupon.getStartTime());
        userCoupon.setValidEndTime(coupon.getEndTime());
        userCoupon.setStatus(UserCouponStatus.UNUSED.getCode());
        userCoupon.setReceiveChannel(request.getReceiveChannel());
        userCoupon.setSourceUserId(request.getSourceUserId());
        userCoupon.setReceiveTime(LocalDateTime.now());
        
        userCouponRepository.insert(userCoupon);
        
        log.info("用户领取优惠券成功, userId={}, couponId={}", userId, coupon.getId());
        
        return convertToUserCouponDTO(userCoupon);
    }

    @Override
    public List<UserCouponDTO> getUserCoupons(Long userId) {
        // 先标记过期优惠券
        markExpiredCoupons(userId);
        
        List<UserCoupon> coupons = userCouponRepository.selectByUserId(userId);
        return coupons.stream()
            .map(this::convertToUserCouponDTO)
            .collect(Collectors.toList());
    }

    @Override
    public IPage<UserCouponDTO> getUsableCoupons(Page<UserCouponDTO> page, Long userId) {
        IPage<UserCoupon> couponPage = userCouponRepository.selectUsableByUserId(
            new Page<>(page.getCurrent(), page.getSize()),
            userId, LocalDateTime.now()
        );
        
        List<UserCouponDTO> dtoList = couponPage.getRecords().stream()
            .map(this::convertToUserCouponDTO)
            .collect(Collectors.toList());
        
        Page<UserCouponDTO> resultPage = new Page<>(couponPage.getCurrent(), couponPage.getSize(), couponPage.getTotal());
        resultPage.setRecords(dtoList);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal useCoupon(Long userId, UseCouponRequestDTO request) {
        UserCoupon userCoupon = userCouponRepository.selectById(request.getUserCouponId());
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (!userCoupon.isUsable()) {
            throw new BusinessException("优惠券不可用");
        }
        
        if (!userCoupon.meetsMinSpend(request.getOrderAmount())) {
            throw new BusinessException("订单金额未达到使用门槛");
        }
        
        BigDecimal discount = userCoupon.calculateDiscount(request.getOrderAmount());
        
        int updated = userCouponRepository.useCoupon(
            userCoupon.getId(), LocalDateTime.now(),
            request.getOrderId(), request.getOrderAmount(), discount
        );
        
        if (updated == 0) {
            throw new BusinessException("优惠券使用失败");
        }
        
        log.info("用户使用优惠券成功, userId={}, couponId={}, discount={}", 
            userId, userCoupon.getId(), discount);
        
        return discount;
    }

    @Override
    public List<UserCouponDTO> calculateAvailableCoupons(Long userId, BigDecimal orderAmount, Long merchantId) {
        markExpiredCoupons(userId);
        
        List<UserCoupon> allCoupons = userCouponRepository.selectByUserId(userId);
        List<UserCouponDTO> availableCoupons = new ArrayList<>();
        
        for (UserCoupon coupon : allCoupons) {
            if (coupon.isUsable() && coupon.meetsMinSpend(orderAmount)) {
                availableCoupons.add(convertToUserCouponDTO(coupon));
            }
        }
        
        // 按优惠金额排序
        availableCoupons.sort((a, b) -> {
            BigDecimal discountA = calculateDiscount(a, orderAmount);
            BigDecimal discountB = calculateDiscount(b, orderAmount);
            return discountB.compareTo(discountA);
        });
        
        return availableCoupons;
    }

    @Override
    public boolean checkCouponUsable(Long userCouponId, Long userId, BigDecimal orderAmount) {
        UserCoupon userCoupon = userCouponRepository.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            return false;
        }
        return userCoupon.isUsable() && userCoupon.meetsMinSpend(orderAmount);
    }

    @Override
    public List<UserCouponDTO> getExpiringSoonCoupons(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusDays(3);
        
        List<UserCoupon> coupons = userCouponRepository.selectExpiringSoon(userId, now, expireTime);
        return coupons.stream()
            .map(this::convertToUserCouponDTO)
            .collect(Collectors.toList());
    }

    @Override
    public int markExpiredCoupons(Long userId) {
        return userCouponRepository.markExpired(userId, LocalDateTime.now());
    }

    // === 私有方法 ===

    private CouponDTO convertToCouponDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        BeanUtils.copyProperties(coupon, dto);
        dto.setTypeName(CouponType.getNameByCode(coupon.getType()));
        dto.setStatusName(CouponStatus.getNameByCode(coupon.getStatus()));
        dto.setRemainingQuantity(coupon.getTotalQuantity() - coupon.getReceivedQuantity());
        return dto;
    }

    private UserCouponDTO convertToUserCouponDTO(UserCoupon userCoupon) {
        UserCouponDTO dto = new UserCouponDTO();
        BeanUtils.copyProperties(userCoupon, dto);
        dto.setCouponTypeName(CouponType.getNameByCode(userCoupon.getCouponType()));
        dto.setStatusName(UserCouponStatus.getNameByCode(userCoupon.getStatus()));
        dto.setReceiveChannelName(ReceiveChannel.getNameByCode(userCoupon.getReceiveChannel()));
        
        // 计算是否即将过期
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(now, userCoupon.getValidEndTime());
        dto.setExpiringSoon(days <= 3 && days >= 0);
        dto.setRemainingDays(Math.max(0, days));
        
        // 格式化显示文本
        dto.setDisplayText(formatCouponDisplay(userCoupon));
        
        return dto;
    }

    private String formatCouponDisplay(UserCoupon coupon) {
        CouponType type = CouponType.getByCode(coupon.getCouponType());
        if (type == null) return "";
        
        switch (type) {
            case FULL_REDUCTION:
                return String.format("满%s减%s", coupon.getMinSpend(), coupon.getCouponValue());
            case DISCOUNT:
                BigDecimal discount = coupon.getCouponValue().multiply(new BigDecimal("10"));
                return String.format("%s折", discount.setScale(1, RoundingMode.HALF_UP));
            case NO_THRESHOLD:
                return String.format("%s元", coupon.getCouponValue());
            case EXCHANGE:
                return "兑换券";
            default:
                return "";
        }
    }

    private BigDecimal calculateDiscount(UserCouponDTO dto, BigDecimal orderAmount) {
        if (dto.getMinSpend() != null && orderAmount.compareTo(dto.getMinSpend()) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount;
        switch (dto.getCouponType()) {
            case 1: // 满减
                discount = dto.getCouponValue();
                break;
            case 2: // 折扣
                discount = orderAmount.multiply(BigDecimal.ONE.subtract(dto.getCouponValue()));
                if (dto.getMaxDiscount() != null && discount.compareTo(dto.getMaxDiscount()) > 0) {
                    discount = dto.getMaxDiscount();
                }
                break;
            case 3: // 无门槛
                discount = dto.getCouponValue().min(orderAmount);
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        return discount;
    }
}
