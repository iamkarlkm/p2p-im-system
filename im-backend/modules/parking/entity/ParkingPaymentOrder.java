package com.im.backend.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 停车支付订单实体类
 * 管理停车缴费、优惠券、积分抵扣等支付相关数据
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_parking_payment_order")
@Schema(description = "停车支付订单实体")
public class ParkingPaymentOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 支付订单ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "支付订单ID")
    private Long id;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 停车记录ID
     */
    @Schema(description = "停车记录ID")
    private Long parkingRecordId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 停车场ID
     */
    @Schema(description = "停车场ID")
    private Long parkingLotId;

    /**
     * 车牌号
     */
    @Schema(description = "车牌号")
    private String plateNumber;

    /**
     * 停车时长（分钟）
     */
    @Schema(description = "停车时长")
    private Integer parkingDuration;

    /**
     * 原始金额
     */
    @Schema(description = "原始金额")
    private BigDecimal originalAmount;

    /**
     * 优惠券抵扣金额
     */
    @Schema(description = "优惠券抵扣金额")
    private BigDecimal couponDiscount;

    /**
     * 使用的优惠券ID
     */
    @Schema(description = "使用的优惠券ID")
    private Long couponId;

    /**
     * 优惠券名称
     */
    @Schema(description = "优惠券名称")
    private String couponName;

    /**
     * 优惠券类型：1-满减券 2-折扣券 3-免单券
     */
    @Schema(description = "优惠券类型")
    private Integer couponType;

    /**
     * 积分抵扣金额
     */
    @Schema(description = "积分抵扣金额")
    private BigDecimal pointsDiscount;

    /**
     * 使用积分数量
     */
    @Schema(description = "使用积分数量")
    private Integer usedPoints;

    /**
     * 会员折扣金额
     */
    @Schema(description = "会员折扣金额")
    private BigDecimal memberDiscount;

    /**
     * 活动优惠金额
     */
    @Schema(description = "活动优惠金额")
    private BigDecimal activityDiscount;

    /**
     * 优惠总金额
     */
    @Schema(description = "优惠总金额")
    private BigDecimal totalDiscount;

    /**
     * 应付金额
     */
    @Schema(description = "应付金额")
    private BigDecimal payableAmount;

    /**
     * 实付金额
     */
    @Schema(description = "实付金额")
    private BigDecimal actualAmount;

    /**
     * 支付方式：wechat,alipay,unionpay,cash,points,free
     */
    @Schema(description = "支付方式")
    private String paymentMethod;

    /**
     * 支付渠道：wechat_mp,wechat_app,alipay_mp,alipay_app,unionpay
     */
    @Schema(description = "支付渠道")
    private String paymentChannel;

    /**
     * 支付状态：0-未支付 1-支付中 2-已支付 3-支付失败 4-已退款 5-已取消
     */
    @Schema(description = "支付状态")
    private Integer status;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    /**
     * 第三方支付交易号
     */
    @Schema(description = "第三方支付交易号")
    private String thirdPartyTradeNo;

    /**
     * 支付回调时间
     */
    @Schema(description = "支付回调时间")
    private LocalDateTime callbackTime;

    /**
     * 支付回调数据
     */
    @Schema(description = "支付回调数据")
    private String callbackData;

    /**
     * 是否无感支付
     */
    @Schema(description = "是否无感支付")
    private Boolean isContactless;

    /**
     * 无感支付绑定ID
     */
    @Schema(description = "无感支付绑定ID")
    private Long contactlessBindingId;

    /**
     * 是否开通电子发票
     */
    @Schema(description = "是否开通电子发票")
    private Boolean invoiceEnabled;

    /**
     * 发票ID
     */
    @Schema(description = "发票ID")
    private Long invoiceId;

    /**
     * 发票状态：0-未开具 1-申请中 2-已开具
     */
    @Schema(description = "发票状态")
    private Integer invoiceStatus;

    /**
     * 发票类型：personal,enterprise
     */
    @Schema(description = "发票类型")
    private String invoiceType;

    /**
     * 发票抬头
     */
    @Schema(description = "发票抬头")
    private String invoiceTitle;

    /**
     * 发票税号
     */
    @Schema(description = "发票税号")
    private String invoiceTaxNo;

    /**
     * 订单过期时间
     */
    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    /**
     * 订单关闭时间
     */
    @Schema(description = "订单关闭时间")
    private LocalDateTime closeTime;

    /**
     * 关闭原因
     */
    @Schema(description = "关闭原因")
    private String closeReason;

    /**
     * 退款金额
     */
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    /**
     * 退款时间
     */
    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    @Schema(description = "退款原因")
    private String refundReason;

    /**
     * 退款单号
     */
    @Schema(description = "退款单号")
    private String refundNo;

    /**
     * 订单备注
     */
    @Schema(description = "订单备注")
    private String remark;

    /**
     * 设备信息
     */
    @Schema(description = "设备信息")
    private String deviceInfo;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 计算应付金额
     */
    public void calculatePayableAmount() {
        BigDecimal discount = BigDecimal.ZERO;
        if (couponDiscount != null) {
            discount = discount.add(couponDiscount);
        }
        if (pointsDiscount != null) {
            discount = discount.add(pointsDiscount);
        }
        if (memberDiscount != null) {
            discount = discount.add(memberDiscount);
        }
        if (activityDiscount != null) {
            discount = discount.add(activityDiscount);
        }

        this.totalDiscount = discount;
        this.payableAmount = originalAmount.subtract(discount);

        // 确保金额不小于0
        if (this.payableAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.payableAmount = BigDecimal.ZERO;
        }
    }

    /**
     * 应用优惠券
     *
     * @param couponId    优惠券ID
     * @param couponName  优惠券名称
     * @param discount    优惠金额
     * @param couponType  优惠券类型
     */
    public void applyCoupon(Long couponId, String couponName, BigDecimal discount, Integer couponType) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponDiscount = discount;
        this.couponType = couponType;
        calculatePayableAmount();
    }

    /**
     * 应用积分抵扣
     *
     * @param points   使用积分
     * @param discount 抵扣金额
     */
    public void applyPoints(Integer points, BigDecimal discount) {
        this.usedPoints = points;
        this.pointsDiscount = discount;
        calculatePayableAmount();
    }

    /**
     * 标记支付成功
     *
     * @param tradeNo       交易号
     * @param actualAmount  实付金额
     * @param paymentMethod 支付方式
     */
    public void markPaid(String tradeNo, BigDecimal actualAmount, String paymentMethod) {
        this.thirdPartyTradeNo = tradeNo;
        this.actualAmount = actualAmount;
        this.paymentMethod = paymentMethod;
        this.status = 2;
        this.payTime = LocalDateTime.now();
    }

    /**
     * 是否已支付
     *
     * @return 是否已支付
     */
    public boolean isPaid() {
        return status != null && status == 2;
    }

    /**
     * 是否可以支付
     *
     * @return 是否可以支付
     */
    public boolean canPay() {
        return status != null && (status == 0 || status == 3);
    }

    /**
     * 是否已过期
     *
     * @return 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 获取支付状态文本
     *
     * @return 状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "支付中";
            case 2:
                return "已支付";
            case 3:
                return "支付失败";
            case 4:
                return "已退款";
            case 5:
                return "已取消";
            default:
                return "未知";
        }
    }

    /**
     * 获取支付方式文本
     *
     * @return 支付方式文本
     */
    public String getPaymentMethodText() {
        if (paymentMethod == null) {
            return "未选择";
        }
        switch (paymentMethod) {
            case "wechat":
                return "微信支付";
            case "alipay":
                return "支付宝";
            case "unionpay":
                return "银联支付";
            case "cash":
                return "现金支付";
            case "points":
                return "积分抵扣";
            case "free":
                return "免费";
            default:
                return "其他方式";
        }
    }

    /**
     * 生成订单编号
     *
     * @return 订单编号
     */
    public String generateOrderNo() {
        return "P" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
