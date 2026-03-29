package com.im.backend.modules.miniprogram.developer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 组件市场实体
 * 可复用的组件和插件
 */
@Data
@TableName("component_market")
public class ComponentMarket {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 组件唯一标识 */
    private String componentKey;
    
    /** 组件名称 */
    private String componentName;
    
    /** 组件描述 */
    private String description;
    
    /** 组件分类：basic-基础 layout-布局 form-表单 media-媒体 map-地图 payment-支付 marketing-营销 */
    private String category;
    
    /** 组件图标 */
    private String icon;
    
    /** 预览图URL */
    private String previewImages;
    
    /** 组件代码包 */
    private String codePackage;
    
    /** 默认配置JSON */
    private String defaultConfig;
    
    /** 属性配置JSON */
    private String propsConfig;
    
    /** 事件配置 */
    private String eventConfig;
    
    /** 开发者ID */
    private Long developerId;
    
    /** 开发者名称 */
    private String developerName;
    
    /** 版本号 */
    private String version;
    
    /** 价格（0表示免费） */
    private BigDecimal price;
    
    /** 下载次数 */
    private Integer downloadCount;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 评分人数 */
    private Integer ratingCount;
    
    /** 状态：0-审核中 1-已上架 2-已下架 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
