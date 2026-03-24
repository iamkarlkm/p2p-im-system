package com.im.backend.repository;

import com.im.backend.entity.ConversationFolderMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConversationFolderMembershipRepository extends JpaRepository<ConversationFolderMembership, Long> {
    List<ConversationFolderMembership> findByFolderId(Long folderId);
    List<ConversationFolderMembership> findByConversationId(Long conversationId);
    Optional<ConversationFolderMembership> findByFolderIdAndConversationId(Long folderId, Long conversationId);
    void deleteByFolderIdAndConversationId(Long folderId, Long conversationId);
}
