package com.im.modules.merchant.automation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能客服消息请求DTO
 * 用于接收用户发送的客服消息
 */
@Data
public class ChatbotMessageRequest {
    
    @NotBlank(message = "商户ID不能为空")
    private String merchantId;
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000字符")
    private String message;
    
    private String sessionId;
    
    private String messageType = "TEXT";
    
    private List<String> imageUrls;
    
    private Map<String, Object> context;
}
