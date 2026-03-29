package com.im.backend.modules.local.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.local.dto.IntelligentAssistantRequest;
import com.im.backend.modules.local.dto.IntelligentAssistantResponse;
import com.im.backend.modules.local.dto.POISemanticSearchRequest;
import com.im.backend.modules.local.dto.POISemanticSearchResponse;
import com.im.backend.modules.local.service.IntelligentAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * 智能对话助手控制器
 * 提供自然语言POI搜索、智能问答、多轮对话等API接口
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local/assistant")
@RequiredArgsConstructor
@Validated
@Tag(name = "智能对话助手", description = "本地生活智能对话助手与POI语义搜索相关接口")
public class IntelligentAssistantController {

    private final IntelligentAssistantService assistantService;

    /**
     * 智能对话 - 处理自然语言查询
     */
    @PostMapping("/dialog")
    @Operation(summary = "智能对话", description = "处理用户自然语言查询，返回智能回复和推荐结果")
    public ApiResponse<IntelligentAssistantResponse> processDialog(
            @Valid @RequestBody IntelligentAssistantRequest request) {
        log.info("Received dialog request: {}", request.getQuery());
        
        IntelligentAssistantResponse response = assistantService.processDialog(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 智能对话 - 异步处理
     */
    @PostMapping("/dialog/async")
    @Operation(summary = "智能对话（异步）", description = "异步处理用户自然语言查询")
    public CompletableFuture<ApiResponse<IntelligentAssistantResponse>> processDialogAsync(
            @Valid @RequestBody IntelligentAssistantRequest request) {
        log.info("Received async dialog request: {}", request.getQuery());
        
        return assistantService.processDialogAsync(request)
                .thenApply(ApiResponse::success);
    }

    /**
     * POI语义搜索
     */
    @PostMapping("/semantic-search")
    @Operation(summary = "POI语义搜索", description = "基于自然语言的POI语义搜索")
    public ApiResponse<POISemanticSearchResponse> semanticSearch(
            @Valid @RequestBody POISemanticSearchRequest request) {
        log.info("Received semantic search request: {}", request.getSemanticQuery());
        
        POISemanticSearchResponse response = assistantService.semanticSearch(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 快速搜索 - GET方式
     */
    @GetMapping("/quick-search")
    @Operation(summary = "快速语义搜索", description = "通过URL参数进行快速语义搜索")
    public ApiResponse<POISemanticSearchResponse> quickSearch(
            @Parameter(description = "搜索语义") @RequestParam String query,
            @Parameter(description = "纬度") @RequestParam(required = false) Double lat,
            @Parameter(description = "经度") @RequestParam(required = false) Double lng,
            @Parameter(description = "半径（米）") @RequestParam(required = false, defaultValue = "5000") Integer radius,
            @Parameter(description = "分类") @RequestParam(required = false) String category) {
        
        log.info("Received quick search: {} at [{}, {}]", query, lat, lng);
        
        POISemanticSearchRequest request = POISemanticSearchRequest.builder()
                .semanticQuery(query)
                .latitude(lat)
                .longitude(lng)
                .radius(radius)
                .category(category)
                .page(1)
                .pageSize(20)
                .build();
        
        POISemanticSearchResponse response = assistantService.semanticSearch(request);
        
        return ApiResponse.success(response);
    }

    /**
     * POI智能问答
     */
    @GetMapping("/poi/{poiId}/qa")
    @Operation(summary = "POI智能问答", description = "回答关于特定POI的问题")
    public ApiResponse<String> answerPOIQuestion(
            @Parameter(description = "POI ID") @PathVariable String poiId,
            @Parameter(description = "问题") @RequestParam String question) {
        log.info("Received POI question for {}: {}", poiId, question);
        
        String answer = assistantService.answerPOIQuestion(poiId, question);
        
        return ApiResponse.success(answer);
    }

    /**
     * 语音搜索
     */
    @PostMapping("/voice-search")
    @Operation(summary = "语音搜索", description = "处理语音输入的搜索请求")
    public ApiResponse<IntelligentAssistantResponse> voiceSearch(
            @Parameter(description = "语音文本") @RequestParam String voiceText,
            @Parameter(description = "方言类型") @RequestParam(required = false) String dialectType,
            @Parameter(description = "会话ID") @RequestParam(required = false) String conversationId,
            @Parameter(description = "纬度") @RequestParam(required = false) Double lat,
            @Parameter(description = "经度") @RequestParam(required = false) Double lng) {
        
        log.info("Received voice search: {}", voiceText);
        
        IntelligentAssistantRequest request = IntelligentAssistantRequest.builder()
                .query(voiceText)
                .conversationId(conversationId)
                .voiceInput(true)
                .dialectType(dialectType)
                .latitude(lat)
                .longitude(lng)
                .build();
        
        IntelligentAssistantResponse response = assistantService.processDialog(request);
        
        return ApiResponse.success(response);
    }
}
