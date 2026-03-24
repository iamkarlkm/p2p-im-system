package com.im.server.chatbot;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BotWebSocketHandler {
    private final BotService botService;
    private final Map<String, BotSession> sessions = new ConcurrentHashMap<>();

    public BotWebSocketHandler(BotService botService) {
        this.botService = botService;
    }

    public void handleMessage(String sessionId, BotMessage message) {
        BotSession session = sessions.get(sessionId);
        if (session == null) {
            session = new BotSession(sessionId, message.getBotId());
            sessions.put(sessionId, session);
        }

        session.addMessage(message);

        if (message.getContent().startsWith("/")) {
            handleSlashCommand(session, message);
        } else {
            handleAIMessage(session, message);
        }
    }

    private void handleSlashCommand(BotSession session, BotMessage message) {
        try {
            String response = botService.processMessage(
                    session.getBotId(),
                    message.getSenderId(),
                    message.getContent()
            );

            BotMessage responseMsg = new BotMessage();
            responseMsg.setBotId(session.getBotId());
            responseMsg.setSenderId(session.getBotId());
            responseMsg.setContent(response);
            responseMsg.setTimestamp(LocalDateTime.now());
            responseMsg.setMessageType("text");

            session.addMessage(responseMsg);
            broadcastMessage(session.getSessionId(), responseMsg);
        } catch (Exception e) {
            sendError(session.getSessionId(), e.getMessage());
        }
    }

    private void handleAIMessage(BotSession session, BotMessage message) {
        try {
            String response = botService.processMessage(
                    session.getBotId(),
                    message.getSenderId(),
                    message.getContent()
            );

            BotMessage responseMsg = new BotMessage();
            responseMsg.setBotId(session.getBotId());
            responseMsg.setSenderId(session.getBotId());
            responseMsg.setContent(response);
            responseMsg.setTimestamp(LocalDateTime.now());
            responseMsg.setMessageType("text");

            session.addMessage(responseMsg);
            broadcastMessage(session.getSessionId(), responseMsg);
        } catch (Exception e) {
            sendError(session.getSessionId(), e.getMessage());
        }
    }

    private void broadcastMessage(String sessionId, BotMessage message) {
    }

    private void sendError(String sessionId, String error) {
    }

    public void closeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public BotSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
