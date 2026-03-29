package com.im.service.usergrowth;

import com.im.entity.usergrowth.*;
import java.util.List;
import java.util.Map;

/**
 * 用户积分服务接口
 */
public interface UserPointsService {
    
    /**
     * 获取用户积分账户
     */
    UserPointsAccount getUserPointsAccount(Long userId);
    
    /**
     * 增加积分
     */
    boolean addPoints(Long userId, Long points, String sourceType, String sourceDesc, String bizType, Long bizId);
    
    /**
     * 批量增加积分
     */
    boolean batchAddPoints(List<PointsAddRequest> requests);
    
    /**
     * 消耗积分
     */
    PointsDeductResult deductPoints(Long userId, Long points, String spendType, String sourceDesc, String bizType, Long bizId);
    
    /**
     * 冻结积分
     */
    boolean freezePoints(Long userId, Long points, String sourceDesc);
    
    /**
     * 解冻积分
     */
    boolean unfreezePoints(Long userId, Long points, String sourceDesc);
    
    /**
     * 确认消耗冻结积分
     */
    boolean confirmFrozenPoints(Long userId, Long points, String sourceDesc);
    
    /**
     * 查询积分是否足够
     */
    boolean hasEnoughPoints(Long userId, Long requiredPoints);
    
    /**
     * 获取积分流水
     */
    List<PointsTransactionLog> getPointsTransactionLogs(Long userId, String transactionType, Integer page, Integer size);
    
    /**
     * 获取积分来源统计
     */
    Map<String, Long> getPointsSourceStats(Long userId);
    
    /**
     * 签到
     */
    SignInResult signIn(Long userId);
    
    /**
     * 获取连续签到天数
     */
    Integer getConsecutiveSignDays(Long userId);
    
    /**
     * 获取签到日历
     */
    List<SignInCalendarItem> getSignInCalendar(Long userId, Integer year, Integer month);
    
    /**
     * 计算签到奖励
     */
    Long calculateSignInReward(Integer consecutiveDays);
    
    /**
     * 每日重置
     */
    void dailyReset();
    
    /**
     * 处理过期积分
     */
    Long processExpiredPoints();
    
    /**
     * 积分增加请求
     */
    class PointsAddRequest {
        private Long userId;
        private Long points;
        private String sourceType;
        private String sourceDesc;
        private String bizType;
        private Long bizId;
        private Integer expireDays;
        
        public PointsAddRequest(Long userId, Long points, String sourceType, String sourceDesc) {
            this.userId = userId;
            this.points = points;
            this.sourceType = sourceType;
            this.sourceDesc = sourceDesc;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getPoints() { return points; }
        public void setPoints(Long points) { this.points = points; }
        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }
        public String getSourceDesc() { return sourceDesc; }
        public void setSourceDesc(String sourceDesc) { this.sourceDesc = sourceDesc; }
        public String getBizType() { return bizType; }
        public void setBizType(String bizType) { this.bizType = bizType; }
        public Long getBizId() { return bizId; }
        public void setBizId(Long bizId) { this.bizId = bizId; }
        public Integer getExpireDays() { return expireDays; }
        public void setExpireDays(Integer expireDays) { this.expireDays = expireDays; }
    }
    
    /**
     * 积分消耗结果
     */
    class PointsDeductResult {
        private Long userId;
        private Boolean success;
        private Long deductedPoints;
        private Long remainingPoints;
        private String failReason;
        
        public PointsDeductResult(Long userId, Boolean success, Long deductedPoints, Long remainingPoints) {
            this.userId = userId;
            this.success = success;
            this.deductedPoints = deductedPoints;
            this.remainingPoints = remainingPoints;
        }
        
        public static PointsDeductResult fail(Long userId, String reason) {
            PointsDeductResult result = new PointsDeductResult(userId, false, 0L, 0L);
            result.failReason = reason;
            return result;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public Boolean getSuccess() { return success; }
        public Long getDeductedPoints() { return deductedPoints; }
        public Long getRemainingPoints() { return remainingPoints; }
        public String getFailReason() { return failReason; }
    }
    
    /**
     * 签到结果
     */
    class SignInResult {
        private Long userId;
        private Boolean success;
        private Integer consecutiveDays;
        private Long rewardPoints;
        private Long rewardGrowth;
        private String badgeCode;
        private String badgeName;
        
        public SignInResult(Long userId, Boolean success, Integer consecutiveDays, Long rewardPoints) {
            this.userId = userId;
            this.success = success;
            this.consecutiveDays = consecutiveDays;
            this.rewardPoints = rewardPoints;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public Boolean getSuccess() { return success; }
        public Integer getConsecutiveDays() { return consecutiveDays; }
        public Long getRewardPoints() { return rewardPoints; }
        public Long getRewardGrowth() { return rewardGrowth; }
        public void setRewardGrowth(Long rewardGrowth) { this.rewardGrowth = rewardGrowth; }
        public String getBadgeCode() { return badgeCode; }
        public void setBadgeCode(String badgeCode) { this.badgeCode = badgeCode; }
        public String getBadgeName() { return badgeName; }
        public void setBadgeName(String badgeName) { this.badgeName = badgeName; }
    }
    
    /**
     * 签到日历项
     */
    class SignInCalendarItem {
        private Integer day;
        private Boolean signed;
        private Long rewardPoints;
        private Boolean today;
        private Boolean future;
        
        public SignInCalendarItem(Integer day, Boolean signed, Long rewardPoints) {
            this.day = day;
            this.signed = signed;
            this.rewardPoints = rewardPoints;
        }
        
        // Getters and Setters
        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }
        public Boolean getSigned() { return signed; }
        public void setSigned(Boolean signed) { this.signed = signed; }
        public Long getRewardPoints() { return rewardPoints; }
        public void setRewardPoints(Long rewardPoints) { this.rewardPoints = rewardPoints; }
        public Boolean getToday() { return today; }
        public void setToday(Boolean today) { this.today = today; }
        public Boolean getFuture() { return future; }
        public void setFuture(Boolean future) { this.future = future; }
    }
}
