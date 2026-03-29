package com.im.backend.modules.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手实体类
 * 用于存储配送骑手信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("delivery_rider")
public class DeliveryRider implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 骑手ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 骑手编号 */
    private String riderNo;

    /** 用户ID */
    private Long userId;

    /** 真实姓名 */
    private String realName;

    /** 身份证号 */
    private String idCard;

    /** 手机号 */
    private String phone;

    /** 头像 */
    private String avatar;

    /** 工作状态: 0-离线 1-在线空闲 2-在线忙碌 */
    private Integer workStatus;

    /** 审核状态: 0-待审核 1-已通过 2-已拒绝 */
    private Integer auditStatus;

    /** 当前经度 */
    private BigDecimal currentLongitude;

    /** 当前纬度 */
    private BigDecimal currentLatitude;

    /** 位置更新时间 */
    private LocalDateTime locationUpdateTime;

    /** 所在城市 */
    private String city;

    /** 所在区域 */
    private String district;

    /** 今日接单数 */
    private Integer todayOrderCount;

    /** 今日配送距离(米) */
    private Integer todayDistance;

    /** 总接单数 */
    private Integer totalOrderCount;

    /** 评分 */
    private BigDecimal rating;

    /** 等级 */
    private Integer level;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0-正常 1-已删除 */
    @TableLogic
    private Integer deleted;
}
