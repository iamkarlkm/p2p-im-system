package com.im.backend.service;

import com.im.backend.dto.poi.*;
import java.util.List;

/**
 * 用户积分服务接口
 */
public interface UserPointService {
    
    /**
     * 获取用户积分账户信息
     */
    UserPointAccountResponse getUserPointAccount(Long userId);
    
    /**
     * 增加用户积分
     */
    Boolean addPoints(Long userId, Integer points, String transactionType, String description, String relatedId);
    
    /**
     * 消费用户积分
     */
    Boolean consumePoints(Long userId, Integer points, String description, String relatedId);
    
    /**
     * 获取积分交易记录
     */
    List<PointTransactionDTO> getTransactionRecords(Long userId, int page, int size);
    
    /**
     * 获取用户等级信息
     */
    UserLevelInfoDTO getUserLevelInfo(Long userId);
    
    /**
     * 获取所有等级规则
     */
    List<LevelRuleDTO> getAllLevelRules();
    
    /**
     * 获取用户成就徽章列表
     */
    List<AchievementDTO> getUserAchievements(Long userId);
    
    /**
     * 初始化用户积分账户
     */
    void initUserPointAccount(Long userId);
}
