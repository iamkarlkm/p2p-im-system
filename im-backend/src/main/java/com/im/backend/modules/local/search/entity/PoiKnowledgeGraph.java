package com.im.backend.modules.local.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.im.backend.common.BaseEntity;

/**
 * POI知识图谱实体
 * 存储POI实体及其关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("poi_knowledge_graph")
public class PoiKnowledgeGraph extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体唯一标识
     */
    private String entityId;

    /**
     * 实体名称
     */
    private String entityName;

    /**
     * 实体类型：MERCHANT-商户 CATEGORY-分类 DISTRICT-商圈 TAG-标签
     */
    private String entityType;

    /**
     * 关联的POI ID
     */
    private Long poiId;

    /**
     * 实体属性JSON
     */
    private String entityProperties;

    /**
     * 关联实体列表JSON
     */
    private String relatedEntities;

    /**
     * 实体权重分数
     */
    private Double weightScore;

    /**
     * 实体热度分数
     */
    private Double hotScore;

    /**
     * 被搜索次数
     */
    private Long searchCount;

    /**
     * 被点击次数
     */
    private Long clickCount;
}
