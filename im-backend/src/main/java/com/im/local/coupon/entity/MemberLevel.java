package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会员等级实体
 * 本地生活会员营销体系 - 会员等级配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLevel {

    /** 等级ID */
    private Long id;

    /** 商户ID(0表示平台通用等级) */
    private Long merchantId;

    /** 等级名称 */
    private String name;

    /** 等级级别(数字越大等级越高) */
    private Integer level;

    /** 等级图标 */
    private String icon;

    /** 等级背景图 */
    private String backgroundImage;

    /** 升级所需成长值 */
    private Integer requiredGrowth;

    /** 等级颜色 */
    private String color;

    /** 等级描述 */
    private String description;

    /** 等级权益列表(JSON格式) */
    private String benefits;

    /** 是否显示: 0-隐藏 1-显示 */
    private Integer visible;

    /** 排序权重 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
