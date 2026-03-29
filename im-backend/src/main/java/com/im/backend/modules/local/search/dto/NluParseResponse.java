package com.im.backend.modules.local.search.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 自然语言理解响应DTO
 */
@Data
public class NluParseResponse {

    /**
     * 原始查询
     */
    private String originalQuery;

    /**
     * 标准化查询
     */
    private String normalizedQuery;

    /**
     * 搜索意图
     */
    private String intent;

    /**
     * 意图置信度 0-100
     */
    private Integer confidence;

    /**
     * 识别的实体列表
     */
    private List<ExtractedEntity> entities;

    /**
     * 提取的槽位信息
     */
    private Map<String, Object> slots;

    /**
     * 是否需要澄清
     */
    private Boolean needsClarification;

    /**
     * 澄清提示语
     */
    private String clarificationPrompt;

    /**
     * 情感倾向：POSITIVE-积极 NEGATIVE-消极 NEUTRAL-中性
     */
    private String sentiment;

    @Data
    public static class ExtractedEntity {
        /**
         * 实体文本
         */
        private String text;

        /**
         * 实体类型
         */
        private String type;

        /**
         * 在原文中的开始位置
         */
        private Integer startPos;

        /**
         * 在原文中的结束位置
         */
        private Integer endPos;

        /**
         * 标准化值
         */
        private String normalizedValue;

        /**
         * 置信度
         */
        private Double confidence;
    }
}
