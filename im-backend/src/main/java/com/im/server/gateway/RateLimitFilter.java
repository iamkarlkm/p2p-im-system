package com.im.server.gateway;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCounted;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流过滤器 - Netty Pipeline Handler
 * 
 * 在Netty管道中处理限流逻辑：
 * - IP限流
 * - 用户限流
 * - WebSocket连接限流
 * - 消息发送限流
 * 
 * 使用示例:
 * <pre>
 * pipeline.addLast("rateLimitFilter", new RateLimitFilter());
 * </pre>
 * 
 * @author IM System
 * @version 1.0
 * @since 2026-03-18
 */
public class RateLimitFilter extends ChannelInboundHandlerAdapter {

    // ==================== 常量定义 ====================
    
    /** 限流响应JSON模板 */
    private static final String RATE_LIMIT_RESPONSE = 
        "{\"code\":429,\"msg\":\"Rate limit exceeded\",\"retryAfter\":%d}";
    
    /** 心跳命令 */
    private static final int CMD_HEARTBEAT = 3001;
    
    /** 连接命令 */
    private static final int CMD_CONNECT = 1000;
    
    /** 消息命令 */
    private static final int CMD_MESSAGE = 1001;

    // ==================== 限流服务 ====================
    
    private final RateLimitService rateLimitService;
    
    // ==================== 通道存储 ====================
    
    /** 通道用户映射 */
    private static final ConcurrentMap<ChannelHandlerContext, ChannelInfo> channelInfoMap = 
        new ConcurrentHashMap<>();
    
    /** 用户通道映射 */
    private static final ConcurrentMap<Long, ChannelHandlerContext> userChannelMap = 
        new ConcurrentHashMap<>();

    // ==================== 统计 ====================
    
    /** 通过的消息计数 */
    private static final AtomicInteger passedMessages = new AtomicInteger(0);
    
    /** 拦截的消息计数 */
    private static final AtomicInteger blockedMessages = new AtomicInteger(0);

    // ==================== 构造函数 ====================

    /**
     * 使用默认限流服务
     */
    public RateLimitFilter() {
        this(RateLimitService.getInstance());
    }

    /**
     * 使用自定义限流服务
     */
    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    // ==================== 通道事件处理 ====================

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String ip = getRemoteIp(ctx);
        
        // 检查连接限流
        RateLimitService.RateLimitResult result = rateLimitService.checkIp(ip);
        
        if (result.isLimited()) {
            sendRateLimitResponse(ctx, result);
            ctx.close();
            blockedMessages.incrementAndGet();
            return;
        }
        
