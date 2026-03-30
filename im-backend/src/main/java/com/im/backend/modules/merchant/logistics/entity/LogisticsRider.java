package com.im.backend.modules.merchant.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 配送骑手实体 - 功能#311: 本地物流配送调度
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("logistics_rider")
public class LogisticsRider {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 骑手姓名 */
    private String realName;

    /** 身份证号 */
    private String idCard;

    /** 手机号 */
    private String phone;

    /** 工作状态: 0-离线, 1-在线空闲, 2-在线忙碌 */
    private Integer workStatus;

    /** 当前经度 */
    private BigDecimal currentLng;

    /** 当前纬度 */
    private BigDecimal currentLat;

    /** 位置更新时间 */
    private LocalDateTime locationUpdateTime;

    /** 今日接单数 */
    private Integer todayOrderCount;

    /** 今日收入 */
    private BigDecimal todayIncome;

    /** 总评分 */
    private BigDecimal rating;

    /** 评分次数 */
    private Integer ratingCount;

    /** 审核状态: 0-待审核, 1-已通过, 2-已拒绝 */
    private Integer auditStatus;

    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
