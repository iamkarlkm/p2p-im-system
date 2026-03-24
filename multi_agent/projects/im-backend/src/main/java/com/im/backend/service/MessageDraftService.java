package com.im.backend.service;

import com.im.backend.entity.MessageDraft;
import com.im.backend.repository.MessageDraftRepository;
import com.im.backend.dto.DraftRequest;
import com.im.backend.dto.DraftResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDraftService {

    private final MessageDraftRepository draftRepository;

    @Transactional
    public DraftResponse saveDraft(DraftRequest request) {
        Optional<MessageDraft> existing = draftRepository.findByUserIdAndConversationIdAndIsDeletedFalse(
                request.getUserId(), request.getConversationId());

        MessageDraft draft;
        if (existing.isPresent()) {
            draft = existing.get();
            draft.setContent(request.getContent());
            draft.setMentionIds(request.getMentionIds());
            draft.setReplyMessageId(request.getReplyMessageId());
            draft.setMessageType(request.getMessageType());
            draft.setUpdatedAt(System.currentTimeMillis());
        } else {
            draft = MessageDraft.builder()
                    .userId(request.getUserId())
                    .conversationId(request.getConversationId())
                    .content(request.getContent())
                    .mentionIds(request.getMentionIds())
                    .replyMessageId(request.getReplyMessageId())
                    .messageType(request.getMessageType())
                    .updatedAt(System.currentTimeMillis())
                    .isDeleted(false)
                    .build();
        }

        MessageDraft saved = draftRepository.save(draft);
        log.info("Draft saved for user {} in conversation {}", request.getUserId(), request.getConversationId());
        return toResponse(saved);
    }

    @Transactional
    public void deleteDraft(Long userId, String conversationId) {
        draftRepository.deleteByUserIdAndConversationId(userId, conversationId);
        log.info("Draft deleted for user {} in conversation {}", userId, conversationId);
    }

    public Optional<DraftResponse> getDraft(Long userId, String conversationId) {
        return draftRepository.findByUserIdAndConversationIdAndIsDeletedFalse(userId, conversationId)
                .map(this::toResponse);
    }

    public List<DraftResponse> getAllDrafts(Long userId) {
        return draftRepository.findByUserIdAndIsDeletedFalseOrderByUpdatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void syncDraftToDevice(Long userId, String conversationId, Long deviceId) {
        Optional<MessageDraft> draft = draftRepository.findByUserIdAndConversationIdAndIsDeletedFalse(userId, conversationId);
        if (draft.isPresent()) {
            log.info("Syncing draft to device {} for user {} in conversation {}", deviceId, userId, conversationId);
        }
    }

    private DraftResponse toResponse(MessageDraft draft) {
        return DraftResponse.builder()
                .id(draft.getId())
                .userId(draft.getUserId())
                .conversationId(draft.getConversationId())
                .content(draft.getContent())
                .mentionIds(draft.getMentionIds())
                .replyMessageId(draft.getReplyMessageId())
                .messageType(draft.getMessageType())
                .updatedAt(draft.getUpdatedAt())
                .build();
    }
}
