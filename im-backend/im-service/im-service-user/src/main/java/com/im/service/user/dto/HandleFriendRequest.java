package com.im.service.user.dto;

import lombok.Data;

/**
 * 处理好友申请请求DTO
 */
@Data
public class HandleFriendRequest {

    /**
     * 申请记录ID
     */
    private Long requestId;

    /**
     * 是否接受: true-接受, false-拒绝
     */
    private Boolean accepted;

    /**
     * 拒绝理由（可选）
     */
    private String rejectReason;
}
