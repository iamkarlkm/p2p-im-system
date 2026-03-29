package com.im.backend.modules.miniprogram.developer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 小程序模板实体
 * 低代码平台预设模板
 */
@Data
@TableName("mini_program_template")
public class MiniProgramTemplate {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 模板唯一标识 */
    private String templateKey;
    
    /** 模板名称 */
    private String templateName;
    
    /** 模板描述 */
    private String description;
    
    /** 行业分类：catering-餐饮 retail-零售 service-服务 entertainment-娱乐 */
    private String industry;
    
    /** 预览图URL */
    private String previewImages;
    
    /** 模板配置JSON */
    private String templateConfig;
    
    /** 页面配置列表 */
    private String pages;
    
    /** 全局样式配置 */
    private String globalStyle;
    
    /** 使用次数 */
    private Integer usageCount;
    
    /** 排序序号 */
    private Integer sortOrder;
    
    /** 是否推荐 */
    private Boolean isRecommended;
    
    /** 状态：0-下架 1-上架 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
