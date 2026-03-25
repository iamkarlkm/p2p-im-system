package com.im.system.service;

import com.im.system.entity.MessageSearchEntity;
import com.im.system.repository.MessageSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息全文搜索服务
 */
@Service
@Transactional
public class MessageSearchService {
    
    @Autowired
    private MessageSearchRepository messageSearchRepository;
    
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int BATCH_SIZE = 1000;
    
    // ==================== 索引操作 ====================
    
    /**
     * 创建消息搜索索引
     */
    public MessageSearchEntity createIndex(MessageSearchEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("消息搜索实体不能为空");
        }
        
        // 设置默认值
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        if (entity.getUpdatedAt() == null) {
            entity.setUpdatedAt(LocalDateTime.now());
        }
        if (entity.getLanguage() == null) {
            entity.setLanguage(detectLanguage(entity.getContent()));
        }
        if (entity.getSentimentScore() == null) {
            entity.setSentimentScore(calculateSentimentScore(entity.getContent()));
        }
        
        return messageSearchRepository.save(entity);
    }
    
    /**
     * 批量创建索引
     */
    public List<MessageSearchEntity> batchCreateIndex(List<MessageSearchEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<MessageSearchEntity> savedEntities = new ArrayList<>();
        for (int i = 0; i < entities.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, entities.size());
            List<MessageSearchEntity> batch = entities.subList(i, end);
            
            // 设置默认值
            batch.forEach(entity -> {
                if (entity.getCreatedAt() == null) {
                    entity.setCreatedAt(LocalDateTime.now());
                }
                if (entity.getLanguage() == null) {
                    entity.setLanguage(detectLanguage(entity.getContent()));
                }
            });
            
            savedEntities.addAll(messageSearchRepository.saveAll(batch));
        }
        
        return savedEntities;
    }
    
    /**
     * 更新消息搜索索引
     */
    public MessageSearchEntity updateIndex(String messageId, MessageSearchEntity entity) {
        Optional<MessageSearchEntity> existing = messageSearchRepository.findByMessageId(messageId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("消息不存在: " + messageId);
        }
        
        MessageSearchEntity existingEntity = existing.get();
        
        // 更新字段
        if (entity.getContent() != null) {
            existingEntity.setContent(entity.getContent());
            existingEntity.incrementEditCount();
            existingEntity.setIsEdited(true);
        }
        if (entity.getSenderName() != null) {
            existingEntity.setSenderName(entity.getSenderName());
        }
        if (entity.getMessageType() != null) {
            existingEntity.setMessageType(entity.getMessageType());
        }
        if (entity.getHasAttachment() != null) {
            existingEntity.setHasAttachment(entity.getHasAttachment());
        }
        if (entity.getAttachmentCount() != null) {
            existingEntity.setAttachmentCount(entity.getAttachmentCount());
        }
        if (entity.getAttachmentTypes() != null) {
            existingEntity.setAttachmentTypes(entity.getAttachmentTypes());
        }
        if (entity.getTags() != null) {
            existingEntity.setTags(entity.getTags());
        }
        if (entity.getPriority() != null) {
            existingEntity.setPriority(entity.getPriority());
        }
        if (entity.getSentimentScore() != null) {
            existingEntity.setSentimentScore(entity.getSentimentScore());
        }
        if (entity.getMetadataJson() != null) {
            existingEntity.setMetadataJson(entity.getMetadataJson());
        }
        
        existingEntity.setUpdatedAt(LocalDateTime.now());
        
        return messageSearchRepository.save(existingEntity);
    }
    
    /**
     * 删除消息搜索索引
     */
    public boolean deleteIndex(String messageId) {
        Optional<MessageSearchEntity> entity = messageSearchRepository.findByMessageId(messageId);
        if (entity.isEmpty()) {
            return false;
        }
        
        messageSearchRepository.delete(entity.get());
        return true;
    }
    
    /**
     * 软删除消息
     */
    public boolean softDeleteIndex(String messageId) {
        int updated = messageSearchRepository.softDeleteByMessageId(messageId);
        return updated > 0;
    }
    
    /**
     * 恢复软删除消息
     */
    public boolean restoreIndex(String messageId) {
        int updated = messageSearchRepository.restoreByMessageId(messageId);
        return updated > 0;
    }
    
    // ==================== 全文搜索 ====================
    
    /**
     * 基础全文搜索
     */
    public Page<MessageSearchEntity> search(String query, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.fullTextSearch(query, pageable);
    }
    
    /**
     * 会话内全文搜索
     */
    public Page<MessageSearchEntity> searchInSession(String sessionId, String query, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.findBySessionIdAndContentContaining(sessionId, query, pageable);
    }
    
    /**
     * 发送者消息搜索
     */
    public Page<MessageSearchEntity> searchBySender(String senderId, String query, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.findBySenderIdAndContentContaining(senderId, query, pageable);
    }
    
    /**
     * 高级搜索
     */
    public Page<MessageSearchEntity> advancedSearch(AdvancedSearchRequest request) {
        Pageable pageable = createPageable(request.getPage(), request.getSize(), 
                                          Sort.by(Sort.Order.desc("createdAt")));
        
        Criteria criteria = new Criteria();
        
        // 关键词搜索
        if (StringUtils.hasText(request.getQuery())) {
            criteria.and(new Criteria("content").matches(request.getQuery()))
                   .or(new Criteria("plain_content").matches(request.getQuery()))
                   .or(new Criteria("sender_name").matches(request.getQuery()))
                   .or(new Criteria("tags").matches(request.getQuery()))
                   .or(new Criteria("keywords").matches(request.getQuery()));
        }
        
        // 会话过滤
        if (StringUtils.hasText(request.getSessionId())) {
            criteria.and(new Criteria("session_id").is(request.getSessionId()));
        }
        
        // 发送者过滤
        if (StringUtils.hasText(request.getSenderId())) {
            criteria.and(new Criteria("sender_id").is(request.getSenderId()));
        }
        
        // 消息类型过滤
        if (StringUtils.hasText(request.getMessageType())) {
            criteria.and(new Criteria("message_type").is(request.getMessageType()));
        }
        
        // 时间范围过滤
        if (request.getStartTime() != null && request.getEndTime() != null) {
            criteria.and(new Criteria("created_at").between(request.getStartTime(), request.getEndTime()));
        } else if (request.getStartTime() != null) {
            criteria.and(new Criteria("created_at").greaterThanEqual(request.getStartTime()));
        } else if (request.getEndTime() != null) {
            criteria.and(new Criteria("created_at").lessThanEqual(request.getEndTime()));
        }
        
        // 附件过滤
        if (request.getHasAttachment() != null) {
            criteria.and(new Criteria("has_attachment").is(request.getHasAttachment()));
        }
        
        // 加密过滤
        if (request.getIsEncrypted() != null) {
            criteria.and(new Criteria("is_encrypted").is(request.getIsEncrypted()));
        }
        
        // 语言过滤
        if (StringUtils.hasText(request.getLanguage())) {
            criteria.and(new Criteria("language").is(request.getLanguage()));
        }
        
        // 优先级过滤
        if (request.getMinPriority() != null) {
            criteria.and(new Criteria("priority").greaterThanEqual(request.getMinPriority()));
        }
        
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<MessageSearchEntity> searchHits = elasticsearchOperations.search(criteriaQuery, MessageSearchEntity.class);
        
        List<MessageSearchEntity> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
                content, 
                pageable, 
                searchHits.getTotalHits()
        );
    }
    
    /**
     * 短语搜索
     */
    public Page<MessageSearchEntity> phraseSearch(String phrase, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.phraseSearch(phrase, pageable);
    }
    
    /**
     * 模糊搜索
     */
    public Page<MessageSearchEntity> fuzzySearch(String query, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.fuzzySearch(query, pageable);
    }
    
    /**
     * 通配符搜索
     */
    public Page<MessageSearchEntity> wildcardSearch(String pattern, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.wildcardSearch(pattern, pageable);
    }
    
    /**
     * 正则表达式搜索
     */
    public Page<MessageSearchEntity> regexSearch(String regex, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.regexSearch(regex, pageable);
    }
    
    /**
     * 多关键词搜索
     */
    public Page<MessageSearchEntity> multiKeywordSearch(List<String> keywords, boolean andLogic, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        
        if (keywords == null || keywords.isEmpty()) {
            return Page.empty(pageable);
        }
        
        if (keywords.size() == 1) {
            return messageSearchRepository.fullTextSearch(keywords.get(0), pageable);
        }
        
        if (andLogic) {
            return messageSearchRepository.multiKeywordAndSearch(keywords.get(0), keywords.get(1), pageable);
        } else {
            return messageSearchRepository.multiKeywordOrSearch(keywords.get(0), keywords.get(1), pageable);
        }
    }
    
    /**
     * 排除关键词搜索
     */
    public Page<MessageSearchEntity> excludeKeywordSearch(String include, String exclude, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.excludeKeywordSearch(include, exclude, pageable);
    }
    
    /**
     * 加权搜索
     */
    public Page<MessageSearchEntity> weightedSearch(String query, Map<String, Integer> fieldWeights, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        
        // 如果有自定义权重，使用Criteria查询
        if (fieldWeights != null && !fieldWeights.isEmpty()) {
            Criteria criteria = new Criteria();
            for (Map.Entry<String, Integer> entry : fieldWeights.entrySet()) {
                criteria.or(new Criteria(entry.getKey()).matches(query).boost(entry.getValue().floatValue()));
            }
            
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<MessageSearchEntity> searchHits = elasticsearchOperations.search(criteriaQuery, MessageSearchEntity.class);
            
            List<MessageSearchEntity> content = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
            
            return new org.springframework.data.domain.PageImpl<>(
                    content, 
                    pageable, 
                    searchHits.getTotalHits()
            );
        }
        
        return messageSearchRepository.weightedSearch(query, pageable);
    }
    
    // ==================== 查询方法 ====================
    
    /**
     * 根据消息ID查找
     */
    public Optional<MessageSearchEntity> findByMessageId(String messageId) {
        return messageSearchRepository.findByMessageId(messageId);
    }
    
    /**
     * 获取会话消息
     */
    public Page<MessageSearchEntity> getSessionMessages(String sessionId, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.findBySessionId(sessionId, pageable);
    }
    
    /**
     * 获取发送者消息
     */
    public Page<MessageSearchEntity> getSenderMessages(String senderId, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.findBySenderId(senderId, pageable);
    }
    
    /**
     * 获取时间范围内消息
     */
    public List<MessageSearchEntity> getMessagesByTimeRange(LocalDateTime start, LocalDateTime end) {
        return messageSearchRepository.findByCreatedAtBetween(start, end);
    }
    
    /**
     * 获取会话时间范围内消息
     */
    public List<MessageSearchEntity> getSessionMessagesByTimeRange(String sessionId, LocalDateTime start, LocalDateTime end) {
        return messageSearchRepository.findBySessionIdAndCreatedAtBetween(sessionId, start, end);
    }
    
    /**
     * 获取发送者时间范围内消息
     */
    public List<MessageSearchEntity> getSenderMessagesByTimeRange(String senderId, LocalDateTime start, LocalDateTime end) {
        return messageSearchRepository.findBySenderIdAndCreatedAtBetween(senderId, start, end);
    }
    
    /**
     * 获取有附件的消息
     */
    public List<MessageSearchEntity> getMessagesWithAttachments() {
        return messageSearchRepository.findByHasAttachmentTrue();
    }
    
    /**
     * 获取加密消息
     */
    public List<MessageSearchEntity> getEncryptedMessages() {
        return messageSearchRepository.findByIsEncryptedTrue();
    }
    
    /**
     * 获取已编辑消息
     */
    public List<MessageSearchEntity> getEditedMessages() {
        return messageSearchRepository.findByIsEditedTrue();
    }
    
    /**
     * 获取高互动消息
     */
    public List<MessageSearchEntity> getHighInteractionMessages(int minInteraction) {
        return messageSearchRepository.findHighInteractionMessages(minInteraction);
    }
    
    /**
     * 获取热门消息
     */
    public List<MessageSearchEntity> getPopularMessages(int minReadCount) {
        return messageSearchRepository.findByReadCountGreaterThanEqual(minReadCount);
    }
    
    /**
     * 获取最新消息
     */
    public List<MessageSearchEntity> getLatestMessages(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("createdAt")));
        return messageSearchRepository.findAll(pageable).getContent();
    }
    
    /**
     * 获取会话最新消息
     */
    public List<MessageSearchEntity> getLatestSessionMessages(String sessionId, int limit) {
        return messageSearchRepository.findTop10BySessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取发送者最新消息
     */
    public List<MessageSearchEntity> getLatestSenderMessages(String senderId, int limit) {
        return messageSearchRepository.findTop10BySenderIdOrderByCreatedAtDesc(senderId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    // ==================== 统计方法 ====================
    
    /**
     * 统计消息数量
     */
    public long countMessages() {
        return messageSearchRepository.count();
    }
    
    /**
     * 统计会话消息数量
     */
    public long countSessionMessages(String sessionId) {
        return messageSearchRepository.countBySessionId(sessionId);
    }
    
    /**
     * 统计发送者消息数量
     */
    public long countSenderMessages(String senderId) {
        return messageSearchRepository.countBySenderId(senderId);
    }
    
    /**
     * 统计时间范围内消息数量
     */
    public long countMessagesByTimeRange(LocalDateTime start, LocalDateTime end) {
        return messageSearchRepository.countByTimeRange(start, end);
    }
    
    /**
     * 统计各类型消息数量
     */
    public Map<String, Long> countByMessageType() {
        List<Object[]> results = messageSearchRepository.countByMessageType();
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    /**
     * 统计各会话消息数量
     */
    public Map<String, Long> countBySession() {
        List<Object[]> results = messageSearchRepository.countBySession();
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    /**
     * 统计各发送者消息数量
     */
    public Map<String, Map<String, Object>> countBySender() {
        List<Object[]> results = messageSearchRepository.countBySender();
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("senderName", arr[1]);
                            map.put("messageCount", arr[2]);
                            return map;
                        }
                ));
    }
    
    /**
     * 获取索引健康统计
     */
    public Map<String, Object> getIndexHealthStats() {
        List<Object[]> results = messageSearchRepository.getIndexHealthStats();
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Object[] stats = results.get(0);
        Map<String, Object> healthStats = new HashMap<>();
        healthStats.put("totalMessages", stats[0]);
        healthStats.put("deletedMessages", stats[1]);
        healthStats.put("editedMessages", stats[2]);
        healthStats.put("totalAttachments", stats[3]);
        healthStats.put("averageContentLength", stats[4]);
        
        return healthStats;
    }
    
    /**
     * 获取索引大小统计
     */
    public Map<String, String> getIndexSizeStats() {
        List<Object[]> results = messageSearchRepository.getIndexSizeStats();
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Object[] stats = results.get(0);
        Map<String, String> sizeStats = new HashMap<>();
        sizeStats.put("schema", (String) stats[0]);
        sizeStats.put("tableName", (String) stats[1]);
        sizeStats.put("totalSize", (String) stats[2]);
        sizeStats.put("tableSize", (String) stats[3]);
        sizeStats.put("indexSize", (String) stats[4]);
        
        return sizeStats;
    }
    
    /**
     * 获取索引碎片率
     */
    public Map<String, Object> getIndexFragmentationStats() {
        List<Object[]> results = messageSearchRepository.getIndexFragmentationStats();
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Object[] stats = results.get(0);
        Map<String, Object> fragStats = new HashMap<>();
        fragStats.put("schema", stats[0]);
        fragStats.put("tableName", stats[1]);
        fragStats.put("deadTuples", stats[2]);
        fragStats.put("liveTuples", stats[3]);
        fragStats.put("deadTuplePercent", stats[4]);
        fragStats.put("lastAutoVacuum", stats[5]);
        fragStats.put("lastAutoAnalyze", stats[6]);
        
        return fragStats;
    }
    
    // ==================== 索引维护 ====================
    
    /**
     * 重建索引
     */
    public void rebuildIndex() {
        // 清除现有索引
        messageSearchRepository.deleteAll();
        
        // 重新从数据库加载数据并索引
        // 注意：这里需要实现从原始消息表加载数据的逻辑
        // 暂时留空，需要在具体业务中实现
    }
    
    /**
     * 刷新索引
     */
    public void refreshIndex() {
        messageSearchRepository.refresh();
    }
    
    /**
     * 优化索引
     */
    public void optimizeIndex() {
        messageSearchRepository.optimize();
    }
    
    /**
     * 删除过期消息
     */
    public int deleteExpiredMessages() {
        return messageSearchRepository.deleteExpiredMessages();
    }
    
    /**
     * 清除所有索引数据
     */
    public int clearAllIndex() {
        return messageSearchRepository.clearAll();
    }
    
    /**
     * 批量更新消息状态
     */
    public int batchUpdateStatus(List<String> messageIds, boolean deleted) {
        return messageSearchRepository.updateDeleteStatus(messageIds, deleted);
    }
    
    // ==================== 辅助方法 ====================
    
    private Pageable createPageable(int page, int size, Sort sort) {
        if (page < 0) page = 0;
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        if (size > MAX_PAGE_SIZE) size = MAX_PAGE_SIZE;
        
        return PageRequest.of(page, size, sort);
    }
    
    private String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }
        
        // 简单语言检测（实际应用中可以使用语言检测库）
        int chineseCount = 0;
        int englishCount = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                chineseCount++;
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                englishCount++;
            }
        }
        
        if (chineseCount > englishCount) {
            return "zh-CN";
        } else if (englishCount > chineseCount) {
            return "en-US";
        } else {
            return "mixed";
        }
    }
    
    private Float calculateSentimentScore(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }
        
        // 简单情感分析（实际应用中可以使用NLP库）
        String[] positiveWords = {"好", "喜欢", "爱", "开心", "高兴", "棒", "优秀", "完美", "谢谢", "感谢"};
        String[] negativeWords = {"坏", "讨厌", "恨", "伤心", "难过", "糟糕", "差", "垃圾", "生气", "愤怒"};
        
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String word : positiveWords) {
            if (text.contains(word)) {
                positiveCount++;
            }
        }
        
        for (String word : negativeWords) {
            if (text.contains(word)) {
                negativeCount++;
            }
        }
        
        int total = positiveCount + negativeCount;
        if (total == 0) {
            return 0.0f;
        }
        
        return (positiveCount - negativeCount) * 1.0f / total;
    }
    
    // ==================== DTO类 ====================
    
    public static class AdvancedSearchRequest {
        private String query;
        private String sessionId;
        private String senderId;
        private String messageType;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean hasAttachment;
        private Boolean isEncrypted;
        private String language;
        private Integer minPriority;
        private int page = 0;
        private int size = DEFAULT_PAGE_SIZE;
        
        // Getter 和 Setter 方法
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public Boolean getHasAttachment() { return hasAttachment; }
        public void setHasAttachment(Boolean hasAttachment) { this.hasAttachment = hasAttachment; }
        
        public Boolean getIsEncrypted() { return isEncrypted; }
        public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public Integer getMinPriority() { return minPriority; }
        public void setMinPriority(Integer minPriority) { this.minPriority = minPriority; }
        
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
    }
}