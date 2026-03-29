package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import java.util.List;

/**
 * 小程序推荐请求DTO
 */
@Data
public class MiniProgramRecommendRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前位置GeoHash
     */
    private String locationHash;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 场景标签（当前场景）
     */
    private String currentScene;

    /**
     * 推荐类型：1-附近推荐 2-猜你喜欢 3-热门榜单 4-新品发现
     */
    private Integer recommendType = 1;

    /**
     * 排除已使用的小程序
     */
    private Boolean excludeUsed = true;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
