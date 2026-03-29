package com.im.backend.repository;

import com.im.backend.entity.ReadReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadReceiptRepository extends JpaRepository<ReadReceipt, Long> {

    Optional<ReadReceipt> findByMessageIdAndUserId(String messageId, Long userId);

    List<ReadReceipt> findByConversationIdAndMessageId(String conversationId, String messageId);

    List<ReadReceipt> findByUserIdAndConversationIdOrderByReadAtDesc(Long userId, String conversationId);

    List<ReadReceipt> findByConversationIdAndUserIdIn(String conversationId, List<Long> userIds);
}
