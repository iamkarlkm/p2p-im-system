package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 好友备注更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRemarkRequest {

    /** 好友ID */
    private Long friendId;

    /** 备注名 */
    private String remarkName;

    /** 分组ID */
    private Long groupId;

    /** 是否置顶 */
    private Boolean isPinned;
}
