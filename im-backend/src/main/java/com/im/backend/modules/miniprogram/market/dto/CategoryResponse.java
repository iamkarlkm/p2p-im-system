package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序分类响应DTO
 */
@Data
public class CategoryResponse {

    private Long id;
    private String categoryCode;
    private String categoryName;
    private String parentCode;
    private String parentName;
    private Integer level;
    private String iconUrl;
    private String description;
    private Integer sceneType;
    private Integer sortWeight;
    private Integer appCount;
    private LocalDateTime createTime;
}
