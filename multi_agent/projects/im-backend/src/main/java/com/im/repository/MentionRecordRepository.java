package com.im.repository;

import com.im.entity.MentionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MentionRecordRepository extends JpaRepository<MentionRecordEntity, Long> {

    /** 查询某用户所有未读@提及 (按时间倒序) */
    List<MentionRecordEntity> findByMentionedUserIdAndStatusOrderByCreatedAtDesc(
        Long mentionedUserId, String status);

    /** 查询某用户所有@提及 (分页) */
    @Query("SELECT m FROM MentionRecordEntity m WHERE m.mentionedUserId = :userId " +
           "ORDER BY m.createdAt DESC")
    List<MentionRecordEntity> findByUserId(
        @Param("userId") Long userId,
        org.springframework.data.domain.Pageable pageable);

    /** 查询某会话中的所有@提及 */
    List<MentionRecordEntity> findByConversationIdAndMentionTypeNotOrderByCreatedAtAsc(
        Long conversationId, String mentionType);

    /** 批量查询消息的@记录 */
    List<MentionRecordEntity> findByMessageIdIn(List<Long> messageIds);

    /** 查询某用户在某会话中未读的@提及数量 */
    @Query("SELECT COUNT(m) FROM MentionRecordEntity m " +
           "WHERE m.mentionedUserId = :userId AND m.conversationId = :convId " +
           "AND m.status = 'UNREAD'")
    long countUnreadByUserAndConversation(
        @Param("userId") Long userId,
        @Param("convId") Long conversationId);

    /** 查询某用户所有未读的@提及总数 */
    long countByMentionedUserIdAndStatus(Long mentionedUserId, String status);

    /** 标记单条@为已读 */
    @Query("UPDATE MentionRecordEntity m SET m.status = 'READ', m.readAt = :now " +
           "WHERE m.id = :id AND m.mentionedUserId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, 
                   @Param("now") LocalDateTime now);

    /** 批量标记会话中所有@为已读 */
    @Query("UPDATE MentionRecordEntity m SET m.status = 'READ', m.readAt = :now " +
           "WHERE m.conversationId = :convId AND m.mentionedUserId = :userId " +
           "AND m.status = 'UNREAD'")
    int markAllReadInConversation(
        @Param("convId") Long conversationId,
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now);

    /** 删除某消息的所有@记录 */
    void deleteByMessageId(Long messageId);

    /** 删除某会话的所有@记录 */
    void deleteByConversationId(Long conversationId);

    /** 获取需要推送但尚未通知的@记录 */
    @Query("SELECT m FROM MentionRecordEntity m WHERE m.pushEnabled = true " +
           "AND m.notified = false AND m.status = 'UNREAD' " +
           "AND (m.expireAt IS NULL OR m.expireAt > :now)")
    List<MentionRecordEntity> findPendingNotifications(@Param("now") LocalDateTime now);

    /** 按角色ID查询@记录 (mention_type=ROLE) */
    List<MentionRecordEntity> findByMessageIdAndMentionType(Long messageId, String mentionType);

    /** 统计某用户在某时间范围内的@提及数 */
    @Query("SELECT COUNT(m) FROM MentionRecordEntity m " +
           "WHERE m.mentionedUserId = :userId " +
           "AND m.createdAt BETWEEN :start AND :end")
    long countByUserInTimeRange(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);

    /** 查询某用户最近@过谁 */
    @Query("SELECT DISTINCT m.mentionedUserId FROM MentionRecordEntity m " +
           "WHERE m.senderId = :senderId ORDER BY MAX(m.createdAt) DESC")
    List<Long> findRecentMentionedUsers(@Param("senderId") Long senderId, 
                                         org.springframework.data.domain.Pageable pageable);

    /** 获取会话中@ALL的消息 (用于群通知) */
    List<MentionRecordEntity> findByConversationIdAndMentionTypeAndStatus(
        Long conversationId, String mentionType, String status);

    /** 按用户分组统计未读@数 (仪表盘用) */
    @Query("SELECT m.conversationId, COUNT(m) FROM MentionRecordEntity m " +
           "WHERE m.mentionedUserId = :userId AND m.status = 'UNREAD' " +
           "GROUP BY m.conversationId")
    List<Object[]> countUnreadGroupByConversation(@Param("userId") Long userId);

    Optional<MentionRecordEntity> findByMessageIdAndMentionedUserId(Long messageId, Long mentionedUserId);
}
