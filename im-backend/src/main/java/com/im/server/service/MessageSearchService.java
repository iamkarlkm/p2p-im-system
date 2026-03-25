package com.im.server.service;

import com.im.server.config.SearchConfig;
import com.im.server.entity.MessageSearchIndex;
import com.im.server.repository.MessageSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Message Search Service
 * 
 * 消息搜索服务，提供全文搜索、关键词高亮、搜索建议等功能
 * 支持多种搜索模式：全文搜索、会话内搜索、组合搜索
 * 
 * 功能特性：
 * - 中文分词支持（IKAnalyzer 风格）
 * - 关键词高亮标注
 * - 搜索结果缓存
 * - 异步索引构建
 * - 热门搜索统计
 * - 搜索历史记录
 * - 搜索建议/补全
 * - 多条件组合搜索
 * - 搜索结果分页
 */
@Service
public class MessageSearchService {

    private static final Logger logger = LoggerFactory.getLogger(MessageSearchService.class);

    @Autowired
    private MessageSearchRepository searchRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== 核心搜索功能 ====================

    /**
     * 全文搜索消息
     * 
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 搜索结果分页
     */
    public SearchResult search(String keyword, int page, int size) {
        // 参数校验
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchResult.empty();
        }
        keyword = keyword.trim();
        
        if (keyword.length() < SearchConfig.MIN_KEYWORD_LENGTH) {
            return SearchResult.empty();
        }
        if (keyword.length() > SearchConfig.MAX_KEYWORD_LENGTH) {
            keyword = keyword.substring(0, SearchConfig.MAX_KEYWORD_LENGTH);
        }
        
        size = Math.min(size, SearchConfig.MAX_SEARCH_RESULTS);
        
        // 尝试从缓存获取
        String cacheKey = buildCacheKey(keyword, page, size);
        SearchResult cached = getCachedResult(cacheKey);
        if (cached != null) {
            logger.debug("Search cache hit for keyword: {}", keyword);
            return cached;
        }
        
        // 执行搜索
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageSearchIndex> resultPage = searchRepository.searchByKeyword(keyword, pageable);
        
        // 构建搜索结果
        SearchResult searchResult = buildSearchResult(resultPage, keyword);
        
        // 缓存结果
        cacheResult(cacheKey, searchResult);
        
        // 记录搜索统计（异步）
        recordSearchStatAsync(keyword);
        
