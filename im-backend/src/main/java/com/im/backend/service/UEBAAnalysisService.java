package com.im.backend.service;

import com.im.backend.model.RiskScore;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * UEBA (User and Entity Behavior Analytics) 行为分析服务
 * 
 * 通过机器学习算法和统计模型分析用户行为模式，检测异常行为，
 * 计算行为风险分数。是零信任持续风险评估的核心引擎。
 * 
 * 核心功能:
 * - 用户行为基线建立
 * - 实时行为异常检测
 * - 异常登录行为分析
 * - 异常访问模式分析
 * - 设备行为分析
 * - 横向移动检测
 * 
 * @author ZeroTrust Team
 * @since 1.0.0
 */
@Service
public class UEBAAnalysisService {
    
    @Autowired
    private UserBehaviorRepository behaviorRepository;
    
    @Autowired
    private RiskEventRepository riskEventRepository;
    
    // 行为基线缓存 - userId -> 行为基线
    private final ConcurrentHashMap<String, UserBehaviorBaseline> baselineCache = new ConcurrentHashMap<>();
    
    // 实时行为窗口 - userId -> 最近行为列表
    private final ConcurrentHashMap<String, List<UserBehaviorEvent>> behaviorWindow = new ConcurrentHashMap<>();
    
    // 异常分数阈值
    private static final double ANOMALY_THRESHOLD_LOW = 0.3;
    private static final double ANOMALY_THRESHOLD_MEDIUM = 0.5;
    private static final double ANOMALY_THRESHOLD_HIGH = 0.7;
    private static final double ANOMALY_THRESHOLD_CRITICAL = 0.9;
    
    // 时间窗口配置
    private static final int BASELINE_WINDOW_DAYS = 30;
    private static final int REALTIME_WINDOW_MINUTES = 60;
    
    /**
     * 分析用户行为并返回风险因子
     * 
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param context 访问上下文
     * @return 行为风险因子
     */
    public RiskScore.RiskFactor analyzeUserBehavior(String userId, String deviceId, AccessContext context) {
        double anomalyScore = 0.0;
        List<String> evidence = new ArrayList<>();
        
        // 1. 登录行为分析
        double loginAnomaly = analyzeLoginBehavior(userId, context);
        if (loginAnomaly > ANOMALY_THRESHOLD_LOW) {
            anomalyScore = Math.max(anomalyScore, loginAnomaly * 100);
            evidence.add(String.format("登录行为异常: 异常度 %.2f", loginAnomaly));
        }
        
        // 2. 访问模式分析
        double accessAnomaly = analyzeAccessPattern(userId, context);
        if (accessAnomaly > ANOMALY_THRESHOLD_LOW) {
            anomalyScore = Math.max(anomalyScore, accessAnomaly * 100);
            evidence.add(String.format("访问模式异常: 异常度 %.2f", accessAnomaly));
        }
        
        // 3. 设备行为分析
        double deviceAnomaly = analyzeDeviceBehavior(userId, deviceId, context);
        if (deviceAnomaly > ANOMALY_THRESHOLD_LOW) {
            anomalyScore = Math.max(anomalyScore, deviceAnomaly * 100);
            evidence.add(String.format("设备行为异常: 异常度 %.2f", deviceAnomaly));
        }
        
        // 4. 时间行为分析
        double timeAnomaly = analyzeTimeBehavior(userId, context);
        if (timeAnomaly > ANOMALY_THRESHOLD_LOW) {
            anomalyScore = Math.max(anomalyScore, timeAnomaly * 100);
            evidence.add(String.format("时间行为异常: 异常度 %.2f", timeAnomaly));
        }
        
        // 5. 地理位置分析
        double geoAnomaly = analyzeGeolocation(userId, context);
        if (geoAnomaly > ANOMALY_THRESHOLD_LOW) {
            anomalyScore = Math.max(anomalyScore, geoAnomaly * 100);
            evidence.add(String.format("地理位置异常: 异常度 %.2f", geoAnomaly));
        }
        
        // 6. 速度异常分析（不可能旅行）
        double velocityAnomaly = analyzeVelocity(userId, context);
        if (velocityAnomaly > ANOMALY_THRESHOLD_LOW) {
            anomalyScore = Math.max(anomalyScore, velocityAnomaly * 100);
            evidence.add(String.format("移动速度异常: 异常度 %.2f", velocityAnomaly));
        }
        
        // 7. 横向移动检测
        double lateralMovement = detectLateralMovement(userId, context);
        if (lateralMovement > ANOMALY_THRESHOLD_MEDIUM) {
            anomalyScore = Math.max(anomalyScore, lateralMovement * 100);
            evidence.add(String.format("疑似横向移动: 异常度 %.2f", lateralMovement));
        }
        
        // 构建风险因子
        String evidenceStr = evidence.isEmpty() ? "行为模式正常" : String.join("; ", evidence);
        String description = buildBehaviorDescription(anomalyScore, evidence);
        
        return new RiskScore.RiskFactor(
            RiskScore.RiskFactorType.USER_BEHAVIOR,
            anomalyScore,
            0.35, // 权重35%
            description,
            evidenceStr
        );
    }
    
