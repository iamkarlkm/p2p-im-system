package com.im.server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.server.netty.dto.WsMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket消息处理器
 */
@Component
public class WebSocketMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);
    
    private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>();
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MessageDispatcher messageDispatcher;
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        logger.debug("收到WebSocket消息: {}", text);
        
        try {
            WsMessage wsMessage = objectMapper.readValue(text, WsMessage.class);
            
            // 处理用户认证
            if ("auth".equals(wsMessage.getType())) {
                handleAuth(ctx, wsMessage);
                return;
            }
            
            // 验证用户是否已认证
            String userId = getUserId(ctx.channel());
            if (userId == null) {
                sendError(ctx, "未认证，请先登录");
                return;
            }
            
            // 分发消息到对应的处理器
            messageDispatcher.dispatch(wsMessage, userId, ctx.channel());
            
        } catch (Exception e) {
            logger.error("处理WebSocket消息失败", e);
            sendError(ctx, "消息格式错误");
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("客户端连接: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("客户端断开: {}", ctx.channel().remoteAddress());
        
        // 移除 channel
        String userId = getUserId(ctx.channel());
        if (userId != null) {
            CHANNELS.remove(userId);
            // 处理用户离线
            logger.info("用户 {} 已离线", userId);
        }
        
        super.channelInactive(ctx);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 空闲超时，关闭连接
            logger.info("客户端空闲超时，关闭连接: {}", ctx.channel().remoteAddress());
            ctx.channel().close();
        }
        super.userEventTriggered(ctx, evt);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("WebSocket异常: {}", cause.getMessage());
        ctx.channel().close();
    }
    
    /**
     * 处理用户认证
     */
    private void handleAuth(ChannelHandlerContext ctx, WsMessage wsMessage) {
        String token = wsMessage.getContent();
        // TODO: 验证token，获取用户ID
        String userId = "user_" + System.currentTimeMillis(); // 临时实现
        
        // 绑定用户ID到channel
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
        CHANNELS.put(userId, ctx.channel());
        
        // 发送认证成功响应
        WsMessage response = new WsMessage();
        response.setType("auth_ack");
        response.setContent("认证成功");
        ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(response)));
        
        logger.info("用户 {} 认证成功", userId);
    }
    
    /**
     * 发送错误消息
     */
    private void sendError(ChannelHandlerContext ctx, String errorMsg) {
        WsMessage response = new WsMessage();
        response.setType("error");
        response.setContent(errorMsg);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 获取channel关联的用户ID
     */
    private String getUserId(Channel channel) {
        return channel.attr(AttributeKey.valueOf("userId")).get();
    }
    
    /**
     * 发送消息给指定用户
     */
    public static void sendMessage(String userId, WsMessage message) {
        Channel channel = CHANNELS.get(userId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(
                new ObjectMapper().writeValueAsString(message)));
        }
    }
}
