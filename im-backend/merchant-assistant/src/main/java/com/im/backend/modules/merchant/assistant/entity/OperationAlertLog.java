package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 提醒记录日志
 */
@Data
@TableName("operation_alert_log")
public class OperationAlertLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 提醒配置ID
     */
    private Long alertId;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 提醒类型
     */
    private String alertType;
    
    /**
     * 提醒标题
     */
    private String alertTitle;
    
    /**
     * 提醒内容
     */
    private String alertContent;
    
    /**
     * 触发原因/数据快照
     */
    private String triggerData;
    
    /**
     * 通知渠道
     */
    private String notifyChannel;
    
    /**
     * 发送状态: PENDING-待发送, SENT-已发送, FAILED-发送失败
     */
    private String sendStatus;
    
    /**
     * 读取状态: UNREAD-未读, READ-已读
     */
    private String readStatus;
    
    /**
     * 读取时间
     */
    private LocalDateTime readTime;
    
    private LocalDateTime createTime;
}
