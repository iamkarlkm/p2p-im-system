package com.im.backend.service;

import com.im.backend.entity.MergedPushMessage;
import com.im.backend.entity.PushMessageBuffer;
import com.im.backend.repository.MergedPushMessageRepository;
import com.im.backend.repository.PushMessageBufferRepository;
import com.im.backend.dto.MergedPushRequest;
import com.im.backend.dto.MergedPushResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergedPushService {
    private final MergedPushMessageRepository mergedPushRepository;
    private final PushMessageBufferRepository bufferRepository;

    private static final int DEFAULT_MERGE_WINDOW_SECONDS = 300;
    private static final int DEFAULT_MAX_MESSAGES = 10;
    private static final int BUFFER_TTL_SECONDS = 3600;

    @Transactional
    public MergedPushResponse bufferPushMessage(MergedPushRequest request) {
        int windowSeconds = request.getMergeWindowSeconds() != null
            ? request.getMergeWindowSeconds() : DEFAULT_MERGE_WINDOW_SECONDS;
        int maxMessages = request.getMaxMessages() != null
            ? request.getMaxMessages() : DEFAULT_MAX_MESSAGES;

        String bufferKey = buildBufferKey(request.getUserId(), request.getConversationId());
        PushMessageBuffer buffer = bufferRepository.findByBufferKey(bufferKey).orElse(null);

        if (buffer == null) {
            buffer = PushMessageBuffer.builder()
                .bufferKey(bufferKey)
                .userId(request.getUserId())
                .deviceToken(request.getDeviceToken())
                .conversationId(request.getConversationId())
                .conversationType(request.getConversationType())
                .messageCount(1)
                .senderNames(request.getSenderName())
                .lastMessagePreview(truncate(request.getMessageContent(), 200))
                .mergedContent(buildMergedContent(request.getSenderName(), request.getMessageContent(), 1))
                .isMerged(false)
                .build();
            buffer.setCreatedAt(Instant.now());
            buffer.setExpiresAt(Instant.now().plusSeconds(windowSeconds));
            buffer = bufferRepository.save(buffer);
        } else {
            buffer.setMessageCount(buffer.getMessageCount() + 1);
            if (buffer.getSenderNames() != null && !buffer.getSenderNames().contains(request.getSenderName())) {
                buffer.setSenderNames(buffer.getSenderNames() + ", " + request.getSenderName());
            }
            buffer.setLastMessagePreview(truncate(request.getMessageContent(), 200));
            buffer.setMergedContent(buildMergedContent(
                buffer.getSenderNames(), request.getMessageContent(), buffer.getMessageCount()));
            buffer.setExpiresAt(Instant.now().plusSeconds(windowSeconds));
            buffer = bufferRepository.save(buffer);
        }

        if (buffer.getMessageCount() >= maxMessages) {
            return flushBuffer(buffer);
        }

        return toMergedPushResponse(buffer);
    }

    @Transactional
    public MergedPushResponse flushBuffer(PushMessageBuffer buffer) {
        if (buffer.getIsMerged()) {
            return toMergedPushResponse(buffer);
        }

        buffer.setIsMerged(true);
        bufferRepository.save(buffer);

        Instant scheduledAt = Instant.now().plusSeconds(DEFAULT_MERGE_WINDOW_SECONDS);

        MergedPushMessage merged = MergedPushMessage.builder()
            .userId(buffer.getUserId())
            .deviceToken(buffer.getDeviceToken())
            .conversationId(buffer.getConversationId())
            .conversationType(buffer.getConversationType())
            .messageCount(buffer.getMessageCount())
            .title(buildTitle(buffer.getConversationType(), buffer.getMessageCount(), buffer.getSenderNames()))
            .previewText(buffer.getLastMessagePreview())
            .mergedContent(buffer.getMergedContent())
            .senderNames(buffer.getSenderNames())
            .scheduledAt(scheduledAt)
            .status("PENDING")
            .build();
        merged = mergedPushRepository.save(merged);

        log.info("Flushed {} messages for user {} in conversation {}",
            buffer.getMessageCount(), buffer.getUserId(), buffer.getConversationId());

        return toMergedPushResponse(merged);
    }

    @Transactional
    public void markAsSent(Long mergedId) {
        mergedPushRepository.findById(mergedId).ifPresent(merged -> {
            merged.setStatus("SENT");
            merged.setSentAt(Instant.now());
            mergedPushRepository.save(merged);
        });
    }

    @Transactional
    public void markAsFailed(Long mergedId) {
        mergedPushRepository.findById(mergedId).ifPresent(merged -> {
            merged.setStatus("FAILED");
            mergedPushRepository.save(merged);
        });
    }

    @Transactional(readOnly = true)
    public List<MergedPushResponse> getPendingPushes(String userId) {
        return mergedPushRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "PENDING")
            .stream().map(this::toMergedPushResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MergedPushResponse.PushStatsResponse getPushStats() {
        Long sent = mergedPushRepository.countSent();
        Long failed = mergedPushRepository.countFailed();
        Long sumMessages = mergedPushRepository.sumMessageCount() != null
            ? mergedPushRepository.sumMessageCount() : 0L;
        Double avg = mergedPushRepository.avgMessageCount() != null
            ? mergedPushRepository.avgMessageCount() : 1.0;

        Long saved = sumMessages - sent;
        Double ratio = avg > 0 ? (avg - 1) / avg : 0.0;

        return MergedPushResponse.PushStatsResponse.builder()
            .totalMergedMessages(sent)
            .totalPushSent(sent)
            .totalPushFailed(failed)
            .averageMergeRatio(ratio)
            .savedPushCount(saved > 0 ? saved : 0L)
            .build();
    }

    @Transactional(readOnly = true)
    public MergedPushResponse.BufferStatusResponse getBufferStatus() {
        Long active = bufferRepository.countActiveBuffers();
        Long buffered = bufferRepository.sumBufferedMessageCount() != null
            ? bufferRepository.sumBufferedMessageCount() : 0L;

        return MergedPushResponse.BufferStatusResponse.builder()
            .activeBuffers(active.intValue())
            .pendingMerges(active.intValue())
            .totalBufferedMessages(buffered)
            .build();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processExpiredBuffers() {
        List<PushMessageBuffer> expired = bufferRepository.findByIsMergedFalseAndExpiresAtBefore(Instant.now());
        for (PushMessageBuffer buffer : expired) {
            flushBuffer(buffer);
        }
        if (!expired.isEmpty()) {
            log.info("Processed {} expired push buffers", expired.size());
        }

        bufferRepository.deleteExpiredOrMerged(Instant.now().minusSeconds(BUFFER_TTL_SECONDS));
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void processDueMergedMessages() {
        List<MergedPushMessage> due = mergedPushRepository.findDueMergedMessages(Instant.now());
        for (MergedPushMessage merged : due) {
            try {
                sendMergedPush(merged);
                merged.setStatus("SENT");
                merged.setSentAt(Instant.now());
            } catch (Exception e) {
                merged.setStatus("FAILED");
                log.error("Failed to send merged push {}: {}", merged.getId(), e.getMessage());
            }
            mergedPushRepository.save(merged);
        }
        if (!due.isEmpty()) {
            log.info("Processed {} due merged push messages", due.size());
        }
    }

    private void sendMergedPush(MergedPushMessage merged) {
        log.info("Sending merged push: {} messages to user {} in conversation {}",
            merged.getMessageCount(), merged.getUserId(), merged.getConversationId());
    }

    private String buildBufferKey(String userId, String conversationId) {
        return userId + ":" + conversationId;
    }

    private String buildMergedContent(String senderName, String lastMessage, int count) {
        if (count == 1) {
            return "[" + senderName + "]: " + lastMessage;
        }
        return "[" + senderName + " 等" + count + "人]: " + lastMessage;
    }

    private String buildTitle(String conversationType, int count, String senderNames) {
        if ("GROUP".equals(conversationType)) {
            return senderNames + "等" + count + "条新消息";
        }
        return "您有" + count + "条新消息";
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    private MergedPushResponse toMergedPushResponse(PushMessageBuffer buffer) {
        return MergedPushResponse.builder()
            .messageCount(buffer.getMessageCount())
            .mergedContent(buffer.getMergedContent())
            .senderNames(buffer.getSenderNames())
            .conversationId(buffer.getConversationId())
            .conversationType(buffer.getConversationType())
            .createdAt(buffer.getCreatedAt())
            .status("BUFFERED")
            .build();
    }

    private MergedPushResponse toMergedPushResponse(MergedPushMessage merged) {
        return MergedPushResponse.builder()
            .id(merged.getId())
            .userId(merged.getUserId())
            .conversationId(merged.getConversationId())
            .conversationType(merged.getConversationType())
            .messageCount(merged.getMessageCount())
            .title(merged.getTitle())
            .mergedContent(merged.getMergedContent())
            .senderNames(merged.getSenderNames())
            .createdAt(merged.getCreatedAt())
            .scheduledAt(merged.getScheduledAt())
            .sentAt(merged.getSentAt())
            .status(merged.getStatus())
            .build();
    }
}
