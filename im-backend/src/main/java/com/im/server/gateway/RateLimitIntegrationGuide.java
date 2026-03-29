package com.im.server.gateway;

import com.im.server.netty.NettyWebSocketServer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 限流和熔断集成指南
 * 
 * 本文件展示如何在Netty WebSocket服务器中集成限流和熔断功能。
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class RateLimitIntegrationGuide {

    // ==================== 集成步骤 ====================

    /**
     * 步骤1: 在NettyWebSocketServer中添加限流过滤器
     * 
     * 在ChannelInitializer中添加以下代码:
     * 
     * <pre>{@code
     * // 添加限流过滤器 (推荐放在最前面)
     * pipeline.addLast("rateLimitFilter", new RateLimitFilter());
     * 
     * // 添加熔断处理器
     * pipeline.addLast("circuitBreakerHandler", new CircuitBreakerHandler());
     * }</pre>
     */

    /**
     * 步骤2: 配置限流服务
     * 
     * <pre>{@code
     * // 创建自定义配置
     * RateLimitConfig config = new RateLimitConfig();
     * config.setGlobalQps(10000);
     * config.setIpQps(100);
     * config.setUserQps(30);
     * config.setUserMessageRate(60);
     * config.setLoginRpm(5);
     * 
     * // 初始化限流服务
     * RateLimitService service = RateLimitService.getInstance(config);
     * }</pre>
     */

    /**
     * 步骤3: 在消息处理中集成熔断保护
     * 
     * <pre>{@code
     * public class ProtectedMessageHandler {
     *     private CircuitBreakerHandler breakerHandler;
     *     
     *     public void handleMessage(ChannelHandlerContext ctx, String message) {
     *         // 使用熔断保护执行操作
     *         String result = breakerHandler.executeService("message-service",
     *             () -> processMessage(message),
     *             () -> "{\"code\": 503, \"msg\": \"Service temporarily unavailable\"}"
     *         );
     *     }
     * }
     * }</pre>
     */

    // ==================== 完整Pipeline示例 ====================

    /**
     * 完整的Netty Pipeline配置示例
     */
    public static void configurePipeline(ChannelPipeline pipeline) {
        // ========== HTTP Codec Layer ==========
        // HTTP请求解码和编码
        pipeline.addLast("httpCodec", new HttpServerCodec());
        
        // 聚合HTTP请求（用于WebSocket握手）
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        
        // ========== WebSocket Layer ==========
        // WebSocket协议处理
        pipeline.addLast("webSocketProtocol", new WebSocketServerProtocolHandler(
            "/ws",  // WebSocket路径
            null,   // 协议版本
            true,   // 允许掩码
            65536  // 最大帧大小
        ));
        
        // ========== Security Layer (安全层) ==========
        // 限流过滤器 - 第一道防线
        // 注意: 应该放在认证之前，防止DDoS攻击
        RateLimitFilter rateLimitFilter = new RateLimitFilter();
        pipeline.addLast("rateLimitFilter", rateLimitFilter);
        
        // ========== Circuit Breaker Layer (熔断层) ==========
        // 熔断处理器 - 保护下游服务
        CircuitBreakerHandler circuitBreakerHandler = new CircuitBreakerHandler();
        pipeline.addLast("circuitBreakerHandler", circuitBreakerHandler);
        
        // ========== Timeout Layer (超时层) ==========
        // 空闲检测 - 30秒无读写则关闭连接
        pipeline.addLast("idleStateHandler", new IdleStateHandler(30, 30, 0));
        
        // ========== Business Layer (业务层) ==========
        // 消息处理器
        // pipeline.addLast("messageHandler", new WebSocketMessageHandler());
    }

    // ==================== API限流示例 ====================

    /**
     * REST API限流示例 (使用Spring Cloud Gateway)
     * 
     * <pre>{@code
     * @Configuration
     * public class RateLimitGatewayConfig {
     *     
     *     @Bean
     *     public RateLimitService rateLimitService() {
     *         return RateLimitService.getInstance();
     *     }
     *     
     *     @Bean
     *     public GlobalFilter rateLimitFilter(RateLimitService service) {
     *         return (exchange, chain) -> {
     *             String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
     *             RateLimitService.RateLimitResult result = service.checkIp(ip);
     *             
     *             if (result.isLimited()) {
     *                 exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
     *                 exchange.getResponse().getHeaders().add("Retry-After", 
     *                     String.valueOf(result.getWaitTimeMs() / 1000));
     *                 return exchange.getResponse().setComplete();
     *             }
     *             
     *             return chain.filter(exchange);
     *         };
     *     }
     * }
     * }</pre>
     */

    // ==================== 监控端点示例 ====================

    /**
     * Actuator端点示例 (Spring Boot)
     * 
     * <pre>{@code
     * @RestController
     * @RequestMapping("/actuator/ratelimit")
     * public class RateLimitActuator {
     *     
     *     @Autowired
     *     private RateLimitService rateLimitService;
     *     
     *     @Autowired
     *     private CircuitBreakerRegistry circuitBreakerRegistry;
     *     
     *     @GetMapping("/stats")
     *     public ResponseEntity<RateLimitStats> getStats() {
     *         return ResponseEntity.ok(rateLimitService.getStats());
     *     }
     *     
     *     @GetMapping("/circuit-breakers")
     *     public ResponseEntity<RegistrySummary> getCircuitBreakers() {
     *         return ResponseEntity.ok(circuitBreakerRegistry.getSummary());
     *     }
     *     
     *     @PostMapping("/circuit-breakers/{name}/reset")
     *     public ResponseEntity<Void> resetCircuitBreaker(@PathVariable String name) {
     *         circuitBreakerRegistry.get(name).reset();
     *         return ResponseEntity.ok().build();
     *     }
     * }
     * }</pre>
     */

    // ==================== 配置示例 ====================

    /**
     * application.yml 配置示例
     * 
     * <pre>{@code
     * # 限流配置
     * rate-limit:
     *   enabled: true
     *   algorithm: TOKEN_BUCKET
     *   
     *   # 全局配置
     *   global:
     *     qps: 10000
     *     rpm: 500000
     *   
     *   # IP级配置
     *   ip:
     *     qps: 100
     *     rpm: 3000
     *     max-connections: 10
     *   
     *   # 用户级配置
     *   user:
     *     qps: 30
     *     rpm: 500
     *     message-rate: 60
     *   
     *   # 登录限流配置
     *   login:
     *     rpm: 5
     *     ban-duration: 300
     *     failure-threshold: 5
     *   
     *   # 白名单
     *   whitelist:
     *     ips:
     *       - 127.0.0.1
     *       - 10.0.0.0/8
     * }</pre>
     */

    // ==================== 熔断配置示例 ====================

    /**
     * 熔断器配置示例
     */
    public static void configureCircuitBreakers() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.getInstance();
        
        // 数据库熔断器 - 快速失败
        registry.getOrCreate("database", 
            CircuitBreakerRegistry.CircuitBreakerConfig.builder()
                .failureThreshold(10)
                .minRequestVolume(20)
                .failureRateThreshold(30)
                .circuitOpenDuration(10000)
                .build()
        );
        
        // 消息队列熔断器 - 快速失败
        registry.getOrCreate("message-queue",
            CircuitBreakerRegistry.CircuitBreakerConfig.builder()
                .failureThreshold(20)
                .minRequestVolume(50)
                .failureRateThreshold(40)
                .circuitOpenDuration(15000)
                .build()
        );
        
        // 文件存储熔断器 - 宽容
        registry.getOrCreate("file-storage",
            CircuitBreakerRegistry.CircuitBreakerConfig.builder()
                .failureThreshold(100)
                .minRequestVolume(200)
                .failureRateThreshold(60)
                .circuitOpenDuration(60000)
                .build()
        );
    }

    // ==================== 使用示例 ====================

    /**
     * 完整使用示例
     */
    public static void example() {
        // 1. 初始化限流服务
        RateLimitConfig config = RateLimitConfig.prodConfig();
        RateLimitService rateLimitService = RateLimitService.getInstance(config);
        
        // 2. 检查请求是否允许
        String clientIp = "192.168.1.100";
        RateLimitService.RateLimitResult result = rateLimitService.checkIp(clientIp);
        
        if (result.isLimited()) {
            System.out.println("请求被限流: " + result.getReason());
            System.out.println("等待时间: " + result.getWaitTimeMs() + "ms");
            return;
        }
        
        // 3. 用户级检查
        Long userId = 12345L;
        result = rateLimitService.checkUser(userId, clientIp);
        
        if (result.isLimited()) {
            System.out.println("用户被限流: " + result.getReason());
            return;
        }
        
        // 4. 消息发送检查
        result = rateLimitService.checkMessage(userId);
        
        if (result.isLimited()) {
            System.out.println("消息发送被限流: " + result.getReason());
            return;
        }
        
        // 5. 使用熔断器执行操作
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.getInstance();
        CircuitBreaker breaker = registry.getOrCreate("user-service");
        
        String data = breaker.execute(
            () -> fetchUserData(userId),  // 正常操作
            () -> getCachedUserData(userId)  // 降级操作
        );
        
        System.out.println("获取用户数据: " + data);
        
        // 6. 获取统计信息
        RateLimitService.RateLimitStats stats = rateLimitService.getStats();
        System.out.println("总请求: " + stats.getTotalRequests());
        System.out.println("限流次数: " + stats.getTotalLimited());
        System.out.println("限流率: " + stats.getLimitedRate() + "%");
        
        CircuitBreakerRegistry.RegistrySummary summary = registry.getSummary();
        System.out.println("熔断器总数: " + summary.getTotalCount());
        System.out.println("打开的熔断器: " + summary.getOpenCount());
    }
    
    // 模拟方法
    private static String fetchUserData(Long userId) {
        return "User data for " + userId;
    }
    
    private static String getCachedUserData(Long userId) {
        return "Cached user data for " + userId;
    }
}
