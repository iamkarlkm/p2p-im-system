package com.im.server.gateway;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 熔断处理器 - Netty Pipeline Handler
 * 
 * 集成熔断器到Netty管道，用于：
 * - 服务调用的熔断保护
 * - 下游服务故障隔离
 * - 快速失败和降级
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class CircuitBreakerHandler extends ChannelInboundHandlerAdapter {

    // ==================== 熔断器注册表 ====================
    
    private final CircuitBreakerRegistry registry;
    
    // ==================== 服务熔断器映射 ====================
    
    /** 服务名称到熔断器的映射 */
    private final ConcurrentMap<String, CircuitBreaker> serviceBreakers = new ConcurrentHashMap<>();
    
    /** 默认服务熔断器配置类型 */
    private CircuitBreakerRegistry.ConfigType defaultConfigType = CircuitBreakerRegistry.ConfigType.DEFAULT;

    // ==================== 构造函数 ====================

    /**
     * 使用默认注册表
     */
    public CircuitBreakerHandler() {
        this(CircuitBreakerRegistry.getInstance());
    }

    /**
     * 使用自定义注册表
     */
    public CircuitBreakerHandler(CircuitBreakerRegistry registry) {
        this.registry = registry;
        initializeDefaultBreakers();
    }

    // ==================== 通道事件处理 ====================

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 处理业务消息，触发熔断检查
        try {
            processMessage(ctx, msg);
        } catch (Exception e) {
            // 记录失败
            recordFailure(ctx, e);
            throw e;
        }
        
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道激活时的逻辑
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通道断开时的逻辑
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录异常作为失败
        recordFailure(ctx, cause);
        super.exceptionCaught(ctx, cause);
    }

    // ==================== 公共方法 ====================

    /**
     * 获取指定服务的熔断器
     * 
     * @param serviceName 服务名称
     * @return 熔断器
     */
    public CircuitBreaker getBreaker(String serviceName) {
        return serviceBreakers.computeIfAbsent(serviceName, 
            k -> registry.getOrCreate("netty:" + serviceName, defaultConfigType));
    }

    /**
     * 获取指定服务的熔断器，使用指定配置
     */
    public CircuitBreaker getBreaker(String serviceName, CircuitBreakerRegistry.ConfigType configType) {
        return serviceBreakers.computeIfAbsent(serviceName, 
            k -> registry.getOrCreate("netty:" + serviceName, configType));
    }

    /**
     * 检查服务是否允许请求
     */
    public boolean isServiceAllowed(String serviceName) {
        CircuitBreaker breaker = getBreaker(serviceName);
        return breaker.allowRequest();
    }

    /**
     * 执行服务调用，带熔断保护
     * 
     * @param serviceName 服务名称
     * @param action 要执行的操作
     * @param fallback 降级处理
     * @return 执行结果
     */
    public <T> T executeService(String serviceName, java.util.function.Supplier<T> action, 
                                java.util.function.Supplier<T> fallback) {
        CircuitBreaker breaker = getBreaker(serviceName);
        return breaker.execute(action, fallback);
    }

    /**
     * 执行服务调用，无降级
     */
    public <T> T executeService(String serviceName, java.util.function.Supplier<T> action) {
        return executeService(serviceName, action, null);
    }

    /**
     * 记录服务调用成功
     */
    public void recordSuccess(String serviceName) {
        CircuitBreaker breaker = serviceBreakers.get(serviceName);
        if (breaker != null) {
            breaker.recordResult(CircuitBreaker.Result.SUCCESS);
        }
    }

    /**
     * 记录服务调用失败
     */
    public void recordFailure(ChannelHandlerContext ctx, Throwable cause) {
        // 根据错误类型确定服务名称
        String serviceName = determineServiceName(ctx, cause);
        CircuitBreaker breaker = serviceBreakers.get(serviceName);
        if (breaker != null) {
            breaker.recordResult(CircuitBreaker.Result.FAILURE);
        }
    }

    /**
     * 记录服务调用失败
     */
    public void recordFailure(String serviceName) {
        CircuitBreaker breaker = serviceBreakers.get(serviceName);
        if (breaker != null) {
            breaker.recordResult(CircuitBreaker.Result.FAILURE);
        }
    }

    /**
     * 获取服务熔断状态
     */
    public CircuitBreaker.State getServiceState(String serviceName) {
        CircuitBreaker breaker = getBreaker(serviceName);
        return breaker.getState();
    }

    /**
     * 检查服务是否熔断打开
     */
    public boolean isServiceOpen(String serviceName) {
        CircuitBreaker breaker = getBreaker(serviceName);
        return breaker.isOpen();
    }

    /**
     * 重置服务熔断器
     */
    public void resetService(String serviceName) {
        CircuitBreaker breaker = serviceBreakers.get(serviceName);
        if (breaker != null) {
            breaker.reset();
        }
    }

    /**
     * 重置所有服务熔断器
     */
    public void resetAll() {
        serviceBreakers.values().forEach(CircuitBreaker::reset);
    }

    /**
     * 获取所有服务熔断状态
     */
    public ConcurrentMap<String, CircuitBreaker.State> getAllServiceStates() {
        ConcurrentMap<String, CircuitBreaker.State> states = new ConcurrentHashMap<>();
        serviceBreakers.forEach((name, breaker) -> {
            states.put(name, breaker.getState());
        });
        return states;
    }

    /**
     * 获取服务统计信息
     */
    public CircuitBreaker.CircuitBreakerStats getServiceStats(String serviceName) {
        CircuitBreaker breaker = getBreaker(serviceName);
        return breaker.getStats();
    }

    /**
     * 获取所有服务统计信息
     */
    public java.util.List<CircuitBreaker.CircuitBreakerStats> getAllServiceStats() {
        return serviceBreakers.values().stream()
            .map(CircuitBreaker::getStats)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取打开熔断的服务列表
     */
    public java.util.List<String> getOpenServices() {
        return serviceBreakers.entrySet().stream()
            .filter(e -> e.getValue().isOpen())
            .map(java.util.Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取半开熔断的服务列表
     */
    public java.util.List<String> getHalfOpenServices() {
        return serviceBreakers.entrySet().stream()
            .filter(e -> e.getValue().isHalfOpen())
            .map(java.util.Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 初始化默认熔断器
     */
    private void initializeDefaultBreakers() {
        // 数据库熔断器
        getOrCreateServiceBreaker("database", CircuitBreakerRegistry.ConfigType.FAST_FAIL);
        
        // 消息队列熔断器
        getOrCreateServiceBreaker("message-queue", CircuitBreakerRegistry.ConfigType.FAST_FAIL);
        
        // Redis熔断器
        getOrCreateServiceBreaker("redis", CircuitBreakerRegistry.ConfigType.FAST_FAIL);
        
        // 文件存储熔断器
        getOrCreateServiceBreaker("file-storage", CircuitBreakerRegistry.ConfigType.GRACEFUL);
        
        // 外部API熔断器
        getOrCreateServiceBreaker("external-api", CircuitBreakerRegistry.ConfigType.FAST_FAIL);
    }

    /**
     * 创建服务熔断器
     */
    private CircuitBreaker getOrCreateServiceBreaker(String serviceName, 
                                                     CircuitBreakerRegistry.ConfigType configType) {
        return serviceBreakers.computeIfAbsent(serviceName, 
            k -> registry.getOrCreate("netty:" + serviceName, configType));
    }

    /**
     * 处理消息
     */
    private void processMessage(ChannelHandlerContext ctx, Object msg) {
        // 根据消息类型调用相应的熔断保护服务
        if (msg instanceof String) {
            String message = (String) msg;
            // 解析消息，确定调用的服务
            String serviceName = parseServiceFromMessage(message);
            if (serviceName != null) {
                CircuitBreaker breaker = getBreaker(serviceName);
                if (!breaker.allowRequest()) {
                    throw new CircuitBreaker.CircuitBreakerOpenException(
                        "Service circuit breaker open: " + serviceName);
                }
            }
        }
    }

    /**
     * 从消息中解析服务名称
     */
    private String parseServiceFromMessage(String message) {
        try {
            // 简单解析 - 实际应用中根据消息类型确定
            if (message.contains("\"type\":\"message\"")) {
                return "message-service";
            } else if (message.contains("\"type\":\"file\"")) {
                return "file-storage";
            } else if (message.contains("\"type\":\"user\"")) {
                return "user-service";
            }
        } catch (Exception e) {
            // 忽略
        }
        return null;
    }

    /**
     * 根据上下文和异常确定服务名称
     */
    private String determineServiceName(ChannelHandlerContext ctx, Throwable cause) {
        // 根据异常类型确定服务
        String className = cause.getClass().getName();
        
        if (className.contains("mysql") || className.contains("jdbc")) {
            return "database";
        } else if (className.contains("redis")) {
            return "redis";
        } else if (className.contains("kafka") || className.contains("mq")) {
            return "message-queue";
        } else if (className.contains("s3") || className.contains("oss")) {
            return "file-storage";
        }
        
        return "unknown";
    }

    /**
     * 设置默认配置类型
     */
    public void setDefaultConfigType(CircuitBreakerRegistry.ConfigType configType) {
        this.defaultConfigType = configType;
    }
}
