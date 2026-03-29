// 支付渠道DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentChannelDTO {
    private ChannelItemDTO wechatPay;
    private ChannelItemDTO alipay;
    private ChannelItemDTO unionPay;
    private ChannelItemDTO memberCard;
    private ChannelItemDTO other;
}

@Data
class ChannelItemDTO {
    private BigDecimal amount;
    private Integer count;
    private BigDecimal ratio;
    private String name;
}
