package com.im.backend.service;

import com.im.backend.entity.BotEntity;
import com.im.backend.entity.BotSessionEntity;
import com.im.backend.repository.BotRepository;
import com.im.backend.repository.BotSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BotService {

    private final BotRepository botRepository;
    private final BotSessionRepository botSessionRepository;
    private final RestTemplate restTemplate;

    private static final Map<String, String> DEFAULT_API_URLS = Map.of(
        "OPENAI", "https://api.openai.com/v1/chat/completions",
        "CLAUDE", "https://api.anthropic.com/v1/messages",
        "GEMINI", "https://generativelanguage.googleapis.com/v1beta/models"
    );

    public BotService(BotRepository botRepository, BotSessionRepository botSessionRepository) {
        this.botRepository = botRepository;
        this.botSessionRepository = botSessionRepository;
        this.restTemplate = new RestTemplate();
    }

    // ========== Bot CRUD ==========

    @Transactional
    public BotEntity createBot(String name, String description, String botType,
                               String modelName, String ownerId) {
        if (botRepository.existsByOwnerIdAndNameAndStatusNot(ownerId, name, "DELETED")) {
            throw new IllegalStateException("机器人名称已存在");
        }

        BotEntity bot = new BotEntity();
        bot.setBotId(UUID.randomUUID().toString());
        bot.setName(name);
        bot.setDescription(description);
        bot.setOwnerId(ownerId);
        bot.setBotType(botType != null ? botType : "OPENAI");
        bot.setModelName(modelName != null ? modelName : "gpt-4");
        bot.setMaxTokens(4096);
        bot.setTemperature(0.7);
        bot.setStatus("ACTIVE");
        bot.setIsPublic(false);
        bot.setEnableImageGen(false);
        bot.setEnableSpeechToText(false);
        bot.setRateLimit(60);
        bot.setSessionCount(0L);
        bot.setMessageCount(0L);
        bot.setTotalTokensUsed(0L);
        bot.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        bot.setCreatedAt(LocalDateTime.now());
        bot.setUpdatedAt(LocalDateTime.now());

        return botRepository.save(bot);
    }

    @Transactional
    public BotEntity updateBot(String botId, Map<String, Object> updates) {
        BotEntity bot = botRepository.findByBotId(botId)
            .orElseThrow(() -> new IllegalArgumentException("机器人不存在"));

        if (updates.containsKey("name")) bot.setName((String) updates.get("name"));
        if (updates.containsKey("description")) bot.setDescription((String) updates.get("description"));
        if (updates.containsKey("avatarUrl")) bot.setAvatarUrl((String) updates.get("avatarUrl"));
        if (updates.containsKey("botType")) bot.setBotType((String) updates.get("botType"));
        if (updates.containsKey("modelName")) bot.setModelName((String) updates.get("modelName"));
        if (updates.containsKey("apiKey")) bot.setApiKey((String) updates.get("apiKey"));
        if (updates.containsKey("apiBaseUrl")) bot.setApiBaseUrl((String) updates.get("apiBaseUrl"));
        if (updates.containsKey("webhookUrl")) bot.setWebhookUrl((String) updates.get("webhookUrl"));
        if (updates.containsKey("webhookSecret")) bot.setWebhookSecret((String) updates.get("webhookSecret"));
        if (updates.containsKey("systemPrompt")) bot.setSystemPrompt((String) updates.get("systemPrompt"));
        if (updates.containsKey("maxTokens")) bot.setMaxTokens((Integer) updates.get("maxTokens"));
        if (updates.containsKey("temperature")) bot.setTemperature((Double) updates.get("temperature"));
        if (updates.containsKey("status")) bot.setStatus((String) updates.get("status"));
        if (updates.containsKey("isPublic")) bot.setIsPublic((Boolean) updates.get("isPublic"));
        if (updates.containsKey("enableImageGen")) bot.setEnableImageGen((Boolean) updates.get("enableImageGen"));
        if (updates.containsKey("enableSpeechToText")) bot.setEnableSpeechToText((Boolean) updates.get("enableSpeechToText"));
        if (updates.containsKey("rateLimit")) bot.setRateLimit((Integer) updates.get("rateLimit"));

        bot.setUpdatedAt(LocalDateTime.now());
        return botRepository.save(bot);
    }

    public Optional<BotEntity> getBot(String botId) {
        return botRepository.findByBotId(botId);
    }

    public Optional<BotEntity> getBotByAccessToken(String token) {
        return botRepository.findByAccessToken(token);
    }

    public List<BotEntity> getMyBots(String ownerId) {
        return botRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    public List<BotEntity> getPublicBots() {
        return botRepository.findByStatusAndIsPublicTrueOrderByCreatedAtDesc("ACTIVE");
    }

    @Transactional
    public void deleteBot(String botId) {
        BotEntity bot = botRepository.findByBotId(botId)
            .orElseThrow(() -> new IllegalArgumentException("机器人不存在"));
        bot.setStatus("DELETED");
        botRepository.save(bot);
        botSessionRepository.deleteByBotId(botId);
    }

    // ========== Bot Session ==========

    @Transactional
    public BotSessionEntity createSession(String botId, String userId, String conversationId) {
        BotSessionEntity session = new BotSessionEntity();
        session.setSessionId(UUID.randomUUID().toString());
        session.setBotId(botId);
        session.setUserId(userId);
        session.setConversationId(conversationId);
        session.setContextTokens(0);
        session.setTurnCount(0);
        session.setTotalTokensUsed(0L);
        session.setStatus("ACTIVE");
        session.setCreatedAt(LocalDateTime.now());
        session.setLastMessageAt(LocalDateTime.now());

        BotEntity bot = botRepository.findByBotId(botId).orElse(null);
        if (bot != null) {
            bot.setSessionCount(bot.getSessionCount() + 1);
            bot.setLastActiveAt(LocalDateTime.now());
            botRepository.save(bot);
        }

        return botSessionRepository.save(session);
    }

    @Transactional
    public String chatWithBot(String botId, String userId, String conversationId, String userMessage) {
        BotEntity bot = botRepository.findByBotId(botId)
            .orElseThrow(() -> new IllegalArgumentException("机器人不存在"));

        if (!"ACTIVE".equals(bot.getStatus())) {
            throw new IllegalStateException("机器人未启用");
        }

        BotSessionEntity session = botSessionRepository.findByConversationIdAndStatus(conversationId, "ACTIVE")
            .orElseGet(() -> createSession(botId, userId, conversationId));

        session.setTurnCount(session.getTurnCount() + 1);
        session.setLastMessageAt(LocalDateTime.now());

        // 调用 AI 模型
        String aiResponse = callAIModel(bot, userMessage, session.getContextJson());

        // 简单估算令牌数
        int estimatedTokens = (userMessage.length() + aiResponse.length()) / 4;
        session.setContextTokens(session.getContextTokens() + estimatedTokens);
        session.setTotalTokensUsed(session.getTotalTokensUsed() + estimatedTokens);

        bot.setMessageCount(bot.getMessageCount() + 2);
        bot.setTotalTokensUsed(bot.getTotalTokensUsed() + estimatedTokens);
        bot.setLastActiveAt(LocalDateTime.now());
        botRepository.save(bot);
        botSessionRepository.save(session);

        return aiResponse;
    }

    private String callAIModel(BotEntity bot, String userMessage, String contextJson) {
        if ("CUSTOM".equals(bot.getBotType()) && bot.getWebhookUrl() != null) {
            return callWebhook(bot, userMessage);
        }

        String apiUrl = bot.getApiBaseUrl();
        if (apiUrl == null || apiUrl.isEmpty()) {
            apiUrl = DEFAULT_API_URLS.getOrDefault(bot.getBotType(), DEFAULT_API_URLS.get("OPENAI"));
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if ("OPENAI".equals(bot.getBotType())) {
                headers.set("Authorization", "Bearer " + bot.getApiKey());
                Map<String, Object> requestBody = new HashMap<>();
                List<Map<String, String>> messages = new ArrayList<>();
                if (bot.getSystemPrompt() != null) {
                    messages.add(Map.of("role", "system", "content", bot.getSystemPrompt()));
                }
                messages.add(Map.of("role", "user", "content", userMessage));
                requestBody.put("model", bot.getModelName());
                requestBody.put("messages", messages);
                requestBody.put("max_tokens", bot.getMaxTokens());
                requestBody.put("temperature", bot.getTemperature());

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

                if (response.getBody() != null && response.getBody().containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, String> message = (Map<String, String>) choice.get("message");
                        return message != null ? message.get("content") : "抱歉，AI 未能生成回复";
                    }
                }
            }

            return "AI 模型调用失败，请检查配置";
        } catch (Exception e) {
            return "AI 服务异常: " + e.getMessage();
        }
    }

    private String callWebhook(BotEntity bot, String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (bot.getWebhookSecret() != null) {
                headers.set("X-Webhook-Secret", bot.getWebhookSecret());
            }

            Map<String, String> payload = new HashMap<>();
            payload.put("message", userMessage);
            payload.put("botId", bot.getBotId());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(bot.getWebhookUrl(), entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("reply")) {
                return (String) response.getBody().get("reply");
            }
            return "Webhook 响应格式错误";
        } catch (Exception e) {
            return "Webhook 调用失败: " + e.getMessage();
        }
    }

    public Optional<BotSessionEntity> getSession(String sessionId) {
        return botSessionRepository.findBySessionId(sessionId);
    }

    public List<BotSessionEntity> getUserSessions(String userId) {
        return botSessionRepository.findRecentByUser(userId);
    }

    @Transactional
    public void endSession(String sessionId, String reason) {
        botSessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setStatus("ENDED");
            session.setEndReason(reason);
            session.setEndedAt(LocalDateTime.now());
            botSessionRepository.save(session);
        });
    }

    public Map<String, Object> getBotStats(String botId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", botSessionRepository.countByBotId(botId));
        stats.put("activeSessions", botSessionRepository.countByBotIdAndStatus(botId, "ACTIVE"));
        stats.put("totalTokens", botSessionRepository.sumTokensByBot(botId));
        stats.put("avgTurns", botSessionRepository.avgTurnsByBot(botId));

        botRepository.findByBotId(botId).ifPresent(bot -> {
            stats.put("totalMessages", bot.getMessageCount());
            stats.put("totalTokensUsed", bot.getTotalTokensUsed());
        });

        return stats;
    }

    public List<Map<String, Object>> getBotLeaderboard() {
        List<BotEntity> bots = botRepository.findMostActiveBots();
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        for (BotEntity bot : bots) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("botId", bot.getBotId());
            entry.put("name", bot.getName());
            entry.put("messageCount", bot.getMessageCount());
            entry.put("sessionCount", bot.getSessionCount());
            entry.put("totalTokensUsed", bot.getTotalTokensUsed());
            leaderboard.add(entry);
        }
        return leaderboard;
    }
}
