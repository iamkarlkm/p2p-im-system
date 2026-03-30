package com.im.backend.repository;

import com.im.backend.entity.MessageSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息搜索索引数据访问层
 */
@Repository
public interface MessageSearchRepository extends JpaRepository<MessageSearchIndex, Long> {

    Optional<MessageSearchIndex> findByMessageId(Long messageId);

    /**
     * 全文搜索消息内容
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "m.content LIKE %:keyword% OR " +
           "m.keywords LIKE %:keyword% " +
           "ORDER BY m.createdAt DESC")
    Page<MessageSearchIndex> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 在指定会话中搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "(m.content LIKE %:keyword% OR m.keywords LIKE %:keyword%) AND " +
           "m.conversationType = :type AND m.conversationId = :convId " +
           "ORDER BY m.createdAt DESC")
    Page<MessageSearchIndex> searchInConversation(@Param("keyword") String keyword,
                                                   @Param("type") String conversationType,
                                                   @Param("convId") Long conversationId,
                                                   Pageable pageable);

    /**
     * 按发送者搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "(m.content LIKE %:keyword% OR m.keywords LIKE %:keyword%) AND " +
           "m.senderId = :senderId " +
           "ORDER BY m.createdAt DESC")
    Page<MessageSearchIndex> searchBySender(@Param("keyword") String keyword,
                                             @Param("senderId") Long senderId,
                                             Pageable pageable);

    /**
     * 按时间范围搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "m.content LIKE %:keyword% AND " +
           "m.createdAt BETWEEN :startTime AND :endTime " +
           "ORDER BY m.createdAt DESC")
    Page<MessageSearchIndex> searchByTimeRange(@Param("keyword") String keyword,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                Pageable pageable);

    /**
     * 按内容类型搜索
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "m.contentType = :contentType AND " +
           "(m.content LIKE %:keyword% OR m.keywords LIKE %:keyword%) " +
           "ORDER BY m.createdAt DESC")
    Page<MessageSearchIndex> searchByContentType(@Param("keyword") String keyword,
                                                  @Param("contentType") String contentType,
                                                  Pageable pageable);

    /**
     * 高级搜索：组合条件
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "(:keyword IS NULL OR m.content LIKE %:keyword% OR m.keywords LIKE %:keyword%) AND " +
           "(:senderId IS NULL OR m.senderId = :senderId) AND " +
           "(:conversationType IS NULL OR m.conversationType = :conversationType) AND " +
           "(:conversationId IS NULL OR m.conversationId = :conversationId) AND " +
           "(:contentType IS NULL OR m.contentType = :contentType) AND " +
           "(:startTime IS NULL OR m.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR m.createdAt <= :endTime) " +
           "ORDER BY m.createdAt DESC")
    Page<MessageSearchIndex> advancedSearch(@Param("keyword") String keyword,
                                             @Param("senderId") Long senderId,
                                             @Param("conversationType") String conversationType,
                                             @Param("conversationId") Long conversationId,
                                             @Param("contentType") String contentType,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             Pageable pageable);

    /**
     * 获取用户的所有相关消息索引
     */
    @Query("SELECT m FROM MessageSearchIndex m WHERE " +
           "m.senderId = :userId OR " +
           "(m.conversationType = 'PRIVATE' AND m.conversationId IN :privateSessionIds) OR " +
           "(m.conversationType = 'GROUP' AND m.conversationId IN :groupIds) " +
           "ORDER BY m.createdAt DESC")
    List<MessageSearchIndex> findAllRelevantMessages(@Param("userId") Long userId,
                                                      @Param("privateSessionIds") List<Long> privateSessionIds,
                                                      @Param("groupIds") List<Long> groupIds);

    void deleteByMessageId(Long messageId);
}
