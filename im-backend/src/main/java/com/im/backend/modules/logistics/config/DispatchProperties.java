package com.im.backend.modules.logistics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配送调度配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "delivery.dispatch")
public class DispatchProperties {

    /** 自动派单开关 */
    private boolean autoDispatchEnabled = true;

    /** 自动派单间隔(秒) */
    private int autoDispatchInterval = 30;

    /** 单次最大派单数 */
    private int maxDispatchPerBatch = 50;

    /** 骑手最大接单距离(米) */
    private int maxRiderDistance = 5000;

    /** 订单超时时间(分钟) */
    private int orderTimeoutMinutes = 30;

    /** 位置上报间隔(秒) */
    private int locationReportInterval = 10;

    /** 轨迹保存天数 */
    private int traceRetentionDays = 30;
}
