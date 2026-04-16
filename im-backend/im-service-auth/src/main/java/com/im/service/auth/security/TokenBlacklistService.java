package com.im.service.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 * 
 * 功能特性：
 * 1. Token 黑名单管理 - 将登出的 Token 加入黑名单
 * 2. 自动过期清理 - 根据 Token 过期时间自动删除
 * 3. 高效查询 - 使用 Redis 实现 O(1) 查询
 * 4. Token 哈希存储 - 减少存储空间并保护 Token 内容
 * 
 * 存储策略：
 * - 使用 Redis Set 存储黑名单 Token 的哈希值
 * - 根据 Token 过期时间设置 Redis 键的 TTL
 * - 定期清理过期数据
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 键前缀
     */
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    
    /**
     * 黑名单集合键 - 存储所有黑名单 Token 哈希
     */
    private static final String BLACKLIST_SET_KEY = "token:blacklist:all";

    /**
     * 统计信息键
     */
    private static final String STATS_KEY = "token:blacklist:stats";

    /**
     * 哈希算法
     */
    private static final String HASH_ALGORITHM = "SHA-256";

    @PostConstruct
    public void init() {
        log.info("Token Blacklist Service initialized");
        // 可以在这里预热统计信息
    }

    // ==================== 黑名单核心操作 ====================

    /**
     * 将 Token 加入黑名单
     *
     * @param token JWT Token
     * @param expirationTimeMillis Token 剩余过期时间（毫秒）
     */
    public void addToBlacklist(String token, long expirationTimeMillis) {
        try {
            // 1. 计算 Token 的哈希值
            String tokenHash = hashToken(token);
            
            // 2. 计算过期时间（秒）
            long expirationSeconds = Math.max(expirationTimeMillis / 1000, 1);
            
            // 3. 将 Token 哈希存入 Redis，设置过期时间
            String blacklistKey = BLACKLIST_PREFIX + tokenHash;
            redisTemplate.opsForValue().set(blacklistKey, System.currentTimeMillis(), 
                    expirationSeconds, TimeUnit.SECONDS);
            
            // 4. 同时添加到集合中用于统计
            redisTemplate.opsForSet().add(BLACKLIST_SET_KEY, tokenHash);
            
            // 5. 为集合设置过期时间（比最长的 Token 有效期稍长）
            redisTemplate.expire(BLACKLIST_SET_KEY, 8, TimeUnit.DAYS);
            
            // 6. 更新统计
            incrementBlacklistCount();
            
            log.debug("Token added to blacklist, hash: {}, expires in {} seconds", 
                    maskHash(tokenHash), expirationSeconds);
            
        } catch (Exception e) {
            log.error("Failed to add token to blacklist: {}", e.getMessage());
        }
    }

    /**
     * 检查 Token 是否在黑名单中
     *
     * @param token JWT Token
     * @return 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        try {
            String tokenHash = hashToken(token);
            String blacklistKey = BLACKLIST_PREFIX + tokenHash;
            
            // 检查是否存在
            Boolean exists = redisTemplate.hasKey(blacklistKey);
            boolean isBlacklisted = Boolean.TRUE.equals(exists);
            
            if (isBlacklisted) {
                log.debug("Token is blacklisted: {}", maskHash(tokenHash));
            }
            
            return isBlacklisted;
            
        } catch (Exception e) {
            log.error("Failed to check token blacklist status: {}", e.getMessage());
            // 发生错误时，为了安全起见，视为已在黑名单
            return true;
        }
    }

    /**
     * 从黑名单中移除 Token（主要用于测试或手动清理）
     *
     * @param token JWT Token
     */
    public void removeFromBlacklist(String token) {
        try {
            String tokenHash = hashToken(token);
            String blacklistKey = BLACKLIST_PREFIX + tokenHash;
            
            redisTemplate.delete(blacklistKey);
            redisTemplate.opsForSet().remove(BLACKLIST_SET_KEY, tokenHash);
            
            log.info("Token removed from blacklist: {}", maskHash(tokenHash));
            
        } catch (Exception e) {
            log.error("Failed to remove token from blacklist: {}", e.getMessage());
        }
    }

    // ==================== 批量操作 ====================

    /**
     * 批量添加 Token 到黑名单
     *
     * @param tokens Token 数组
     * @param expirationTimeMillis 过期时间（毫秒）
     */
    public void addAllToBlacklist(String[] tokens, long expirationTimeMillis) {
        for (String token : tokens) {
            addToBlacklist(token, expirationTimeMillis);
        }
        log.info("Batch added {} tokens to blacklist", tokens.length);
    }

    /**
     * 批量检查 Token 是否在黑名单中
     *
     * @param tokens Token 数组
     * @return 是否有任一 Token 在黑名单中
     */
    public boolean isAnyBlacklisted(String[] tokens) {
        for (String token : tokens) {
            if (isBlacklisted(token)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 统计与监控 ====================

    /**
     * 获取黑名单中的 Token 数量
     *
     * @return 数量
     */
    public long getBlacklistCount() {
        try {
            Long size = redisTemplate.opsForSet().size(BLACKLIST_SET_KEY);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Failed to get blacklist count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取黑名单统计信息
     *
     * @return 统计信息
     */
    public BlacklistStats getBlacklistStats() {
        long count = getBlacklistCount();
        long totalAdded = getTotalBlacklistCount();
        
        return BlacklistStats.builder()
                .currentCount(count)
                .totalAdded(totalAdded)
                .lastCleanupTime(getLastCleanupTime())
                .build();
    }

    /**
     * 清空黑名单（危险操作，仅用于测试）
     */
    public void clearBlacklist() {
        try {
            // 获取所有黑名单键
            Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            redisTemplate.delete(BLACKLIST_SET_KEY);
            redisTemplate.delete(STATS_KEY);
            
            log.warn("Blacklist cleared completely");
        } catch (Exception e) {
            log.error("Failed to clear blacklist: {}", e.getMessage());
        }
    }

    // ==================== 定时清理 ====================

    /**
     * 定期清理过期的统计信息
     * 每天执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredStats() {
        log.info("Starting scheduled blacklist cleanup");
        
        try {
            // Redis 会自动清理过期的键，这里主要清理集合中已经不存在的键
            Set<Object> members = redisTemplate.opsForSet().members(BLACKLIST_SET_KEY);
            
            if (members != null) {
                int removed = 0;
                for (Object member : members) {
                    String tokenHash = (String) member;
                    String blacklistKey = BLACKLIST_PREFIX + tokenHash;
                    
                    if (Boolean.FALSE.equals(redisTemplate.hasKey(blacklistKey))) {
                        redisTemplate.opsForSet().remove(BLACKLIST_SET_KEY, tokenHash);
                        removed++;
                    }
                }
                
                log.info("Blacklist cleanup completed. Removed {} expired entries", removed);
            }
            
            // 更新最后清理时间
            redisTemplate.opsForHash().put(STATS_KEY, "lastCleanupTime", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error during blacklist cleanup: {}", e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算 Token 的哈希值
     *
     * @param token JWT Token
     * @return 哈希字符串
     * @throws NoSuchAlgorithmException 算法不存在
     */
    private String hashToken(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * 遮罩哈希值用于日志
     *
     * @param hash 哈希值
     * @return 遮罩后的值
     */
    private String maskHash(String hash) {
        if (hash == null || hash.length() < 8) {
            return "***";
        }
        return hash.substring(0, 8) + "...";
    }

    /**
     * 增加黑名单计数
     */
    private void incrementBlacklistCount() {
        try {
            redisTemplate.opsForHash().increment(STATS_KEY, "totalAdded", 1);
        } catch (Exception e) {
            log.error("Failed to increment blacklist count: {}", e.getMessage());
        }
    }

    /**
     * 获取总黑名单计数
     *
     * @return 总数
     */
    private long getTotalBlacklistCount() {
        try {
            Object count = redisTemplate.opsForHash().get(STATS_KEY, "totalAdded");
            return count != null ? ((Number) count).longValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取最后清理时间
     *
     * @return 时间戳
     */
    private long getLastCleanupTime() {
        try {
            Object time = redisTemplate.opsForHash().get(STATS_KEY, "lastCleanupTime");
            return time != null ? ((Number) time).longValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== 统计信息类 ====================

    /**
     * 黑名单统计信息
     */
    public static class BlacklistStats {
        private long currentCount;
        private long totalAdded;
        private long lastCleanupTime;

        public static BlacklistStatsBuilder builder() {
            return new BlacklistStatsBuilder();
        }

        // Getters
        public long getCurrentCount() { return currentCount; }
        public long getTotalAdded() { return totalAdded; }
        public long getLastCleanupTime() { return lastCleanupTime; }

        public static class BlacklistStatsBuilder {
            private BlacklistStats stats = new BlacklistStats();

            public BlacklistStatsBuilder currentCount(long count) {
                stats.currentCount = count;
                return this;
            }

            public BlacklistStatsBuilder totalAdded(long total) {
                stats.totalAdded = total;
                return this;
            }

            public BlacklistStatsBuilder lastCleanupTime(long time) {
                stats.lastCleanupTime = time;
                return this;
            }

            public BlacklistStats build() {
                return stats;
            }
        }
    }
}
