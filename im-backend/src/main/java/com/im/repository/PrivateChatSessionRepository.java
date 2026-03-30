package com.im.repository;

import com.im.entity.PrivateChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 单聊会话数据访问接口
 * 功能ID: #6
 */
@Repository
public interface PrivateChatSessionRepository extends JpaRepository<PrivateChatSession, String> {
    
    @Query("SELECT s FROM PrivateChatSession s WHERE s.user1Id = :userId OR s.user2Id = :userId ORDER BY s.lastMessageTime DESC")
    List<PrivateChatSession> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT s FROM PrivateChatSession s WHERE " +
           "(s.user1Id = :user1 AND s.user2Id = :user2) OR " +
           "(s.user1Id = :user2 AND s.user2Id = :user1)")
    Optional<PrivateChatSession> findByUserPair(@Param("user1") String user1, @Param("user2") String user2);
}
