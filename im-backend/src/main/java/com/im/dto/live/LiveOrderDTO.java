package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 直播订单响应DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "直播订单信息")
public class LiveOrderDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "直播间ID")
    private Long roomId;

    @Schema(description = "直播间标题")
    private String roomTitle;

    @Schema(description = "主播ID")
    private Long anchorId;

    @Schema(description = "主播昵称")
    private String anchorNickname;

    @Schema(description = "订单状态：0-待付款 1-已付款 2-已发货 3-已收货 4-已完成 5-已取消 6-退款中 7-已退款")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "商品总金额（元）")
    private BigDecimal productAmount;

    @Schema(description = "运费（元）")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额（元）")
    private BigDecimal discountAmount;

    @Schema(description = "实付金额（元）")
    private BigDecimal payAmount;

    @Schema(description = "支付方式：1-微信支付 2-支付宝 3-余额支付")
    private Integer payType;

    @Schema(description = "支付方式文本")
    private String payTypeText;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "详细地址")
    private String receiverDetail;

    @Schema(description = "配送方式：1-快递 2-同城配送 3-到店自提")
    private Integer deliveryType;

    @Schema(description = "配送方式文本")
    private String deliveryTypeText;

    @Schema(description = "自提码")
    private String pickupCode;

    @Schema(description = "自提时间")
    private LocalDateTime pickupTime;

    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Schema(description = "物流单号")
    private String logisticsNo;

    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    @Schema(description = "收货时间")
    private LocalDateTime receiveTime;

    @Schema(description = "订单商品列表")
    private List<LiveOrderItemResponseDTO> items;

    @Schema(description = "买家留言")
    private String buyerRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "剩余支付时间（秒），null表示不需要支付")
    private Long remainingPaySeconds;

    @Schema(description = "是否可以取消")
    private Boolean canCancel;

    @Schema(description = "是否可以申请退款")
    private Boolean canRefund;

    @Schema(description = "是否可以确认收货")
    private Boolean canReceive;

    /**
     * 订单商品项响应
     */
    @Data
    @Schema(description = "订单商品项")
    public static class LiveOrderItemResponseDTO {

        @Schema(description = "商品ID")
        private Long productId;

        @Schema(description = "商品名称")
        private String productName;

        @Schema(description = "商品图片")
        private String productImage;

        @Schema(description = "商品规格")
        private String specification;

        @Schema(description = "单价（元）")
        private BigDecimal price;

        @Schema(description = "购买数量")
        private Integer quantity;

        @Schema(description = "小计金额（元）")
        private BigDecimal subtotal;
    }
}
