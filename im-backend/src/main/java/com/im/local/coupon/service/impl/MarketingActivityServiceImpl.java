package com.im.local.coupon.service.impl;

import com.im.local.coupon.entity.*;
import com.im.local.coupon.enums.ActivityStatus;
import com.im.local.coupon.enums.BargainStatus;
import com.im.local.coupon.enums.GroupBuyingStatus;
import com.im.local.coupon.repository.*;
import com.im.local.coupon.service.IMarketingActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 营销活动服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingActivityServiceImpl implements IMarketingActivityService {

    private final MarketingActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createActivity(MarketingActivity activity, Long createBy) {
        activity.setStatus(ActivityStatus.DRAFT.getCode());
        activity.setSoldQuantity(0);
        activity.setViewCount(0);
        activity.setShareCount(0);
        activity.setDeleted(0);
        activity.setCreateBy(createBy);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());

        activityMapper.insert(activity);
        log.info("创建营销活动成功: id={}, name={}", activity.getId(), activity.getName());
        return activity.getId();
    }

    @Override
    public MarketingActivity getActivityDetail(Long activityId) {
        return activityMapper.selectById(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivityParticipation participateActivity(Long activityId, Long userId, Integer participateType) {
        MarketingActivity activity = activityMapper.selectById(activityId);
        if (activity == null || !ActivityStatus.IN_PROGRESS.getCode().equals(activity.getStatus())) {
            throw new RuntimeException("活动不在进行中");
        }

        activityMapper.incrementSold(activityId, 1);

        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participation.setParticipateType(participateType);
        participation.setStatus(1);
        participation.setParticipateTime(LocalDateTime.now());
        participation.setCreateTime(LocalDateTime.now());

        log.info("用户参与活动: userId={}, activityId={}", userId, activityId);
        return participation;
    }

    @Override
    public List<MarketingActivity> getActiveActivities() {
        return activityMapper.selectActiveActivities();
    }

    @Override
    public List<MarketingActivity> getMerchantActivities(Long merchantId) {
        return activityMapper.selectByMerchantId(merchantId);
    }

    @Override
    public GroupBuying createGroupBuying(Long activityId, Long leaderId) {
        GroupBuying group = new GroupBuying();
        group.setActivityId(activityId);
        group.setLeaderId(leaderId);
        group.setRequiredMembers(3);
        group.setCurrentMembers(1);
        group.setStatus(GroupBuyingStatus.IN_PROGRESS.getCode());
        group.setValidHours(24);
        group.setStartTime(LocalDateTime.now());
        group.setExpireTime(LocalDateTime.now().plusHours(24));
        group.setGroupPrice(new BigDecimal("99.00"));
        group.setCreateTime(LocalDateTime.now());

        log.info("创建拼团: leaderId={}, activityId={}", leaderId, activityId);
        return group;
    }

    @Override
    public boolean joinGroupBuying(Long groupId, Long userId) {
        log.info("用户加入拼团: userId={}, groupId={}", userId, groupId);
        return true;
    }

    @Override
    public BargainActivity startBargain(Long activityId, Long userId) {
        BargainActivity bargain = new BargainActivity();
        bargain.setActivityId(activityId);
        bargain.setUserId(userId);
        bargain.setOriginalPrice(new BigDecimal("199.00"));
        bargain.setFloorPrice(new BigDecimal("99.00"));
        bargain.setCurrentPrice(new BigDecimal("199.00"));
        bargain.setBargainedAmount(BigDecimal.ZERO);
        bargain.setStatus(BargainStatus.IN_PROGRESS.getCode());
        bargain.setHelperCount(0);
        bargain.setValidHours(24);
        bargain.setStartTime(LocalDateTime.now());
        bargain.setExpireTime(LocalDateTime.now().plusHours(24));
        bargain.setCreateTime(LocalDateTime.now());

        log.info("发起砍价: userId={}, activityId={}", userId, activityId);
        return bargain;
    }

    @Override
    public BargainHelpRecord helpBargain(Long bargainId, Long helperId) {
        BigDecimal helpAmount = new BigDecimal(new Random().nextInt(10) + 5);

        BargainHelpRecord record = new BargainHelpRecord();
        record.setBargainId(bargainId);
        record.setHelperId(helperId);
        record.setBargainAmount(helpAmount);
        record.setIsNewUser(0);
        record.setHelpTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());

        log.info("用户帮砍: helperId={}, bargainId={}, amount={}", helperId, bargainId, helpAmount);
        return record;
    }

    @Override
    public GroupBuying getGroupBuyingDetail(Long groupId) {
        return new GroupBuying();
    }

    @Override
    public BargainActivity getBargainDetail(Long bargainId) {
        return new BargainActivity();
    }
}
