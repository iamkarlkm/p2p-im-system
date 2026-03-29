package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息模板
 */
@Data
@TableName("message_template")
public class MessageTemplate {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID(0表示平台通用模板)
     */
    private Long merchantId;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 模板类型: WELCOME-欢迎, REMIND-提醒, MARKETING-营销, ORDER-订单
     */
    private String templateType;
    
    /**
     * 模板标题
     */
    private String title;
    
    /**
     * 模板内容(支持变量占位符如{{userName}})
     */
    private String content;
    
    /**
     * 富媒体内容JSON(图片/链接/卡片等)
     */
    private String richContent;
    
    /**
     * 变量说明JSON
     */
    private String variables;
    
    /**
     * 适用场景
     */
    private String usageScenario;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
