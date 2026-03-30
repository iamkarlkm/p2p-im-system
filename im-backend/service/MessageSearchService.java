package com.im.backend.service;

import com.im.backend.dto.MessageSearchRequest;
import com.im.backend.dto.MessageSearchResult;
import com.im.backend.dto.SearchHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 消息搜索服务接口
 */
public interface MessageSearchService {

    /**
     * 搜索消息
     */
    Page<MessageSearchResult> searchMessages(Long userId, MessageSearchRequest request);

    /**
     * 在指定会话中搜索
     */
    Page<MessageSearchResult> searchInConversation(Long userId, Long conversationId, 
                                                    String conversationType, String keyword, Pageable pageable);

    /**
     * 高级搜索
     */
    Page<MessageSearchResult> advancedSearch(Long userId, MessageSearchRequest request);

    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(Long userId, String prefix);

    /**
     * 获取搜索历史
     */
    List<SearchHistoryDTO> getSearchHistory(Long userId, int limit);

    /**
     * 删除搜索历史
     */
    void deleteSearchHistory(Long userId, String keyword);

    /**
     * 清空搜索历史
     */
    void clearSearchHistory(Long userId);

    /**
     * 获取热门搜索
     */
    List<String> getHotKeywords(int limit);

    /**
     * 索引新消息
     */
    void indexMessage(Long messageId, Long senderId, String senderName, String conversationType,
                      Long conversationId, String content, String contentType);

    /**
     * 删除消息索引
     */
    void deleteMessageIndex(Long messageId);

    /**
     * 高亮搜索关键词
     */
    String highlightKeyword(String content, String keyword);
}
