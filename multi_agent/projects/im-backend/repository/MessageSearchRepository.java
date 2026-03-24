package com.im.system.repository;

import com.im.system.entity.MessageSearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query as JpaQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息全文搜索仓储接口
 * 支持 Elasticsearch 和 PostgreSQL 双存储
 */
@Repository
public interface MessageSearchRepository extends 
        ElasticsearchRepository<MessageSearchEntity, Long>,
        JpaRepository<MessageSearchEntity, Long> {
    
    // ==================== Elasticsearch 全文搜索方法 ====================
    
    /**
     * 全文搜索 - 基础搜索
     */
    Page<MessageSearchEntity> findByContentContaining(String keyword, Pageable pageable);
    
    /**
     * 全文搜索 - 会话内搜索
     */
    Page<MessageSearchEntity> findBySessionIdAndContentContaining(String sessionId, String keyword, Pageable pageable);
    
    /**
     * 全文搜索 - 发送者消息搜索
     */
    Page<MessageSearchEntity> findBySenderIdAndContentContaining(String senderId, String keyword, Pageable pageable);
    
    /**
     * 全文搜索 - 多字段搜索
     */
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"content^2\", \"plain_content\", \"sender_name\", \"tags\", \"keywords\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}]}}")
    Page<MessageSearchEntity> fullTextSearch(String query, Pageable pageable);
    
    /**
     * 高级全文搜索 - 带过滤器
     */
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"content^2\", \"plain_content\", \"sender_name\", \"tags\", \"keywords\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}], \"filter\": [{\"term\": {\"session_id\": \"?1\"}}, {\"range\": {\"created_at\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}]}}")
    Page<MessageSearchEntity> advancedSearch(String query, String sessionId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 短语搜索
     */
    @Query("{\"match_phrase\": {\"content\": {\"query\": \"?0\", \"slop\": 2}}}")
    Page<MessageSearchEntity> phraseSearch(String phrase, Pageable pageable);
    
    /**
     * 通配符搜索
     */
    @Query("{\"wildcard\": {\"content\": {\"value\": \"*?0*\"}}}")
    Page<MessageSearchEntity> wildcardSearch(String pattern, Pageable pageable);
    
    /**
     * 正则表达式搜索
     */
    @Query("{\"regexp\": {\"content\": {\"value\": \"?0\"}}}")
    Page<MessageSearchEntity> regexSearch(String regex, Pageable pageable);
    
    /**
     * 多关键词搜索 (AND 逻辑)
     */
    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}, {\"match\": {\"content\": \"?1\"}}]}}")
    Page<MessageSearchEntity> multiKeywordAndSearch(String keyword1, String keyword2, Pageable pageable);
    
    /**
     * 多关键词搜索 (OR 逻辑)
     */
    @Query("{\"bool\": {\"should\": [{\"match\": {\"content\": \"?0\"}}, {\"match\": {\"content\": \"?1\"}}], \"minimum_should_match\": 1}}")
    Page<MessageSearchEntity> multiKeywordOrSearch(String keyword1, String keyword2, Pageable pageable);
    
    /**
     * 排除特定关键词搜索
     */
    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}], \"must_not\": [{\"match\": {\"content\": \"?1\"}}]}}")
    Page<MessageSearchEntity> excludeKeywordSearch(String include, String exclude, Pageable pageable);
    
    /**
     * 带权重搜索
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"content^3\", \"plain_content^2\", \"sender_name^1\", \"tags^2\", \"keywords^2\"], \"type\": \"best_fields\", \"fuzziness\": \"AUTO\"}}")
    Page<MessageSearchEntity> weightedSearch(String query, Pageable pageable);
    
    /**
     * 模糊搜索
     */
    @Query("{\"match\": {\"content\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\", \"prefix_length\": 1}}}")
    Page<MessageSearchEntity> fuzzySearch(String query, Pageable pageable);
    
    // ==================== JPA 数据库查询方法 ====================
    
    /**
     * 根据消息ID查找
     */
    Optional<MessageSearchEntity> findByMessageId(String messageId);
    
    /**
     * 根据会话ID查找消息
     */
    List<MessageSearchEntity> findBySessionId(String sessionId);
    
    /**
     * 根据会话ID分页查找
     */
    Page<MessageSearchEntity> findBySessionId(String sessionId, Pageable pageable);
    
    /**
     * 根据发送者ID查找消息
     */
    List<MessageSearchEntity> findBySenderId(String senderId);
    
    /**
     * 根据发送者ID分页查找
     */
    Page<MessageSearchEntity> findBySenderId(String senderId, Pageable pageable);
    
    /**
     * 根据消息类型查找
     */
    List<MessageSearchEntity> findByMessageType(String messageType);
    
    /**
     * 查找有附件的消息
     */
    List<MessageSearchEntity> findByHasAttachmentTrue();
    
    /**
     * 查找加密消息
     */
    List<MessageSearchEntity> findByIsEncryptedTrue();
    
    /**
     * 查找已删除消息
     */
    List<MessageSearchEntity> findByIsDeletedTrue();
    
    /**
     * 查找已编辑消息
     */
    List<MessageSearchEntity> findByIsEditedTrue();
    
    /**
     * 根据时间范围查找
     */
    List<MessageSearchEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据会话ID和时间范围查找
     */
    List<MessageSearchEntity> findBySessionIdAndCreatedAtBetween(String sessionId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据发送者ID和时间范围查找
     */
    List<MessageSearchEntity> findBySenderIdAndCreatedAtBetween(String senderId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据标签查找
     */
    List<MessageSearchEntity> findByTagsContaining(String tag);
    
    /**
     * 根据关键词查找
     */
    List<MessageSearchEntity> findByKeywordsContaining(String keyword);
    
    /**
     * 根据语言查找
     */
    List<MessageSearchEntity> findByLanguage(String language);
    
    /**
     * 查找高优先级消息
     */
    List<MessageSearchEntity> findByPriorityGreaterThanEqual(Integer minPriority);
    
    /**
     * 查找高互动消息 (反应+回复数)
     */
    @JpaQuery("SELECT m FROM MessageSearchEntity m WHERE (m.reactionCount + m.replyCount) >= :minInteraction")
    List<MessageSearchEntity> findHighInteractionMessages(@Param("minInteraction") Integer minInteraction);
    
    /**
     * 查找热门消息 (阅读数)
     */
    List<MessageSearchEntity> findByReadCountGreaterThanEqual(Integer minReadCount);
    
    /**
     * 复合查询：会话内+发送者+时间范围
     */
    List<MessageSearchEntity> findBySessionIdAndSenderIdAndCreatedAtBetween(
            String sessionId, String senderId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 复合查询：消息类型+是否有附件
     */
    List<MessageSearchEntity> findByMessageTypeAndHasAttachment(String messageType, Boolean hasAttachment);
    
    /**
     * 统计会话消息数量
     */
    @JpaQuery("SELECT COUNT(m) FROM MessageSearchEntity m WHERE m.sessionId = :sessionId")
    Long countBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 统计发送者消息数量
     */
    @JpaQuery("SELECT COUNT(m) FROM MessageSearchEntity m WHERE m.senderId = :senderId")
    Long countBySenderId(@Param("senderId") String senderId);
    
    /**
     * 统计时间段内消息数量
     */
    @JpaQuery("SELECT COUNT(m) FROM MessageSearchEntity m WHERE m.createdAt BETWEEN :start AND :end")
    Long countByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * 统计各类型消息数量
     */
    @JpaQuery("SELECT m.messageType, COUNT(m) FROM MessageSearchEntity m GROUP BY m.messageType")
    List<Object[]> countByMessageType();
    
    /**
     * 统计各会话消息数量
     */
    @JpaQuery("SELECT m.sessionId, COUNT(m) FROM MessageSearchEntity m GROUP BY m.sessionId")
    List<Object[]> countBySession();
    
    /**
     * 统计各发送者消息数量
     */
    @JpaQuery("SELECT m.senderId, m.senderName, COUNT(m) FROM MessageSearchEntity m GROUP BY m.senderId, m.senderName")
    List<Object[]> countBySender();
    
    /**
     * 查找最新消息
     */
    List<MessageSearchEntity> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 查找会话最新消息
     */
    List<MessageSearchEntity> findTop10BySessionIdOrderByCreatedAtDesc(String sessionId);
    
    /**
     * 查找发送者最新消息
     */
    List<MessageSearchEntity> findTop10BySenderIdOrderByCreatedAtDesc(String senderId);
    
    /**
     * 根据消息ID批量查找
     */
    List<MessageSearchEntity> findByMessageIdIn(List<String> messageIds);
    
    /**
     * 根据会话ID批量查找
     */
    List<MessageSearchEntity> findBySessionIdIn(List<String> sessionIds);
    
    /**
     * 根据发送者ID批量查找
     */
    List<MessageSearchEntity> findBySenderIdIn(List<String> senderIds);
    
    /**
     * 批量更新删除状态
     */
    @Modifying
    @JpaQuery("UPDATE MessageSearchEntity m SET m.isDeleted = :deleted WHERE m.messageId IN :messageIds")
    int updateDeleteStatus(@Param("messageIds") List<String> messageIds, @Param("deleted") Boolean deleted);
    
    /**
     * 批量更新编辑状态
     */
    @Modifying
    @JpaQuery("UPDATE MessageSearchEntity m SET m.isEdited = :edited, m.editCount = m.editCount + 1 WHERE m.messageId IN :messageIds")
    int updateEditStatus(@Param("messageIds") List<String> messageIds, @Param("edited") Boolean edited);
    
    /**
     * 删除过期消息
     */
    @Modifying
    @JpaQuery("DELETE FROM MessageSearchEntity m WHERE m.expiresAt IS NOT NULL AND m.expiresAt < CURRENT_TIMESTAMP")
    int deleteExpiredMessages();
    
    /**
     * 软删除消息
     */
    @Modifying
    @JpaQuery("UPDATE MessageSearchEntity m SET m.isDeleted = true WHERE m.messageId = :messageId")
    int softDeleteByMessageId(@Param("messageId") String messageId);
    
    /**
     * 恢复软删除消息
     */
    @Modifying
    @JpaQuery("UPDATE MessageSearchEntity m SET m.isDeleted = false WHERE m.messageId = :messageId")
    int restoreByMessageId(@Param("messageId") String messageId);
    
    /**
     * 批量删除会话消息
     */
    @Modifying
    @JpaQuery("DELETE FROM MessageSearchEntity m WHERE m.sessionId = :sessionId")
    int deleteBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 批量删除发送者消息
     */
    @Modifying
    @JpaQuery("DELETE FROM MessageSearchEntity m WHERE m.senderId = :senderId")
    int deleteBySenderId(@Param("senderId") String senderId);
    
    /**
     * 清除索引数据（开发/测试用）
     */
    @Modifying
    @JpaQuery("DELETE FROM MessageSearchEntity m WHERE m.id IS NOT NULL")
    int clearAll();
    
    // ==================== 全文索引维护方法 ====================
    
    /**
     * 重建索引（Elasticsearch）
     * 注意：需要在服务层实现
     */
    
    /**
     * 刷新索引（Elasticsearch）
     */
    void refresh();
    
    /**
     * 优化索引（Elasticsearch）
     */
    void optimize();
    
    /**
     * 检查索引健康状态
     */
    @JpaQuery("SELECT COUNT(m) as total, " +
             "SUM(CASE WHEN m.isDeleted = true THEN 1 ELSE 0 END) as deleted, " +
             "SUM(CASE WHEN m.isEdited = true THEN 1 ELSE 0 END) as edited, " +
             "SUM(m.attachmentCount) as attachments, " +
             "AVG(LENGTH(m.content)) as avgLength " +
             "FROM MessageSearchEntity m")
    List<Object[]> getIndexHealthStats();
    
    /**
     * 获取索引大小统计
     */
    @JpaQuery(value = 
        "SELECT table_schema, table_name, " +
        "pg_size_pretty(pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || '\"')) as total_size, " +
        "pg_size_pretty(pg_relation_size('\"' || table_schema || '\".\"' || table_name || '\"')) as table_size, " +
        "pg_size_pretty(pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || '\"') - " +
        "pg_relation_size('\"' || table_schema || '\".\"' || table_name || '\"')) as index_size " +
        "FROM information_schema.tables " +
        "WHERE table_name = 'message_search_index'", 
        nativeQuery = true)
    List<Object[]> getIndexSizeStats();
    
    /**
     * 获取索引碎片率
     */
    @JpaQuery(value = 
        "SELECT schemaname, tablename, n_dead_tup, n_live_tup, " +
        "ROUND(n_dead_tup::numeric / (n_live_tup + n_dead_tup) * 100, 2) as dead_tuple_percent, " +
        "last_autovacuum, last_autoanalyze " +
        "FROM pg_stat_user_tables " +
        "WHERE tablename = 'message_search_index'", 
        nativeQuery = true)
    List<Object[]> getIndexFragmentationStats();
}