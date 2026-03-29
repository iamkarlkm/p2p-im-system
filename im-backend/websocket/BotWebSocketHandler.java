package com.im.backend.websocket;

import com.im.backend.entity.BotEntity;
import com.im.backend.service.BotService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AI 聊天机器人 WebSocket 事件处理器
 * 支持机器人消息接收、主动推送、Webhook 触发事件
 */
@Component
public class BotWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentMap<String, WebSocketSession> botSessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> sessionToBotId = new ConcurrentHashMap<>();
    private final BotService botService;

    public BotWebSocketHandler(BotService botService) {
        this.botService = botService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String botId = extractBotId(session);
        if (botId != null) {
            botSessions.put(botId, session);
            sessionToBotId.put(session.getId(), botId);
            session.sendMessage(new TextMessage("{\"type\":\"connected\",\"botId\":\"" + botId + "\"}"));
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String botId = sessionToBotId.get(session.getId());
        if (botId == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        String payload = message.getPayload();
        // 处理机器人接收的消息
        handleBotMessage(botId, payload, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String botId = sessionToBotId.remove(session.getId());
        if (botId != null) {
            botSessions.remove(botId);
        }
    }

    private String extractBotId(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        if (attributes.containsKey("botId")) {
            return (String) attributes.get("botId");
        }

        // 从查询参数中提取 botId
        String query = session.getUri().getQuery();
        if (query != null && query.contains("botId=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("botId=")) {
                    return param.substring(6);
                }
            }
        }
        return null;
    }

    private void handleBotMessage(String botId, String payload, WebSocketSession session) throws IOException {
        try {
            // 解析消息类型
            // {"type":"message","userId":"user123","conversationId":"conv456","message":"你好"}
            // {"type":"stats","interval":"daily"}
            // {"type":"webhook","event":"trigger"}

            if (payload.contains("\"type\":\"message\"")) {
                String userId = extractValue(payload, "userId");
                String conversationId = extractValue(payload, "conversationId");
                String message = extractValue(payload, "message");

                if (userId != null && conversationId != null && message != null) {
                    String reply = botService.chatWithBot(botId, userId, conversationId, message);
                    session.sendMessage(new TextMessage("{\"type\":\"reply\",\"reply\":\"" + reply + "\"}"));
                }
            } else if (payload.contains("\"type\":\"ping\"")) {
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            } else if (payload.contains("\"type\":\"stats\"")) {
                Map<String, Object> stats = botService.getBotStats(botId);
                session.sendMessage(new TextMessage("{\"type\":\"stats\",\"data\":" + mapToJson(stats) + "}"));
            }
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}"));
        }
    }

    public void sendBotEvent(String botId, String eventType, String data) {
        WebSocketSession session = botSessions.get(botId);
        if (session != null && session.isOpen()) {
            try {
                String message = "{\"type\":\"event\",\"event\":\"" + eventType + "\",\"data\":" + data + "}";
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                // 连接可能已断开
                botSessions.remove(botId);
            }
        }
    }

    public void broadcastBotStats(String eventType, Map<String, Object> data) {
        String jsonData = mapToJson(data);
        for (Map.Entry<String, WebSocketSession> entry : botSessions.entrySet()) {
            if (entry.getValue().isOpen()) {
                try {
                    String message = "{\"type\":\"broadcast\",\"event\":\"" + eventType + "\",\"data\":" + jsonData + "}";
                    entry.getValue().sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    // 忽略单个连接错误
                }
            }
        }
    }

    public boolean isBotConnected(String botId) {
        WebSocketSession session = botSessions.get(botId);
        return session != null && session.isOpen();
    }

    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey);
        if (start == -1) return null;
        start += searchKey.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private String mapToJson(Map<String, Object> map) {
        if (map == null) return "{}";
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
