package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 快捷回复模板实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceQuickReply {
    
    /** 模板ID */
    private Long id;
    
    /** 模板标题 */
    private String title;
    
    /** 模板内容 */
    private String content;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 使用场景：1-通用 2-售前 3-售后 4-投诉 */
    private Integer sceneType;
    
    /** 创建人ID */
    private Long createBy;
    
    /** 使用次数 */
    private Integer useCount;
    
    /** 排序号 */
    private Integer sortOrder;
    
    /** 状态：0-禁用 1-启用 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
