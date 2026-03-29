package com.im.entity.fencemessage;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 围栏消息模板实体
 * 定义围栏触发时发送的消息模板
 */
@Data
public class FenceMessageTemplate {
    
    /** 模板ID */
    private String templateId;
    
    /** 模板名称 */
    private String templateName;
    
    /** 模板类型: WELCOME-欢迎, COUPON-优惠券, SERVICE-服务, SURVEY-调查, PROMOTION-促销, REMINDER-提醒 */
    private String templateType;
    
    /** 触发场景: ENTER-进入, DWELL-停留, EXIT-离开, SCHEDULE-定时 */
    private String triggerScene;
    
    /** 关联POI分类(可多选) */
    private List<String> poiCategories;
    
    /** 消息标题模板(支持变量如${poiName}) */
    private String titleTemplate;
    
    /** 消息内容模板 */
    private String contentTemplate;
    
    /** 消息卡片类型: TEXT-纯文本, COUPON-优惠券, SERVICE-服务卡片, PRODUCT-商品卡片, NAVIGATION-导航卡片 */
    private String cardType;
    
    /** 卡片数据模板 */
    private Map<String, Object> cardDataTemplate;
    
    /** 跳转链接 */
    private String actionUrl;
    
    /** 跳转类型: MINI_APP-小程序, H5-H5页面, NATIVE-原生页面 */
    private String actionType;
    
    /** 推送渠道: APP_PUSH-应用推送, SMS-短信, WECHAT-微信, IN_APP-应用内, ALL-全渠道 */
    private String pushChannel;
    
    /** 消息优先级: HIGH-高, NORMAL-普通, LOW-低 */
    private String priority;
    
    /** 去重时间窗口(分钟,0表示不去重) */
    private Integer dedupWindowMinutes;
    
    /** 生效时间段配置 */
    private Map<String, Object> timeRules;
    
    /** 目标人群标签 */
    private List<String> targetUserTags;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 创建者 */
    private String createBy;
}
