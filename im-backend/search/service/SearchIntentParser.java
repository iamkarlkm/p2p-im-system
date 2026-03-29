package com.im.search.service;

import com.im.search.dto.SearchIntentDTO;

/**
 * 搜索意图解析服务接口
 */
public interface SearchIntentParser {

    /**
     * 解析搜索意图
     * @param query 用户查询
     * @param sessionId 会话ID
     * @return 意图识别结果
     */
    SearchIntentDTO parse(String query, String sessionId);
}
