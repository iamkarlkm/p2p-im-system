package com.im.live.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直播商品DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveProductDTO {

    private Long id;
    private Long roomId;
    private Long productId;
    private String productName;
    private String productImage;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal livePrice;
    private Integer stock;
    private Integer soldCount;
    private Integer status;
    private String statusText;
    private Integer sortOrder;
    private Boolean isLimited;
    private Integer limitCount;
    private Boolean isSeckill;
    private LocalDateTime seckillStartTime;
    private LocalDateTime seckillEndTime;
    private LocalDateTime explainStartTime;
    private String explainVideoUrl;
    private LocalDateTime createTime;

    /** 折扣率 */
    private Integer discountRate;

    /** 是否正在讲解 */
    private Boolean isExplaining;

    /** 是否可购买 */
    private Boolean canBuy;
}
