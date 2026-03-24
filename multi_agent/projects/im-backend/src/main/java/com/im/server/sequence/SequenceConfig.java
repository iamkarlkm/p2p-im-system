package com.im.server.sequence;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 消息序列号配置
 * 支持雪花算法、UUID、时间戳+随机数等多种生成策略
 */
@Configuration
@ConfigurationProperties(prefix = "im.sequence")
@Component
public class SequenceConfig {

    // 序列号生成策略: SNOWFLAKE, UUID, TIMESTAMP_RANDOM, HYBRID
    private String strategy = "HYBRID";

    // ==================== 雪花算法配置 ====================
    private SnowflakeConfig snowflake = new SnowflakeConfig();

    // ==================== 混合策略配置 ====================
    private HybridConfig hybrid = new HybridConfig();

    // ==================== 序列号格式 ====================
    private String format = "HYBRID";  // HYBRID, PURE_NUMBER, ALPHANUMERIC
    private int minLength = 16;
    private int maxLength = 32;

    // ==================== 缓存配置 ====================
    private int cacheSize = 1000;
    private long cacheExpireMs = 60000;

    // ==================== 分区配置 ====================
    private int partitionCount = 1;
    private int currentPartition = 0;

    // ==================== Getters and Setters ====================

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public SnowflakeConfig getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(SnowflakeConfig snowflake) {
        this.snowflake = snowflake;
    }

    public HybridConfig getHybrid() {
        return hybrid;
    }

    public void setHybrid(HybridConfig hybrid) {
        this.hybrid = hybrid;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getCacheExpireMs() {
        return cacheExpireMs;
    }

    public void setCacheExpireMs(long cacheExpireMs) {
        this.cacheExpireMs = cacheExpireMs;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    public int getCurrentPartition() {
        return currentPartition;
    }

    public void setCurrentPartition(int currentPartition) {
        this.currentPartition = currentPartition;
    }

    // ==================== 内部配置类 ====================

    /**
     * 雪花算法配置
     */
    public static class SnowflakeConfig {
        // 数据中心ID (0-31)
        private long datacenterId = 1;
        // 机器ID (0-31)
        private long machineId = 1;
        // 时间戳起始点 (2020-01-01)
        private long epoch = 1577836800000L;
        // 序列号位数
        private int sequenceBits = 12;
        // 机器ID位数
        private int machineIdBits = 5;
        // 数据中心ID位数
        private int datacenterIdBits = 5;

        // 计算出的偏移量
        private transient long timestampOffset;
        private transient long machineIdOffset;
        private transient long datacenterIdOffset;
        private transient long sequenceMask;

        public long getDatacenterId() {
            return datacenterId;
        }

        public void setDatacenterId(long datacenterId) {
            this.datacenterId = datacenterId;
        }

        public long getMachineId() {
            return machineId;
        }

        public void setMachineId(long machineId) {
            this.machineId = machineId;
        }

        public long getEpoch() {
            return epoch;
        }

        public void setEpoch(long epoch) {
            this.epoch = epoch;
        }

        public int getSequenceBits() {
            return sequenceBits;
        }

        public void setSequenceBits(int sequenceBits) {
            this.sequenceBits = sequenceBits;
        }

        public int getMachineIdBits() {
            return machineIdBits;
        }

        public void setMachineIdBits(int machineIdBits) {
            this.machineIdBits = machineIdBits;
        }

        public int getDatacenterIdBits() {
            return datacenterIdBits;
        }

        public void setDatacenterIdBits(int datacenterIdBits) {
            this.datacenterIdBits = datacenterIdBits;
        }

        // 计算偏移量
        public long getTimestampOffset() {
            return timestampOffset == 0 ? machineIdBits + datacenterIdBits + sequenceBits : timestampOffset;
        }

        public long getMachineIdOffset() {
            return machineIdOffset == 0 ? datacenterIdBits + sequenceBits : machineIdOffset;
        }

        public long getDatacenterIdOffset() {
            return datacenterIdOffset == 0 ? sequenceBits : datacenterIdOffset;
        }

        public long getSequenceMask() {
            return sequenceMask == 0 ? ~(-1L << sequenceBits) : sequenceMask;
        }
    }

    /**
     * 混合策略配置
     */
    public static class HybridConfig {
        // 时间戳位数
        private int timestampBits = 41;
        // 客户ID位数
        private int customerIdBits = 10;
        // 序列号位数
        private int sequenceBits = 12;
        // 时间戳起始点 (2020-01-01)
        private long epoch = 1577836800000L;
        // 自增序列起始值
        private long sequenceStart = 0;
        // 是否使用字母编码
        private boolean useAlphaEncoding = false;

        public int getTimestampBits() {
            return timestampBits;
        }

        public void setTimestampBits(int timestampBits) {
            this.timestampBits = timestampBits;
        }

        public int getCustomerIdBits() {
            return customerIdBits;
        }

        public void setCustomerIdBits(int customerIdBits) {
            this.customerIdBits = customerIdBits;
        }

        public int getSequenceBits() {
            return sequenceBits;
        }

        public void setSequenceBits(int sequenceBits) {
            this.sequenceBits = sequenceBits;
        }

        public long getEpoch() {
            return epoch;
        }

        public void setEpoch(long epoch) {
            this.epoch = epoch;
        }

        public long getSequenceStart() {
            return sequenceStart;
        }

        public void setSequenceStart(long sequenceStart) {
            this.sequenceStart = sequenceStart;
        }

        public boolean isUseAlphaEncoding() {
            return useAlphaEncoding;
        }

        public void setUseAlphaEncoding(boolean useAlphaEncoding) {
            this.useAlphaEncoding = useAlphaEncoding;
        }
    }
}
