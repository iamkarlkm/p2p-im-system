package com.im.backend.controller.search;

import com.im.backend.common.Result;
import com.im.backend.dto.search.SemanticSearchRequestDTO;
import com.im.backend.dto.search.SemanticSearchResponseDTO;
import com.im.backend.dto.search.POIQADTO;
import com.im.backend.service.search.SemanticSearchService;
import com.im.backend.service.search.ConversationService;
import com.im.backend.service.search.POIQAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 语义搜索控制器
 * 处理自然语言搜索、POI问答、多轮对话等请求
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Validated
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;
    private final ConversationService conversationService;
    private final POIQAService poiQAService;
    
    /**
     * 语义搜索 - 自然语言POI搜索主接口
     * 
     * @param request 搜索请求
     * @return 搜索结果
     */
    @PostMapping("/semantic")
    public Result<SemanticSearchResponseDTO> semanticSearch(
            @RequestBody @Validated SemanticSearchRequestDTO request) {
        
        log.info("Semantic search request: {}", request.getQuery());
        
        Long userId = getCurrentUserId();
        
        // 处理语音输入
        if (Boolean.TRUE.equals(request.getIsVoiceInput()) && request.getVoiceData() != null) {
            String recognizedText = semanticSearchService.recognizeVoiceQuery(request.getVoiceData());
            request.setQuery(recognizedText);
        }
        
        // 执行语义搜索
        SemanticSearchResponseDTO response = semanticSearchService.semanticSearch(request, userId);
        
        // 记录会话
        if (response.getSessionId() == null) {
            String sessionId = conversationService.createSession(userId, "SEARCH");
            response.setSessionId(sessionId);
        }
        
        return Result.success(response);
    }
    
    /**
     * 多轮对话搜索
     * 
     * @param sessionId 会话ID
     * @param request 搜索请求
     * @return 搜索结果
     */
    @PostMapping("/semantic/session/{sessionId}")
    public Result<SemanticSearchResponseDTO> multiTurnSearch(
            @PathVariable String sessionId,
            @RequestBody @Validated SemanticSearchRequestDTO request) {
        
        log.info("Multi-turn search - session: {}, query: {}", sessionId, request.getQuery());
        
        Long userId = getCurrentUserId();
        
        // 获取或创建会话
        var session = conversationService.getSession(sessionId);
        if (session == null) {
            session = conversationService.createSessionEntity(userId, "SEARCH");
            sessionId = session.getSessionId();
        }
        
        // 执行多轮搜索
        SemanticSearchResponseDTO response = semanticSearchService.multiTurnSearch(request, session, userId);
        response.setSessionId(sessionId);
        
        return Result.success(response);
    }
    
    /**
     * POI智能问答
     * 
     * @param dto 问答请求
     * @return 问答响应
     */
    @PostMapping("/poi-qa")
    public Result<POIQADTO.Response> poiQA(@RequestBody @Validated POIQADTO dto) {
        log.info("POI QA request - poiId: {}, question: {}", dto.getPoiId(), dto.getQuestion());
        
        Long userId = getCurrentUserId();
        dto.setUserId(userId);
        
        // 处理语音输入
        if (Boolean.TRUE.equals(dto.getIsVoiceInput()) && dto.getVoiceData() != null) {
            String recognizedText = semanticSearchService.recognizeVoiceQuery(dto.getVoiceData());
            dto.setQuestion(recognizedText);
        }
        
        POIQADTO.Response response = poiQAService.answerQuestion(dto);
        
        return Result.success(response);
    }
    
    /**
     * 获取搜索建议
     * 
     * @param query 部分查询文本
     * @return 建议列表
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSuggestions(@RequestParam String query) {
        log.info("Search suggestions request: {}", query);
        
        Long userId = getCurrentUserId();
        List<String> suggestions = semanticSearchService.getSearchSuggestions(query, userId);
        
        return Result.success(suggestions);
    }
    
    /**
     * 获取热门搜索
     * 
     * @param cityCode 城市代码（可选）
     * @param limit 数量限制（默认10）
     * @return 热门搜索列表
     */
    @GetMapping("/hot")
    public Result<List<String>> getHotSearches(
            @RequestParam(required = false) String cityCode,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Hot searches request - city: {}, limit: {}", cityCode, limit);
        
        List<String> hotSearches = semanticSearchService.getHotSearches(cityCode, limit);
        
        return Result.success(hotSearches);
    }
    
    /**
     * 获取个性化搜索推荐
     * 
     * @param limit 数量限制（默认5）
     * @return 个性化推荐列表
     */
    @GetMapping("/personalized")
    public Result<List<String>> getPersonalizedSuggestions(
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("Personalized suggestions request");
        
        Long userId = getCurrentUserId();
        List<String> suggestions = semanticSearchService.getPersonalizedSuggestions(userId, limit);
        
        return Result.success(suggestions);
    }
    
    /**
     * 结束搜索会话
     * 
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @PostMapping("/session/{sessionId}/end")
    public Result<Void> endSession(@PathVariable String sessionId) {
        log.info("End session request: {}", sessionId);
        
        conversationService.endSession(sessionId);
        
        return Result.success();
    }
    
    /**
     * 获取会话历史
     * 
     * @param sessionId 会话ID
     * @return 会话历史
     */
    @GetMapping("/session/{sessionId}/history")
    public Result<List<Object>> getSessionHistory(@PathVariable String sessionId) {
        log.info("Get session history: {}", sessionId);
        
        List<Object> history = conversationService.getSessionHistory(sessionId);
        
        return Result.success(history);
    }
    
    /**
     * 语音搜索
     * 
     * @param voiceData 语音数据（Base64）
     * @param longitude 经度
     * @param latitude 纬度
     * @return 搜索结果
     */
    @PostMapping("/voice")
    public Result<SemanticSearchResponseDTO> voiceSearch(
            @RequestParam String voiceData,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        
        log.info("Voice search request");
        
        // 语音识别
        String recognizedText = semanticSearchService.recognizeVoiceQuery(voiceData);
        
        // 构建搜索请求
        SemanticSearchRequestDTO request = SemanticSearchRequestDTO.builder()
                .query(recognizedText)
                .isVoiceInput(true)
                .voiceData(voiceData)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        
        Long userId = getCurrentUserId();
        SemanticSearchResponseDTO response = semanticSearchService.semanticSearch(request, userId);
        
        return Result.success(response);
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 获取当前用户ID
     * 实际实现需要从SecurityContext获取
     */
    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取
        return 1L;
    }
}
