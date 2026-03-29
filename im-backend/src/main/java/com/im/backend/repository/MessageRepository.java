package com.im.backend.repository;

import com.im.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息数据访问接口
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 查询两个用户之间的消息（分页）
     * 包含双向消息：user1发给user2 和 user2发给user1
     */
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) " +
           "AND m.type = 'PRIVATE' " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findMessagesBetweenUsers(@Param("userId1") Long userId1, 
                                           @Param("userId2") Long userId2, 
                                           Pageable pageable);

    /**
     * 查询两个用户之间的未读消息
     * 查询sender发送给receiver的未读消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "m.senderId = :senderId AND m.receiverId = :receiverId " +
           "AND m.isRead = false AND m.type = 'PRIVATE'")
    List<Message> findUnreadMessagesBetweenUsers(@Param("senderId") Long senderId, 
                                                  @Param("receiverId") Long receiverId);

    /**
     * 查询用户最近的对话列表（按最后消息时间排序）
     * 返回与该用户有过消息往来的所有用户ID和最后消息时间
     */
    @Query(value = "SELECT CASE WHEN m.sender_id = :userId THEN m.receiver_id ELSE m.sender_id END as contact_id, " +
                   "MAX(m.created_at) as last_message_time " +
                   "FROM messages m " +
                   "WHERE m.sender_id = :userId OR m.receiver_id = :userId " +
                   "GROUP BY contact_id " +
                   "ORDER BY last_message_time DESC", nativeQuery = true)
    List<Object[]> findRecentConversations(@Param("userId") Long userId);

    /**
     * 获取用户的未读消息总数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);

    /**
     * 获取指定发送者发给接收者的未读消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.isRead = false")
    long countUnreadMessagesFromSender(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
