package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 智能到店消息VO
 */
@Data
@Schema(description = "智能到店消息")
public class SmartArrivalMessageVO {
    
    @Schema(description = "消息ID")
    private Long id;
    
    @Schema(description = "消息唯一标识")
    private String messageId;
    
    @Schema(description = "围栏ID")
    private Long geofenceId;
    
    @Schema(description = "商户ID")
    private Long merchantId;
    
    @Schema(description = "商户名称")
    private String merchantName;
    
    @Schema(description = "商户Logo")
    private String merchantLogo;
    
    @Schema(description = "消息类型: WELCOME-欢迎, THANKS-感谢, OFFER-优惠, SERVICE-服务")
    private String messageType;
    
    @Schema(description = "消息标题")
    private String title;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "副标题/摘要")
    private String subtitle;
    
    @Schema(description = "封面图片")
    private String coverImage;
    
    @Schema(description = "跳转链接")
    private String actionUrl;
    
    @Schema(description = "跳转类型")
    private String actionType;
    
    @Schema(description = "按钮文字")
    private String actionButtonText;
    
    @Schema(description = "优惠券ID")
    private Long couponId;
    
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @Schema(description = "优惠券金额")
    private BigDecimal couponAmount;
    
    @Schema(description = "会员专属")
    private Boolean memberExclusive;
    
    @Schema(description = "消息状态")
    private String status;
    
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @Schema(description = "已读时间")
    private LocalDateTime readTime;
    
    @Schema(description = "是否已读")
    private Boolean isRead;
}
