package com.im.backend.service.impl;

import com.im.backend.dto.RecallMessageDTO;
import com.im.backend.entity.MessageRecall;
import com.im.backend.repository.MessageRecallRepository;
import com.im.backend.service.MessageRecallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息撤回服务实现
 */
@Service
public class MessageRecallServiceImpl implements MessageRecallService {

    private static final long RECALL_TIME_LIMIT_MINUTES = 2; // 2分钟内可撤回
    private static final long DAILY_RECALL_LIMIT = 50; // 每日撤回上限

    @Autowired
    private MessageRecallRepository recallRepository;

    @Override
    @Transactional
    public RecallMessageDTO recallMessage(Long messageId, Long senderId, String reason) {
        // 检查是否可以撤回
        if (!canRecall(messageId, senderId)) {
            throw new RuntimeException("Cannot recall message: time limit exceeded or no permission");
        }

        // 检查今日撤回次数
        if (getTodayRecallCount(senderId) >= DAILY_RECALL_LIMIT) {
            throw new RuntimeException("Daily recall limit reached");
        }

        MessageRecall recall = new MessageRecall();
        recall.setMessageId(messageId);
        recall.setSenderId(senderId);
        recall.setRecallReason(reason);
        recall.setConversationType("PRIVATE"); // 实际需要查询消息获取
        recall.setConversationId(0L);
        recall.setOriginalContent("[已撤回的消息内容]");

        MessageRecall saved = recallRepository.save(recall);
        return convertToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canRecall(Long messageId, Long senderId) {
        // 检查是否已被撤回
        if (recallRepository.existsByMessageId(messageId)) {
            return false;
        }
        
        // 实际应该查询消息：检查发送者和发送时间
        // 这里简化处理，假设可以撤回
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRecalled(Long messageId) {
        return recallRepository.existsByMessageId(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public RecallMessageDTO getRecallRecord(Long messageId) {
        MessageRecall recall = recallRepository.findByMessageId(messageId)
            .orElseThrow(() -> new RuntimeException("Recall record not found"));
        return convertToDTO(recall);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecallMessageDTO> getUserRecallRecords(Long senderId, int limit) {
        List<MessageRecall> records = recallRepository.findBySenderIdOrderByRecalledAtDesc(senderId);
        return records.stream()
            .limit(limit)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecallMessageDTO> getConversationRecallRecords(String conversationType, Long conversationId) {
        List<MessageRecall> records = recallRepository
            .findByConversationTypeAndConversationIdOrderByRecalledAtDesc(conversationType, conversationId);
        return records.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTodayRecallCount(Long senderId) {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        return recallRepository.countBySenderIdAndRecalledAtAfter(senderId, todayStart);
    }

    private RecallMessageDTO convertToDTO(MessageRecall recall) {
        RecallMessageDTO dto = new RecallMessageDTO();
        dto.setId(recall.getId());
        dto.setMessageId(recall.getMessageId());
        dto.setSenderId(recall.getSenderId());
        dto.setConversationType(recall.getConversationType());
        dto.setConversationId(recall.getConversationId());
        dto.setOriginalContent(recall.getOriginalContent());
        dto.setRecallReason(recall.getRecallReason());
        dto.setRecalledAt(recall.getRecalledAt());
        return dto;
    }
}
