package com.im.controller.multimodal;

import com.im.entity.multimodal.*;
import com.im.service.multimodal.MultimodalAIAssistantService;
import com.im.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * 多模态AI助手控制器
 * REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai-assistant")
@RequiredArgsConstructor
public class MultimodalAIAssistantController {

    private final MultimodalAIAssistantService assistantService;

    /**
     * 创建AI助手
     */
    @PostMapping("/assistants")
    public Result<MultimodalAIAssistant> createAssistant(@RequestBody MultimodalAIAssistant assistant) {
        MultimodalAIAssistant created = assistantService.createAssistant(assistant);
        return Result.success(created);
    }

    /**
     * 获取所有助手
     */
    @GetMapping("/assistants")
    public Result<List<MultimodalAIAssistant>> getAllAssistants() {
        return Result.success(assistantService.getAllAssistants());
    }

    /**
     * 获取助手详情
     */
    @GetMapping("/assistants/{assistantId}")
    public Result<MultimodalAIAssistant> getAssistant(@PathVariable String assistantId) {
        return assistantService.getAssistant(assistantId)
            .map(Result::success)
            .orElse(Result.error("Assistant not found"));
    }

    /**
     * 获取用户可访问的助手
     */
    @GetMapping("/assistants/accessible")
    public Result<List<MultimodalAIAssistant>> getAccessibleAssistants(@RequestParam Long userId) {
        return Result.success(assistantService.getAccessibleAssistants(userId));
    }

    /**
     * 更新助手
     */
    @PutMapping("/assistants/{assistantId}")
    public Result<MultimodalAIAssistant> updateAssistant(
            @PathVariable String assistantId,
            @RequestBody MultimodalAIAssistant updates) {
        return Result.success(assistantService.updateAssistant(assistantId, updates));
    }

    /**
     * 删除助手
     */
    @DeleteMapping("/assistants/{assistantId}")
    public Result<Void> deleteAssistant(@PathVariable String assistantId) {
        assistantService.deleteAssistant(assistantId);
        return Result.success();
    }

    /**
     * 设置助手在线状态
     */
    @PostMapping("/assistants/{assistantId}/online")
    public Result<Void> setAssistantOnline(
            @PathVariable String assistantId,
            @RequestParam boolean online) {
        assistantService.setAssistantOnline(assistantId, online);
        return Result.success();
    }

    /**
     * 创建对话
     */
    @PostMapping("/conversations")
    public Result<MultimodalConversation> createConversation(
            @RequestParam Long userId,
            @RequestParam String assistantId,
            @RequestParam(required = false) String title) {
        MultimodalConversation conversation = assistantService.createConversation(userId, assistantId, title);
        return Result.success(conversation);
    }

    /**
     * 获取对话详情
     */
    @GetMapping("/conversations/{conversationId}")
    public Result<MultimodalConversation> getConversation(@PathVariable String conversationId) {
        return assistantService.getConversation(conversationId)
            .map(Result::success)
            .orElse(Result.error("Conversation not found"));
    }

    /**
     * 获取用户对话列表
     */
    @GetMapping("/conversations/user/{userId}")
    public Result<List<MultimodalConversation>> getUserConversations(@PathVariable Long userId) {
        return Result.success(assistantService.getUserConversations(userId));
    }

    /**
     * 获取对话消息
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public Result<List<MultimodalMessage>> getConversationMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "50") int limit) {
        return Result.success(assistantService.getConversationMessages(conversationId, limit));
    }

    /**
     * 发送消息
     */
    @PostMapping("/conversations/{conversationId}/messages")
    public Result<MultimodalMessage> sendMessage(
            @PathVariable String conversationId,
            @RequestBody SendMessageRequest request) {
        MultimodalMessage response = assistantService.sendMessage(
            conversationId, 
            request.getContent(), 
            request.getModality(),
            request.getAttachments()
        );
        return Result.success(response);
    }

    /**
     * 流式发送消息
     */
    @GetMapping(value = "/conversations/{conversationId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(
            @PathVariable String conversationId,
            @RequestParam String content) {
        return assistantService.sendMessageStream(conversationId, content);
    }

    /**
     * 归档对话
     */
    @PostMapping("/conversations/{conversationId}/archive")
    public Result<Void> archiveConversation(@PathVariable String conversationId) {
        assistantService.archiveConversation(conversationId);
        return Result.success();
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public Result<Void> deleteConversation(@PathVariable String conversationId) {
        assistantService.deleteConversation(conversationId);
        return Result.success();
    }

    /**
     * 获取助手统计
     */
    @GetMapping("/assistants/{assistantId}/stats")
    public Result<Map<String, Object>> getAssistantStats(@PathVariable String assistantId) {
        return Result.success(assistantService.getAssistantStats(assistantId));
    }

    /**
     * 发送消息请求
     */
    @lombok.Data
    public static class SendMessageRequest {
        private String content;
        private MultimodalAIAssistant.ModalityType modality;
        private List<MessageAttachment> attachments;
    }
}
