package com.im.local.geofence.entity;

import com.im.local.geofence.enums.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 地理围栏实体
 */
@Data
@Entity
@Builder
@Table(name = "geofence")
@EntityListeners(AuditingEntityListener.class)
public class Geofence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private GeofenceType type;
    
    @Enumerated(EnumType.STRING)
    private GeofenceShape shape;
    
    // 圆形围栏参数
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radius; // 米
    
    // 多边形围栏参数
    @ElementCollection
    @CollectionTable(name = "geofence_coordinates", joinColumns = @JoinColumn(name = "geofence_id"))
    private List<GeoCoordinate> coordinates;
    
    // 所有者
    private Long ownerId;
    
    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;
    
    // 目标对象
    private Long targetId;
    
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    
    // 触发事件类型
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<GeofenceEventType> triggerEvents;
    
    // 停留时间阈值（分钟）
    private Integer dwellTimeMinutes;
    
    @Enumerated(EnumType.STRING)
    private GeofenceStatus status;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @CreatedDate
    private LocalDateTime createTime;
    
    @LastModifiedDate
    private LocalDateTime updateTime;
}
