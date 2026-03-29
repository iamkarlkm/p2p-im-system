package com.im.server.chatbot;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
public class BotController {
    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping
    public Bot createBot(@RequestBody Bot bot, @RequestHeader("X-User-Id") String userId) {
        return botService.createBot(bot, userId);
    }

    @GetMapping("/{botId}")
    public Bot getBot(@PathVariable String botId) {
        return botService.getBot(botId);
    }

    @PutMapping("/{botId}")
    public Bot updateBot(@PathVariable String botId, @RequestBody Bot bot) {
        return botService.updateBot(botId, bot);
    }

    @DeleteMapping("/{botId}")
    public void deleteBot(@PathVariable String botId) {
        botService.deleteBot(botId);
    }

    @GetMapping("/user/{userId}")
    public List<Bot> getUserBots(@PathVariable String userId) {
        return botService.getUserBots(userId);
    }

    @GetMapping("/group/{groupId}")
    public List<Bot> getGroupBots(@PathVariable String groupId) {
        return botService.getGroupBots(groupId);
    }

    @PostMapping("/{botId}/message")
    public Map<String, String> sendMessage(
            @PathVariable String botId,
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String message = request.get("message");
        String response = botService.processMessage(botId, userId, message);
        return Map.of("response", response);
    }

    @GetMapping("/{botId}/commands")
    public List<String> getSlashCommands(@PathVariable String botId) {
        Bot bot = botService.getBot(botId);
        return bot.getSlashCommands();
    }

    @PostMapping("/{botId}/enable")
    public Bot enableBot(@PathVariable String botId) {
        Bot bot = botService.getBot(botId);
        bot.setEnabled(true);
        return botService.updateBot(botId, bot);
    }

    @PostMapping("/{botId}/disable")
    public Bot disableBot(@PathVariable String botId) {
        Bot bot = botService.getBot(botId);
        bot.setEnabled(false);
        return botService.updateBot(botId, bot);
    }
}
