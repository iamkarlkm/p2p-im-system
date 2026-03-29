package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POI导航引导点实体类
 * POI Navigation Guidance Point Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_poi_guidance")
public class PoiGuidance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * POI ID
     */
    @TableField("poi_id")
    private Long poiId;

    /**
     * 引导点类型：ENTRANCE-入口, PARKING-停车场, ENTRANCE_GATE-大门, SUB_ENTRANCE-侧门, ELEVATOR-电梯, STAIRS-楼梯
     */
    @TableField("guidance_type")
    private String guidanceType;

    /**
     * 引导点名称
     */
    @TableField("name")
    private String name;

    /**
     * 经度
     */
    @TableField("lng")
    private BigDecimal lng;

    /**
     * 纬度
     */
    @TableField("lat")
    private BigDecimal lat;

    /**
     * 楼层（室内导航使用）
     */
    @TableField("floor")
    private Integer floor;

    /**
     * 楼层名称
     */
    @TableField("floor_name")
    private String floorName;

    /**
     * 室内地图ID
     */
    @TableField("indoor_map_id")
    private String indoorMapId;

    /**
     * 地址描述
     */
    @TableField("address")
    private String address;

    /**
     * 引导提示语
     */
    @TableField("guidance_tips")
    private String guidanceTips;

    /**
     * 图片URL（入口照片等）
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 是否主入口
     */
    @TableField("is_main")
    private Boolean isMain;

    /**
     * 营业时间
     */
    @TableField("business_hours")
    private String businessHours;

    /**
     * 停车场空位数量
     */
    @TableField("parking_available")
    private Integer parkingAvailable;

    /**
     * 停车场总位数
     */
    @TableField("parking_total")
    private Integer parkingTotal;

    /**
     * 停车场收费标准
     */
    @TableField("parking_fee")
    private String parkingFee;

    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
