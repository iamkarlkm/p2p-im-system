package com.im.server.database;

import com.im.server.database.DistributedDatabaseConfig.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式数据库服务
 * 提供统一的数据访问接口，自动处理分库分表、读写分离
 */
@Service
public class DistributedDatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedDatabaseService.class);

    private final DistributedDatabaseConfig config;
    private final ShardingStrategyManager shardingStrategy;
    private final DistributedTransactionManager transactionManager;
    private final DataSourceRouter dataSourceRouter;

    // 分片元数据缓存
    private final ConcurrentHashMap<String, ShardMetadata> shardMetadataCache = new ConcurrentHashMap<>();

    // 统计信息
    private final AtomicLong totalReadCount = new AtomicLong(0);
    private final AtomicLong totalWriteCount = new AtomicLong(0);
    private final AtomicLong totalShardQueryCount = new AtomicLong(0);

    public DistributedDatabaseService(
            DistributedDatabaseConfig config,
            ShardingStrategyManager shardingStrategy,
            DistributedTransactionManager transactionManager,
            DataSourceRouter dataSourceRouter) {
        this.config = config;
        this.shardingStrategy = shardingStrategy;
        this.transactionManager = transactionManager;
        this.dataSourceRouter = dataSourceRouter;
        initialize();
    }

    /**
     * 初始化
     */
    private void initialize() {
        logger.info("初始化分布式数据库服务");
        loadShardMetadata();
    }

    /**
     * 加载分片元数据
     */
    private void loadShardMetadata() {
        ShardingConfig sharding = config.getSharding();
        if (sharding.isEnabled()) {
            logger.info("加载分片元数据，分片数: {}", sharding.getShardCount());
            for (int i = 0; i < sharding.getShardCount(); i++) {
                String dbName = shardingStrategy.getShardDatabaseName(i);
                String tablePattern = sharding.getTables().isEmpty() ? "all" :
                        String.join(",", sharding.getTables());
                shardMetadataCache.put(dbName, new ShardMetadata(dbName, i, tablePattern));
            }
        }
    }

    // ==================== 路由方法 ====================

    /**
     * 执行读操作（自动路由到从库）
     */
    public <T> T executeRead(String sql, Object... params) {
        totalReadCount.incrementAndGet();
        return DataSourceRouter.executeRead(() -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(getCurrentDataSource());
            return jdbcTemplate.queryForObject(sql, params);
        });
    }

    /**
     * 执行写操作（自动路由到主库）
     */
    public int executeWrite(String sql, Object... params) {
        totalWriteCount.incrementAndGet();
        return DataSourceRouter.executeWrite(() -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(getCurrentDataSource());
            return jdbcTemplate.update(sql, params);
        });
    }

    /**
     * 执行分片写操作
     */
    public int executeShardWrite(String tableName, Object shardKey, String sql, Object... params) {
        int shardIndex = shardingStrategy.calculateShardIndex(tableName, shardKey);
        String shardTableName = shardingStrategy.getShardTableName(tableName, shardIndex);

        totalShardQueryCount.incrementAndGet();

        return DataSourceRouter.executeShard(shardIndex, () -> {
            // 替换表名
            String actualSql = sql.replace(tableName, shardTableName);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(getCurrentDataSource());
            return jdbcTemplate.update(actualSql, params);
        });
    }

    /**
     * 执行分片读操作
     */
    public <T> List<T> executeShardRead(String tableName, Object shardKey, String sql, Class<T> rowMapper, Object... params) {
        int shardIndex = shardingStrategy.calculateShardIndex(tableName, shardKey);
        String shardTableName = shardingStrategy.getShardTableName(tableName, shardIndex);

        totalShardQueryCount.incrementAndGet();

        return DataSourceRouter.executeRead(() -> {
            String actualSql = sql.replace(tableName, shardTableName);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(getCurrentDataSource());
            return jdbcTemplate.query(actualSql, params, (rs, rowNum) -> {
                // 简单实现，实际应该使用RowMapper
                return null;
            });
        });
    }

    /**
     * 跨分片查询（散写归并读）
     */
    public <T> List<T> executeCrossShardQuery(String tableName, String sql, Class<T> rowMapper, Object... params) {
        ShardingConfig sharding = config.getSharding();
        int shardCount = sharding.getShardCount();

        List<T> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < shardCount; i++) {
            String shardTableName = shardingStrategy.getShardTableName(tableName, i);
            String actualSql = sql.replace(tableName, shardTableName);

            try {
                DataSourceRouter.executeRead(() -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(getCurrentDataSource());
                    List<T> shardResults = jdbcTemplate.query(actualSql, params, (rs, rowNum) -> null);
                    results.addAll(shardResults);
                    return null;
                });
            } catch (Exception e) {
                logger.error("跨分片查询失败: shard={}", i, e);
            }
        }

        return results;
    }

    // ==================== 事务方法 ====================

    /**
     * 执行分布式事务
     */
    public <T> T executeTransaction(TransactionCallback<T> callback) {
        String transactionId = transactionManager.beginDistributedTransaction();
        try {
            T result = callback.execute();
            boolean committed = transactionManager.commit(transactionId);
            if (!committed) {
                throw new RuntimeException("事务提交失败");
            }
            return result;
        } catch (Exception e) {
            transactionManager.rollback(transactionId);
            throw new RuntimeException("事务执行失败", e);
        }
    }

    /**
     * 执行TCC补偿事务
     */
    public boolean executeTCC(DistributedTransactionManager.TCCOperation operation) {
        String transactionId = transactionManager.beginDistributedTransaction();
        return transactionManager.tryExecute(transactionId, operation);
    }

    // ==================== 分片管理 ====================

    /**
     * 获取所有分片信息
     */
    public List<ShardInfo> getAllShardInfo() {
        List<ShardInfo> shardInfos = new ArrayList<>();
        for (int i = 0; i < config.getSharding().getShardCount(); i++) {
            String dbName = shardingStrategy.getShardDatabaseName(i);
            shardInfos.add(new ShardInfo(
                    i,
                    dbName,
                    shardMetadataCache.get(dbName)
            ));
        }
        return shardInfos;
    }

    /**
     * 获取表在所有分片上的记录数
     */
    public Map<Integer, Long> getTableCountAcrossShards(String tableName) {
        Map<Integer, Long> counts = new ConcurrentHashMap<>();
        int shardCount = config.getSharding().getShardCount();

        for (int i = 0; i < shardCount; i++) {
            String shardTableName = shardingStrategy.getShardTableName(tableName, i);
            String sql = "SELECT COUNT(*) FROM " + shardTableName;

            try {
                Long count = DataSourceRouter.executeRead(() -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(getCurrentDataSource());
                    return jdbcTemplate.queryForObject(sql, Long.class);
                });
                counts.put(i, count != null ? count : 0L);
            } catch (Exception e) {
                logger.error("获取分片{}的记录数失败", i, e);
                counts.put(i, -1L);
            }
        }

        return counts;
    }

    /**
     * 清理过期分片数据
     */
    public int cleanupExpiredShards(String tableName) {
        int cleanedCount = 0;
        // 实现数据清理逻辑
        logger.info("清理过期分片数据: {}", tableName);
        return cleanedCount;
    }

    // ==================== 统计信息 ====================

    /**
     * 获取数据库统计信息
     */
    public DatabaseStats getDatabaseStats() {
        return new DatabaseStats(
                totalReadCount.get(),
                totalWriteCount.get(),
                totalShardQueryCount.get(),
                config.getSharding().isEnabled() ? config.getSharding().getShardCount() : 1,
                shardMetadataCache.size()
        );
    }

    /**
     * 重置统计信息
     */
    public void resetStats() {
        totalReadCount.set(0);
        totalWriteCount.set(0);
        totalShardQueryCount.set(0);
    }

    // ==================== 辅助方法 ====================

    private DataSource getCurrentDataSource() {
        DataSourceRouter.DataSourceType type = DataSourceRouter.getDataSourceType();
        if (type == DataSourceRouter.DataSourceType.REPLICA) {
            return dataSourceRouter.selectReplica();
        }
        return dataSourceRouter.getPrimaryDataSource();
    }

    // ==================== 内部类 ====================

    @FunctionalInterface
    public interface TransactionCallback<T> {
        T execute();
    }

    /**
     * 分片元数据
     */
    public static class ShardMetadata {
        private final String databaseName;
        private final int shardIndex;
        private final String tablePattern;
        private long recordCount;
        private long dataSize;
        private long lastUpdated;

        public ShardMetadata(String databaseName, int shardIndex, String tablePattern) {
            this.databaseName = databaseName;
            this.shardIndex = shardIndex;
            this.tablePattern = tablePattern;
            this.lastUpdated = System.currentTimeMillis();
        }

        public String getDatabaseName() { return databaseName; }
        public int getShardIndex() { return shardIndex; }
        public String getTablePattern() { return tablePattern; }
        public long getRecordCount() { return recordCount; }
        public void setRecordCount(long recordCount) { this.recordCount = recordCount; }
        public long getDataSize() { return dataSize; }
        public void setDataSize(long dataSize) { this.dataSize = dataSize; }
        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    /**
     * 分片信息
     */
    public static class ShardInfo {
        private final int shardIndex;
        private final String databaseName;
        private final ShardMetadata metadata;

        public ShardInfo(int shardIndex, String databaseName, ShardMetadata metadata) {
            this.shardIndex = shardIndex;
            this.databaseName = databaseName;
            this.metadata = metadata;
        }

        public int getShardIndex() { return shardIndex; }
        public String getDatabaseName() { return databaseName; }
        public ShardMetadata getMetadata() { return metadata; }
    }

    /**
     * 数据库统计
     */
    public static class DatabaseStats {
        private final long totalReads;
        private final long totalWrites;
        private final long totalShardQueries;
        private final int shardCount;
        private final int activeShards;

        public DatabaseStats(long totalReads, long totalWrites, long totalShardQueries, int shardCount, int activeShards) {
            this.totalReads = totalReads;
            this.totalWrites = totalWrites;
            this.totalShardQueries = totalShardQueries;
            this.shardCount = shardCount;
            this.activeShards = activeShards;
        }

        public long getTotalReads() { return totalReads; }
        public long getTotalWrites() { return totalWrites; }
        public long getTotalShardQueries() { return totalShardQueries; }
        public int getShardCount() { return shardCount; }
        public int getActiveShards() { return activeShards; }
        public double getReadWriteRatio() {
            long total = totalReads + totalWrites;
            return total > 0 ? (double) totalReads / total : 0.5;
        }
    }
}
