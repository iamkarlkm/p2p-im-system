package com.im.backend.dto.search;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * POI问答请求DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POIQADTO {
    
    /**
     * POI ID
     */
    @NotNull(message = "POI ID不能为空")
    private Long poiId;
    
    /**
     * 用户问题
     */
    @NotBlank(message = "问题不能为空")
    private String question;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户当前经度
     */
    private Double longitude;
    
    /**
     * 用户当前纬度
     */
    private Double latitude;
    
    /**
     * 是否为语音输入
     */
    private Boolean isVoiceInput;
    
    /**
     * 语音数据
     */
    private String voiceData;
    
    /**
     * 请求来源
     * POI_DETAIL: POI详情页
     * SEARCH_RESULT: 搜索结果页
     * MAP: 地图页
     * RECOMMENDATION: 推荐页
     */
    private String source;
    
    // ========== 响应部分 ==========
    
    /**
     * POI问答响应DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        
        /**
         * 响应状态
         */
        private String status;
        
        /**
         * 会话ID
         */
        private String sessionId;
        
        /**
         * 问题类型
         */
        private String questionType;
        
        /**
         * 系统回答
         */
        private String answer;
        
        /**
         * 详细答案（多维度信息）
         */
        private Map<String, Object> detailedAnswer;
        
        /**
         * 相关图片
         */
        private List<String> relatedImages;
        
        /**
         * 回答置信度
         */
        private Double confidence;
        
        /**
         * 是否为实时信息
         */
        private Boolean isRealTime;
        
        /**
         * 信息过期时间
         */
        private String expireTime;
        
        /**
         * 建议的快捷操作
         */
        private List<String> suggestedActions;
        
        /**
         * 相关问题推荐
         */
        private List<String> relatedQuestions;
        
        /**
         * 是否需要转人工
         */
        private Boolean needsHumanTransfer;
        
        /**
         * 转人工原因
         */
        private String transferReason;
        
        /**
         * 回答生成时间（毫秒）
         */
        private Long responseTimeMs;
    }
}
