package com.im.local.geofence.repository;

import com.im.local.geofence.entity.GeofenceEvent;
import com.im.local.geofence.enums.GeofenceEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地理围栏事件Repository
 */
@Repository
public interface GeofenceEventRepository extends JpaRepository<GeofenceEvent, Long> {
    
    List<GeofenceEvent> findByUserIdAndGeofenceId(Long userId, Long geofenceId);
    
    List<GeofenceEvent> findByGeofenceId(Long geofenceId);
    
    List<GeofenceEvent> findByUserIdAndEventType(Long userId, GeofenceEventType eventType);
}
