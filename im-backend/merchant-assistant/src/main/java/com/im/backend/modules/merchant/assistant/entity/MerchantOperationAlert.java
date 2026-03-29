package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商户运营提醒配置
 */
@Data
@TableName("merchant_operation_alert")
public class MerchantOperationAlert {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 提醒类型: ORDER_ABNORMAL-订单异常, INVENTORY_LOW-库存不足, 
     * COMPETITOR_PRICE-竞品价格, DAILY_REPORT-日报, WEEKLY_REPORT-周报
     */
    private String alertType;
    
    /**
     * 提醒名称
     */
    private String alertName;
    
    /**
     * 监控指标配置JSON
     */
    private String monitorConfig;
    
    /**
     * 触发阈值
     */
    private String thresholdConfig;
    
    /**
     * 通知方式: SMS-短信, PUSH-推送, IM-站内信, EMAIL-邮件
     */
    private String notifyChannels;
    
    /**
     * 通知对象JSON(商家负责人/运营人员)
     */
    private String notifyTargets;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 触发次数
     */
    private Integer triggerCount;
    
    /**
     * 上次触发时间
     */
    private LocalDateTime lastTriggerTime;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
