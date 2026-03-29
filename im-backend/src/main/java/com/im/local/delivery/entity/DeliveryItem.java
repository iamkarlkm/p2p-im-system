package com.im.local.delivery.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * 配送商品项
 */
@Data
@Embeddable
public class DeliveryItem {
    
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
    private String specifications;
}
