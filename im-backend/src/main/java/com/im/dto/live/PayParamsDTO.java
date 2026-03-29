package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 支付参数DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "支付参数")
public class PayParamsDTO {

    @Schema(description = "支付方式：1-微信支付 2-支付宝 3-余额支付")
    private Integer payType;

    @Schema(description = "支付方式文本")
    private String payTypeText;

    // 微信支付参数
    @Schema(description = "微信appId")
    private String wxAppId;

    @Schema(description = "微信时间戳")
    private String wxTimeStamp;

    @Schema(description = "微信随机字符串")
    private String wxNonceStr;

    @Schema(description = "微信预支付ID")
    private String wxPackage;

    @Schema(description = "微信签名类型")
    private String wxSignType;

    @Schema(description = "微信签名")
    private String wxPaySign;

    // 支付宝参数
    @Schema(description = "支付宝订单信息")
    private String alipayOrderInfo;

    // 通用参数
    @Schema(description = "商户订单号")
    private String outTradeNo;

    @Schema(description = "支付金额（元）")
    private String totalAmount;

    @Schema(description = "订单标题")
    private String subject;

    @Schema(description = "支付回调URL")
    private String notifyUrl;
}
