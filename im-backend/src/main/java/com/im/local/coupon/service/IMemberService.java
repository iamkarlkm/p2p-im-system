package com.im.local.coupon.service;

import com.im.local.coupon.dto.MemberLevelResponse;
import com.im.local.coupon.dto.UserMemberResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员服务接口
 */
public interface IMemberService {

    /**
     * 获取会员等级列表
     */
    List<MemberLevelResponse> getMemberLevels(Long merchantId);

    /**
     * 获取用户会员信息
     */
    UserMemberResponse getUserMemberInfo(Long userId, Long merchantId);

    /**
     * 用户加入会员
     */
    Long joinMember(Long userId, Long merchantId);

    /**
     * 增加成长值(消费后调用)
     */
    void addGrowthValue(Long userId, Long merchantId, BigDecimal amount);

    /**
     * 增加积分
     */
    void addPoints(Long userId, Long merchantId, Integer points, Integer sourceType, Long sourceId);

    /**
     * 扣除积分
     */
    boolean deductPoints(Long userId, Long merchantId, Integer points);

    /**
     * 每日签到
     */
    boolean dailySignIn(Long userId, Long merchantId);

    /**
     * 获取用户所有会员身份
     */
    List<UserMemberResponse> getUserAllMemberships(Long userId);
}