        return searchResult;
    }

    /**
     * 在指定会话中搜索消息
     * 
     * @param conversationId 会话ID
     * @param conversationType 会话类型：1-私聊 2-群聊
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    public SearchResult searchInConversation(Long conversationId, Integer conversationType,
                                            String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchResult.empty();
        }
        keyword = keyword.trim();
        size = Math.min(size, SearchConfig.MAX_SEARCH_RESULTS);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageSearchIndex> resultPage;
        
        if (conversationId != null) {
            resultPage = searchRepository.searchInConversation(conversationId, keyword, pageable);
        } else if (conversationType != null) {
            if (conversationType == 1) {
                resultPage = searchRepository.searchPrivateMessages(keyword, pageable);
            } else if (conversationType == 2) {
                resultPage = searchRepository.searchGroupMessages(keyword, pageable);
            } else {
                resultPage = searchRepository.searchByKeyword(keyword, pageable);
            }
        } else {
            resultPage = searchRepository.searchByKeyword(keyword, pageable);
        }
        
        return buildSearchResult(resultPage, keyword);
    }

    /**
     * 在多个会话中搜索（用户所有会话）
     * 
     * @param conversationIds 会话ID列表
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    public SearchResult searchInConversations(List<Long> conversationIds, String keyword,
                                              int page, int size) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return SearchResult.empty();
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchResult.empty();
        }
        keyword = keyword.trim();
        size = Math.min(size, SearchConfig.MAX_SEARCH_RESULTS);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageSearchIndex> resultPage = searchRepository.searchInConversations(
            conversationIds, keyword, pageable);
        
        return buildSearchResult(resultPage, keyword);
    }

    /**
     * 组合搜索（多条件）
     * 
     * @param keyword 关键词
     * @param conversationId 会话ID
     * @param conversationType 会话类型
     * @param senderId 发送者ID
     * @param messageType 消息类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    public SearchResult searchWithFilters(String keyword, Long conversationId,
                                          Integer conversationType, Long senderId,
                                          Integer messageType, LocalDateTime startTime,
                                          LocalDateTime endTime, int page, int size) {
        size = Math.min(size, SearchConfig.MAX_SEARCH_RESULTS);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageSearchIndex> resultPage = searchRepository.searchWithFilters(
            keyword, conversationId, conversationType, senderId, messageType, startTime, endTime, pageable);
        
        return buildSearchResult(resultPage, keyword);
    }

    // ==================== 搜索建议功能 ====================

    /**
     * 获取搜索建议
     * 
     * @param prefix 输入前缀
     * @param limit 返回数量
     * @return 建议列表
     */
    public List<String> getSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        prefix = prefix.trim().toLowerCase();
        limit = Math.min(limit, 20);
        
        // 从热门搜索中匹配
        List<String> hotKeywords = getHotKeywords(10);
        List<String> suggestions = hotKeywords.stream()
            .filter(k -> k.toLowerCase().contains(prefix))
            .limit(limit)
            .collect(Collectors.toList());
        
        // 如果不够，从历史搜索中补充
        if (suggestions.size() < limit) {
            List<String> historyKeywords = getRecentSearchHistory("global", 10);
            historyKeywords.stream()
                .filter(k -> k.toLowerCase().contains(prefix))
                .filter(k -> !suggestions.contains(k))
                .limit(limit - suggestions.size())
                .forEach(suggestions::add);
        }
        
        return suggestions;
    }

    /**
     * 获取热门搜索关键词
     * 
     * @param limit 返回数量
     * @return 热门关键词列表
     */
    public List<String> getHotKeywords(int limit) {
        String cacheKey = SearchConfig.HOT_SEARCH_PREFIX + "daily";
        List<String> cached = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached.stream().limit(limit).collect(Collectors.toList());
        }
        
        // 查询最近7天的热门关键词
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, limit);
        List<String> keywords = searchRepository.findHotKeywords(since, pageable);
        
        if (!keywords.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, keywords, Duration.ofHours(1));
        }
        
        return keywords;
    }

    // ==================== 搜索历史功能 ====================

    /**
     * 记录用户搜索历史
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     */
    public void recordSearchHistory(Long userId, String keyword) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        keyword = keyword.trim();
        if (keyword.length() > SearchConfig.MAX_KEYWORD_LENGTH) {
            keyword = keyword.substring(0, SearchConfig.MAX_KEYWORD_LENGTH);
        }
        
        String key = SearchConfig.SEARCH_HISTORY_PREFIX + userId;
        
        // 使用 Redis List 存储历史记录
        redisTemplate.opsForList().leftPush(key, keyword);
        redisTemplate.opsForList().trim(key, 0, 49); // 最多保留50条
        
        // 设置过期时间（30天）
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    /**
     * 获取用户搜索历史
     * 
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 搜索历史列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getSearchHistory(Long userId, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        String key = SearchConfig.SEARCH_HISTORY_PREFIX + userId;
        List<String> history = redisTemplate.opsForList().range(key, 0, limit - 1);
        
        return history != null ? history : Collections.emptyList();
    }

    /**
     * 清空用户搜索历史
     * 
     * @param userId 用户ID
     */
    public void clearSearchHistory(Long userId) {
        if (userId == null) {
            return;
        }
        String key = SearchConfig.SEARCH_HISTORY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 获取最近的全局搜索历史（用于建议）
     */
    public List<String> getRecentSearchHistory(String userId, int limit) {
        return getSearchHistory(userId != null ? userId.hashCode() : 0L, limit);
    }

    // ==================== 索引管理功能 ====================

    /**
     * 索引单条消息（异步）
     * 
     * @param messageId 消息ID
     * @param conversationId 会话ID
     * @param conversationType 会话类型
     * @param senderId 发送者ID
     * @param senderNickname 发送者昵称
     * @param messageType 消息类型
     * @param content 消息内容
     * @param plainText 纯文本内容
     * @param fileName 文件名
     * @param mentionedUsers 提及的用户
     * @param messageTime 消息时间
     */
    @Async("searchIndexExecutor")
    public CompletableFuture<Void> indexMessageAsync(Long messageId, Long conversationId,
            Integer conversationType, Long senderId, String senderNickname,
            Integer messageType, String content, String plainText, String fileName,
            List<Long> mentionedUsers, LocalDateTime messageTime) {
        
        try {
            indexMessage(messageId, conversationId, conversationType, senderId,
                senderNickname, messageType, content, plainText, fileName,
                mentionedUsers, messageTime);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Failed to index message: {}", messageId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 索引单条消息（同步）
     */
    public void indexMessage(Long messageId, Long conversationId, Integer conversationType,
                            Long senderId, String senderNickname, Integer messageType,
                            String content, String plainText, String fileName,
                            List<Long> mentionedUsers, LocalDateTime messageTime) {
        // 检查是否已存在索引
        if (searchRepository.existsByMessageId(messageId)) {
            logger.debug("Message already indexed: {}", messageId);
            return;
        }
        
        // 提取关键词
        String keywords = extractKeywords(plainText);
        
        // 构建索引实体
        MessageSearchIndex index = new MessageSearchIndex();
        index.setMessageId(messageId);
        index.setConversationId(conversationId);
        index.setConversationType(conversationType);
        index.setSenderId(senderId);
        index.setSenderNickname(senderNickname);
        index.setMessageType(messageType);
        index.setContent(content);
        index.setPlainText(plainText);
        index.setFileName(fileName);
        index.setKeywords(keywords);
        index.setMentionedUsers(mentionedUsers != null ? 
            String.join(",", mentionedUsers.stream().map(String::valueOf).toArray(String[]::new)) : null);
        index.setMessageTime(messageTime);
        index.setIndexedAt(LocalDateTime.now());
        index.setDeleted(false);
        
        searchRepository.save(index);
        logger.debug("Message indexed successfully: {}", messageId);
    }

    /**
     * 删除消息索引
     * 
     * @param messageId 消息ID
     */
    public void deleteIndex(Long messageId) {
        Optional<MessageSearchIndex> optIndex = searchRepository.findAll().stream()
            .filter(i -> i.getMessageId().equals(messageId))
            .findFirst();
        
        if (optIndex.isPresent()) {
            MessageSearchIndex index = optIndex.get();
            index.setDeleted(true);
            index.setDeletedAt(LocalDateTime.now());
            searchRepository.save(index);
            logger.debug("Message index deleted: {}", messageId);
        }
    }

    // ==================== 内部工具方法 ====================

    /**
     * 构建搜索结果
     */
    private SearchResult buildSearchResult(Page<MessageSearchIndex> page, String keyword) {
        List<SearchResult.SearchHit> hits = new ArrayList<>();
        
        for (MessageSearchIndex index : page.getContent()) {
            // 高亮处理
            String highlightedContent = highlightText(index.getPlainText(), keyword);
            String snippet = generateSnippet(highlightedContent, keyword, 100);
            
            SearchResult.SearchHit hit = new SearchResult.SearchHit();
            hit.setMessageId(index.getMessageId());
            hit.setConversationId(index.getConversationId());
            hit.setConversationType(index.getConversationType());
            hit.setSenderId(index.getSenderId());
            hit.setSenderNickname(index.getSenderNickname());
            hit.setMessageType(index.getMessageType());
            hit.setContent(snippet);
            hit.setFullContent(highlightedContent);
            hit.setFileName(index.getFileName());
            hit.setMessageTime(index.getMessageTime());
            
            hits.add(hit);
        }
        
        SearchResult result = new SearchResult();
        result.setHits(hits);
        result.setTotal(page.getTotalElements());
        result.setPage(page.getNumber());
        result.setSize(page.getSize());
        result.setTotalPages(page.getTotalPages());
        result.setHasNext(page.hasNext());
        result.setHasPrevious(page.hasPrevious());
        result.setKeyword(keyword);
        
        return result;
    }

    /**
     * 关键词高亮
     */
    private String highlightText(String text, String keyword) {
        if (text == null || keyword == null) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        String lowerText = text.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        
        int lastEnd = 0;
        int index = lowerText.indexOf(lowerKeyword);
        
        while (index != -1) {
            // 添加高亮前的文本
            result.append(escapeHtml(text.substring(lastEnd, index)));
            // 添加高亮标签
            result.append(SearchConfig.HIGHLIGHT_PREFIX)
                  .append(escapeHtml(text.substring(index, index + keyword.length())))
                  .append(SearchConfig.HIGHLIGHT_SUFFIX);
            
            lastEnd = index + keyword.length();
            index = lowerText.indexOf(lowerKeyword, lastEnd);
        }
        
        // 添加剩余文本
        result.append(escapeHtml(text.substring(lastEnd)));
        
        return result.toString();
    }

    /**
     * HTML转义
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * 生成摘要
     */
    private String generateSnippet(String text, String keyword, int maxLength) {
        if (text == null) {
            return "";
        }
        
        // 查找关键词位置
        int keywordIndex = text.toLowerCase().indexOf(keyword.toLowerCase());
        
        if (keywordIndex == -1) {
            // 关键词不在文本中，返回开头部分
            return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
        }
        
        // 以关键词为中心生成摘要
        int start = Math.max(0, keywordIndex - maxLength / 2);
        int end = Math.min(text.length(), keywordIndex + keyword.length() + maxLength / 2);
        
        String snippet = text.substring(start, end);
        
        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < text.length()) {
            snippet = snippet + "...";
        }
        
        return snippet;
    }

    /**
     * 提取关键词（简单实现）
     * 实际生产环境建议使用 IKAnalyzer / HanLP 等分词库
     */
    private String extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // 简单分词：按空格和标点分割
        String[] words = text.split("[\\s，。、！？：；""''（）《》【】,!?;:\"'()\\[\\]{}]+");
        
        // 过滤掉短词和停用词
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个",
            "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好",
            "自己", "这", "那", "他", "她", "它", "们", "这个", "那个", "什么", "怎么",
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could", "should"
        ));
        
        StringBuilder keywords = new StringBuilder();
        for (String word : words) {
            if (word.length() >= 2 && !stopWords.contains(word.toLowerCase())) {
                if (keywords.length() > 0) {
                    keywords.append(",");
                }
                keywords.append(word);
            }
        }
        
        return keywords.toString();
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String keyword, int page, int size) {
        return String.format("im:search:%s:%d:%d", keyword.toLowerCase(), page, size);
    }

    /**
     * 获取缓存的搜索结果
     */
    private SearchResult getCachedResult(String cacheKey) {
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof SearchResult) {
                return (SearchResult) cached;
            }
        } catch (Exception e) {
            logger.warn("Failed to get cached search result", e);
        }
        return null;
    }

    /**
     * 缓存搜索结果
     */
    private void cacheResult(String cacheKey, SearchResult result) {
        try {
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(SearchConfig.SEARCH_CACHE_TTL_SECONDS));
        } catch (Exception e) {
            logger.warn("Failed to cache search result", e);
        }
    }

    /**
     * 异步记录搜索统计
     */
    @Async("searchIndexExecutor")
    public void recordSearchStatAsync(String keyword) {
        try {
            String statKey = SearchConfig.SEARCH_STATS_PREFIX + "daily:" + java.time.LocalDate.now();
            redisTemplate.opsForHash().increment(statKey, keyword.toLowerCase(), 1);
            redisTemplate.expire(statKey, Duration.ofDays(8)); // 保留8天
        } catch (Exception e) {
            logger.warn("Failed to record search stat", e);
        }
    }

    // ==================== 搜索结果内部类 ====================

    /**
     * 搜索结果封装
     */
    public static class SearchResult {
        private List<SearchHit> hits;
        private long total;
        private int page;
        private int size;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        private String keyword;

        public static SearchResult empty() {
            SearchResult result = new SearchResult();
            result.hits = Collections.emptyList();
            result.total = 0;
            result.page = 0;
            result.size = 0;
            result.totalPages = 0;
            result.hasNext = false;
            result.hasPrevious = false;
            return result;
        }

        // Getters and Setters
        public List<SearchHit> getHits() {
            return hits;
        }

        public void setHits(List<SearchHit> hits) {
            this.hits = hits;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        /**
         * 单条搜索命中结果
         */
        public static class SearchHit {
            private Long messageId;
            private Long conversationId;
            private Integer conversationType;
            private Long senderId;
            private String senderNickname;
            private Integer messageType;
            private String content;
            private String fullContent;
            private String fileName;
            private LocalDateTime messageTime;

            // Getters and Setters
            public Long getMessageId() {
                return messageId;
            }

            public void setMessageId(Long messageId) {
                this.messageId = messageId;
            }

            public Long getConversationId() {
                return conversationId;
            }

            public void setConversationId(Long conversationId) {
                this.conversationId = conversationId;
            }

            public Integer getConversationType() {
                return conversationType;
            }

            public void setConversationType(Integer conversationType) {
                this.conversationType = conversationType;
            }

            public Long getSenderId() {
                return senderId;
            }

            public void setSenderId(Long senderId) {
                this.senderId = senderId;
            }

            public String getSenderNickname() {
                return senderNickname;
            }

            public void setSenderNickname(String senderNickname) {
                this.senderNickname = senderNickname;
            }

            public Integer getMessageType() {
                return messageType;
            }

            public void setMessageType(Integer messageType) {
                this.messageType = messageType;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getFullContent() {
                return fullContent;
            }

            public void setFullContent(String fullContent) {
                this.fullContent = fullContent;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public LocalDateTime getMessageTime() {
                return messageTime;
            }

            public void setMessageTime(LocalDateTime messageTime) {
                this.messageTime = messageTime;
            }
        }
    }
}
