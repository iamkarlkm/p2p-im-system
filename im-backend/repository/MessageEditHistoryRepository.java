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
 */
@Repository
public interface MessageEditHistoryRepository extends JpaRepository<MessageEditHistory, Long> {

    /**
     * 根据消息ID查询编辑历史（按版本号倒序）
     */
    List<MessageEditHistory> findByMessageIdOrderByEditVersionDesc(Long messageId);

    /**
     * 根据消息ID查询编辑历史（按时间倒序，分页）
     */
    Page<MessageEditHistory> findByMessageIdOrderByEditTimeDesc(Long messageId, Pageable pageable);

    /**
     * 根据编辑者ID查询编辑历史
     */
    Page<MessageEditHistory> findByEditedByOrderByEditTimeDesc(Long editedBy, Pageable pageable);

    /**
     * 根据消息ID和版本号查询
     */
    Optional<MessageEditHistory> findByMessageIdAndEditVersion(Long messageId, int editVersion);

    /**
     * 统计消息的编辑次数
     */
    int countByMessageId(Long messageId);

    /**
     * 查询会话中的编辑历史
     */
    @Query("SELECT h FROM MessageEditHistory h WHERE h.conversationId = :conversationId ORDER BY h.editTime DESC")
    Page<MessageEditHistory> findByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    /**
     * 查询指定时间范围内的编辑记录
     */
    @Query("SELECT h FROM MessageEditHistory h WHERE h.editTime BETWEEN :startTime AND :endTime ORDER BY h.editTime DESC")
    List<MessageEditHistory> findByEditTimeBetween(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 获取消息的最新编辑记录
     */
    @Query("SELECT h FROM MessageEditHistory h WHERE h.messageId = :messageId ORDER BY h.editVersion DESC")
    List<MessageEditHistory> findLatestByMessageId(@Param("messageId") Long messageId, Pageable pageable);

    /**
     * 删除指定时间之前的旧记录
     */
    @Query("DELETE FROM MessageEditHistory h WHERE h.editTime < :beforeTime")
    void deleteByEditTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计用户的编辑次数
     */
    long countByEditedBy(Long editedBy);

    /**
     * 统计会话的编辑次数
     */
    long countByConversationId(Long conversationId);
}
