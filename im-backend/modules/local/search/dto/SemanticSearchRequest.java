package com.im.backend.modules.local.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 语义搜索请求DTO
 * 支持自然语言查询理解
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "语义搜索请求")
public class SemanticSearchRequest {
    
    @NotBlank(message = "自然语言查询不能为空")
    @Schema(description = "自然语言查询", example = "我想找一个适合约会的人均200以下的西餐厅", required = true)
    private String naturalQuery;
    
    @NotNull(message = "经度不能为空")
    @Schema(description = "用户当前经度", example = "121.4737", required = true)
    private Double longitude;
    
    @NotNull(message = "纬度不能为空")
    @Schema(description = "用户当前纬度", example = "31.2304", required = true)
    private Double latitude;
    
    @Schema(description = "是否启用多轮对话", example = "false")
    @Builder.Default
    private Boolean enableMultiTurn = false;
    
    @Schema(description = "对话会话ID（多轮对话时传入）")
    private String conversationId;
    
    @Schema(description = "对话历史（多轮对话时传入）")
    private List<DialogueTurnDTO> dialogueHistory;
    
    @Schema(description = "用户画像标签")
    private List<String> userProfileTags;
    
    @Schema(description = "页码", example = "1")
    @Builder.Default
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量", example = "20")
    @Builder.Default
    private Integer pageSize = 20;
    
    // ==================== 内部DTO ====================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "对话轮次")
    public static class DialogueTurnDTO {
        @Schema(description = "轮次编号")
        private Integer turn;
        
        @Schema(description = "用户输入")
        private String userInput;
        
        @Schema(description = "系统回复")
        private String systemResponse;
        
        @Schema(description = "该轮提取的参数")
        private Map<String, Object> extractedParams;
    }
}
