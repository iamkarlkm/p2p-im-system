package com.im.backend.repository;

import com.im.backend.model.ScheduledMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时消息数据访问层
 */
@Repository
public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessage, Long> {

    /**
     * 根据发送者ID查询定时消息（分页）
     */
    Page<ScheduledMessage> findBySenderIdOrderByScheduledTimeDesc(Long senderId, Pageable pageable);

    /**
     * 根据发送者和状态查询
     */
    Page<ScheduledMessage> findBySenderIdAndStatusOrderByScheduledTimeDesc(
            Long senderId, ScheduledMessage.Status status, Pageable pageable);

    /**
     * 查询指定时间之前待发送的消息
     */
    @Query("SELECT sm FROM ScheduledMessage sm WHERE sm.status = 'PENDING' AND sm.scheduledTime <= :now")
    List<ScheduledMessage> findPendingMessagesBefore(@Param("now") LocalDateTime now);

    /**
     * 统计用户的待发送消息数量
     */
    long countBySenderIdAndStatus(Long senderId, ScheduledMessage.Status status);

    /**
     * 查询用户的所有定时消息（不分页）
     */
    List<ScheduledMessage> findBySenderIdOrderByScheduledTimeDesc(Long senderId);
}
