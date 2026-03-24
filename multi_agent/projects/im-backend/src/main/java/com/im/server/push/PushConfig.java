package com.im.server.push;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 推送服务配置
 * 
 * 支持多种推送通道:
 * - APNs (Apple Push Notification service)
 * - FCM (Firebase Cloud Messaging) - Android
 * - 厂商通道: 华为、小米、OPPO、vivo
 * - 第三方推送: 极光(JPush)、个推(GeTui)、OneSignal
 */
@Component
@ConfigurationProperties(prefix = "im.push")
public class PushConfig {

    // ==================== 通用配置 ====================
    private boolean enabled = true;
    private int maxRetryAttempts = 3;
    private long connectionTimeoutMs = 5000;
    private long readTimeoutMs = 10000;
    private int maxMessageSize = 4096; // bytes
    private boolean enableMessageMerging = true;
    private int mergeWindowSeconds = 60;
    private boolean enableQuietHours = true;
    private int maxBatchSize = 100;

    // ==================== APNs 配置 ====================
    private ApnsConfig apns = new ApnsConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.apns")
    public static class ApnsConfig {
        private boolean enabled = true;
        private String teamId;
        private String keyId;
        private String keyPath; // p8 文件路径
        private String bundleId;
        private String productionHost = "api.push.apple.com";
        private String sandboxHost = "api.sandbox.push.apple.com";
        private boolean sandbox = false; // 是否使用沙盒环境
        private int connectionPoolSize = 10;
        private int maxConcurrentStreams = 100;
        private String topic; // 默认为 bundleId
        private int priority = 10; // 推送优先级 10=高, 5=低
        private int expiration = 86400 * 7; // 7天过期
        
        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getTeamId() { return teamId; }
        public void setTeamId(String teamId) { this.teamId = teamId; }
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public String getKeyPath() { return keyPath; }
        public void setKeyPath(String keyPath) { this.keyPath = keyPath; }
        public String getBundleId() { return bundleId; }
        public void setBundleId(String bundleId) { this.bundleId = bundleId; }
        public String getProductionHost() { return productionHost; }
        public void setProductionHost(String productionHost) { this.productionHost = productionHost; }
        public String getSandboxHost() { return sandboxHost; }
        public void setSandboxHost(String sandboxHost) { this.sandboxHost = sandboxHost; }
        public boolean isSandbox() { return sandbox; }
        public void setSandbox(boolean sandbox) { this.sandbox = sandbox; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
        public int getMaxConcurrentStreams() { return maxConcurrentStreams; }
        public void setMaxConcurrentStreams(int maxConcurrentStreams) { this.maxConcurrentStreams = maxConcurrentStreams; }
        public String getTopic() { return topic != null ? topic : bundleId; }
        public void setTopic(String topic) { this.topic = topic; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public int getExpiration() { return expiration; }
        public void setExpiration(int expiration) { this.expiration = expiration; }
    }

    // ==================== FCM 配置 ====================
    private FcmConfig fcm = new FcmConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.fcm")
    public static class FcmConfig {
        private boolean enabled = false;
        private String projectId;
        private String credentialsPath; // service account JSON 文件路径
        private String endpoint = "https://fcm.googleapis.com/v1/projects/";
        private int connectionPoolSize = 5;
        private long tokenRefreshThresholdSeconds = 3600; // token 刷新阈值
        
        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public String getCredentialsPath() { return credentialsPath; }
        public void setCredentialsPath(String credentialsPath) { this.credentialsPath = credentialsPath; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
        public long getTokenRefreshThresholdSeconds() { return tokenRefreshThresholdSeconds; }
        public void setTokenRefreshThresholdSeconds(long tokenRefreshThresholdSeconds) { this.tokenRefreshThresholdSeconds = tokenRefreshThresholdSeconds; }
    }

    // ==================== 华为推送配置 ====================
    private HuaweiConfig huawei = new HuaweiConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.huawei")
    public static class HuaweiConfig {
        private boolean enabled = false;
        private String appId;
        private String appSecret;
        private String endpoint = "https://push-api.cloud.huawei.com";
        private int connectionPoolSize = 5;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        public String getAppSecret() { return appSecret; }
        public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    }

    // ==================== 小米推送配置 ====================
    private XiaomiConfig xiaomi = new XiaomiConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.xiaomi")
    public static class XiaomiConfig {
        private boolean enabled = false;
        private String appId;
        private String appSecret;
        private String endpoint = "https://api.xmpush.xiaomi.com";
        private int connectionPoolSize = 5;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        public String getAppSecret() { return appSecret; }
        public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    }

    // ==================== OPPO 推送配置 ====================
    private OppoConfig oppo = new OppoConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.oppo")
    public static class OppoConfig {
        private boolean enabled = false;
        private String appKey;
        private String appSecret;
        private String endpoint = "https://api.push.oppomobile.com";
        private int connectionPoolSize = 5;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppKey() { return appKey; }
        public void setAppKey(String appKey) { this.appKey = appKey; }
        public String getAppSecret() { return appSecret; }
        public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    }

    // ==================== vivo 推送配置 ====================
    private VivoConfig vivo = new VivoConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.vivo")
    public static class VivoConfig {
        private boolean enabled = false;
        private String appId;
        private String appKey;
        private String appSecret;
        private String endpoint = "https://api.vivo.com.cn/service/msg/push";
        private int connectionPoolSize = 5;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        public String getAppKey() { return appKey; }
        public void setAppKey(String appKey) { this.appKey = appKey; }
        public String getAppSecret() { return appSecret; }
        public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    }

    // ==================== 极光推送配置 ====================
    private JPushConfig jpush = new JPushConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.jpush")
    public static class JPushConfig {
        private boolean enabled = false;
        private String appKey;
        private String masterSecret;
        private String endpoint = "https://api.jpush.cn/v3/push";
        private int connectionPoolSize = 5;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getAppKey() { return appKey; }
        public void setAppKey(String appKey) { this.appKey = appKey; }
        public String getMasterSecret() { return masterSecret; }
        public void setMasterSecret(String masterSecret) { this.masterSecret = masterSecret; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    }

    // ==================== 免打扰配置 ====================
    private QuietHoursConfig quietHours = new QuietHoursConfig();

    @Component
    @ConfigurationProperties(prefix = "im.push.quiet-hours")
    public static class QuietHoursConfig {
        private boolean enabled = true;
        private int startHour = 22; // 晚上10点开始
        private int startMinute = 0;
        private int endHour = 8;    // 早上8点结束
        private int endMinute = 0;
        private int timezoneOffsetHours = 8; // UTC+8
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getStartHour() { return startHour; }
        public void setStartHour(int startHour) { this.startHour = startHour; }
        public int getStartMinute() { return startMinute; }
        public void setStartMinute(int startMinute) { this.startMinute = startMinute; }
        public int getEndHour() { return endHour; }
        public void setEndHour(int endHour) { this.endHour = endHour; }
        public int getEndMinute() { return endMinute; }
        public void setEndMinute(int endMinute) { this.endMinute = endMinute; }
        public int getTimezoneOffsetHours() { return timezoneOffsetHours; }
        public void setTimezoneOffsetHours(int timezoneOffsetHours) { this.timezoneOffsetHours = timezoneOffsetHours; }
    }

    // ==================== Getters and Setters ====================
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getMaxRetryAttempts() { return maxRetryAttempts; }
    public void setMaxRetryAttempts(int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }
    public long getConnectionTimeoutMs() { return connectionTimeoutMs; }
    public void setConnectionTimeoutMs(long connectionTimeoutMs) { this.connectionTimeoutMs = connectionTimeoutMs; }
    public long getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(long readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }
    public int getMaxMessageSize() { return maxMessageSize; }
    public void setMaxMessageSize(int maxMessageSize) { this.maxMessageSize = maxMessageSize; }
    public boolean isEnableMessageMerging() { return enableMessageMerging; }
    public void setEnableMessageMerging(boolean enableMessageMerging) { this.enableMessageMerging = enableMessageMerging; }
    public int getMergeWindowSeconds() { return mergeWindowSeconds; }
    public void setMergeWindowSeconds(int mergeWindowSeconds) { this.mergeWindowSeconds = mergeWindowSeconds; }
    public boolean isEnableQuietHours() { return enableQuietHours; }
    public void setEnableQuietHours(boolean enableQuietHours) { this.enableQuietHours = enableQuietHours; }
    public int getMaxBatchSize() { return maxBatchSize; }
    public void setMaxBatchSize(int maxBatchSize) { this.maxBatchSize = maxBatchSize; }
    public ApnsConfig getApns() { return apns; }
    public void setApns(ApnsConfig apns) { this.apns = apns; }
    public FcmConfig getFcm() { return fcm; }
    public void setFcm(FcmConfig fcm) { this.fcm = fcm; }
    public HuaweiConfig getHuawei() { return huawei; }
    public void setHuawei(HuaweiConfig huawei) { this.huawei = huawei; }
    public XiaomiConfig getXiaomi() { return xiaomi; }
    public void setXiaomi(XiaomiConfig xiaomi) { this.xiaomi = xiaomi; }
    public OppoConfig getOppo() { return oppo; }
    public void setOppo(OppoConfig oppo) { this.oppo = oppo; }
    public VivoConfig getVivo() { return vivo; }
    public void setVivo(VivoConfig vivo) { this.vivo = vivo; }
    public JPushConfig getJpush() { return jpush; }
    public void setJpush(JPushConfig jpush) { this.jpush = jpush; }
    public QuietHoursConfig getQuietHours() { return quietHours; }
    public void setQuietHours(QuietHoursConfig quietHours) { this.quietHours = quietHours; }
}
