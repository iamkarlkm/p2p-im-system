package com.im.backend.service.impl;

import com.im.backend.dto.poi.*;
import com.im.backend.model.enums.PointTransactionType;
import com.im.backend.model.enums.UserLevel;
import com.im.backend.model.poi.*;
import com.im.backend.service.UserPointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户积分服务实现
 */
@Service
public class UserPointServiceImpl implements UserPointService {

    // 模拟数据存储
    private final Map<Long, UserPointAccount> accounts = new HashMap<>();
    private final Map<Long, List<PointTransactionRecord>> transactions = new HashMap<>();
    private final Map<Long, List<CheckinAchievement>> achievements = new HashMap<>();
    private long transactionId = 1;

    @Override
    public UserPointAccountResponse getUserPointAccount(Long userId) {
        UserPointAccount account = accounts.get(userId);
        if (account == null) {
            initUserPointAccount(userId);
            account = accounts.get(userId);
        }
        
        return convertToResponse(account);
    }

    @Override
    public Boolean addPoints(Long userId, Integer points, String transactionType, String description, String relatedId) {
        UserPointAccount account = accounts.get(userId);
        if (account == null) {
            initUserPointAccount(userId);
            account = accounts.get(userId);
        }
        
        int balanceBefore = account.getAvailablePoints();
        account.setTotalPoints(account.getTotalPoints() + points);
        account.setAvailablePoints(account.getAvailablePoints() + points);
        account.setLevelPoints(account.getLevelPoints() + points);
        account.setUpdatedAt(LocalDateTime.now());
        
        // 检查等级提升
        checkLevelUp(account);
        
        // 记录交易
        PointTransactionRecord record = new PointTransactionRecord();
        record.setId(transactionId++);
        record.setUserId(userId);
        record.setTransactionType(transactionType);
        record.setPoints(points);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(account.getAvailablePoints());
        record.setRelatedId(relatedId);
        record.setDescription(description);
        record.setCreatedAt(LocalDateTime.now());
        
        transactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
        
        return true;
    }

    @Override
    public Boolean consumePoints(Long userId, Integer points, String description, String relatedId) {
        UserPointAccount account = accounts.get(userId);
        if (account == null || account.getAvailablePoints() < points) {
            return false;
        }
        
        int balanceBefore = account.getAvailablePoints();
        account.setAvailablePoints(account.getAvailablePoints() - points);
        account.setConsumedPoints(account.getConsumedPoints() + points);
        account.setUpdatedAt(LocalDateTime.now());
        
        // 记录交易
        PointTransactionRecord record = new PointTransactionRecord();
        record.setId(transactionId++);
        record.setUserId(userId);
        record.setTransactionType(PointTransactionType.CONSUME.name());
        record.setPoints(-points);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(account.getAvailablePoints());
        record.setRelatedId(relatedId);
        record.setDescription(description);
        record.setCreatedAt(LocalDateTime.now());
        
        transactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
        
        return true;
    }

