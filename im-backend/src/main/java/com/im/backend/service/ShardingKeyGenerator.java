package com.im.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class ShardingKeyGenerator {
    
    private final AtomicLong sequence = new AtomicLong(0);
    private final long workerId;
    private final long dataCenterId;
    private final long epoch = 1609459200000L;
    
    public ShardingKeyGenerator() {
        this.workerId = 1;
        this.dataCenterId = 1;
    }
    
    public ShardingKeyGenerator(long workerId, long dataCenterId) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }
    
    public long generateId() {
        long timestamp = System.currentTimeMillis() - epoch;
        long sequenceId = sequence.incrementAndGet() & 4095;
        
        long id = (timestamp << 22) |
                  (dataCenterId << 17) |
                  (workerId << 12) |
                  sequenceId;
        
        log.debug("Generated ID: {}", id);
        return id;
    }
    
    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public String generateBatchId() {
        return "BATCH_" + System.currentTimeMillis() + "_" + generateUUID().substring(0, 8);
    }
    
    public String generateTransactionId() {
        return "TX_" + System.currentTimeMillis() + "_" + generateUUID().substring(0, 12);
    }
    
    public long getShardFromId(long id) {
        return (id >> 12) & 7;
    }
    
    public long extractTimestamp(long id) {
        return ((id >> 22) & 0x1FFFFFFFFFFL) + epoch;
    }
}
