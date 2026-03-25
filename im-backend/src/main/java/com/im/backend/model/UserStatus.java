package com.im.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatus {
    
    private String userId;
    private StatusType status;
    private String customMessage;
    private Instant lastChanged;
    private Instant expiresAt;
    
    public enum StatusType {
        ONLINE("online", "在线", 0),
        AWAY("away", "离开", 5 * 60 * 1000L),
        BUSY("busy", "忙碌", 0),
        DND("dnd", "勿扰", 0),
        INVISIBLE("invisible", "隐身", 0),
        OFFLINE("offline", "离线", 0);
        
        private final String value;
        private final String label;
        private final long autoRevertMs;
        
        StatusType(String value, String label, long autoRevertMs) {
            this.value = value;
            this.label = label;
            this.autoRevertMs = autoRevertMs;
        }
        
        public String getValue() { return value; }
        public String getLabel() { return label; }
        public long getAutoRevertMs() { return autoRevertMs; }
        
        public static StatusType fromValue(String value) {
            for (StatusType s : values()) {
                if (s.value.equalsIgnoreCase(value)) return s;
            }
            return ONLINE;
        }
    }
    
    public static UserStatus create(String userId, StatusType status) {
        return UserStatus.builder()
                .userId(userId)
                .status(status)
                .lastChanged(Instant.now())
                .expiresAt(status.getAutoRevertMs() > 0 
                    ? Instant.now().plusMillis(status.getAutoRevertMs()) 
                    : null)
                .build();
    }
    
    public boolean isAutoRevert() {
        return status.getAutoRevertMs() > 0;
    }
    
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
    
    public String getStatusColor() {
        switch (status) {
            case ONLINE: return "#4CAF50";
            case AWAY: return "#FFC107";
            case BUSY: return "#FF5722";
            case DND: return "#F44336";
            case INVISIBLE: return "#9E9E9E";
            case OFFLINE: return "#BDBDBD";
            default: return "#BDBDBD";
        }
    }
    
    public String getStatusIcon() {
        switch (status) {
            case ONLINE: return "🟢";
            case AWAY: return "🟡";
            case BUSY: return "🟠";
            case DND: return "🔴";
            case INVISIBLE: return "⚫";
            case OFFLINE: return "⚪";
            default: return "⚪";
        }
    }
}
