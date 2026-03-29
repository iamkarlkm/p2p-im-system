package com.im.server.announcement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 群公告WebSocket处理器
 * 实时推送新公告、编辑、删除事件
 */
public class AnnouncementWebSocketHandler {
    
    // 群组订阅列表: groupId -> WebSocket连接列表
    private final Map<String, List<WebSocketSession>> groupSubscriptions = new ConcurrentHashMap<>();
    
    // 连接会话信息
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    
    public static class WebSocketSession {
        public String sessionId;
        public String userId;
        
        public WebSocketSession(String sessionId, String userId) {
            this.sessionId = sessionId;
            this.userId = userId;
        }
    }
    
    public static class SessionInfo {
        public String sessionId;
        public String userId;
        public Set<String> subscribedGroups = new HashSet<>();
        public long connectedAt;
        public boolean isActive;
        
        public SessionInfo(String sessionId, String userId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.connectedAt = System.currentTimeMillis();
            this.isActive = true;
        }
    }
    
    // ============ 连接管理 ============
    
    public void onConnect(String sessionId, String userId) {
        sessions.put(sessionId, new SessionInfo(sessionId, userId));
    }
    
    public void onDisconnect(String sessionId) {
        SessionInfo info = sessions.remove(sessionId);
        if (info != null) {
            for (String groupId : info.subscribedGroups) {
                removeFromGroup(groupId, sessionId);
            }
        }
    }
    
    // ============ 群组订阅 ============
    
    public void subscribeToGroup(String sessionId, String groupId) {
        SessionInfo info = sessions.get(sessionId);
        if (info == null) return;
        
        info.subscribedGroups.add(groupId);
        groupSubscriptions
            .computeIfAbsent(groupId, k -> new CopyOnWriteArrayList<>())
            .add(new WebSocketSession(sessionId, info.userId));
    }
    
    public void unsubscribeFromGroup(String sessionId, String groupId) {
        SessionInfo info = sessions.get(sessionId);
        if (info != null) {
            info.subscribedGroups.remove(groupId);
        }
        removeFromGroup(groupId, sessionId);
    }
    
    private void removeFromGroup(String groupId, String sessionId) {
        List<WebSocketSession> subs = groupSubscriptions.get(groupId);
        if (subs != null) {
            subs.removeIf(s -> s.sessionId.equals(sessionId));
            if (subs.isEmpty()) {
                groupSubscriptions.remove(groupId);
            }
        }
    }
    
    // ============ 事件推送 ============
    
    public void notifyNewAnnouncement(Announcement announcement) {
        broadcast(announcement.getGroupId(), buildEvent("announcement.created", announcement.toMap()));
    }
    
    public void notifyAnnouncementUpdated(Announcement announcement) {
        broadcast(announcement.getGroupId(), buildEvent("announcement.updated", announcement.toMap()));
    }
    
    public void notifyAnnouncementDeleted(String groupId, String announcementId) {
        Map<String, Object> data = new HashMap<>();
        data.put("announcementId", announcementId);
        broadcast(groupId, buildEvent("announcement.deleted", data));
    }
    
    public void notifyAnnouncementPinned(Announcement announcement) {
        broadcast(announcement.getGroupId(), buildEvent("announcement.pinned", announcement.toMap()));
    }
    
    public void notifyAnnouncementUnpinned(Announcement announcement) {
        broadcast(announcement.getGroupId(), buildEvent("announcement.unpinned", announcement.toMap()));
    }
    
    // ============ 广播方法 ============
    
    private void broadcast(String groupId, Map<String, Object> message) {
        List<WebSocketSession> subs = groupSubscriptions.get(groupId);
        if (subs == null || subs.isEmpty()) return;
        
        String json = toJson(message);
        for (WebSocketSession sub : subs) {
            sendToSession(sub.sessionId, json);
        }
    }
    
    private Map<String, Object> buildEvent(String eventType, Map<String, Object> data) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", eventType);
        event.put("data", data);
        event.put("timestamp", System.currentTimeMillis());
        return event;
    }
    
    // ============ 工具方法 ============
    
    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            Object v = entry.getValue();
            if (v instanceof String) {
                sb.append("\"").append(escapeJson((String) v)).append("\"");
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v);
            } else {
                sb.append("null");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    private native void sendToSession(String sessionId, String message);
    
    // ============ 统计信息 ============
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", sessions.size());
        stats.put("totalGroupSubscriptions", groupSubscriptions.size());
        stats.put("activeConnections", sessions.values().stream().filter(s -> s.isActive).count());
        return stats;
    }
    
    public Set<String> getSubscribedGroups(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        return info != null ? info.subscribedGroups : Collections.emptySet();
    }
}
