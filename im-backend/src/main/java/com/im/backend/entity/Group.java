package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群组实体类
 * 功能 #5: 群组管理基础模块 - 群组创建/解散/信息管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_group")
public class Group {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群组唯一标识
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 群组名称
     */
    @TableField("name")
    private String name;

    /**
     * 群组描述
     */
    @TableField("description")
    private String description;

    /**
     * 群组头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 群主用户ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 群组类型: 0-普通群 1-付费群 2-企业群
     */
    @TableField("group_type")
    private Integer groupType;

    /**
     * 成员数量
     */
    @TableField("member_count")
    private Integer memberCount;

    /**
     * 最大成员数
     */
    @TableField("max_member_count")
    private Integer maxMemberCount;

    /**
     * 入群验证方式: 0-无需验证 1-需要验证 2-禁止加入
     */
    @TableField("join_type")
    private Integer joinType;

    /**
     * 是否全员禁言: 0-否 1-是
     */
    @TableField("all_muted")
    private Integer allMuted;

    /**
     * 群组状态: 0-正常 1-解散 2-封禁
     */
    @TableField("status")
    private Integer status;

    /**
     * 群组公告
     */
    @TableField("announcement")
    private String announcement;

    /**
     * 扩展字段(JSON格式)
     */
    @TableField("extra")
    private String extra;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 群组成员列表(非数据库字段)
     */
    @TableField(exist = false)
    private List<GroupMember> members;

    /**
     * 群主信息(非数据库字段)
     */
    @TableField(exist = false)
    private User owner;

    /**
     * 获取群组类型描述
     */
    public String getGroupTypeDesc() {
        switch (groupType) {
            case 0: return "普通群";
            case 1: return "付费群";
            case 2: return "企业群";
            default: return "未知";
        }
    }

    /**
     * 获取入群验证方式描述
     */
    public String getJoinTypeDesc() {
        switch (joinType) {
            case 0: return "无需验证";
            case 1: return "需要验证";
            case 2: return "禁止加入";
            default: return "未知";
        }
    }

    /**
     * 检查是否已满员
     */
    public boolean isFull() {
        return memberCount != null && maxMemberCount != null 
            && memberCount >= maxMemberCount;
    }

    /**
     * 检查当前用户是否为群主
     */
    public boolean isOwner(Long userId) {
        return ownerId != null && ownerId.equals(userId);
    }

    /**
     * 增加成员数
     */
    public void incrementMemberCount() {
        if (this.memberCount == null) {
            this.memberCount = 0;
        }
        this.memberCount++;
    }

    /**
     * 减少成员数
     */
    public void decrementMemberCount() {
        if (this.memberCount != null && this.memberCount > 0) {
            this.memberCount--;
        }
    }
}
