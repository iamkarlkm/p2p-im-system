package com.im.server.chatbot;

public interface AIProvider {
    String generateResponse(Bot bot, String userId, String message);
    
    String generateStreamResponse(Bot bot, String userId, String message, AIResponseCallback callback);
    
    boolean validateConfig(BotConfig config);
    
    String getProviderName();
}

interface AIResponseCallback {
    void onChunk(String chunk);
    void onComplete(String fullResponse);
    void onError(String error);
}

class OpenAIProvider implements AIProvider {
    @Override
    public String generateResponse(Bot bot, String userId, String message) {
        return "OpenAI response to: " + message;
    }

    @Override
    public String generateStreamResponse(Bot bot, String userId, String message, AIResponseCallback callback) {
        String response = generateResponse(bot, userId, message);
        for (char c : response.toCharArray()) {
            callback.onChunk(String.valueOf(c));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        callback.onComplete(response);
        return response;
    }

    @Override
    public boolean validateConfig(BotConfig config) {
        return config.getApiKey() != null && !config.getApiKey().isEmpty();
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }
}

class ClaudeProvider implements AIProvider {
    @Override
    public String generateResponse(Bot bot, String userId, String message) {
        return "Claude response to: " + message;
    }

    @Override
    public String generateStreamResponse(Bot bot, String userId, String message, AIResponseCallback callback) {
        String response = generateResponse(bot, userId, message);
        callback.onComplete(response);
        return response;
    }

    @Override
    public boolean validateConfig(BotConfig config) {
        return config.getApiKey() != null && !config.getApiKey().isEmpty();
    }

    @Override
    public String getProviderName() {
        return "Claude";
    }
}

class GeminiProvider implements AIProvider {
    @Override
    public String generateResponse(Bot bot, String userId, String message) {
        return "Gemini response to: " + message;
    }

    @Override
    public String generateStreamResponse(Bot bot, String userId, String message, AIResponseCallback callback) {
        String response = generateResponse(bot, userId, message);
        callback.onComplete(response);
        return response;
    }

    @Override
    public boolean validateConfig(BotConfig config) {
        return config.getApiKey() != null && !config.getApiKey().isEmpty();
    }

    @Override
    public String getProviderName() {
        return "Gemini";
    }
}
