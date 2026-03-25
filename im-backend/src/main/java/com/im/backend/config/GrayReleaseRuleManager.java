package com.im.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 灰度发布规则管理器
 * 管理灰度发布规则和动态配置
 */
@Slf4j
@Component
public class GrayReleaseRuleManager {

    private final boolean enabled;
    private final int percentage;
    private final List<String> whitelistUsers;
    
    private final Map<String, GrayReleaseRule> rules;
    private final Map<String, Object> dynamicConfig;

    public GrayReleaseRuleManager(boolean enabled, int percentage, List<String> whitelistUsers) {
        this.enabled = enabled;
        this.percentage = percentage;
        this.whitelistUsers = whitelistUsers != null ? whitelistUsers : new ArrayList<>();
        this.rules = new ConcurrentHashMap<>();
        this.dynamicConfig = new ConcurrentHashMap<>();
        initializeDefaultRules();
    }

    /**
     * 初始化默认规则
     */
    private void initializeDefaultRules() {
        // 基于用户的灰度规则
        rules.put("user-based", new GrayReleaseRule(
                "user-based",
                "基于用户 ID 的灰度规则",
                RuleType.USER_ID,
                true
        ));

        // 基于百分比的灰度规则
        rules.put("percentage-based", new GrayReleaseRule(
                "percentage-based",
                "基于百分比的灰度规则",
                RuleType.PERCENTAGE,
                true
        ));

        // 基于地区的灰度规则
        rules.put("region-based", new GrayReleaseRule(
                "region-based",
                "基于地区的灰度规则",
                RuleType.REGION,
                false
        ));

        // 基于版本的灰度规则
        rules.put("version-based", new GrayReleaseRule(
                "version-based",
                "基于客户端版本的灰度规则",
                RuleType.VERSION,
                false
        ));
    }

    /**
     * 添加灰度规则
     */
    public void addRule(GrayReleaseRule rule) {
        rules.put(rule.getName(), rule);
        log.info("Added gray release rule: {}", rule.getName());
    }

    /**
     * 移除灰度规则
     */
    public void removeRule(String ruleName) {
        rules.remove(ruleName);
        log.info("Removed gray release rule: {}", ruleName);
    }

    /**
     * 启用/禁用规则
     */
    public void toggleRule(String ruleName, boolean enabled) {
        GrayReleaseRule rule = rules.get(ruleName);
        if (rule != null) {
            rule.setEnabled(enabled);
            log.info("Toggled rule {} to {}", ruleName, enabled ? "enabled" : "disabled");
        }
    }

    /**
     * 更新动态配置
     */
    public void updateConfig(String key, Object value) {
        dynamicConfig.put(key, value);
        log.debug("Updated dynamic config: {} = {}", key, value);
    }

    /**
     * 获取动态配置
     */
    public Object getConfig(String key) {
        return dynamicConfig.get(key);
    }

    /**
     * 检查请求是否应该走灰度通道
     */
    public boolean isGrayRelease(String userId, String region, String clientVersion, String ipAddress) {
        if (!enabled) {
            return false;
        }

        // 检查白名单
        if (whitelistUsers.contains(userId)) {
            return true;
        }

        // 检查活跃规则
        for (GrayReleaseRule rule : rules.values()) {
            if (rule.isEnabled() && rule.matches(userId, region, clientVersion, ipAddress, percentage)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取所有规则
     */
    public Map<String, GrayReleaseRule> getAllRules() {
        return new HashMap<>(rules);
    }

    /**
     * 获取灰度状态统计
     */
    public GrayReleaseStatus getStatus() {
        return new GrayReleaseStatus(
                enabled,
                percentage,
                rules.size(),
                whitelistUsers.size(),
                dynamicConfig.size()
        );
    }

    /**
     * 灰度发布规则类型
     */
    public enum RuleType {
        USER_ID,
        PERCENTAGE,
        REGION,
        VERSION,
        HEADER,
        CUSTOM
    }

    /**
     * 灰度发布规则
     */
    public static class GrayReleaseRule {
        private final String name;
        private final String description;
        private final RuleType type;
        private boolean enabled;
        private Map<String, Object> conditions;
        private int priority;

        public GrayReleaseRule(String name, String description, RuleType type, boolean enabled) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.enabled = enabled;
            this.conditions = new HashMap<>();
            this.priority = 0;
        }

        public boolean matches(String userId, String region, String clientVersion, 
                              String ipAddress, int percentage) {
            switch (type) {
                case USER_ID:
                    return conditions.containsKey("userIds") && 
                           ((List<?>) conditions.get("userIds")).contains(userId);
                
                case PERCENTAGE:
                    int hash = Math.abs(ipAddress.hashCode() % 100);
                    Integer threshold = (Integer) conditions.getOrDefault("percentage", percentage);
                    return hash < threshold;
                
                case REGION:
                    return conditions.containsKey("regions") && 
                           ((List<?>) conditions.get("regions")).contains(region);
                
                case VERSION:
                    String minVersion = (String) conditions.get("minVersion");
                    return minVersion != null && compareVersions(clientVersion, minVersion) >= 0;
                
                default:
                    return false;
            }
        }

        private int compareVersions(String v1, String v2) {
            if (v1 == null || v2 == null) return 0;
            String[] parts1 = v1.split("\\.");
            String[] parts2 = v2.split("\\.");
            int length = Math.max(parts1.length, parts2.length);
            
            for (int i = 0; i < length; i++) {
                int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
                int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
                if (part1 != part2) return part1 - part2;
            }
            return 0;
        }

        public void addCondition(String key, Object value) {
            conditions.put(key, value);
        }

        // Getters and Setters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public RuleType getType() { return type; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Map<String, Object> getConditions() { return conditions; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }

    /**
     * 灰度发布状态
     */
    public static class GrayReleaseStatus {
        private final boolean enabled;
        private final int percentage;
        private final int ruleCount;
        private final int whitelistSize;
        private final int configCount;

        public GrayReleaseStatus(boolean enabled, int percentage, int ruleCount, 
                                int whitelistSize, int configCount) {
            this.enabled = enabled;
            this.percentage = percentage;
            this.ruleCount = ruleCount;
            this.whitelistSize = whitelistSize;
            this.configCount = configCount;
        }

        // Getters
        public boolean isEnabled() { return enabled; }
        public int getPercentage() { return percentage; }
        public int getRuleCount() { return ruleCount; }
        public int getWhitelistSize() { return whitelistSize; }
        public int getConfigCount() { return configCount; }
    }
}
