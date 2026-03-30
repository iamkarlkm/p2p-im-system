package com.im.repository;

import com.im.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息数据访问接口
 * 功能ID: #4
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.fromUserId = :userId1 AND m.toUserId = :userId2) OR " +
           "(m.fromUserId = :userId2 AND m.toUserId = :userId1) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(@Param("userId1") String userId1,
                                    @Param("userId2") String userId2,
                                    Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.toUserId = :userId AND m.status = 0")
    List<Message> findUnreadByToUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.toUserId = :userId AND m.status = 0")
    long countUnreadByToUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.status = 1 WHERE m.fromUserId = :fromUserId AND m.toUserId = :toUserId AND m.status = 0")
    void markConversationAsRead(@Param("fromUserId") String fromUserId,
                                 @Param("toUserId") String toUserId);
}
