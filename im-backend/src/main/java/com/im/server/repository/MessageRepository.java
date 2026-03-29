package com.im.server.repository;

import com.im.server.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息Repository
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 获取私聊消息记录
     */
    Page<Message> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
            Long fromUserId1, Long toUserId1,
            Long fromUserId2, Long toUserId2,
            Pageable pageable);
    
    /**
     * 获取群聊消息记录
     */
    Page<Message> findByChatTypeAndChatId(Integer chatType, Long chatId, Pageable pageable);
    
    /**
     * 统计未读消息数
     */
    int countByToUserIdAndStatus(Long toUserId, Integer status);
    
    /**
     * 标记消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = 4 WHERE m.toUserId = :toUserId AND m.fromUserId = :fromUserId AND m.status = 1")
    void markMessagesAsRead(@Param("toUserId") Long toUserId, @Param("fromUserId") Long fromUserId);
    
    /**
     * 根据消息ID查询消息
     */
    Message findByMsgId(String msgId);
}
