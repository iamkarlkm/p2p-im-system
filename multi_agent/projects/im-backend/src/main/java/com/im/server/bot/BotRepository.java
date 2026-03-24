package com.im.server.bot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 机器人仓储层
 */
public class BotRepository {
    
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final Map<String, BotConversation> conversations = new ConcurrentHashMap<>();
    
    public Bot saveBot(Bot bot) {
        bot.setUpdatedAt(java.time.LocalDateTime.now());
        bots.put(bot.getBotId(), bot);
        return bot;
    }
    
    public Optional<Bot> findBotById(String botId) {
        return Optional.ofNullable(bots.get(botId));
    }
    
    public List<Bot> findAllBots() {
        return new ArrayList<>(bots.values());
    }
    
    public List<Bot> findBotsByOwner(String ownerId) {
        return bots.values().stream()
            .filter(b -> ownerId.equals(b.getOwnerId()))
            .toList();
    }
    
    public List<Bot> findBotsByGroup(String groupId) {
        return bots.values().stream()
            .filter(b -> groupId.equals(b.getGroupId()))
            .toList();
    }
    
    public List<Bot> findActiveBotsByGroup(String groupId) {
        return bots.values().stream()
            .filter(b -> groupId.equals(b.getGroupId()))
            .filter(b -> b.getStatus() == Bot.BotStatus.ACTIVE)
            .toList();
    }
    
    public boolean deleteBot(String botId) {
        return bots.remove(botId) != null;
    }
    
    public boolean existsById(String botId) {
        return bots.containsKey(botId);
    }
    
    public List<Bot> findBotsByType(Bot.BotType type) {
        return bots.values().stream()
            .filter(b -> b.getType() == type)
            .toList();
    }
    
    // 对话管理
    public BotConversation saveConversation(BotConversation conv) {
        conversations.put(conv.getConversationId(), conv);
        return conv;
    }
    
    public Optional<BotConversation> findConversation(String convId) {
        return Optional.ofNullable(conversations.get(convId));
    }
    
    public List<BotConversation> findConversationsByUser(String userId) {
        return conversations.values().stream()
            .filter(c -> userId.equals(c.getUserId()))
            .toList();
    }
    
    public void deleteConversation(String convId) {
        conversations.remove(convId);
    }
}
