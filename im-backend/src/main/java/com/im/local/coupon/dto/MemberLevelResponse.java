package com.im.local.coupon.dto;

import lombok.Data;

/**
 * 会员等级响应DTO
 */
@Data
public class MemberLevelResponse {

    /** 等级ID */
    private Long id;

    /** 等级名称 */
    private String name;

    /** 等级级别 */
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

    /** 等级权益 */
    private String benefits;

    /** 排序权重 */
    private Integer sortOrder;
}
