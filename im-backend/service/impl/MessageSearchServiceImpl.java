package com.im.backend.service.impl;

import com.im.backend.dto.MessageSearchRequest;
import com.im.backend.dto.MessageSearchResult;
import com.im.backend.dto.SearchHistoryDTO;
import com.im.backend.entity.MessageSearchIndex;
import com.im.backend.entity.SearchHistory;
import com.im.backend.repository.MessageSearchRepository;
import com.im.backend.repository.SearchHistoryRepository;
import com.im.backend.service.MessageSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 消息搜索服务实现
 */
@Service
public class MessageSearchServiceImpl implements MessageSearchService {

    @Autowired
    private MessageSearchRepository searchRepository;

    @Autowired
    private SearchHistoryRepository historyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<MessageSearchResult> searchMessages(Long userId, MessageSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<MessageSearchIndex> searchPage;
        
        // 根据搜索类型选择查询方式
        if (request.getConversationId() != null && request.getSearchType() != null) {
            searchPage = searchRepository.searchInConversation(
                request.getKeyword(), request.getSearchType(), 
                request.getConversationId(), pageable);
        } else if (request.getSenderId() != null) {
            searchPage = searchRepository.searchBySender(
                request.getKeyword(), request.getSenderId(), pageable);
        } else if (request.getStartTime() != null && request.getEndTime() != null) {
            searchPage = searchRepository.searchByTimeRange(
                request.getKeyword(), request.getStartTime(), request.getEndTime(), pageable);
        } else {
            searchPage = searchRepository.searchByKeyword(request.getKeyword(), pageable);
        }

        // 保存搜索历史
        if (request.getSaveHistory()) {
            saveSearchHistory(userId, request.getKeyword(), request.getSearchType());
        }

        return searchPage.map(this::convertToResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageSearchResult> searchInConversation(Long userId, Long conversationId, 
                                                           String conversationType, String keyword, 
                                                           Pageable pageable) {
        Page<MessageSearchIndex> searchPage = searchRepository.searchInConversation(
            keyword, conversationType, conversationId, pageable);
        return searchPage.map(this::convertToResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageSearchResult> advancedSearch(Long userId, MessageSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        String keyword = request.getKeyword();
        Long senderId = request.getSenderId();
        String conversationType = request.getSearchType();
        Long conversationId = request.getConversationId();
        String contentType = request.getContentType();
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        Page<MessageSearchIndex> searchPage = searchRepository.advancedSearch(
            keyword, senderId, conversationType, conversationId, contentType, 
            startTime, endTime, pageable);

        return searchPage.map(this::convertToResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSearchSuggestions(Long userId, String prefix) {
        // 获取用户历史搜索中匹配的关键词
        List<SearchHistory> history = historyRepository.findByUserIdOrderByCreatedAtDesc(userId, 
            PageRequest.of(0, 50));
        
        return history.stream()
            .map(SearchHistory::getKeyword)
            .filter(k -> k.toLowerCase().contains(prefix.toLowerCase()))
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchHistoryDTO> getSearchHistory(Long userId, int limit) {
        List<SearchHistory> history = historyRepository.findDistinctByUserIdOrderByCreatedAtDesc(
            userId, PageRequest.of(0, limit));
        
        return history.stream()
            .map(this::convertToHistoryDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSearchHistory(Long userId, String keyword) {
        historyRepository.deleteByUserIdAndKeyword(userId, keyword);
    }

    @Override
    @Transactional
    public void clearSearchHistory(Long userId) {
        historyRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getHotKeywords(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Object[]> hotKeywords = historyRepository.findHotKeywords(since, 
            PageRequest.of(0, limit));
        
        return hotKeywords.stream()
            .map(obj -> (String) obj[0])
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void indexMessage(Long messageId, Long senderId, String senderName, 
                             String conversationType, Long conversationId, 
                             String content, String contentType) {
        MessageSearchIndex index = new MessageSearchIndex();
        index.setMessageId(messageId);
        index.setSenderId(senderId);
        index.setSenderName(senderName);
        index.setConversationType(conversationType);
        index.setConversationId(conversationId);
        index.setContent(content);
        index.setContentType(contentType);
        
        // 提取关键词
        String keywords = extractKeywords(content);
        index.setKeywords(keywords);
        
        searchRepository.save(index);
    }

    @Override
    @Transactional
    public void deleteMessageIndex(Long messageId) {
        searchRepository.deleteByMessageId(messageId);
    }

    @Override
    public String highlightKeyword(String content, String keyword) {
        if (content == null || keyword == null || keyword.isEmpty()) {
            return content;
        }
        
        String regex = "(" + Pattern.quote(keyword) + ")";
        return content.replaceAll(regex, "<mark>$1</mark>");
    }

    private void saveSearchHistory(Long userId, String keyword, String searchType) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword);
        history.setSearchType(searchType != null ? searchType : "ALL");
        historyRepository.save(history);
    }

    private String extractKeywords(String content) {
        // 简单的关键词提取：去除停用词，保留长度>2的词
        if (content == null) return "";
        
        String[] stopWords = {"的", "了", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这"};
        
        String cleaned = content.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", " ");
        String[] words = cleaned.split("\\s+");
        
        List<String> keywords = new ArrayList<>();
        for (String word : words) {
            if (word.length() > 2) {
                boolean isStopWord = false;
                for (String stop : stopWords) {
                    if (word.equals(stop)) {
                        isStopWord = true;
                        break;
                    }
                }
                if (!isStopWord) {
                    keywords.add(word);
                }
            }
        }
        
        return String.join(",", keywords);
    }

    private MessageSearchResult convertToResult(MessageSearchIndex index) {
        MessageSearchResult result = new MessageSearchResult();
        result.setMessageId(index.getMessageId());
        result.setSenderId(index.getSenderId());
        result.setSenderName(index.getSenderName());
        result.setConversationType(index.getConversationType());
        result.setConversationId(index.getConversationId());
        result.setContent(index.getContent());
        result.setContentType(index.getContentType());
        result.setCreatedAt(index.getCreatedAt());
        return result;
    }

    private SearchHistoryDTO convertToHistoryDTO(SearchHistory history) {
        SearchHistoryDTO dto = new SearchHistoryDTO();
        dto.setId(history.getId());
        dto.setKeyword(history.getKeyword());
        dto.setSearchType(history.getSearchType());
        dto.setResultCount(history.getResultCount());
        dto.setSearchTime(history.getCreatedAt());
        return dto;
    }
}
