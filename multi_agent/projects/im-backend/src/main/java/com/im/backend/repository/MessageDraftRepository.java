package com.im.backend.repository;

import com.im.backend.entity.MessageDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageDraftRepository extends JpaRepository<MessageDraft, Long> {

    Optional<MessageDraft> findByUserIdAndConversationIdAndIsDeletedFalse(Long userId, String conversationId);

    List<MessageDraft> findByUserIdAndIsDeletedFalseOrderByUpdatedAtDesc(Long userId);

    List<MessageDraft> findByConversationIdAndUserIdInAndIsDeletedFalse(List<Long> userIds, String conversationId);

    void deleteByUserIdAndConversationId(Long userId, String conversationId);
}
