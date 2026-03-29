package com.im.live.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直播商品实体
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "live_product")
public class LiveProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 直播间ID */
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /** 商品ID */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 商品名称 */
    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    /** 商品主图 */
    @Column(name = "product_image", length = 500)
    private String productImage;

    /** 商品描述 */
    @Column(name = "description", length = 2000)
    private String description;

    /** 原价 */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /** 直播价 */
    @Column(name = "live_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal livePrice;

    /** 库存数量 */
    @Column(name = "stock")
    private Integer stock;

    /** 已售数量 */
    @Column(name = "sold_count")
    private Integer soldCount;

    /** 商品状态：0-下架 1-上架 2-讲解中 3-售罄 */
    @Column(name = "status")
    private Integer status;

    /** 排序号 */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /** 是否限购 */
    @Column(name = "is_limited")
    private Boolean isLimited;

    /** 限购数量 */
    @Column(name = "limit_count")
    private Integer limitCount;

    /** 是否秒杀商品 */
    @Column(name = "is_seckill")
    private Boolean isSeckill;

    /** 秒杀开始时间 */
    @Column(name = "seckill_start_time")
    private LocalDateTime seckillStartTime;

    /** 秒杀结束时间 */
    @Column(name = "seckill_end_time")
    private LocalDateTime seckillEndTime;

    /** 讲解开始时间 */
    @Column(name = "explain_start_time")
    private LocalDateTime explainStartTime;

    /** 讲解视频回放地址 */
    @Column(name = "explain_video_url", length = 1000)
    private String explainVideoUrl;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
