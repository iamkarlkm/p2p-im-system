package com.im.repository;

import com.im.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    /**
     * 查询用户通知列表 (分页)
     */
    Page<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 按类型查询用户通知
     */
    Page<NotificationEntity> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type, Pageable pageable);

    /**
     * 查询未读通知
     */
    Page<NotificationEntity> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 未读数量
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 按类型统计未读数量
     */
    long countByUserIdAndTypeAndIsReadFalse(Long userId, String type);

    /**
     * 标记单条通知已读
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :id AND n.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    /**
     * 批量标记已读
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.id IN :ids")
    int batchMarkAsRead(@Param("userId") Long userId, @Param("ids") List<Long> ids, @Param("readAt") LocalDateTime readAt);

    /**
     * 标记全部已读
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    /**
     * 删除过期通知
     */
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);

    /**
     * 查询特定会话相关通知
     */
    List<NotificationEntity> findByUserIdAndConversationIdOrderByCreatedAtDesc(Long userId, Long conversationId);

    /**
     * 统计各类型未读数量
     */
    @Query("SELECT n.type, COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false GROUP BY n.type")
    List<Object[]> countUnreadByType(@Param("userId") Long userId);

    /**
     * 删除用户所有通知
     */
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
