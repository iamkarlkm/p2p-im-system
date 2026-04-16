package com.im.service.user.dto;

import lombok.Data;

/**
 * 添加好友请求DTO
 */
@Data
public class AddFriendRequest {

    /**
     * 目标用户ID
     */
    private Long targetUserId;

    /**
     * 申请来源: SEARCH, QR_CODE, CONTACT, GROUP, RECOMMEND, OTHER
     */
    private String source;

    /**
     * 验证消息
     */
    private String applyMessage;
}
