package com.im.server.sequence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息序列号仓储层
 * 提供序列号的持久化和查询
 */
@Repository
public interface SequenceRepository extends JpaRepository<SequenceMetadata, Long> {

    /**
     * 根据序列号ID查找
     */
    Optional<SequenceMetadata> findBySequenceId(String sequenceId);

    /**
     * 根据发送者ID查找
     */
    List<SequenceMetadata> findBySenderId(Long senderId);

    /**
     * 根据会话ID查找
     */
    List<SequenceMetadata> findByConversationId(String conversationId);

    /**
     * 根据状态查找
     */
    List<SequenceMetadata> findByStatus(String status);

    /**
     * 根据发送者和状态查找
     */
    List<SequenceMetadata> findBySenderIdAndStatus(Long senderId, String status);

    /**
     * 统计指定状态的序列号数量
     */
    long countByStatus(String status);

    /**
     * 查找指定时间之后创建的序列号
     */
    List<SequenceMetadata> findByCreatedAtGreaterThan(Long timestamp);

    /**
     * 查找指定时间之前创建的序列号
     */
    List<SequenceMetadata> findByCreatedAtBefore(Long timestamp);

    /**
     * 删除指定时间之前创建的序列号
     */
    void deleteByCreatedAtBefore(Long timestamp);

    /**
     * 根据发送者和会话查找最新的序列号
     */
    Optional<SequenceMetadata> findTopBySenderIdAndConversationIdOrderByCreatedAtDesc(
            Long senderId, String conversationId);

    /**
     * 查找指定会话的最新N条序列号
     */
    List<SequenceMetadata> findByConversationIdOrderByCreatedAtDesc(String conversationId);

    /**
     * 统计发送者生成的序列号数量
     */
    long countBySenderId(Long senderId);

    /**
     * 统计会话中的序列号数量
     */
    long countByConversationId(String conversationId);

    /**
     * 检查序列号是否存在
     */
    boolean existsBySequenceId(String sequenceId);

    /**
     * 根据状态和发送者统计
     */
    long countByStatusAndSenderId(String status, Long senderId);

    /**
     * 批量查找序列号
     */
    List<SequenceMetadata> findBySequenceIdIn(List<String> sequenceIds);

    /**
     * 根据发送者删除
     */
    void deleteBySenderId(Long senderId);

    /**
     * 根据会话删除
     */
    void deleteByConversationId(String conversationId);

    /**
     * 更新序列号状态
     */
    void updateStatusBySequenceId(String sequenceId, String status);

    /**
     * 查找状态为指定值的序列号（分页）
     */
    @Query("SELECT s FROM SequenceMetadata s WHERE s.status = :status ORDER BY s.createdAt DESC")
    List<SequenceMetadata> findByStatusOrderByCreatedAtDesc(@Param("status") String status);

    /**
     * 查找会话中指定时间范围的序列号
     */
    @Query("SELECT s FROM SequenceMetadata s WHERE s.conversationId = :conversationId " +
            "AND s.createdAt >= :startTime AND s.createdAt <= :endTime ORDER BY s.createdAt ASC")
    List<SequenceMetadata> findByConversationIdAndTimeRange(
            @Param("conversationId") String conversationId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);

    /**
     * 查找指定发送者在时间范围内的序列号
     */
    @Query("SELECT s FROM SequenceMetadata s WHERE s.senderId = :senderId " +
            "AND s.createdAt >= :startTime AND s.createdAt <= :endTime ORDER BY s.createdAt DESC")
    List<SequenceMetadata> findBySenderIdAndTimeRange(
            @Param("senderId") Long senderId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);

    /**
     * 统计每个发送者的序列号数量
     */
    @Query("SELECT s.senderId, COUNT(s) FROM SequenceMetadata s GROUP BY s.senderId")
    List<Object[]> countBySenderIdGroupBySender();

    /**
     * 统计每个会话的序列号数量
     */
    @Query("SELECT s.conversationId, COUNT(s) FROM SequenceMetadata s GROUP BY s.conversationId")
    List<Object[]> countByConversationIdGroupByConversation();
}
