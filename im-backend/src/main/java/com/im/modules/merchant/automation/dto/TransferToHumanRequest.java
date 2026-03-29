package com.im.modules.merchant.automation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 智能转人工请求DTO
 */
@Data
public class TransferToHumanRequest {
    
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    @NotBlank(message = "商户ID不能为空")
    private String merchantId;
    
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    private String reason;
    
    private String priority = "NORMAL";
    
    private List<String> tags;
    
    private String expectedSkill;
}
