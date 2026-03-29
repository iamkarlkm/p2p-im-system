package com.im.backend.modules.local_life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 自然语言搜索请求DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "自然语言搜索请求")
public class NaturalLanguageSearchRequestDTO {

    @NotBlank(message = "搜索内容不能为空")
    @Size(max = 500, message = "搜索内容不能超过500字符")
    @Schema(description = "自然语言搜索文本，如'附近好吃的火锅'", required = true, example = "附近人均100以下的火锅店")
    private String query;

    @Schema(description = "会话ID（多轮对话时传入）", example = "conv_abc123")
    private String sessionId;

    @Schema(description = "用户当前纬度", example = "31.2304")
    private Double latitude;

    @Schema(description = "用户当前经度", example = "121.4737")
    private Double longitude;

    @Schema(description = "搜索半径（米），默认3000", example = "3000")
    private Integer radius = 3000;

    @Schema(description = "是否启用语音输入", example = "false")
    private Boolean isVoiceInput = false;

    @Schema(description = "语音语言类型", example = "zh-CN")
    private String voiceLanguage = "zh-CN";

    @Schema(description = "方言类型", example = "mandarin")
    private String dialect;

    @Schema(description = "是否优先推荐好友推荐过的商户", example = "true")
    private Boolean prioritizeSocial = false;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = 20;
}
