package com.im.backend.integration;

import com.im.backend.model.TranslationRequest;
import com.im.backend.model.TranslationResult;
import com.im.backend.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息翻译集成器
 * 将翻译服务集成到即时通讯系统中
 */
@Component
public class MessageTranslationIntegration {

    @Autowired
    private TranslationService translationService;

    /**
     * 翻译聊天消息
     */
    public TranslatedMessage translateChatMessage(ChatMessage message, String targetLanguage) {
        TranslationRequest request = new TranslationRequest();
        request.setText(message.getContent());
        request.setTargetLanguage(targetLanguage);
        request.setUserId(message.getSenderId());
        
        TranslationResult result = translationService.translate(request);
        
        return new TranslatedMessage(
            message.getId(),
            message.getContent(),
            result.getTranslatedText(),
            result.getSourceLanguage(),
            targetLanguage,
            result.getConfidence()
        );
    }

    /**
     * 批量翻译消息列表
     */
    public List<TranslatedMessage> translateMessageList(
            List<ChatMessage> messages, 
            String targetLanguage) {
        return messages.stream()
            .map(msg -> translateChatMessage(msg, targetLanguage))
            .collect(Collectors.toList());
    }

    /**
     * 自动检测并翻译
     */
    public TranslatedMessage autoTranslate(ChatMessage message, String userPreferredLanguage) {
        TranslationRequest request = new TranslationRequest();
        request.setText(message.getContent());
        request.setAutoDetect(true);
        request.setTargetLanguage(userPreferredLanguage);
        
        TranslationResult result = translationService.translate(request);
        
        // 如果源语言和目标语言相同，不返回翻译
        if (result.getSourceLanguage().equals(userPreferredLanguage)) {
            return null;
        }
        
        return new TranslatedMessage(
            message.getId(),
            message.getContent(),
            result.getTranslatedText(),
            result.getSourceLanguage(),
            userPreferredLanguage,
            result.getConfidence()
        );
    }

    /**
     * 聊天消息类
     */
    public static class ChatMessage {
        private String id;
        private String content;
        private String senderId;
        private long timestamp;

        public ChatMessage(String id, String content, String senderId) {
            this.id = id;
            this.content = content;
            this.senderId = senderId;
            this.timestamp = System.currentTimeMillis();
        }

        public String getId() { return id; }
        public String getContent() { return content; }
        public String getSenderId() { return senderId; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * 翻译后的消息
     */
    public static class TranslatedMessage {
        private String messageId;
        private String originalText;
        private String translatedText;
        private String sourceLanguage;
        private String targetLanguage;
        private double confidence;
        private long timestamp;

        public TranslatedMessage(String messageId, String originalText, 
                                String translatedText, String sourceLanguage,
                                String targetLanguage, double confidence) {
            this.messageId = messageId;
            this.originalText = originalText;
            this.translatedText = translatedText;
            this.sourceLanguage = sourceLanguage;
            this.targetLanguage = targetLanguage;
            this.confidence = confidence;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessageId() { return messageId; }
        public String getOriginalText() { return originalText; }
        public String getTranslatedText() { return translatedText; }
        public String getSourceLanguage() { return sourceLanguage; }
        public String getTargetLanguage() { return targetLanguage; }
        public double getConfidence() { return confidence; }
        public long getTimestamp() { return timestamp; }
    }
}
