package com.im.server.chatbot;

public interface SlashCommand {
    String getName();
    String getDescription();
    String getUsage();
    String execute(Bot bot, String args);
}

class HelpCommand implements SlashCommand {
    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Show available commands";
    }

    @Override
    public String getUsage() {
        return "/help [command]";
    }

    @Override
    public String execute(Bot bot, String args) {
        StringBuilder response = new StringBuilder();
        response.append("🤖 **Bot Commands**\n\n");
        response.append("/help - Show this help message\n");
        response.append("/status - Show bot status\n");
        response.append("/info - Show bot information\n");
        
        if (bot.getSlashCommands() != null) {
            response.append("\n**Custom Commands:**\n");
            for (String cmd : bot.getSlashCommands()) {
                response.append(cmd).append("\n");
            }
        }
        
        return response.toString();
    }
}

class StatusCommand implements SlashCommand {
    @Override
    public String getName() {
        return "/status";
    }

    @Override
    public String getDescription() {
        return "Show bot status";
    }

    @Override
    public String getUsage() {
        return "/status";
    }

    @Override
    public String execute(Bot bot, String args) {
        StringBuilder response = new StringBuilder();
        response.append("📊 **Bot Status**\n\n");
        response.append("**Name:** ").append(bot.getName()).append("\n");
        response.append("**Status:** ").append(bot.isEnabled() ? "🟢 Online" : "🔴 Offline").append("\n");
        response.append("**Type:** ").append(bot.getBotType()).append("\n");
        
        if ("AI".equals(bot.getBotType())) {
            response.append("**AI Provider:** ").append(bot.getAiProvider()).append("\n");
            response.append("**Model:** ").append(bot.getAiModel()).append("\n");
        }
        
        response.append("**Global:** ").append(bot.isGlobalEnabled() ? "Yes" : "No").append("\n");
        
        return response.toString();
    }
}

class InfoCommand implements SlashCommand {
    @Override
    public String getName() {
        return "/info";
    }

    @Override
    public String getDescription() {
        return "Show bot information";
    }

    @Override
    public String getUsage() {
        return "/info";
    }

    @Override
    public String execute(Bot bot, String args) {
        StringBuilder response = new StringBuilder();
        response.append("ℹ️ **Bot Information**\n\n");
        response.append("**Name:** ").append(bot.getName()).append("\n");
        response.append("**Description:** ").append(bot.getDescription()).append("\n");
        response.append("**Version:** 1.0.0\n");
        response.append("**Created:** ").append(bot.getCreatedAt()).append("\n");
        
        return response.toString();
    }
}
