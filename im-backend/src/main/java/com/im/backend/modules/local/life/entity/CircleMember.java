package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 圈子成员实体
 * 记录用户加入圈子的信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_circle_member")
public class CircleMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 圈子ID */
    private Long circleId;

    /** 用户ID */
    private Long userId;

    /** 用户昵称 */
    private String userNickname;

    /** 用户头像 */
    private String userAvatar;

    /** 成员角色: OWNER-圈主, ADMIN-管理员, MEMBER-普通成员, VIP-资深成员 */
    private String role;

    /** 成员状态: ACTIVE-正常, MUTED-禁言, KICKED-踢出, LEFT-主动退出 */
    private String status;

    /** 入圈方式: APPLY-申请加入, INVITE-被邀请 */
    private String joinType;

    /** 邀请人ID */
    private Long inviterId;

    /** 成员积分 */
    private Integer points;

    /** 成员等级 */
    private Integer level;

    /** 发帖数量 */
    private Integer postCount;

    /** 评论数量 */
    private Integer commentCount;

    /** 获赞数量 */
    private Integer likeReceivedCount;

    /** 最后活跃时间 */
    private LocalDateTime lastActiveTime;

    /** 禁言截止时间 */
    private LocalDateTime muteEndTime;

    /** 申请理由 */
    private String applyReason;

    /** 审核备注 */
    private String reviewRemark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;
}
