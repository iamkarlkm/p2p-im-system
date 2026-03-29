package com.im.local.geofence.dto;

import com.im.local.geofence.enums.OwnerType;
import com.im.local.geofence.enums.TargetType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建群组位置共享请求
 */
@Data
public class CreateGroupSharingRequest {
    
    @NotNull(message = "群组名称不能为空")
    private String name;
    
    @NotNull(message = "创建者ID不能为空")
    private Long creatorId;
    
    @NotNull(message = "成员ID列表不能为空")
    private List<Long> memberIds;
    
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String destinationName;
    
    private SharingLevel sharingLevel;
    private Integer durationHours;
}
