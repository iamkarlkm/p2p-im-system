package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 砍价帮助记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BargainHelpRecord {

    /** 帮助记录ID */
    private Long id;

    /** 砍价ID */
    private Long bargainId;

    /** 帮助用户ID */
    private Long helperId;

    /** 砍价金额 */
    private BigDecimal bargainAmount;

    /** 是否为新人助力(额外优惠) */
    private Integer isNewUser;

    /** 助力时间 */
    private LocalDateTime helpTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
