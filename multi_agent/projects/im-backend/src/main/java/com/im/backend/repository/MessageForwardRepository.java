package com.im.backend.repository;

import com.im.backend.entity.MessageForward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageForwardRepository extends JpaRepository<MessageForward, Long> {
    
    List<MessageForward> findByOriginalMessageId(Long originalMessageId);
    
    List<MessageForward> findByTargetConversationId(Long targetConversationId);
    
    List<MessageForward> findByForwardedBy(Long userId);
    
    @Query("SELECT mf FROM MessageForward mf WHERE mf.originalMessageId = :messageId AND mf.targetConversationId = :conversationId")
    List<MessageForward> findExistingForward(@Param("messageId") Long messageId, @Param("conversationId") Long conversationId);
    
    @Query("SELECT COUNT(mf) FROM MessageForward mf WHERE mf.forwardedBy = :userId")
    long countByForwardedBy(@Param("userId") Long userId);
}
