package com.im.backend.repository;

import com.im.backend.entity.MessageReplyChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReplyChainRepository extends JpaRepository<MessageReplyChain, Long> {
    
    List<MessageReplyChain> findByConversationIdOrderByDepthAsc(Long conversationId);
    
    List<MessageReplyChain> findByRootMessageId(Long rootMessageId);
    
    Optional<MessageReplyChain> findByRootMessageIdAndParentMessageId(Long rootMessageId, Long parentMessageId);
    
    @Query("SELECT m FROM MessageReplyChain m WHERE m.conversationId = :convId AND m.isRoot = true ORDER BY m.createdAt DESC")
    List<MessageReplyChain> findRootChainsByConversation(@Param("convId") Long conversationId);
    
    @Query("SELECT m FROM MessageReplyChain m WHERE m.conversationId = :convId AND m.branchPath LIKE :pathPrefix%")
    List<MessageReplyChain> findBranchByPath(@Param("convId") Long conversationId, @Param("pathPrefix") String pathPrefix);
    
    @Query("SELECT m FROM MessageReplyChain m WHERE m.parentMessageId = :parentId ORDER BY m.createdAt ASC")
    List<MessageReplyChain> findChildrenByParent(@Param("parentId") Long parentMessageId);
    
    void deleteByConversationId(Long conversationId);
    
    @Query("SELECT COUNT(m) FROM MessageReplyChain m WHERE m.conversationId = :convId")
    Long countByConversationId(@Param("convId") Long conversationId);
}
