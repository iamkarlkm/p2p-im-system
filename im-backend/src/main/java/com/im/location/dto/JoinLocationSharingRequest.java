package com.im.location.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 加入位置共享请求
 */
@Data
public class JoinLocationSharingRequest {
    
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double latitude;
}
