package com.im.backend.controller;

import com.im.backend.dto.ExpirationRuleRequest;
import com.im.backend.dto.ExpirationRuleResponse;
import com.im.backend.service.MessageExpirationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息过期规则控制器
 */
@RestController
@RequestMapping("/api/expiration")
public class MessageExpirationController {

    private final MessageExpirationService expirationService;

    public MessageExpirationController(MessageExpirationService expirationService) {
        this.expirationService = expirationService;
    }

    /**
     * 创建会话级过期规则
     */
    @PostMapping("/rules")
    public ExpirationRuleResponse createRule(@RequestBody ExpirationRuleRequest req,
                                            @RequestHeader("X-User-Id") Long userId) {
        return expirationService.saveRule(userId, req);
    }

    /**
     * 更新过期规则
     */
    @PutMapping("/rules/{ruleId}")
    public ExpirationRuleResponse updateRule(@PathVariable Long ruleId,
                                             @RequestBody ExpirationRuleRequest req,
                                             @RequestHeader("X-User-Id") Long userId) {
        req.setId(ruleId);
        return expirationService.saveRule(userId, req);
    }

    /**
     * 获取用户所有规则
     */
    @GetMapping("/rules")
    public List<ExpirationRuleResponse> getUserRules(@RequestHeader("X-User-Id") Long userId) {
        return expirationService.getUserRules(userId);
    }

    /**
     * 获取会话的生效规则
     */
    @GetMapping("/rules/conversation/{conversationId}")
    public ExpirationRuleResponse getEffectiveRule(@PathVariable String conversationId,
                                                   @RequestHeader("X-User-Id") Long userId) {
        return expirationService.getEffectiveRule(userId, conversationId);
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/rules/{ruleId}")
    public Map<String, Object> deleteRule(@PathVariable Long ruleId,
                                          @RequestHeader("X-User-Id") Long userId) {
        expirationService.deleteRule(ruleId, userId);
        return Map.of("success", true, "message", "规则已删除");
    }

    /**
     * 启用/禁用规则
     */
    @PatchMapping("/rules/{ruleId}/toggle")
    public ExpirationRuleResponse toggleRule(@PathVariable Long ruleId,
                                            @RequestParam boolean enabled,
                                            @RequestHeader("X-User-Id") Long userId) {
        return expirationService.toggleRule(ruleId, userId, enabled);
    }

    /**
     * 获取消息剩余存活时间
     */
    @GetMapping("/messages/{messageId}/remaining")
    public Map<String, Object> getRemainingTime(@PathVariable Long messageId,
                                                 @RequestHeader("X-User-Id") Long userId) {
        Long remaining = expirationService.getMessageRemainingSeconds(messageId, userId);
        return Map.of("messageId", messageId, "remainingSeconds", remaining != null ? remaining : -1);
    }

    /**
     * 记录消息被阅读（客户端通知服务端开始计时）
     */
    @PostMapping("/messages/{messageId}/read")
    public Map<String, Object> recordRead(@PathVariable Long messageId,
                                          @RequestHeader("X-User-Id") Long userId) {
        expirationService.recordMessageRead(messageId, userId, 0L);
        return Map.of("success", true, "messageId", messageId);
    }

    /**
     * 获取用户的全局默认规则
     */
    @GetMapping("/rules/global")
    public ExpirationRuleResponse getGlobalRule(@RequestHeader("X-User-Id") Long userId) {
        return expirationService.getEffectiveRule(userId, null);
    }
}
