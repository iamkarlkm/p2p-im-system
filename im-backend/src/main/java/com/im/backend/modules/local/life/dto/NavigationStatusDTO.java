package com.im.backend.modules.local.life.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 导航状态响应DTO
 * Navigation Status Response DTO
 */
@Data
public class NavigationStatusDTO {

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 路线ID
     */
    private Long routeId;

    /**
     * 当前位置
     */
    private CurrentLocationDTO currentLocation;

    /**
     * 当前步骤信息
     */
    private CurrentStepDTO currentStep;

    /**
     * 下一步信息
     */
    private NextStepDTO nextStep;

    /**
     * 导航状态
     */
    private String status;

    /**
     * 是否偏航
     */
    private Boolean isOffRoute;

    /**
     * 剩余距离（米）
     */
    private Integer remainingDistance;

    /**
     * 剩余距离格式化
     */
    private String remainingDistanceText;

    /**
     * 剩余时间（秒）
     */
    private Integer remainingDuration;

    /**
     * 剩余时间格式化
     */
    private String remainingDurationText;

    /**
     * 当前道路名称
     */
    private String currentRoad;

    /**
     * 语音播报文本
     */
    private String voiceText;

    /**
     * 是否需要重新规划路线
     */
    private Boolean needReroute;

    @Data
    public static class CurrentLocationDTO {
        private BigDecimal lng;
        private BigDecimal lat;
        private Integer speed;
        private Integer heading;
        private String roadName;
    }

    @Data
    public static class CurrentStepDTO {
        private Integer index;
        private String instruction;
        private Integer remainingDistance;
        private String voiceText;
        private String action;
        private String roadName;
    }

    @Data
    public static class NextStepDTO {
        private Integer index;
        private String instruction;
        private Integer distance;
        private String action;
        private String roadName;
    }
}
