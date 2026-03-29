package com.im.backend.modules.local.search.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 自然语言理解请求DTO
 */
@Data
public class NluParseRequest {

    /**
     * 自然语言查询
     */
    @NotBlank(message = "查询文本不能为空")
    private String query;

    /**
     * 上下文信息JSON
     */
    private String context;

    /**
     * 用户位置纬度
     */
    private Double lat;

    /**
     * 用户位置经度
     */
    private Double lng;
}
