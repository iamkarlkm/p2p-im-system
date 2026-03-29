package com.im.ai.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 意图识别结果
 */
@Data
@Builder
public class IntentResult {
    
    /**
     * 意图类型
     */
    private IntentType intent;
    
    /**
     * 置信度(0-1)
     */
    private Double confidence;
    
    /**
     * 识别来源(rules/model/merged)
     */
    private String source;
    
    /**
     * 提取的参数
     */
    private Map<String, String> parameters;
}
