package com.im.service.fencemessage;

import com.im.entity.fencemessage.FenceMessageTemplate;
import java.util.List;

/**
 * 围栏消息模板服务接口
 */
public interface FenceMessageTemplateService {
    
    /**
     * 创建消息模板
     */
    FenceMessageTemplate createTemplate(FenceMessageTemplate template);
    
    /**
     * 更新消息模板
     */
    FenceMessageTemplate updateTemplate(String templateId, FenceMessageTemplate template);
    
    /**
     * 删除消息模板
     */
    void deleteTemplate(String templateId);
    
    /**
     * 获取模板详情
     */
    FenceMessageTemplate getTemplate(String templateId);
    
    /**
     * 获取所有模板
     */
    List<FenceMessageTemplate> getAllTemplates();
    
    /**
     * 根据类型获取模板
     */
    List<FenceMessageTemplate> getTemplatesByType(String templateType);
    
    /**
     * 根据场景获取模板
     */
    List<FenceMessageTemplate> getTemplatesByScene(String triggerScene);
    
    /**
     * 启用模板
     */
    void enableTemplate(String templateId);
    
    /**
     * 禁用模板
     */
    void disableTemplate(String templateId);
    
    /**
     * 渲染消息内容
     */
    String renderMessage(String template, Map<String, String> variables);
}
