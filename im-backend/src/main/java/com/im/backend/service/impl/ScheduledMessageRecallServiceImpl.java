package com.im.backend.service.impl;

import com.im.backend.dto.ScheduledMessageRecallDTO;
import com.im.backend.model.ScheduledMessageRecall;
import com.im.backend.repository.ScheduledMessageRecallRepository;
import com.im.backend.service.ScheduledMessageRecallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息定时撤回服务实现
 */
@Service
public class ScheduledMessageRecallServiceImpl implements ScheduledMessageRecallService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledMessageRecallServiceImpl.class);
    
    @Autowired
    private ScheduledMessageRecallRepository scheduledRecallRepository;
    
    @Override
    @Transactional
    public ScheduledMessageRecallDTO createScheduledRecall(Long userId, Long messageId,
            Long conversationId, ScheduledMessageRecall.ConversationType conversationType,
            String messageContent, Integer scheduledSeconds, String recallReason,
            Boolean notifyReceivers, String customNotifyMessage, Boolean isCancelable) {
        
        // 验证参数
        if (scheduledSeconds == null || scheduledSeconds < 10 || scheduledSeconds > 3600) {
            throw new IllegalArgumentException("定时撤回时间必须在10秒到1小时之间");
        }
        
        // 检查是否已存在待执行的定时撤回
        if (scheduledRecallRepository.existsByMessageIdAndStatus(messageId, 
                ScheduledMessageRecall.RecallStatus.PENDING)) {
            throw new IllegalStateException("该消息已设置定时撤回");
        }
        
        ScheduledMessageRecall recall = new ScheduledMessageRecall();
        recall.setUserId(userId);
        recall.setMessageId(messageId);
        recall.setConversationId(conversationId);
        recall.setConversationType(conversationType);
        recall.setMessageContent(messageContent);
        recall.setScheduledSeconds(scheduledSeconds);
        recall.setScheduledRecallTime(LocalDateTime.now().plusSeconds(scheduledSeconds));
        recall.setRecallReason(recallReason);
        recall.setNotifyReceivers(notifyReceivers != null ? notifyReceivers : true);
        recall.setCustomNotifyMessage(customNotifyMessage);
        recall.setIsCancelable(isCancelable != null ? isCancelable : true);
        
        // 设置取消截止时间（执行前30秒可取消）
        recall.setCancelDeadline(recall.getScheduledRecallTime().minusSeconds(30));
        
        ScheduledMessageRecall saved = scheduledRecallRepository.save(recall);
        logger.info("创建定时撤回任务: userId={}, messageId={}, scheduledTime={}", 
            userId, messageId, saved.getScheduledRecallTime());
        
        return new ScheduledMessageRecallDTO(saved);
    }
    
    @Override
    @Transactional
    public ScheduledMessageRecallDTO cancelScheduledRecall(Long id, Long userId) {
        ScheduledMessageRecall recall = scheduledRecallRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("定时撤回任务不存在"));
        
        // 验证权限
        if (!recall.getUserId().equals(userId)) {
            throw new SecurityException("无权操作此定时撤回任务");
        }
        
        // 检查是否可以取消
        if (!recall.canCancel()) {
            throw new IllegalStateException("该定时撤回任务无法取消");
        }
        
        scheduledRecallRepository.markAsCancelled(id, LocalDateTime.now());
        recall.markCancelled();
        
        logger.info("取消定时撤回任务: id={}, userId={}", id, userId);
        return new ScheduledMessageRecallDTO(recall);
    }
    
    @Override
    @Transactional
    public boolean executeRecall(Long id) {
        try {
            ScheduledMessageRecall recall = scheduledRecallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("定时撤回任务不存在"));
            
            if (recall.getStatus() != ScheduledMessageRecall.RecallStatus.PENDING) {
                logger.warn("定时撤回任务状态不正确: id={}, status={}", id, recall.getStatus());
                return false;
            }
            
            // TODO: 调用消息撤回服务实际撤回消息
            // messageService.recallMessage(recall.getMessageId(), recall.getUserId());
            
            scheduledRecallRepository.markAsExecuted(id, LocalDateTime.now());
            logger.info("执行定时撤回成功: id={}, messageId={}", id, recall.getMessageId());
            return true;
        } catch (Exception e) {
            logger.error("执行定时撤回失败: id={}", id, e);
            scheduledRecallRepository.markAsFailed(id, LocalDateTime.now());
            return false;
        }
    }
    
    @Override
    public Optional<ScheduledMessageRecallDTO> getScheduledRecallById(Long id) {
        return scheduledRecallRepository.findById(id)
            .map(ScheduledMessageRecallDTO::new);
    }
    
    @Override
    public Optional<ScheduledMessageRecallDTO> getScheduledRecallByMessageId(Long messageId) {
        return scheduledRecallRepository.findByMessageId(messageId)
            .map(ScheduledMessageRecallDTO::new);
    }
    
    @Override
    public List<ScheduledMessageRecallDTO> getUserScheduledRecalls(Long userId) {
        return scheduledRecallRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(ScheduledMessageRecallDTO::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<ScheduledMessageRecallDTO> getUserScheduledRecalls(Long userId, Pageable pageable) {
        return scheduledRecallRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(ScheduledMessageRecallDTO::new);
    }
    
    @Override
    public List<ScheduledMessageRecallDTO> getUserPendingRecalls(Long userId) {
        return scheduledRecallRepository.findPendingByUserId(userId)
            .stream()
            .map(ScheduledMessageRecallDTO::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledMessageRecallDTO> getConversationRecalls(Long conversationId) {
        return scheduledRecallRepository.findByConversationIdOrderByCreatedAtDesc(conversationId)
            .stream()
            .map(ScheduledMessageRecallDTO::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledMessageRecallDTO> getAllPendingRecalls() {
        return scheduledRecallRepository.findByStatusOrderByScheduledRecallTimeAsc(
                ScheduledMessageRecall.RecallStatus.PENDING)
            .stream()
            .map(ScheduledMessageRecallDTO::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledMessageRecallDTO> getDueRecalls() {
        return scheduledRecallRepository.findPendingAndDue(LocalDateTime.now())
            .stream()
            .map(ScheduledMessageRecallDTO::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public long countUserPendingRecalls(Long userId) {
        return scheduledRecallRepository.countByUserIdAndStatus(userId, 
            ScheduledMessageRecall.RecallStatus.PENDING);
    }
    
    @Override
    public long countUserRecalls(Long userId) {
        return scheduledRecallRepository.countByUserId(userId);
    }
    
    @Override
    public boolean isMessageScheduledForRecall(Long messageId) {
        return scheduledRecallRepository.existsByMessageIdAndStatus(messageId,
            ScheduledMessageRecall.RecallStatus.PENDING);
    }
    
    @Override
    @Transactional
    public void deleteScheduledRecall(Long id, Long userId) {
        ScheduledMessageRecall recall = scheduledRecallRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("定时撤回任务不存在"));
        
        if (!recall.getUserId().equals(userId)) {
            throw new SecurityException("无权删除此定时撤回任务");
        }
        
        // 只能删除已完成的记录
        if (recall.getStatus() == ScheduledMessageRecall.RecallStatus.PENDING) {
            throw new IllegalStateException("不能删除待执行的定时撤回任务，请先取消");
        }
        
        scheduledRecallRepository.deleteById(id);
        logger.info("删除定时撤回任务: id={}, userId={}", id, userId);
    }
    
    @Override
    @Transactional
    public int cleanupOldRecords(int daysToKeep) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(daysToKeep);
        int deleted = scheduledRecallRepository.deleteOldCompletedRecords(beforeDate);
        logger.info("清理过期定时撤回记录: 删除{}条", deleted);
        return deleted;
    }
    
    @Override
    public List<Integer> getRecommendedTimeOptions() {
        // 推荐的定时撤回时间选项（秒）
        return Arrays.asList(30, 60, 120, 300, 600, 1800, 3600);
    }
    
    @Override
    @Transactional
    public int batchExecuteDueRecalls() {
        List<ScheduledMessageRecallDTO> dueRecalls = getDueRecalls();
        int successCount = 0;
        
        for (ScheduledMessageRecallDTO recall : dueRecalls) {
            if (executeRecall(recall.getId())) {
                successCount++;
            }
        }
        
        logger.info("批量执行定时撤回: 总共{}, 成功{}", dueRecalls.size(), successCount);
        return successCount;
    }
    
    @Override
    @Transactional
    public ScheduledMessageRecallDTO updateScheduledTime(Long id, Long userId, Integer newSeconds) {
        ScheduledMessageRecall recall = scheduledRecallRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("定时撤回任务不存在"));
        
        if (!recall.getUserId().equals(userId)) {
            throw new SecurityException("无权修改此定时撤回任务");
        }
        
        if (recall.getStatus() != ScheduledMessageRecall.RecallStatus.PENDING) {
            throw new IllegalStateException("只能修改待执行的任务");
        }
        
        if (newSeconds == null || newSeconds < 10 || newSeconds > 3600) {
            throw new IllegalArgumentException("定时撤回时间必须在10秒到1小时之间");
        }
        
        recall.setScheduledSeconds(newSeconds);
        recall.setScheduledRecallTime(LocalDateTime.now().plusSeconds(newSeconds));
        recall.setCancelDeadline(recall.getScheduledRecallTime().minusSeconds(30));
        
        ScheduledMessageRecall saved = scheduledRecallRepository.save(recall);
        logger.info("更新定时撤回时间: id={}, newSeconds={}", id, newSeconds);
        
        return new ScheduledMessageRecallDTO(saved);
    }
}
