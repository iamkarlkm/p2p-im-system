package com.im.backend.modules.local_life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 对话响应DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "对话响应")
public class ConversationResponseDTO {

    @Schema(description = "响应ID", example = "resp_12345")
    private String responseId;

    @Schema(description = "会话ID", example = "conv_abc123")
    private String sessionId;

    @Schema(description = "响应类型：ANSWER-直接回答, CLARIFICATION-需要澄清, SEARCH_RESULTS-搜索结果, NAVIGATION-导航信息, ERROR-错误")
    private String responseType;

    @Schema(description = "自然语言回复文本", example = "为您找到附近3家人均100元以下的火锅店，评分都在4.5分以上")
    private String replyText;

    @Schema(description = "语音回复URL（如启用语音输出）")
    private String voiceReplyUrl;

    @Schema(description = "搜索结果列表")
    private List<SemanticSearchResultDTO> searchResults;

    @Schema(description = "POI详情（如果是单个商户查询）")
    private PoiDetailDTO poiDetail;

    @Schema(description = "导航信息")
    private NavigationInfoDTO navigationInfo;

    @Schema(description = "相关推荐问题")
    private List<String> suggestedQuestions;

    @Schema(description = "对话轮次", example = "3")
    private Integer turnNumber;

    @Schema(description = "会话状态：ACTIVE-继续对话, ENDED-对话结束, NEEDS_CLARIFICATION-需要澄清")
    private String sessionStatus;

    @Schema(description = "当前上下文摘要")
    private Map<String, Object> contextSummary;

    @Schema(description = "响应时间", example = "2026-03-28T06:45:00")
    private LocalDateTime responseTime;

    @Schema(description = "处理耗时（毫秒）", example = "120")
    private Integer processingTimeMs;

    @Schema(description = "是否来自缓存", example = "false")
    private Boolean fromCache;

    /**
     * POI详情DTO（内部类）
     */
    @Data
    @Schema(description = "POI详情")
    public static class PoiDetailDTO {
        @Schema(description = "POI ID")
        private Long poiId;

        @Schema(description = "商户名称")
        private String name;

        @Schema(description = "地址")
        private String address;

        @Schema(description = "评分")
        private Double rating;

        @Schema(description = "人均消费")
        private Integer avgPrice;

        @Schema(description = "营业时间")
        private String businessHours;

        @Schema(description = "当前是否营业")
        private Boolean isOpen;

        @Schema(description = "联系电话")
        private String phone;

        @Schema(description = "特色标签")
        private List<String> tags;
    }

    /**
     * 导航信息DTO（内部类）
     */
    @Data
    @Schema(description = "导航信息")
    public static class NavigationInfoDTO {
        @Schema(description = "距离（米）")
        private Integer distance;

        @Schema(description = "预计时间（分钟）")
        private Integer duration;

        @Schema(description = "导航路线概要")
        private String routeSummary;

        @Schema(description = "导航链接")
        private String navigationUrl;
    }
}
