package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 好友分组实体
 * 管理好友的分组信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendGroup {

    private Long id;

    /** 用户ID */
    private Long userId;

    /** 分组名称 */
    private String groupName;

    /** 分组排序 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
