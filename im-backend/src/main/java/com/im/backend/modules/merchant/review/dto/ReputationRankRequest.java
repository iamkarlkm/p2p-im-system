package com.im.backend.modules.merchant.review.dto;

import lombok.Data;

/**
 * 口碑榜单查询请求DTO
 */
@Data
public class ReputationRankRequest {

    private String rankType; // overall/taste/service/environment/value/hot/rising/new
    private Long categoryId;
    private Long areaId;
    private Integer page = 1;
    private Integer size = 20;
}
