package com.im.location.repository;

import com.im.location.entity.GeofenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GeofenceRepository extends JpaRepository<GeofenceEntity, UUID> {
    
    List<GeofenceEntity> findByUserId(UUID userId);
    
    List<GeofenceEntity> findByUserIdAndIsActive(UUID userId, Boolean isActive);
    
    List<GeofenceEntity> findByConversationId(UUID conversationId);
    
    List<GeofenceEntity> findByTriggerType(String triggerType);
    
    @Query("SELECT g FROM GeofenceEntity g WHERE g.userId = :userId AND g.isActive = true AND g.expiresAt > :now")
    List<GeofenceEntity> findActiveGeofences(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT g FROM GeofenceEntity g WHERE g.isActive = true AND g.notifyOnEnter = true")
    List<GeofenceEntity> findEnterNotificationGeofences();
    
    @Query("SELECT g FROM GeofenceEntity g WHERE g.isActive = true AND g.notifyOnExit = true")
    List<GeofenceEntity> findExitNotificationGeofences();
    
    @Query("SELECT g FROM GeofenceEntity g WHERE g.userId = :userId ORDER BY g.priority DESC, g.createdAt DESC")
    List<GeofenceEntity> findByUserOrdered(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(g) FROM GeofenceEntity g WHERE g.userId = :userId AND g.isActive = true")
    Long countActiveGeofences(@Param("userId") UUID userId);
    
    @Query("UPDATE GeofenceEntity g SET g.triggerCount = g.triggerCount + 1, g.lastTriggeredAt = :now WHERE g.id = :id")
    int incrementTriggerCount(@Param("id") UUID id, @Param("now") LocalDateTime now);
    
    void deleteByUserId(UUID userId);
}