package com.im.local.coupon.service.impl;

import com.im.local.coupon.dto.MemberLevelResponse;
import com.im.local.coupon.dto.UserMemberResponse;
import com.im.local.coupon.entity.MemberLevel;
import com.im.local.coupon.entity.PointsTransaction;
import com.im.local.coupon.entity.UserMember;
import com.im.local.coupon.enums.PointsChangeType;
import com.im.local.coupon.repository.PointsTransactionMapper;
import com.im.local.coupon.repository.UserMemberMapper;
import com.im.local.coupon.service.IMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements IMemberService {

    private final UserMemberMapper userMemberMapper;
    private final PointsTransactionMapper pointsTransactionMapper;

    @Override
    public List<MemberLevelResponse> getMemberLevels(Long merchantId) {
        return new ArrayList<>();
    }

    @Override
    public UserMemberResponse getUserMemberInfo(Long userId, Long merchantId) {
        UserMember member = userMemberMapper.selectByUserAndMerchant(userId, merchantId);
        if (member == null) {
            return null;
        }
        return convertToResponse(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long joinMember(Long userId, Long merchantId) {
        UserMember existing = userMemberMapper.selectByUserAndMerchant(userId, merchantId);
        if (existing != null) {
            return existing.getId();
        }

        UserMember member = new UserMember();
        member.setUserId(userId);
        member.setMerchantId(merchantId);
        member.setCurrentLevelId(1L);
        member.setCurrentLevel(1);
        member.setGrowthValue(0);
        member.setPointsBalance(0);
        member.setTotalPoints(0);
        member.setTotalSpend(BigDecimal.ZERO);
        member.setTotalOrders(0);
        member.setStatus(1);
        member.setJoinTime(LocalDateTime.now());
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());

        userMemberMapper.insert(member);
        log.info("用户加入会员成功: userId={}, merchantId={}", userId, merchantId);

        return member.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGrowthValue(Long userId, Long merchantId, BigDecimal amount) {
        UserMember member = userMemberMapper.selectByUserAndMerchant(userId, merchantId);
        if (member == null) {
            joinMember(userId, merchantId);
            member = userMemberMapper.selectByUserAndMerchant(userId, merchantId);
        }

        int growth = amount.divide(new BigDecimal("10"), 0, RoundingMode.DOWN).intValue();
        userMemberMapper.addGrowthAndSpend(member.getId(), growth, amount);
        log.info("增加成长值: userId={}, growth={}", userId, growth);

        addPoints(userId, merchantId, growth, 1, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, Long merchantId, Integer points, Integer sourceType, Long sourceId) {
        UserMember member = userMemberMapper.selectByUserAndMerchant(userId, merchantId);
        if (member == null) return;

        int before = member.getPointsBalance();
        userMemberMapper.addPoints(member.getId(), points);

        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setMerchantId(merchantId);
        transaction.setPoints(points);
        transaction.setBalanceBefore(before);
        transaction.setBalanceAfter(before + points);
        transaction.setChangeType(PointsChangeType.CONSUME_EARN.getCode());
        transaction.setSourceType(sourceType);
        transaction.setSourceId(sourceId);
        transaction.setCreateTime(LocalDateTime.now());

        pointsTransactionMapper.insert(transaction);
        log.info("增加积分: userId={}, points={}", userId, points);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, Long merchantId, Integer points) {
        UserMember member = userMemberMapper.selectByUserAndMerchant(userId, merchantId);
        if (member == null || member.getPointsBalance() < points) {
            return false;
        }

        int before = member.getPointsBalance();
        int updated = userMemberMapper.deductPoints(member.getId(), points);
        if (updated == 0) return false;

        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(userId);
        transaction.setMerchantId(merchantId);
        transaction.setPoints(-points);
        transaction.setBalanceBefore(before);
        transaction.setBalanceAfter(before - points);
        transaction.setChangeType(PointsChangeType.REDEEM_DEDUCT.getCode());
        transaction.setCreateTime(LocalDateTime.now());

        pointsTransactionMapper.insert(transaction);
        log.info("扣除积分: userId={}, points={}", userId, points);
        return true;
    }

    @Override
    public boolean dailySignIn(Long userId, Long merchantId) {
        addPoints(userId, merchantId, 10, 2, null);
        log.info("用户签到: userId={}", userId);
        return true;
    }

    @Override
    public List<UserMemberResponse> getUserAllMemberships(Long userId) {
        List<UserMember> members = userMemberMapper.selectByUserId(userId);
        return members.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private UserMemberResponse convertToResponse(UserMember member) {
        UserMemberResponse resp = new UserMemberResponse();
        BeanUtils.copyProperties(member, resp);
        resp.setLevelName("V" + member.getCurrentLevel());
        resp.setNeedGrowth(100 - (member.getGrowthValue() % 100));
        resp.setProgressPercent((member.getGrowthValue() % 100));
        return resp;
    }
}
