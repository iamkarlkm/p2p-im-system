package com.im.backend.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 停车记录实体类
 * 记录用户每次停车的完整信息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_parking_record")
@Schema(description = "停车记录实体")
public class ParkingRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 停车记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "停车记录ID")
    private Long id;

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
     * 车牌颜色：blue,yellow,green,white,black
     */
    @Schema(description = "车牌颜色")
    private String plateColor;

    /**
     * 车辆类型：1-小型车 2-中型车 3-大型车 4-新能源车
     */
    @Schema(description = "车辆类型")
    private Integer vehicleType;

    /**
     * 车辆ID（绑定车辆）
     */
    @Schema(description = "车辆ID")
    private Long vehicleId;

    /**
     * 入场时间
     */
    @Schema(description = "入场时间")
    private LocalDateTime entryTime;

    /**
     * 入场经度
     */
    @Schema(description = "入场经度")
    private Double entryLongitude;

    /**
     * 入场纬度
     */
    @Schema(description = "入场纬度")
    private Double entryLatitude;

    /**
     * 入场位置名称
     */
    @Schema(description = "入场位置名称")
    private String entryLocationName;

    /**
     * 入场方式：1-自动识别 2-扫码 3-预约 4-共享停车
     */
    @Schema(description = "入场方式")
    private Integer entryMethod;

    /**
     * 入场照片URL
     */
    @Schema(description = "入场照片URL")
    private String entryPhotoUrl;

    /**
     * 入场设备ID
     */
    @Schema(description = "入场设备ID")
    private String entryDeviceId;

    /**
     * 入场操作员ID
     */
    @Schema(description = "入场操作员ID")
    private Long entryOperatorId;

    /**
     * 停车楼层
     */
    @Schema(description = "停车楼层")
    private String parkingFloor;

    /**
     * 停车区域
     */
    @Schema(description = "停车区域")
    private String parkingArea;

    /**
     * 停车位编号
     */
    @Schema(description = "停车位编号")
    private String parkingSpaceNumber;

    /**
     * 车位照片URL（用于反向寻车）
     */
    @Schema(description = "车位照片URL")
    private String parkingSpacePhotoUrl;

    /**
     * 车位经度（记录位置）
     */
    @Schema(description = "车位经度")
    private Double spaceLongitude;

    /**
     * 车位纬度（记录位置）
     */
    @Schema(description = "车位纬度")
    private Double spaceLatitude;

    /**
     * 车位标记备注
     */
    @Schema(description = "车位标记备注")
    private String spaceMarkNote;

    /**
     * 出场时间
     */
    @Schema(description = "出场时间")
    private LocalDateTime exitTime;

    /**
     * 出场经度
     */
    @Schema(description = "出场经度")
    private Double exitLongitude;

    /**
     * 出场纬度
     */
    @Schema(description = "出场纬度")
    private Double exitLatitude;

    /**
     * 出场方式：1-自动识别 2-扫码 3-无感支付 4-人工处理
     */
    @Schema(description = "出场方式")
    private Integer exitMethod;

    /**
     * 出场照片URL
     */
    @Schema(description = "出场照片URL")
    private String exitPhotoUrl;

    /**
     * 出场设备ID
     */
    @Schema(description = "出场设备ID")
    private String exitDeviceId;

    /**
     * 出场操作员ID
     */
    @Schema(description = "出场操作员ID")
    private Long exitOperatorId;

    /**
     * 停车状态：1-停车中 2-已离场 3-已取消
     */
    @Schema(description = "停车状态")
    private Integer status;

    /**
     * 停车时长（分钟）
     */
    @Schema(description = "停车时长")
    private Integer parkingDuration;

    /**
     * 应缴金额
     */
    @Schema(description = "应缴金额")
    private BigDecimal payableAmount;

    /**
     * 优惠金额
     */
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    /**
     * 实缴金额
     */
    @Schema(description = "实缴金额")
    private BigDecimal actualAmount;

    /**
     * 支付方式：wechat,alipay,unionpay,cash,points
     */
    @Schema(description = "支付方式")
    private String paymentMethod;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    /**
     * 支付交易号
     */
    @Schema(description = "支付交易号")
    private String paymentTradeNo;

    /**
     * 是否使用优惠券
     */
    @Schema(description = "是否使用优惠券")
    private Boolean usedCoupon;

    /**
     * 优惠券ID
     */
    @Schema(description = "优惠券ID")
    private Long couponId;

    /**
     * 优惠券名称
     */
    @Schema(description = "优惠券名称")
    private String couponName;

    /**
     * 是否使用积分抵扣
     */
    @Schema(description = "是否使用积分抵扣")
    private Boolean usedPoints;

    /**
     * 抵扣积分数量
     */
    @Schema(description = "抵扣积分数量")
    private Integer usedPointsAmount;

    /**
     * 积分抵扣金额
     */
    @Schema(description = "积分抵扣金额")
    private BigDecimal pointsDiscountAmount;

    /**
     * 电子发票ID
     */
    @Schema(description = "电子发票ID")
    private Long invoiceId;

    /**
     * 发票状态：0-未开具 1-已申请 2-已开具
     */
    @Schema(description = "发票状态")
    private Integer invoiceStatus;

    /**
     * 是否预约停车
     */
    @Schema(description = "是否预约停车")
    private Boolean isReservation;

    /**
     * 预约ID
     */
    @Schema(description = "预约ID")
    private Long reservationId;

    /**
     * 预约到达时间
     */
    @Schema(description = "预约到达时间")
    private LocalDateTime reservationTime;

    /**
     * 是否为共享停车
     */
    @Schema(description = "是否为共享停车")
    private Boolean isSharedParking;

    /**
     * 共享停车发布ID
     */
    @Schema(description = "共享停车发布ID")
    private Long sharedParkingId;

    /**
     * 订单备注
     */
    @Schema(description = "订单备注")
    private String remark;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 计算停车时长
     *
     * @return 停车时长（分钟）
     */
    public Integer calculateParkingDuration() {
        if (entryTime == null) {
            return 0;
        }
        LocalDateTime endTime = exitTime != null ? exitTime : LocalDateTime.now();
        return (int) java.time.Duration.between(entryTime, endTime).toMinutes();
    }

    /**
     * 更新停车时长
     */
    public void updateParkingDuration() {
        this.parkingDuration = calculateParkingDuration();
    }

    /**
     * 是否停车中
     *
     * @return 是否停车中
     */
    public boolean isParking() {
        return status != null && status == 1;
    }

    /**
     * 是否已完成
     *
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return status != null && status == 2;
    }

    /**
     * 完成停车记录
     *
     * @param exitTime   出场时间
     * @param exitMethod 出场方式
     */
    public void completeParking(LocalDateTime exitTime, Integer exitMethod) {
        this.exitTime = exitTime;
        this.exitMethod = exitMethod;
        this.status = 2;
        updateParkingDuration();
    }

    /**
     * 记录支付信息
     *
     * @param actualAmount 实缴金额
     * @param paymentMethod 支付方式
     * @param tradeNo      交易号
     */
    public void recordPayment(BigDecimal actualAmount, String paymentMethod, String tradeNo) {
        this.actualAmount = actualAmount;
        this.paymentMethod = paymentMethod;
        this.paymentTradeNo = tradeNo;
        this.paymentTime = LocalDateTime.now();
    }

    /**
     * 获取格式化的停车时长
     *
     * @return 格式化时长（如：2小时30分钟）
     */
    public String getFormattedDuration() {
        if (parkingDuration == null) {
            updateParkingDuration();
        }
        if (parkingDuration == null || parkingDuration == 0) {
            return "0分钟";
        }

        int hours = parkingDuration / 60;
        int minutes = parkingDuration % 60;

        if (hours > 0 && minutes > 0) {
            return hours + "小时" + minutes + "分钟";
        } else if (hours > 0) {
            return hours + "小时";
        } else {
            return minutes + "分钟";
        }
    }

    /**
     * 获取停车天数
     *
     * @return 停车天数
     */
    public Integer getParkingDays() {
        if (parkingDuration == null) {
            updateParkingDuration();
        }
        return parkingDuration / (24 * 60);
    }

    /**
     * 计算预计费用（根据当前时长）
     *
     * @param basePrice     基础价格
     * @param unitPrice     单位价格
     * @param unitDuration  单位时长
     * @param freeDuration  免费时长
     * @param dailyCap      每日封顶
     * @return 预计费用
     */
    public BigDecimal calculateCurrentFee(BigDecimal basePrice, BigDecimal unitPrice,
                                          Integer unitDuration, Integer freeDuration, BigDecimal dailyCap) {
        if (parkingDuration == null) {
            updateParkingDuration();
        }

        if (parkingDuration <= freeDuration) {
            return BigDecimal.ZERO;
        }

        int chargeMinutes = parkingDuration - freeDuration;
        BigDecimal totalFee = basePrice;

        if (chargeMinutes > 60) {
            int extraUnits = (int) Math.ceil((chargeMinutes - 60.0) / unitDuration);
            totalFee = totalFee.add(unitPrice.multiply(new BigDecimal(extraUnits)));
        }

        // 多日封顶处理
        int days = getParkingDays() + 1;
        BigDecimal maxFee = dailyCap.multiply(new BigDecimal(days));
        if (totalFee.compareTo(maxFee) > 0) {
            totalFee = maxFee;
        }

        return totalFee;
    }

    /**
     * 获取完整的停车位置信息
     *
     * @return 停车位置描述
     */
    public String getFullParkingLocation() {
        StringBuilder sb = new StringBuilder();
        if (parkingFloor != null && !parkingFloor.isEmpty()) {
            sb.append(parkingFloor).append("层 ");
        }
        if (parkingArea != null && !parkingArea.isEmpty()) {
            sb.append(parkingArea).append("区 ");
        }
        if (parkingSpaceNumber != null && !parkingSpaceNumber.isEmpty()) {
            sb.append(parkingSpaceNumber).append("号车位");
        }
        return sb.toString().trim();
    }
}
