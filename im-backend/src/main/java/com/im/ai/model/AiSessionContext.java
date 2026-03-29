package com.im.ai.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI会话上下文
 */
@Data
public class AiSessionContext {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 历史消息列表
     */
    private List<AiMessage> messages;
    
    /**
     * 会话创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后交互时间
     */
    private LocalDateTime lastInteractionTime;
    
    /**
     * 交互次数
     */
    private Integer interactionCount;
    
    /**
     * 当前人格
     */
    private String personality;
    
    /**
     * 会话状态
     */
    private SessionState state;
    
    /**
     * 当前槽位填充状态
     */
    private SlotFillingState slotState;
    
    /**
     * 等待确认的意图
     */
    private IntentResult pendingIntent;
    
    public AiSessionContext() {
        this.messages = new java.util.ArrayList<>();
        this.interactionCount = 0;
        this.state = SessionState.ACTIVE;
        this.slotState = new SlotFillingState();
    }
    
    /**
     * 添加消息
     */
    public void addMessage(AiMessage message) {
        messages.add(message);
        // 限制历史长度
        if (messages.size() > 50) {
            messages.remove(0);
        }
    }
    
    /**
     * 获取最近消息
     */
    public List<AiMessage> getRecentMessages(int count) {
        int start = Math.max(0, messages.size() - count);
        return messages.subList(start, messages.size());
    }
    
    /**
     * 增加交互计数
     */
    public void incrementInteractionCount() {
        interactionCount++;
    }
    
    /**
     * 转换为实体对象
     */
    public AiConversation toEntity() {
        AiConversation entity = new AiConversation();
        entity.setSessionId(sessionId);
        entity.setUserId(userId);
        entity.setMessageCount(messages.size());
        entity.setInteractionCount(interactionCount);
        entity.setCreatedAt(createdAt);
        entity.setLastInteractionTime(lastInteractionTime);
        entity.setPersonality(personality);
        entity.setState(state);
        return entity;
    }
}
