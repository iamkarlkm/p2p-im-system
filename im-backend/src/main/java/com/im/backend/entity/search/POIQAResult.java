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
 * POI问答结果实体类
 * 存储智能问答系统对POI相关问题的回答
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_poi_qa_result")
public class POIQAResult {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * POI ID
     */
    private Long poiId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户问题
     */
    private String userQuestion;
    
    /**
     * 问题类型
     * BUSINESS_HOURS: 营业时间
     * PRICE_RANGE: 人均消费
     * SPECIALTY: 特色菜/特色服务
     * PARKING: 停车信息
     * QUEUE: 排队情况
     * RECOMMENDATION: 推荐菜/推荐服务
     * LOCATION: 位置/交通
     * CONTACT: 联系方式
     * FACILITY: 设施服务
     * COMPARISON: 对比询问
     * GENERAL: 一般询问
     */
    private String questionType;
    
    /**
     * 系统回答
     */
    private String systemAnswer;
    
    /**
     * 回答详细内容（JSON格式，包含多维度信息）
     */
    private String answerDetails;
    
    /**
     * 相关图片URL列表（JSON格式）
     */
    private String relatedImages;
    
    /**
     * 数据来源
     * KNOWLEDGE_BASE: 知识库
     * POI_PROFILE: POI资料
     * USER_REVIEW: 用户评价
     * REAL_TIME: 实时数据
     * INFERRED: 推理生成
     */
    private String dataSource;
    
    /**
     * 回答置信度
     */
    private Double confidence;
    
    /**
     * 是否实时信息
     */
    private Boolean isRealTimeInfo;
    
    /**
     * 实时信息过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 是否需要转人工
     */
    private Boolean needsHumanTransfer;
    
    /**
     * 转人工原因
     */
    private String transferReason;
    
    /**
     * 用户满意度评分（1-5）
     */
    private Integer userRating;
    
    /**
     * 用户反馈
     */
    private String userFeedback;
    
    /**
     * 回答生成时间
     */
    private LocalDateTime answerTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // ========== 问题类型常量 ==========
    
    public static final String TYPE_BUSINESS_HOURS = "BUSINESS_HOURS";
    public static final String TYPE_PRICE_RANGE = "PRICE_RANGE";
    public static final String TYPE_SPECIALTY = "SPECIALTY";
    public static final String TYPE_PARKING = "PARKING";
    public static final String TYPE_QUEUE = "QUEUE";
    public static final String TYPE_RECOMMENDATION = "RECOMMENDATION";
    public static final String TYPE_LOCATION = "LOCATION";
    public static final String TYPE_CONTACT = "CONTACT";
    public static final String TYPE_FACILITY = "FACILITY";
    public static final String TYPE_COMPARISON = "COMPARISON";
    public static final String TYPE_GENERAL = "GENERAL";
    
    // ========== 业务方法 ==========
    
    /**
     * 判断回答是否有效
     */
    public boolean isValidAnswer() {
        return confidence >= 0.7 && systemAnswer != null && !systemAnswer.isEmpty();
    }
    
    /**
     * 判断是否为实时信息类问题
     */
    public boolean isRealTimeQuestion() {
        return TYPE_QUEUE.equals(questionType) || 
               TYPE_BUSINESS_HOURS.equals(questionType);
    }
    
    /**
     * 判断是否需要更新实时信息
     */
    public boolean needsRealTimeUpdate() {
        if (!Boolean.TRUE.equals(isRealTimeInfo)) return false;
        if (expireTime == null) return false;
        return LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 获取回答质量等级
     */
    public String getQualityLevel() {
        if (confidence >= 0.9 && Boolean.TRUE.equals(isRealTimeInfo)) return "EXCELLENT";
        if (confidence >= 0.8) return "GOOD";
        if (confidence >= 0.6) return "FAIR";
        return "POOR";
    }
    
    /**
     * 构建快捷操作按钮
     */
    public List<String> getSuggestedActions() {
        return switch (questionType) {
            case TYPE_BUSINESS_HOURS -> List.of("导航到店", "立即预约");
            case TYPE_PRICE_RANGE -> List.of("查看菜单", "团购优惠");
            case TYPE_QUEUE -> List.of("在线取号", "附近其他店");
            case TYPE_RECOMMENDATION -> List.of("查看图片", "查看评价");
            case TYPE_LOCATION -> List.of("导航", "查看地图");
            case TYPE_PARKING -> List.of("导航到停车场", "查看周边停车");
            default -> List.of("查看详情", "收藏店铺");
        };
    }
    
    /**
     * 构建相似问题推荐
     */
    public List<String> getRelatedQuestions() {
        return switch (questionType) {
            case TYPE_BUSINESS_HOURS -> List.of("周末营业吗？", "节假日营业吗？", "最早几点开门？");
            case TYPE_PRICE_RANGE -> List.of("有什么优惠活动？", "性价比怎么样？", "有套餐吗？");
            case TYPE_SPECIALTY -> List.of("推荐菜有哪些？", "招牌菜是什么？", "必点菜品？");
            case TYPE_QUEUE -> List.of("现在排队人多吗？", "可以预约吗？", "有包间吗？");
            default -> List.of();
        };
    }
}
