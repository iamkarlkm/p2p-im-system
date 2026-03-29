package com.im.backend.modules.local.search.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 搜索建议请求DTO
 */
@Data
public class SearchSuggestionRequest {

    /**
     * 用户输入的关键词前缀
     */
    @NotBlank(message = "关键词不能为空")
    private String keyword;

    /**
     * 用户位置纬度
     */
    private Double lat;

    /**
     * 用户位置经度
     */
    private Double lng;

    /**
     * 返回数量限制，默认10
     */
    private Integer limit = 10;
}
