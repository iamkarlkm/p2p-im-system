package com.im.service;

import com.im.entity.DeadLetterMessage;
import com.im.entity.QueueMessage;
import java.util.List;

/**
 * 死信消息处理器接口
 * 功能 #1: 消息队列核心系统 - 死信队列
 */
public interface DeadLetterHandler {
    
    /**
     * 处理死信消息
     */
    void handleDeadLetter(DeadLetterMessage deadLetter);
    
    /**
     * 批量处理死信消息
     */
    void handleBatchDeadLetters(List<DeadLetterMessage> deadLetters);
    
    /**
     * 重新投递死信消息到原队列
     */
    boolean requeueDeadLetter(String deadLetterId);
    
    /**
     * 归档死信消息
     */
    boolean archiveDeadLetter(String deadLetterId);
    
    /**
     * 丢弃死信消息
     */
    boolean discardDeadLetter(String deadLetterId);
    
    /**
     * 获取死信消息详情
     */
    DeadLetterMessage getDeadLetter(String deadLetterId);
    
    /**
     * 获取队列的死信消息列表
     */
    List<DeadLetterMessage> getDeadLettersByQueue(String queueName, int limit);
    
    /**
     * 清理过期的死信消息
     */
    int cleanupExpiredDeadLetters(long maxAgeDays);
}