    /**
     * 分析登录行为异常
     */
    private double analyzeLoginBehavior(String userId, AccessContext context) {
        UserBehaviorBaseline baseline = getOrCreateBaseline(userId);
        double anomalyScore = 0.0;
        
        // 检查登录时间是否异常
        int currentHour = context.getTimestamp().getHour();
        if (!baseline.getNormalLoginHours().contains(currentHour)) {
            anomalyScore += 0.3;
        }
        
        // 检查登录IP是否为新IP
        String currentIp = context.getIpAddress();
        if (!baseline.getKnownIPs().contains(currentIp)) {
            anomalyScore += 0.4;
            // 如果是首次出现的IP，额外加分
            if (baseline.getKnownIPs().size() > 5) {
                anomalyScore += 0.2;
            }
        }
        
        // 检查登录失败次数
        int recentFailures = getRecentLoginFailures(userId, Duration.ofHours(1));
        if (recentFailures > 3) {
            anomalyScore += Math.min(recentFailures * 0.1, 0.3);
        }
        
        // 检查是否使用新设备
        String deviceId = context.getDeviceId();
        if (!baseline.getKnownDevices().contains(deviceId)) {
            anomalyScore += 0.2;
        }
        
        return Math.min(anomalyScore, 1.0);
    }
    
    /**
     * 分析访问模式异常
     */
    private double analyzeAccessPattern(String userId, AccessContext context) {
        UserBehaviorBaseline baseline = getOrCreateBaseline(userId);
        double anomalyScore = 0.0;
        
        // 检查访问的资源是否异常
        String resource = context.getResource();
        if (!baseline.getNormalResources().contains(resource)) {
            anomalyScore += 0.25;
            // 检查是否为敏感资源
            if (isSensitiveResource(resource)) {
                anomalyScore += 0.3;
            }
        }
        
        // 检查访问频率
        int recentAccessCount = getRecentAccessCount(userId, Duration.ofMinutes(5));
        double avgAccessRate = baseline.getAvgAccessRatePerMinute();
        if (avgAccessRate > 0 && recentAccessCount > avgAccessRate * 5) {
            anomalyScore += 0.25;
        }
        
        // 检查访问的操作类型
        String action = context.getAction();
        if (!baseline.getNormalActions().contains(action)) {
            anomalyScore += 0.2;
        }
        
        // 检查是否有批量下载行为
        if (isBulkDownloadBehavior(userId, context)) {
            anomalyScore += 0.3;
        }
        
        return Math.min(anomalyScore, 1.0);
    }
    
