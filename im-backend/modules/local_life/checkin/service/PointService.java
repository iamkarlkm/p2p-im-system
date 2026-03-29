package com.im.backend.modules.local_life.checkin.service;

import com.im.backend.modules.local_life.checkin.dto.*;

import java.util.List;

/**
 * 积分服务接口
 */
public interface PointService {

    /**
     * 获取用户积分账户
     */
    PointAccountResponse getPointAccount(Long userId);

    /**
     * 增加积分
     */
    void addPoints(Long userId, Integer points, String pointType, Long businessId, String description);

    /**
     * 扣除积分
     */
    boolean deductPoints(Long userId, Integer points, String businessType, String description);

    /**
     * 获取积分交易记录
     */
    List<PointTransactionDTO> getTransactions(Long userId, Integer page, Integer size);

    /**
     * 计算签到积分
     */
    int calculateCheckinPoints(Long userId, boolean isFirstTimeAtPoi, int streakDays);
}
