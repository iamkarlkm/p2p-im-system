package com.im.local.geofence.entity;

import com.im.local.geofence.enums.*;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 地理围栏触发规则实体
 */
@Data
@Entity
@Builder
@Table(name = "geofence_trigger_rule")
public class GeofenceTriggerRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long geofenceId;
    
    @Enumerated(EnumType.STRING)
    private GeofenceEventType eventType;
    
    @Enumerated(EnumType.STRING)
    private TriggerActionType actionType;
    
    @ElementCollection
    @CollectionTable(name = "geofence_rule_action_data", joinColumns = @JoinColumn(name = "rule_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, String> actionData;
    
    private Boolean active;
    
    private Integer maxTriggers;
    private Integer triggerCount;
    private LocalDateTime lastTriggerTime;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private LocalDateTime createTime;
}
