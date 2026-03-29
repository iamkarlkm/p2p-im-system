package com.im.local.geofence.entity;

import com.im.local.geofence.enums.GeofenceEventType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 地理围栏事件实体
 */
@Data
@Entity
@Builder
@Table(name = "geofence_event")
public class GeofenceEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long geofenceId;
    
    @Enumerated(EnumType.STRING)
    private GeofenceEventType eventType;
    
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    
    @CreatedDate
    private LocalDateTime eventTime;
    
    private Boolean processed;
}
