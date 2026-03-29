package com.im.entity.poiim;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能客服知识库实体
 * 用于自动问答
 */
@Data
public class AgentKnowledgeBase {
    
    /** 知识条目ID */
    private String knowledgeId;
    
    /** 所属POI ID */
    private String poiId;
    
    /** 问题分类: HOURS-营业时间, PRICE-价格, PARKING-停车, QUEUE-排队, SERVICE-服务, LOCATION-位置, OTHER-其他 */
    private String category;
    
    /** 问题关键词列表 */
    private List<String> keywords;
    
    /** 标准问题 */
    private String question;
    
    /** 标准答案 */
    private String answer;
    
    /** 相似问题变体 */
    private List<String> similarQuestions;
    
    /** 答案类型: TEXT-文本, RICH-富文本, CARD-卡片, LINK-链接 */
    private String answerType;
    
    /** 富文本答案内容 */
    private Map<String, Object> richContent;
    
    /** 关联操作(预约/排队/导航等) */
    private String relatedAction;
    
    /** 点击率(用于排序) */
    private Integer clickCount;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
