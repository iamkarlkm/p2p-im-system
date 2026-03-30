package com.im.service.impl;

import com.im.dto.*;
import com.im.entity.ConversationType;
import com.im.entity.MessageForward;
import com.im.repository.MessageForwardRepository;
import com.im.service.MessageForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息转发服务实现
 * 功能#22: 消息转发
 */
@Service
public class MessageForwardServiceImpl implements MessageForwardService {

    @Autowired
    private MessageForwardRepository forwardRepository;

    @Override
    @Transactional
    public ForwardMessageResponse forwardMessage(Long forwarderId, ForwardMessageRequest request) {
        ForwardMessageResponse response = new ForwardMessageResponse();
        response.setOriginalMessageId(request.getOriginalMessageId());
        response.setForwardTime(LocalDateTime.now());

        try {
            if (!canForward(request.getOriginalMessageId(), forwarderId)) {
                response.setSuccess(false);
                response.setErrorMessage("无权转发该消息");
                return response;
            }

            MessageForward forward = new MessageForward();
            forward.setOriginalMessageId(request.getOriginalMessageId());
            forward.setForwarderId(forwarderId);
            forward.setForwardComment(request.getForwardComment());
            forward.setIsMultiForward(request.getIsMultiForward());

            if (request.getTargetConversationIds() != null && !request.getTargetConversationIds().isEmpty()) {
                forward.setTargetConversationId(request.getTargetConversationIds().get(0));
                if (request.getTargetConversationTypes() != null && !request.getTargetConversationTypes().isEmpty()) {
                    forward.setTargetConversationType(ConversationType.valueOf(
                        request.getTargetConversationTypes().get(0).toUpperCase()));
                }
            }

            MessageForward saved = forwardRepository.save(forward);

            Long newMessageId = createNewMessageFromForward(request.getOriginalMessageId(), 
                forward.getTargetConversationId(), forward.getTargetConversationType(), forwarderId);
            saved.setNewMessageId(newMessageId);
            forwardRepository.save(saved);

            response.setForwardId(saved.getId());
            response.setNewMessageId(newMessageId);
            response.setTargetConversationId(saved.getTargetConversationId());
            response.setTargetConversationType(
                saved.getTargetConversationType() != null ? saved.getTargetConversationType().name() : null);
            response.setSuccess(true);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    @Override
    @Transactional
    public List<ForwardMessageResponse> batchForwardMessage(Long forwarderId, ForwardMessageRequest request) {
        List<ForwardMessageResponse> responses = new ArrayList<>();

        if (request.getTargetConversationIds() == null) {
            return responses;
        }

        for (int i = 0; i < request.getTargetConversationIds().size(); i++) {
            ForwardMessageRequest singleRequest = new ForwardMessageRequest();
            singleRequest.setOriginalMessageId(request.getOriginalMessageId());
            singleRequest.setTargetConversationIds(
                List.of(request.getTargetConversationIds().get(i)));
            if (request.getTargetConversationTypes() != null && i < request.getTargetConversationTypes().size()) {
                singleRequest.setTargetConversationTypes(
                    List.of(request.getTargetConversationTypes().get(i)));
            }
            singleRequest.setForwardComment(request.getForwardComment());
            singleRequest.setIsMultiForward(true);

            responses.add(forwardMessage(forwarderId, singleRequest));
        }

        return responses;
    }

    @Override
    public List<ForwardRecordDTO> getForwardRecords(Long forwarderId, Integer limit) {
        List<MessageForward> records = forwardRepository.findByForwarderId(forwarderId);
        return records.stream()
            .limit(limit != null ? limit : 50)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Long getForwardCount(Long messageId) {
        return (long) forwardRepository.findByOriginalMessageId(messageId).size();
    }

    @Override
    public Boolean canForward(Long messageId, Long userId) {
        return messageId != null && userId != null;
    }

    @Override
    public List<MessageDTO> getMergedForwardContent(Long forwardId) {
        return new ArrayList<>();
    }

    private ForwardRecordDTO convertToDTO(MessageForward forward) {
        ForwardRecordDTO dto = new ForwardRecordDTO();
        dto.setId(forward.getId());
        dto.setOriginalMessageId(forward.getOriginalMessageId());
        dto.setForwarderId(forward.getForwarderId());
        dto.setTargetConversationId(forward.getTargetConversationId());
        dto.setTargetConversationType(
            forward.getTargetConversationType() != null ? forward.getTargetConversationType().name() : null);
        dto.setForwardTime(forward.getForwardTime());
        dto.setForwardComment(forward.getForwardComment());
        dto.setIsMultiForward(forward.getIsMultiForward());
        return dto;
    }

    private Long createNewMessageFromForward(Long originalMessageId, Long targetConversationId, 
            ConversationType conversationType, Long forwarderId) {
        return System.currentTimeMillis();
    }
}
