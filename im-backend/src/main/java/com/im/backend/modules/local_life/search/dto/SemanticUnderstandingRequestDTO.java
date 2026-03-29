package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 语义理解请求DTO
 */
@Data
public class SemanticUnderstandingRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询文本
     */
    @NotBlank(message = "查询文本不能为空")
    private String query;

    /**
     * 当前位置经度
     */
    private Double longitude;

    /**
     * 当前位置纬度
     */
    private Double latitude;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 对话上下文（JSON格式）
     */
    private String contextJson;

    /**
     * 上一意图
     */
    private String previousIntent;
}
