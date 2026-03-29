package com.im.local.geofence.entity;

import com.im.local.geofence.enums.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 群组位置共享实体
 */
@Data
@Entity
@Builder
@Table(name = "group_location_sharing")
public class GroupLocationSharing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String groupId;
    
    private String name;
    
    @Column(nullable = false)
    private Long creatorId;
    
    @ElementCollection
    private Set<Long> memberIds;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "group_id")
    @MapKey(name = "userId")
    private Map<Long, MemberLocation> memberLocations = new HashMap<>();
    
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String destinationName;
    
    @Enumerated(EnumType.STRING)
    private SharingStatus status;
    
    @Enumerated(EnumType.STRING)
    private SharingLevel sharingLevel;
    
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private LocalDateTime endTime;
    
    @CreatedDate
    private LocalDateTime createTime;
}
