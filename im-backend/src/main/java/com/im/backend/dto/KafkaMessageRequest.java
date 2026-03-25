package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessageRequest {
    
    private String topic;
    
    private String key;
    
    private Object message;
    
    private MessagePriority priority;
    
    public enum MessagePriority {
        HIGH(1),
        NORMAL(0),
        LOW(-1);
        
        private final int level;
        
        MessagePriority(int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return level;
        }
    }
}
