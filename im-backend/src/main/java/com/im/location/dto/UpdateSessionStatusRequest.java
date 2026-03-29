package com.im.location.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新会话状态请求
 */
@Data
public class UpdateSessionStatusRequest {
    
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    /**
     * 新状态: 1-开始 2-暂停 3-结束
     */
    @NotBlank(message = "状态不能为空")
    private Integer status;
}
