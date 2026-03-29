package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价实体类
 * 支持多维度评价体系：星级评分 + 细评维度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_review")
public class MerchantReview extends BaseEntity {

    /** 评价ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** POI兴趣点ID */
    private Long poiId;

    /** 用户ID */
    private Long userId;

    /** 订单ID（可选，关联消费订单） */
    private Long orderId;

    /** 综合星级评分（1-5分，支持半星） */
    private BigDecimal overallRating;

    /** 口味评分（1-5分，餐饮类商户） */
    private BigDecimal tasteRating;

    /** 环境评分（1-5分） */
    private BigDecimal environmentRating;

    /** 服务评分（1-5分） */
    private BigDecimal serviceRating;

    /** 性价比评分（1-5分） */
    private BigDecimal valueRating;

    /** 评价内容文本 */
    private String content;

    /** 评价图片URLs（JSON数组存储） */
    private String images;

    /** 评价视频URL */
    private String videoUrl;

    /** 视频封面图 */
    private String videoCover;

    /** 视频时长（秒） */
    private Integer videoDuration;

    /** 消费金额 */
    private BigDecimal consumptionAmount;

    /** 人均消费 */
    private BigDecimal perCapitaCost;

    /** 是否匿名评价 */
    private Boolean anonymous;

    /** 评价类型：1-图文评价 2-视频评价 */
    private Integer reviewType;

    /** 评价来源：1-APP 2-小程序 3-H5 */
    private Integer source;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 浏览数 */
    private Integer viewCount;

    /** 是否推荐（平台优质评价标记） */
    private Boolean recommended;

    /** 推荐权重（用于排序） */
    private Integer recommendWeight;

    /** 评价状态：0-待审核 1-已通过 2-已拒绝 3-已删除 */
    private Integer status;

    /** 审核备注 */
    private String auditRemark;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 审核人ID */
    private Long auditBy;

    /** 是否置顶 */
    private Boolean pinned;

    /** 置顶排序 */
    private Integer pinOrder;

    /** 用餐时间（餐饮类） */
    private LocalDateTime diningTime;

    /** 用餐人数 */
    private Integer diningPeople;

    /** 标签（JSON数组：口味赞、环境好等） */
    private String tags;

    /** 是否体验过 */
    private Boolean experienced;

    /** 体验方式：1-到店消费 2-外卖 3-预约到店 */
    private Integer experienceType;

    /** 地理位置-经度 */
    private BigDecimal longitude;

    /** 地理位置-纬度 */
    private BigDecimal latitude;

    /** 地理位置名称 */
    private String locationName;

    /** 是否已同步到ES */
    private Boolean syncedToEs;

    /** 同步ES时间 */
    private LocalDateTime esSyncTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    // ========== 非持久化字段 ==========

    /** 用户昵称 */
    @TableField(exist = false)
    private String userNickname;

    /** 用户头像 */
    @TableField(exist = false)
    private String userAvatar;

    /** 用户等级 */
    @TableField(exist = false)
    private Integer userLevel;

    /** 商户名称 */
    @TableField(exist = false)
    private String merchantName;

    /** 商户Logo */
    @TableField(exist = false)
    private String merchantLogo;

    /** 是否已点赞 */
    @TableField(exist = false)
    private Boolean hasLiked;

    /** 图片列表（解析后的） */
    @TableField(exist = false)
    private List<String> imageList;

    /** 标签列表（解析后的） */
    @TableField(exist = false)
    private List<String> tagList;

    /** 回复列表 */
    @TableField(exist = false)
    private List<MerchantReviewReply> replies;

    /**
     * 计算平均分
     */
    public BigDecimal calculateAverageRating() {
        if (tasteRating == null && environmentRating == null && 
            serviceRating == null && valueRating == null) {
            return overallRating;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        
        if (tasteRating != null) {
            sum = sum.add(tasteRating);
            count++;
        }
        if (environmentRating != null) {
            sum = sum.add(environmentRating);
            count++;
        }
        if (serviceRating != null) {
            sum = sum.add(serviceRating);
            count++;
        }
        if (valueRating != null) {
            sum = sum.add(valueRating);
            count++;
        }
        
        if (count == 0) {
            return overallRating;
        }
        
        return sum.divide(new BigDecimal(count), 1, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取星级文本描述
     */
    public String getRatingText() {
        if (overallRating == null) return "暂无评分";
        double rating = overallRating.doubleValue();
        if (rating >= 4.5) return "极佳";
        if (rating >= 4.0) return "非常好";
        if (rating >= 3.5) return "不错";
        if (rating >= 3.0) return "一般";
        if (rating >= 2.0) return "较差";
        return "很差";
    }

    /**
     * 是否为优质评价（字数+图片/视频）
     */
    public boolean isHighQuality() {
        boolean hasEnoughText = content != null && content.length() >= 50;
        boolean hasMedia = (images != null && !images.isEmpty()) || 
                          (videoUrl != null && !videoUrl.isEmpty());
        return hasEnoughText && hasMedia;
    }

    /**
     * 是否为视频评价
     */
    public boolean isVideoReview() {
        return videoUrl != null && !videoUrl.isEmpty();
    }
}
