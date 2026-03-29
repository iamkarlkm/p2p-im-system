package com.im.backend.repository;

import com.im.backend.model.MessageRecallLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息撤回日志数据访问层
 */
@Repository
public interface MessageRecallRepository extends JpaRepository<MessageRecallLog, Long> {

    /**
     * 根据消息ID查询撤回历史（按时间倒序）
     */
    List<MessageRecallLog> findByMessageIdOrderByRecallTimeDesc(Long messageId);

    /**
     * 根据会话ID查询撤回历史（分页）
     */
    Page<MessageRecallLog> findByConversationIdOrderByRecallTimeDesc(Long conversationId, Pageable pageable);

    /**
     * 根据撤回者ID查询撤回历史（分页）
     */
    Page<MessageRecallLog> findByRecalledByOrderByRecallTimeDesc(Long recalledBy, Pageable pageable);

    /**
     * 根据发送者ID查询被撤回的记录
     */
    List<MessageRecallLog> findBySenderIdOrderByRecallTimeDesc(Long senderId);

    /**
     * 查询指定时间范围内的撤回记录
     */
    @Query("SELECT r FROM MessageRecallLog r WHERE r.recallTime BETWEEN :startTime AND :endTime ORDER BY r.recallTime DESC")
    List<MessageRecallLog> findByRecallTimeBetween(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 统计指定会话的撤回次数
     */
    long countByConversationId(Long conversationId);

    /**
     * 统计指定用户的撤回次数
     */
    long countByRecalledBy(Long recalledBy);

    /**
     * 统计指定时间范围内的撤回次数
     */
    @Query("SELECT COUNT(r) FROM MessageRecallLog r WHERE r.recallTime BETWEEN :startTime AND :endTime")
    long countByRecallTimeBetween(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 查询指定消息的最新撤回记录
     */
    @Query("SELECT r FROM MessageRecallLog r WHERE r.messageId = :messageId ORDER BY r.recallTime DESC")
    List<MessageRecallLog> findLatestByMessageId(@Param("messageId") Long messageId, Pageable pageable);

    /**
     * 删除指定时间之前的旧记录（清理历史数据）
     */
    @Query("DELETE FROM MessageRecallLog r WHERE r.recallTime < :beforeTime")
    void deleteByRecallTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 根据撤回类型查询记录
     */
    List<MessageRecallLog> findByRecallTypeOrderByRecallTimeDesc(String recallType);

    /**
     * 查询管理员撤回的记录（分页）
     */
    @Query("SELECT r FROM MessageRecallLog r WHERE r.recallType = 'ADMIN' ORDER BY r.recallTime DESC")
    Page<MessageRecallLog> findAdminRecalls(Pageable pageable);
}
