package com.im.backend.modules.local.search.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 知识图谱查询响应DTO
 */
@Data
public class KnowledgeGraphResponse {

    /**
     * 中心实体ID
     */
    private String entityId;

    /**
     * 实体名称
     */
    private String entityName;

    /**
     * 实体类型
     */
    private String entityType;

    /**
     * 实体属性
     */
    private Map<String, Object> properties;

    /**
     * 关联实体列表
     */
    private List<RelatedEntity> relatedEntities;

    /**
     * 关联路径
     */
    private List<String> relationPaths;

    @Data
    public static class RelatedEntity {
        /**
         * 实体ID
         */
        private String entityId;

        /**
         * 实体名称
         */
        private String entityName;

        /**
         * 实体类型
         */
        private String entityType;

        /**
         * 关系类型
         */
        private String relationType;

        /**
         * 关系权重
         */
        private Double relationWeight;

        /**
         * 实体属性
         */
        private Map<String, Object> properties;
    }
}
