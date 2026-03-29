package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;

/**
 * 创建消息模板请求
 */
@Data
public class CreateTemplateRequest {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 模板类型
     */
    private String templateType;
    
    /**
     * 模板标题
     */
    private String title;
    
    /**
     * 模板内容
     */
    private String content;
    
    /**
     * 富媒体内容
     */
    private String richContent;
    
    /**
     * 变量说明
     */
    private String variables;
    
    /**
     * 适用场景
     */
    private String usageScenario;
}
