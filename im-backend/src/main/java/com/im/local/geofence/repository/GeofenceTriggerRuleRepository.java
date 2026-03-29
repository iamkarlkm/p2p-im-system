package com.im.local.geofence.repository;

import com.im.local.geofence.entity.GeofenceTriggerRule;
import com.im.local.geofence.enums.GeofenceEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地理围栏触发规则Repository
 */
@Repository
public interface GeofenceTriggerRuleRepository extends JpaRepository<GeofenceTriggerRule, Long> {
    
    List<GeofenceTriggerRule> findByGeofenceId(Long geofenceId);
    
    List<GeofenceTriggerRule> findByGeofenceIdAndEventType(Long geofenceId, GeofenceEventType eventType);
    
    List<GeofenceTriggerRule> findByActive(Boolean active);
}
