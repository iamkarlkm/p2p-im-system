package com.im.service.usergrowth;

import com.im.entity.usergrowth.*;
import java.util.List;
import java.util.Map;

/**
 * 用户成长值服务接口
 */
public interface UserGrowthService {
    
    /**
     * 获取用户成长记录
     */
    UserGrowthRecord getUserGrowthRecord(Long userId);
    
    /**
     * 增加成长值
     */
    boolean addGrowthValue(Long userId, Long value, String sourceType, String sourceDesc, String bizType, Long bizId);
    
    /**
     * 批量增加成长值
     */
    boolean batchAddGrowthValue(List<GrowthAddRequest> requests);
    
    /**
     * 获取当前等级定义
     */
    UserLevelDefinition getCurrentLevel(Long userId);
    
    /**
     * 获取下一等级定义
     */
    UserLevelDefinition getNextLevel(Long userId);
    
    /**
     * 计算等级进度
     */
    LevelProgressDTO calculateLevelProgress(Long userId);
    
    /**
     * 手动执行等级评估
     */
    LevelEvaluationResult evaluateLevel(Long userId);
    
    /**
     * 处理等级升级
     */
    LevelUpgradeResult processLevelUpgrade(Long userId, String newLevelCode);
    
    /**
     * 处理等级降级
     */
    LevelDowngradeResult processLevelDowngrade(Long userId, String newLevelCode);
    
    /**
     * 处理等级保级
     */
    LevelRetainResult processLevelRetain(Long userId);
    
    /**
     * 获取用户等级特权
     */
    List<UserLevelDefinition.LevelPrivilege> getUserPrivileges(Long userId);
    
    /**
     * 检查用户是否有指定特权
     */
    boolean hasPrivilege(Long userId, String privilegeType);
    
    /**
     * 获取特权值
     */
    String getPrivilegeValue(Long userId, String privilegeType);
    
    /**
     * 获取所有等级定义
     */
    List<UserLevelDefinition> getAllLevelDefinitions();
    
    /**
     * 获取成长值来源统计
     */
    Map<String, Long> getGrowthSourceStats(Long userId);
    
    /**
     * 获取成长值流水
     */
    List<GrowthTransactionLog> getGrowthTransactionLogs(Long userId, Integer page, Integer size);
    
    /**
     * 每日重置任务
     */
    void dailyReset();
    
    /**
     * 每月评估任务
     */
    void monthlyEvaluation();
    
    /**
     * 成长值增加请求
     */
    class GrowthAddRequest {
        private Long userId;
        private Long value;
        private String sourceType;
        private String sourceDesc;
        private String bizType;
        private Long bizId;
        
        public GrowthAddRequest(Long userId, Long value, String sourceType, String sourceDesc) {
            this.userId = userId;
            this.value = value;
            this.sourceType = sourceType;
            this.sourceDesc = sourceDesc;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getValue() { return value; }
        public void setValue(Long value) { this.value = value; }
        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }
        public String getSourceDesc() { return sourceDesc; }
        public void setSourceDesc(String sourceDesc) { this.sourceDesc = sourceDesc; }
        public String getBizType() { return bizType; }
        public void setBizType(String bizType) { this.bizType = bizType; }
        public Long getBizId() { return bizId; }
        public void setBizId(Long bizId) { this.bizId = bizId; }
    }
    
    /**
     * 等级进度DTO
     */
    class LevelProgressDTO {
        private String currentLevelCode;
        private String currentLevelName;
        private Long currentGrowthValue;
        private Long nextLevelMinValue;
        private Long needGrowthValue;
        private Double progressPercent;
        private Integer daysUntilExpire;
        private Boolean needRetain;
        
