package com.im.dto.usergrowth;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户积分信息响应DTO
 */
@Data
public class UserPointsInfoResponseDTO {
    
    /** 用户ID */
    private Long userId;
    
    /** 积分账户信息 */
    private PointsAccountInfo accountInfo;
    
    /** 今日签到状态 */
    private SignInStatus signInStatus;
    
    /** 积分等级信息 */
    private PointsLevelInfo pointsLevelInfo;
    
    /** 最近流水 */
    private List<RecentTransaction> recentTransactions;
    
    /**
     * 积分账户信息
     */
    @Data
    public static class PointsAccountInfo {
        private Long availablePoints;
        private Long totalEarnedPoints;
        private Long totalSpentPoints;
        private Long frozenPoints;
        private Long expiringSoonPoints;
        private LocalDateTime expiringSoonDate;
    }
    
    /**
     * 签到状态
     */
    @Data
    public static class SignInStatus {
        private Boolean todaySigned;
        private Integer consecutiveDays;
        private Integer totalSignDays;
        private Long todayReward;
        private Long tomorrowReward;
    }
    
    /**
     * 积分等级信息
     */
    @Data
    public static class PointsLevelInfo {
        private String level;
        private String levelName;
        private Long pointsToNextLevel;
        private Double progressPercent;
    }
    
    /**
     * 最近交易记录
     */
    @Data
    public static class RecentTransaction {
        private String transactionType;
        private Long points;
        private String sourceType;
        private String sourceDesc;
        private LocalDateTime transactionTime;
    }
}
