package com.im.local.geofence.entity;

import com.im.local.geofence.enums.MemberStatus;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 成员位置实体
 */
@Data
@Entity
@Builder
@Table(name = "member_location")
public class MemberLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    
    private LocalDateTime updateTime;
    private LocalDateTime arrivalTime;
}
