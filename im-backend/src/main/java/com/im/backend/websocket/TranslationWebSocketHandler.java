package com.im.backend.websocket;

import com.im.backend.model.TranslationRequest;
import com.im.backend.model.TranslationResult;
import com.im.backend.service.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket翻译处理器
 * 支持实时流式翻译
 */
@Component
public class TranslationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private TranslationService translationService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 存储活跃的翻译会话
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    
    // 用户的翻译偏好设置
    private final Map<String, UserTranslationPreference> userPreferences = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        activeSessions.put(sessionId, session);
        
        // 发送连接成功消息
        sendMessage(session, "connected", Map.of(
            "sessionId", sessionId,
            "message", "Translation service connected"
        ));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            // 解析请求
            WebSocketRequest request = objectMapper.readValue(payload, WebSocketRequest.class);
            
            switch (request.getType()) {
                case "translate":
                    handleTranslate(session, request.getData());
                    break;
                case "stream_translate":
                    handleStreamTranslate(session, request.getData());
                    break;
                case "set_preference":
                    handleSetPreference(session, request.getData());
                    break;
                case "ping":
                    sendMessage(session, "pong", Map.of("timestamp", System.currentTimeMillis()));
                    break;
                default:
                    sendError(session, "Unknown request type: " + request.getType());
            }
        } catch (Exception e) {
            sendError(session, "Error processing request: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        activeSessions.remove(sessionId);
        userPreferences.remove(sessionId);
    }

    /**
     * 处理翻译请求
     */
    private void handleTranslate(WebSocketSession session, Map<String, Object> data) {
        try {
            TranslationRequest request = new TranslationRequest();
            request.setText((String) data.get("text"));
            request.setSourceLanguage((String) data.get("sourceLanguage"));
            request.setTargetLanguage((String) data.get("targetLanguage"));
            request.setEngine((String) data.get("engine"));
            request.setSessionId(session.getId());
            
            // 应用用户偏好
            UserTranslationPreference pref = userPreferences.get(session.getId());
            if (pref != null) {
                if (request.getTargetLanguage() == null) {
                    request.setTargetLanguage(pref.getDefaultTargetLanguage());
                }
                if (request.getEngine() == null) {
                    request.setEngine(pref.getPreferredEngine());
                }
            }
            
            TranslationResult result = translationService.translate(request);
            
            sendMessage(session, "translation_result", Map.of(
                "result", result,
                "requestId", data.get("requestId")
            ));
            
        } catch (Exception e) {
            sendError(session, "Translation failed: " + e.getMessage());
        }
    }

    /**
     * 处理流式翻译
     */
    private void handleStreamTranslate(WebSocketSession session, Map<String, Object> data) {
        try {
            TranslationRequest request = new TranslationRequest();
            request.setText((String) data.get("text"));
            request.setSourceLanguage((String) data.get("sourceLanguage"));
            request.setTargetLanguage((String) data.get("targetLanguage"));
            
            // 发送开始消息
            sendMessage(session, "stream_start", Map.of("requestId", data.get("requestId")));
            
            // 流式翻译
            translationService.translateStream(request, result -> {
                try {
                    sendMessage(session, "stream_chunk", Map.of(
                        "chunk", result.getTranslatedText(),
                        "requestId", data.get("requestId"),
                        "isFinal", false
                    ));
                } catch (Exception e) {
                    System.err.println("Error sending stream chunk: " + e.getMessage());
                }
            });
            
            // 发送结束消息
            sendMessage(session, "stream_end", Map.of("requestId", data.get("requestId")));
            
        } catch (Exception e) {
            sendError(session, "Stream translation failed: " + e.getMessage());
        }
    }

    /**
     * 处理设置偏好
     */
    private void handleSetPreference(WebSocketSession session, Map<String, Object> data) {
        UserTranslationPreference pref = new UserTranslationPreference();
        pref.setDefaultTargetLanguage((String) data.get("defaultTargetLanguage"));
        pref.setPreferredEngine((String) data.get("preferredEngine"));
        pref.setAutoTranslate((Boolean) data.getOrDefault("autoTranslate", false));
        
        userPreferences.put(session.getId(), pref);
        
        sendMessage(session, "preference_set", Map.of("success", true));
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, String type, Map<String, Object> data) {
        try {
            Map<String, Object> message = Map.of(
                "type", type,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            System.err.println("Error sending WebSocket message: " + e.getMessage());
        }
    }

    /**
     * 发送错误消息
     */
    private void sendError(WebSocketSession session, String errorMessage) {
        sendMessage(session, "error", Map.of("message", errorMessage));
    }

    /**
     * WebSocket请求内部类
     */
    private static class WebSocketRequest {
        private String type;
        private Map<String, Object> data;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }

    /**
     * 用户翻译偏好
     */
    private static class UserTranslationPreference {
        private String defaultTargetLanguage;
        private String preferredEngine;
        private boolean autoTranslate;

        public String getDefaultTargetLanguage() { return defaultTargetLanguage; }
        public void setDefaultTargetLanguage(String lang) { this.defaultTargetLanguage = lang; }
        public String getPreferredEngine() { return preferredEngine; }
        public void setPreferredEngine(String engine) { this.preferredEngine = engine; }
        public boolean isAutoTranslate() { return autoTranslate; }
        public void setAutoTranslate(boolean auto) { this.autoTranslate = auto; }
    }
}
