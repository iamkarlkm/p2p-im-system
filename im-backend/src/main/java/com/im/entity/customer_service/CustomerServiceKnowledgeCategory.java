package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识库分类实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceKnowledgeCategory {
    
    /** 分类ID */
    private Long id;
    
    /** 父分类ID（0为根分类） */
    private Long parentId;
    
    /** 分类名称 */
    private String name;
    
    /** 分类描述 */
    private String description;
    
    /** 分类图标 */
    private String icon;
    
    /** 排序号 */
    private Integer sortOrder;
    
    /** 知识数量 */
    private Integer knowledgeCount;
    
    /** 状态：0-禁用 1-启用 */
    private Integer status;
    
    /** 创建人ID */
    private Long createBy;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
