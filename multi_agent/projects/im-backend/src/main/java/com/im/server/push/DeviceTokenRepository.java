package com.im.server.push;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 设备Token仓储
 * 
 * 简化实现：使用内存存储
 * 生产环境应使用数据库 + Redis 缓存
 */
@Repository
public class DeviceTokenRepository {

    // 内存存储: userId -> List<DeviceToken>
    private final Map<Long, List<DeviceToken>> userTokens = new ConcurrentHashMap<>();

    // Token -> DeviceToken 索引
    private final Map<String, DeviceToken> tokenIndex = new ConcurrentHashMap<>();

    /**
     * 注册/更新设备Token
     */
    public DeviceToken save(DeviceToken token) {
        if (token.getUserId() == null || token.getDeviceToken() == null) {
            throw new IllegalArgumentException("userId and deviceToken are required");
        }

        token.setTokenUpdatedTime(LocalDateTime.now());
        token.setLastActiveTime(LocalDateTime.now());

        // 移除旧的同名 token（同一平台）
        List<DeviceToken> tokens = userTokens.computeIfAbsent(token.getUserId(), k -> new ArrayList<>());
        tokens.removeIf(t -> t.getPlatform() == token.getPlatform() 
                && Objects.equals(t.getChannel(), token.getChannel()));

        tokens.add(token);

        // 更新索引
        tokenIndex.put(token.getDeviceToken(), token);

        return token;
    }

    /**
     * 查询用户的所有设备
     */
    public List<DeviceToken> findByUserId(Long userId) {
        List<DeviceToken> tokens = userTokens.get(userId);
        if (tokens == null) return Collections.emptyList();
        return tokens.stream()
                .filter(DeviceToken::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * 查询用户的指定平台设备
     */
    public List<DeviceToken> findByUserIdAndPlatform(Long userId, DeviceToken.Platform platform) {
        return findByUserId(userId).stream()
                .filter(t -> t.getPlatform() == platform)
                .collect(Collectors.toList());
    }

    /**
     * 通过Token查询
     */
    public Optional<DeviceToken> findByToken(String token) {
        return Optional.ofNullable(tokenIndex.get(token));
    }

    /**
     * 删除设备Token
     */
    public void deleteByToken(String token) {
        DeviceToken dt = tokenIndex.remove(token);
        if (dt != null) {
            List<DeviceToken> tokens = userTokens.get(dt.getUserId());
            if (tokens != null) {
                tokens.removeIf(t -> t.getDeviceToken().equals(token));
            }
        }
    }

    /**
     * 删除用户的所有设备
     */
    public void deleteByUserId(Long userId) {
        List<DeviceToken> tokens = userTokens.remove(userId);
        if (tokens != null) {
            for (DeviceToken t : tokens) {
                tokenIndex.remove(t.getDeviceToken());
            }
        }
    }

    /**
     * 禁用设备Token
     */
    public void disableByToken(String token) {
        DeviceToken dt = tokenIndex.get(token);
        if (dt != null) {
            dt.setEnabled(false);
        }
    }

    /**
     * 批量注册Token
     */
    public List<DeviceToken> saveAll(List<DeviceToken> tokens) {
        return tokens.stream().map(this::save).collect(Collectors.toList());
    }

    /**
     * 查询所有活跃设备
     */
    public List<DeviceToken> findAllActive() {
        return userTokens.values().stream()
                .flatMap(List::stream)
                .filter(DeviceToken::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * 统计用户设备数量
     */
    public int countByUserId(Long userId) {
        List<DeviceToken> tokens = userTokens.get(userId);
        return tokens != null ? (int) tokens.stream().filter(DeviceToken::isEnabled).count() : 0;
    }

    /**
     * 清理过期Token（超过30天未活跃）
     */
    public int cleanupExpired(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        int removed = 0;

        for (Map.Entry<Long, List<DeviceToken>> entry : userTokens.entrySet()) {
            List<DeviceToken> expired = new ArrayList<>();
            for (DeviceToken t : entry.getValue()) {
                if (t.getLastActiveTime() != null && t.getLastActiveTime().isBefore(threshold)) {
                    expired.add(t);
                    tokenIndex.remove(t.getDeviceToken());
                    removed++;
                }
            }
            entry.getValue().removeAll(expired);
        }

        return removed;
    }
}