        // 创建通道信息
        ChannelInfo info = new ChannelInfo(ctx, ip);
        channelInfoMap.put(ctx, info);
        
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 清理通道信息
        ChannelInfo info = channelInfoMap.remove(ctx);
        if (info != null && info.getUserId() != null) {
            userChannelMap.remove(info.getUserId());
        }
        
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof String)) {
            // 非字符串消息直接通过
            super.channelRead(ctx, msg);
            return;
        }
        
        String ip = getRemoteIp(ctx);
        ChannelInfo info = channelInfoMap.get(ctx);
        Long userId = info != null ? info.getUserId() : null;
        
        // 解析消息类型
        String message = (String) msg;
        int cmd = parseCommand(message);
        
        RateLimitService.RateLimitResult result;
        
        switch (cmd) {
            case CMD_CONNECT:
                // 连接命令也需要限流
                result = rateLimitService.checkIp(ip);
                break;
                
            case CMD_HEARTBEAT:
                // 心跳单独限流，更宽松
                result = rateLimitService.checkSlidingWindow("heartbeat:" + ip, 120, 60000);
                break;
                
            case CMD_MESSAGE:
                // 消息发送限流
                if (userId != null) {
                    result = rateLimitService.checkMessage(userId);
                    if (!result.isLimited()) {
                        result = rateLimitService.checkUser(userId, ip);
                    }
                } else {
                    result = rateLimitService.checkApi("/ws/message", ip, null);
                }
                break;
                
            default:
                // 其他消息使用API限流
                result = rateLimitService.checkApi("/ws/default", ip, userId);
                break;
        }
        
        if (result.isLimited()) {
            sendRateLimitResponse(ctx, result);
            blockedMessages.incrementAndGet();
            return;
        }
        
        passedMessages.incrementAndGet();
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.ALL_IDLE) {
                // 读空闲太久，关闭连接
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录异常但不断开连接
        super.exceptionCaught(ctx, cause);
    }

    // ==================== 公共方法 ====================

    /**
     * 用户认证后注册
     */
    public static void registerUser(ChannelHandlerContext ctx, Long userId) {
        ChannelInfo info = channelInfoMap.get(ctx);
        if (info != null) {
            info.setUserId(userId);
            // 移除旧的绑定
            userChannelMap.remove(userId);
            userChannelMap.put(userId, ctx);
        }
    }

    /**
     * 获取通道关联的用户ID
     */
    public static Long getUserId(ChannelHandlerContext ctx) {
        ChannelInfo info = channelInfoMap.get(ctx);
        return info != null ? info.getUserId() : null;
    }

    /**
     * 获取用户对应的通道
     */
    public static ChannelHandlerContext getChannelByUserId(Long userId) {
        return userChannelMap.get(userId);
    }

    /**
     * 获取当前在线用户数
     */
    public static int getOnlineUserCount() {
        return userChannelMap.size();
    }

    /**
     * 获取当前活跃通道数
     */
    public static int getActiveChannelCount() {
        return channelInfoMap.size();
    }

    /**
     * 获取统计信息
     */
    public static RateLimitFilterStats getStats() {
        return new RateLimitFilterStats(
            passedMessages.get(),
            blockedMessages.get(),
            channelInfoMap.size(),
            userChannelMap.size()
        );
    }

    /**
     * 重置统计
     */
    public static void resetStats() {
        passedMessages.set(0);
        blockedMessages.set(0);
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Long userId) {
        return userChannelMap.containsKey(userId);
    }

    /**
     * 获取用户IP
     */
    public static String getUserIp(Long userId) {
        ChannelHandlerContext ctx = userChannelMap.get(userId);
        if (ctx != null) {
            ChannelInfo info = channelInfoMap.get(ctx);
            return info != null ? info.getIp() : null;
        }
        return null;
    }

    // ==================== 私有方法 ====================

    /**
     * 获取远程IP地址
     */
    private String getRemoteIp(ChannelHandlerContext ctx) {
        try {
            InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
            return remote.getAddress().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 解析消息命令类型
     */
    private int parseCommand(String message) {
        try {
            // 简单JSON解析，提取cmd字段
            // 格式: {"cmd":1001,...}
            int cmdIndex = message.indexOf("\"cmd\":");
            if (cmdIndex >= 0) {
                int start = cmdIndex + 6;
                int end = start;
                while (end < message.length()) {
                    char c = message.charAt(end);
                    if (c == ',' || c == '}') break;
                    end++;
                }
                return Integer.parseInt(message.substring(start, end).trim());
            }
        } catch (Exception e) {
            // 解析失败，忽略
        }
        return 0;
    }

    /**
     * 发送限流响应
     */
    private void sendRateLimitResponse(ChannelHandlerContext ctx, RateLimitService.RateLimitResult result) {
        String response = String.format(RATE_LIMIT_RESPONSE, result.getWaitTimeMs());
        ctx.writeAndFlush(response + "\n");
    }

    // ==================== 内部类 ====================

    /**
     * 通道信息
     */
    public static class ChannelInfo {
        private final ChannelHandlerContext ctx;
        private final String ip;
        private volatile Long userId;
        private volatile long connectTime;
        private volatile long lastMessageTime;
        private volatile int messageCount;
        
        public ChannelInfo(ChannelHandlerContext ctx, String ip) {
            this.ctx = ctx;
            this.ip = ip;
            this.connectTime = System.currentTimeMillis();
            this.lastMessageTime = connectTime;
            this.messageCount = 0;
        }
        
        public ChannelHandlerContext getCtx() { return ctx; }
        public String getIp() { return ip; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public long getConnectTime() { return connectTime; }
        public long getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(long lastMessageTime) { this.lastMessageTime = lastMessageTime; }
        public int getMessageCount() { return messageCount; }
        public void incrementMessageCount() { this.messageCount++; }
        
        public long getOnlineDuration() {
            return System.currentTimeMillis() - connectTime;
        }
    }

    /**
     * 限流过滤器统计
     */
    public static class RateLimitFilterStats {
        private final long passedMessages;
        private final long blockedMessages;
        private final int activeChannels;
        private final int onlineUsers;
        
        public RateLimitFilterStats(long passedMessages, long blockedMessages, 
                                   int activeChannels, int onlineUsers) {
            this.passedMessages = passedMessages;
            this.blockedMessages = blockedMessages;
            this.activeChannels = activeChannels;
            this.onlineUsers = onlineUsers;
        }
        
        public double getBlockRate() {
            long total = passedMessages + blockedMessages;
            return total > 0 ? (double) blockedMessages / total * 100 : 0;
        }
        
        public long getPassedMessages() { return passedMessages; }
        public long getBlockedMessages() { return blockedMessages; }
        public int getActiveChannels() { return activeChannels; }
        public int getOnlineUsers() { return onlineUsers; }
    }
}
