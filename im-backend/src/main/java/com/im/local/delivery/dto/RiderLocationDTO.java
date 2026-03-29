package com.im.local.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 骑手位置DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderLocationDTO {
    
    private Long riderId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime updateTime;
    private Double accuracy;
    private Double speed;
    private Double heading;
}
