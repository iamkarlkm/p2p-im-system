package com.im.server.gateway;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 熔断器实现
 * 
 * 实现三态熔断模式：
 * - CLOSED: 正常状态，请求通过，统计失败
 * - OPEN: 熔断状态，所有请求快速失败
 * - HALF_OPEN: 半开状态，允许测试请求
 * 
 * 使用滑动窗口统计失败率，支持自动恢复
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class CircuitBreaker {

    // ==================== 熔断器状态 ====================
    
    /** 熔断器状态枚举 */
    public enum State {
        /** 关闭状态 - 正常请求通过 */
        CLOSED,
        /** 打开状态 - 所有请求快速失败 */
        OPEN,
        /** 半开状态 - 允许部分测试请求 */
        HALF_OPEN
    }

    /** 熔断结果枚举 */
    public enum Result {
        /** 请求成功 */
        SUCCESS,
        /** 请求失败 */
        FAILURE,
        /** 被熔断器拒绝 */
        REJECTED
    }

    // ==================== 配置参数 ====================
    
    /** 熔断器名称 */
    private final String name;
    
    /** 触发熔断的连续失败次数阈值 */
    private final int failureThreshold;
    
    /** 触发熔断的滑动窗口内的最小请求数 */
    private final int minRequestVolume;
    
    /** 触发熔断的失败率阈值 (%) */
    private final int failureRateThreshold;
    
    /** 熔断持续时间 (毫秒) */
    private final long circuitOpenDuration;
    
    /** 半开状态下允许的测试请求数 */
    private final int halfOpenMaxAttempts;
    
    /** 半开状态下成功阈值 (%) */
    private final int halfOpenSuccessRateThreshold;

    // ==================== 状态变量 ====================
    
    /** 当前状态 */
    private volatile State currentState = State.CLOSED;
    
    /** 连续失败计数 */
    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    
    /** 连续成功计数 (半开状态用) */
    private final AtomicInteger consecutiveSuccesses = new AtomicInteger(0);
    
    /** 半开状态下的测试请求计数 */
    private final AtomicInteger halfOpenAttempts = new AtomicInteger(0);
    
    /** 最后失败时间戳 */
    private volatile long lastFailureTime = 0;
    
    /** 熔断打开时间戳 */
    private volatile long circuitOpenedAt = 0;
    
    /** 总成功计数 */
    private final AtomicLong totalSuccesses = new AtomicLong(0);
    
    /** 总失败计数 */
    private final AtomicLong totalFailures = new AtomicLong(0);
    
    /** 总被拒绝计数 */
    private final AtomicLong totalRejected = new AtomicLong(0);
    
    /** 总请求计数 */
    private final AtomicLong totalRequests = new AtomicLong(0);
    
    /** 状态变更锁 */
    private final ReentrantLock stateLock = new ReentrantLock();
    
    /** 滑动窗口数组 - 记录每个时间窗口的失败数 */
    private final SlidingWindowCounter[] slidingWindow;
    
    /** 滑动窗口大小 (毫秒) */
    private final long windowSizeMs;
    
    /** 滑动窗口桶数量 */
    private final int bucketCount;
    
    /** 当前桶索引 */
    private volatile int currentBucket = 0;
    
    /** 最后一个请求时间 */
    private volatile long lastRequestTime = 0;

    // ==================== 构造函数 ====================

    /**
     * 使用默认配置创建熔断器
     */
    public CircuitBreaker(String name) {
        this(name, 50, 1000, 50, 30000, 3, 50);
    }

    /**
     * 使用自定义配置创建熔断器
     */
    public CircuitBreaker(String name, int failureThreshold, int minRequestVolume,
                         int failureRateThreshold, long circuitOpenDuration,
                         int halfOpenMaxAttempts, int halfOpenSuccessRateThreshold) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.minRequestVolume = minRequestVolume;
        this.failureRateThreshold = failureRateThreshold;
        this.circuitOpenDuration = circuitOpenDuration;
        this.halfOpenMaxAttempts = halfOpenMaxAttempts;
        this.halfOpenSuccessRateThreshold = halfOpenSuccessRateThreshold;
        
        // 初始化滑动窗口
        this.windowSizeMs = 60000; // 1分钟窗口
        this.bucketCount = 60; // 60个桶，每秒一个
        this.slidingWindow = new SlidingWindowCounter[bucketCount];
        for (int i = 0; i < bucketCount; i++) {
            slidingWindow[i] = new SlidingWindowCounter();
        }
    }

    // ==================== 核心方法 ====================

    /**
     * 检查是否允许请求通过
     * 
     * @return true 如果允许请求，否则返回false
     */
    public boolean allowRequest() {
        totalRequests.incrementAndGet();
        lastRequestTime = System.currentTimeMillis();
        
        stateLock.lock();
        try {
            switch (currentState) {
                case CLOSED:
                    return allowInClosedState();
                    
                case OPEN:
                    return allowInOpenState();
                    
                case HALF_OPEN:
                    return allowInHalfOpenState();
                    
                default:
                    return false;
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 记录请求结果
     * 
     * @param result 请求结果
     */
    public void recordResult(Result result) {
        switch (result) {
            case SUCCESS:
                recordSuccess();
                break;
            case FAILURE:
                recordFailure();
                break;
            case REJECTED:
                totalRejected.incrementAndGet();
                break;
        }
    }

    /**
     * 执行带熔断保护的请求
     * 
     * @param supplier 要执行的请求
     * @param fallback 降级处理函数
     * @return 请求结果或降级结果
     */
    public <T> T execute(Supplier<T> supplier, Supplier<T> fallback) {
        if (allowRequest()) {
            try {
                T result = supplier.get();
                recordResult(Result.SUCCESS);
                return result;
            } catch (Exception e) {
                recordResult(Result.FAILURE);
                if (fallback != null) {
                    return fallback.get();
                }
                throw e;
            }
        } else {
            recordResult(Result.REJECTED);
            if (fallback != null) {
                return fallback.get();
            }
            throw new CircuitBreakerOpenException("Circuit breaker is open: " + name);
        }
    }

    /**
     * 执行带熔断保护的请求 (无降级)
     */
    public <T> T execute(Supplier<T> supplier) {
        return execute(supplier, null);
    }

    /**
     * 执行带熔断保护的Runnable
     */
    public void execute(Runnable runnable, Runnable fallback) {
        if (allowRequest()) {
            try {
                runnable.run();
                recordResult(Result.SUCCESS);
            } catch (Exception e) {
                recordResult(Result.FAILURE);
                if (fallback != null) {
                    fallback.run();
                }
            }
        } else {
            recordResult(Result.REJECTED);
            if (fallback != null) {
                fallback.run();
            }
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 关闭状态下检查是否允许请求
     */
    private boolean allowInClosedState() {
        // 检查滑动窗口失败率
        if (getSlidingWindowFailureCount() >= failureThreshold) {
            transitionToOpen();
            return false;
        }
        return true;
    }

    /**
     * 打开状态下检查是否应该进入半开状态
     */
    private boolean allowInOpenState() {
        long elapsed = System.currentTimeMillis() - circuitOpenedAt;
        if (elapsed >= circuitOpenDuration) {
            transitionToHalfOpen();
            return allowInHalfOpenState();
        }
        return false;
    }

    /**
     * 半开状态下允许部分测试请求
     */
    private boolean allowInHalfOpenState() {
        int attempts = halfOpenAttempts.incrementAndGet();
        if (attempts <= halfOpenMaxAttempts) {
            return true;
        }
        return false;
    }

    /**
     * 记录成功结果
     */
    private void recordSuccess() {
        totalSuccesses.incrementAndGet();
        consecutiveFailures.set(0);
        
        if (currentState == State.HALF_OPEN) {
            int successes = consecutiveSuccesses.incrementAndGet();
            int attempts = halfOpenAttempts.get();
            
            // 计算半开状态下的成功率
            if (attempts > 0) {
                double successRate = (double) successes / attempts * 100;
                if (successRate >= halfOpenSuccessRateThreshold) {
                    transitionToClosed();
                } else if (attempts >= halfOpenMaxAttempts) {
                    // 达到最大测试次数但成功率不足，重置并重新打开
                    transitionToOpen();
                }
            }
        }
    }

    /**
     * 记录失败结果
     */
    private void recordFailure() {
        totalFailures.incrementAndGet();
        consecutiveSuccesses.set(0);
        lastFailureTime = System.currentTimeMillis();
        
        // 更新滑动窗口
        updateSlidingWindow();
        
        // 增加连续失败计数
        int failures = consecutiveFailures.incrementAndGet();
        
        if (currentState == State.HALF_OPEN) {
            // 半开状态下任何失败都立即打开
            transitionToOpen();
        } else if (currentState == State.CLOSED) {
            // 关闭状态下检查是否达到熔断阈值
            if (failures >= failureThreshold || getSlidingWindowFailureRate() >= failureRateThreshold) {
                transitionToOpen();
            }
        }
    }

    /**
     * 更新滑动窗口
     */
    private void updateSlidingWindow() {
        int bucket = (int) ((System.currentTimeMillis() / 1000) % bucketCount);
        if (bucket != currentBucket) {
            // 重置新桶
            slidingWindow[bucket].reset();
            currentBucket = bucket;
        }
        slidingWindow[bucket].incrementFailure();
    }

    /**
     * 获取滑动窗口内的失败总数
     */
    private int getSlidingWindowFailureCount() {
        int total = 0;
        for (SlidingWindowCounter counter : slidingWindow) {
            total += counter.getFailureCount();
        }
        return total;
    }

    /**
     * 获取滑动窗口内的总请求数
     */
    private int getSlidingWindowTotalCount() {
        int total = 0;
        for (SlidingWindowCounter counter : slidingWindow) {
            total += counter.getTotalCount();
        }
        return total;
    }

    /**
     * 获取滑动窗口内的失败率
     */
    private double getSlidingWindowFailureRate() {
        int total = getSlidingWindowTotalCount();
        if (total == 0) return 0;
        return (double) getSlidingWindowFailureCount() / total * 100;
    }

    /**
     * 状态转换到打开
     */
    private void transitionToOpen() {
        if (currentState != State.OPEN) {
            currentState = State.OPEN;
            circuitOpenedAt = System.currentTimeMillis();
            consecutiveFailures.set(0);
            consecutiveSuccesses.set(0);
            halfOpenAttempts.set(0);
            onStateTransition(State.OPEN);
        }
    }

    /**
     * 状态转换到半开
     */
    private void transitionToHalfOpen() {
        if (currentState != State.HALF_OPEN) {
            currentState = State.HALF_OPEN;
            consecutiveSuccesses.set(0);
            halfOpenAttempts.set(0);
            onStateTransition(State.HALF_OPEN);
        }
    }

    /**
     * 状态转换到关闭
     */
    private void transitionToClosed() {
        if (currentState != State.CLOSED) {
            currentState = State.CLOSED;
            consecutiveFailures.set(0);
            consecutiveSuccesses.set(0);
            halfOpenAttempts.set(0);
            // 重置滑动窗口
            for (SlidingWindowCounter counter : slidingWindow) {
                counter.reset();
            }
            onStateTransition(State.CLOSED);
        }
    }

    /**
     * 状态变更回调 - 可被子类重写
     */
    protected void onStateTransition(State newState) {
        // 默认空实现，可重写
    }

    // ==================== 公共查询方法 ====================

    /**
     * 获取当前状态
     */
    public State getState() {
        return currentState;
    }

    /**
     * 检查是否熔断打开
     */
    public boolean isOpen() {
        return currentState == State.OPEN;
    }

    /**
     * 检查是否熔断关闭
     */
    public boolean isClosed() {
        return currentState == State.CLOSED;
    }

    /**
     * 检查是否半开状态
     */
    public boolean isHalfOpen() {
        return currentState == State.HALF_OPEN;
    }

    /**
     * 获取熔断器名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取连续失败次数
     */
    public int getConsecutiveFailures() {
        return consecutiveFailures.get();
    }

    /**
     * 获取总成功数
     */
    public long getTotalSuccesses() {
        return totalSuccesses.get();
    }

    /**
     * 获取总失败数
     */
    public long getTotalFailures() {
        return totalFailures.get();
    }

    /**
     * 获取总被拒绝数
     */
    public long getTotalRejected() {
        return totalRejected.get();
    }

    /**
     * 获取总请求数
     */
    public long getTotalRequests() {
        return totalRequests.get();
    }

    /**
     * 获取整体成功率
     */
    public double getSuccessRate() {
        long total = totalSuccesses.get() + totalFailures.get();
        if (total == 0) return 100.0;
        return (double) totalSuccesses.get() / total * 100;
    }

    /**
     * 获取熔断打开的剩余时间 (毫秒)
     */
    public long getTimeUntilRetry() {
        if (currentState != State.OPEN) return 0;
        long elapsed = System.currentTimeMillis() - circuitOpenedAt;
        return Math.max(0, circuitOpenDuration - elapsed);
    }

    /**
     * 获取熔断器统计信息
     */
    public CircuitBreakerStats getStats() {
        return new CircuitBreakerStats(
            name,
            currentState,
            totalRequests.get(),
            totalSuccesses.get(),
            totalFailures.get(),
            totalRejected.get(),
            getSuccessRate(),
            consecutiveFailures.get(),
            getTimeUntilRetry()
        );
    }

    /**
     * 重置熔断器
     */
    public void reset() {
        stateLock.lock();
        try {
            transitionToClosed();
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 强制打开熔断器
     */
    public void forceOpen() {
        stateLock.lock();
        try {
            transitionToOpen();
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 强制关闭熔断器
     */
    public void forceClosed() {
        stateLock.lock();
        try {
            transitionToClosed();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public String toString() {
        return String.format(
            "CircuitBreaker{name='%s', state=%s, successRate=%.2f%%, consecutiveFailures=%d, totalRequests=%d}",
            name, currentState, getSuccessRate(), consecutiveFailures.get(), totalRequests.get()
        );
    }

    // ==================== 内部类 ====================

    /**
     * 滑动窗口计数器
     */
    private static class SlidingWindowCounter {
        private final AtomicInteger failures = new AtomicInteger(0);
        private final AtomicInteger total = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();
        
        public void incrementFailure() {
            failures.incrementAndGet();
            total.incrementAndGet();
        }
        
        public void incrementTotal() {
            total.incrementAndGet();
        }
        
        public int getFailureCount() {
            return failures.get();
        }
        
        public int getTotalCount() {
            return total.get();
        }
        
        public void reset() {
            failures.set(0);
            total.set(0);
            windowStart = System.currentTimeMillis();
        }
    }

    /**
     * 熔断器统计信息
     */
    public static class CircuitBreakerStats {
        private final String name;
        private final State state;
        private final long totalRequests;
        private final long totalSuccesses;
        private final long totalFailures;
        private final long totalRejected;
        private final double successRate;
        private final int consecutiveFailures;
        private final long timeUntilRetry;

        public CircuitBreakerStats(String name, State state, long totalRequests,
                                   long totalSuccesses, long totalFailures,
                                   long totalRejected, double successRate,
                                   int consecutiveFailures, long timeUntilRetry) {
            this.name = name;
            this.state = state;
            this.totalRequests = totalRequests;
            this.totalSuccesses = totalSuccesses;
            this.totalFailures = totalFailures;
            this.totalRejected = totalRejected;
            this.successRate = successRate;
            this.consecutiveFailures = consecutiveFailures;
            this.timeUntilRetry = timeUntilRetry;
        }

        public String getName() { return name; }
        public State getState() { return state; }
        public long getTotalRequests() { return totalRequests; }
        public long getTotalSuccesses() { return totalSuccesses; }
        public long getTotalFailures() { return totalFailures; }
        public long getTotalRejected() { return totalRejected; }
        public double getSuccessRate() { return successRate; }
        public int getConsecutiveFailures() { return consecutiveFailures; }
        public long getTimeUntilRetry() { return timeUntilRetry; }

        @Override
        public String toString() {
            return String.format(
                "CircuitBreakerStats{name='%s', state=%s, totalRequests=%d, successRate=%.2f%%, consecutiveFailures=%d, timeUntilRetry=%dms}",
                name, state, totalRequests, successRate, consecutiveFailures, timeUntilRetry
            );
        }
    }

    /**
     * 熔断器打开异常
     */
    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}
