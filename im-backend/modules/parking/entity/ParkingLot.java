package com.im.backend.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 停车场实体类
 * 存储停车场基本信息、位置、容量、价格等数据
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_parking_lot")
@Schema(description = "停车场实体")
public class ParkingLot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 停车场ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "停车场ID")
    private Long id;

    /**
     * 停车场名称
     */
    @Schema(description = "停车场名称")
    private String name;

    /**
     * 停车场编码（唯一标识）
     */
    @Schema(description = "停车场编码")
    private String code;

    /**
     * 所属商户ID
     */
    @Schema(description = "所属商户ID")
    private Long merchantId;

    /**
     * 停车场类型：1-路侧停车 2-室内停车场 3-露天停车场 4-立体车库 5-机械车库
     */
    @Schema(description = "停车场类型")
    private Integer type;

    /**
     * 经度（WGS84坐标系）
     */
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 纬度（WGS84坐标系）
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;

    /**
     * 省市区编码
     */
    @Schema(description = "省市区编码")
    private String areaCode;

    /**
     * GeoHash编码（用于地理围栏查询）
     */
    @Schema(description = "GeoHash编码")
    private String geoHash;

    /**
     * 总车位数
     */
    @Schema(description = "总车位数")
    private Integer totalSpaces;

    /**
     * 可用车位数
     */
    @Schema(description = "可用车位数")
    private Integer availableSpaces;

    /**
     * 已占用车位数
     */
    @Schema(description = "已占用车位数")
    private Integer occupiedSpaces;

    /**
     * 预留车位数（VIP/残疾人等）
     */
    @Schema(description = "预留车位数")
    private Integer reservedSpaces;

    /**
     * 充电桩数量
     */
    @Schema(description = "充电桩数量")
    private Integer chargingPiles;

    /**
     * 营业状态：0-关闭 1-营业中 2-已满 3-维护中
     */
    @Schema(description = "营业状态")
    private Integer status;

    /**
     * 营业时间配置（JSON格式）
     * {"weekday": "00:00-24:00", "weekend": "00:00-24:00", "holiday": "08:00-22:00"}
     */
    @Schema(description = "营业时间配置")
    private String businessHours;

    /**
     * 是否24小时营业
     */
    @Schema(description = "是否24小时营业")
    private Boolean isOpen24Hours;

    /**
     * 收费标准（JSON格式）
     * {"basePrice": 5, "unitPrice": 2, "unitDuration": 60, "dailyCap": 50, "nightPrice": 10}
     */
    @Schema(description = "收费标准")
    private String feeConfig;

    /**
     * 基础价格（首小时）
     */
    @Schema(description = "基础价格")
    private BigDecimal basePrice;

    /**
     * 计费单位价格
     */
    @Schema(description = "计费单位价格")
    private BigDecimal unitPrice;

    /**
     * 计费单位时长（分钟）
     */
    @Schema(description = "计费单位时长")
    private Integer unitDuration;

    /**
     * 每日封顶价格
     */
    @Schema(description = "每日封顶价格")
    private BigDecimal dailyCap;

    /**
     * 夜间价格
     */
    @Schema(description = "夜间价格")
    private BigDecimal nightPrice;

    /**
     * 免费时长（分钟）
     */
    @Schema(description = "免费时长")
    private Integer freeDuration;

    /**
     * 是否支持无感支付
     */
    @Schema(description = "是否支持无感支付")
    private Boolean supportsContactlessPayment;

    /**
     * 支持的支付方式：wechat,alipay,unionpay,cash
     */
    @Schema(description = "支持的支付方式")
    private String supportedPaymentMethods;

    /**
     * 停车场入口坐标列表（JSON数组）
     */
    @Schema(description = "停车场入口坐标列表")
    private String entranceCoordinates;

    /**
     * 停车场出口坐标列表（JSON数组）
     */
    @Schema(description = "停车场出口坐标列表")
    private String exitCoordinates;

    /**
     * 楼层信息（多层停车场）
     */
    @Schema(description = "楼层信息")
    private String floorInfo;

    /**
     * 是否有室内导航
     */
    @Schema(description = "是否有室内导航")
    private Boolean hasIndoorNavigation;

    /**
     * 是否支持预约停车
     */
    @Schema(description = "是否支持预约停车")
    private Boolean supportsReservation;

    /**
     * 是否支持共享停车
     */
    @Schema(description = "是否支持共享停车")
    private Boolean supportsSharing;

    /**
     * 停车场图片URL列表（逗号分隔）
     */
    @Schema(description = "停车场图片URL列表")
    private String images;

    /**
     * 停车场设施：elevator,escalator,restroom,cctv,security,charging,wash
     */
    @Schema(description = "停车场设施")
    private String facilities;

    /**
     * 数据来源：internal,gaode,baidu,tencent
     */
    @Schema(description = "数据来源")
    private String dataSource;

    /**
     * 第三方停车场ID
     */
    @Schema(description = "第三方停车场ID")
    private String thirdPartyId;

    /**
     * 实时数据更新时间
     */
    @Schema(description = "实时数据更新时间")
    private LocalDateTime realTimeUpdateTime;

    /**
     * 数据同步时间
     */
    @Schema(description = "数据同步时间")
    private LocalDateTime syncTime;

    /**
     * 评分（1-5分）
     */
    @Schema(description = "评分")
    private BigDecimal rating;

    /**
     * 评分人数
     */
    @Schema(description = "评分人数")
    private Integer ratingCount;

    /**
     * 今日停车人次
     */
    @Schema(description = "今日停车人次")
    private Integer todayParkingCount;

    /**
     * 平均停车时长（分钟）
     */
    @Schema(description = "平均停车时长")
    private Integer avgParkingDuration;

    /**
     * 权重分数（用于排序）
     */
    @Schema(description = "权重分数")
    private Integer weightScore;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Boolean isRecommended;

    /**
     * 推荐排序
     */
    @Schema(description = "推荐排序")
    private Integer recommendSort;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 计算预计停车费用
     *
     * @param durationMinutes 停车时长（分钟）
     * @return 预计费用
     */
    public BigDecimal calculateEstimatedFee(Integer durationMinutes) {
        if (durationMinutes <= freeDuration) {
            return BigDecimal.ZERO;
        }

        int chargeMinutes = durationMinutes - freeDuration;
        BigDecimal totalFee = basePrice;

        if (chargeMinutes > 60) {
            int extraUnits = (int) Math.ceil((chargeMinutes - 60.0) / unitDuration);
            totalFee = totalFee.add(unitPrice.multiply(new BigDecimal(extraUnits)));
        }

        // 每日封顶
        if (dailyCap != null && totalFee.compareTo(dailyCap) > 0) {
            totalFee = dailyCap;
        }

        return totalFee;
    }

    /**
     * 更新可用车位数
     *
     * @param delta 变化量（正数为增加，负数为减少）
     */
    public void updateAvailableSpaces(Integer delta) {
        this.availableSpaces = Math.max(0, Math.min(totalSpaces, availableSpaces + delta));
        this.occupiedSpaces = totalSpaces - availableSpaces;
        this.realTimeUpdateTime = LocalDateTime.now();
    }

    /**
     * 判断是否营业中
     *
     * @return 是否营业
     */
    public boolean isOpenNow() {
        if (isOpen24Hours != null && isOpen24Hours) {
            return true;
        }
        if (status == null || status != 1) {
            return false;
        }
        // 简化判断，实际应根据businessHours解析
        return true;
    }

    /**
     * 检查是否有空位
     *
     * @return 是否有空位
     */
    public boolean hasAvailableSpace() {
        return availableSpaces != null && availableSpaces > 0;
    }

    /**
     * 获取空置率
     *
     * @return 空置率（0-1之间）
     */
    public Double getVacancyRate() {
        if (totalSpaces == null || totalSpaces == 0) {
            return 0.0;
        }
        return (double) availableSpaces / totalSpaces;
    }

    /**
     * 计算距离评分（距离越近分数越高）
     *
     * @param distance 距离（米）
     * @return 距离评分（0-100）
     */
    public Integer calculateDistanceScore(Double distance) {
        if (distance <= 100) {
            return 100;
        } else if (distance <= 500) {
            return 80;
        } else if (distance <= 1000) {
            return 60;
        } else if (distance <= 2000) {
            return 40;
        } else {
            return 20;
        }
    }

    /**
     * 计算价格评分（价格越低分数越高）
     *
     * @param avgPrice 平均价格
     * @return 价格评分（0-100）
     */
    public Integer calculatePriceScore(BigDecimal avgPrice) {
        if (basePrice == null) {
            return 50;
        }
        if (basePrice.compareTo(new BigDecimal("5")) <= 0) {
            return 100;
        } else if (basePrice.compareTo(new BigDecimal("10")) <= 0) {
            return 80;
        } else if (basePrice.compareTo(new BigDecimal("20")) <= 0) {
            return 60;
        } else if (basePrice.compareTo(new BigDecimal("30")) <= 0) {
            return 40;
        } else {
            return 20;
        }
    }

    /**
     * 计算综合推荐分数
     *
     * @param distance 距离（米）
     * @return 综合分数
     */
    public Integer calculateRecommendScore(Double distance) {
        int distanceScore = calculateDistanceScore(distance);
        int priceScore = calculatePriceScore(basePrice);
        int vacancyScore = (int) (getVacancyRate() * 100);
        int ratingScore = rating != null ? rating.multiply(new BigDecimal("20")).intValue() : 50;

        // 加权计算：距离40% + 价格20% + 空置率20% + 评分20%
        return (int) (distanceScore * 0.4 + priceScore * 0.2 + vacancyScore * 0.2 + ratingScore * 0.2);
    }
}
