package com.im.server.repository;

import com.im.server.entity.MessageSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Message Search Index Repository
 * 
 * 消息搜索索引数据访问层
 * 支持多种查询方式：
 * - 全文搜索
 * - 按会话搜索
 * - 按时间范围搜索
 * - 按发送者搜索
 * - 关键词高亮
 */
@Repository
public interface MessageSearchRepository extends JpaRepository<MessageSearchIndex, Long> {

    /**
     * 全文搜索消息
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 在指定会话中搜索消息
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.conversationId = :conversationId " +
           "AND m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchInConversation(@Param("conversationId") Long conversationId,
                                                   @Param("keyword") String keyword,
                                                   Pageable pageable);

    /**
     * 搜索私聊消息
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.conversationType = 1 " +
           "AND m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchPrivateMessages(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 搜索群聊消息
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.conversationType = 2 " +
           "AND m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchGroupMessages(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 在多个会话中搜索消息
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.conversationId IN :conversationIds " +
           "AND m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchInConversations(@Param("conversationIds") List<Long> conversationIds,
                                                    @Param("keyword") String keyword,
                                                    Pageable pageable);

    /**
     * 按时间范围搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.deleted = false " +
           "AND m.messageTime BETWEEN :startTime AND :endTime " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchByTimeRange(@Param("keyword") String keyword,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                Pageable pageable);

    /**
     * 按发送者搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.senderId = :senderId " +
           "AND m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchBySender(@Param("senderId") Long senderId,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);

    /**
     * 按消息类型搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.messageType = :messageType " +
           "AND m.deleted = false " +
           "AND (LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchByMessageType(@Param("keyword") String keyword,
                                                  @Param("messageType") Integer messageType,
                                                  Pageable pageable);

    /**
     * 组合搜索（多条件）
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.deleted = false " +
           "AND (:keyword IS NULL OR LOWER(m.plainText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:conversationId IS NULL OR m.conversationId = :conversationId) " +
           "AND (:conversationType IS NULL OR m.conversationType = :conversationType) " +
           "AND (:senderId IS NULL OR m.senderId = :senderId) " +
           "AND (:messageType IS NULL OR m.messageType = :messageType) " +
           "AND (:startTime IS NULL OR m.messageTime >= :startTime) " +
           "AND (:endTime IS NULL OR m.messageTime <= :endTime) " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> searchWithFilters(@Param("keyword") String keyword,
                                                @Param("conversationId") Long conversationId,
                                                @Param("conversationType") Integer conversationType,
                                                @Param("senderId") Long senderId,
                                                @Param("messageType") Integer messageType,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                Pageable pageable);

    /**
     * 获取用户参与的热门搜索关键词
     */
    @Query("SELECT m.keywords FROM MessageSearchIndex m WHERE m.deleted = false " +
           "AND m.messageTime >= :since GROUP BY m.keywords " +
           "ORDER BY COUNT(m.id) DESC")
    List<String> findHotKeywords(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * 查找提及某用户的消息
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE m.mentionedUsers LIKE CONCAT('%', :userId, '%') " +
           "AND m.deleted = false " +
           "ORDER BY m.messageTime DESC")
    Page<MessageSearchIndex> findMentions(@Param("userId") Long userId, Pageable pageable);

    /**
     * 删除消息索引（软删除）
     */
    void deleteByMessageId(Long messageId);

    /**
     * 检查索引是否存在
     */
    boolean existsByMessageId(Long messageId);
}
