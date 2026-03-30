package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 搜索结果缓存实体
 * 缓存热门搜索结果提升响应速度
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("search_result_cache")
public class SearchResultCache {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 缓存键(MD5哈希)
     */
    private String cacheKey;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * GeoHash编码(6位精度区域)
     */
    private String geoHash;

    /**
     * 筛选条件哈希
     */
    private String filterHash;

    /**
     * 搜索结果JSON
     */
    private String resultJson;

    /**
     * 结果数量
     */
    private Integer resultCount;

    /**
     * 缓存类型: HOT-热门缓存, PERSONAL-个性化缓存
     */
    private String cacheType;

    /**
     * 命中次数
     */
    private Integer hitCount;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    /**
     * 检查缓存是否过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 增加命中计数
     */
    public void incrementHit() {
        this.hitCount = (this.hitCount == null ? 0 : this.hitCount) + 1;
    }
}
