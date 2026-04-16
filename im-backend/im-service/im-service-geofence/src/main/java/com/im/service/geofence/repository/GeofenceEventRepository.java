package com.im.service.geofence.repository;

import com.im.service.geofence.entity.GeofenceEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地理围栏事件数据访问层
 */
@Repository
public interface GeofenceEventRepository extends JpaRepository<GeofenceEvent, Long> {

    GeofenceEvent findByEventId(String eventId);

    List<GeofenceEvent> findByGeofenceId(String geofenceId);

    List<GeofenceEvent> findByUserId(String userId);

    Page<GeofenceEvent> findByUserIdOrderByEventTimeDesc(String userId, Pageable pageable);

    List<GeofenceEvent> findByGeofenceIdAndUserId(String geofenceId, String userId);

    @Query("SELECT e FROM GeofenceEvent e WHERE e.userId = :userId AND e.eventTime >= :startTime ORDER BY e.eventTime DESC")
    List<GeofenceEvent> findRecentByUserId(@Param("userId") String userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(e) FROM GeofenceEvent e WHERE e.geofenceId = :geofenceId AND e.eventType = :eventType")
    long countByGeofenceIdAndEventType(@Param("geofenceId") String geofenceId, @Param("eventType") String eventType);

    @Query("SELECT e FROM GeofenceEvent e WHERE e.geofenceId = :geofenceId AND e.userId = :userId ORDER BY e.eventTime DESC LIMIT 1")
    GeofenceEvent findLatestByGeofenceIdAndUserId(@Param("geofenceId") String geofenceId, @Param("userId") String userId);
}
