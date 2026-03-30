package com.im.repository;

import com.im.entity.ConversationType;
import com.im.entity.MessageForward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息转发记录数据访问层
 * 功能#22: 消息转发
 */
@Repository
public interface MessageForwardRepository extends JpaRepository<MessageForward, Long> {

    List<MessageForward> findByForwarderId(Long forwarderId);

    List<MessageForward> findByOriginalMessageId(Long originalMessageId);

    List<MessageForward> findByForwarderIdAndForwardTimeBetween(
            Long forwarderId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(mf) FROM MessageForward mf WHERE mf.forwarderId = ?1 AND mf.forwardTime >= ?2")
    Long countByForwarderIdAndForwardTimeAfter(Long forwarderId, LocalDateTime since);

    List<MessageForward> findByTargetConversationIdAndTargetConversationType(
            Long targetConversationId, ConversationType targetConversationType);

    @Query("SELECT mf.originalMessageId, COUNT(mf) as cnt FROM MessageForward mf " +
           "WHERE mf.originalMessageId IN ?1 GROUP BY mf.originalMessageId")
    List<Object[]> countForwardsByOriginalMessageIds(List<Long> messageIds);
}
