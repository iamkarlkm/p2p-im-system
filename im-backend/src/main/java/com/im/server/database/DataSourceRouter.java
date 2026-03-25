package com.im.server.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源路由
 * 支持读写分离、分库分表
 */
@Component
public class DataSourceRouter extends AbstractRoutingDataSource {

    // 线程本地变量，存储当前数据源类型
    private static final ThreadLocal<DataSourceType> currentType = new ThreadLocal<>();

    // 从库选择器
    private final AtomicInteger replicaCounter = new AtomicInteger(0);
    private final Map<String, AtomicInteger> replicaCounters = new ConcurrentHashMap<>();

    // 数据源集合
    private DataSource primaryDataSource;
    private List<DataSource> replicaDataSources;

    // 读写分离配置
    private DistributedDatabaseConfig.ReadWriteSplitConfig rwConfig;

    public enum DataSourceType {
        PRIMARY,   // 主库（写）
        REPLICA,   // 从库（读）
        SHARD      // 分片库
    }

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType type = currentType.get();
        if (type == null) {
            return DataSourceType.PRIMARY.name();
        }
        return type.name();
    }

    /**
     * 设置当前数据源类型
     */
    public static void setDataSourceType(DataSourceType type) {
        currentType.set(type);
    }

    /**
     * 获取当前数据源类型
     */
    public static DataSourceType getDataSourceType() {
        return currentType.get();
    }

    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        currentType.remove();
    }

    /**
     * 执行写操作（自动切换到主库）
     */
    public static <T> T executeWrite(Executable<T> executor) {
        DataSourceType previous = currentType.get();
        try {
            currentType.set(DataSourceType.PRIMARY);
            return executor.execute();
        } finally {
            currentType.set(previous);
        }
    }

    /**
     * 执行读操作（自动切换到从库）
     */
    public static <T> T executeRead(Executable<T> executor) {
        DataSourceType previous = currentType.get();
        try {
            currentType.set(DataSourceType.REPLICA);
            return executor.execute();
        } finally {
            currentType.set(previous);
        }
    }

    /**
     * 执行分片操作
     */
    public static <T> T executeShard(int shardIndex, Executable<T> executor) {
        DataSourceType previous = currentType.get();
        try {
            currentType.set(DataSourceType.SHARD);
            return executor.execute();
        } finally {
            currentType.set(previous);
        }
    }

    /**
     * 选择从库（基于配置的负载均衡策略）
     */
    public DataSource selectReplica() {
        if (replicaDataSources == null || replicaDataSources.isEmpty()) {
            return primaryDataSource;
        }

        int selectorType = rwConfig != null ? rwConfig.getReplicaSelectorType() : 1;

        switch (selectorType) {
            case 1:  // 随机选择
                int randomIndex = (int) (Math.random() * replicaDataSources.size());
                return replicaDataSources.get(randomIndex);

            case 2:  // 权重轮询
                return selectByWeight();

            case 3:  // 简单轮询
            default:
                return selectByRoundRobin();

            case 4:  // 最少连接
                return selectLeastConnections();
        }
    }

    /**
     * 权重选择
     */
    private DataSource selectByWeight() {
        // 简化实现，实际应该根据权重计算
        int totalWeight = replicaDataSources.size();
        int index = replicaCounter.getAndIncrement() % totalWeight;
        return replicaDataSources.get(index);
    }

    /**
     * 轮询选择
     */
    private DataSource selectByRoundRobin() {
        int index = replicaCounter.getAndIncrement() % replicaDataSources.size();
        return replicaDataSources.get(index);
    }

    /**
     * 最少连接选择（简化版）
     */
    private DataSource selectLeastConnections() {
        // 简化实现，实际应该跟踪每个从库的活跃连接数
        return replicaDataSources.get(0);
    }

    // ==================== Getters and Setters ====================

    public void setPrimaryDataSource(DataSource primaryDataSource) {
        this.primaryDataSource = primaryDataSource;
    }

    public void setReplicaDataSources(List<DataSource> replicaDataSources) {
        this.replicaDataSources = replicaDataSources;
    }

    public void setRwConfig(DistributedDatabaseConfig.ReadWriteSplitConfig rwConfig) {
        this.rwConfig = rwConfig;
    }

    public DataSource getPrimaryDataSource() {
        return primaryDataSource;
    }

    public List<DataSource> getReplicaDataSources() {
        return replicaDataSources;
    }

    /**
     * 函数式接口
     */
    @FunctionalInterface
    public interface Executable<T> {
        T execute();
    }
}
