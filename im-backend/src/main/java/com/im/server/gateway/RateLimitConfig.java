package com.im.server.gateway;

/**
 * 限流配置类
 * 
 * 配置限流的各种参数，支持：
 * - Token Bucket 令牌桶限流
 * - Sliding Window 滑动窗口限流 (Redis)
 * - Fixed Window 固定窗口限流
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class RateLimitConfig {

    // ==================== 全局限流配置 ====================
    
    /** 全局每秒最大请求数 */
    private int globalQps = 10000;
    
    /** 全局每分钟最大请求数 */
    private int globalRpm = 500000;
    
    /** 全局每分钟最大连接数 */
    private int globalMaxConnections = 50000;

    // ==================== IP级限流配置 ====================
    
    /** IP级每秒最大请求数 */
    private int ipQps = 100;
    
    /** IP级每分钟最大请求数 */
    private int ipRpm = 3000;
    
    /** IP级每小时最大请求数 */
    private int ipRph = 10000;
    
    /** 单IP最大连接数 */
    private int ipMaxConnections = 10;

    // ==================== 用户级限流配置 ====================
    
    /** 用户级每秒最大请求数 */
    private int userQps = 30;
    
    /** 用户级每分钟最大请求数 */
    private int userRpm = 500;
    
    /** 用户级每小时最大请求数 */
    private int userRph = 5000;
    
    /** 单用户最大连接数 */
    private int userMaxConnections = 5;
    
    /** 单用户消息发送速率 (条/分钟) */
    private int userMessageRate = 60;

    // ==================== 登录限流配置 ====================
    
    /** 登录尝试每秒最大次数 */
    private int loginQps = 2;
    
    /** 登录尝试每分钟最大次数 */
    private int loginRpm = 5;
    
    /** 登录失败后封禁时长 (秒) */
    private int loginBanDuration = 300;
    
    /** 连续登录失败次数阈值 */
    private int loginFailureThreshold = 5;

    // ==================== 滑动窗口配置 ====================
    
    /** 滑动窗口大小 (毫秒) */
    private long slidingWindowSize = 60000;
    
    /** 滑动窗口子窗口数量 */
    private int windowBuckets = 60;

    // ==================== 限流算法配置 ====================
    
    /** 限流算法类型: TOKEN_BUCKET, SLIDING_WINDOW, FIXED_WINDOW */
    private String algorithm = "TOKEN_BUCKET";
    
    /** 是否启用限流 */
    private boolean enabled = true;
    
    /** 是否启用Redis分布式限流 */
    private boolean useRedis = true;

    // ==================== 白名单配置 ====================
    
    /** IP白名单 */
    private String[] whiteList = new String[]{
        "127.0.0.1",
        "0:0:0:0:0:0:0:1",
        "localhost"
    };
    
    /** 用户白名单 (管理员用户ID) */
    private long[] userWhiteList = new long[]{
        1L,  // 管理员
        999L // 测试用户
    };

    // ==================== 限流响应配置 ====================
    
    /** 限流响应HTTP状态码 */
    private int responseStatusCode = 429;
    
    /** 限流响应消息 */
    private String responseMessage = "Too Many Requests";
    
    /** 是否返回Retry-After头 */
    private boolean returnRetryAfter = true;

    // ==================== Getters and Setters ====================

    public int getGlobalQps() { return globalQps; }
    public void setGlobalQps(int globalQps) { this.globalQps = globalQps; }

    public int getGlobalRpm() { return globalRpm; }
    public void setGlobalRpm(int globalRpm) { this.globalRpm = globalRpm; }

    public int getGlobalMaxConnections() { return globalMaxConnections; }
    public void setGlobalMaxConnections(int globalMaxConnections) { this.globalMaxConnections = globalMaxConnections; }

    public int getIpQps() { return ipQps; }
    public void setIpQps(int ipQps) { this.ipQps = ipQps; }

    public int getIpRpm() { return ipRpm; }
    public void setIpRpm(int ipRpm) { this.ipRpm = ipRpm; }

    public int getIpRph() { return ipRph; }
    public void setIpRph(int ipRph) { this.ipRph = ipRph; }

    public int getIpMaxConnections() { return ipMaxConnections; }
    public void setIpMaxConnections(int ipMaxConnections) { this.ipMaxConnections = ipMaxConnections; }

    public int getUserQps() { return userQps; }
    public void setUserQps(int userQps) { this.userQps = userQps; }

    public int getUserRpm() { return userRpm; }
    public void setUserRpm(int userRpm) { this.userRpm = userRpm; }

    public int getUserRph() { return userRph; }
    public void setUserRph(int userRph) { this.userRph = userRph; }

    public int getUserMaxConnections() { return userMaxConnections; }
    public void setUserMaxConnections(int userMaxConnections) { this.userMaxConnections = userMaxConnections; }

    public int getUserMessageRate() { return userMessageRate; }
    public void setUserMessageRate(int userMessageRate) { this.userMessageRate = userMessageRate; }

    public int getLoginQps() { return loginQps; }
    public void setLoginQps(int loginQps) { this.loginQps = loginQps; }

    public int getLoginRpm() { return loginRpm; }
    public void setLoginRpm(int loginRpm) { this.loginRpm = loginRpm; }

    public int getLoginBanDuration() { return loginBanDuration; }
    public void setLoginBanDuration(int loginBanDuration) { this.loginBanDuration = loginBanDuration; }

    public int getLoginFailureThreshold() { return loginFailureThreshold; }
    public void setLoginFailureThreshold(int loginFailureThreshold) { this.loginFailureThreshold = loginFailureThreshold; }

    public long getSlidingWindowSize() { return slidingWindowSize; }
    public void setSlidingWindowSize(long slidingWindowSize) { this.slidingWindowSize = slidingWindowSize; }

    public int getWindowBuckets() { return windowBuckets; }
    public void setWindowBuckets(int windowBuckets) { this.windowBuckets = windowBuckets; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isUseRedis() { return useRedis; }
    public void setUseRedis(boolean useRedis) { this.useRedis = useRedis; }

    public String[] getWhiteList() { return whiteList; }
    public void setWhiteList(String[] whiteList) { this.whiteList = whiteList; }

    public long[] getUserWhiteList() { return userWhiteList; }
    public void setUserWhiteList(long[] userWhiteList) { this.userWhiteList = userWhiteList; }

    public int getResponseStatusCode() { return responseStatusCode; }
    public void setResponseStatusCode(int responseStatusCode) { this.responseStatusCode = responseStatusCode; }

    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }

    public boolean isReturnRetryAfter() { return returnRetryAfter; }
    public void setReturnRetryAfter(boolean returnRetryAfter) { this.returnRetryAfter = returnRetryAfter; }

    /**
     * 检查IP是否在白名单中
     */
    public boolean isIpWhitelisted(String ip) {
        if (ip == null) return false;
        for (String whiteIp : whiteList) {
            if (whiteIp.equals(ip) || whiteIp.equals("*")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查用户是否在白名单中
     */
    public boolean isUserWhitelisted(Long userId) {
        if (userId == null) return false;
        for (long whiteUserId : userWhiteList) {
            if (whiteUserId == userId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建默认配置
     */
    public static RateLimitConfig defaultConfig() {
        return new RateLimitConfig();
    }

    /**
     * 创建开发环境配置
     */
    public static RateLimitConfig devConfig() {
        RateLimitConfig config = new RateLimitConfig();
        config.setGlobalQps(1000);
        config.setIpQps(50);
        config.setUserQps(20);
        config.setLoginRpm(10);
        return config;
    }

    /**
     * 创建生产环境配置
     */
    public static RateLimitConfig prodConfig() {
        RateLimitConfig config = new RateLimitConfig();
        config.setGlobalQps(20000);
        config.setIpQps(200);
        config.setUserQps(50);
        config.setLoginRpm(5);
        config.setLoginBanDuration(600);
        return config;
    }

    @Override
    public String toString() {
        return "RateLimitConfig{" +
                "globalQps=" + globalQps +
                ", globalRpm=" + globalRpm +
                ", ipQps=" + ipQps +
                ", ipRpm=" + ipRpm +
                ", userQps=" + userQps +
                ", userRpm=" + userRpm +
                ", algorithm='" + algorithm + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
