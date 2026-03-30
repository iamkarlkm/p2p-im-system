package com.im.backend.repository;

import com.im.backend.entity.ContactCardMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 名片分享消息数据访问层
 * 功能#27: 名片分享
 */
@Repository
public interface ContactCardRepository extends JpaRepository<ContactCardMessage, Long> {
    
    Optional<ContactCardMessage> findByMessageId(String messageId);
    
    @Query("SELECT ccm FROM ContactCardMessage ccm WHERE ccm.senderId = :userId OR ccm.receiverId = :userId ORDER BY ccm.createdAt DESC")
    Page<ContactCardMessage> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT ccm FROM ContactCardMessage ccm WHERE (ccm.senderId = :userId1 AND ccm.receiverId = :userId2) OR (ccm.senderId = :userId2 AND ccm.receiverId = :userId1) ORDER BY ccm.createdAt DESC")
    Page<ContactCardMessage> findByConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);
    
    @Query("SELECT ccm FROM ContactCardMessage ccm WHERE ccm.groupId = :groupId ORDER BY ccm.createdAt DESC")
    Page<ContactCardMessage> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE ContactCardMessage ccm SET ccm.isRead = true, ccm.readTime = :readTime WHERE ccm.id = :id")
    void markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);
    
    @Query("SELECT COUNT(ccm) FROM ContactCardMessage ccm WHERE ccm.receiverId = :userId AND ccm.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
}