        // Getters and Setters
        public String getCurrentLevelCode() { return currentLevelCode; }
        public void setCurrentLevelCode(String currentLevelCode) { this.currentLevelCode = currentLevelCode; }
        public String getCurrentLevelName() { return currentLevelName; }
        public void setCurrentLevelName(String currentLevelName) { this.currentLevelName = currentLevelName; }
        public Long getCurrentGrowthValue() { return currentGrowthValue; }
        public void setCurrentGrowthValue(Long currentGrowthValue) { this.currentGrowthValue = currentGrowthValue; }
        public Long getNextLevelMinValue() { return nextLevelMinValue; }
        public void setNextLevelMinValue(Long nextLevelMinValue) { this.nextLevelMinValue = nextLevelMinValue; }
        public Long getNeedGrowthValue() { return needGrowthValue; }
        public void setNeedGrowthValue(Long needGrowthValue) { this.needGrowthValue = needGrowthValue; }
        public Double getProgressPercent() { return progressPercent; }
        public void setProgressPercent(Double progressPercent) { this.progressPercent = progressPercent; }
        public Integer getDaysUntilExpire() { return daysUntilExpire; }
        public void setDaysUntilExpire(Integer daysUntilExpire) { this.daysUntilExpire = daysUntilExpire; }
        public Boolean getNeedRetain() { return needRetain; }
        public void setNeedRetain(Boolean needRetain) { this.needRetain = needRetain; }
    }
    
    /**
     * 等级评估结果
     */
    class LevelEvaluationResult {
        private Long userId;
        private String action;  // UPGRADE/DOWNGRADE/RETAIN
        private String fromLevel;
        private String toLevel;
        private Boolean success;
        private String message;
        
        public LevelEvaluationResult(Long userId, String action, String fromLevel, String toLevel, Boolean success) {
            this.userId = userId;
            this.action = action;
            this.fromLevel = fromLevel;
            this.toLevel = toLevel;
            this.success = success;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public String getAction() { return action; }
        public String getFromLevel() { return fromLevel; }
        public String getToLevel() { return toLevel; }
        public Boolean getSuccess() { return success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 等级升级结果
     */
    class LevelUpgradeResult {
        private Long userId;
        private String oldLevel;
        private String newLevel;
        private List<UserLevelDefinition.LevelPrivilege> unlockedPrivileges;
        private Boolean success;
        
        public LevelUpgradeResult(Long userId, String oldLevel, String newLevel, Boolean success) {
            this.userId = userId;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
            this.success = success;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public String getOldLevel() { return oldLevel; }
        public String getNewLevel() { return newLevel; }
        public List<UserLevelDefinition.LevelPrivilege> getUnlockedPrivileges() { return unlockedPrivileges; }
        public void setUnlockedPrivileges(List<UserLevelDefinition.LevelPrivilege> unlockedPrivileges) { this.unlockedPrivileges = unlockedPrivileges; }
        public Boolean getSuccess() { return success; }
    }
    
    /**
     * 等级降级结果
     */
    class LevelDowngradeResult {
        private Long userId;
        private String oldLevel;
        private String newLevel;
        private List<String> lostPrivileges;
        private Boolean success;
        
        public LevelDowngradeResult(Long userId, String oldLevel, String newLevel, Boolean success) {
            this.userId = userId;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
            this.success = success;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public String getOldLevel() { return oldLevel; }
        public String getNewLevel() { return newLevel; }
        public List<String> getLostPrivileges() { return lostPrivileges; }
        public void setLostPrivileges(List<String> lostPrivileges) { this.lostPrivileges = lostPrivileges; }
        public Boolean getSuccess() { return success; }
    }
    
    /**
     * 等级保级结果
     */
    class LevelRetainResult {
        private Long userId;
        private String currentLevel;
        private Boolean retainSuccess;
        private Integer consecutiveCount;
        
        public LevelRetainResult(Long userId, String currentLevel, Boolean retainSuccess) {
            this.userId = userId;
            this.currentLevel = currentLevel;
            this.retainSuccess = retainSuccess;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public String getCurrentLevel() { return currentLevel; }
        public Boolean getRetainSuccess() { return retainSuccess; }
        public Integer getConsecutiveCount() { return consecutiveCount; }
        public void setConsecutiveCount(Integer consecutiveCount) { this.consecutiveCount = consecutiveCount; }
    }
}
