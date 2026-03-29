package com.im.backend.modules.local.life.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * POI引导点查询请求DTO
 * POI Guidance Query Request DTO
 */
@Data
public class PoiGuidanceQueryRequestDTO {

    /**
     * POI ID
     */
    @NotNull(message = "POI ID不能为空")
    private Long poiId;

    /**
     * 用户当前经度（用于计算距离排序）
     */
    private BigDecimal userLng;

    /**
     * 用户当前纬度（用于计算距离排序）
     */
    private BigDecimal userLat;

    /**
     * 引导点类型过滤
     */
    private String guidanceType;

    /**
     * 是否只查主入口
     */
    private Boolean onlyMain;
}
