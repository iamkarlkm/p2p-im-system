package com.im.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ShardingStrategy {
    
    private final int shardCount;
    private final ConcurrentHashMap<String, Integer> userShardCache = new ConcurrentHashMap<>();
    
    public ShardingStrategy(int shardCount) {
        this.shardCount = shardCount;
    }
    
    public int getShardIndexByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        String key = "user_" + userId;
        Integer cached = userShardCache.get(key);
        if (cached != null) {
            return cached;
        }
        
        int index = Math.abs((int) (userId % shardCount));
        userShardCache.put(key, index);
        
        log.debug("User {} mapped to shard {}", userId, index);
        return index;
    }
    
    public int getShardIndexByConversationId(Long conversationId) {
        if (conversationId == null) {
            return 0;
        }
        
        return Math.abs((int) (conversationId % shardCount));
    }
    
    public int getShardIndexByMessageId(Long messageId) {
        if (messageId == null) {
            return 0;
        }
        
        return Math.abs((int) (messageId % shardCount));
    }
    
    public String getShardTableSuffix(Long id, String tableName) {
        int shardIndex = getShardIndexByMessageId(id);
        return tableName + "_" + shardIndex;
    }
    
    public String getShardDataSourceName(Long userId) {
        int shardIndex = getShardIndexByUserId(userId);
        return "shard_" + shardIndex;
    }
    
    public int getShardCount() {
        return shardCount;
    }
    
    public void clearCache() {
        userShardCache.clear();
        log.info("Sharding cache cleared");
    }
}
