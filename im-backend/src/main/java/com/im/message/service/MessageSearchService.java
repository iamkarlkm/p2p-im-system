package com.im.message.service;

import com.im.message.dto.MessageQueryRequest;
import com.im.message.dto.MessageSearchResponse;

import java.util.List;

/**
 * 消息搜索服务接口 - 全文检索与搜索
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface MessageSearchService {
    
    /**
     * 关键词搜索消息
     * 
     * @param request 查询请求
     * @return 搜索结果列表
     */
    List<MessageSearchResponse> searchMessages(MessageQueryRequest request);
    
    /**
     * 全文搜索(高性能)
     * 
     * @param conversationId 会话ID
     * @param conversationType 会话类型
     * @param keyword 关键词
     * @param limit 返回数量限制
     * @return 搜索结果列表
     */
    List<MessageSearchResponse> fullTextSearch(Long conversationId, Integer conversationType, 
                                                String keyword, Integer limit);
    
    /**
     * 高亮搜索(带关键词高亮)
     * 
     * @param request 查询请求
     * @return 带高亮的结果
     */
    List<MessageSearchResponse> searchWithHighlight(MessageQueryRequest request);
    
    /**
     * 搜索建议(自动补全)
     * 
     * @param conversationId 会话ID
     * @param prefix 前缀
     * @param limit 返回数量
     * @return 建议列表
     */
    List<String> searchSuggestions(Long conversationId, String prefix, Integer limit);
    
    /**
     * 重建搜索索引
     * 
     * @param conversationId 会话ID
     * @return 重建的消息数量
     */
    int rebuildIndex(Long conversationId);
    
    /**
     * 索引单条消息
     * 
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean indexMessage(Long messageId);
    
    /**
     * 批量索引消息
     * 
     * @param messageIds 消息ID列表
     * @return 成功数量
     */
    int batchIndexMessages(List<Long> messageIds);
    
    /**
     * 删除索引
     * 
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean deleteIndex(Long messageId);
}
