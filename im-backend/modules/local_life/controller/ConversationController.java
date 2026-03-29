package com.im.backend.modules.local_life.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.local_life.dto.*;
import com.im.backend.modules.local_life.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能对话控制器
 * 处理自然语言对话、多轮问答
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
@Tag(name = "智能对话", description = "本地生活智能对话助手API")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/sessions")
    @Operation(summary = "创建对话会话", description = "创建新的对话会话")
    public Result<String> createSession(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "会话类型") @RequestParam(defaultValue = "SEARCH") String sessionType) {
        String sessionId = conversationService.createSession(userId, sessionType);
        return Result.success(sessionId);
    }

    @PostMapping("/query")
    @Operation(summary = "发送自然语言查询", description = "发送自然语言查询并获取AI回复")
    public Result<ConversationResponseDTO> sendQuery(
            @RequestBody @Validated NaturalLanguageSearchRequestDTO request,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        ConversationResponseDTO response = conversationService.processQuery(request, userId);
        return Result.success(response);
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "获取会话详情", description = "获取指定会话的详细信息")
    public Result<Map<String, Object>> getSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {
        // 简化实现，实际应返回完整会话详情
        return Result.success(Map.of("sessionId", sessionId, "status", "ACTIVE"));
    }

    @PostMapping("/sessions/{sessionId}/end")
    @Operation(summary = "结束会话", description = "结束指定的对话会话")
    public Result<Void> endSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {
        conversationService.endSession(sessionId);
        return Result.success();
    }

    @GetMapping("/sessions/{sessionId}/history")
    @Operation(summary = "获取会话历史", description = "获取会话的历史消息记录")
    public Result<List<ConversationResponseDTO>> getSessionHistory(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "返回条数") @RequestParam(defaultValue = "20") int limit) {
        List<ConversationResponseDTO> history = conversationService.getSessionHistory(sessionId, limit);
        return Result.success(history);
    }

    @PostMapping("/sessions/{sessionId}/rate")
    @Operation(summary = "评价会话", description = "对会话满意度进行评分")
    public Result<Void> rateSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "满意度分数 1-5", required = true) @RequestParam Integer score) {
        conversationService.rateSession(sessionId, score);
        return Result.success();
    }

    @PostMapping("/voice-query")
    @Operation(summary = "语音查询", description = "发送语音搜索请求")
    public Result<ConversationResponseDTO> voiceQuery(
            @Parameter(description = "语音文件") @RequestPart("audio") org.springframework.web.multipart.MultipartFile audio,
            @Parameter(description = "会话ID") @RequestParam(required = false) String sessionId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "纬度") @RequestParam(required = false) Double latitude,
            @Parameter(description = "经度") @RequestParam(required = false) Double longitude) {
        // 简化实现：语音转文本后调用文本搜索
        NaturalLanguageSearchRequestDTO request = new NaturalLanguageSearchRequestDTO();
        request.setQuery("语音搜索：附近好吃的");
        request.setSessionId(sessionId);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setIsVoiceInput(true);
        ConversationResponseDTO response = conversationService.processQuery(request, userId);
        return Result.success(response);
    }
}
