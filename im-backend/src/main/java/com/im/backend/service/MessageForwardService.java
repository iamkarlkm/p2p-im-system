package com.im.backend.service;

import com.im.backend.dto.ForwardRequest;
import com.im.backend.dto.ForwardResponse;
import com.im.backend.entity.*;
import com.im.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageForwardService {
    private final MessageForwardRepository forwardRepository;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SequenceGeneratorService sequenceGenerator;

    @Transactional
    public ForwardResponse forwardMessages(ForwardRequest request, Long userId) {
        try {
            List<Message> originalMessages = messageRepository.findAllById(request.getMessageIds());
            if (originalMessages.isEmpty()) {
                return ForwardResponse.error("No valid messages found");
            }
            
            Conversation target = conversationRepository.findById(request.getTargetConversationId())
                    .orElse(null);
            if (target == null) {
                return ForwardResponse.error("Target conversation not found");
            }
            
            if (!conversationRepository.isParticipant(request.getTargetConversationId(), userId)) {
                return ForwardResponse.error("You are not a participant in target conversation");
            }
            
            LocalDateTime now = LocalDateTime.now();
            
            if (request.isMerged() && request.getMessageIds().size() > 1) {
                return createMergedForward(originalMessages, request, userId, now);
            } else {
                return createSingleForwards(originalMessages, request, userId, now);
            }
        } catch (Exception e) {
            log.error("Forward messages failed", e);
            return ForwardResponse.error("Forward failed: " + e.getMessage());
        }
    }

    private ForwardResponse createSingleForwards(List<Message> originals, ForwardRequest request, Long userId, LocalDateTime now) {
        List<Long> newMessageIds = new ArrayList<>();
        
        for (Message original : originals) {
            List<MessageForward> existing = forwardRepository.findExistingForward(
                    original.getId(), request.getTargetConversationId());
            if (!existing.isEmpty()) {
                continue;
            }
            
            Message newMsg = Message.builder()
                    .msgId(sequenceGenerator.generateMessageId())
                    .conversationId(request.getTargetConversationId())
                    .senderId(userId)
                    .content(buildForwardContent(original))
                    .contentType(MessageContentType.FORWARD)
                    .sentAt(now)
                    .status(MessageStatus.SENT)
                    .originalMessageId(original.getId())
                    .build();
            newMsg = messageRepository.save(newMsg);
            newMessageIds.add(newMsg.getId());
            
            MessageForward forward = MessageForward.builder()
                    .originalMessageId(original.getId())
                    .targetConversationId(request.getTargetConversationId())
                    .forwardedBy(userId)
                    .forwardedAt(now)
                    .comment(request.getComment())
                    .forwardType(MessageForward.ForwardType.SINGLE)
                    .build();
            forwardRepository.save(forward);
        }
        
        return ForwardResponse.success(newMessageIds, now);
    }

    private ForwardResponse createMergedForward(List<Message> originals, ForwardRequest request, Long userId, LocalDateTime now) {
        String mergedId = "MF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        List<MergedForward.MergedForwardMessageDTO> dtoList = originals.stream()
                .map(m -> MergedForward.MergedForwardMessageDTO.builder()
                        .originalMessageId(m.getId())
                        .senderName(getUserDisplayName(m.getSenderId()))
                        .content(m.getContent())
                        .sentAt(m.getSentAt())
                        .contentType(m.getContentType().name())
                        .build())
                .collect(Collectors.toList());
        
        StringBuilder sb = new StringBuilder();
        sb.append("[Merged Forward]\n");
        if (request.getMergedTitle() != null && !request.getMergedTitle().isEmpty()) {
            sb.append(request.getMergedTitle()).append("\n");
        }
        sb.append("─".repeat(20)).append("\n");
        for (MergedForward.MergedForwardMessageDTO dto : dtoList) {
            sb.append(dto.getSenderName()).append(": ").append(dto.getContent()).append("\n");
        }
        
        Message mergedMsg = Message.builder()
                .msgId(sequenceGenerator.generateMessageId())
                .conversationId(request.getTargetConversationId())
                .senderId(userId)
                .content(sb.toString())
                .contentType(MessageContentType.MERGED_FORWARD)
                .sentAt(now)
                .status(MessageStatus.SENT)
                .mergedForwardId(mergedId)
                .build();
        mergedMsg = messageRepository.save(mergedMsg);
        
        for (Message original : originals) {
            MessageForward forward = MessageForward.builder()
                    .originalMessageId(original.getId())
                    .targetConversationId(request.getTargetConversationId())
                    .forwardedBy(userId)
                    .forwardedAt(now)
                    .forwardType(MessageForward.ForwardType.MERGED)
                    .build();
            forwardRepository.save(forward);
        }
        
        return ForwardResponse.mergedSuccess(mergedId, now);
    }

    private String buildForwardContent(Message original) {
        String senderName = getUserDisplayName(original.getSenderId());
        StringBuilder sb = new StringBuilder();
        sb.append("[Forwarded]\n");
        sb.append("From: ").append(senderName).append("\n");
        sb.append("Time: ").append(original.getSentAt()).append("\n");
        sb.append("─".repeat(15)).append("\n");
        sb.append(original.getContent());
        return sb.toString();
    }

    private String getUserDisplayName(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getNickname() != null ? u.getNickname() : u.getUsername())
                .orElse("Unknown User");
    }

    public List<MessageForward> getForwardHistory(Long messageId) {
        return forwardRepository.findByOriginalMessageId(messageId);
    }
}
