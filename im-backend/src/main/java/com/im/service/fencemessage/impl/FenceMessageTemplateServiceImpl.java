package com.im.service.fencemessage.impl;

import com.im.entity.fencemessage.FenceMessageTemplate;
import com.im.service.fencemessage.FenceMessageTemplateService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 围栏消息模板服务实现
 */
@Service
public class FenceMessageTemplateServiceImpl implements FenceMessageTemplateService {
    
    private final Map<String, FenceMessageTemplate> templateStore = new ConcurrentHashMap<>();
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    @Override
    public FenceMessageTemplate createTemplate(FenceMessageTemplate template) {
        template.setTemplateId(UUID.randomUUID().toString());
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setEnabled(true);
        templateStore.put(template.getTemplateId(), template);
        return template;
    }
    
    @Override
    public FenceMessageTemplate updateTemplate(String templateId, FenceMessageTemplate template) {
        template.setTemplateId(templateId);
        template.setUpdateTime(LocalDateTime.now());
        templateStore.put(templateId, template);
        return template;
    }
    
    @Override
    public void deleteTemplate(String templateId) {
        templateStore.remove(templateId);
    }
    
    @Override
    public FenceMessageTemplate getTemplate(String templateId) {
        return templateStore.get(templateId);
    }
    
    @Override
    public List<FenceMessageTemplate> getAllTemplates() {
        return new ArrayList<>(templateStore.values());
    }
    
    @Override
    public List<FenceMessageTemplate> getTemplatesByType(String templateType) {
        return templateStore.values().stream()
                .filter(t -> templateType.equals(t.getTemplateType()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FenceMessageTemplate> getTemplatesByScene(String triggerScene) {
        return templateStore.values().stream()
                .filter(t -> triggerScene.equals(t.getTriggerScene()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void enableTemplate(String templateId) {
        FenceMessageTemplate template = templateStore.get(templateId);
        if (template != null) {
            template.setEnabled(true);
            template.setUpdateTime(LocalDateTime.now());
        }
    }
    
    @Override
    public void disableTemplate(String templateId) {
        FenceMessageTemplate template = templateStore.get(templateId);
        if (template != null) {
            template.setEnabled(false);
            template.setUpdateTime(LocalDateTime.now());
        }
    }
    
    @Override
    public String renderMessage(String template, Map<String, String> variables) {
        if (template == null) return "";
        
        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            String varValue = variables.getOrDefault(varName, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(varValue));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
}
