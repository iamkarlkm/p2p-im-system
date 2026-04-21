package com.im.service.websocket.p2p;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * P2P WebSocket 通道会话管理器
 * 替代 Spring WebSocketSessionManager，使用 Netty Channel
 */
@Slf4j
@Component
public class P2PChannelSessionManager {

    private static final long HEARTBEAT_TIMEOUT_MS = 120000;

    // userId -> {deviceId -> Channel}
    private final Map<Long, Map<String, Channel>> userChannels = new ConcurrentHashMap<>();

    // channelId -> SessionInfo
    private final Map<String, SessionInfo> sessionInfoMap = new ConcurrentHashMap<>();

    public void registerSession(Long userId, String deviceId, Channel channel) {
        if (userId == null || deviceId == null || channel == null) return;
        String channelId = channel.id().asLongText();

        Map<String, Channel> devices = userChannels.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        Channel old = devices.get(deviceId);
        if (old != null && !old.id().asLongText().equals(channelId)) {
            log.info("同一设备重复登录，关闭旧通道: userId={}, deviceId={}", userId, deviceId);
            old.close();
        }
        devices.put(deviceId, channel);

        SessionInfo info = new SessionInfo();
        info.setChannelId(channelId);
        info.setUserId(userId);
        info.setDeviceId(deviceId);
        info.setChannel(channel);
        info.setConnectTime(System.currentTimeMillis());
        info.setLastActivityTime(System.currentTimeMillis());
        sessionInfoMap.put(channelId, info);

        log.info("P2P 通道注册: userId={}, deviceId={}, channelId={}", userId, deviceId, channelId);
    }

    public void unregisterSession(Long userId, String deviceId) {
        if (userId == null || deviceId == null) return;
        Map<String, Channel> devices = userChannels.get(userId);
        if (devices != null) {
            Channel ch = devices.remove(deviceId);
            if (ch != null) {
                sessionInfoMap.remove(ch.id().asLongText());
            }
            if (devices.isEmpty()) {
                userChannels.remove(userId);
            }
        }
        log.info("P2P 通道注销: userId={}, deviceId={}", userId, deviceId);
    }

    public void unregisterByChannel(Channel channel) {
        if (channel == null) return;
        SessionInfo info = sessionInfoMap.remove(channel.id().asLongText());
        if (info != null) {
            Map<String, Channel> devices = userChannels.get(info.getUserId());
            if (devices != null) {
                Channel stored = devices.get(info.getDeviceId());
                if (stored != null && stored.id().asLongText().equals(channel.id().asLongText())) {
                    devices.remove(info.getDeviceId());
                    if (devices.isEmpty()) {
                        userChannels.remove(info.getUserId());
                    }
                }
            }
            log.info("P2P 通道断开: userId={}, deviceId={}", info.getUserId(), info.getDeviceId());
        }
    }

    public Set<Channel> getUserChannels(Long userId) {
        if (userId == null) return Collections.emptySet();
        Map<String, Channel> devices = userChannels.get(userId);
        if (devices == null || devices.isEmpty()) return Collections.emptySet();
        return new HashSet<>(devices.values());
    }

    public Channel getDeviceChannel(Long userId, String deviceId) {
        if (userId == null || deviceId == null) return null;
        Map<String, Channel> devices = userChannels.get(userId);
        return devices != null ? devices.get(deviceId) : null;
    }

    public boolean hasActiveSession(Long userId) {
        Map<String, Channel> devices = userChannels.get(userId);
        if (devices == null) return false;
        return devices.values().stream().anyMatch(Channel::isActive);
    }

    public Set<Long> getAllOnlineUsers() {
        return new HashSet<>(userChannels.keySet());
    }

    public void updateLastActivityTime(Channel channel) {
        SessionInfo info = sessionInfoMap.get(channel.id().asLongText());
        if (info != null) {
            info.setLastActivityTime(System.currentTimeMillis());
        }
    }

    public SessionInfo getSessionInfo(Channel channel) {
        return sessionInfoMap.get(channel.id().asLongText());
    }

    public Long getUserIdByChannel(Channel channel) {
        SessionInfo info = sessionInfoMap.get(channel.id().asLongText());
        return info != null ? info.getUserId() : null;
    }

    @Data
    public static class SessionInfo {
        private String channelId;
        private Long userId;
        private String deviceId;
        private Channel channel;
        private long connectTime;
        private long lastActivityTime;
    }
}
