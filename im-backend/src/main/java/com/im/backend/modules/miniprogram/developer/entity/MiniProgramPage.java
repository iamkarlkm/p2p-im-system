package com.im.backend.modules.miniprogram.developer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 小程序页面配置实体
 * 可视化编辑器生成的页面配置
 */
@Data
@TableName("mini_program_page")
public class MiniProgramPage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 所属项目ID */
    private Long projectId;
    
    /** 页面唯一标识 */
    private String pageKey;
    
    /** 页面名称 */
    private String pageName;
    
    /** 页面标题 */
    private String pageTitle;
    
    /** 页面路径 */
    private String pagePath;
    
    /** 页面类型：home-首页 category-分类 cart-购物车 profile-个人中心 custom-自定义 */
    private String pageType;
    
    /** 页面布局配置JSON */
    private String layoutConfig;
    
    /** 组件列表JSON */
    private String components;
    
    /** 数据源绑定配置 */
    private String dataBinding;
    
    /** 事件配置 */
    private String eventConfig;
    
    /** 样式配置 */
    private String styleConfig;
    
    /** 排序序号 */
    private Integer sortOrder;
    
    /** 是否首页 */
    private Boolean isHome;
    
    /** 是否启用 */
    private Boolean isEnabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