    /**
     * 分析设备行为异常
     */
    private double analyzeDeviceBehavior(String userId, String deviceId, AccessContext context) {
        UserBehaviorBaseline baseline = getOrCreateBaseline(userId);
        double anomalyScore = 0.0;
        
        if (!baseline.getKnownDevices().contains(deviceId)) {
            anomalyScore += 0.4;
        }
        
        // 检查User-Agent是否异常
        String userAgent = context.getUserAgent();
        if (userAgent != null) {
            String deviceType = parseDeviceType(userAgent);
            if (!baseline.getNormalDeviceTypes().contains(deviceType)) {
                anomalyScore += 0.2;
            }
            
            // 检查是否使用自动化工具
            if (isAutomationTool(userAgent)) {
                anomalyScore += 0.4;
            }
        }
        
        // 检查屏幕分辨率是否异常（可能的虚拟机/远程桌面）
        String screenRes = context.getScreenResolution();
        if (screenRes != null && !baseline.getNormalScreenResolutions().contains(screenRes)) {
            anomalyScore += 0.1;
        }
        
        return Math.min(anomalyScore, 1.0);
    }
    
    /**
     * 分析时间行为异常
     */
    private double analyzeTimeBehavior(String userId, AccessContext context) {
        UserBehaviorBaseline baseline = getOrCreateBaseline(userId);
        double anomalyScore = 0.0;
        
        LocalDateTime timestamp = context.getTimestamp();
        int hour = timestamp.getHour();
        DayOfWeek dayOfWeek = timestamp.getDayOfWeek();
        
        // 检查是否在正常工作时间
        if (!baseline.getNormalLoginHours().contains(hour)) {
            anomalyScore += 0.3;
            // 深夜访问（0-5点）风险更高
            if (hour >= 0 && hour <= 5) {
                anomalyScore += 0.2;
            }
        }
        
        // 检查是否在正常工作日
        if (!baseline.getNormalWorkDays().contains(dayOfWeek)) {
            anomalyScore += 0.25;
            // 节假日访问
            if (isHoliday(timestamp)) {
                anomalyScore += 0.2;
            }
        }
        
        // 检查长期未登录后的突然登录
        LocalDateTime lastLogin = baseline.getLastLoginTime();
        if (lastLogin != null) {
            long daysSinceLastLogin = ChronoUnit.DAYS.between(lastLogin, timestamp);
            if (daysSinceLastLogin > 30) {
                anomalyScore += 0.3;
            }
        }
        
        return Math.min(anomalyScore, 1.0);
    }
    
    /**
     * 分析地理位置异常
     */
    private double analyzeGeolocation(String userId, AccessContext context) {
        UserBehaviorBaseline baseline = getOrCreateBaseline(userId);
        double anomalyScore = 0.0;
        
        String currentLocation = context.getGeolocation();
        if (currentLocation == null) return 0.0;
        
        // 检查是否为新位置
        if (!baseline.getKnownLocations().contains(currentLocation)) {
            anomalyScore += 0.3;
        }
        
        // 检查是否来自高风险地区
        if (isHighRiskLocation(currentLocation)) {
            anomalyScore += 0.4;
        }
        
        // 检查是否来自TOR出口节点
        if (isTorExitNode(context.getIpAddress())) {
            anomalyScore += 0.5;
        }
        
        // 检查是否使用VPN/代理
        if (context.isUsingVpn() || context.isUsingProxy()) {
            anomalyScore += 0.25;
        }
        
        return Math.min(anomalyScore, 1.0);
    }
    
