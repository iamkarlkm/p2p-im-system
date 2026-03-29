package com.im.server.chatbot;

public class BotException extends RuntimeException {
    private final String errorCode;

    public BotException(String message) {
        super(message);
        this.errorCode = "BOT_ERROR";
    }

    public BotException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BotException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BOT_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static class BotNotFoundException extends BotException {
        public BotNotFoundException(String botId) {
            super("Bot not found: " + botId, "BOT_NOT_FOUND");
        }
    }

    public static class BotDisabledException extends BotException {
        public BotDisabledException(String botId) {
            super("Bot is disabled: " + botId, "BOT_DISABLED");
        }
    }

    public static class MaxBotsReachedException extends BotException {
        public MaxBotsReachedException() {
            super("Maximum number of bots reached", "MAX_BOTS_REACHED");
        }
    }

    public static class AIProviderException extends BotException {
        public AIProviderException(String message, Throwable cause) {
            super(message, "AI_PROVIDER_ERROR");
            initCause(cause);
        }
    }

    public static class WebhookException extends BotException {
        public WebhookException(String message) {
            super(message, "WEBHOOK_ERROR");
        }
    }

    public static class UnauthorizedException extends BotException {
        public UnauthorizedException() {
            super("Unauthorized access", "UNAUTHORIZED");
        }
    }

    public static class InvalidCommandException extends BotException {
        public InvalidCommandException(String command) {
            super("Invalid command: " + command, "INVALID_COMMAND");
        }
    }
}
