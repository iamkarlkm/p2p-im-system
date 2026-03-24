package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 在线状态更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineStatusRequest {

    /** 在线状态: ONLINE, AWAY, BUSY, DND, INVISIBLE, OFFLINE */
    private String status;

    /** 自定义状态文本 */
    private String statusText;
}
