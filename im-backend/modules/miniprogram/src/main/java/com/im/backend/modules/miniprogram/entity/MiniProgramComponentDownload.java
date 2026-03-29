package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 组件下载/购买记录
 */
@Data
@TableName("mini_program_component_download")
public class MiniProgramComponentDownload {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 组件ID
     */
    private Long componentId;

    /**
     * 下载用户ID
     */
    private Long userId;

    /**
     * 购买价格
     */
    private BigDecimal price;

    /**
     * 下载类型：1-免费下载 2-付费购买
     */
    private Integer downloadType;

    /**
     * 下载的组件版本
     */
    private String version;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下载时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
