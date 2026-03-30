package com.im.backend.repository;

import com.im.backend.entity.MessageReadReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息已读记录数据访问接口
 * 对应功能 #16 - 消息已读回执功能
 */
@Repository
public interface MessageReadReceiptRepository extends JpaRepository<MessageReadReceipt, Long> {
    
    /**
     * 查询消息的已读用户列表
     */
    List<MessageReadReceipt> findByMessageId(Long messageId);
    
    /**
     * 查询消息已读数量
     */
    long countByMessageId(Long messageId);
    
    /**
     * 检查用户是否已读消息
     */
    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
    
    /**
     * 查询用户在会话中已读的所有消息
     */
    @Query("SELECT mr FROM MessageReadReceipt mr WHERE mr.userId = :userId AND mr.conversationType = :type AND mr.conversationId = :conversationId")
    List<MessageReadReceipt> findByUserIdAndConversation(@Param("userId") Long userId, 
                                                          @Param("type") MessageReadReceipt.ConversationType type,
                                                          @Param("conversationId") Long conversationId);
    
    /**
     * 查询会话中某条消息之后所有已读记录
     */
    @Query("SELECT mr FROM MessageReadReceipt mr WHERE mr.conversationType = :type AND mr.conversationId = :conversationId AND mr.messageId > :messageId")
    List<MessageReadReceipt> findByConversationAfterMessage(@Param("type") MessageReadReceipt.ConversationType type,
                                                            @Param("conversationId") Long conversationId,
                                                            @Param("messageId") Long messageId);
}
