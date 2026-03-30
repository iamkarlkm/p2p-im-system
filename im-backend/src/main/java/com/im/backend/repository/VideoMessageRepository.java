package com.im.backend.repository;

import com.im.backend.entity.ConversationType;
import com.im.backend.entity.VideoMessage;
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
 * 视频消息数据访问层
 * 功能#25: 视频消息
 */
@Repository
public interface VideoMessageRepository extends JpaRepository<VideoMessage, Long> {
    
    Optional<VideoMessage> findByMessageId(String messageId);
    
    @Query("SELECT vm FROM VideoMessage vm WHERE vm.senderId = :userId OR vm.receiverId = :userId ORDER BY vm.createdAt DESC")
    Page<VideoMessage> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT vm FROM VideoMessage vm WHERE (vm.senderId = :userId1 AND vm.receiverId = :userId2) OR (vm.senderId = :userId2 AND vm.receiverId = :userId1) ORDER BY vm.createdAt DESC")
    Page<VideoMessage> findByConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);
    
    @Query("SELECT vm FROM VideoMessage vm WHERE vm.groupId = :groupId ORDER BY vm.createdAt DESC")
    Page<VideoMessage> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);
    
    @Query("SELECT vm FROM VideoMessage vm WHERE vm.receiverId = :userId AND vm.isRead = false ORDER BY vm.createdAt DESC")
    List<VideoMessage> findUnreadByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE VideoMessage vm SET vm.isRead = true, vm.readTime = :readTime WHERE vm.id = :id")
    void markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);
    
    @Query("SELECT COUNT(vm) FROM VideoMessage vm WHERE vm.receiverId = :userId AND vm.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT vm FROM VideoMessage vm WHERE vm.senderId = :userId AND vm.createdAt >= :startTime ORDER BY vm.createdAt DESC")
    List<VideoMessage> findRecentBySender(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
}
