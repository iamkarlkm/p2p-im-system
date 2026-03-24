package com.im.server.typing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class TypingService {
    private final Map<String, Long> typingUsers = new ConcurrentHashMap<>();
    private final Map<String, Long> debounceMap = new ConcurrentHashMap<>();
    private final long DEBOUNCE_MS = 2000;
    private final long EXPIRE_MS = 5000;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final TypingWebSocketHandler wsHandler;

    public TypingService(TypingWebSocketHandler wsHandler) {
        this.wsHandler = wsHandler;
        scheduler.scheduleAtFixedRate(this::cleanupExpired, 1, 1, TimeUnit.SECONDS);
    }

    public void onTypingStart(String conversationId, String userId) {
        long now = System.currentTimeMillis();
        String key = conversationId + ":" + userId;
        Long last = debounceMap.get(key);
        if (last != null && now - last < DEBOUNCE_MS) return;
        debounceMap.put(key, now);
        typingUsers.put(key, now);
        log.debug("Typing started: {} in {}", userId, conversationId);
        wsHandler.broadcastTyping(conversationId, userId, TypingIndicator.TypingState.STARTED);
        scheduleAutoStop(key);
    }

    public void onTypingStop(String conversationId, String userId) {
        String key = conversationId + ":" + userId;
        typingUsers.remove(key);
        debounceMap.remove(key);
        log.debug("Typing stopped: {} in {}", userId, conversationId);
        wsHandler.broadcastTyping(conversationId, userId, TypingIndicator.TypingState.STOPPED);
    }

    public Map<String, Long> getTypingUsers(String conversationId) {
        long now = System.currentTimeMillis();
        Map<String, Long> result = new java.util.LinkedHashMap<>();
        typingUsers.forEach((k, v) -> {
            if (k.startsWith(conversationId + ":") && now - v < EXPIRE_MS) {
                result.put(k, v);
            }
        });
        return result;
    }

    private void scheduleAutoStop(String key) {
        scheduler.schedule(() -> {
            Long ts = typingUsers.get(key);
            if (ts != null && System.currentTimeMillis() - ts > EXPIRE_MS) {
                typingUsers.remove(key);
                String[] parts = key.split(":");
                if (parts.length >= 2) {
                    wsHandler.broadcastTyping(parts[0], parts[1], TypingIndicator.TypingState.STOPPED);
                }
            }
        }, EXPIRE_MS, TimeUnit.MILLISECONDS);
    }

    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        typingUsers.entrySet().removeIf(e -> now - e.getValue() > EXPIRE_MS);
    }
}
