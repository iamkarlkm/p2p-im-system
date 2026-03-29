package com.im.backend.modules.local.search.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 语义搜索请求DTO
 */
@Data
public class SemanticSearchRequest {

    /**
     * 搜索查询文本
     */
    @NotBlank(message = "搜索内容不能为空")
    private String query;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户位置纬度
     */
    private Double lat;

    /**
     * 用户位置经度
     */
    private Double lng;

    /**
     * 搜索半径(米)，默认5000
     */
    private Integer radius = 5000;

    /**
     * 会话ID，用于多轮对话
     */
    private String sessionId;

    /**
     * 是否语音搜索
     */
    private Boolean isVoice = false;

    /**
     * 排序方式：DISTANCE-距离 HOT-热度 SCORE-综合
     */
    private String sortBy = "SCORE";

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;
}