    @Override
    public List<PointTransactionDTO> getTransactionRecords(Long userId, int page, int size) {
        List<PointTransactionRecord> records = transactions.getOrDefault(userId, new ArrayList<>());
        records.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        int start = page * size;
        int end = Math.min(start + size, records.size());
        if (start >= records.size()) {
            return new ArrayList<>();
        }
        
        List<PointTransactionDTO> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            result.add(convertToTransactionDTO(records.get(i)));
        }
        return result;
    }

    @Override
    public UserLevelInfoDTO getUserLevelInfo(Long userId) {
        UserPointAccount account = accounts.get(userId);
        if (account == null) {
            initUserPointAccount(userId);
            account = accounts.get(userId);
        }
        
        UserLevel currentLevel = UserLevel.valueOf(account.getCurrentLevel());
        UserLevel nextLevel = getNextLevel(currentLevel);
        
        UserLevelInfoDTO dto = new UserLevelInfoDTO();
        dto.setCurrentLevel(currentLevel.name());
        dto.setCurrentLevelName(currentLevel.getName());
        dto.setCurrentPoints(account.getLevelPoints());
        dto.setMinPoints(currentLevel.getMinPoints());
        dto.setMaxPoints(currentLevel.getMaxPoints());
        dto.setCheckinBonus(currentLevel.getCheckinBonus());
        
        if (nextLevel != null) {
            double progress = (double) (account.getLevelPoints() - currentLevel.getMinPoints()) 
                    / (nextLevel.getMinPoints() - currentLevel.getMinPoints()) * 100;
            dto.setProgress(Math.min(progress, 100.0));
            dto.setPointsToNextLevel(nextLevel.getMinPoints() - account.getLevelPoints());
            dto.setNextLevelName(nextLevel.getName());
        } else {
            dto.setProgress(100.0);
            dto.setPointsToNextLevel(0);
            dto.setNextLevelName("已达最高等级");
        }
        
        return dto;
    }

    @Override
    public List<LevelRuleDTO> getAllLevelRules() {
        List<LevelRuleDTO> rules = new ArrayList<>();
        for (UserLevel level : UserLevel.values()) {
            LevelRuleDTO dto = new LevelRuleDTO();
            dto.setLevelCode(level.name());
            dto.setLevelName(level.getName());
            dto.setMinPoints(level.getMinPoints());
            dto.setMaxPoints(level.getMaxPoints());
            dto.setCheckinBonus(level.getCheckinBonus());
            dto.setLevelOrder(level.ordinal());
            rules.add(dto);
        }
        return rules;
    }

    @Override
    public List<AchievementDTO> getUserAchievements(Long userId) {
        List<CheckinAchievement> userAchievements = achievements.getOrDefault(userId, new ArrayList<>());
        List<AchievementDTO> result = new ArrayList<>();
        for (CheckinAchievement achievement : userAchievements) {
            result.add(convertToAchievementDTO(achievement));
        }
        return result;
    }

    @Override
    public void initUserPointAccount(Long userId) {
        if (!accounts.containsKey(userId)) {
            UserPointAccount account = new UserPointAccount();
            account.setId((long) (accounts.size() + 1));
            account.setUserId(userId);
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setConsumedPoints(0);
            account.setFrozenPoints(0);
            account.setCurrentLevel(UserLevel.BRONZE.name());
            account.setLevelPoints(0);
            account.setTotalCheckins(0);
            account.setConsecutiveCheckins(0);
            account.setStreakDays(0);
            account.setMaxStreakDays(0);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            accounts.put(userId, account);
        }
    }

    // 辅助方法
    private void checkLevelUp(UserPointAccount account) {
        UserLevel newLevel = UserLevel.fromPoints(account.getLevelPoints());
        UserLevel currentLevel = UserLevel.valueOf(account.getCurrentLevel());
        
        if (newLevel.ordinal() > currentLevel.ordinal()) {
            account.setCurrentLevel(newLevel.name());
        }
    }

    private UserLevel getNextLevel(UserLevel currentLevel) {
        UserLevel[] levels = UserLevel.values();
        int nextIndex = currentLevel.ordinal() + 1;
        if (nextIndex < levels.length) {
            return levels[nextIndex];
        }
        return null;
    }

    private UserPointAccountResponse convertToResponse(UserPointAccount account) {
        UserPointAccountResponse response = new UserPointAccountResponse();
        response.setUserId(account.getUserId());
        response.setTotalPoints(account.getTotalPoints());
        response.setAvailablePoints(account.getAvailablePoints());
        response.setConsumedPoints(account.getConsumedPoints());
        response.setCurrentLevel(account.getCurrentLevel());
        response.setCurrentLevelName(UserLevel.valueOf(account.getCurrentLevel()).getName());
        response.setLevelPoints(account.getLevelPoints());
        response.setTotalCheckins(account.getTotalCheckins());
        response.setConsecutiveCheckins(account.getConsecutiveCheckins());
        response.setStreakDays(account.getStreakDays());
        response.setMaxStreakDays(account.getMaxStreakDays());
        response.setLastCheckinTime(account.getLastCheckinTime());
        
        UserLevel currentLevel = UserLevel.valueOf(account.getCurrentLevel());
        UserLevel nextLevel = getNextLevel(currentLevel);
        if (nextLevel != null) {
            response.setNextLevelProgress((double) (account.getLevelPoints() - currentLevel.getMinPoints()) 
                    / (nextLevel.getMinPoints() - currentLevel.getMinPoints()) * 100);
            response.setPointsToNextLevel(nextLevel.getMinPoints() - account.getLevelPoints());
        }
        
        return response;
    }

    private PointTransactionDTO convertToTransactionDTO(PointTransactionRecord record) {
        PointTransactionDTO dto = new PointTransactionDTO();
        dto.setId(record.getId());
        dto.setTransactionType(record.getTransactionType());
        try {
            dto.setTransactionTypeName(PointTransactionType.valueOf(record.getTransactionType()).getDescription());
        } catch (Exception e) {
            dto.setTransactionTypeName(record.getTransactionType());
        }
        dto.setPoints(record.getPoints());
        dto.setBalanceAfter(record.getBalanceAfter());
        dto.setDescription(record.getDescription());
        dto.setSourceType(record.getSourceType());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }

    private AchievementDTO convertToAchievementDTO(CheckinAchievement achievement) {
        AchievementDTO dto = new AchievementDTO();
        dto.setAchievementCode(achievement.getAchievementCode());
        dto.setAchievementName(achievement.getAchievementName());
        dto.setDescription(achievement.getDescription());
        dto.setIconUrl(achievement.getIconUrl());
        dto.setRarity(achievement.getRarity());
        dto.setPointsReward(achievement.getPointsReward());
        dto.setUnlockedAt(achievement.getUnlockedAt());
        return dto;
    }
}
