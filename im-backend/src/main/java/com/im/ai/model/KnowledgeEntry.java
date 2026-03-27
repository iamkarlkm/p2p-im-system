package com.im.ai.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库条目
 */
@Data
public class KnowledgeEntry {
    
    /**
     * 条目ID
     */
    private String id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签
     */
    private List<String> tags;
    
    /**
     * 条目类型
     */
    private EntryType entryType;
    
    /**
     * 优先级(0-10)
     */
    private Integer priority;
    
    /**
     * 查看次数
     */
    private Integer viewCount;
    
    /**
     * 有帮助次数
     */
    private Integer helpfulCount;
    
    /**
     * 无帮助次数
     */
    private Integer notHelpfulCount;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 相关条目ID
     */
    private List<String> relatedEntries;
    
    /**
     * 向量嵌入
     */
    private float[] embeddingVector;
    
    /**
     * 相关度分数(搜索时计算)
     */
    private transient Double relevanceScore;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
