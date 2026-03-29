package com.im.backend.modules.local.life.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 位置更新请求DTO
 * Location Update Request DTO
 */
@Data
public class LocationUpdateRequestDTO {

    /**
     * 导航会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    /**
     * 当前经度
     */
    @NotNull(message = "经度不能为空")
    private BigDecimal lng;

    /**
     * 当前纬度
     */
    @NotNull(message = "纬度不能为空")
    private BigDecimal lat;

    /**
     * 当前速度（km/h）
     */
    private Integer speed;

    /**
     * 当前航向角度（0-360度）
     */
    private Integer heading;

    /**
     * 海拔高度（米）
     */
    private Double altitude;

    /**
     * 定位精度（米）
     */
    private Float accuracy;

    /**
     * 时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 是否模拟位置
     */
    private Boolean isSimulated;
}
