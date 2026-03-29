package com.im.local.delivery.entity;

import com.im.local.delivery.enums.DispatchStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 派单记录实体
 */
@Data
@Entity
@Builder
@Table(name = "dispatch_record")
public class DispatchRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long orderId;
    
    @Column(nullable = false)
    private Long riderId;
    
    @Enumerated(EnumType.STRING)
    private DispatchStatus status;
    
    // 评分详情
    private Double score;
    private Double distanceScore;
    private Double routeMatchScore;
    private Double loadScore;
    private Double ratingScore;
    
    // 时间
    @CreatedDate
    private LocalDateTime createTime;
    
    private LocalDateTime expireTime;
    private LocalDateTime acceptTime;
    private LocalDateTime rejectTime;
    private String rejectReason;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
