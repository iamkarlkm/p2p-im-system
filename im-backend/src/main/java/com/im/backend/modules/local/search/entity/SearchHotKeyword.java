package com.im.backend.modules.local.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.im.backend.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 搜索热词实体
 * 记录搜索热词和趋势
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("search_hot_keyword")
public class SearchHotKeyword extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 关键词分类：FOOD-美食 ENTERTAINMENT-娱乐 SHOPPING-购物
     */
    private String category;

    /**
     * 搜索次数
     */
    private Long searchCount;

    /**
     * 热度排名
     */
    private Integer hotRank;

    /**
     * 热度趋势：UP-上升 DOWN-下降 STABLE-稳定 NEW-新增
     */
    private String trend;

    /**
     * 所属区域
     */
    private String district;

    /**
     * 统计周期：DAY-日 WEEK-周 MONTH-月
     */
    private String periodType;

    /**
     * 统计日期
     */
    private String statDate;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
}
