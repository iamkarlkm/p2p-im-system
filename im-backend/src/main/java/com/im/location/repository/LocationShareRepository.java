package com.im.location.repository;

import com.im.location.entity.LocationShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationShareRepository extends JpaRepository<LocationShareEntity, UUID> {
    
    // Basic queries
    List<LocationShareEntity> findByUserId(UUID userId);
    
    List<LocationShareEntity> findByRecipientId(UUID recipientId);
    
    List<LocationShareEntity> findByUserIdAndIsActive(UUID userId, Boolean isActive);
    
    List<LocationShareEntity> findByRecipientIdAndIsActive(UUID recipientId, Boolean isActive);
    
    List<LocationShareEntity> findByConversationId(UUID conversationId);
    
    List<LocationShareEntity> findByConversationIdAndIsActive(UUID conversationId, Boolean isActive);
    
    Optional<LocationShareEntity> findByUserIdAndRecipientIdAndIsActive(UUID userId, UUID recipientId, Boolean isActive);
    
    // Complex queries
    @Query("SELECT l FROM LocationShareEntity l WHERE l.userId = :userId AND l.isActive = true AND l.expiresAt > :now")
    List<LocationShareEntity> findActiveSharesByUser(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.recipientId = :recipientId AND l.isActive = true AND l.expiresAt > :now")
    List<LocationShareEntity> findActiveSharesForRecipient(@Param("recipientId") UUID recipientId, @Param("now") LocalDateTime now);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.isActive = true AND l.shareType = :type")
    List<LocationShareEntity> findActiveSharesByType(@Param("type") String type);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.userId = :userId AND l.isActive = true AND l.shareType = 'REALTIME'")
    List<LocationShareEntity> findRealtimeSharesByUser(@Param("userId") UUID userId);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.userId = :userId AND l.isActive = true AND l.expiresAt <= :threshold")
    List<LocationShareEntity> findExpiringShares(@Param("userId") UUID userId, @Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.isActive = true AND l.lastUpdateAt < :threshold")
    List<LocationShareEntity> findStaleShares(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT COUNT(l) FROM LocationShareEntity l WHERE l.userId = :userId AND l.isActive = true")
    Long countActiveSharesByUser(@Param("userId") UUID userId);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.userId = :userId AND l.recipientId = :recipientId ORDER BY l.createdAt DESC")
    List<LocationShareEntity> findShareHistory(@Param("userId") UUID userId, @Param("recipientId") UUID recipientId);
    
    @Query("SELECT DISTINCT l.recipientId FROM LocationShareEntity l WHERE l.userId = :userId AND l.isActive = true")
    List<UUID> findActiveRecipients(@Param("userId") UUID userId);
    
    @Query("SELECT DISTINCT l.userId FROM LocationShareEntity l WHERE l.recipientId = :recipientId AND l.isActive = true")
    List<UUID> findActiveSharers(@Param("recipientId") UUID recipientId);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.isActive = true AND l.privacyLevel = :level")
    List<LocationShareEntity> findSharesByPrivacyLevel(@Param("level") String level);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.userId = :userId AND l.isActive = true AND l.backgroundTracking = true")
    List<LocationShareEntity> findBackgroundTrackingShares(@Param("userId") UUID userId);
    
    @Query("SELECT l FROM LocationShareEntity l WHERE l.isActive = true AND l.notificationEnabled = true")
    List<LocationShareEntity> findSharesWithNotifications();
    
    // Update queries
    @Query("UPDATE LocationShareEntity l SET l.isActive = false, l.endedAt = :now WHERE l.userId = :userId")
    int deactivateAllUserShares(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("UPDATE LocationShareEntity l SET l.isActive = false, l.endedAt = :now WHERE l.expiresAt < :now AND l.isActive = true")
    int deactivateExpiredShares(@Param("now") LocalDateTime now);
    
    @Query("UPDATE LocationShareEntity l SET l.latitude = :lat, l.longitude = :lon, l.lastUpdateAt = :now WHERE l.id = :id")
    int updateLocation(@Param("id") UUID id, @Param("lat") Double lat, @Param("lon") Double lon, @Param("now") LocalDateTime now);
    
    // Delete queries
    void deleteByUserId(UUID userId);
    
    void deleteByRecipientId(UUID recipientId);
    
    @Query("DELETE FROM LocationShareEntity l WHERE l.isActive = false AND l.endedAt < :threshold")
    int deleteOldInactiveShares(@Param("threshold") LocalDateTime threshold);
}