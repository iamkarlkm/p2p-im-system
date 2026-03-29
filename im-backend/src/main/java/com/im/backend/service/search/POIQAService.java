package com.im.backend.service.search;

import com.im.backend.dto.search.POIQADTO;

/**
 * POI问答服务接口
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface POIQAService {
    
    /**
     * 回答POI相关问题
     * 
     * @param dto 问答请求
     * @return 问答响应
     */
    POIQADTO.Response answerQuestion(POIQADTO dto);
    
    /**
     * 识别问题类型
     * 
     * @param question 问题文本
     * @return 问题类型
     */
    String recognizeQuestionType(String question);
    
    /**
     * 生成回答
     * 
     * @param poiId POI ID
     * @param questionType 问题类型
     * @param question 问题
     * @return 回答内容
     */
    String generateAnswer(Long poiId, String questionType, String question);
    
    /**
     * 获取实时信息
     * 
     * @param poiId POI ID
     * @param infoType 信息类型
     * @return 实时信息
     */
    String getRealTimeInfo(Long poiId, String infoType);
    
    /**
     * 判断是否需要转人工
     * 
     * @param question 问题
     * @param confidence 置信度
     * @return 是否需要转人工
     */
    boolean needsHumanTransfer(String question, double confidence);
}