    /**
     * 分析速度异常（不可能旅行检测）
     */
    private double analyzeVelocity(String userId, AccessContext context) {
        UserBehaviorEvent lastEvent = getLastEvent(userId);
        if (lastEvent == null) return 0.0;
        
        String currentLocation = context.getGeolocation();
        String lastLocation = lastEvent.getLocation();
        
        if (currentLocation == null || lastLocation == null) return 0.0;
        if (currentLocation.equals(lastLocation)) return 0.0;
        
        double distance = calculateDistance(lastLocation, currentLocation); // 公里
        long timeDiff = ChronoUnit.MINUTES.between(lastEvent.getTimestamp(), context.getTimestamp());
        
        if (timeDiff <= 0) return 0.0;
        
        double speed = distance / (timeDiff / 60.0); // 公里/小时
        
        // 飞机平均速度约900km/h，考虑到各种因素，超过800km/h视为异常
        if (speed > 800) {
            return Math.min(speed / 1000.0, 1.0);
        }
        
        // 超过300km/h也需要关注（高铁速度）
        if (speed > 300) {
            return Math.min(speed / 600.0, 0.5);
        }
        
        return 0.0;
    }
    
    /**
     * 检测横向移动行为
     */
    private double detectLateralMovement(String userId, AccessContext context) {
        double anomalyScore = 0.0;
        
        // 获取最近访问的资源列表
        List<String> recentResources = getRecentResources(userId, Duration.ofMinutes(10));
        
        // 检查是否快速访问多个不同资源
        if (recentResources.size() > 10) {
            long uniqueResources = recentResources.stream().distinct().count();
            if (uniqueResources > 8) {
                anomalyScore += 0.3;
            }
        }
        
        // 检查是否访问了不相关的资源
        Set<String> normalResourcePatterns = getNormalResourcePatterns(userId);
        for (String resource : recentResources) {
            boolean matchesPattern = normalResourcePatterns.stream()
                .anyMatch(pattern -> resource.matches(pattern));
            if (!matchesPattern) {
                anomalyScore += 0.1;
            }
        }
        
        // 检查是否有权限提升尝试
        if (hasPrivilegeEscalationAttempt(userId, context)) {
            anomalyScore += 0.5;
        }
        
        return Math.min(anomalyScore, 1.0);
    }
    
    /**
     * 获取或创建用户行为基线
     */
    private UserBehaviorBaseline getOrCreateBaseline(String userId) {
        return baselineCache.computeIfAbsent(userId, k -> {
            UserBehaviorBaseline baseline = behaviorRepository.findBaselineByUserId(userId);
            if (baseline == null) {
                baseline = createDefaultBaseline(userId);
            }
            return baseline;
        });
    }
    
    /**
     * 创建默认行为基线
     */
    private UserBehaviorBaseline createDefaultBaseline(String userId) {
        UserBehaviorBaseline baseline = new UserBehaviorBaseline();
        baseline.setUserId(userId);
        baseline.setCreatedAt(LocalDateTime.now());
        
        // 默认工作时间：9-18点
        baseline.setNormalLoginHours(new HashSet<>(Arrays.asList(9, 10, 11, 12, 13, 14, 15, 16, 17, 18)));
        
        // 默认工作日
        baseline.setNormalWorkDays(new HashSet<>(Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        )));
        
