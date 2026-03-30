package com.im.backend.modules.merchant.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 小程序应用实体 - 功能#313: 小程序开发者生态
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mini_program_app")
public class MiniProgramApp {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 小程序ID */
    private String appId;

    /** 小程序名称 */
    private String appName;

    /** 商户ID */
    private Long merchantId;

    /** 图标URL */
    private String iconUrl;

    /** 描述 */
    private String description;

    /** 分类 */
    private String category;

    /** 状态: 0-开发中, 1-审核中, 2-已发布, 3-已下架 */
    private Integer status;

    /** 版本号 */
    private String version;

    /** 代码包URL */
    private String codeUrl;

    /** 配置JSON */
    private String configJson;

    /** 浏览数 */
    private Integer viewCount;

    /** 使用数 */
    private Integer useCount;

    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
