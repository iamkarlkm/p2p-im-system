package com.im.backend.controller;

import com.im.backend.entity.BotEntity;
import com.im.backend.entity.BotSessionEntity;
import com.im.backend.service.BotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AI 聊天机器人 REST API
 * 支持多平台 AI 模型集成 (OpenAI/Claude/Gemini/Custom Webhook)
 */
@RestController
@RequestMapping("/api/bots")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    // ========== Bot CRUD ==========

    @PostMapping
    public ResponseEntity<BotEntity> createBot(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String description = body.get("description");
        String botType = body.get("botType");
        String modelName = body.get("modelName");
        String ownerId = body.get("ownerId");

        if (name == null || ownerId == null) {
            return ResponseEntity.badRequest().build();
        }

        BotEntity bot = botService.createBot(name, description, botType, modelName, ownerId);
        return ResponseEntity.ok(bot);
    }

    @GetMapping("/{botId}")
    public ResponseEntity<BotEntity> getBot(@PathVariable String botId) {
        return botService.getBot(botId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<BotEntity> getBotByToken(@PathVariable String token) {
        return botService.getBotByAccessToken(token)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<BotEntity>> getMyBots(@RequestParam String ownerId) {
        return ResponseEntity.ok(botService.getMyBots(ownerId));
    }

    @GetMapping("/public")
    public ResponseEntity<List<BotEntity>> getPublicBots() {
        return ResponseEntity.ok(botService.getPublicBots());
    }

    @PutMapping("/{botId}")
    public ResponseEntity<BotEntity> updateBot(
            @PathVariable String botId,
            @RequestBody Map<String, Object> updates) {
        BotEntity bot = botService.updateBot(botId, updates);
        return ResponseEntity.ok(bot);
    }

    @DeleteMapping("/{botId}")
    public ResponseEntity<Void> deleteBot(@PathVariable String botId) {
        botService.deleteBot(botId);
        return ResponseEntity.noContent().build();
    }

    // ========== Chat ==========

    @PostMapping("/{botId}/chat")
    public ResponseEntity<Map<String, String>> chat(
            @PathVariable String botId,
            @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String conversationId = body.get("conversationId");
        String message = body.get("message");

        if (userId == null || conversationId == null || message == null) {
            return ResponseEntity.badRequest().build();
        }

        String reply = botService.chatWithBot(botId, userId, conversationId, message);

        Map<String, String> response = new HashMap<>();
        response.put("reply", reply);
        response.put("botId", botId);
        response.put("timestamp", new Date().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook/{token}/receive")
    public ResponseEntity<Map<String, String>> receiveWebhook(
            @PathVariable String token,
            @RequestBody Map<String, String> body) {
        String message = body.get("message");

        return botService.getBotByAccessToken(token)
            .map(bot -> {
                String reply = botService.chatWithBot(
                    bot.getBotId(),
                    body.getOrDefault("userId", "webhook"),
                    body.getOrDefault("conversationId", UUID.randomUUID().toString()),
                    message
                );
                Map<String, String> response = new HashMap<>();
                response.put("reply", reply);
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.status(401).build());
    }

    // ========== Sessions ==========

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<BotSessionEntity> getSession(@PathVariable String sessionId) {
        return botService.getSession(sessionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<BotSessionEntity>> getUserSessions(@PathVariable String userId) {
        return ResponseEntity.ok(botService.getUserSessions(userId));
    }

    @PostMapping("/sessions/{sessionId}/end")
    public ResponseEntity<Void> endSession(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "USER_END");
        botService.endSession(sessionId, reason);
        return ResponseEntity.ok().build();
    }

    // ========== Stats ==========

    @GetMapping("/{botId}/stats")
    public ResponseEntity<Map<String, Object>> getBotStats(@PathVariable String botId) {
        return ResponseEntity.ok(botService.getBotStats(botId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
        return ResponseEntity.ok(botService.getBotLeaderboard());
    }

    // ========== Config Presets ==========

    @GetMapping("/presets")
    public ResponseEntity<List<Map<String, String>>> getPresets() {
        List<Map<String, String>> presets = Arrays.asList(
            Map.of("type", "OPENAI", "model", "gpt-4", "name", "GPT-4 (通用)"),
            Map.of("type", "OPENAI", "model", "gpt-4-turbo", "name", "GPT-4 Turbo (快速)"),
            Map.of("type", "OPENAI", "model", "gpt-3.5-turbo", "name", "GPT-3.5 Turbo (经济)"),
            Map.of("type", "CLAUDE", "model", "claude-3-opus-20240229", "name", "Claude 3 Opus"),
            Map.of("type", "CLAUDE", "model", "claude-3-sonnet-20240229", "name", "Claude 3 Sonnet"),
            Map.of("type", "GEMINI", "model", "gemini-pro", "name", "Gemini Pro"),
            Map.of("type", "GEMINI", "model", "gemini-1.5-pro", "name", "Gemini 1.5 Pro"),
            Map.of("type", "CUSTOM", "model", "custom", "name", "Custom Webhook")
        );
        return ResponseEntity.ok(presets);
    }
}
