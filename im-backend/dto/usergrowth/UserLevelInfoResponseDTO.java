package com.im.dto.usergrowth;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 用户等级信息响应DTO
 */
@Data
public class UserLevelInfoResponseDTO {
    
    /** 用户ID */
    private Long userId;
    
    /** 当前等级信息 */
    private LevelInfo currentLevel;
    
    /** 下一等级信息 */
    private LevelInfo nextLevel;
    
    /** 等级进度 */
    private LevelProgress progress;
    
    /** 当前等级特权列表 */
    private List<PrivilegeInfo> privileges;
    
    /** 保级信息 */
    private RetainInfo retainInfo;
    
    /** 历史最高等级 */
    private String highestLevelCode;
    
    /** 升级次数 */
    private Integer upgradeCount;
    
    /** 降级次数 */
    private Integer downgradeCount;
    
    /**
     * 等级信息
     */
    @Data
    public static class LevelInfo {
        private String levelCode;
        private String levelName;
        private String levelIcon;
        private String levelColor;
        private Integer levelOrder;
        private Long minGrowthValue;
        private Long maxGrowthValue;
    }
    
    /**
     * 等级进度
     */
    @Data
    public static class LevelProgress {
        private Long currentGrowthValue;
        private Long needGrowthValue;
        private Double progressPercent;
        private String progressText;
    }
    
    /**
     * 特权信息
     */
    @Data
    public static class PrivilegeInfo {
        private String privilegeType;
        private String privilegeName;
        private String privilegeIcon;
        private String privilegeDesc;
        private String privilegeValue;
        private Integer dailyLimit;
        private Integer monthlyLimit;
    }
    
    /**
     * 保级信息
     */
    @Data
    public static class RetainInfo {
        private Boolean needRetain;
        private Long daysUntilDeadline;
        private Long retainMinGrowthValue;
        private Long currentYearGrowthValue;
        private Boolean retainSuccess;
        private Integer consecutiveRetainCount;
    }
}
