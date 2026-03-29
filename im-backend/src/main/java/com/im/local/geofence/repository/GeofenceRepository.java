package com.im.local.geofence.repository;

import com.im.local.geofence.entity.Geofence;
import com.im.local.geofence.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地理围栏Repository
 */
@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {
    
    List<Geofence> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
    
    List<Geofence> findByTargetIdAndTargetTypeAndStatus(Long targetId, TargetType targetType, GeofenceStatus status);
    
    List<Geofence> findByStatus(GeofenceStatus status);
    
    List<Geofence> findByEndTimeBeforeAndStatus(LocalDateTime time, GeofenceStatus status);
    
    @Query(value = "SELECT * FROM geofence WHERE status = 'ACTIVE' AND " +
        "ST_DWithin(ST_MakePoint(center_longitude, center_latitude)::geography, " +
        "ST_MakePoint(:lng, :lat)::geography, :radius)", nativeQuery = true)
    List<Geofence> findNearbyGeofences(@Param("lat") double lat, @Param("lng") double lng, 
                                        @Param("radius") double radius);
}
