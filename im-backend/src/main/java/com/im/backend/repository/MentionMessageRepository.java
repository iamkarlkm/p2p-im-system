package com.im.backend.repository;

import com.im.backend.entity.MentionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @提醒消息数据访问层
 * 功能#28: 消息@提醒
 */
@Repository
public interface MentionMessageRepository extends JpaRepository<MentionMessage, Long> {
    
    Optional<MentionMessage> findByMessageId(String messageId);
    
    @Query("SELECT mm FROM MentionMessage mm WHERE mm.mentionedUserId = :userId ORDER BY mm.createdAt DESC")
    Page<MentionMessage> findByMentionedUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT mm FROM MentionMessage mm WHERE mm.mentionedUserId = :userId AND mm.isRead = false ORDER BY mm.createdAt DESC")
    List<MentionMessage> findUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT mm FROM MentionMessage mm WHERE mm.groupId = :groupId AND mm.mentionedUserId = :userId ORDER BY mm.createdAt DESC")
    Page<MentionMessage> findByGroupAndUser(@Param("groupId") Long groupId, @Param("userId") Long userId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE MentionMessage mm SET mm.isRead = true, mm.readTime = :readTime WHERE mm.id = :id")
    void markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);
    
    @Query("SELECT COUNT(mm) FROM MentionMessage mm WHERE mm.mentionedUserId = :userId AND mm.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT mm FROM MentionMessage mm WHERE mm.originalMessageId = :originalMessageId")
    List<MentionMessage> findByOriginalMessageId(@Param("originalMessageId") String originalMessageId);
}
