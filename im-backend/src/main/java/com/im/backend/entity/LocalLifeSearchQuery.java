package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 本地生活搜索查询实体
 * 记录用户搜索历史与搜索参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("local_life_search_query")
public class LocalLifeSearchQuery {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型: KEYWORD-关键词, POI-地点, MERCHANT-商户, COUPON-优惠券
     */
    private String searchType;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * GeoHash编码(8位精度)
     */
    private String geoHash;

    /**
     * 搜索半径(米)
     */
    private Integer radius;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 区域编码
     */
    private String districtCode;

    /**
     * POI分类代码
     */
    private String poiType;

    /**
     * 筛选条件JSON
     */
    private String filterJson;

    /**
     * 排序方式: DISTANCE-距离, RATING-评分, POPULAR-热度, SMART-智能排序
     */
    private String sortBy;

    /**
     * 返回结果数量
     */
    private Integer resultCount;

    /**
     * 响应时间(ms)
     */
    private Integer responseTime;

    /**
     * 是否点击结果
     */
    private Boolean hasClick;

    /**
     * 点击的POI ID
     */
    private Long clickedPoiId;

    /**
     * 搜索来源: MINI_APP-小程序, APP-App, H5-H5页面
     */
    private String source;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}
