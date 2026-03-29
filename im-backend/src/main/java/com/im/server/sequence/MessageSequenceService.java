package com.im.server.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息序列号服务
 * 提供全局唯一消息序列号，确保消息顺序，支持多点分发
 */
@Service
public class MessageSequenceService {

    private static final Logger logger = LoggerFactory.getLogger(MessageSequenceService.class);

    private final SequenceGenerator sequenceGenerator;
    private final SequenceRepository sequenceRepository;
    private final StringRedisTemplate redisTemplate;

    // 本地序列号缓存（减少Redis访问）
    private final Map<String, AtomicLong> localSequenceCache = new ConcurrentHashMap<>();
    private final Map<String, Long> localCacheExpiry = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60000;  // 1分钟

    // 分片序列号（用于多点分发）
    private final Map<String, AtomicLong> shardSequences = new ConcurrentHashMap<>();

    // 序列号状态
    private final Map<String, SequenceStatus> sequenceStatuses = new ConcurrentHashMap<>();

    public MessageSequenceService(
            SequenceGenerator sequenceGenerator,
            SequenceRepository sequenceRepository,
            StringRedisTemplate redisTemplate) {
        this.sequenceGenerator = sequenceGenerator;
        this.sequenceRepository = sequenceRepository;
        this.redisTemplate = redisTemplate;
    }

    // ==================== 序列号生成 ====================

    /**
     * 生成全局唯一消息序列号
     */
    public String generateMessageSequence(Long senderId, String conversationId) {
        // 使用混合策略生成
        String sequenceId = sequenceGenerator.generate(senderId);

        // 存储序列号元数据
        storeSequenceMetadata(sequenceId, senderId, conversationId);

        // 更新序列号状态
        updateSequenceStatus(sequenceId, "GENERATED");

        logger.debug("生成消息序列号: {}, senderId: {}, conversationId: {}", sequenceId, senderId, conversationId);

        return sequenceId;
    }

    /**
     * 生成带版本的消息序列号
     */
    public String generateVersionedSequence(Long senderId, String conversationId, int version) {
        String baseSeq = sequenceGenerator.generate(senderId);
        return baseSeq + "V" + version;
    }

