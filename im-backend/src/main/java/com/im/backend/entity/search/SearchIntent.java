package com.im.backend.entity.search;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索意图实体类
 * 存储NLP解析后的搜索意图信息
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_search_intent")
public class SearchIntent {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的语义查询ID
     */
    private Long semanticQueryId;
    
    /**
     * 主要意图类型
     * NAVIGATION: 导航到店
     * GROUP_BUY: 团购优惠
     * RESERVATION: 预约订座
     * PRICE_COMPARE: 价格比较
     * DETAIL: 查看详情
     * REVIEW: 查看评价
     * PHOTO: 查看图片
     * PHONE: 拨打电话
     * FAVORITE: 收藏
     * SHARE: 分享
     * NEARBY: 附近推荐
     * GENERAL: 通用搜索
     */
    private String primaryIntent;
    
    /**
     * 意图置信度（0-1）
     */
    private Double confidence;
    
    /**
     * 次要意图列表（JSON格式）
     */
    private String secondaryIntents;
    
    /**
     * 意图槽位填充结果（JSON格式）
     */
    private String slotValues;
    
    /**
     * 是否需要澄清
     */
    private Boolean needsClarification;
    
    /**
     * 澄清问题文本
     */
    private String clarificationQuestion;
    
    /**
     * 实体识别结果（JSON格式）
     */
    private String entities;
    
    /**
     * 情感倾向
     * POSITIVE: 积极
     * NEGATIVE: 消极
     * NEUTRAL: 中性
     */
    private String sentiment;
    
    /**
     * 紧急程度
     * URGENT: 紧急
     * NORMAL: 普通
     * CASUAL: 随意
     */
    private String urgency;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // ========== 意图常量定义 ==========
    
    public static final String INTENT_NAVIGATION = "NAVIGATION";
    public static final String INTENT_GROUP_BUY = "GROUP_BUY";
    public static final String INTENT_RESERVATION = "RESERVATION";
    public static final String INTENT_PRICE_COMPARE = "PRICE_COMPARE";
    public static final String INTENT_DETAIL = "DETAIL";
    public static final String INTENT_REVIEW = "REVIEW";
    public static final String INTENT_PHOTO = "PHOTO";
    public static final String INTENT_PHONE = "PHONE";
    public static final String INTENT_FAVORITE = "FAVORITE";
    public static final String INTENT_SHARE = "SHARE";
    public static final String INTENT_NEARBY = "NEARBY";
    public static final String INTENT_GENERAL = "GENERAL";
    
    // ========== 业务方法 ==========
    
    /**
     * 判断是否为导航意图
     */
    public boolean isNavigationIntent() {
        return INTENT_NAVIGATION.equals(primaryIntent);
    }
    
    /**
     * 判断是否为团购意图
     */
    public boolean isGroupBuyIntent() {
        return INTENT_GROUP_BUY.equals(primaryIntent);
    }
    
    /**
     * 判断是否为预约意图
     */
    public boolean isReservationIntent() {
        return INTENT_RESERVATION.equals(primaryIntent);
    }
    
    /**
     * 判断是否需要立即响应
     */
    public boolean requiresImmediateResponse() {
        return "URGENT".equals(urgency) || isNavigationIntent();
    }
    
    /**
     * 获取意图可靠性等级
     */
    public String getReliabilityLevel() {
        if (confidence >= 0.9) return "HIGH";
        if (confidence >= 0.7) return "MEDIUM";
        return "LOW";
    }
    
    /**
     * 判断是否需要人工介入
     */
    public boolean needsHumanIntervention() {
        return confidence < 0.5 || Boolean.TRUE.equals(needsClarification);
    }
    
    /**
     * 获取建议的响应类型
     */
    public String getSuggestedResponseType() {
        return switch (primaryIntent) {
            case INTENT_NAVIGATION -> "map_navigation";
            case INTENT_GROUP_BUY -> "deal_list";
            case INTENT_RESERVATION -> "booking_form";
            case INTENT_PRICE_COMPARE -> "price_comparison";
            case INTENT_DETAIL -> "detail_page";
            case INTENT_REVIEW -> "review_list";
            case INTENT_NEARBY -> "nearby_recommendation";
            default -> "general_result";
        };
    }
}
