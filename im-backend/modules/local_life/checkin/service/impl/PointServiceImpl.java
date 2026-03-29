package com.im.backend.modules.local_life.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.local_life.checkin.dto.PointAccountResponse;
import com.im.backend.modules.local_life.checkin.dto.PointTransactionDTO;
import com.im.backend.modules.local_life.checkin.entity.PointTransaction;
import com.im.backend.modules.local_life.checkin.entity.UserPointAccount;
import com.im.backend.modules.local_life.checkin.enums.PointType;
import com.im.backend.modules.local_life.checkin.enums.UserLevel;
import com.im.backend.modules.local_life.checkin.mapper.PointTransactionMapper;
import com.im.backend.modules.local_life.checkin.mapper.UserPointAccountMapper;
import com.im.backend.modules.local_life.checkin.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointAccountMapper accountMapper;
    private final PointTransactionMapper transactionMapper;

    @Override
    public PointAccountResponse getPointAccount(Long userId) {
        UserPointAccount account = getOrCreateAccount(userId);
        
        PointAccountResponse response = new PointAccountResponse();
        response.setUserId(userId);
        response.setBalance(account.getBalance());
        response.setTotalEarned(account.getTotalEarned());
        response.setTotalUsed(account.getTotalUsed());
        response.setLevel(account.getLevel());
        response.setLevelName(account.getLevelName());
        response.setStreakDays(account.getStreakDays());
        response.setMaxStreakDays(account.getMaxStreakDays());
        response.setTotalCheckinDays(account.getTotalCheckinDays());
        response.setLastCheckinDate(account.getLastCheckinDate());
        
        // 计算等级进度
        UserLevel currentLevel = UserLevel.getByPoints(account.getTotalEarned());
        response.setLevelColor(currentLevel.getColor());
        response.setCurrentLevelMinPoints(currentLevel.getMinPoints());
        
        UserLevel[] levels = UserLevel.values();
        if (currentLevel.ordinal() < levels.length - 1) {
            UserLevel nextLevel = levels[currentLevel.ordinal() + 1];
            response.setNextLevelMinPoints(nextLevel.getMinPoints());
            response.setPointsToNextLevel(nextLevel.getMinPoints() - account.getBalance());
            int progress = (account.getTotalEarned() - currentLevel.getMinPoints()) * 100 
                    / (nextLevel.getMinPoints() - currentLevel.getMinPoints());
            response.setLevelProgressPercent(Math.min(progress, 100));
        } else {
            response.setLevelProgressPercent(100);
            response.setPointsToNextLevel(0);
        }
        
        return response;
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String pointType, Long businessId, String description) {
        UserPointAccount account = getOrCreateAccount(userId);
        
        int balanceBefore = account.getBalance();
        account.setBalance(balanceBefore + points);
        account.setTotalEarned(account.getTotalEarned() + points);
        account.setTotalCheckinDays(account.getTotalCheckinDays() + 1);
        
        // 更新等级
        UserLevel newLevel = UserLevel.getByPoints(account.getTotalEarned());
        account.setLevel(newLevel.getLevel());
        account.setLevelName(newLevel.getName());
        
        accountMapper.updateById(account);
        
        // 记录交易
        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType("EARN");
        transaction.setPoints(points);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setPointType(pointType);
        transaction.setBusinessId(businessId);
        transaction.setDescription(description);
        transaction.setTransactionTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
    }

    @Override
    @Transactional
    public boolean deductPoints(Long userId, Integer points, String businessType, String description) {
        UserPointAccount account = getOrCreateAccount(userId);
        
        if (account.getBalance() < points) {
            return false;
        }
        
        int balanceBefore = account.getBalance();
        account.setBalance(balanceBefore - points);
        account.setTotalUsed(account.getTotalUsed() + points);
        accountMapper.updateById(account);
        
        // 记录交易
        PointTransaction transaction = new PointTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionType("USE");
        transaction.setPoints(-points);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setBusinessType(businessType);
        transaction.setDescription(description);
        transaction.setTransactionTime(LocalDateTime.now());
        transactionMapper.insert(transaction);
        
        return true;
    }

    @Override
    public List<PointTransactionDTO> getTransactions(Long userId, Integer page, Integer size) {
        // 简化实现
        return List.of();
    }

    @Override
    public int calculateCheckinPoints(Long userId, boolean isFirstTimeAtPoi, int streakDays) {
        int points = PointType.CHECKIN.getBasePoints();
        
        // 首次签到奖励
        if (isFirstTimeAtPoi) {
            points += PointType.FIRST_CHECKIN.getBasePoints();
        }
        
        // 连续签到奖励
        if (streakDays >= 7) {
            points += PointType.STREAK_BONUS.getBasePoints();
        }
        if (streakDays >= 30) {
            points += PointType.STREAK_BONUS.getBasePoints() * 2;
        }
        
        return points;
    }

    private UserPointAccount getOrCreateAccount(Long userId) {
        LambdaQueryWrapper<UserPointAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointAccount::getUserId, userId);
        UserPointAccount account = accountMapper.selectOne(wrapper);
        
        if (account == null) {
            account = new UserPointAccount();
            account.setUserId(userId);
            account.setBalance(0);
            account.setTotalEarned(0);
            account.setTotalUsed(0);
            account.setLevel(1);
            account.setLevelName(UserLevel.BRONZE.getName());
            account.setStreakDays(0);
            account.setMaxStreakDays(0);
            account.setTotalCheckinDays(0);
            accountMapper.insert(account);
        }
        
        return account;
    }
}
