package com.im.push.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 推送请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "推送标题不能为空")
    private String title;
    
    @NotBlank(message = "推送内容不能为空")
    private String content;
    
    private String messageId;
    private Integer badge;
    private String sound;
    private Map<String, Object> extra;
    private Integer priority;
}
