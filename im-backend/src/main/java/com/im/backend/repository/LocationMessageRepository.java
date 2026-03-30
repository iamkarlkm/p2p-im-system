package com.im.backend.repository;

import com.im.backend.entity.LocationMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 位置消息数据访问层
 * 功能#26: 位置消息
 */
@Repository
public interface LocationMessageRepository extends JpaRepository<LocationMessage, Long> {
    
    Optional<LocationMessage> findByMessageId(String messageId);
    
    @Query("SELECT lm FROM LocationMessage lm WHERE lm.senderId = :userId OR lm.receiverId = :userId ORDER BY lm.createdAt DESC")
    Page<LocationMessage> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT lm FROM LocationMessage lm WHERE (lm.senderId = :userId1 AND lm.receiverId = :userId2) OR (lm.senderId = :userId2 AND lm.receiverId = :userId1) ORDER BY lm.createdAt DESC")
    Page<LocationMessage> findByConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);
    
    @Query("SELECT lm FROM LocationMessage lm WHERE lm.groupId = :groupId ORDER BY lm.createdAt DESC")
    Page<LocationMessage> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);
    
    @Query(value = "SELECT * FROM location_messages lm WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(lm.latitude)) * cos(radians(lm.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(lm.latitude)))) < :radiusKm ORDER BY lm.created_at DESC", nativeQuery = true)
    List<LocationMessage> findNearby(@Param("lat") BigDecimal lat, @Param("lon") BigDecimal lon, @Param("radiusKm") double radiusKm);
    
    @Modifying
    @Query("UPDATE LocationMessage lm SET lm.isRead = true, lm.readTime = :readTime WHERE lm.id = :id")
    void markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);
    
    @Query("SELECT COUNT(lm) FROM LocationMessage lm WHERE lm.receiverId = :userId AND lm.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
}