    /**
     * 生成批量消息序列号
     */
    public List<String> generateBatchSequences(Long senderId, String conversationId, int count) {
        List<String> sequences = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sequences.add(generateMessageSequence(senderId, conversationId));
        }
        return sequences;
    }

    // ==================== 序列号管理 ====================

    /**
     * 获取下一个序列号（单调递增）
     */
    public long getNextSequence(String conversationId) {
        String cacheKey = "seq:conversation:" + conversationId;

        // 先检查本地缓存
        AtomicLong localSeq = localSequenceCache.get(conversationId);
        long now = System.currentTimeMillis();

        if (localSeq != null) {
            Long expiry = localCacheExpiry.get(conversationId);
            if (expiry != null && now < expiry) {
                return localSeq.incrementAndGet();
            }
        }

        // 从Redis获取全局序列号
        Long nextSeq = redisTemplate.opsForValue().increment(cacheKey);
        if (nextSeq == null) {
            nextSeq = 1L;
        }

        // 更新本地缓存
        localSeq = new AtomicLong(nextSeq);
        localSequenceCache.put(conversationId, localSeq);
        localCacheExpiry.put(conversationId, now + CACHE_TTL_MS);

        return nextSeq;
    }

    /**
     * 获取当前序列号（不递增）
     */
    public long getCurrentSequence(String conversationId) {
        String cacheKey = "seq:conversation:" + conversationId;
        String current = redisTemplate.opsForValue().get(cacheKey);
        return current != null ? Long.parseLong(current) : 0L;
    }

    /**
     * 批量获取序列号
     */
    public List<Long> getNextBatchSequences(String conversationId, int count) {
        List<Long> sequences = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sequences.add(getNextSequence(conversationId));
        }
        return sequences;
    }

    // ==================== 分片序列号 ====================

    /**
     * 生成分片序列号（用于消息去重）
     * 格式: {shardId}:{sequence}
     */
    public String generateShardSequence(int shardId, Long senderId) {
        String key = "shard:" + shardId + ":sender:" + senderId;
        Long seq = redisTemplate.opsForValue().increment(key);
        if (seq == null) {
            seq = 1L;
        }
        redisTemplate.expire(key, Duration.ofHours(24));
        return shardId + ":" + seq;
    }

    /**
     * 解析分片序列号
     */
    public ShardSequenceInfo parseShardSequence(String shardSequence) {
        String[] parts = shardSequence.split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            return new ShardSequenceInfo(Integer.parseInt(parts[0]), Long.parseLong(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取分片内的下一个序列号
     */
    public long getNextShardSequence(int shardId, String conversationId) {
        String key = "shard:" + shardId + ":conv:" + conversationId;
        Long seq = redisTemplate.opsForValue().increment(key);
        return seq != null ? seq : 1L;
    }

    // ==================== 序列号解析 ====================

    /**
     * 解析消息序列号
     */
    public MessageSequenceInfo parseSequence(String sequenceId) {
        if (sequenceId == null || sequenceId.isEmpty()) {
            return null;
        }

        try {
            long numericId = Long.parseLong(sequenceId.replaceAll("[^0-9]", ""));

            // 尝试解析雪花算法
            SequenceGenerator.SnowflakeInfo snowflakeInfo = sequenceGenerator.parseSnowflake(numericId);
            if (snowflakeInfo != null) {
                return new MessageSequenceInfo(
                        sequenceId,
                        new Date(snowflakeInfo.getTimestamp()),
                        (int) snowflakeInfo.getDatacenterId(),
                        (int) snowflakeInfo.getMachineId(),
                        (int) snowflakeInfo.getSequence()
                );
            }

            // 尝试解析混合策略
            return parseHybridSequence(sequenceId);

        } catch (Exception e) {
            logger.error("解析序列号失败: {}", sequenceId, e);
            return null;
        }
    }

    /**
     * 解析混合策略序列号
     */
    private MessageSequenceInfo parseHybridSequence(String sequenceId) {
        try {
            // 简化的混合序列号解析
            long numericId = Long.parseLong(sequenceId);
            long timestamp = (numericId >> 22) + 1577836800000L;
            return new MessageSequenceInfo(
                    sequenceId,
                    new Date(timestamp),
                    0, 0, 0
            );
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 序列号比较 ====================

    /**
     * 比较两个序列号的顺序
     * 返回: -1 (seq1 < seq2), 0 (相等), 1 (seq1 > seq2)
     */
    public int compareSequences(String seq1, String seq2) {
        if (seq1 == null || seq2 == null) {
            return seq1 == seq2 ? 0 : (seq1 == null ? -1 : 1);
        }

        try {
            // 如果都是纯数字
            if (seq1.matches("\\d+") && seq2.matches("\\d+")) {
                long n1 = Long.parseLong(seq1);
                long n2 = Long.parseLong(seq2);
                return Long.compare(n1, n2);
            }

            // 解析并比较雪花算法时间戳
            MessageSequenceInfo info1 = parseSequence(seq1);
            MessageSequenceInfo info2 = parseSequence(seq2);

            if (info1 != null && info2 != null) {
                return info1.getTimestamp().compareTo(info2.getTimestamp());
            }

            // 字符串比较作为兜底
            return seq1.compareTo(seq2);

        } catch (Exception e) {
            logger.error("比较序列号失败: {} vs {}", seq1, seq2, e);
            return seq1.compareTo(seq2);
        }
    }

    /**
     * 判断序列号是否在指定范围内
     */
    public boolean isInRange(String sequenceId, String startSeq, String endSeq) {
        return compareSequences(sequenceId, startSeq) >= 0
                && compareSequences(sequenceId, endSeq) <= 0;
    }

    // ==================== 序列号存储 ====================

    /**
     * 存储序列号元数据
     */
    private void storeSequenceMetadata(String sequenceId, Long senderId, String conversationId) {
        try {
            SequenceMetadata metadata = new SequenceMetadata();
            metadata.setSequenceId(sequenceId);
            metadata.setSenderId(senderId);
            metadata.setConversationId(conversationId);
            metadata.setCreatedAt(System.currentTimeMillis());
            metadata.setStatus("ACTIVE");

            sequenceRepository.save(metadata);
        } catch (Exception e) {
            logger.error("存储序列号元数据失败", e);
        }
    }

    /**
     * 获取序列号元数据
     */
    public SequenceMetadata getSequenceMetadata(String sequenceId) {
        return sequenceRepository.findBySequenceId(sequenceId);
    }

    /**
     * 更新序列号状态
     */
    public void updateSequenceStatus(String sequenceId, String status) {
        SequenceMetadata metadata = getSequenceMetadata(sequenceId);
        if (metadata != null) {
            metadata.setStatus(status);
            metadata.setUpdatedAt(System.currentTimeMillis());
            sequenceRepository.save(metadata);
        }
    }

    // ==================== 序列号追踪 ====================

    /**
     * 追踪消息序列号的流转路径
     */
    public List<SequenceTrace> traceSequence(String sequenceId) {
        List<SequenceTrace> traces = new ArrayList<>();

        // 获取序列号元数据
        SequenceMetadata metadata = getSequenceMetadata(sequenceId);
        if (metadata != null) {
            traces.add(new SequenceTrace(
                    metadata.getCreatedAt(),
                    "MESSAGE_CREATED",
                    "消息创建"
            ));
        }

        // 获取序列号状态历史
        SequenceStatus status = sequenceStatuses.get(sequenceId);
        if (status != null) {
            for (SequenceStatus.StatusChange change : status.getChanges()) {
                traces.add(new SequenceTrace(
                        change.getTimestamp(),
                        change.getStatus(),
                        change.getDescription()
                ));
            }
        }

        // 按时间排序
        traces.sort(Comparator.comparing(SequenceTrace::getTimestamp));

        return traces;
    }

    /**
     * 验证序列号的唯一性
     */
    public boolean verifyUniqueness(String sequenceId) {
        SequenceMetadata metadata = getSequenceMetadata(sequenceId);
        return metadata == null;
    }

    // ==================== 统计信息 ====================

    /**
     * 获取序列号统计信息
     */
    public SequenceStats getStats() {
        SequenceStats stats = new SequenceStats();

        stats.setTotalGenerated(sequenceRepository.count());
        stats.setActiveCount(sequenceRepository.countByStatus("ACTIVE"));
        stats.setUsedCount(sequenceRepository.countByStatus("USED"));
        stats.setExpiredCount(sequenceRepository.countByStatus("EXPIRED"));

        stats.setLocalCacheSize(localSequenceCache.size());
        stats.setShardSequenceCount(shardSequences.size());

        return stats;
    }

    // ==================== 清理过期数据 ====================

    /**
     * 清理过期的序列号数据
     */
    public void cleanupExpiredSequences(int daysToKeep) {
        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 86400000L);
        sequenceRepository.deleteByCreatedAtBefore(cutoffTime);
        logger.info("清理过期序列号数据，保留 {} 天前", daysToKeep);
    }

    // ==================== 内部类 ====================

    /**
     * 消息序列号信息
     */
    public static class MessageSequenceInfo {
        private final String sequenceId;
        private final Date timestamp;
        private final int datacenterId;
        private final int machineId;
        private final int sequence;

        public MessageSequenceInfo(String sequenceId, Date timestamp, int datacenterId, int machineId, int sequence) {
            this.sequenceId = sequenceId;
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.machineId = machineId;
            this.sequence = sequence;
        }

        public String getSequenceId() { return sequenceId; }
        public Date getTimestamp() { return timestamp; }
        public int getDatacenterId() { return datacenterId; }
        public int getMachineId() { return machineId; }
        public int getSequence() { return sequence; }
    }

    /**
     * 分片序列号信息
     */
    public static class ShardSequenceInfo {
        private final int shardId;
        private final long sequence;

        public ShardSequenceInfo(int shardId, long sequence) {
            this.shardId = shardId;
            this.sequence = sequence;
        }

        public int getShardId() { return shardId; }
        public long getSequence() { return sequence; }
    }

    /**
     * 序列号追踪信息
     */
    public static class SequenceTrace {
        private final long timestamp;
        private final String event;
        private final String description;

        public SequenceTrace(long timestamp, String event, String description) {
            this.timestamp = timestamp;
            this.event = event;
            this.description = description;
        }

        public long getTimestamp() { return timestamp; }
        public String getEvent() { return event; }
        public String getDescription() { return description; }
    }

    /**
     * 序列号统计
     */
    public static class SequenceStats {
        private long totalGenerated;
        private long activeCount;
        private long usedCount;
        private long expiredCount;
        private int localCacheSize;
        private int shardSequenceCount;

        public long getTotalGenerated() { return totalGenerated; }
        public void setTotalGenerated(long totalGenerated) { this.totalGenerated = totalGenerated; }
        public long getActiveCount() { return activeCount; }
        public void setActiveCount(long activeCount) { this.activeCount = activeCount; }
        public long getUsedCount() { return usedCount; }
        public void setUsedCount(long usedCount) { this.usedCount = usedCount; }
        public long getExpiredCount() { return expiredCount; }
        public void setExpiredCount(long expiredCount) { this.expiredCount = expiredCount; }
        public int getLocalCacheSize() { return localCacheSize; }
        public void setLocalCacheSize(int localCacheSize) { this.localCacheSize = localCacheSize; }
        public int getShardSequenceCount() { return shardSequenceCount; }
        public void setShardSequenceCount(int shardSequenceCount) { this.shardSequenceCount = shardSequenceCount; }
    }

    /**
     * 序列号状态
     */
    public static class SequenceStatus {
        private final String sequenceId;
        private final List<StatusChange> changes = new ArrayList<>();

        public SequenceStatus(String sequenceId) {
            this.sequenceId = sequenceId;
        }

        public String getSequenceId() { return sequenceId; }
        public List<StatusChange> getChanges() { return changes; }

        public void addChange(String status, String description) {
            changes.add(new StatusChange(System.currentTimeMillis(), status, description));
        }

        public static class StatusChange {
            private final long timestamp;
            private final String status;
            private final String description;

            public StatusChange(long timestamp, String status, String description) {
                this.timestamp = timestamp;
                this.status = status;
                this.description = description;
            }

            public long getTimestamp() { return timestamp; }
            public String getStatus() { return status; }
            public String getDescription() { return description; }
        }
    }
}
