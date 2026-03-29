package com.im.backend.modules.local.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 智能对话助手配置
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Component
@ConfigurationProperties(prefix = "im.local.assistant")
public class IntelligentAssistantProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 默认搜索半径（米）
     */
    private Integer defaultRadius = 5000;

    /**
     * 最大搜索半径（米）
     */
    private Integer maxRadius = 50000;

    /**
     * 默认返回结果数
     */
    private Integer defaultPageSize = 20;

    /**
     * 最大返回结果数
     */
    private Integer maxPageSize = 100;

    /**
     * 会话过期时间（分钟）
     */
    private Integer sessionExpireMinutes = 30;

    /**
     * 上下文最大轮数
     */
    private Integer maxContextTurns = 10;

    /**
     * 意图识别配置
     */
    private IntentConfig intent;

    /**
     * 语义搜索配置
     */
    private SemanticSearchConfig semanticSearch;

    /**
     * 响应模板配置
     */
    private Map<String, String> responseTemplates;

    /**
     * 意图配置
     */
    @Data
    public static class IntentConfig {
        /**
         * 最低置信度阈值
         */
        private Double minConfidence = 0.6;

        /**
         * 是否启用上下文继承
         */
        private Boolean enableContextInheritance = true;

        /**
         * 意图关键词配置
         */
        private Map<String, List<String>> keywords;
    }

    /**
     * 语义搜索配置
     */
    @Data
    public static class SemanticSearchConfig {
        /**
         * 是否启用语义理解
         */
        private Boolean enableSemantic = true;

        /**
         * 语义服务URL
         */
        private String semanticServiceUrl;

        /**
         * 是否启用同义词扩展
         */
        private Boolean enableSynonym = true;

        /**
         * 同义词词典路径
         */
        private String synonymDictPath;

        /**
         * 是否启用纠错
         */
        private Boolean enableCorrection = true;

        /**
         * 个性化权重
         */
        private Double personalizationWeight = 0.3;

        /**
         * 距离权重
         */
        private Double distanceWeight = 0.4;

        /**
         * 热度权重
         */
        private Double popularityWeight = 0.3;
    }
}
