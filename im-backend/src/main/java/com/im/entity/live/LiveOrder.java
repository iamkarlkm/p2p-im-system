package com.im.entity.live;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 直播订单实体类
 * 直播间带货订单管理
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_live_order")
public class LiveOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 直播间ID */
    private Long roomId;

    /** 主播ID */
    private Long anchorId;

    /** 商户ID */
    private Long merchantId;

    /** 订单状态：0-待付款 1-已付款 2-已发货 3-已收货 4-已完成 5-已取消 6-退款中 7-已退款 */
    private Integer status;

    /** 商品总金额（分） */
    private Long productAmount;

    /** 运费（分） */
    private Long freightAmount;

    /** 优惠金额（分） */
    private Long discountAmount;

    /** 实付金额（分） */
    private Long payAmount;

    /** 支付方式：1-微信支付 2-支付宝 3-余额支付 */
    private Integer payType;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 支付流水号 */
    private String payTradeNo;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 收货地址 */
    private String receiverAddress;

    /** 详细地址 */
    private String receiverDetail;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区县 */
    private String district;

    /** 邮编 */
    private String zipCode;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** 配送方式：1-快递 2-同城配送 3-到店自提 */
    private Integer deliveryType;

    /** 自提门店ID */
    private Long pickupStoreId;

    /** 自提码 */
    private String pickupCode;

    /** 自提时间 */
    private LocalDateTime pickupTime;

    /** 物流公司 */
    private String logisticsCompany;

    /** 物流单号 */
    private String logisticsNo;

    /** 发货时间 */
    private LocalDateTime shipTime;

    /** 收货时间 */
    private LocalDateTime receiveTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 取消原因 */
    private String cancelReason;

    /** 买家留言 */
    private String buyerRemark;

    /** 卖家备注 */
    private String sellerRemark;

    /** 订单来源：1-直播间 2-回放 3-商品详情 */
    private Integer sourceType;

    /** 推广人ID（分销） */
    private Long promoterId;

    /** 佣金比例 */
    private Integer commissionRate;

    /** 佣金金额（分） */
    private Long commissionAmount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 是否删除：0-否 1-是 */
    @TableLogic
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 判断是否待付款
     */
    public boolean isPendingPayment() {
        return status != null && status == 0;
    }

    /**
     * 判断是否已付款
     */
    public boolean isPaid() {
        return status != null && status >= 1;
    }

    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return status != null && status == 4;
    }

    /**
     * 判断是否已取消
     */
    public boolean isCancelled() {
        return status != null && status == 5;
    }

    /**
     * 计算订单总金额
     */
    public long calculateTotalAmount() {
        long product = productAmount != null ? productAmount : 0;
        long freight = freightAmount != null ? freightAmount : 0;
        long discount = discountAmount != null ? discountAmount : 0;
        return product + freight - discount;
    }

    /**
     * 支付订单
     */
    public void pay(int payType, String payTradeNo) {
        this.status = 1;
        this.payType = payType;
        this.payTradeNo = payTradeNo;
        this.payTime = LocalDateTime.now();
        this.payAmount = calculateTotalAmount();
    }

    /**
     * 发货
     */
    public void ship(String logisticsCompany, String logisticsNo) {
        this.status = 2;
        this.logisticsCompany = logisticsCompany;
        this.logisticsNo = logisticsNo;
        this.shipTime = LocalDateTime.now();
    }

    /**
     * 确认收货
     */
    public void receive() {
        this.status = 3;
        this.receiveTime = LocalDateTime.now();
    }

    /**
     * 完成订单
     */
    public void complete() {
        this.status = 4;
        this.completeTime = LocalDateTime.now();
    }

    /**
     * 取消订单
     */
    public void cancel(String reason) {
        this.status = 5;
        this.cancelReason = reason;
        this.cancelTime = LocalDateTime.now();
    }

    /**
     * 获取实付金额（元）
     */
    public double getPayAmountYuan() {
        return payAmount != null ? payAmount / 100.0 : 0.0;
    }

    /**
     * 获取佣金（元）
     */
    public double getCommissionYuan() {
        return commissionAmount != null ? commissionAmount / 100.0 : 0.0;
    }
}
