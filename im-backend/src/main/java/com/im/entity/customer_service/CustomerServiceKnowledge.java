package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服知识库实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceKnowledge {
    
    /** 知识ID */
    private Long id;
    
    /** 知识分类ID */
    private Long categoryId;
    
    /** 问题标题 */
    private String question;
    
    /** 问题答案 */
    private String answer;
    
    /** 相似问题（JSON数组） */
    private String similarQuestions;
    
    /** 关键词（逗号分隔） */
    private String keywords;
    
    /** 适用场景：1-售前 2-售中 3-售后 */
    private Integer applyScene;
    
    /** 知识类型：1-常见问题 2-政策说明 3-操作指南 4-故障处理 */
    private Integer knowledgeType;
    
    /** 状态：0-草稿 1-已发布 2-已下线 */
    private Integer status;
    
    /** 浏览次数 */
    private Integer viewCount;
    
    /** 帮助人数 */
    private Integer helpCount;
    
    /** 未帮助人数 */
    private Integer notHelpCount;
    
    /** 创建人ID */
    private Long createBy;
    
    /** 更新人ID */
    private Long updateBy;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
