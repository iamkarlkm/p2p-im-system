package com.im.server.gateway;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 限流服务
 * 
 * 实现多种限流算法：
 * - Token Bucket (令牌桶) - 支持突发流量
 * - Sliding Window (滑动窗口) - 更精确的限流
 * - Fixed Window (固定窗口) - 简单易实现
 * 
 * 支持多维度限流：
 * - 全局限流
 * - IP级限流
 * - 用户级限流
 * - API级限流
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class RateLimitService {

    // ==================== 单例实例 ====================
    
    private static volatile RateLimitService instance;
    
    // ==================== 配置 ====================
    
    private RateLimitConfig config;
    
    // ==================== 限流器存储 ====================
    
    /** 全局限流器 */
    private final GlobalRateLimiter globalLimiter;
    
    /** IP级限流器存储 */
    private final ConcurrentMap<String, TokenBucket> ipLimiters = new ConcurrentHashMap<>();
    
    /** 用户级限流器存储 */
    private final ConcurrentMap<Long, TokenBucket> userLimiters = new ConcurrentHashMap<>();
    
    /** API级限流器存储 */
    private final ConcurrentMap<String, TokenBucket> apiLimiters = new ConcurrentHashMap<>();
    
    /** 滑动窗口限流器 */
    private final ConcurrentMap<String, SlidingWindowRateLimiter> slidingWindowLimiters = new ConcurrentHashMap<>();
    
    /** 登录限流器 (IP维度) */
    private final ConcurrentMap<String, LoginRateLimiter> loginLimiters = new ConcurrentHashMap<>();

    // ==================== 统计信息 ====================
    
    /** 限流命中统计 */
    private final LongAdder totalRequests = new LongAdder();
    private final LongAdder totalLimited = new LongAdder();
    private final LongAdder totalWhitelisted = new LongAdder();
    
    /** 按维度统计 */
    private final ConcurrentMap<String, LongAdder> ipStats = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, LongAdder> userStats = new ConcurrentHashMap<>();

    // ==================== 调度器 ====================
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private volatile boolean running = false;

    // ==================== 构造函数 ====================

    /**
     * 私有构造函数 - 单例模式
     */
    private RateLimitService() {
        this.config = RateLimitConfig.defaultConfig();
        this.globalLimiter = new GlobalRateLimiter(config.getGlobalQps());
        initializeLimiters();
        startScheduler();
    }

    /**
     * 私有构造函数 - 自定义配置
     */
    private RateLimitService(RateLimitConfig config) {
        this.config = config;
        this.globalLimiter = new GlobalRateLimiter(config.getGlobalQps());
        initializeLimiters();
        startScheduler();
    }

    /**
     * 获取单例实例
     */
    public static RateLimitService getInstance() {
        if (instance == null) {
            synchronized (RateLimitService.class) {
                if (instance == null) {
                    instance = new RateLimitService();
                }
            }
        }
        return instance;
    }

    /**
     * 获取单例实例 (自定义配置)
     */
    public static RateLimitService getInstance(RateLimitConfig config) {
        if (instance == null) {
            synchronized (RateLimitService.class) {
                if (instance == null) {
                    instance = new RateLimitService(config);
                }
            }
        }
        return instance;
    }

    // ==================== 核心限流方法 ====================

    /**
     * 检查IP是否允许访问
     * 
     * @param ip 客户端IP
     * @return 限流结果
     */
    public RateLimitResult checkIp(String ip) {
        if (!config.isEnabled()) {
            return RateLimitResult.allow();
        }
        
        // 检查白名单
        if (config.isIpWhitelisted(ip)) {
            totalWhitelisted.increment();
            return RateLimitResult.allow();
        }
        
        totalRequests.increment();
        
        // 检查全局限流
        if (!globalLimiter.tryAcquire()) {
            totalLimited.increment();
            return RateLimitResult.limited("Global rate limit exceeded");
        }
        
        // 获取或创建IP限流器
        TokenBucket ipLimiter = ipLimiters.computeIfAbsent(ip, k -> new TokenBucket(
            config.getIpQps(), 
            config.getIpQps() * 2  // 允许2秒突发
        ));
        
        if (!ipLimiter.tryAcquire()) {
            totalLimited.increment();
            getIpStats(ip).increment();
            return RateLimitResult.limited("IP rate limit exceeded", ipLimiter.getWaitTimeMs());
        }
        
        // 检查IP连接数
        if (ipLimiter.getCurrentTokens() < 0) {
            return RateLimitResult.limited("Too many connections from IP");
        }
        
        return RateLimitResult.allow();
    }

    /**
     * 检查用户是否允许访问
     * 
     * @param userId 用户ID
     * @param ip 客户端IP (可选)
     * @return 限流结果
     */
    public RateLimitResult checkUser(Long userId, String ip) {
        if (!config.isEnabled()) {
            return RateLimitResult.allow();
        }
        
        // 检查用户白名单
        if (config.isUserWhitelisted(userId)) {
            totalWhitelisted.increment();
            return RateLimitResult.allow();
        }
        
        totalRequests.increment();
        
        // 先检查IP限流
        if (ip != null) {
            RateLimitResult ipResult = checkIp(ip);
            if (ipResult.isLimited()) {
                return ipResult;
            }
        }
        
        // 获取或创建用户限流器
        TokenBucket userLimiter = userLimiters.computeIfAbsent(userId, k -> new TokenBucket(
            config.getUserQps(),
            config.getUserQps() * 3  // 允许3秒突发
        ));
        
        if (!userLimiter.tryAcquire()) {
            totalLimited.increment();
            getUserStats(userId).increment();
            return RateLimitResult.limited("User rate limit exceeded", userLimiter.getWaitTimeMs());
        }
        
        return RateLimitResult.allow();
    }

    /**
     * 检查API调用
     * 
     * @param api API路径
     * @param ip 客户端IP
     * @param userId 用户ID (可选)
     * @return 限流结果
     */
    public RateLimitResult checkApi(String api, String ip, Long userId) {
        if (!config.isEnabled()) {
            return RateLimitResult.allow();
        }
        
        // 获取或创建API限流器
        TokenBucket apiLimiter = apiLimiters.computeIfAbsent(api, k -> new TokenBucket(
            100,  // 默认每秒100次
            200   // 突发200次
        ));
        
        if (!apiLimiter.tryAcquire()) {
            totalLimited.increment();
            return RateLimitResult.limited("API rate limit exceeded", apiLimiter.getWaitTimeMs());
        }
        
        // 如果有用户信息，进一步检查用户限流
        if (userId != null) {
            return checkUser(userId, ip);
        }
        
        return RateLimitResult.allow();
    }

    /**
     * 检查消息发送速率
     * 
     * @param userId 用户ID
     * @return 限流结果
     */
    public RateLimitResult checkMessage(Long userId) {
        String key = "message:" + userId;
        
        SlidingWindowRateLimiter limiter = slidingWindowLimiters.computeIfAbsent(
            key, k -> new SlidingWindowRateLimiter(config.getUserMessageRate(), 60000)
        );
        
        if (!limiter.tryAcquire()) {
            totalLimited.increment();
            return RateLimitResult.limited("Message rate limit exceeded. Max " + 
                config.getUserMessageRate() + " messages per minute", 
                limiter.getWaitTimeMs());
        }
        
        return RateLimitResult.allow();
    }

    /**
     * 检查登录尝试
     * 
     * @param ip 客户端IP
     * @param username 用户名
     * @return 限流结果
     */
    public RateLimitResult checkLogin(String ip, String username) {
        String key = ip + ":" + (username != null ? username : "unknown");
        
        LoginRateLimiter limiter = loginLimiters.computeIfAbsent(key, k -> new LoginRateLimiter(
            config.getLoginRpm(),
            config.getLoginBanDuration(),
            config.getLoginFailureThreshold()
        ));
        
        // 检查是否被封禁
        if (limiter.isBanned()) {
            return RateLimitResult.limited("IP temporarily banned due to too many failed login attempts",
                limiter.getBanRemainingMs());
        }
        
        // 检查速率
        if (!limiter.tryAttempt()) {
            return RateLimitResult.limited("Login rate limit exceeded", limiter.getWaitTimeMs());
        }
        
        return RateLimitResult.allow();
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailure(String ip, String username) {
        String key = ip + ":" + (username != null ? username : "unknown");
        LoginRateLimiter limiter = loginLimiters.get(key);
        if (limiter != null) {
            limiter.recordFailure();
        }
    }

    /**
     * 记录登录成功
     */
    public void recordLoginSuccess(String ip, String username) {
        String key = ip + ":" + (username != null ? username : "unknown");
        LoginRateLimiter limiter = loginLimiters.get(key);
        if (limiter != null) {
            limiter.recordSuccess();
        }
    }

    // ==================== 滑动窗口限流 ====================

    /**
     * 使用滑动窗口检查限流
     */
    public RateLimitResult checkSlidingWindow(String key, int maxRequests, long windowMs) {
        SlidingWindowRateLimiter limiter = slidingWindowLimiters.computeIfAbsent(
            key, k -> new SlidingWindowRateLimiter(maxRequests, windowMs)
        );
        
        if (!limiter.tryAcquire()) {
            totalLimited.increment();
            return RateLimitResult.limited("Rate limit exceeded", limiter.getWaitTimeMs());
        }
        
        return RateLimitResult.allow();
    }

    // ==================== Redis集成 (预留) ====================

    /**
     * 使用Redis进行分布式限流
     * 注意：需要Redis连接，此为预留方法
     */
    public RateLimitResult checkWithRedis(String key, int maxRequests, long windowMs) {
        // TODO: 实现Redis滑动窗口限流
        // Redis实现思路：
        // 1. ZADD key timestamp request_id
        // 2. ZREMRANGEBYSCORE key 0 timestamp-window
        // 3. ZCARD key 获取计数
        // 4. 如果超过阈值，拒绝请求
        return checkSlidingWindow(key, maxRequests, windowMs);
    }

    // ==================== 私有方法 ====================

    /**
     * 初始化预定义限流器
     */
    private void initializeLimiters() {
        // WebSocket连接限流
        slidingWindowLimiters.put("ws:connect", new SlidingWindowRateLimiter(10, 10000));
        
        // 消息发送限流
        slidingWindowLimiters.put("ws:message", new SlidingWindowRateLimiter(
            config.getUserMessageRate(), 60000));
        
        // 心跳限流
        slidingWindowLimiters.put("ws:heartbeat", new SlidingWindowRateLimiter(120, 60000));
    }

    /**
     * 获取IP统计
     */
    private LongAdder getIpStats(String ip) {
        return ipStats.computeIfAbsent(ip, k -> new LongAdder());
    }

    /**
     * 获取用户统计
     */
    private LongAdder getUserStats(Long userId) {
        return userStats.computeIfAbsent(userId, k -> new LongAdder());
    }

    /**
     * 启动定时任务
     */
    private void startScheduler() {
        if (running) return;
        running = true;
        
        // 定期清理过期限流器
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupExpiredLimiters();
            } catch (Exception e) {
                // 忽略
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 清理过期的限流器
     */
    private void cleanupExpiredLimiters() {
        long now = System.currentTimeMillis();
        long threshold = TimeUnit.HOURS.toMillis(1);
        
        // 清理IP限流器
        ipLimiters.entrySet().removeIf(entry -> 
            now - entry.getValue().getLastAccessTime() > threshold);
        
        // 清理用户限流器
        userLimiters.entrySet().removeIf(entry ->
            now - entry.getValue().getLastAccessTime() > threshold);
        
        // 清理滑动窗口限流器
        slidingWindowLimiters.entrySet().removeIf(entry ->
            now - entry.getValue().getLastAccessTime() > threshold);
        
        // 清理登录限流器
        loginLimiters.entrySet().removeIf(entry ->
            now - entry.getValue().getLastAccessTime() > threshold);
    }

    // ==================== 配置和统计方法 ====================

    /**
     * 更新配置
     */
    public void updateConfig(RateLimitConfig newConfig) {
        this.config = newConfig;
    }

    /**
     * 获取当前配置
     */
    public RateLimitConfig getConfig() {
        return config;
    }

    /**
     * 获取限流统计
     */
    public RateLimitStats getStats() {
        return new RateLimitStats(
            totalRequests.sum(),
            totalLimited.sum(),
            totalWhitelisted.sum(),
            ipLimiters.size(),
            userLimiters.size(),
            apiLimiters.size(),
            slidingWindowLimiters.size()
        );
    }

    /**
     * 重置统计
     */
    public void resetStats() {
        totalRequests.reset();
        totalLimited.reset();
        totalWhitelisted.reset();
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    // ==================== 内部类 ====================

    /**
     * 令牌桶实现
     */
    public static class TokenBucket {
        private final double maxTokens;
        private final double refillRate; // 每秒补充的令牌数
        private volatile double tokens;
        private volatile long lastRefillTime;
        private volatile long lastAccessTime;
        
        public TokenBucket(int qps) {
            this(qps, qps);
        }
        
        public TokenBucket(int qps, int burstCapacity) {
            this.maxTokens = burstCapacity;
            this.refillRate = qps;
            this.tokens = burstCapacity;
            this.lastRefillTime = System.currentTimeMillis();
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public synchronized boolean tryAcquire() {
            refill();
            lastAccessTime = System.currentTimeMillis();
            
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
        
        public synchronized boolean tryAcquire(int permits) {
            refill();
            lastAccessTime = System.currentTimeMillis();
            
            if (tokens >= permits) {
                tokens -= permits;
                return true;
            }
            return false;
        }
        
        public long getWaitTimeMs() {
            if (tokens >= 1) return 0;
            return (long) ((1 - tokens) / refillRate * 1000);
        }
        
        public double getCurrentTokens() {
            return tokens;
        }
        
        public long getLastAccessTime() {
            return lastAccessTime;
        }
        
        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            double refill = (double) elapsed / 1000 * refillRate;
            
            tokens = Math.min(maxTokens, tokens + refill);
            lastRefillTime = now;
        }
    }

    /**
     * 滑动窗口限流器
     */
    public static class SlidingWindowRateLimiter {
        private final int maxRequests;
        private final long windowMs;
        private final ConcurrentHashMap<Long, AtomicLong> windows = new ConcurrentHashMap<>();
        private volatile long lastAccessTime;
        
        public SlidingWindowRateLimiter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public boolean tryAcquire() {
            lastAccessTime = System.currentTimeMillis();
            long now = System.currentTimeMillis();
            long windowStart = now - windowMs;
            
            // 清理过期窗口
            windows.entrySet().removeIf(entry -> entry.getKey() < windowStart);
            
            // 计算当前窗口内的请求数
            int currentCount = windows.values().stream()
                .mapToInt(v -> (int) v.get())
                .sum();
            
            if (currentCount < maxRequests) {
                windows.computeIfAbsent(now, k -> new AtomicLong()).incrementAndGet();
                return true;
            }
            
            return false;
        }
        
        public long getWaitTimeMs() {
            return windowMs / maxRequests;
        }
        
        public long getLastAccessTime() {
            return lastAccessTime;
        }
        
        public int getCurrentCount() {
            long now = System.currentTimeMillis();
            long windowStart = now - windowMs;
            
            return windows.entrySet().stream()
                .filter(e -> e.getKey() >= windowStart)
                .mapToInt(e -> (int) e.getValue().get())
                .sum();
        }
    }

    /**
     * 全局限流器
     */
    public static class GlobalRateLimiter {
        private final TokenBucket bucket;
        
        public GlobalRateLimiter(int qps) {
            this.bucket = new TokenBucket(qps);
        }
        
        public boolean tryAcquire() {
            return bucket.tryAcquire();
        }
    }

    /**
     * 登录限流器
     */
    public static class LoginRateLimiter {
        private final int maxAttempts;
        private final long banDuration;
        private final int failureThreshold;
        
        private final AtomicLong attempts = new AtomicLong(0);
        private volatile long lastAttemptTime;
        private volatile long banStartTime;
        private volatile boolean banned = false;
        private volatile long lastAccessTime;
        
        public LoginRateLimiter(int maxAttempts, long banDuration, int failureThreshold) {
            this.maxAttempts = maxAttempts;
            this.banDuration = banDuration;
            this.failureThreshold = failureThreshold;
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public boolean tryAttempt() {
            lastAccessTime = System.currentTimeMillis();
            
            if (banned) {
                return false;
            }
            
            long current = attempts.incrementAndGet();
            lastAttemptTime = System.currentTimeMillis();
            
            return current <= maxAttempts;
        }
        
        public void recordFailure() {
            if (attempts.get() >= failureThreshold) {
                banned = true;
                banStartTime = System.currentTimeMillis();
            }
        }
        
        public void recordSuccess() {
            attempts.set(0);
            banned = false;
        }
        
        public boolean isBanned() {
            if (!banned) return false;
            
            if (System.currentTimeMillis() - banStartTime >= banDuration) {
                banned = false;
                attempts.set(0);
                return false;
            }
            return true;
        }
        
        public long getBanRemainingMs() {
            if (!banned) return 0;
            long elapsed = System.currentTimeMillis() - banStartTime;
            return Math.max(0, banDuration - elapsed);
        }
        
        public long getWaitTimeMs() {
            long current = attempts.get();
            if (current <= maxAttempts) return 0;
            return 1000; // 等待1秒
        }
        
        public long getLastAccessTime() {
            return lastAccessTime;
        }
    }

    /**
     * 限流结果
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final boolean limited;
        private final String reason;
        private final long waitTimeMs;
        
        private RateLimitResult(boolean allowed, boolean limited, String reason, long waitTimeMs) {
            this.allowed = allowed;
            this.limited = limited;
            this.reason = reason;
            this.waitTimeMs = waitTimeMs;
        }
        
        public static RateLimitResult allow() {
            return new RateLimitResult(true, false, null, 0);
        }
        
        public static RateLimitResult limited(String reason) {
            return new RateLimitResult(false, true, reason, 0);
        }
        
        public static RateLimitResult limited(String reason, long waitTimeMs) {
            return new RateLimitResult(false, true, reason, waitTimeMs);
        }
        
        public boolean isAllowed() { return allowed; }
        public boolean isLimited() { return limited; }
        public String getReason() { return reason; }
        public long getWaitTimeMs() { return waitTimeMs; }
    }

    /**
     * 限流统计
     */
    public static class RateLimitStats {
        private final long totalRequests;
        private final long totalLimited;
        private final long totalWhitelisted;
        private final int ipLimiterCount;
        private final int userLimiterCount;
        private final int apiLimiterCount;
        private final int slidingWindowLimiterCount;
        
        public RateLimitStats(long totalRequests, long totalLimited, long totalWhitelisted,
                             int ipLimiterCount, int userLimiterCount, int apiLimiterCount,
                             int slidingWindowLimiterCount) {
            this.totalRequests = totalRequests;
            this.totalLimited = totalLimited;
            this.totalWhitelisted = totalWhitelisted;
            this.ipLimiterCount = ipLimiterCount;
            this.userLimiterCount = userLimiterCount;
            this.apiLimiterCount = apiLimiterCount;
            this.slidingWindowLimiterCount = slidingWindowLimiterCount;
        }
        
        public double getLimitedRate() {
            return totalRequests > 0 ? (double) totalLimited / totalRequests * 100 : 0;
        }
        
        public int getTotalLimiterCount() {
            return ipLimiterCount + userLimiterCount + apiLimiterCount + slidingWindowLimiterCount;
        }
        
        // Getters
        public long getTotalRequests() { return totalRequests; }
        public long getTotalLimited() { return totalLimited; }
        public long getTotalWhitelisted() { return totalWhitelisted; }
        public int getIpLimiterCount() { return ipLimiterCount; }
        public int getUserLimiterCount() { return userLimiterCount; }
        public int getApiLimiterCount() { return apiLimiterCount; }
        public int getSlidingWindowLimiterCount() { return slidingWindowLimiterCount; }
    }
}
