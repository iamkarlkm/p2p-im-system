package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 好友备注实体
 * 管理用户对好友的备注信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRemark {

    private Long id;

    /** 当前用户ID */
    private Long userId;

    /** 好友用户ID */
    private Long friendId;

    /** 好友备注名 */
    private String remarkName;

    /** 所属分组ID */
    private Long groupId;

    /** 是否置顶: true-置顶, false-不置顶 */
    private Boolean isPinned;

    /** 添加时间 */
    private LocalDateTime addedAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
