package com.im.backend.modules.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 配送围栏实体类
 * 用于管理配送区域的电子围栏
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("delivery_fence")
public class DeliveryFence implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 围栏ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 围栏名称 */
    private String fenceName;

    /** 围栏类型: 1-配送范围 2-取货区域 3-禁区 */
    private Integer fenceType;

    /** 围栏形状: 1-圆形 2-多边形 */
    private Integer shapeType;

    /** 中心经度(圆形) */
    private Double centerLongitude;

    /** 中心纬度(圆形) */
    private Double centerLatitude;

    /** 半径(米,圆形) */
    private Integer radius;

    /** 多边形坐标点(JSON数组) */
    private String polygonPoints;

    /** 所属城市 */
    private String city;

    /** 所属区域 */
    private String district;

    /** 状态: 0-禁用 1-启用 */
    private Integer status;

    /** 描述 */
    private String description;

    /** 创建人 */
    private Long createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0-正常 1-已删除 */
    @TableLogic
    private Integer deleted;
}
