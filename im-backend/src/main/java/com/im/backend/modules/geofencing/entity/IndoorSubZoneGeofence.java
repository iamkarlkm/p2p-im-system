package com.im.backend.modules.geofencing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 店内子区域围栏实体类
 * 支持商场楼层、店铺内区域、座位等精细化管理
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("indoor_sub_zone_geofence")
public class IndoorSubZoneGeofence {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 区域名称 */
    private String name;
    
    /** 区域类型: FLOOR-楼层, AREA-区域, SEAT-座位, COUNTER-柜台, ROOM-包间 */
    private String zoneType;
    
    /** 父围栏ID（关联主围栏） */
    private Long parentGeofenceId;
    
    /** 关联商户ID */
    private Long merchantId;
    
    /** 关联POI ID */
    private Long poiId;
    
    /** 楼层号 */
    private Integer floorNumber;
    
    /** 楼层名称 */
    private String floorName;
    
    /** 区域编号 */
    private String zoneCode;
    
    /** 座位号/包间号 */
    private String seatNumber;
    
    /** 容纳人数 */
    private Integer capacity;
    
    /** 多边形坐标点JSON */
    private String polygonPoints;
    
    /** 地理哈希 */
    private String geoHash;
    
    /** 区域功能: DINING-就餐, SHOPPING-购物, ENTERTAINMENT-娱乐, SERVICE-服务 */
    private String functionType;
    
    /** 是否需要预约 */
    private Boolean requireReservation;
    
    /** 是否支持排队 */
    private Boolean supportQueue;
    
    /** 当前排队人数 */
    private Integer currentQueueCount;
    
    /** 预计等待时间（分钟） */
    private Integer estimatedWaitMinutes;
    
    /** 区域状态: AVAILABLE-可用, FULL-已满, MAINTENANCE-维护, RESERVED-预留 */
    private String status;
    
    /** 启用状态 */
    private Boolean enabled;
    
    /** 区域属性JSON（座位类型、环境等） */
    private String properties;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 删除标记 */
    @TableLogic
    private Boolean deleted;
}
