package com.im.backend.modules.merchant.assistant.service;

import com.im.backend.modules.merchant.assistant.dto.CreateTemplateRequest;
import com.im.backend.modules.merchant.assistant.dto.MarketingRuleResponse;
import com.im.backend.modules.merchant.assistant.entity.MessageTemplate;

import java.util.List;
import java.util.Map;

/**
 * 消息模板服务接口
 */
public interface IMessageTemplateService {
    
    /**
     * 创建消息模板
     */
    MessageTemplate createTemplate(CreateTemplateRequest request);
    
    /**
     * 更新消息模板
     */
    void updateTemplate(Long templateId, CreateTemplateRequest request);
    
    /**
     * 删除消息模板
     */
    void deleteTemplate(Long templateId);
    
    /**
     * 获取商户的模板列表
     */
    List<MessageTemplate> getMerchantTemplates(Long merchantId);
    
    /**
     * 根据类型获取模板
     */
    List<MessageTemplate> getTemplatesByType(Long merchantId, String templateType);
    
    /**
     * 渲染模板
     */
    String renderTemplate(Long templateId, Map<String, Object> variables);
    
    /**
     * 渲染模板内容
     */
    String renderTemplateContent(String templateContent, Map<String, Object> variables);
}
