package com.im.repository;

import com.im.entity.MessageRecallEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRecallEventRepository extends JpaRepository<MessageRecallEventEntity, Long> {

    /** 查询某消息的撤回记录 */
    Optional<MessageRecallEventEntity> findByMessageId(Long messageId);

    /** 查询某会话的所有撤回事件 (分页) */
    List<MessageRecallEventEntity> findByConversationIdOrderByRecalledAtDesc(
        Long conversationId, org.springframework.data.domain.Pageable pageable);

    /** 查询某用户撤回的所有消息 */
    List<MessageRecallEventEntity> findByRecallerIdOrderByRecalledAtDesc(
        Long recallerId, org.springframework.data.domain.Pageable pageable);

    /** 查询某用户被撤回的所有消息 */
    List<MessageRecallEventEntity> findBySenderIdOrderByRecalledAtDesc(
        Long senderId, org.springframework.data.domain.Pageable pageable);

    /** 统计某会话在时间范围内的撤回数 */
    @Query("SELECT COUNT(e) FROM MessageRecallEventEntity e " +
           "WHERE e.conversationId = :convId " +
           "AND e.recalledAt BETWEEN :start AND :end")
    long countByConversationInRange(
        @Param("convId") Long conversationId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);

    /** 统计某用户的撤回数 */
    long countByRecallerId(Long recallerId);

    /** 查询最近未通知的撤回事件 (用于重试推送) */
    @Query("SELECT e FROM MessageRecallEventEntity e " +
           "WHERE e.notified = false AND e.retryCount < 3 " +
           "ORDER BY e.recalledAt ASC")
    List<MessageRecallEventEntity> findPendingNotifications();

    /** 查询某时间段内的所有撤回事件 */
    List<MessageRecallEventEntity> findByRecalledAtBetweenOrderByRecalledAtDesc(
        LocalDateTime start, LocalDateTime end);

    /** 按撤回角色统计 */
    @Query("SELECT e.recallRole, COUNT(e) FROM MessageRecallEventEntity e " +
           "WHERE e.conversationId = :convId GROUP BY e.recallRole")
    List<Object[]> countByRecallRole(@Param("convId") Long conversationId);

    /** 查询某用户最近N天内的撤回事件 */
    List<MessageRecallEventEntity> findByRecallerIdAndRecalledAtAfterOrderByRecalledAtDesc(
        Long recallerId, LocalDateTime after, org.springframework.data.domain.Pageable pageable);

    /** 删除某会话的所有撤回记录 (会话销毁时) */
    void deleteByConversationId(Long conversationId);
}
