package com.im.server.chatbot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BotRepository {
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userBots = new ConcurrentHashMap<>();
    private final Map<String, List<String>> groupBots = new ConcurrentHashMap<>();

    public Bot save(Bot bot) {
        if (bot.getId() == null) {
            bot.setId(UUID.randomUUID().toString());
        }
        bots.put(bot.getId(), bot);
        
        if (bot.getOwnerId() != null) {
            userBots.computeIfAbsent(bot.getOwnerId(), k -> new ArrayList<>()).add(bot.getId());
        }
        
        if (bot.getAllowedGroupIds() != null) {
            for (String groupId : bot.getAllowedGroupIds()) {
                groupBots.computeIfAbsent(groupId, k -> new ArrayList<>()).add(bot.getId());
            }
        }
        
        return bot;
    }

    public Optional<Bot> findById(String id) {
        return Optional.ofNullable(bots.get(id));
    }

    public List<Bot> findByOwnerId(String ownerId) {
        List<String> botIds = userBots.getOrDefault(ownerId, Collections.emptyList());
        return botIds.stream()
                .map(bots::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Bot> findByGroupId(String groupId) {
        List<String> botIds = groupBots.getOrDefault(groupId, Collections.emptyList());
        return botIds.stream()
                .map(bots::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Bot> findAllEnabled() {
        return bots.values().stream()
                .filter(Bot::isEnabled)
                .toList();
    }

    public List<Bot> findGlobalEnabled() {
        return bots.values().stream()
                .filter(Bot::isEnabled)
                .filter(Bot::isGlobalEnabled)
                .toList();
    }

    public void deleteById(String id) {
        Bot bot = bots.remove(id);
        if (bot != null && bot.getOwnerId() != null) {
            List<String> userBotList = userBots.get(bot.getOwnerId());
            if (userBotList != null) {
                userBotList.remove(id);
            }
        }
    }

    public boolean existsById(String id) {
        return bots.containsKey(id);
    }

    public long countByOwnerId(String ownerId) {
        return userBots.getOrDefault(ownerId, Collections.emptyList()).size();
    }
}
