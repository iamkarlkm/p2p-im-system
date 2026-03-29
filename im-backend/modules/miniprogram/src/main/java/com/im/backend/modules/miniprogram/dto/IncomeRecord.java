package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收益记录
 */
@Data
public class IncomeRecord {

    private Long id;

    /**
     * 类型：1-组件销售 2-模板销售 3-其他
     */
    private Integer type;

    private String typeDesc;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 来源名称
     */
    private String sourceName;

    /**
     * 购买者
     */
    private String buyerName;

    /**
     * 时间
     */
    private LocalDateTime createTime;
}
