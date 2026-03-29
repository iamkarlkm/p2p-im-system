package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 智能对话助手请求DTO
 * 支持自然语言POI搜索、智能问答、多轮对话
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能对话助手请求")
public class IntelligentAssistantRequest {
    
    @NotBlank(message = "对话内容不能为空")
    @Size(max = 500, message = "对话内容不能超过500字符")
    @Schema(description = "用户输入的对话内容", example = "附近有好吃的火锅吗", required = true)
    private String query;
    
    @Schema(description = "会话ID（用于多轮对话上下文）", example = "conv_1234567890")
    private String conversationId;
    
    @Schema(description = "用户当前位置纬度", example = "31.230416")
    private Double latitude;
    
    @Schema(description = "用户当前位置经度", example = "121.473701")
    private Double longitude;
    
    @Schema(description = "搜索半径（米）", example = "5000")
    private Integer radius;
    
    @Schema(description = "搜索意图类型", example = "SEARCH")
    private String intentType;
    
    @Schema(description = "对话上下文历史")
    private List<DialogContext> contextHistory;
    
    @Schema(description = "附加参数")
    private Map<String, Object> extraParams;
    
    @Schema(description = "是否语音输入", example = "false")
    private Boolean voiceInput;
    
    @Schema(description = "方言类型", example = "mandarin")
    private String dialectType;
    
    /**
     * 对话上下文项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "对话上下文项")
    public static class DialogContext {
        @Schema(description = "角色：user/assistant", example = "user")
        private String role;
        
        @Schema(description = "对话内容", example = "附近有好吃的火锅吗")
        private String content;
        
        @Schema(description = "时间戳", example = "1711632000000")
        private Long timestamp;
        
        @Schema(description = "意图类型", example = "SEARCH")
        private String intent;
    }
}
