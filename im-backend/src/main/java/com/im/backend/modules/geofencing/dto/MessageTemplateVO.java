package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 消息模板VO
 */
@Data
@Schema(description = "消息模板")
public class MessageTemplateVO {
    
    @Schema(description = "模板ID")
    private Long templateId;
    
    @Schema(description = "模板名称")
    private String name;
    
    @Schema(description = "消息类型")
    private String messageType;
    
    @Schema(description = "标题模板")
    private String titleTemplate;
    
    @Schema(description = "内容模板")
    private String contentTemplate;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
}
