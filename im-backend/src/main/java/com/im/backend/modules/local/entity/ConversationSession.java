package com.im.backend.modules.local.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话会话实体
 * 存储用户与智能助手的多轮对话上下文
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_conversation_session")
public class ConversationSession {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 会话状态：active/paused/closed
     */
    private String status;

    /**
     * 会话上下文JSON（存储多轮对话历史）
     */
    private String contextJson;

    /**
     * 最后意图类型
     */
    private String lastIntent;

    /**
     * 最后搜索分类
     */
    private String lastCategory;

    /**
     * 最后搜索位置纬度
     */
    private Double lastLat;

    /**
     * 最后搜索位置经度
     */
    private Double lastLng;

    /**
     * 对话轮数
     */
    private Integer turnCount;

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

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
