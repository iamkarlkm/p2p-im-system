package com.im.live.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 直播间信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomDTO {

    private Long id;
    private String title;
    private String coverImage;
    private Long anchorId;
    private String anchorName;
    private String anchorAvatar;
    private Long merchantId;
    private Integer status;
    private String statusText;
    private String playUrl;
    private String backupPlayUrl;
    private Integer roomType;
    private String category;
    private String description;
    private LocalDateTime plannedStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private Integer onlineCount;
    private Integer totalViewCount;
    private Integer likeCount;
    private Integer shareCount;
    private Integer duration;
    private Boolean allowComment;
    private Boolean allowLike;
    private Boolean allowShare;
    private String locationName;
    private LocalDateTime createTime;
    
    /** 是否已预约 */
    private Boolean isSubscribed;
    
    /** 直播商品数量 */
    private Integer productCount;
    
    /** 优惠券列表 */
    private List<LiveCouponDTO> coupons;

    /**
     * 直播优惠券DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LiveCouponDTO {
        private Long couponId;
        private String title;
        private String discount;
        private BigDecimal minConsume;
        private BigDecimal discountAmount;
    }
}
