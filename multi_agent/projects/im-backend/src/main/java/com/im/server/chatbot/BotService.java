package com.im.server.chatbot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class BotService {
    private final BotRepository botRepository;
    private final Map<String, AIProvider> aiProviders = new HashMap<>();
    private final Map<String, SlashCommand> slashCommands = new HashMap<>();

    public BotService(BotRepository botRepository) {
        this.botRepository = botRepository;
        registerDefaultAIProviders();
        registerDefaultSlashCommands();
    }

    private void registerDefaultAIProviders() {
        aiProviders.put("OPENAI", new OpenAIProvider());
        aiProviders.put("CLAUDE", new ClaudeProvider());
        aiProviders.put("GEMINI", new GeminiProvider());
    }

    private void registerDefaultSlashCommands() {
        slashCommands.put("/help", new HelpCommand());
        slashCommands.put("/status", new StatusCommand());
        slashCommands.put("/info", new InfoCommand());
    }

    public Bot createBot(Bot bot, String ownerId) {
        if (botRepository.countByOwnerId(ownerId) >= 10) {
            throw new BotException("Maximum number of bots (10) reached");
        }
        bot.setOwnerId(ownerId);
        bot.setCreatedAt(LocalDateTime.now());
        bot.setUpdatedAt(LocalDateTime.now());
        return botRepository.save(bot);
    }

    public Bot updateBot(String botId, Bot updatedBot) {
        Bot existingBot = botRepository.findById(botId)
                .orElseThrow(() -> new BotException("Bot not found: " + botId));
        
        existingBot.setName(updatedBot.getName());
        existingBot.setDescription(updatedBot.getDescription());
        existingBot.setAvatarUrl(updatedBot.getAvatarUrl());
        existingBot.setEnabled(updatedBot.isEnabled());
        existingBot.setGlobalEnabled(updatedBot.isGlobalEnabled());
        existingBot.setAllowedGroupIds(updatedBot.getAllowedGroupIds());
        existingBot.setConfig(updatedBot.getConfig());
        existingBot.setUpdatedAt(LocalDateTime.now());
        
        return botRepository.save(existingBot);
    }

    public void deleteBot(String botId) {
        if (!botRepository.existsById(botId)) {
            throw new BotException("Bot not found: " + botId);
        }
        botRepository.deleteById(botId);
    }

    public Bot getBot(String botId) {
        return botRepository.findById(botId)
                .orElseThrow(() -> new BotException("Bot not found: " + botId));
    }

    public List<Bot> getUserBots(String userId) {
        return botRepository.findByOwnerId(userId);
    }

    public List<Bot> getGroupBots(String groupId) {
        return botRepository.findByGroupId(groupId);
    }

    public String processMessage(String botId, String userId, String message) {
        Bot bot = getBot(botId);
        
        if (!bot.isEnabled()) {
            throw new BotException("Bot is disabled");
        }

        if (message.startsWith("/")) {
            return processSlashCommand(bot, message);
        }

        if ("AI".equals(bot.getBotType())) {
            return processAIMessage(bot, userId, message);
        } else if ("WEBHOOK".equals(bot.getBotType())) {
            return callWebhook(bot, userId, message);
        }

        return "Unknown bot type";
    }

    private String processSlashCommand(Bot bot, String message) {
        String[] parts = message.split(" ", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        SlashCommand handler = slashCommands.get(command);
        if (handler != null) {
            return handler.execute(bot, args);
        }

        return "Unknown command: " + command;
    }

    private String processAIMessage(Bot bot, String userId, String message) {
        AIProvider provider = aiProviders.get(bot.getAiProvider());
        if (provider == null) {
            throw new BotException("AI provider not configured: " + bot.getAiProvider());
        }
        return provider.generateResponse(bot, userId, message);
    }

    private String callWebhook(Bot bot, String userId, String message) {
        if (bot.getWebhookUrl() == null) {
            throw new BotException("Webhook URL not configured");
        }
        return "Webhook called for: " + bot.getWebhookUrl();
    }

    public void registerAIProvider(String name, AIProvider provider) {
        aiProviders.put(name, provider);
    }

    public void registerSlashCommand(String name, SlashCommand command) {
        slashCommands.put(name, command);
    }
}
