package com.im.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息索引实体 - 用于加速消息搜索和检索
 * 
 * 功能: 全文搜索索引、关键词索引、时间范围索引
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_message_index")
public class MessageIndex {
    
    /**
     * 索引ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 关联的消息ID
     */
    private Long messageId;
    
    /**
     * 消息全局唯一标识
     */
    private String messageUuid;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 接收者ID(会话ID)
     */
    private Long receiverId;
    
    /**
     * 会话类型
     */
    private Integer conversationType;
    
    /**
     * 分词后的关键词(JSON数组)
     */
    private String keywords;
    
    /**
     * 关键词哈希值(用于快速去重和匹配)
     */
    private Long keywordHash;
    
    /**
     * 消息内容全文(用于搜索高亮)
     */
    private String fullContent;
    
    /**
     * 消息类型
     */
    private Integer messageType;
    
    /**
     * 发送时间戳(毫秒)
     */
    private Long sendTimestamp;
    
    /**
     * 发送日期(用于按天分片)
     */
    private Integer sendDate;
    
    /**
     * 年份月份(用于按月归档)
     */
    private Integer yearMonth;
    
    /**
     * 是否包含附件
     */
    private Boolean hasAttachment;
    
    /**
     * 附件类型列表
     */
    private String attachmentTypes;
    
    /**
     * 是否被@提及
     */
    private Boolean mentioned;
    
    /**
     * 被@的用户ID
     */
    private String mentionUserIds;
    
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
    
    // ============ 索引分表策略 ============
    
    /**
     * 计算分表后缀(按月分表)
     */
    public static String calculateTableSuffix(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        return String.format("_%d%02d", year, month);
    }
    
    /**
     * 计算分表后缀(按年月)
     */
    public static String calculateTableSuffix(int yearMonth) {
        return "_" + yearMonth;
    }
}
