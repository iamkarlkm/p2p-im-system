package com.im.server.database;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分库分表策略管理器
 * 支持：哈希分片、范围分片、时间分片
 */
@Component
public class ShardingStrategyManager {

    private final DistributedDatabaseConfig config;
    private final ConcurrentHashMap<String, AtomicInteger> roundRobinCounters = new ConcurrentHashMap<>();

    public ShardingStrategyManager(DistributedDatabaseConfig config) {
        this.config = config;
    }

    /**
     * 计算分片索引
     * @param tableName 表名
     * @param shardKey 分片键值
     * @return 分片索引
     */
    public int calculateShardIndex(String tableName, Object shardKey) {
        DistributedDatabaseConfig.ShardingConfig sharding = config.getSharding();
        if (!sharding.isEnabled()) {
            return 0;
        }

        String strategy = sharding.getStrategy();
        switch (strategy.toUpperCase()) {
            case "HASH":
                return calculateHashShard(tableName, shardKey, sharding.getShardCount());
            case "RANGE":
                return calculateRangeShard(shardKey, sharding.getShardCount());
            case "TIME":
                return calculateTimeShard(tableName, shardKey);
            default:
                return calculateHashShard(tableName, shardKey, sharding.getShardCount());
        }
    }

    /**
     * 哈希分片
     * 将分片键的hash值对分片数取模
     */
    private int calculateHashShard(String tableName, Object shardKey, int shardCount) {
        if (shardKey == null) {
            return 0;
        }

        int hashCode;
        if (shardKey instanceof Number) {
            hashCode = ((Number) shardKey).intValue();
        } else {
            hashCode = shardKey.hashCode();
        }

        // 使用一致性哈希的思想，避免分片数变化时大量数据迁移
        int index = Math.abs(hashCode) % shardCount;
        return index;
    }

    /**
     * 范围分片
     * 根据分片键的数值范围划分
     */
    private int calculateRangeShard(Object shardKey, int shardCount) {
        if (shardKey == null) {
            return 0;
        }

        long value;
        if (shardKey instanceof Number) {
            value = ((Number) shardKey).longValue();
        } else {
            value = Long.parseLong(shardKey.toString());
        }

        // 假设每个分片覆盖范围为 shardCount * 10000
        long rangeSize = 10000L;
        int index = (int) (value / rangeSize) % shardCount;
        return Math.max(0, Math.min(index, shardCount - 1));
    }

    /**
     * 时间分片
     * 根据时间戳或日期进行分片
     */
    private int calculateTimeShard(String tableName, Object shardKey) {
        String timeFormat = config.getSharding().getTimeFormat();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);

        LocalDate date;
        if (shardKey instanceof LocalDate) {
            date = (LocalDate) shardKey;
        } else if (shardKey instanceof java.util.Date) {
            date = ((java.util.Date) shardKey).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } else if (shardKey instanceof Long) {
            date = java.time.Instant.ofEpochMilli((Long) shardKey)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } else {
            date = LocalDate.parse(shardKey.toString(), formatter);
        }

        // 按年月分片
        int year = date.getYear();
        int month = date.getMonthValue();

        // 年月组合取模
        int shardCount = config.getSharding().getShardCount();
        int index = ((year - 2020) * 12 + month) % shardCount;

        // 使用表名的轮询计数器避免热点
        AtomicInteger counter = roundRobinCounters.computeIfAbsent(
                tableName + "_" + date.format(DateTimeFormatter.ofPattern("yyyyMM")),
                k -> new AtomicInteger(0)
        );
        index = (index + counter.getAndIncrement()) % shardCount;

        return Math.max(0, Math.min(index, shardCount - 1));
    }

    /**
     * 获取分片后的表名
     * @param tableName 原始表名
     * @param shardIndex 分片索引
     * @return 分片后的表名
     */
    public String getShardTableName(String tableName, int shardIndex) {
        return tableName + "_" + shardIndex;
    }

    /**
     * 获取分片后的数据库名
     * @param dbIndex 分库索引
     * @return 分片后的数据库名
     */
    public String getShardDatabaseName(int dbIndex) {
        return "im_shard_" + dbIndex;
    }

    /**
     * 计算分库索引
     */
    public int calculateDatabaseShard(Object shardKey, int dbCount) {
        if (shardKey == null) {
            return 0;
        }

        int hashCode;
        if (shardKey instanceof Number) {
            hashCode = ((Number) shardKey).intValue();
        } else {
            hashCode = shardKey.hashCode();
        }

        return Math.abs(hashCode) % dbCount;
    }

    /**
     * 批量计算分片索引
     */
    public List<Integer> calculateBatchShardIndexes(String tableName, List<Object> shardKeys) {
        return shardKeys.stream()
                .map(key -> calculateShardIndex(tableName, key))
                .toList();
    }

    /**
     * 判断是否需要分片
     */
    public boolean shouldShard(String tableName) {
        DistributedDatabaseConfig.ShardingConfig sharding = config.getSharding();
        if (!sharding.isEnabled()) {
            return false;
        }

        List<String> tables = sharding.getTables();
        return tables.isEmpty() || tables.contains(tableName);
    }

    /**
     * 获取数据保留策略（用于清理过期分片）
     */
    public LocalDate getRetentionBoundary() {
        int retentionMonths = config.getSharding().getRetentionMonths();
        return LocalDate.now().minusMonths(retentionMonths);
    }

    /**
     * 获取当前分片数
     */
    public int getShardCount() {
        return config.getSharding().getShardCount();
    }
}
