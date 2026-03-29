package com.im.backend.modules.local.service.impl;

import com.im.backend.modules.local.dto.UserGrowthRequest;
import com.im.backend.modules.local.dto.UserGrowthResponse;
import com.im.backend.modules.local.service.UserGrowthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户成长体系服务实现类
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
public class UserGrowthServiceImpl implements UserGrowthService {

    // 用户经验值缓存
    private final Map<String, Long> userExpCache = new ConcurrentHashMap<>();
    
    // 等级配置
    private static final List<LevelConfig> LEVEL_CONFIGS = new ArrayList<>();
    
    // 行为经验值配置
    private static final Map<String, Integer> ACTION_EXP_MAP = new HashMap<>();
    
    static {
        // 初始化等级配置
        LEVEL_CONFIGS.add(createLevelConfig(1, "新手", 0L, 100L, "新手特权"));
        LEVEL_CONFIGS.add(createLevelConfig(2, "探索者", 100L, 500L, "探索者特权"));
        LEVEL_CONFIGS.add(createLevelConfig(3, "达人", 500L, 1500L, "达人特权"));
        LEVEL_CONFIGS.add(createLevelConfig(4, "专家", 1500L, 5000L, "专家特权"));
        LEVEL_CONFIGS.add(createLevelConfig(5, "大师", 5000L, 15000L, "大师特权"));
        LEVEL_CONFIGS.add(createLevelConfig(6, "传奇", 15000L, 50000L, "传奇特权"));
        LEVEL_CONFIGS.add(createLevelConfig(7, "神话", 50000L, Long.MAX_VALUE, "神话特权"));
        
        // 初始化行为经验值
        ACTION_EXP_MAP.put("browse", 5);
        ACTION_EXP_MAP.put("search", 10);
        ACTION_EXP_MAP.put("checkin", 20);
        ACTION_EXP_MAP.put("review", 50);
        ACTION_EXP_MAP.put("share", 30);
        ACTION_EXP_MAP.put("consume", 100);
    }
    
    private static LevelConfig createLevelConfig(int level, String name, long minExp, long maxExp, String benefit) {
        LevelConfig config = new LevelConfig();
        config.setLevel(level);
        config.setName(name);
        config.setMinExp(minExp);
        config.setMaxExp(maxExp);
        config.setBenefits(Collections.singletonList(benefit));
        return config;
    }

    @Override
    public UserGrowthResponse recordAction(UserGrowthRequest request) {
        String userId = request.getUserId();
        String actionType = request.getActionType();
        
        log.info("Recording action for user: {}, action: {}", userId, actionType);
        
        // 获取当前经验值
        long currentExp = userExpCache.getOrDefault(userId, 0L);
        
        // 计算获得的经验值
        int gainedExp = ACTION_EXP_MAP.getOrDefault(actionType, 10);
        if (request.getActionValue() != null && request.getActionValue() > 0) {
            gainedExp = gainedExp * request.getActionValue() / 100;
        }
        
        // 更新经验值
        long newExp = currentExp + gainedExp;
        userExpCache.put(userId, newExp);
        
        // 获取当前等级
        int oldLevel = calculateLevel(currentExp);
        int newLevel = calculateLevel(newExp);
        boolean levelUp = newLevel > oldLevel;
        
        // 构建响应
        LevelConfig currentConfig = getLevelConfig(newLevel);
        LevelConfig nextConfig = getLevelConfig(newLevel + 1);
        
        double progressPercent = nextConfig != null 
                ? (double) (newExp - currentConfig.getMinExp()) / (currentConfig.getMaxExp() - currentConfig.getMinExp()) * 100
                : 100.0;
        
        return UserGrowthResponse.builder()
                .userId(userId)
                .currentLevel(newLevel)
                .levelName(currentConfig.getName())
                .currentExp(newExp)
                .nextLevelExp(nextConfig != null ? nextConfig.getMinExp() : currentConfig.getMaxExp())
                .progressPercent(Math.min(progressPercent, 100.0))
                .gainedExp((long) gainedExp)
                .levelUp(levelUp)
                .newLevel(levelUp ? newLevel : null)
                .newLevelName(levelUp ? currentConfig.getName() : null)
                .benefits(getBenefitsByLevel(newLevel))
                .nextLevelBenefits(nextConfig != null ? getBenefitsByLevel(newLevel + 1) : null)
                .build();
    }

    @Override
    public UserGrowthResponse getUserLevel(String userId) {
        long currentExp = userExpCache.getOrDefault(userId, 0L);
        int level = calculateLevel(currentExp);
        
        LevelConfig currentConfig = getLevelConfig(level);
        LevelConfig nextConfig = getLevelConfig(level + 1);
        
        double progressPercent = nextConfig != null 
                ? (double) (currentExp - currentConfig.getMinExp()) / (currentConfig.getMaxExp() - currentConfig.getMinExp()) * 100
                : 100.0;
        
        return UserGrowthResponse.builder()
                .userId(userId)
                .currentLevel(level)
                .levelName(currentConfig.getName())
                .currentExp(currentExp)
                .nextLevelExp(nextConfig != null ? nextConfig.getMinExp() : currentConfig.getMaxExp())
                .progressPercent(Math.min(progressPercent, 100.0))
                .gainedExp(0L)
                .levelUp(false)
                .benefits(getBenefitsByLevel(level))
                .nextLevelBenefits(nextConfig != null ? getBenefitsByLevel(level + 1) : null)
                .build();
    }

    @Override
    public List<LevelConfig> getLevelList() {
        return new ArrayList<>(LEVEL_CONFIGS);
    }

    @Override
    public List<UserGrowthResponse.LevelBenefit> getUserBenefits(String userId) {
        long currentExp = userExpCache.getOrDefault(userId, 0L);
        int level = calculateLevel(currentExp);
        return getBenefitsByLevel(level);
    }

    @Override
    public boolean claimBenefit(String userId, String benefitType) {
        log.info("User {} claiming benefit: {}", userId, benefitType);
        return true;
    }
    
    private int calculateLevel(long exp) {
        for (LevelConfig config : LEVEL_CONFIGS) {
            if (exp >= config.getMinExp() && exp < config.getMaxExp()) {
                return config.getLevel();
            }
        }
        return LEVEL_CONFIGS.get(LEVEL_CONFIGS.size() - 1).getLevel();
    }
    
    private LevelConfig getLevelConfig(int level) {
        for (LevelConfig config : LEVEL_CONFIGS) {
            if (config.getLevel() == level) {
                return config;
            }
        }
        return null;
    }
    
    private List<UserGrowthResponse.LevelBenefit> getBenefitsByLevel(int level) {
        List<UserGrowthResponse.LevelBenefit> benefits = new ArrayList<>();
        
        benefits.add(UserGrowthResponse.LevelBenefit.builder()
                .type("coupon")
                .name("等级专属券")
                .description("每月" + level + "张优惠券")
                .icon("coupon_icon")
                .build());
        
        benefits.add(UserGrowthResponse.LevelBenefit.builder()
                .type("priority")
                .name("优先服务")
                .description("享受优先客服通道")
                .icon("priority_icon")
                .build());
        
        if (level >= 3) {
            benefits.add(UserGrowthResponse.LevelBenefit.builder()
                    .type("exclusive")
                    .name("专属活动")
                    .description("专享高等级用户活动")
                    .icon("exclusive_icon")
                    .build());
        }
        
        return benefits;
    }
}
