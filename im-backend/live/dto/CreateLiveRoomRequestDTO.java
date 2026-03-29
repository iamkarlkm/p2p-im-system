package com.im.live.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建直播间请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLiveRoomRequestDTO {

    /** 直播间标题 */
    @NotBlank(message = "直播间标题不能为空")
    private String title;

    /** 直播间封面 */
    private String coverImage;

    /** 直播分类 */
    @NotBlank(message = "直播分类不能为空")
    private String category;

    /** 房间类型：1-普通直播 2-带货直播 3-活动直播 */
    @NotNull(message = "房间类型不能为空")
    private Integer roomType;

    /** 直播简介 */
    private String description;

    /** 计划开始时间 */
    private LocalDateTime plannedStartTime;

    /** 是否允许评论 */
    private Boolean allowComment = true;

    /** 是否允许点赞 */
    private Boolean allowLike = true;

    /** 是否允许分享 */
    private Boolean allowShare = true;

    /** 评论审核：0-无需审核 1-需要审核 */
    private Integer commentAudit = 0;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** 位置名称 */
    private String locationName;

    /** 直播商品列表 */
    private List<LiveProductDTO> products;

    /**
     * 直播商品DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LiveProductDTO {
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal originalPrice;
        private BigDecimal livePrice;
        private Integer stock;
        private Boolean isLimited;
        private Integer limitCount;
        private Boolean isSeckill;
        private LocalDateTime seckillStartTime;
        private LocalDateTime seckillEndTime;
    }
}
