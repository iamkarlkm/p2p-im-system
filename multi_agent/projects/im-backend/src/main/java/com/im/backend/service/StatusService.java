package com.im.backend.service;

import com.im.backend.model.UserStatus;
import com.im.backend.model.UserStatus.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class StatusService {

    private final Map<String, UserStatus> userStatuses = new ConcurrentHashMap<>();
    private final Map<String, List<StatusChangeListener>> listeners = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public StatusService() {
        scheduler.scheduleAtFixedRate(this::checkExpiredStatuses, 30, 30, TimeUnit.SECONDS);
    }

    public UserStatus setStatus(String userId, StatusType status, String customMessage) {
        UserStatus userStatus = UserStatus.builder()
                .userId(userId)
                .status(status)
                .customMessage(customMessage)
                .lastChanged(Instant.now())
                .expiresAt(status.getAutoRevertMs() > 0
                    ? Instant.now().plusMillis(status.getAutoRevertMs())
                    : null)
                .build();

        userStatuses.put(userId, userStatus);
        log.info("User {} status changed to {}", userId, status.getValue());

        notifyListeners(userId, userStatus);
        return userStatus;
    }

    public UserStatus getStatus(String userId) {
        UserStatus status = userStatuses.get(userId);
        if (status != null && status.isExpired()) {
            UserStatus reverted = setStatus(userId, StatusType.ONLINE, null);
            return reverted;
        }
        return status != null ? status : createDefaultStatus(userId);
    }

    public Map<String, UserStatus> getStatuses(List<String> userIds) {
        Map<String, UserStatus> result = new HashMap<>();
        for (String userId : userIds) {
            result.put(userId, getStatus(userId));
        }
        return result;
    }

    public List<UserStatus> getOnlineUsers() {
        List<UserStatus> online = new ArrayList<>();
        for (UserStatus status : userStatuses.values()) {
            if (status.getStatus() != StatusType.OFFLINE && status.getStatus() != StatusType.INVISIBLE) {
                if (!status.isExpired()) {
                    online.add(status);
                }
            }
        }
        return online;
    }

    public boolean isUserOnline(String userId) {
        UserStatus status = getStatus(userId);
        return status.getStatus() != StatusType.OFFLINE 
            && status.getStatus() != StatusType.INVISIBLE
            && !status.isExpired();
    }

    public void registerListener(String userId, StatusChangeListener listener) {
        listeners.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void removeListener(String userId, StatusChangeListener listener) {
        List<StatusChangeListener> list = listeners.get(userId);
        if (list != null) {
            list.remove(listener);
        }
    }

    private void notifyListeners(String userId, UserStatus status) {
        List<StatusChangeListener> list = listeners.get(userId);
        if (list != null) {
            for (StatusChangeListener listener : list) {
                try {
                    listener.onStatusChanged(status);
                } catch (Exception e) {
                    log.error("Error notifying status listener", e);
                }
            }
        }
    }

    private void checkExpiredStatuses() {
        Instant now = Instant.now();
        for (Map.Entry<String, UserStatus> entry : userStatuses.entrySet()) {
            UserStatus status = entry.getValue();
            if (status.isExpired() && status.getExpiresAt() != null) {
                log.info("Auto-reverting status for user {} from {} to ONLINE",
                    entry.getKey(), status.getStatus());
                setStatus(entry.getKey(), StatusType.ONLINE, null);
            }
        }
    }

    private UserStatus createDefaultStatus(String userId) {
        return UserStatus.builder()
                .userId(userId)
                .status(StatusType.OFFLINE)
                .lastChanged(Instant.now())
                .build();
    }

    public interface StatusChangeListener {
        void onStatusChanged(UserStatus status);
    }
}
