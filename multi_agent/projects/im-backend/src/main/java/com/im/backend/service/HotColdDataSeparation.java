package com.im.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotColdDataSeparation {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String HOT_DATA_PREFIX = "hot:";
    private static final String COLD_DATA_PREFIX = "cold:";
    private static final int HOT_THRESHOLD_DAYS = 7;
    private static final int HOT_ACCESS_COUNT = 10;
    
    public void markDataAsHot(String dataType, Long dataId) {
        String key = HOT_DATA_PREFIX + dataType + ":" + dataId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        log.debug("Marked {} {} as hot", dataType, dataId);
    }
    
    public void markDataAsCold(String dataType, Long dataId) {
        String hotKey = HOT_DATA_PREFIX + dataType + ":" + dataId;
        String coldKey = COLD_DATA_PREFIX + dataType + ":" + dataId;
        
        redisTemplate.delete(hotKey);
        redisTemplate.opsForValue().set(coldKey, System.currentTimeMillis());
        redisTemplate.expire(coldKey, 90, TimeUnit.DAYS);
        
        log.debug("Marked {} {} as cold", dataType, dataId);
    }
    
    public boolean isHotData(String dataType, Long dataId) {
        String key = HOT_DATA_PREFIX + dataType + ":" + dataId;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
    
    public boolean isColdData(String dataType, Long dataId) {
        String key = COLD_DATA_PREFIX + dataType + ":" + dataId;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
    
    public int getAccessCount(String dataType, Long dataId) {
        String key = HOT_DATA_PREFIX + dataType + ":" + dataId;
        Object count = redisTemplate.opsForValue().get(key);
        if (count == null) {
            return 0;
        }
        return ((Number) count).intValue();
    }
    
    public void recordAccess(String dataType, Long dataId) {
        String key = HOT_DATA_PREFIX + dataType + ":" + dataId;
        Long newCount = redisTemplate.opsForValue().increment(key);
        
        if (newCount != null && newCount >= HOT_ACCESS_COUNT) {
            log.info("Data {} {} reached hot threshold", dataType, dataId);
        }
    }
    
    @Scheduled(fixedRate = 3600000)
    public void analyzeAndSeparateData() {
        log.info("Starting hot/cold data analysis");
        
        try {
            analyzeRecentMessages();
            analyzeActiveConversations();
            analyzeUserProfiles();
            
            log.info("Hot/cold data analysis completed");
        } catch (Exception e) {
            log.error("Error during hot/cold data analysis", e);
        }
    }
    
    private void analyzeRecentMessages() {
        log.debug("Analyzing recent messages for hot/cold separation");
    }
    
    private void analyzeActiveConversations() {
        log.debug("Analyzing active conversations for hot/cold separation");
    }
    
    private void analyzeUserProfiles() {
        log.debug("Analyzing user profiles for hot/cold separation");
    }
    
    public boolean shouldMigrateToCold(String dataType, Long dataId, Date lastAccessTime) {
        if (lastAccessTime == null) {
            return true;
        }
        
        LocalDateTime lastAccess = lastAccessTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        LocalDateTime threshold = LocalDateTime.now().minusDays(HOT_THRESHOLD_DAYS);
        
        return lastAccess.isBefore(threshold);
    }
    
    public void migrateToColdStorage(String dataType, Long dataId) {
        log.info("Migrating {} {} to cold storage", dataType, dataId);
        markDataAsCold(dataType, dataId);
    }
    
    public void migrateToHotStorage(String dataType, Long dataId) {
        log.info("Migrating {} {} to hot storage", dataType, dataId);
        markDataAsHot(dataType, dataId);
    }
    
    public long getColdDataAge(String dataType, Long dataId) {
        String key = COLD_DATA_PREFIX + dataType + ":" + dataId;
        Object timestamp = redisTemplate.opsForValue().get(key);
        
        if (timestamp == null) {
            return 0;
        }
        
        long coldSince = ((Number) timestamp).longValue();
        return System.currentTimeMillis() - coldSince;
    }
}
