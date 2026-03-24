package com.im.backend.service;

import com.im.backend.entity.ScreenshotEvent;
import com.im.backend.entity.ScreenshotSettings;
import com.im.backend.dto.ScreenshotEventRequest;
import com.im.backend.dto.ScreenshotEventResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ScreenshotNotificationService {

    private final Map<String, ScreenshotEvent> eventStore = new ConcurrentHashMap<>();
    private final Map<Long, ScreenshotSettings> settingsStore = new ConcurrentHashMap<>();

    public ScreenshotEventResponse reportScreenshot(Long userId, String username, ScreenshotEventRequest request) {
        ScreenshotSettings settings = getSettings(userId);
        
        if (!settings.isEnableScreenshotNotification() || !settings.isNotifyOnCapture()) {
            return null;
        }

        String eventId = UUID.randomUUID().toString();
        ScreenshotEvent event = new ScreenshotEvent();
        event.setEventId(eventId);
        event.setUserId(userId);
        event.setConversationId(request.getConversationId());
        event.setConversationType(request.getConversationType());
        event.setCapturedByUserId(userId);
        event.setCapturedByUsername(username);
        event.setScreenshotTime(LocalDateTime.now());
        event.setDeviceType(request.getDeviceType());
        event.setDeviceInfo(request.getDeviceInfo());
        event.setNotified(false);

        eventStore.put(eventId, event);

        List<Long> targetUserIds = getTargetUsers(userId, request.getConversationId(), request.getConversationType());
        for (Long targetUserId : targetUserIds) {
            ScreenshotSettings targetSettings = getSettings(targetUserId);
            if (targetSettings.isReceiveScreenshotAlerts()) {
                notifyTargetUser(targetUserId, event);
            }
        }

        return ScreenshotEventResponse.fromEntity(event);
    }

    private List<Long> getTargetUsers(Long senderId, Long conversationId, String conversationType) {
        if ("private".equals(conversationType)) {
            return Collections.singletonList(conversationId);
        } else {
            return Collections.singletonList(conversationId);
        }
    }

    private void notifyTargetUser(Long targetUserId, ScreenshotEvent event) {
        event.setNotified(true);
    }

    public ScreenshotSettings getSettings(Long userId) {
        return settingsStore.computeIfAbsent(userId, ScreenshotSettings::defaultSettings);
    }

    public ScreenshotSettings updateSettings(Long userId, ScreenshotSettings newSettings) {
        newSettings.setUserId(userId);
        settingsStore.put(userId, newSettings);
        return newSettings;
    }

    public List<ScreenshotEventResponse> getScreenshotHistory(Long userId, int limit) {
        return eventStore.values().stream()
                .filter(e -> e.getUserId().equals(userId))
                .sorted((a, b) -> b.getScreenshotTime().compareTo(a.getScreenshotTime()))
                .limit(limit)
                .map(ScreenshotEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean deleteEvent(String eventId) {
        return eventStore.remove(eventId) != null;
    }

    public boolean clearHistory(Long userId) {
        List<String> keysToRemove = eventStore.values().stream()
                .filter(e -> e.getUserId().equals(userId))
                .map(ScreenshotEvent::getEventId)
                .collect(Collectors.toList());
        keysToRemove.forEach(eventStore::remove);
        return true;
    }
}
