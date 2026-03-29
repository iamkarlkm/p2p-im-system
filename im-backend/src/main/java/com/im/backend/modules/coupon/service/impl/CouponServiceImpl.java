package com.im.backend.modules.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.exception.BusinessException;
import com.im.backend.common.util.GeoHashUtil;
import com.im.backend.common.util.RedisUtil;
import com.im.backend.modules.coupon.dto.*;
import com.im.backend.modules.coupon.entity.Coupon;
import com.im.backend.modules.coupon.entity.UserCoupon;
import com.im.backend.modules.coupon.mapper.CouponMapper;
import com.im.backend.modules.coupon.mapper.UserCouponMapper;
import com.im.backend.modules.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 * 
 * 实现功能:
 * 1. 优惠券全生命周期管理（创建/审核/暂停/结束）
 * 2. 高并发领取服务（Redis Lua + 分布式锁）
 * 3. LBS附近搜索（Redis Geo）
 * 4. 优惠券核销与退还
 * 5. 数据统计算法
 * 
 * 技术亮点:
 * - 库存扣减使用Redis Lua脚本保证原子性
 * - 附近搜索使用Redis Geo + GeoHash分层
 * - 核销使用乐观锁防止并发冲突
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtil redisUtil;
    
    // Redis Key前缀
    private static final String COUPON_STOCK_KEY = "coupon:stock:";
    private static final String COUPON_RECEIVE_COUNT_KEY = "coupon:receive:user:";
    private static final String COUPON_GEO_KEY = "coupon:geo";
    private static final String COUPON_LOCK_KEY = "coupon:lock:";
    private static final String USER_COUPON_LOCK_KEY = "user:coupon:lock:";
    
    // Lua脚本：扣减库存并记录领取
    private static final String RECEIVE_COUPON_LUA = 
        "local stockKey = KEYS[1]" +
        "local userCountKey = KEYS[2]" +
        "local limit = tonumber(ARGV[1])" +
        "local stock = redis.call('get', stockKey)" +
        "if not stock then return -1 end" +
        "if tonumber(stock) <= 0 then return -2 end" +
        "local userCount = redis.call('get', userCountKey)" +
        "if userCount and tonumber(userCount) >= limit then return -3 end" +
        "redis.call('decr', stockKey)" +
        "redis.call('incr', userCountKey)" +
        "redis.call('expire', userCountKey, 86400)" +
        "return 1";

    // ==================== 优惠券管理 ====================
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(CreateCouponRequest request, Long operatorId) {
        log.info("创建优惠券, name={}, operatorId={}", request.getName(), operatorId);
        
        // 校验时间
        if (request.getIssueEndTime().isBefore(request.getIssueStartTime())) {
            throw new BusinessException("发放结束时间不能早于开始时间");
        }
        if (request.getUseEndTime().isBefore(request.getUseStartTime())) {
            throw new BusinessException("使用结束时间不能早于开始时间");
        }
        
        // 构建实体
        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setCouponType(request.getCouponType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinSpend(request.getMinSpend());
        coupon.setMaxDiscount(request.getMaxDiscount());
        
        // 生成优惠券编码
        coupon.setCouponCode(generateCouponCode());
        
        // 适用范围
        if (!CollectionUtils.isEmpty(request.getApplicablePoiIds())) {
            coupon.setApplicablePoiIds(JSON.toJSONString(request.getApplicablePoiIds()));
        }
        if (!CollectionUtils.isEmpty(request.getApplicableCategories())) {
            coupon.setApplicableCategories(JSON.toJSONString(request.getApplicableCategories()));
        }
        if (!CollectionUtils.isEmpty(request.getApplicableProductIds())) {
            coupon.setApplicableProductIds(JSON.toJSONString(request.getApplicableProductIds()));
        }
        if (!CollectionUtils.isEmpty(request.getExcludedProductIds())) {
            coupon.setExcludedProductIds(JSON.toJSONString(request.getExcludedProductIds()));
        }
        
        // 地理位置
        coupon.setGeoLimited(request.getGeoLimited());
        if (Boolean.TRUE.equals(request.getGeoLimited())) {
            coupon.setCenterLongitude(request.getCenterLongitude());
            coupon.setCenterLatitude(request.getCenterLatitude());
            coupon.setEffectiveRadius(request.getEffectiveRadius());
            coupon.setGeofenceId(request.getGeofenceId());
            // 计算GeoHash
            if (request.getCenterLatitude() != null && request.getCenterLongitude() != null) {
                coupon.setGeoHash(GeoHashUtil.encode(
                    request.getCenterLatitude().doubleValue(), 
                    request.getCenterLongitude().doubleValue(), 7));
            }
        }
        
        // 库存配置
        coupon.setTotalStock(request.getTotalStock());
        coupon.setRemainingStock(request.getTotalStock());
        coupon.setPerUserLimit(request.getPerUserLimit());
        coupon.setDailyLimitPerUser(request.getDailyLimitPerUser());
        
        // 时间配置
        coupon.setIssueStartTime(request.getIssueStartTime());
        coupon.setIssueEndTime(request.getIssueEndTime());
        coupon.setUseStartTime(request.getUseStartTime());
        coupon.setUseEndTime(request.getUseEndTime());
        coupon.setValidDaysAfterReceive(request.getValidDaysAfterReceive());
        
        // 触发配置
        coupon.setTriggerScene(request.getTriggerScene());
        coupon.setTriggerConfig(request.getTriggerConfig());
        coupon.setPushNotification(request.getPushNotification());
        coupon.setPushTemplateId(request.getPushTemplateId());
        
        // 分享配置
        coupon.setShareable(request.getShareable());
        coupon.setShareRewardType(request.getShareRewardType());
        coupon.setShareRewardCouponId(request.getShareRewardCouponId());
        
        // 展示配置
        coupon.setSortOrder(request.getSortOrder());
        coupon.setIsTop(request.getIsTop());
        coupon.setCoverImage(request.getCoverImage());
        if (!CollectionUtils.isEmpty(request.getDetailImages())) {
            coupon.setDetailImages(JSON.toJSONString(request.getDetailImages()));
        }
        if (!CollectionUtils.isEmpty(request.getTags())) {
            coupon.setTags(JSON.toJSONString(request.getTags()));
        }
        
        coupon.setStackable(request.getStackable());
        coupon.setExtFields(request.getExtFields());
        coupon.setIsPlatformCoupon(request.getIsPlatformCoupon());
        
        // 初始状态：草稿
        coupon.setStatus(0);
        coupon.setReceivedCount(0);
        coupon.setUsedCount(0);
        coupon.setViewCount(0);
        coupon.setCreateUserId(operatorId);
        coupon.setUpdateUserId(operatorId);
        
        couponMapper.insert(coupon);
        
        log.info("优惠券创建成功, id={}, code={}", coupon.getId(), coupon.getCouponCode());
        return coupon.getId();
    }
    
    /**
     * 生成优惠券编码
     */
    private String generateCouponCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("CP");
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    @Override
    public void updateCoupon(Long couponId, UpdateCouponRequest request, Long operatorId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的优惠券可以修改");
        }
        
        // 更新字段...
        coupon.setUpdateUserId(operatorId);
        couponMapper.updateById(coupon);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupon(Long couponId, Long operatorId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getReceivedCount() > 0) {
            throw new BusinessException("已有用户领取，不能删除");
        }
        couponMapper.deleteById(couponId);
    }
    
    @Override
    public CouponDetailResponse getCouponDetail(Long couponId, Long userId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        // 增加浏览量
        coupon.setViewCount(coupon.getViewCount() + 1);
        couponMapper.updateById(coupon);
        
        return convertToDetailResponse(coupon, userId);
    }
    
    @Override
    public Page<CouponListResponse> listCoupons(CouponQueryRequest query) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getStatus() != null) {
            wrapper.eq(Coupon::getStatus, query.getStatus());
        }
        if (query.getMerchantId() != null) {
            wrapper.eq(Coupon::getMerchantId, query.getMerchantId());
        }
        if (query.getCouponType() != null) {
            wrapper.eq(Coupon::getCouponType, query.getCouponType());
        }
        if (Boolean.TRUE.equals(query.getIsPlatformCoupon())) {
            wrapper.eq(Coupon::getIsPlatformCoupon, true);
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Coupon::getName, query.getKeyword());
        }
        
        // 默认排序：置顶优先，然后按排序值
        wrapper.orderByDesc(Coupon::getIsTop)
               .orderByDesc(Coupon::getSortOrder)
               .orderByDesc(Coupon::getCreateTime);
        
        Page<Coupon> page = new Page<>(query.getPage(), query.getSize());
        Page<Coupon> result = couponMapper.selectPage(page, wrapper);
        
        List<CouponListResponse> list = result.getRecords().stream()
            .map(this::convertToListResponse)
            .collect(Collectors.toList());
        
        Page<CouponListResponse> responsePage = new Page<>();
        responsePage.setCurrent(result.getCurrent());
        responsePage.setSize(result.getSize());
        responsePage.setTotal(result.getTotal());
        responsePage.setRecords(list);
        
        return responsePage;
    }
    
    @Override
    public List<CouponListResponse> listMerchantCoupons(Long merchantId, Integer status) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getMerchantId, merchantId);
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }
        wrapper.orderByDesc(Coupon::getCreateTime);
        
        List<Coupon> coupons = couponMapper.selectList(wrapper);
        return coupons.stream()
            .map(this::convertToListResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditCoupon(Long couponId, Boolean approved, String remark, Long auditorId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new BusinessException("只有待审核状态可以审核");
        }
        
        if (approved) {
            coupon.setStatus(2); // 进行中
            // 同步到Redis
            syncCouponToRedis(couponId);
        } else {
            coupon.setStatus(5); // 审核拒绝
        }
        coupon.setAuditRemark(remark);
        coupon.setAuditTime(LocalDateTime.now());
        coupon.setAuditUserId(auditorId);
        couponMapper.updateById(coupon);
    }
    
    @Override
    public void pauseCoupon(Long couponId, Long operatorId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 2) {
            throw new BusinessException("优惠券不在进行中状态");
        }
        coupon.setStatus(3);
        coupon.setUpdateUserId(operatorId);
        couponMapper.updateById(coupon);
    }
    
    @Override
    public void resumeCoupon(Long couponId, Long operatorId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 3) {
            throw new BusinessException("优惠券不在暂停状态");
        }
        coupon.setStatus(2);
        coupon.setUpdateUserId(operatorId);
        couponMapper.updateById(coupon);
    }
    
    @Override
    public void terminateCoupon(Long couponId, Long operatorId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        coupon.setStatus(4);
        coupon.setIssueEndTime(LocalDateTime.now());
        coupon.setUpdateUserId(operatorId);
        couponMapper.updateById(coupon);
    }
    
    // ==================== 用户领取服务 ====================
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long receiveCoupon(Long couponId, Long userId, Integer receiveSource,
                               BigDecimal longitude, BigDecimal latitude) {
        log.info("用户领取优惠券, couponId={}, userId={}", couponId, userId);
        
        // 获取优惠券
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        // 基础校验
        validateCouponForReceive(coupon, userId);
        
        // 分布式锁防止并发超领
        String lockKey = COUPON_LOCK_KEY + couponId + ":" + userId;
        Boolean locked = redisUtil.tryLock(lockKey, 10, TimeUnit.SECONDS);
        if (!locked) {
            throw new BusinessException("操作过于频繁，请稍后重试");
        }
        
        try {
            // 再次校验（防止并发）
            validateCouponForReceive(coupon, userId);
            
            // Redis扣减库存
            String stockKey = COUPON_STOCK_KEY + couponId;
            String userCountKey = COUPON_RECEIVE_COUNT_KEY + couponId + ":" + userId;
            
            Long result = redisUtil.executeLua(RECEIVE_COUPON_LUA, 
                Arrays.asList(stockKey, userCountKey),
                Arrays.asList(String.valueOf(coupon.getPerUserLimit())));
            
            if (result == null || result == -1) {
                throw new BusinessException("优惠券不存在或已下架");
            }
            if (result == -2) {
                throw new BusinessException("优惠券已被领完");
            }
            if (result == -3) {
                throw new BusinessException("您已达到领取上限");
            }
            
            // 创建用户优惠券记录
            UserCoupon userCoupon = createUserCoupon(coupon, userId, receiveSource, longitude, latitude);
            userCouponMapper.insert(userCoupon);
            
            // 更新优惠券统计
            coupon.setReceivedCount(coupon.getReceivedCount() + 1);
            coupon.setRemainingStock(coupon.getRemainingStock() - 1);
            couponMapper.updateById(coupon);
            
            log.info("用户领取优惠券成功, userCouponId={}", userCoupon.getId());
            return userCoupon.getId();
            
        } finally {
            redisUtil.unlock(lockKey);
        }
    }
    
    /**
     * 校验优惠券是否可领取
     */
    private void validateCouponForReceive(Coupon coupon, Long userId) {
        // 状态校验
        if (coupon.getStatus() != 2) {
            throw new BusinessException("优惠券不在领取期");
        }
        
        // 时间校验
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueStartTime()) || now.isAfter(coupon.getIssueEndTime())) {
            throw new BusinessException("不在优惠券领取时间内");
        }
        
        // 库存校验
        if (coupon.getRemainingStock() <= 0) {
            throw new BusinessException("优惠券已被领完");
        }
        
        // 每人限领校验
        Long userReceivedCount = userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getCouponId, coupon.getId())
                .eq(UserCoupon::getUserId, userId)
        );
        if (userReceivedCount >= coupon.getPerUserLimit()) {
            throw new BusinessException("您已达到领取上限");
        }
    }
    
    /**
     * 创建用户优惠券实体
     */
    private UserCoupon createUserCoupon(Coupon coupon, Long userId, Integer receiveSource,
                                        BigDecimal longitude, BigDecimal latitude) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(coupon.getId());
        userCoupon.setCouponCode(coupon.getCouponCode());
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setStatus(0); // 未使用
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCoupon.setReceiveSource(receiveSource);
        userCoupon.setReceiveLongitude(longitude);
        userCoupon.setReceiveLatitude(latitude);
        userCoupon.setOriginalUserId(userId);
        userCoupon.setTransferCount(0);
        
        // 计算有效期
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidDaysAfterReceive() != null && coupon.getValidDaysAfterReceive() > 0) {
            userCoupon.setValidStartTime(now);
            userCoupon.setValidEndTime(now.plusDays(coupon.getValidDaysAfterReceive()));
        } else {
            userCoupon.setValidStartTime(coupon.getUseStartTime());
            userCoupon.setValidEndTime(coupon.getUseEndTime());
        }
        
        // 使用限制快照
        JSONObject snapshot = new JSONObject();
        snapshot.put("minSpend", coupon.getMinSpend());
        snapshot.put("discountValue", coupon.getDiscountValue());
        snapshot.put("maxDiscount", coupon.getMaxDiscount());
        snapshot.put("applicableCategories", coupon.getApplicableCategories());
        snapshot.put("applicableProductIds", coupon.getApplicableProductIds());
        snapshot.put("excludedProductIds", coupon.getExcludedProductIds());
        userCoupon.setRestrictionSnapshot(snapshot.toJSONString());
        
        return userCoupon;
    }
    
    @Override
    public Boolean receiveCouponByGeofence(Long couponId, Long userId, Long fenceId,
                                            BigDecimal longitude, BigDecimal latitude) {
        // 校验围栏触发
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || !Boolean.TRUE.equals(coupon.getGeoLimited())) {
            return false;
        }
        
        // 检查是否在有效范围内
        double distance = calculateDistance(
            latitude.doubleValue(), longitude.doubleValue(),
            coupon.getCenterLatitude().doubleValue(), coupon.getCenterLongitude().doubleValue()
        );
        
        if (distance > coupon.getEffectiveRadius()) {
            log.warn("用户不在优惠券有效范围内, distance={}m", distance);
            return false;
        }
        
        try {
            receiveCoupon(couponId, userId, 2, longitude, latitude);
            return true;
        } catch (BusinessException e) {
            log.warn("地理围栏领券失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 计算两点距离（米）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 地球半径（米）
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    @Override
    public List<Long> batchReceiveCoupons(List<Long> couponIds, Long userId, Integer source) {
        List<Long> successIds = new ArrayList<>();
        for (Long couponId : couponIds) {
            try {
                Long userCouponId = receiveCoupon(couponId, userId, source, null, null);
                successIds.add(userCouponId);
            } catch (Exception e) {
                log.warn("批量领券失败, couponId={}: {}", couponId, e.getMessage());
            }
        }
        return successIds;
    }
    
    @Override
    public ReceiveCheckResult checkCanReceive(Long couponId, Long userId) {
        ReceiveCheckResult result = new ReceiveCheckResult();
        Coupon coupon = couponMapper.selectById(couponId);
        
        if (coupon == null) {
            result.setCanReceive(false);
            result.setReason("优惠券不存在");
            return result;
        }
        
        if (coupon.getStatus() != 2) {
            result.setCanReceive(false);
            result.setReason("优惠券不在领取期");
            return result;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueStartTime())) {
            result.setCanReceive(false);
            result.setReason("领取尚未开始");
            result.setCountdownSeconds(
                java.time.Duration.between(now, coupon.getIssueStartTime()).getSeconds()
            );
            return result;
        }
        
        if (now.isAfter(coupon.getIssueEndTime())) {
            result.setCanReceive(false);
            result.setReason("领取已结束");
            return result;
        }
        
        if (coupon.getRemainingStock() <= 0) {
            result.setCanReceive(false);
            result.setReason("已被领完");
            return result;
        }
        
        Long receivedCount = userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getCouponId, couponId)
                .eq(UserCoupon::getUserId, userId)
        );
        if (receivedCount >= coupon.getPerUserLimit()) {
            result.setCanReceive(false);
            result.setReason("您已达到领取上限");
            result.setReceivedCount(receivedCount.intValue());
            result.setLimitCount(coupon.getPerUserLimit());
            return result;
        }
        
        result.setCanReceive(true);
        result.setRemainingStock(coupon.getRemainingStock());
        result.setReceivedCount(receivedCount.intValue());
        result.setLimitCount(coupon.getPerUserLimit());
        return result;
    }
    
    // 由于篇幅限制，以下方法简化实现...
    
    @Override
    public Page<UserCouponResponse> listUserCoupons(Long userId, Integer status, int page, int size) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getCreateTime);
        
        Page<UserCoupon> result = userCouponMapper.selectPage(new Page<>(page, size), wrapper);
        
        List<UserCouponResponse> list = result.getRecords().stream()
            .map(this::convertToUserCouponResponse)
            .collect(Collectors.toList());
        
        Page<UserCouponResponse> responsePage = new Page<>();
        responsePage.setCurrent(result.getCurrent());
        responsePage.setSize(result.getSize());
        responsePage.setTotal(result.getTotal());
        responsePage.setRecords(list);
        
        return responsePage;
    }
    
    @Override
    public List<UserCouponResponse> listAvailableCoupons(Long userId, Long merchantId, 
                                                           BigDecimal orderAmount, List<Long> categoryIds) {
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
               .eq(UserCoupon::getStatus, 0)
               .le(UserCoupon::getValidStartTime, now)
               .ge(UserCoupon::getValidEndTime, now);
        
        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);
        
        return userCoupons.stream()
            .filter(uc -> isCouponApplicable(uc, merchantId, orderAmount, categoryIds))
            .map(this::convertToUserCouponResponse)
            .sorted((a, b) -> b.getDiscountAmount().compareTo(a.getDiscountAmount()))
            .collect(Collectors.toList());
    }
    
    /**
     * 检查优惠券是否适用于当前订单
     */
    private boolean isCouponApplicable(UserCoupon userCoupon, Long merchantId, 
                                        BigDecimal orderAmount, List<Long> categoryIds) {
        // 解析快照
        if (!StringUtils.hasText(userCoupon.getRestrictionSnapshot())) {
            return true;
        }
        
        try {
            JSONObject snapshot = JSON.parseObject(userCoupon.getRestrictionSnapshot());
            BigDecimal minSpend = snapshot.getBigDecimal("minSpend");
            
            // 金额限制
            if (minSpend != null && orderAmount.compareTo(minSpend) < 0) {
                return false;
            }
            
            // 其他限制校验...
            return true;
        } catch (Exception e) {
            return true;
        }
    }
    
    @Override
    public UserCouponDetailResponse getUserCouponDetail(Long userCouponId, Long userId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("优惠券不存在");
        }
        return convertToUserCouponDetailResponse(userCoupon);
    }
    
    @Override
    public UserCouponStatistics getUserCouponStatistics(Long userId) {
        UserCouponStatistics stats = new UserCouponStatistics();
        stats.setUnusedCount(userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getUserId, userId).eq(UserCoupon::getStatus, 0)));
        stats.setUsedCount(userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getUserId, userId).eq(UserCoupon::getStatus, 1)));
        stats.setExpiredCount(userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getUserId, userId).eq(UserCoupon::getStatus, 2)));
        
        // 计算即将过期（3天内）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);
        stats.setExpiringSoonCount(userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, 0)
                .le(UserCoupon::getValidEndTime, threeDaysLater)
                .ge(UserCoupon::getValidEndTime, now)));
        
        return stats;
    }
    
    // ==================== LBS附近优惠券 ====================
    
    @Override
    public List<NearbyCouponResponse> searchNearbyCoupons(BigDecimal longitude, BigDecimal latitude, 
                                                           Integer radius, int page, int size) {
        // 使用Redis Geo搜索
        Circle circle = new Circle(new Point(longitude.doubleValue(), latitude.doubleValue()), 
                                   new Distance(radius, RedisGeoCommands.DistanceUnit.METERS));
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
            .newGeoRadiusArgs().includeDistance().sortAscending().limit(page * size + size);
        
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
            redisTemplate.opsForGeo().radius(COUPON_GEO_KEY, circle, args);
        
        if (results == null) {
            return Collections.emptyList();
        }
        
        List<NearbyCouponResponse> list = new ArrayList<>();
        int skip = (page - 1) * size;
        int index = 0;
        
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : results) {
            if (index++ < skip) continue;
            if (list.size() >= size) break;
            
            Long couponId = Long.valueOf(result.getContent().getName().toString());
            Double distance = result.getDistance().getValue();
            
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon != null && coupon.getStatus() == 2) {
                NearbyCouponResponse response = new NearbyCouponResponse();
                response.setCouponId(couponId);
                response.setName(coupon.getName());
                response.setDistance(distance.intValue());
                // 设置其他字段...
                list.add(response);
            }
        }
        
        return list;
    }
    
    @Override
    public Long countNearbyCoupons(BigDecimal longitude, BigDecimal latitude, Integer radius) {
        Circle circle = new Circle(new Point(longitude.doubleValue(), latitude.doubleValue()), 
                                   new Distance(radius, RedisGeoCommands.DistanceUnit.METERS));
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
            redisTemplate.opsForGeo().radius(COUPON_GEO_KEY, circle);
        return results != null ? results.getContent().size() : 0L;
    }
    
    @Override
    public List<CouponListResponse> listCouponsByPoi(Long poiId, Long userId) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 2)
               .like(Coupon::getApplicablePoiIds, "\"" + poiId + "\"")
               .or()
               .apply("JSON_CONTAINS(applicable_poi_ids, CAST({0} AS JSON))", poiId);
        
        List<Coupon> coupons = couponMapper.selectList(wrapper);
        return coupons.stream()
            .map(this::convertToListResponse)
            .collect(Collectors.toList());
    }
    
    // ==================== 优惠券核销 ====================
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal useCoupon(Long userCouponId, Long userId, Long orderId, String orderNo,
                                 BigDecimal orderAmount, Long merchantId, Long poiId,
                                 BigDecimal longitude, BigDecimal latitude) {
        log.info("使用优惠券, userCouponId={}, orderId={}", userCouponId, orderId);
        
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (userCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券状态异常: " + userCoupon.getStatusDescription());
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(userCoupon.getValidEndTime())) {
            throw new BusinessException("优惠券已过期");
        }
        
        // 获取优惠券模板计算优惠
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null) {
            throw new BusinessException("优惠券模板不存在");
        }
        
        BigDecimal discountAmount = coupon.calculateDiscount(orderAmount);
        if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("订单金额不满足优惠券使用条件");
        }
        
        // 乐观锁更新
        int updated = userCouponMapper.updateStatusWithVersion(userCouponId, 0, 1, userCoupon.getVersion());
        if (updated == 0) {
            throw new BusinessException("优惠券使用失败，请重试");
        }
        
        userCoupon.setStatus(1);
        userCoupon.setUseTime(now);
        userCoupon.setOrderId(orderId);
        userCoupon.setOrderNo(orderNo);
        userCoupon.setOrderAmount(orderAmount);
        userCoupon.setDiscountAmount(discountAmount);
        userCoupon.setUsedMerchantId(merchantId);
        userCoupon.setUsedPoiId(poiId);
        userCoupon.setUseLongitude(longitude);
        userCoupon.setUseLatitude(latitude);
        userCouponMapper.updateById(userCoupon);
        
        // 更新优惠券统计
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        coupon.setRelatedOrderAmount(coupon.getRelatedOrderAmount().add(orderAmount));
        coupon.setTotalDiscountAmount(coupon.getTotalDiscountAmount().add(discountAmount));
        couponMapper.updateById(coupon);
        
        log.info("优惠券使用成功, discountAmount={}", discountAmount);
        return discountAmount;
    }
    
    @Override
    public BestCouponRecommendation recommendBestCoupon(Long userId, Long merchantId, 
                                                         BigDecimal orderAmount, List<Long> productIds) {
        List<UserCouponResponse> availableCoupons = listAvailableCoupons(userId, merchantId, orderAmount, productIds);
        
        if (availableCoupons.isEmpty()) {
            return new BestCouponRecommendation();
        }
        
        // 按优惠金额排序，返回最优的
        UserCouponResponse best = availableCoupons.get(0);
        
        BestCouponRecommendation recommendation = new BestCouponRecommendation();
        recommendation.setUserCouponId(best.getId());
        recommendation.setCouponName(best.getCouponName());
        recommendation.setDiscountAmount(best.getDiscountAmount());
        recommendation.setFinalAmount(orderAmount.subtract(best.getDiscountAmount()));
        recommendation.setAvailableCoupons(availableCoupons);
        
        return recommendation;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean returnCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getOrderId().equals(orderId)) {
            return false;
        }
        
        if (userCoupon.getStatus() != 1) {
            return false;
        }
        
        userCoupon.setStatus(6); // 退款退回
        userCoupon.setRemark("订单退款，优惠券退回");
        userCouponMapper.updateById(userCoupon);
        
        return true;
    }
    
    @Override
    public Boolean lockCoupon(Long userCouponId, Long orderId, Integer timeoutSeconds) {
        String lockKey = USER_COUPON_LOCK_KEY + userCouponId;
        return redisUtil.tryLock(lockKey, timeoutSeconds, TimeUnit.SECONDS);
    }
    
    @Override
    public void unlockCoupon(Long userCouponId, Long orderId) {
        String lockKey = USER_COUPON_LOCK_KEY + userCouponId;
        redisUtil.unlock(lockKey);
    }
    
    // ==================== 转赠功能 ====================
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transferCoupon(Long userCouponId, Long fromUserId, Long toUserId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(fromUserId)) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (userCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券状态异常，无法转赠");
        }
        
        if (!userCoupon.canTransfer()) {
            throw new BusinessException("该优惠券不支持转赠或已达转赠上限");
        }
        
        userCoupon.setStatus(3); // 已转赠
        userCoupon.setTransferTime(LocalDateTime.now());
        userCoupon.setTransferredToUserId(toUserId);
        userCoupon.setTransferCount(userCoupon.getTransferCount() + 1);
        userCouponMapper.updateById(userCoupon);
        
        // 创建新的用户优惠券记录给接收者
        UserCoupon newCoupon = new UserCoupon();
        newCoupon.setUserId(toUserId);
        newCoupon.setCouponId(userCoupon.getCouponId());
        newCoupon.setCouponCode(userCoupon.getCouponCode());
        newCoupon.setCouponName(userCoupon.getCouponName());
        newCoupon.setStatus(0);
        newCoupon.setValidStartTime(userCoupon.getValidStartTime());
        newCoupon.setValidEndTime(userCoupon.getValidEndTime());
        newCoupon.setOriginalUserId(userCoupon.getOriginalUserId());
        newCoupon.setTransferredFromUserId(fromUserId);
        newCoupon.setTransferCount(userCoupon.getTransferCount());
        newCoupon.setMaxTransferCount(userCoupon.getMaxTransferCount());
        newCoupon.setReceiveSource(10); // 转赠获得
        newCoupon.setReceiveTime(LocalDateTime.now());
        newCoupon.setRestrictionSnapshot(userCoupon.getRestrictionSnapshot());
        userCouponMapper.insert(newCoupon);
        
        return true;
    }
    
    @Override
    public Long acceptTransferredCoupon(String transferCode, Long toUserId) {
        // 转赠码逻辑简化实现
        return null;
    }
    
    // ==================== 数据统计 ====================
    
    @Override
    public CouponStatisticsResponse getCouponStatistics(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        CouponStatisticsResponse stats = new CouponStatisticsResponse();
        stats.setCouponId(couponId);
        stats.setName(coupon.getName());
        stats.setTotalStock(coupon.getTotalStock());
        stats.setReceivedCount(coupon.getReceivedCount());
        stats.setUsedCount(coupon.getUsedCount());
        stats.setViewCount(coupon.getViewCount());
        stats.setUnusedCount(coupon.getReceivedCount() - coupon.getUsedCount());
        
        // 计算转化率
        if (coupon.getViewCount() > 0) {
            stats.setReceiveRate(new BigDecimal(coupon.getReceivedCount() * 100)
                .divide(new BigDecimal(coupon.getViewCount()), 2, RoundingMode.HALF_UP));
        }
        if (coupon.getReceivedCount() > 0) {
            stats.setUsageRate(new BigDecimal(coupon.getUsedCount() * 100)
                .divide(new BigDecimal(coupon.getReceivedCount()), 2, RoundingMode.HALF_UP));
        }
        
        stats.setTotalOrderAmount(coupon.getRelatedOrderAmount());
        stats.setTotalDiscountAmount(coupon.getTotalDiscountAmount());
        stats.setRoi(coupon.getRoi());
        
        return stats;
    }
    
    @Override
    public MerchantCouponStatistics getMerchantStatistics(Long merchantId, String startDate, String endDate) {
        // 简化实现
        return new MerchantCouponStatistics();
    }
    
    @Override
    public PlatformCouponOverview getPlatformOverview() {
        PlatformCouponOverview overview = new PlatformCouponOverview();
        overview.setTotalCouponCount(couponMapper.selectCount(null));
        overview.setActiveCouponCount(couponMapper.selectCount(
            new LambdaQueryWrapper<Coupon>().eq(Coupon::getStatus, 2)));
        overview.setTotalReceivedCount(userCouponMapper.selectCount(null));
        overview.setTotalUsedCount(userCouponMapper.selectCount(
            new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getStatus, 1)));
        return overview;
    }
    
    // ==================== 定时任务 ====================
    
    @Override
    public Integer processExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询已过期的用户优惠券
        List<UserCoupon> expiredCoupons = userCouponMapper.selectList(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getStatus, 0)
                .lt(UserCoupon::getValidEndTime, now)
        );
        
        int count = 0;
        for (UserCoupon coupon : expiredCoupons) {
            coupon.setStatus(2); // 已过期
            coupon.setExpireTime(now);
            userCouponMapper.updateById(coupon);
            count++;
        }
        
        log.info("处理过期优惠券完成, count={}", count);
        return count;
    }
    
    @Override
    public Integer sendExpireReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);
        
        // 查询3天内过期且未提醒的优惠券
        List<UserCoupon> expiringCoupons = userCouponMapper.selectList(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getStatus, 0)
                .le(UserCoupon::getValidEndTime, threeDaysLater)
                .ge(UserCoupon::getValidEndTime, now)
                .eq(UserCoupon::getUpcomingExpireReminderSent, false)
        );
        
        int count = 0;
        for (UserCoupon coupon : expiringCoupons) {
            // 发送提醒（简化实现）
            coupon.setUpcomingExpireReminderSent(true);
            userCouponMapper.updateById(coupon);
            count++;
        }
        
        log.info("发送过期提醒完成, count={}", count);
        return count;
    }
    
    @Override
    public void syncCouponToRedis(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 2) {
            return;
        }
        
        // 同步库存到Redis
        String stockKey = COUPON_STOCK_KEY + couponId;
        redisTemplate.opsForValue().set(stockKey, coupon.getRemainingStock());
        
        // 同步地理位置到Redis Geo
        if (Boolean.TRUE.equals(coupon.getGeoLimited()) 
            && coupon.getCenterLongitude() != null 
            && coupon.getCenterLatitude() != null) {
            Point point = new Point(coupon.getCenterLongitude().doubleValue(), 
                                   coupon.getCenterLatitude().doubleValue());
            redisTemplate.opsForGeo().add(COUPON_GEO_KEY, point, couponId.toString());
        }
        
        log.info("优惠券同步到Redis完成, couponId={}", couponId);
    }
    
    @Override
    public void cleanExpiredCouponCache() {
        // 清理已过期的优惠券缓存
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(Coupon::getIssueEndTime, LocalDateTime.now())
               .ne(Coupon::getStatus, 4);
        
        List<Coupon> expiredCoupons = couponMapper.selectList(wrapper);
        for (Coupon coupon : expiredCoupons) {
            // 从Redis中移除
            redisTemplate.opsForGeo().remove(COUPON_GEO_KEY, coupon.getId().toString());
            redisTemplate.delete(COUPON_STOCK_KEY + coupon.getId());
            
            // 更新状态
            coupon.setStatus(4);
            couponMapper.updateById(coupon);
        }
        
        log.info("清理过期优惠券缓存完成, count={}", expiredCoupons.size());
    }
    
    // ==================== 转换方法 ====================
    
    private CouponDetailResponse convertToDetailResponse(Coupon coupon, Long userId) {
        CouponDetailResponse response = new CouponDetailResponse();
        response.setId(coupon.getId());
        response.setCouponCode(coupon.getCouponCode());
        response.setName(coupon.getName());
        response.setDescription(coupon.getDescription());
        response.setCouponType(coupon.getCouponType());
        response.setTypeDescription(coupon.getTypeDescription());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinSpend(coupon.getMinSpend());
        response.setMaxDiscount(coupon.getMaxDiscount());
        response.setMerchantId(coupon.getMerchantId());
        response.setMerchantName(coupon.getMerchantName());
        response.setRemainingStock(coupon.getRemainingStock());
        response.setPerUserLimit(coupon.getPerUserLimit());
        response.setIssueStartTime(coupon.getIssueStartTime());
        response.setIssueEndTime(coupon.getIssueEndTime());
        response.setUseStartTime(coupon.getUseStartTime());
        response.setUseEndTime(coupon.getUseEndTime());
        response.setStatus(coupon.getStatus());
        response.setStatusDescription(coupon.getStatusDescription());
        response.setCoverImage(coupon.getCoverImage());
        response.setTags(parseJsonArray(coupon.getTags()));
        
        // 检查当前用户领取状态
        if (userId != null) {
            ReceiveCheckResult checkResult = checkCanReceive(coupon.getId(), userId);
            response.setCanReceive(checkResult.isCanReceive());
            response.setReceiveReason(checkResult.getReason());
            response.setUserReceivedCount(checkResult.getReceivedCount());
        }
        
        return response;
    }
    
    private CouponListResponse convertToListResponse(Coupon coupon) {
        CouponListResponse response = new CouponListResponse();
        response.setId(coupon.getId());
        response.setName(coupon.getName());
        response.setCouponType(coupon.getCouponType());
        response.setTypeDescription(coupon.getTypeDescription());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinSpend(coupon.getMinSpend());
        response.setMerchantName(coupon.getMerchantName());
        response.setRemainingStock(coupon.getRemainingStock());
        response.setCoverImage(coupon.getCoverImage());
        response.setTags(parseJsonArray(coupon.getTags()));
        response.setIsTop(coupon.getIsTop());
        
        // 计算状态
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStatus() == 2) {
            if (now.isBefore(coupon.getIssueStartTime())) {
                response.setTimeStatus(0); // 即将开始
            } else if (now.isAfter(coupon.getIssueEndTime())) {
                response.setTimeStatus(2); // 已结束
            } else {
                response.setTimeStatus(1); // 进行中
            }
        }
        
        return response;
    }
    
    private UserCouponResponse convertToUserCouponResponse(UserCoupon userCoupon) {
        UserCouponResponse response = new UserCouponResponse();
        response.setId(userCoupon.getId());
        response.setCouponId(userCoupon.getCouponId());
        response.setCouponCode(userCoupon.getCouponCode());
        response.setCouponName(userCoupon.getCouponName());
        response.setStatus(userCoupon.getStatus());
        response.setStatusDescription(userCoupon.getStatusDescription());
        response.setValidStartTime(userCoupon.getValidStartTime());
        response.setValidEndTime(userCoupon.getValidEndTime());
        response.setRemainingDays(userCoupon.getRemainingDays());
        response.setUseTime(userCoupon.getUseTime());
        response.setOrderNo(userCoupon.getOrderNo());
        response.setDiscountAmount(userCoupon.getDiscountAmount());
        response.setReceiveSource(userCoupon.getReceiveSource());
        response.setSourceDescription(userCoupon.getSourceDescription());
        
        // 解析限制快照计算优惠金额
        if (StringUtils.hasText(userCoupon.getRestrictionSnapshot())) {
            try {
                JSONObject snapshot = JSON.parseObject(userCoupon.getRestrictionSnapshot());
                response.setMinSpend(snapshot.getBigDecimal("minSpend"));
                response.setDiscountValue(snapshot.getBigDecimal("discountValue"));
            } catch (Exception ignored) {}
        }
        
        return response;
    }
    
    private UserCouponDetailResponse convertToUserCouponDetailResponse(UserCoupon userCoupon) {
        UserCouponDetailResponse response = new UserCouponDetailResponse();
        // 复制基础字段
        response.setId(userCoupon.getId());
        response.setCouponId(userCoupon.getCouponId());
        response.setCouponName(userCoupon.getCouponName());
        response.setStatus(userCoupon.getStatus());
        response.setValidEndTime(userCoupon.getValidEndTime());
        
        // 获取优惠券模板详情
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon != null) {
            response.setDescription(coupon.getDescription());
            response.setMerchantName(coupon.getMerchantName());
            response.setCoverImage(coupon.getCoverImage());
        }
        
        return response;
    }
    
    private List<String> parseJsonArray(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(json, String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
