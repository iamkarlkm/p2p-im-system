package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送站点实体
 * 本地物流配送智能调度引擎 - 配送站点管理
 */
@Data
@Accessors(chain = true)
@TableName("delivery_station")
public class DeliveryStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 站点编号 */
    private String stationNo;

    /** 站点名称 */
    private String name;

    /** 站点类型: FIXED-固定站点, MOBILE-移动站点, TEMP-临时站点 */
    private String type;

    /** 站点状态: ACTIVE-运营中, SUSPENDED-暂停, CLOSED-关闭 */
    private String status;

    /** 纬度 */
    private BigDecimal lat;

    /** 经度 */
    private BigDecimal lng;

    /** GeoHash */
    private String geoHash;

    /** 服务半径(米) */
    private Integer serviceRadius;

    /** 地址 */
    private String address;

    /** 联系电话 */
    private String phone;

    /** 负责人ID */
    private Long managerId;

    /** 最大骑手数 */
    private Integer maxRiderCount;

    /** 当前骑手数 */
    private Integer currentRiderCount;

    /** 服务区域边界(JSON多边形坐标) */
    private String serviceArea;

    /** 运营开始时间 */
    private String operationStartTime;

    /** 运营结束时间 */
    private String operationEndTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除 */
    @TableLogic
    private Boolean deleted;
}