        return baseline;
    }
    
    /**
     * 获取最近的登录失败次数
     */
    private int getRecentLoginFailures(String userId, Duration duration) {
        LocalDateTime since = LocalDateTime.now().minus(duration);
        return riskEventRepository.countFailedLoginsSince(userId, since);
    }
    
    /**
     * 获取最近访问次数
     */
    private int getRecentAccessCount(String userId, Duration duration) {
        LocalDateTime since = LocalDateTime.now().minus(duration);
        return (int) behaviorWindow.getOrDefault(userId, new ArrayList<>())
            .stream()
            .filter(e -> e.getTimestamp().isAfter(since))
            .count();
    }
    
    /**
     * 获取最近访问的资源
     */
    private List<String> getRecentResources(String userId, Duration duration) {
        LocalDateTime since = LocalDateTime.now().minus(duration);
        return behaviorWindow.getOrDefault(userId, new ArrayList<>())
            .stream()
            .filter(e -> e.getTimestamp().isAfter(since))
            .map(UserBehaviorEvent::getResource)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取最后一条事件
     */
    private UserBehaviorEvent getLastEvent(String userId) {
        List<UserBehaviorEvent> events = behaviorWindow.get(userId);
        if (events == null || events.isEmpty()) return null;
        return events.get(events.size() - 1);
    }
    
    /**
     * 记录行为事件
     */
    public void recordBehaviorEvent(String userId, UserBehaviorEvent event) {
        behaviorWindow.computeIfAbsent(userId, k -> new ArrayList<>()).add(event);
        
        // 清理过期事件
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(REALTIME_WINDOW_MINUTES);
        behaviorWindow.get(userId).removeIf(e -> e.getTimestamp().isBefore(cutoff));
    }
    
    /**
     * 更新行为基线
     */
    public void updateBaseline(String userId, UserBehaviorBaseline baseline) {
        baseline.setUpdatedAt(LocalDateTime.now());
        baselineCache.put(userId, baseline);
        behaviorRepository.saveBaseline(baseline);
    }
    
    // ========== 辅助方法 ==========
    
    private boolean isSensitiveResource(String resource) {
        String[] sensitivePatterns = {"/admin", "/config", "/password", "/secret", "/api/users"};
        return Arrays.stream(sensitivePatterns).anyMatch(resource::contains);
    }
    
    private boolean isBulkDownloadBehavior(String userId, AccessContext context) {
        // 简化实现，实际应该分析下载模式
        return false;
    }
    
    private String parseDeviceType(String userAgent) {
        if (userAgent.contains("Mobile")) return "mobile";
        if (userAgent.contains("Tablet")) return "tablet";
        return "desktop";
    }
    
    private boolean isAutomationTool(String userAgent) {
        String[] botPatterns = {"bot", "crawler", "spider", "scrape", "headless", "selenium", "puppeteer"};
        String lowerUA = userAgent.toLowerCase();
        return Arrays.stream(botPatterns).anyMatch(lowerUA::contains);
    }
    
    private boolean isHoliday(LocalDateTime date) {
        // 简化实现，实际需要节假日数据
        return false;
    }
    
    private boolean isHighRiskLocation(String location) {
        // 简化实现，实际需要威胁情报数据
        return false;
    }
    
    private boolean isTorExitNode(String ipAddress) {
        // 简化实现，实际需要TOR出口节点列表
        return false;
    }
    
    private double calculateDistance(String loc1, String loc2) {
        // 简化实现，实际需要地理坐标计算
        return 0.0;
    }
    
    private Set<String> getNormalResourcePatterns(String userId) {
        UserBehaviorBaseline baseline = getOrCreateBaseline(userId);
        return baseline.getNormalResources().stream()
            .map(r -> r.replace("*", ".*"))
            .collect(Collectors.toSet());
    }
    
    private boolean hasPrivilegeEscalationAttempt(String userId, AccessContext context) {
        // 简化实现，实际应该检查权限变更日志
        return false;
    }
    
    private String buildBehaviorDescription(double score, List<String> evidence) {
        if (score < 20) return "用户行为正常";
        if (score < 40) return "检测到轻微行为异常";
        if (score < 60) return "检测到中等行为异常，建议关注";
        if (score < 80) return "检测到显著行为异常，建议验证身份";
        return "检测到严重行为异常，建议立即阻断";
    }
    
    // ========== 内部类定义 ==========
    
    /**
     * 用户行为基线
     */
    public static class UserBehaviorBaseline {
        private String userId;
        private Set<Integer> normalLoginHours = new HashSet<>();
        private Set<DayOfWeek> normalWorkDays = new HashSet<>();
        private Set<String> knownIPs = new HashSet<>();
        private Set<String> knownDevices = new HashSet<>();
        private Set<String> knownLocations = new HashSet<>();
        private Set<String> normalResources = new HashSet<>();
        private Set<String> normalActions = new HashSet<>();
        private Set<String> normalDeviceTypes = new HashSet<>();
        private Set<String> normalScreenResolutions = new HashSet<>();
        private double avgAccessRatePerMinute;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Set<Integer> getNormalLoginHours() { return normalLoginHours; }
        public void setNormalLoginHours(Set<Integer> normalLoginHours) { this.normalLoginHours = normalLoginHours; }
        public Set<DayOfWeek> getNormalWorkDays() { return normalWorkDays; }
        public void setNormalWorkDays(Set<DayOfWeek> normalWorkDays) { this.normalWorkDays = normalWorkDays; }
        public Set<String> getKnownIPs() { return knownIPs; }
        public void setKnownIPs(Set<String> knownIPs) { this.knownIPs = knownIPs; }
        public Set<String> getKnownDevices() { return knownDevices; }
        public void setKnownDevices(Set<String> knownDevices) { this.knownDevices = knownDevices; }
        public Set<String> getKnownLocations() { return knownLocations; }
        public void setKnownLocations(Set<String> knownLocations) { this.knownLocations = knownLocations; }
        public Set<String> getNormalResources() { return normalResources; }
        public void setNormalResources(Set<String> normalResources) { this.normalResources = normalResources; }
        public Set<String> getNormalActions() { return normalActions; }
        public void setNormalActions(Set<String> normalActions) { this.normalActions = normalActions; }
        public Set<String> getNormalDeviceTypes() { return normalDeviceTypes; }
        public void setNormalDeviceTypes(Set<String> normalDeviceTypes) { this.normalDeviceTypes = normalDeviceTypes; }
        public Set<String> getNormalScreenResolutions() { return normalScreenResolutions; }
        public void setNormalScreenResolutions(Set<String> normalScreenResolutions) { this.normalScreenResolutions = normalScreenResolutions; }
        public double getAvgAccessRatePerMinute() { return avgAccessRatePerMinute; }
        public void setAvgAccessRatePerMinute(double avgAccessRatePerMinute) { this.avgAccessRatePerMinute = avgAccessRatePerMinute; }
        public LocalDateTime getLastLoginTime() { return lastLoginTime; }
        public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
    
    /**
     * 用户行为事件
     */
    public static class UserBehaviorEvent {
        private String userId;
        private String resource;
        private String action;
        private String location;
        private LocalDateTime timestamp;
        
        public UserBehaviorEvent() {}
        
        public UserBehaviorEvent(String userId, String resource, String action, 
                                String location, LocalDateTime timestamp) {
            this.userId = userId;
            this.resource = resource;
            this.action = action;
            this.location = location;
            this.timestamp = timestamp;
        }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 访问上下文
     */
    public static class AccessContext {
        private String userId;
        private String deviceId;
        private String ipAddress;
        private String userAgent;
        private String geolocation;
        private String resource;
        private String action;
        private String screenResolution;
        private LocalDateTime timestamp;
        private boolean usingVpn;
        private boolean usingProxy;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getGeolocation() { return geolocation; }
        public void setGeolocation(String geolocation) { this.geolocation = geolocation; }
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getScreenResolution() { return screenResolution; }
        public void setScreenResolution(String screenResolution) { this.screenResolution = screenResolution; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public boolean isUsingVpn() { return usingVpn; }
        public void setUsingVpn(boolean usingVpn) { this.usingVpn = usingVpn; }
        public boolean isUsingProxy() { return usingProxy; }
        public void setUsingProxy(boolean usingProxy) { this.usingProxy = usingProxy; }
    }
    
    // Repository 接口定义（简化）
    public interface UserBehaviorRepository {
        UserBehaviorBaseline findBaselineByUserId(String userId);
        void saveBaseline(UserBehaviorBaseline baseline);
    }
    
    public interface RiskEventRepository {
        int countFailedLoginsSince(String userId, LocalDateTime since);
    }
}
