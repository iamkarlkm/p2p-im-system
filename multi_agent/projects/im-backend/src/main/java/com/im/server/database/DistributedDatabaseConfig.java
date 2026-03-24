package com.im.server.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式数据库配置
 * 支持多数据源、分库分表、读写分离
 */
@Configuration
@ConfigurationProperties(prefix = "im.database")
public class DistributedDatabaseConfig {

    // ==================== 主数据源配置 ====================
    private String primaryUrl = "jdbc:mysql://localhost:3306/im_primary?useSSL=false&serverTimezone=Asia/Shanghai";
    private String primaryUsername = "root";
    private String primaryPassword = "";
    private int primaryMaximumPoolSize = 20;
    private int primaryMinimumIdle = 5;

    // ==================== 从数据源配置（读库） ====================
    private List<ReplicaConfig> replicas = new ArrayList<>();

    // ==================== 分片配置 ====================
    private ShardingConfig sharding = new ShardingConfig();

    // ==================== 读写分离配置 ====================
    private ReadWriteSplitConfig readWriteSplit = new ReadWriteSplitConfig();

    // ==================== 连接池配置 ====================
    private ConnectionPoolConfig connectionPool = new ConnectionPoolConfig();

    // ==================== 事务配置 ====================
    private TransactionConfig transaction = new TransactionConfig();

    // ==================== Getters and Setters ====================

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public void setPrimaryUrl(String primaryUrl) {
        this.primaryUrl = primaryUrl;
    }

    public String getPrimaryUsername() {
        return primaryUsername;
    }

    public void setPrimaryUsername(String primaryUsername) {
        this.primaryUsername = primaryUsername;
    }

    public String getPrimaryPassword() {
        return primaryPassword;
    }

    public void setPrimaryPassword(String primaryPassword) {
        this.primaryPassword = primaryPassword;
    }

    public int getPrimaryMaximumPoolSize() {
        return primaryMaximumPoolSize;
    }

    public void setPrimaryMaximumPoolSize(int primaryMaximumPoolSize) {
        this.primaryMaximumPoolSize = primaryMaximumPoolSize;
    }

    public int getPrimaryMinimumIdle() {
        return primaryMinimumIdle;
    }

    public void setPrimaryMinimumIdle(int primaryMinimumIdle) {
        this.primaryMinimumIdle = primaryMinimumIdle;
    }

    public List<ReplicaConfig> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<ReplicaConfig> replicas) {
        this.replicas = replicas;
    }

    public ShardingConfig getSharding() {
        return sharding;
    }

    public void setSharding(ShardingConfig sharding) {
        this.sharding = sharding;
    }

    public ReadWriteSplitConfig getReadWriteSplit() {
        return readWriteSplit;
    }

    public void setReadWriteSplit(ReadWriteSplitConfig readWriteSplit) {
        this.readWriteSplit = readWriteSplit;
    }

    public ConnectionPoolConfig getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPoolConfig connectionPool) {
        this.connectionPool = connectionPool;
    }

    public TransactionConfig getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionConfig transaction) {
        this.transaction = transaction;
    }

    // ==================== 内部配置类 ====================

    /**
     * 从库配置
     */
    public static class ReplicaConfig {
        private String name;
        private String url;
        private String username;
        private String password;
        private int weight = 1;  // 权重，用于负载均衡
        private boolean enabled = true;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 分片配置
     */
    public static class ShardingConfig {
        private boolean enabled = false;
        private String strategy = "HASH";  // HASH, RANGE, TIME
        private int shardCount = 4;  // 分片数量
        private String shardColumn = "user_id";  // 分片键
        private List<String> tables = new ArrayList<>();  // 需要分片的表

        // 时间分片配置
        private String timeFormat = "yyyyMM";  // 时间格式
        private int retentionMonths = 12;  // 数据保留月数

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getStrategy() {
            return strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }

        public int getShardCount() {
            return shardCount;
        }

        public void setShardCount(int shardCount) {
            this.shardCount = shardCount;
        }

        public String getShardColumn() {
            return shardColumn;
        }

        public void setShardColumn(String shardColumn) {
            this.shardColumn = shardColumn;
        }

        public List<String> getTables() {
            return tables;
        }

        public void setTables(List<String> tables) {
            this.tables = tables;
        }

        public String getTimeFormat() {
            return timeFormat;
        }

        public void setTimeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
        }

        public int getRetentionMonths() {
            return retentionMonths;
        }

        public void setRetentionMonths(int retentionMonths) {
            this.retentionMonths = retentionMonths;
        }
    }

    /**
     * 读写分离配置
     */
    public static class ReadWriteSplitConfig {
        private boolean enabled = false;
        private boolean stickySession = false;  // 粘性会话，同一事务内使用同一连接
        private int replicaSelectorType = 1;  // 1=随机, 2=权重, 3=轮询
        private double readWriteRatio = 0.3;  // 读流量占比
        private List<String> readOnlyTables = new ArrayList<>();  // 只读表（强制走从库）

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isStickySession() {
            return stickySession;
        }

        public void setStickySession(boolean stickySession) {
            this.stickySession = stickySession;
        }

        public int getReplicaSelectorType() {
            return replicaSelectorType;
        }

        public void setReplicaSelectorType(int replicaSelectorType) {
            this.replicaSelectorType = replicaSelectorType;
        }

        public double getReadWriteRatio() {
            return readWriteRatio;
        }

        public void setReadWriteRatio(double readWriteRatio) {
            this.readWriteRatio = readWriteRatio;
        }

        public List<String> getReadOnlyTables() {
            return readOnlyTables;
        }

        public void setReadOnlyTables(List<String> readOnlyTables) {
            this.readOnlyTables = readOnlyTables;
        }
    }

    /**
     * 连接池配置
     */
    public static class ConnectionPoolConfig {
        private int maxPoolSize = 50;
        private int minIdle = 10;
        private long connectionTimeout = 30000;
        private long idleTimeout = 600000;
        private long maxLifetime = 1800000;
        private String poolName = "IM-DB-Pool";

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public long getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(long idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public long getMaxLifetime() {
            return maxLifetime;
        }

        public void setMaxLifetime(long maxLifetime) {
            this.maxLifetime = maxLifetime;
        }

        public String getPoolName() {
            return poolName;
        }

        public void setPoolName(String poolName) {
            this.poolName = poolName;
        }
    }

    /**
     * 事务配置
     */
    public static class TransactionConfig {
        private String transactionManager = "JTATransactionManager";  // JTATransactionManager, DataSourceTransactionManager
        private int isolationLevel = 4;  // 1=READ_UNCOMMITTED, 2=READ_COMMITTED, 4=REPEATABLE_READ, 8=SERIALIZABLE
        private int propagation = 1;  // 1=REQUIRED, 2=SUPPORTS, 3=MANDATORY, 4=REQUIRES_NEW, 5=NOT_SUPPORTED, 6=NEVER, 7=NESTED
        private long timeout = 30;  // 事务超时时间（秒）
        private boolean readOnly = false;

        public String getTransactionManager() {
            return transactionManager;
        }

        public void setTransactionManager(String transactionManager) {
            this.transactionManager = transactionManager;
        }

        public int getIsolationLevel() {
            return isolationLevel;
        }

        public void setIsolationLevel(int isolationLevel) {
            this.isolationLevel = isolationLevel;
        }

        public int getPropagation() {
            return propagation;
        }

        public void setPropagation(int propagation) {
            this.propagation = propagation;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public void setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
        }
    }
}
