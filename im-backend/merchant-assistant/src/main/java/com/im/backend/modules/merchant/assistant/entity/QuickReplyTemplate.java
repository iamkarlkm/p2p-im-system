package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 快捷回复模板
 */
@Data
@TableName("quick_reply_template")
public class QuickReplyTemplate {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 模板标题
     */
    private String title;
    
    /**
     * 模板内容
     */
    private String content;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
