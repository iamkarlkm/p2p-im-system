package com.im.server.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 序列号生成器
 * 支持：雪花算法、UUID、时间戳+随机数、混合策略
 */
@Component
public class SequenceGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SequenceGenerator.class);

    private final SequenceConfig config;

    // ==================== 雪花算法状态 ====================
    private final AtomicLong snowflakeTimestamp = new AtomicLong(0);
    private final AtomicLong snowflakeSequence = new AtomicLong(0);

    // ==================== 混合策略状态 ====================
    private final ConcurrentLinkedQueue<Long> hybridSequenceCache = new ConcurrentLinkedQueue<>();
    private final AtomicLong hybridTimestamp = new AtomicLong(0);
    private final AtomicLong hybridSequence = new AtomicLong(0);
    private final AtomicReference<String> hybridLastDate = new AtomicReference<>();

    // ==================== 时间戳基准 ====================
    private static final long TIMESTAMP_BASE = 1577836800000L;  // 2020-01-01 00:00:00

    public SequenceGenerator(SequenceConfig config) {
        this.config = config;
        initializeCache();
    }

    /**
     * 初始化序列号缓存
     */
    private void initializeCache() {
        // 预填充缓存
        if ("HYBRID".equalsIgnoreCase(config.getStrategy())) {
            int cacheSize = config.getCacheSize();
            for (int i = 0; i < cacheSize; i++) {
                hybridSequenceCache.offer(0L);
            }
        }
    }

    /**
     * 生成序列号（通用方法）
     */
    public String generate(Long customerId) {
        String strategy = config.getStrategy().toUpperCase();

        switch (strategy) {
            case "SNOWFLAKE":
                return String.valueOf(generateSnowflake());
            case "UUID":
                return generateUUID();
            case "TIMESTAMP_RANDOM":
                return generateTimestampRandom();
            case "HYBRID":
            default:
                return generateHybrid(customerId);
        }
    }

    /**
     * 生成纯数字序列号
     */
    public long generateNumeric(Long customerId) {
        String seq = generate(customerId);
        try {
            return Long.parseLong(seq);
        } catch (NumberFormatException e) {
            // 如果不是纯数字，返回雪花算法
            return generateSnowflake();
        }
    }

    // ==================== 雪花算法 ====================

    /**
     * 生成雪花算法序列号
     * 格式: timestamp(41bits) + datacenterId(5bits) + machineId(5bits) + sequence(12bits)
     */
    public synchronized long generateSnowflake() {
        SequenceConfig.SnowflakeConfig sf = config.getSnowflake();

        long timestamp = System.currentTimeMillis() - sf.getEpoch();

        // 获取当前时间戳
        long currentTimestamp = timestamp;

        // 如果当前时间戳小于上一次的时间戳，说明时钟回拨
        long lastTimestamp = snowflakeTimestamp.get();
        if (currentTimestamp < lastTimestamp) {
            // 等待直到时间追上
            currentTimestamp = lastTimestamp;
        }

        // 如果时间戳相同，递增序列号
        if (currentTimestamp == lastTimestamp) {
            long seq = snowflakeSequence.incrementAndGet();
            if (seq >= sf.getSequenceMask()) {
                // 序列号用尽，等待下一个毫秒
                currentTimestamp = waitForNextTimestamp(lastTimestamp, sf.getEpoch());
                snowflakeSequence.set(0);
            }
        } else {
            // 时间戳变化，重置序列号
            snowflakeSequence.set(0);
        }

        snowflakeTimestamp.set(currentTimestamp);

        // 组装序列号
        long datacenterIdShift = sf.getMachineIdBits() + sf.getSequenceBits();
        long machineIdShift = sf.getSequenceBits();

        return (currentTimestamp << sf.getTimestampOffset())
                | ((sf.getDatacenterId() & ((1 << sf.getDatacenterIdBits()) - 1)) << sf.getDatacenterIdOffset())
                | ((sf.getMachineId() & ((1 << sf.getMachineIdBits()) - 1)) << sf.getMachineIdOffset())
                | (snowflakeSequence.get() & sf.getSequenceMask());
    }

    /**
     * 等待下一个时间戳
     */
    private long waitForNextTimestamp(long lastTimestamp, long epoch) {
        long timestamp = System.currentTimeMillis() - epoch;
        while (timestamp <= lastTimestamp) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            timestamp = System.currentTimeMillis() - epoch;
        }
        return timestamp;
    }

    /**
     * 解析雪花算法序列号
     */
    public SnowflakeInfo parseSnowflake(long id) {
        SequenceConfig.SnowflakeConfig sf = config.getSnowflake();

        long timestamp = (id >> sf.getTimestampOffset()) + sf.getEpoch();
        long datacenterId = (id >> sf.getDatacenterIdOffset()) & ((1 << sf.getDatacenterIdBits()) - 1);
        long machineId = (id >> sf.getMachineIdOffset()) & ((1 << sf.getMachineIdBits()) - 1);
        long sequence = id & sf.getSequenceMask();

        return new SnowflakeInfo(timestamp, datacenterId, machineId, sequence);
    }

    // ==================== UUID ====================

    /**
     * 生成UUID序列号
     */
    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带前缀的UUID
     */
    public String generateUUID(String prefix) {
        return prefix + generateUUID();
    }

    // ==================== 时间戳+随机数 ====================

    /**
     * 生成时间戳+随机数序列号
     */
    public String generateTimestampRandom() {
        long timestamp = System.currentTimeMillis() - TIMESTAMP_BASE;
        long random = (long) (Math.random() * 1000000);
        return String.format("%012d%06d", timestamp, random);
    }

    /**
     * 生成指定长度的时间戳+随机数序列号
     */
    public String generateTimestampRandom(int length) {
        String base = generateTimestampRandom();
        if (base.length() >= length) {
            return base.substring(0, length);
        }
        // 补齐到指定长度
        long random = (long) (Math.random() * Math.pow(10, length - base.length()));
        return base + String.format("%0" + (length - base.length()) + "d", random);
    }

    // ==================== 混合策略 ====================

    /**
     * 生成混合策略序列号
     * 格式: timestamp(41bits) + customerId(10bits) + sequence(12bits)
     * 可选: 字母编码
     */
    public String generateHybrid(Long customerId) {
        if (customerId == null) {
            customerId = 0L;
        }

        SequenceConfig.HybridConfig hybrid = config.getHybrid();

        long timestamp = System.currentTimeMillis() - hybrid.getEpoch();
        long currentSeq = getNextHybridSequence(hybrid);

        // 组装: timestamp << (customerIdBits + sequenceBits) | customerId << sequenceBits | sequence
        long seqId = (timestamp << (hybrid.getCustomerIdBits() + hybrid.getSequenceBits()))
                | ((customerId & ((1 << hybrid.getCustomerIdBits()) - 1)) << hybrid.getSequenceBits())
                | (currentSeq & ((1 << hybrid.getSequenceBits()) - 1));

        String result = String.valueOf(seqId);

        // 可选: 使用36进制字母编码压缩
        if (hybrid.isUseAlphaEncoding()) {
            result = encodeToAlpha(seqId);
        }

        return result;
    }

    /**
     * 获取下一个混合序列号
     */
    private long getNextHybridSequence(SequenceConfig.HybridConfig hybrid) {
        String today = String.valueOf(System.currentTimeMillis() / 86400000);

        // 检查是否跨天
        String lastDate = hybridLastDate.get();
        if (!today.equals(lastDate)) {
            synchronized (this) {
                if (!today.equals(hybridLastDate.get())) {
                    hybridLastDate.set(today);
                    hybridTimestamp.set(0);
                    hybridSequence.set(0);
                }
            }
        }

        long seq = hybridSequence.incrementAndGet();
        long maxSeq = (1 << hybrid.getSequenceBits()) - 1;

        if (seq >= maxSeq) {
            // 等待下一毫秒
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            hybridSequence.set(0);
            return hybridSequence.incrementAndGet();
        }

        return seq;
    }

    /**
     * 36进制字母编码
     */
    private String encodeToAlpha(long number) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int index = (int) (number % 36);
            sb.append(chars.charAt(index));
            number /= 36;
        }
        return sb.reverse().toString();
    }

    /**
     * 解码36进制字母编码
     */
    public long decodeFromAlpha(String encoded) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        long result = 0;
        for (int i = 0; i < encoded.length(); i++) {
            result = result * 36 + chars.indexOf(encoded.charAt(i));
        }
        return result;
    }

    // ==================== 批量生成 ====================

    /**
     * 批量生成序列号
     */
    public String[] generateBatch(Long customerId, int count) {
        String[] results = new String[count];
        for (int i = 0; i < count; i++) {
            results[i] = generate(customerId);
        }
        return results;
    }

    // ==================== 工具方法 ====================

    /**
     * 验证序列号格式
     */
    public boolean validate(String sequenceId) {
        if (sequenceId == null || sequenceId.isEmpty()) {
            return false;
        }

        String format = config.getFormat().toUpperCase();
        switch (format) {
            case "PURE_NUMBER":
                return sequenceId.matches("\\d+");
            case "ALPHANUMERIC":
                return sequenceId.matches("[A-Za-z0-9]+");
            default:
                return true;
        }
    }

    /**
     * 序列号信息
     */
    public static class SnowflakeInfo {
        private final long timestamp;
        private final long datacenterId;
        private final long machineId;
        private final long sequence;

        public SnowflakeInfo(long timestamp, long datacenterId, long machineId, long sequence) {
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.machineId = machineId;
            this.sequence = sequence;
        }

        public long getTimestamp() { return timestamp; }
        public long getDatacenterId() { return datacenterId; }
        public long getMachineId() { return machineId; }
        public long getSequence() { return sequence; }
    }
}
