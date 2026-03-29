package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价实体类
 * 支持多维度评分、图文评价、短视频评价
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_review")
public class MerchantReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评价ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** POI兴趣点ID */
    private Long poiId;

    /** 用户ID */
    private Long userId;

    /** 订单ID（可选，关联消费订单） */
    private Long orderId;

    /** 总体星级评分（1-5分，支持半星） */
    private BigDecimal overallRating;

    /** 评价标题 */
    private String title;

    /** 评价内容 */
    private String content;

    /** 评价类型：1-文字评价 2-图文评价 3-短视频评价 */
    private Integer reviewType;

    /** 消费金额 */
    private BigDecimal consumptionAmount;

    /** 消费时间 */
    private LocalDateTime consumptionTime;

    /** 人均消费 */
    private BigDecimal perCapitaAmount;

    /** 是否推荐：0-不推荐 1-推荐 */
    private Integer isRecommended;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 浏览数 */
    private Integer viewCount;

    /** 是否置顶：0-正常 1-置顶 */
    private Integer isTop;

    /** 置顶权重（越大越靠前） */
    private Integer topWeight;

    /** 优质评价标记：0-普通 1-优质 */
    private Integer isQuality;

    /** 优质评分（算法计算） */
    private BigDecimal qualityScore;

    /** 虚假评价标记：0-正常 1-疑似虚假 2-确认虚假 */
    private Integer fakeFlag;

    /** 虚假评价置信度 */
    private BigDecimal fakeConfidence;

    /** 评价状态：0-待审核 1-已通过 2-已拒绝 3-已删除 */
    private Integer status;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 审核人ID */
    private Long auditorId;

    /** 审核备注 */
    private String auditRemark;

    /** 申诉状态：0-无申诉 1-申诉中 2-申诉成功 3-申诉失败 */
    private Integer appealStatus;

    /** 申诉时间 */
    private LocalDateTime appealTime;

    /** 申诉理由 */
    private String appealReason;

    /** IP地址 */
    private String ipAddress;

    /** 设备信息 */
    private String deviceInfo;

    /** 是否匿名：0-实名 1-匿名 */
    private Integer isAnonymous;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标记 */
    @TableLogic
    private Integer deleted;

    /** 非持久化字段：评价维度列表 */
    @TableField(exist = false)
    private List<ReviewDimension> dimensionList;

    /** 非持久化字段：媒体列表 */
    @TableField(exist = false)
    private List<ReviewMedia> mediaList;

    /** 非持久化字段：回复列表 */
    @TableField(exist = false)
    private List<ReviewReply> replyList;

    /** 非持久化字段：用户信息 */
    @TableField(exist = false)
    private String userNickname;

    @TableField(exist = false)
    private String userAvatar;

    /** 非持久化字段：商户信息 */
    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String merchantLogo;

    /** 非持久化字段：POI信息 */
    @TableField(exist = false)
    private String poiName;

    @TableField(exist = false)
    private String poiAddress;

    /**
     * 计算优质评价得分
     * 基于内容长度、图片数量、点赞数、回复数等多维度计算
     */
    public void calculateQualityScore() {
        BigDecimal score = BigDecimal.ZERO;

        // 基础分：总体评分权重
        if (this.overallRating != null) {
            score = score.add(this.overallRating.multiply(new BigDecimal("0.2")));
        }

        // 内容质量分：评价内容长度权重（最多200字满分）
        if (this.content != null) {
            int contentLength = this.content.length();
            BigDecimal contentScore = BigDecimal.valueOf(Math.min(contentLength, 200))
                    .divide(new BigDecimal("200"), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("0.25"));
            score = score.add(contentScore);
        }

        // 多媒体分：图片/视频数量权重
        int mediaCount = (mediaList != null) ? mediaList.size() : 0;
        BigDecimal mediaScore = BigDecimal.valueOf(Math.min(mediaCount, 9))
                .divide(new BigDecimal("9"), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("0.2"));
        score = score.add(mediaScore);

        // 互动分：点赞数和回复数权重
        int totalInteraction = (likeCount != null ? likeCount : 0) +
                (replyCount != null ? replyCount : 0);
        BigDecimal interactionScore = BigDecimal.valueOf(Math.min(totalInteraction, 50))
                .divide(new BigDecimal("50"), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("0.15"));
        score = score.add(interactionScore);

        // 消费验证分：有订单关联加分
        if (orderId != null && orderId > 0) {
            score = score.add(new BigDecimal("0.1"));
        }

        // 推荐分：是否推荐加分
        if (isRecommended != null && isRecommended == 1) {
            score = score.add(new BigDecimal("0.1"));
        }

        this.qualityScore = score.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);

        // 优质评价阈值：综合得分>=75分
        this.isQuality = (this.qualityScore.compareTo(new BigDecimal("75")) >= 0) ? 1 : 0;
    }

    /**
     * 检测疑似虚假评价
     * 基于内容特征、用户行为、时间分布等维度分析
     */
    public void detectFakeReview() {
        BigDecimal fakeScore = BigDecimal.ZERO;
        StringBuilder reason = new StringBuilder();

        // 1. 内容特征分析
        if (content != null) {
            // 内容过短（少于10字）
            if (content.length() < 10) {
                fakeScore = fakeScore.add(new BigDecimal("0.15"));
                reason.append("内容过短;");
            }

            // 重复内容检测（相同字符占比过高）
            long uniqueChars = content.chars().distinct().count();
            if (content.length() > 0) {
                double uniqueRatio = (double) uniqueChars / content.length();
                if (uniqueRatio < 0.3) {
                    fakeScore = fakeScore.add(new BigDecimal("0.2"));
                    reason.append("重复字符过多;");
                }
            }

            // 敏感词检测（刷单常用语）
            String[] sensitiveWords = {"好评返现", "刷单", "五星好评", "全额返现"};
            for (String word : sensitiveWords) {
                if (content.contains(word)) {
                    fakeScore = fakeScore.add(new BigDecimal("0.25"));
                    reason.append("含敏感词:").append(word).append(";");
                    break;
                }
            }
        }

        // 2. 评分异常分析
        if (overallRating != null) {
            // 极端评分（全5星或全1星）
            if (overallRating.doubleValue() == 5.0 || overallRating.doubleValue() == 1.0) {
                fakeScore = fakeScore.add(new BigDecimal("0.1"));
                reason.append("极端评分;");
            }
        }

        // 3. 媒体异常分析
        if (mediaList != null) {
            // 无文字只有图片
            if ((content == null || content.length() < 5) && mediaList.size() > 5) {
                fakeScore = fakeScore.add(new BigDecimal("0.15"));
                reason.append("无文字多图片;");
            }
        }

        // 4. 时间异常分析
        if (createTime != null && consumptionTime != null) {
            // 消费后立刻评价（少于5分钟）
            long minutesAfter = java.time.Duration.between(consumptionTime, createTime).toMinutes();
            if (minutesAfter < 5) {
                fakeScore = fakeScore.add(new BigDecimal("0.15"));
                reason.append("消费后立刻评价;");
            }
        }

        this.fakeConfidence = fakeScore.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);

        // 虚假评价阈值判定
        if (fakeScore.compareTo(new BigDecimal("0.6")) >= 0) {
            this.fakeFlag = 2; // 确认虚假
        } else if (fakeScore.compareTo(new BigDecimal("0.3")) >= 0) {
            this.fakeFlag = 1; // 疑似虚假
        } else {
            this.fakeFlag = 0; // 正常
        }
    }

    /**
     * 检查评价是否可申诉
     */
    public boolean canAppeal() {
        // 被拒绝的评价或标记为虚假的评价可以申诉
        return status == 2 || fakeFlag > 0;
    }

    /**
     * 提交申诉
     */
    public void submitAppeal(String appealReason) {
        if (!canAppeal()) {
            throw new IllegalStateException("当前评价状态不可申诉");
        }
        this.appealStatus = 1;
        this.appealTime = LocalDateTime.now();
        this.appealReason = appealReason;
    }

    /**
     * 处理申诉
     */
    public void processAppeal(boolean approved, String remark) {
        if (appealStatus != 1) {
            throw new IllegalStateException("当前无待处理申诉");
        }
        this.appealStatus = approved ? 2 : 3;
        this.auditRemark = remark;
        this.auditTime = LocalDateTime.now();

        // 申诉成功，恢复评价状态
        if (approved) {
            if (fakeFlag == 2) {
                this.fakeFlag = 1; // 降级为疑似
            }
            this.status = 1; // 恢复为已通过
        }
    }

    /**
     * 增加浏览数
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0) + 1;
    }

    /**
     * 增加点赞数
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount != null ? this.likeCount : 0) + 1;
    }

    /**
     * 减少点赞数
     */
    public void decrementLikeCount() {
        this.likeCount = Math.max(0, (this.likeCount != null ? this.likeCount : 0) - 1);
    }

    /**
     * 增加回复数
     */
    public void incrementReplyCount() {
        this.replyCount = (this.replyCount != null ? this.replyCount : 0) + 1;
    }

    /**
     * 置顶评价
     */
    public void setTop(int weight) {
        this.isTop = 1;
        this.topWeight = weight;
    }

    /**
     * 取消置顶
     */
    public void cancelTop() {
        this.isTop = 0;
        this.topWeight = 0;
    }

    /**
     * 审核通过
     */
    public void approve(Long auditorId) {
        this.status = 1;
        this.auditorId = auditorId;
        this.auditTime = LocalDateTime.now();
    }

    /**
     * 审核拒绝
     */
    public void reject(Long auditorId, String remark) {
        this.status = 2;
        this.auditorId = auditorId;
        this.auditTime = LocalDateTime.now();
        this.auditRemark = remark;
    }

    // ============ 静态常量 ============

    /** 评价类型 */
    public static final int REVIEW_TYPE_TEXT = 1;      // 文字评价
    public static final int REVIEW_TYPE_IMAGE = 2;     // 图文评价
    public static final int REVIEW_TYPE_VIDEO = 3;     // 短视频评价

    /** 推荐状态 */
    public static final int NOT_RECOMMENDED = 0;       // 不推荐
    public static final int RECOMMENDED = 1;           // 推荐

    /** 置顶状态 */
    public static final int NOT_TOP = 0;               // 未置顶
    public static final int IS_TOP = 1;                // 已置顶

    /** 优质评价 */
    public static final int NOT_QUALITY = 0;           // 普通
    public static final int IS_QUALITY = 1;            // 优质

    /** 虚假评价标记 */
    public static final int FAKE_NORMAL = 0;           // 正常
    public static final int FAKE_SUSPECTED = 1;        // 疑似虚假
    public static final int FAKE_CONFIRMED = 2;        // 确认虚假

    /** 评价状态 */
    public static final int STATUS_PENDING = 0;        // 待审核
    public static final int STATUS_APPROVED = 1;       // 已通过
    public static final int STATUS_REJECTED = 2;       // 已拒绝
    public static final int STATUS_DELETED = 3;        // 已删除

    /** 申诉状态 */
    public static final int APPEAL_NONE = 0;           // 无申诉
    public static final int APPEAL_PENDING = 1;        // 申诉中
    public static final int APPEAL_SUCCESS = 2;        // 申诉成功
    public static final int APPEAL_FAILED = 3;         // 申诉失败

    /** 匿名状态 */
    public static final int NOT_ANONYMOUS = 0;         // 实名
    public static final int IS_ANONYMOUS = 1;          // 匿名
}
