package com.im.entity.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 召回候选集实体
 * 封装从各路召回策略召回的候选推荐项
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecallCandidate {
    
    /**
     * 候选ID
     */
    private String candidateId;
    
    /**
     * 业务ID
     */
    private String businessId;
    
    /**
     * 候选类型
     */
    private String itemType;
    
    /**
     * 召回源
     * GEO: 地理位置召回
     * HOT: 热门召回
     * CF_USER: 用户协同过滤
     * CF_ITEM: 物品协同过滤
     * VECTOR: 向量召回
     * EMBEDDING: Embedding相似度
     * KNOWLEDGE: 知识图谱召回
     * SOCIAL: 社交召回
     * RULE: 规则召回
     * REALTIME: 实时行为召回
     */
    private String recallSource;
    
    /**
     * 召回通道名称
     */
    private String recallChannel;
    
    /**
     * 召回分数（原始）
     */
    private Double recallScore;
    
    /**
     * 召回排序位置
     */
    private Integer recallRank;
    
    /**
     * 召回原因
     */
    private String recallReason;
    
    /**
     * 召回特征
     */
    private Map<String, Object> recallFeatures;
    
    /**
     * 是否已去重
     */
    private Boolean isDeduplicated;
    
    /**
     * 关联的其他召回源
     */
    private Set<String> relatedSources;
    
    /**
     * 召回时间
     */
    private LocalDateTime recallTime;
    
    /**
     * 召回配置版本
     */
    private String recallConfigVersion;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> extraProperties;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
