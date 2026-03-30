package com.im.backend.repository;

import com.im.backend.entity.MessageRecall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息撤回记录数据访问层
 */
@Repository
public interface MessageRecallRepository extends JpaRepository<MessageRecall, Long> {

    Optional<MessageRecall> findByMessageId(Long messageId);

    List<MessageRecall> findBySenderIdOrderByRecalledAtDesc(Long senderId);

    List<MessageRecall> findByConversationTypeAndConversationIdOrderByRecalledAtDesc(
        String conversationType, Long conversationId);

    long countBySenderIdAndRecalledAtAfter(Long senderId, LocalDateTime since);

    boolean existsByMessageId(Long messageId);
}
