package com.im.ai.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 槽位填充状态
 */
@Data
public class SlotFillingState {
    
    /**
     * 当前正在填充的意图
     */
    private IntentType currentIntent;
    
    /**
     * 已填充的槽位
     */
    private Map<String, Object> filledSlots;
    
    /**
     * 待填充的槽位列表
     */
    private java.util.List<String> pendingSlots;
    
    /**
     * 当前询问的槽位
     */
    private String currentSlot;
    
    /**
     * 尝试次数
     */
    private int attemptCount;
    
    /**
     * 最大尝试次数
     */
    private int maxAttempts;
    
    public SlotFillingState() {
        this.filledSlots = new HashMap<>();
        this.pendingSlots = new java.util.ArrayList<>();
        this.attemptCount = 0;
        this.maxAttempts = 3;
    }
    
    /**
     * 填充槽位
     */
    public void fillSlot(String slotName, Object value) {
        filledSlots.put(slotName, value);
        pendingSlots.remove(slotName);
        attemptCount = 0;
    }
    
    /**
     * 设置待填充槽位
     */
    public void setRequiredSlots(java.util.List<String> slots) {
        this.pendingSlots = new java.util.ArrayList<>(slots);
        if (!slots.isEmpty()) {
            this.currentSlot = slots.get(0);
        }
    }
    
    /**
     * 获取下一个待填充槽位
     */
    public String getNextSlot() {
        return pendingSlots.isEmpty() ? null : pendingSlots.get(0);
    }
    
    /**
     * 是否完成填充
     */
    public boolean isComplete() {
        return pendingSlots.isEmpty();
    }
    
    /**
     * 增加尝试次数
     */
    public void incrementAttempt() {
        attemptCount++;
    }
    
    /**
     * 是否超过最大尝试次数
     */
    public boolean isMaxAttemptsReached() {
        return attemptCount >= maxAttempts;
    }
}
