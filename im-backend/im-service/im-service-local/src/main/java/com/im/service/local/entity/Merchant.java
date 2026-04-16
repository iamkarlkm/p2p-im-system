package com.im.service.local.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家实体
 */
@Data
@Entity
@Table(name = "im_local_merchant", indexes = {
    @Index(name = "idx_merchant_category", columnList = "categoryId"),
    @Index(name = "idx_merchant_location", columnList = "longitude,latitude")
})
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 商家名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 商家描述 */
    @Column(length = 1000)
    private String description;

    /** 商家logo */
    @Column(length = 500)
    private String logo;

    /** 分类ID */
    @Column(length = 36)
    private String categoryId;

    /** 地址 */
    @Column(length = 200)
    private String address;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 联系电话 */
    @Column(length = 20)
    private String phone;

    /** 营业时间 */
    @Column(length = 100)
    private String businessHours;

    /** 平均消费 */
    private BigDecimal averageCost;

    /** 评分 */
    private BigDecimal rating;

    /** 评分人数 */
    private Integer ratingCount;

    /** 状态: ACTIVE, INACTIVE, CLOSED */
    @Column(length = 20)
    private String status;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
        if (rating == null) rating = BigDecimal.ZERO;
        if (ratingCount == null) ratingCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
