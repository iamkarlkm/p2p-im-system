package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 搜索建议请求DTO
 */
@Data
public class SearchSuggestionRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部分输入文本
     */
    @NotBlank(message = "搜索词不能为空")
    private String keyword;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 用户经度
     */
    private Double longitude;

    /**
     * 用户纬度
     */
    private Double latitude;

    /**
     * 建议数量限制
     */
    private Integer limit = 10;
}
