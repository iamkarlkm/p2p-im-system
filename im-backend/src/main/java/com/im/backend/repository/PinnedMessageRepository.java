package com.im.backend.repository;

import com.im.backend.model.PinnedMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 置顶消息数据访问层
 */
@Repository
public interface PinnedMessageRepository extends JpaRepository<PinnedMessage, Long> {
    
    /**
     * 查询会话中所有有效的置顶消息
     */
    @Query("SELECT pm FROM PinnedMessage pm WHERE pm.conversationId = :conversationId " +
           "AND pm.isActive = true AND (pm.expiresAt IS NULL OR pm.expiresAt > :now) " +
           "ORDER BY pm.pinOrder ASC, pm.createdAt DESC")
    List<PinnedMessage> findActiveByConversationId(@Param("conversationId") Long conversationId, 
                                                    @Param("now") LocalDateTime now);
    
    /**
     * 分页查询会话置顶消息
     */
    @Query("SELECT pm FROM PinnedMessage pm WHERE pm.conversationId = :conversationId " +
           "AND pm.isActive = true AND (pm.expiresAt IS NULL OR pm.expiresAt > :now) " +
           "ORDER BY pm.pinOrder ASC, pm.createdAt DESC")
    Page<PinnedMessage> findActiveByConversationIdPageable(@Param("conversationId") Long conversationId,
                                                            @Param("now") LocalDateTime now,
                                                            Pageable pageable);
    
    /**
     * 统计会话中有效置顶消息数量
     */
    @Query("SELECT COUNT(pm) FROM PinnedMessage pm WHERE pm.conversationId = :conversationId " +
           "AND pm.isActive = true AND (pm.expiresAt IS NULL OR pm.expiresAt > :now)")
    Long countActiveByConversationId(@Param("conversationId") Long conversationId,
                                      @Param("now") LocalDateTime now);
    
    /**
     * 检查消息是否已被置顶
     */
    @Query("SELECT pm FROM PinnedMessage pm WHERE pm.messageId = :messageId " +
           "AND pm.conversationId = :conversationId AND pm.isActive = true " +
           "AND (pm.expiresAt IS NULL OR pm.expiresAt > :now)")
    Optional<PinnedMessage> findActiveByMessageIdAndConversationId(@Param("messageId") Long messageId,
                                                                    @Param("conversationId") Long conversationId,
                                                                    @Param("now") LocalDateTime now);
    
    /**
     * 查询用户置顶的所有消息
     */
    @Query("SELECT pm FROM PinnedMessage pm WHERE pm.pinnedBy = :userId AND pm.isActive = true " +
           "AND (pm.expiresAt IS NULL OR pm.expiresAt > :now) ORDER BY pm.createdAt DESC")
    List<PinnedMessage> findActiveByPinnedBy(@Param("userId") Long userId, 
                                              @Param("now") LocalDateTime now);
    
    /**
     * 软删除置顶消息（设为失效）
     */
    @Modifying
    @Query("UPDATE PinnedMessage pm SET pm.isActive = false WHERE pm.id = :pinId")
    int deactivateById(@Param("pinId") Long pinId);
    
    /**
     * 根据消息ID取消置顶
     */
    @Modifying
    @Query("UPDATE PinnedMessage pm SET pm.isActive = false WHERE pm.messageId = :messageId " +
           "AND pm.conversationId = :conversationId AND pm.isActive = true")
    int deactivateByMessageId(@Param("messageId") Long messageId, 
                               @Param("conversationId") Long conversationId);
    
    /**
     * 取消会话所有置顶
     */
    @Modifying
    @Query("UPDATE PinnedMessage pm SET pm.isActive = false WHERE pm.conversationId = :conversationId " +
           "AND pm.isActive = true")
    int deactivateAllByConversationId(@Param("conversationId") Long conversationId);
    
    /**
     * 更新置顶排序
     */
    @Modifying
    @Query("UPDATE PinnedMessage pm SET pm.pinOrder = :newOrder WHERE pm.id = :pinId")
    int updatePinOrder(@Param("pinId") Long pinId, @Param("newOrder") Integer newOrder);
    
    /**
     * 查询即将过期的置顶消息
     */
    @Query("SELECT pm FROM PinnedMessage pm WHERE pm.expiresAt BETWEEN :startTime AND :endTime " +
           "AND pm.isActive = true")
    List<PinnedMessage> findExpiringPins(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 清理过期置顶
     */
    @Modifying
    @Query("UPDATE PinnedMessage pm SET pm.isActive = false WHERE pm.expiresAt < :now " +
           "AND pm.isActive = true")
    int cleanExpiredPins(@Param("now") LocalDateTime now);
    
    /**
     * 获取最大排序值
     */
    @Query("SELECT COALESCE(MAX(pm.pinOrder), 0) FROM PinnedMessage pm WHERE pm.conversationId = :conversationId " +
           "AND pm.isActive = true AND (pm.expiresAt IS NULL OR pm.expiresAt > :now)")
    Integer findMaxPinOrder(@Param("conversationId") Long conversationId, @Param("now") LocalDateTime now);
    
    /**
     * 检查用户是否有权限操作置顶
     */
    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END FROM PinnedMessage pm " +
           "WHERE pm.id = :pinId AND (pm.pinnedBy = :userId OR :isAdmin = true)")
    boolean hasPermission(@Param("pinId") Long pinId, @Param("userId") Long userId, 
                          @Param("isAdmin") boolean isAdmin);
}
