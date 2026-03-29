package com.im.backend.repository;

import com.im.backend.model.MessageEditHistory;
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
 * 消息编辑历史数据访问层
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
@Repository
public interface MessageEditHistoryRepository extends JpaRepository<MessageEditHistory, Long> {

    /**
     * 根据消息ID查询所有编辑历史
     */
    List<MessageEditHistory> findByMessageIdOrderByEditSequenceAsc(Long messageId);

    /**
     * 根据消息ID分页查询编辑历史
     */
    Page<MessageEditHistory> findByMessageIdOrderByEditSequenceDesc(Long messageId, Pageable pageable);

    /**
     * 查询消息的最新编辑记录
     */
    Optional<MessageEditHistory> findTopByMessageIdOrderByEditSequenceDesc(Long messageId);

    /**
     * 统计消息的编辑次数
     */
    long countByMessageId(Long messageId);

    /**
     * 查询用户的编辑记录
     */
    List<MessageEditHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 分页查询用户的编辑记录
     */
    Page<MessageEditHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 获取消息的最新编辑序号
     */
    @Query("SELECT COALESCE(MAX(e.editSequence), 0) FROM MessageEditHistory e WHERE e.messageId = :messageId")
    Integer findLatestEditSequenceByMessageId(@Param("messageId") Long messageId);

    /**
     * 查询时间段内的编辑记录
     */
    @Query("SELECT e FROM MessageEditHistory e WHERE e.messageId = :messageId " +
           "AND e.createdAt BETWEEN :startTime AND :endTime ORDER BY e.editSequence ASC")
    List<MessageEditHistory> findByMessageIdAndTimeRange(
            @Param("messageId") Long messageId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 检查消息是否被编辑过
     */
    boolean existsByMessageId(Long messageId);

    /**
     * 批量查询多个消息的编辑次数
     */
    @Query("SELECT e.messageId, COUNT(e) FROM MessageEditHistory e WHERE e.messageId IN :messageIds GROUP BY e.messageId")
    List<Object[]> countByMessageIds(@Param("messageIds") List<Long> messageIds);

    /**
     * 查询最近的编辑记录
     */
    List<MessageEditHistory> findTop10ByOrderByCreatedAtDesc();

    /**
     * 根据编辑类型查询
     */
    List<MessageEditHistory> findByEditTypeOrderByCreatedAtDesc(MessageEditHistory.EditType editType);

    /**
     * 统计用户的总编辑次数
     */
    long countByUserId(Long userId);

    /**
     * 查询用户在时间段内的编辑次数
     */
    @Query("SELECT COUNT(e) FROM MessageEditHistory e WHERE e.userId = :userId " +
           "AND e.createdAt BETWEEN :startTime AND :endTime")
    long countByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
