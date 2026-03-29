package com.im.backend.modules.local_life.service;

import com.im.backend.modules.local_life.dto.SearchIntentDTO;

import java.util.List;

/**
 * 意图识别服务接口
 * 使用NLP技术识别用户搜索意图
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface IntentRecognitionService {

    /**
     * 识别搜索意图
     *
     * @param query 自然语言查询
     * @param context 上下文信息（可选）
     * @return 意图识别结果
     */
    SearchIntentDTO recognizeIntent(String query, java.util.Map<String, Object> context);

    /**
     * 批量识别意图
     *
     * @param queries 查询列表
     * @return 意图列表
     */
    List<SearchIntentDTO> recognizeBatch(List<String> queries);

    /**
     * 提取命名实体
     *
     * @param query 查询文本
     * @return 实体列表
     */
    java.util.List<java.util.Map<String, Object>> extractEntities(String query);

    /**
     * 判断是否需要澄清
     *
     * @param query 查询文本
     * @param intent 已识别意图
     * @return 是否需要澄清
     */
    boolean needsClarification(String query, SearchIntentDTO intent);

    /**
     * 生成澄清问题
     *
     * @param query 原始查询
     * @param missingEntities 缺失的实体
     * @return 澄清问题列表
     */
    List<String> generateClarificationQuestions(String query, List<String> missingEntities);

    /**
     * 获取意图类型列表
     *
     * @return 支持的意图类型
     */
    List<String> getSupportedIntentTypes();

    /**
     * 更新意图模型
     *
     * @param trainingData 训练数据
     */
    void updateModel(java.util.List<java.util.Map<String, Object>> trainingData);
}
