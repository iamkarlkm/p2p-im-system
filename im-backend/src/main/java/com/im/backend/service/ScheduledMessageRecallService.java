package com.im.backend.service;

import com.im.backend.dto.ScheduledMessageRecallDTO;
import com.im.backend.model.ScheduledMessageRecall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 消息定时撤回服务接口
 */
public interface ScheduledMessageRecallService {
    
    /**
     * 创建定时撤回任务
     */
    ScheduledMessageRecallDTO createScheduledRecall(Long userId, Long messageId, 
        Long conversationId, ScheduledMessageRecall.ConversationType conversationType,
        String messageContent, Integer scheduledSeconds, String recallReason,
        Boolean notifyReceivers, String customNotifyMessage, Boolean isCancelable);
    
    /**
     * 取消定时撤回
     */
    ScheduledMessageRecallDTO cancelScheduledRecall(Long id, Long userId);
    
    /**
     * 执行定时撤回
     */
    boolean executeRecall(Long id);
    
    /**
     * 获取定时撤回详情
     */
    Optional<ScheduledMessageRecallDTO> getScheduledRecallById(Long id);
    
    /**
     * 根据消息ID获取定时撤回
     */
    Optional<ScheduledMessageRecallDTO> getScheduledRecallByMessageId(Long messageId);
    
    /**
     * 获取用户的所有定时撤回
     */
    List<ScheduledMessageRecallDTO> getUserScheduledRecalls(Long userId);
    
    /**
     * 分页获取用户的定时撤回
     */
    Page<ScheduledMessageRecallDTO> getUserScheduledRecalls(Long userId, Pageable pageable);
    
    /**
     * 获取用户待执行的定时撤回
     */
    List<ScheduledMessageRecallDTO> getUserPendingRecalls(Long userId);
    
    /**
     * 获取会话中的所有定时撤回
     */
    List<ScheduledMessageRecallDTO> getConversationRecalls(Long conversationId);
    
    /**
     * 获取所有待执行的任务（用于定时任务扫描）
     */
    List<ScheduledMessageRecallDTO> getAllPendingRecalls();
    
    /**
     * 获取已到达执行时间的任务
     */
    List<ScheduledMessageRecallDTO> getDueRecalls();
    
    /**
     * 统计用户待执行的数量
     */
    long countUserPendingRecalls(Long userId);
    
    /**
     * 统计用户总记录数
     */
    long countUserRecalls(Long userId);
    
    /**
     * 检查消息是否已设置定时撤回
     */
    boolean isMessageScheduledForRecall(Long messageId);
    
    /**
     * 删除定时撤回任务
     */
    void deleteScheduledRecall(Long id, Long userId);
    
    /**
     * 清理过期记录
     */
    int cleanupOldRecords(int daysToKeep);
    
    /**
     * 获取推荐的定时撤回时间选项
     */
    List<Integer> getRecommendedTimeOptions();
    
    /**
     * 批量执行定时撤回（用于定时任务）
     */
    int batchExecuteDueRecalls();
    
    /**
     * 更新定时撤回时间
     */
    ScheduledMessageRecallDTO updateScheduledTime(Long id, Long userId, Integer newSeconds);
}
