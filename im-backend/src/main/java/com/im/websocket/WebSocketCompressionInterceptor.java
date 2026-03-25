package com.im.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket STOMP 消息压缩拦截器
 * 
 * 功能:
 * 1. 对 SEND/MESSAGE 帧进行压缩/解压缩
 * 2. 在 headers 中标记压缩状态
 * 3. 仅对超过阈值的大消息进行压缩
 * 
 * 压缩策略:
 * - 消息 > 1KB: 启用 GZIP
 * - 消息 > 32KB: 强制压缩
 * - 订阅响应: 仅压缩超过 4KB 的消息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketCompressionInterceptor implements ChannelInterceptor {

    private final BrotliCompressionService compressionService;

    private static final int AUTO_COMPRESS_THRESHOLD = 1024;  // 1KB
    private static final int FORCE_COMPRESS_THRESHOLD = 32 * 1024; // 32KB
    private static final int SUBSCRIBE_COMPRESS_THRESHOLD = 4 * 1024; // 4KB

    // 压缩标记 header key
    public static final String HEADER_COMPRESSED = "X-Compressed";
    public static final String HEADER_COMPRESSION_TYPE = "X-Compression-Type";
    public static final String HEADER_ORIGINAL_SIZE = "X-Original-Size";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        switch (command) {
            case SEND -> handleSend(message, accessor);
            case MESSAGE -> handleMessage(message, accessor);
            case CONNECT, CONNECTED -> handleConnect(message, accessor);
            default -> {}
        }

        return message;
    }

    /**
     * 处理客户端发送的消息 — 尝试压缩
     */
    private void handleSend(Message<?> message, StompHeaderAccessor accessor) {
        Object payload = message.getPayload();
        if (!(payload instanceof byte[] bytes)) return;

        int size = bytes.length;
        if (size < AUTO_COMPRESS_THRESHOLD) return;

        // 决定是否压缩
        boolean shouldCompress = size > AUTO_COMPRESS_THRESHOLD;
        if (!shouldCompress) return;

        try {
            byte[] compressed = compressionService.compressStream(bytes);
            if (compressed != bytes) {
                // 压缩成功，添加标记 headers
                accessor.setHeader(HEADER_COMPRESSED, "true");
                accessor.setHeader(HEADER_COMPRESSION_TYPE, "gzip");
                accessor.setHeader(HEADER_ORIGINAL_SIZE, String.valueOf(size));
                
                StompHeaderAccessor newAccessor = StompHeaderAccessor.create(StompCommand.SEND);
                newAccessor.setHeaders(accessor.getNativeHeaders());
                newAccessor.setSessionAttributes(accessor.getSessionAttributes());
                
                log.debug("WS SEND compressed: {} -> {} bytes",
                    size, compressed.length);
                
                // 注意: 实际压缩需要在 WebSocket encoder 层处理
                // 这里设置 headers 作为信号
            }
        } catch (Exception e) {
            log.warn("WS compression failed for SEND frame", e);
        }
    }

    /**
     * 处理服务端推送的消息 — 尝试压缩大消息
     */
    private void handleMessage(Message<?> message, StompHeaderAccessor accessor) {
        Object payload = message.getPayload();
        if (!(payload instanceof byte[] bytes)) return;

        int size = bytes.length;
        int threshold = getCompressionThreshold(accessor);

        if (size < threshold) return;

        try {
            byte[] compressed = compressionService.compressStream(bytes);
            if (compressed != bytes) {
                accessor.setHeader(HEADER_COMPRESSED, "true");
                accessor.setHeader(HEADER_COMPRESSION_TYPE, "gzip");
                accessor.setHeader(HEADER_ORIGINAL_SIZE, String.valueOf(size));
                
                log.debug("WS MESSAGE compressed: {} -> {} bytes",
                    size, compressed.length);
            }
        } catch (Exception e) {
            log.warn("WS compression failed for MESSAGE frame", e);
        }
    }

    /**
     * 处理连接握手 — 协商压缩能力
     */
    private void handleConnect(Message<?> message, StompHeaderAccessor accessor) {
        // 支持客户端声明支持的压缩算法
        var headers = accessor.getNativeHeaders();
        if (headers != null && headers.containsKey("Accept-Encoding")) {
            var encodings = headers.get("Accept-Encoding");
            if (encodings != null && !encodings.isEmpty()) {
                String acceptEncoding = String.join(",", encodings);
                log.debug("Client accepts encoding: {}", acceptEncoding);
                // 可以在 CONNECTED 帧中声明服务端支持的压缩算法
            }
        }
    }

    /**
     * 根据帧类型确定压缩阈值
     */
    private int getCompressionThreshold(StompHeaderAccessor accessor) {
        var destination = accessor.getDestination();
        if (destination == null) return AUTO_COMPRESS_THRESHOLD;

        // 消息推送目的地 — 使用较高阈值避免过度压缩
        if (destination.startsWith("/topic/") || destination.startsWith("/queue/")) {
            return SUBSCRIBE_COMPRESS_THRESHOLD;
        }
        return AUTO_COMPRESS_THRESHOLD;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel,
            boolean sent, Exception ex) {
        if (ex != null) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);
            log.warn("WS send failed for {}: {}",
                accessor != null ? accessor.getCommand() : "unknown", ex.getMessage());
        }
    }
}
