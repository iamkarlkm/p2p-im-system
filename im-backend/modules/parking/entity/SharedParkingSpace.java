package com.im.backend.modules.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 共享停车位实体类
 * 管理个人车位共享、错时停车等共享停车资源
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_shared_parking_space")
@Schema(description = "共享停车位实体")
public class SharedParkingSpace extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 共享停车位ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "共享停车位ID")
    private Long id;

    /**
     * 发布用户ID
     */
    @Schema(description = "发布用户ID")
    private Long ownerId;

    /**
     * 停车场ID
     */
    @Schema(description = "停车场ID")
    private Long parkingLotId;

    /**
     * 停车场名称
     */
    @Schema(description = "停车场名称")
    private String parkingLotName;

    /**
     * 车位编号
     */
    @Schema(description = "车位编号")
    private String spaceNumber;

    /**
     * 车位位置描述
     */
    @Schema(description = "车位位置描述")
    private String locationDescription;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 楼层
     */
    @Schema(description = "楼层")
    private String floor;

    /**
     * 区域
     */
    @Schema(description = "区域")
    private String area;

    /**
     * 车位类型：1-标准车位 2-大车位 3-残疾人车位 4-充电桩车位
     */
    @Schema(description = "车位类型")
    private Integer spaceType;

    /**
     * 车位照片URL列表
     */
    @Schema(description = "车位照片URL列表")
    private String photos;

    /**
     * 共享类型：1-固定时段共享 2-灵活共享 3-长期共享
     */
    @Schema(description = "共享类型")
    private Integer shareType;

    /**
     * 可共享开始日期
     */
    @Schema(description = "可共享开始日期")
    private LocalDateTime shareStartDate;

    /**
     * 可共享结束日期
     */
    @Schema(description = "可共享结束日期")
    private LocalDateTime shareEndDate;

    /**
     * 可共享时段配置（JSON）
     * [{"dayOfWeek":1,"startTime":"08:00","endTime":"18:00"}]
     */
    @Schema(description = "可共享时段配置")
    private String shareTimeConfig;

    /**
     * 每小时价格
     */
    @Schema(description = "每小时价格")
    private BigDecimal hourlyPrice;

    /**
     * 每日封顶价格
     */
    @Schema(description = "每日封顶价格")
    private BigDecimal dailyCap;

    /**
     * 晚间特价（22:00-08:00）
     */
    @Schema(description = "晚间特价")
    private BigDecimal nightPrice;

    /**
     * 是否支持包月
     */
    @Schema(description = "是否支持包月")
    private Boolean supportsMonthly;

    /**
     * 包月价格
     */
    @Schema(description = "包月价格")
    private BigDecimal monthlyPrice;

    /**
     * 押金金额
     */
    @Schema(description = "押金金额")
    private BigDecimal depositAmount;

    /**
     * 取消政策：1-免费取消 2-提前2小时 3-提前1天
     */
    @Schema(description = "取消政策")
    private Integer cancellationPolicy;

    /**
     * 车位状态：1-审核中 2-待发布 3-已发布 4-已停用
     */
    @Schema(description = "车位状态")
    private Integer status;

    /**
     * 车位设施：elevator,camera,covered,charging,security
     */
    @Schema(description = "车位设施")
    private String facilities;

    /**
     * 使用须知
     */
    @Schema(description = "使用须知")
    private String usageNotes;

    /**
     * 车位权属证明URL
     */
    @Schema(description = "车位权属证明URL")
    private String ownershipProofUrl;

    /**
     * 审核状态：0-未提交 1-审核中 2-已通过 3-已拒绝
     */
    @Schema(description = "审核状态")
    private Integer auditStatus;

    /**
     * 审核备注
     */
    @Schema(description = "审核备注")
    private String auditRemark;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    @Schema(description = "审核人ID")
    private Long auditorId;

    /**
     * 发布日期
     */
    @Schema(description = "发布日期")
    private LocalDateTime publishTime;

    /**
     * 下架日期
     */
    @Schema(description = "下架日期")
    private LocalDateTime unpublishTime;

    /**
     * 总共享次数
     */
    @Schema(description = "总共享次数")
    private Integer totalShares;

    /**
     * 成功共享次数
     */
    @Schema(description = "成功共享次数")
    private Integer successfulShares;

    /**
     * 累计收入
     */
    @Schema(description = "累计收入")
    private BigDecimal totalIncome;

    /**
     * 评分
     */
    @Schema(description = "评分")
    private BigDecimal rating;

    /**
     * 评价数量
     */
    @Schema(description = "评价数量")
    private Integer ratingCount;

    /**
     * 被收藏次数
     */
    @Schema(description = "被收藏次数")
    private Integer favoriteCount;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 是否自动接单
     */
    @Schema(description = "是否自动接单")
    private Boolean autoAccept;

    /**
     * 删除标记
     */
    @TableLogic
    @Schema(description = "删除标记")
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 发布车位
     */
    public void publish() {
        if (auditStatus != null && auditStatus == 2) {
            this.status = 3;
            this.publishTime = LocalDateTime.now();
        }
    }

    /**
     * 下架车位
     */
    public void unpublish() {
        this.status = 4;
        this.unpublishTime = LocalDateTime.now();
    }

    /**
     * 通过审核
     *
     * @param auditorId 审核人ID
     */
    public void approve(Long auditorId) {
        this.auditStatus = 2;
        this.auditTime = LocalDateTime.now();
        this.auditorId = auditorId;
    }

    /**
     * 拒绝审核
     *
     * @param auditorId 审核人ID
     * @param remark    拒绝原因
     */
    public void reject(Long auditorId, String remark) {
        this.auditStatus = 3;
        this.auditRemark = remark;
        this.auditTime = LocalDateTime.now();
        this.auditorId = auditorId;
    }

    /**
     * 是否已发布
     *
     * @return 是否已发布
     */
    public boolean isPublished() {
        return status != null && status == 3 && auditStatus != null && auditStatus == 2;
    }

    /**
     * 是否可以预约
     *
     * @return 是否可以预约
     */
    public boolean isAvailable() {
        return isPublished();
    }

    /**
     * 计算预计费用
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 预计费用
     */
    public BigDecimal calculateEstimatedFee(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || hourlyPrice == null) {
            return BigDecimal.ZERO;
        }

        long hours = java.time.Duration.between(startTime, endTime).toHours();
        if (hours == 0) {
            hours = 1; // 最少按1小时计费
        }

        BigDecimal fee = hourlyPrice.multiply(new BigDecimal(hours));

        // 应用每日封顶
        if (dailyCap != null && fee.compareTo(dailyCap) > 0) {
            fee = dailyCap;
        }

        return fee;
    }

    /**
     * 增加共享次数
     */
    public void incrementShareCount() {
        this.totalShares = totalShares != null ? totalShares + 1 : 1;
    }

    /**
     * 增加成功共享次数
     *
     * @param income 收入
     */
    public void incrementSuccessfulShare(BigDecimal income) {
        this.successfulShares = successfulShares != null ? successfulShares + 1 : 1;
        if (income != null) {
            this.totalIncome = totalIncome != null ? totalIncome.add(income) : income;
        }
    }

    /**
     * 更新评分
     *
     * @param newRating 新评分
     */
    public void updateRating(BigDecimal newRating) {
        if (rating == null || ratingCount == null || ratingCount == 0) {
            this.rating = newRating;
            this.ratingCount = 1;
        } else {
            BigDecimal totalScore = rating.multiply(new BigDecimal(ratingCount));
            this.ratingCount = ratingCount + 1;
            this.rating = totalScore.add(newRating).divide(new BigDecimal(ratingCount), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 获取共享成功率
     *
     * @return 成功率
     */
    public Double getSuccessRate() {
        if (totalShares == null || totalShares == 0) {
            return 0.0;
        }
        return (double) successfulShares / totalShares;
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        this.viewCount = viewCount != null ? viewCount + 1 : 1;
    }

    /**
     * 增加收藏次数
     */
    public void incrementFavoriteCount() {
        this.favoriteCount = favoriteCount != null ? favoriteCount + 1 : 1;
    }

    /**
     * 减少收藏次数
     */
    public void decrementFavoriteCount() {
        if (favoriteCount != null && favoriteCount > 0) {
            this.favoriteCount--;
        }
    }
}
