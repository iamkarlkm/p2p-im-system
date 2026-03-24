package com.im.backend.service;

import com.im.backend.dto.PinConversationRequest;
import com.im.backend.dto.PinConversationResponse;
import com.im.backend.entity.ConversationPinned;
import com.im.backend.repository.ConversationPinnedRepository;
import com.im.backend.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationPinService {
    private final ConversationPinnedRepository pinnedRepository;
    private final ConversationRepository conversationRepository;

    @Transactional
    public PinConversationResponse pinConversation(PinConversationRequest request, Long userId) {
        if (pinnedRepository.existsByUserIdAndConversationId(userId, request.getConversationId())) {
            return PinConversationResponse.error("Conversation already pinned");
        }
        if (!conversationRepository.isParticipant(request.getConversationId(), userId)) {
            return PinConversationResponse.error("You are not a participant in this conversation");
        }
        Integer maxOrder = pinnedRepository.findMaxSortOrder(userId);
        int newOrder = request.getSortOrder() != null ? request.getSortOrder() : maxOrder + 1;
        ConversationPinned pinned = ConversationPinned.builder()
                .userId(userId)
                .conversationId(request.getConversationId())
                .sortOrder(newOrder)
                .pinnedAt(LocalDateTime.now())
                .pinNote(request.getPinNote())
                .build();
        pinnedRepository.save(pinned);
        return PinConversationResponse.success(getPinnedConversations(userId));
    }

    @Transactional
    public PinConversationResponse unpinConversation(Long conversationId, Long userId) {
        pinnedRepository.deleteByUserIdAndConversationId(userId, conversationId);
        return PinConversationResponse.success(getPinnedConversations(userId));
    }

    @Transactional
    public PinConversationResponse reorderPinned(List<Long> conversationIds, Long userId) {
        for (int i = 0; i < conversationIds.size(); i++) {
            pinnedRepository.findByUserIdAndConversationId(userId, conversationIds.get(i))
                    .ifPresent(p -> {
                        p.setSortOrder(i);
                        pinnedRepository.save(p);
                    });
        }
        return PinConversationResponse.success(getPinnedConversations(userId));
    }

    public List<PinConversationResponse.PinnedConversationDTO> getPinnedConversations(Long userId) {
        return pinnedRepository.findByUserIdOrderBySortOrderAsc(userId).stream()
                .map(p -> {
                    String convName = conversationRepository.findById(p.getConversationId())
                            .map(c -> c.getName() != null ? c.getName() : c.getType().name())
                            .orElse("Unknown");
                    return PinConversationResponse.PinnedConversationDTO.builder()
                            .conversationId(p.getConversationId())
                            .conversationName(convName)
                            .sortOrder(p.getSortOrder())
                            .pinnedAt(p.getPinnedAt())
                            .pinNote(p.getPinNote())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public boolean isPinned(Long userId, Long conversationId) {
        return pinnedRepository.existsByUserIdAndConversationId(userId, conversationId);
    }
}
