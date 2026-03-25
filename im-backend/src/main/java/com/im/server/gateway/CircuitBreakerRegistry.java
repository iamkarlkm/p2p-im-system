package com.im.server.gateway;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断器注册表
 * 
 * 管理所有熔断器实例，支持：
 * - 熔断器的创建、获取、删除
 * - 熔断器状态监控
 * - 熔断器自动清理
 * - 熔断器分组管理
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class CircuitBreakerRegistry {

    // ==================== 单例实例 ====================
    
    private static volatile CircuitBreakerRegistry instance;
    
    // ==================== 熔断器存储 ====================
    
    /** 熔断器存储表 */
    private final ConcurrentMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    
    /** 熔断器分组 */
    private final ConcurrentMap<String, ConcurrentMap<String, CircuitBreaker>> breakerGroups = new ConcurrentHashMap<>();
    
    /** 熔断器配置表 */
    private final ConcurrentMap<String, CircuitBreakerConfig> breakerConfigs = new ConcurrentHashMap<>();
    
    /** 熔断器计数器 */
    private final AtomicInteger breakerCounter = new AtomicInteger(0);
    
    /** 熔断器统计信息收集器 */
    private final ConcurrentMap<String, CircuitBreakerMetrics> metricsCollector = new ConcurrentHashMap<>();

    // ==================== 调度器 ====================
    
    /** 定时任务执行器 */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /** 是否正在运行 */
    private volatile boolean running = false;

    // ==================== 预定义熔断器配置 ====================

    /** 默认熔断器配置 */
    private static final CircuitBreakerConfig DEFAULT_CONFIG = CircuitBreakerConfig.builder()
        .failureThreshold(50)
        .minRequestVolume(100)
        .failureRateThreshold(50)
        .circuitOpenDuration(30000)
        .halfOpenMaxAttempts(3)
        .halfOpenSuccessRateThreshold(50)
        .build();

    /** 快速失败配置 - 用于高优先级服务 */
    private static final CircuitBreakerConfig FAST_FAIL_CONFIG = CircuitBreakerConfig.builder()
        .failureThreshold(10)
        .minRequestVolume(20)
        .failureRateThreshold(30)
        .circuitOpenDuration(10000)
        .halfOpenMaxAttempts(2)
        .halfOpenSuccessRateThreshold(50)
        .build();

    /** 宽容配置 - 用于低优先级服务 */
    private static final CircuitBreakerConfig GRACEFUL_CONFIG = CircuitBreakerConfig.builder()
        .failureThreshold(100)
        .minRequestVolume(200)
        .failureRateThreshold(60)
        .circuitOpenDuration(60000)
        .halfOpenMaxAttempts(5)
        .halfOpenSuccessRateThreshold(40)
        .build();

    // ==================== 构造函数 ====================

    /**
     * 私有构造函数 - 单例模式
     */
    private CircuitBreakerRegistry() {
        // 初始化预定义熔断器
        initializeDefaultBreakers();
        // 启动定时任务
        startScheduler();
    }

    /**
     * 获取单例实例
     */
    public static CircuitBreakerRegistry getInstance() {
        if (instance == null) {
            synchronized (CircuitBreakerRegistry.class) {
                if (instance == null) {
                    instance = new CircuitBreakerRegistry();
                }
            }
        }
        return instance;
    }

    // ==================== 熔断器管理 ====================

    /**
     * 获取熔断器，如果不存在则创建
     * 
     * @param name 熔断器名称
     * @return 熔断器实例
     */
    public CircuitBreaker getOrCreate(String name) {
        return circuitBreakers.computeIfAbsent(name, k -> createBreaker(name, DEFAULT_CONFIG));
    }

    /**
     * 获取熔断器，使用指定配置
     */
    public CircuitBreaker getOrCreate(String name, CircuitBreakerConfig config) {
        return circuitBreakers.computeIfAbsent(name, k -> createBreaker(name, config));
    }

    /**
     * 获取熔断器，使用预定义配置类型
     */
    public CircuitBreaker getOrCreate(String name, ConfigType configType) {
        CircuitBreakerConfig config = getConfigByType(configType);
        return getOrCreate(name, config);
    }

    /**
     * 获取熔断器，如果不存在返回null
     */
    public CircuitBreaker get(String name) {
        return circuitBreakers.get(name);
    }

    /**
     * 检查熔断器是否存在
     */
    public boolean exists(String name) {
        return circuitBreakers.containsKey(name);
    }

    /**
     * 移除熔断器
     */
    public CircuitBreaker remove(String name) {
        CircuitBreaker breaker = circuitBreakers.remove(name);
        if (breaker != null) {
            // 从所有分组中移除
            for (ConcurrentMap<String, CircuitBreaker> group : breakerGroups.values()) {
                group.remove(name);
            }
            // 移除配置
            breakerConfigs.remove(name);
            metricsCollector.remove(name);
        }
        return breaker;
    }

    /**
     * 获取所有熔断器名称
     */
    public java.util.Set<String> getAllBreakerNames() {
        return new java.util.HashSet<>(circuitBreakers.keySet());
    }

    /**
     * 获取熔断器数量
     */
    public int getBreakerCount() {
        return circuitBreakers.size();
    }

    // ==================== 分组管理 ====================

    /**
     * 创建熔断器并添加到分组
     */
    public CircuitBreaker createInGroup(String name, String groupName) {
        return createInGroup(name, groupName, DEFAULT_CONFIG);
    }

    /**
     * 创建熔断器并添加到分组，使用指定配置
     */
    public CircuitBreaker createInGroup(String name, String groupName, CircuitBreakerConfig config) {
        CircuitBreaker breaker = getOrCreate(name, config);
        breakerGroups.computeIfAbsent(groupName, k -> new ConcurrentHashMap<>())
                     .put(name, breaker);
        return breaker;
    }

    /**
     * 获取分组中的所有熔断器
     */
    public java.util.Collection<CircuitBreaker> getBreakersInGroup(String groupName) {
        ConcurrentMap<String, CircuitBreaker> group = breakerGroups.get(groupName);
        return group != null ? group.values() : java.util.Collections.emptyList();
    }

    /**
     * 获取所有分组名称
     */
    public java.util.Set<String> getAllGroupNames() {
        return new java.util.HashSet<>(breakerGroups.keySet());
    }

    /**
     * 移除整个分组
     */
    public void removeGroup(String groupName) {
        breakerGroups.remove(groupName);
    }

    // ==================== 批量操作 ====================

    /**
     * 关闭所有熔断器
     */
    public void closeAll() {
        circuitBreakers.values().forEach(CircuitBreaker::forceClosed);
    }

    /**
     * 打开所有熔断器
     */
    public void openAll() {
        circuitBreakers.values().forEach(CircuitBreaker::forceOpen);
    }

    /**
     * 重置所有熔断器
     */
    public void resetAll() {
        circuitBreakers.values().forEach(CircuitBreaker::reset);
    }

    /**
     * 清理未使用的熔断器
     */
    public void cleanupUnused(long unusedThresholdMs) {
        long now = System.currentTimeMillis();
        circuitBreakers.entrySet().removeIf(entry -> {
            CircuitBreaker breaker = entry.getValue();
            return (now - breaker.getStats().getTimeUntilRetry()) > unusedThresholdMs 
                   && breaker.isClosed()
                   && breaker.getTotalRequests() == 0;
        });
    }

    /**
     * 获取所有熔断器状态摘要
     */
    public RegistrySummary getSummary() {
        int closed = 0, open = 0, halfOpen = 0;
        long totalRequests = 0, totalSuccesses = 0, totalFailures = 0;
        
        for (CircuitBreaker breaker : circuitBreakers.values()) {
            CircuitBreaker.CircuitBreakerStats stats = breaker.getStats();
            switch (stats.getState()) {
                case CLOSED -> closed++;
                case OPEN -> open++;
                case HALF_OPEN -> halfOpen++;
            }
            totalRequests += stats.getTotalRequests();
            totalSuccesses += stats.getTotalSuccesses();
            totalFailures += stats.getTotalFailures();
        }
        
        return new RegistrySummary(
            circuitBreakers.size(),
            closed, open, halfOpen,
            totalRequests, totalSuccesses, totalFailures
        );
    }

    // ==================== 监控和管理 ====================

    /**
     * 获取熔断器配置
     */
    public CircuitBreakerConfig getConfig(String name) {
        return breakerConfigs.get(name);
    }

    /**
     * 获取熔断器统计信息
     */
    public CircuitBreaker.CircuitBreakerStats getStats(String name) {
        CircuitBreaker breaker = circuitBreakers.get(name);
        return breaker != null ? breaker.getStats() : null;
    }

    /**
     * 获取所有熔断器的详细统计
     */
    public java.util.List<CircuitBreaker.CircuitBreakerStats> getAllStats() {
        return circuitBreakers.values().stream()
            .map(CircuitBreaker::getStats)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取打开状态的熔断器
     */
    public java.util.List<CircuitBreaker> getOpenBreakers() {
        return circuitBreakers.values().stream()
            .filter(CircuitBreaker::isOpen)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取半开状态的熔断器
     */
    public java.util.List<CircuitBreaker> getHalfOpenBreakers() {
        return circuitBreakers.values().stream()
            .filter(CircuitBreaker::isHalfOpen)
            .collect(java.util.stream.Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 创建熔断器
     */
    private CircuitBreaker createBreaker(String name, CircuitBreakerConfig config) {
        breakerCounter.incrementAndGet();
        CircuitBreaker breaker = new CircuitBreaker(
            name,
            config.getFailureThreshold(),
            config.getMinRequestVolume(),
            config.getFailureRateThreshold(),
            config.getCircuitOpenDuration(),
            config.getHalfOpenMaxAttempts(),
            config.getHalfOpenSuccessRateThreshold()
        );
        breakerConfigs.put(name, config);
        return breaker;
    }

    /**
     * 初始化默认熔断器
     */
    private void initializeDefaultBreakers() {
        // 用户服务熔断器
        getOrCreate("user-service", DEFAULT_CONFIG);
        
        // 消息服务熔断器
        getOrCreate("message-service", FAST_FAIL_CONFIG);
        
        // 群组服务熔断器
        getOrCreate("group-service", DEFAULT_CONFIG);
        
        // 文件服务熔断器
        getOrCreate("file-service", GRACEFUL_CONFIG);
        
        // 外部API熔断器
        getOrCreate("external-api", FAST_FAIL_CONFIG);
        
        // 数据库熔断器
        getOrCreate("database", FAST_FAIL_CONFIG);
    }

    /**
     * 启动定时任务
     */
    private void startScheduler() {
        if (running) return;
        running = true;
        
        // 定期收集指标
        scheduler.scheduleAtFixedRate(() -> {
            try {
                collectMetrics();
            } catch (Exception e) {
                // 忽略异常
            }
        }, 1, 1, TimeUnit.MINUTES);
        
        // 定期清理未使用的熔断器
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupUnused(TimeUnit.HOURS.toMillis(24));
            } catch (Exception e) {
                // 忽略异常
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 收集指标
     */
    private void collectMetrics() {
        circuitBreakers.forEach((name, breaker) -> {
            CircuitBreaker.CircuitBreakerStats stats = breaker.getStats();
            metricsCollector.compute(name, (k, existing) -> {
                if (existing == null) {
                    existing = new CircuitBreakerMetrics(name);
                }
                existing.update(stats);
                return existing;
            });
        });
    }

    /**
     * 根据类型获取配置
     */
    private CircuitBreakerConfig getConfigByType(ConfigType type) {
        return switch (type) {
            case DEFAULT -> DEFAULT_CONFIG;
            case FAST_FAIL -> FAST_FAIL_CONFIG;
            case GRACEFUL -> GRACEFUL_CONFIG;
        };
    }

    /**
     * 关闭注册表
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
        circuitBreakers.clear();
        breakerGroups.clear();
    }

    // ==================== 内部类和枚举 ====================

    /**
     * 配置类型枚举
     */
    public enum ConfigType {
        /** 默认配置 */
        DEFAULT,
        /** 快速失败配置 */
        FAST_FAIL,
        /** 宽容配置 */
        GRACEFUL
    }

    /**
     * 熔断器配置
     */
    public static class CircuitBreakerConfig {
        private int failureThreshold = 50;
        private int minRequestVolume = 100;
        private int failureRateThreshold = 50;
        private long circuitOpenDuration = 30000;
        private int halfOpenMaxAttempts = 3;
        private int halfOpenSuccessRateThreshold = 50;

        private CircuitBreakerConfig() {}

        public static CircuitBreakerConfig builder() {
            return new CircuitBreakerConfig();
        }

        public CircuitBreakerConfig failureThreshold(int failureThreshold) {
            this.failureThreshold = failureThreshold;
            return this;
        }

        public CircuitBreakerConfig minRequestVolume(int minRequestVolume) {
            this.minRequestVolume = minRequestVolume;
            return this;
        }

        public CircuitBreakerConfig failureRateThreshold(int failureRateThreshold) {
            this.failureRateThreshold = failureRateThreshold;
            return this;
        }

        public CircuitBreakerConfig circuitOpenDuration(long circuitOpenDuration) {
            this.circuitOpenDuration = circuitOpenDuration;
            return this;
        }

        public CircuitBreakerConfig halfOpenMaxAttempts(int halfOpenMaxAttempts) {
            this.halfOpenMaxAttempts = halfOpenMaxAttempts;
            return this;
        }

        public CircuitBreakerConfig halfOpenSuccessRateThreshold(int halfOpenSuccessRateThreshold) {
            this.halfOpenSuccessRateThreshold = halfOpenSuccessRateThreshold;
            return this;
        }

        public CircuitBreakerConfig build() {
            return this;
        }

        // Getters
        public int getFailureThreshold() { return failureThreshold; }
        public int getMinRequestVolume() { return minRequestVolume; }
        public int getFailureRateThreshold() { return failureRateThreshold; }
        public long getCircuitOpenDuration() { return circuitOpenDuration; }
        public int getHalfOpenMaxAttempts() { return halfOpenMaxAttempts; }
        public int getHalfOpenSuccessRateThreshold() { return halfOpenSuccessRateThreshold; }
    }

    /**
     * 注册表摘要
     */
    public static class RegistrySummary {
        private final int totalCount;
        private final int closedCount;
        private final int openCount;
        private final int halfOpenCount;
        private final long totalRequests;
        private final long totalSuccesses;
        private final long totalFailures;

        public RegistrySummary(int totalCount, int closedCount, int openCount, int halfOpenCount,
                             long totalRequests, long totalSuccesses, long totalFailures) {
            this.totalCount = totalCount;
            this.closedCount = closedCount;
            this.openCount = openCount;
            this.halfOpenCount = halfOpenCount;
            this.totalRequests = totalRequests;
            this.totalSuccesses = totalSuccesses;
            this.totalFailures = totalFailures;
        }

        public int getTotalCount() { return totalCount; }
        public int getClosedCount() { return closedCount; }
        public int getOpenCount() { return openCount; }
        public int getHalfOpenCount() { return halfOpenCount; }
        public long getTotalRequests() { return totalRequests; }
        public long getTotalSuccesses() { return totalSuccesses; }
        public long getTotalFailures() { return totalFailures; }
    }

    /**
     * 熔断器指标收集器
     */
    public static class CircuitBreakerMetrics {
        private final String name;
        private long lastRequests;
        private long lastSuccesses;
        private long lastFailures;
        private double lastSuccessRate;
        private long lastUpdateTime;

        public CircuitBreakerMetrics(String name) {
            this.name = name;
        }

        public void update(CircuitBreaker.CircuitBreakerStats stats) {
            this.lastRequests = stats.getTotalRequests();
            this.lastSuccesses = stats.getTotalSuccesses();
            this.lastFailures = stats.getTotalFailures();
            this.lastSuccessRate = stats.getSuccessRate();
            this.lastUpdateTime = System.currentTimeMillis();
        }

        public String getName() { return name; }
        public long getLastRequests() { return lastRequests; }
        public long getLastSuccesses() { return lastSuccesses; }
        public long getLastFailures() { return lastFailures; }
        public double getLastSuccessRate() { return lastSuccessRate; }
        public long getLastUpdateTime() { return lastUpdateTime; }
    }
}
