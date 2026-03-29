package com.im.backend.modules.local.life.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * POI引导点DTO
 * POI Guidance Point DTO
 */
@Data
public class PoiGuidanceDTO {

    /**
     * 引导点ID
     */
    private Long id;

    /**
     * POI ID
     */
    private Long poiId;

    /**
     * 引导点类型
     */
    private String guidanceType;

    /**
     * 引导点类型文本
     */
    private String guidanceTypeText;

    /**
     * 引导点名称
     */
    private String name;

    /**
     * 经度
     */
    private BigDecimal lng;

    /**
     * 纬度
     */
    private BigDecimal lat;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 楼层名称
     */
    private String floorName;

    /**
     * 地址描述
     */
    private String address;

    /**
     * 引导提示语
     */
    private String guidanceTips;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 是否主入口
     */
    private Boolean isMain;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 停车场信息
     */
    private ParkingInfoDTO parkingInfo;

    /**
     * 距离当前位置（米）
     */
    private Integer distance;

    @Data
    public static class ParkingInfoDTO {
        private Integer available;
        private Integer total;
        private String fee;
        private String status;
    }
}
