package com.im.backend.service;

import com.im.backend.dto.ScheduledMessageRequest;
import com.im.backend.dto.ScheduledMessageResponse;
import com.im.backend.entity.ScheduledMessage;
import com.im.backend.repository.ScheduledMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduledMessageService {

    private final ScheduledMessageRepository repository;

    public ScheduledMessageService(ScheduledMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ScheduledMessageResponse scheduleMessage(Long senderId, ScheduledMessageRequest request) {
        if (request.getScheduledTime() == null) {
            throw new RuntimeException("Scheduled time is required");
        }

        LocalDateTime scheduledTime = LocalDateTime.parse(request.getScheduledTime(),
                DateTimeFormatter.ISO_DATE_TIME);

        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Scheduled time must be in the future");
        }

        ScheduledMessage msg = new ScheduledMessage();
        msg.setScheduleId(UUID.randomUUID().toString());
        msg.setSenderId(senderId);
        msg.setTargetType(request.getTargetType() != null ? request.getTargetType() : "USER");
        msg.setTargetId(request.getTargetId());
        msg.setContent(request.getContent());
        msg.setType(request.getType() != null ? request.getType() : "TEXT");
        msg.setAttachmentUrl(request.getAttachmentUrl());
        msg.setAttachmentName(request.getAttachmentName());
        msg.setAttachmentSize(request.getAttachmentSize());
        msg.setScheduledTime(scheduledTime);
        msg.setStatus("PENDING");
        msg.setRecurring(request.getRecurring() != null ? request.getRecurring() : false);
        msg.setRecurrencePattern(request.getRecurrencePattern());
        msg.setRecurrenceInterval(request.getRecurrenceInterval());
        if (request.getRecurrenceEndTime() != null) {
            msg.setRecurrenceEndTime(LocalDateTime.parse(request.getRecurrenceEndTime(), DateTimeFormatter.ISO_DATE_TIME));
        }

        msg = repository.save(msg);
        return toResponse(msg);
    }

    @Transactional
    public void cancelScheduledMessage(Long userId, String scheduleId) {
        ScheduledMessage msg = repository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new RuntimeException("Scheduled message not found"));

        if (!msg.getSenderId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this scheduled message");
        }

        msg.setStatus("CANCELLED");
        repository.save(msg);
    }

    @Transactional
    public ScheduledMessageResponse updateScheduledMessage(Long userId, String scheduleId, ScheduledMessageRequest request) {
        ScheduledMessage msg = repository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new RuntimeException("Scheduled message not found"));

        if (!msg.getSenderId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this scheduled message");
        }

        if ("SENT".equals(msg.getStatus()) || "CANCELLED".equals(msg.getStatus())) {
            throw new RuntimeException("Cannot update a message that has already been sent or cancelled");
        }

        if (request.getContent() != null) msg.setContent(request.getContent());
        if (request.getType() != null) msg.setType(request.getType());
        if (request.getAttachmentUrl() != null) msg.setAttachmentUrl(request.getAttachmentUrl());
        if (request.getScheduledTime() != null) {
            LocalDateTime newTime = LocalDateTime.parse(request.getScheduledTime(), DateTimeFormatter.ISO_DATE_TIME);
            if (newTime.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Scheduled time must be in the future");
            }
            msg.setScheduledTime(newTime);
        }
        if (request.getRecurring() != null) msg.setRecurring(request.getRecurring());
        if (request.getRecurrencePattern() != null) msg.setRecurrencePattern(request.getRecurrencePattern());
        if (request.getRecurrenceInterval() != null) msg.setRecurrenceInterval(request.getRecurrenceInterval());

        msg = repository.save(msg);
        return toResponse(msg);
    }

    public List<ScheduledMessageResponse> getUserScheduledMessages(Long userId) {
        List<ScheduledMessage> messages = repository.findBySenderIdAndStatusOrderByScheduledTimeDesc(userId, "PENDING");
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ScheduledMessageResponse getScheduledMessage(String scheduleId) {
        ScheduledMessage msg = repository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new RuntimeException("Scheduled message not found"));
        return toResponse(msg);
    }

    public Map<String, Object> getUserScheduleStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", repository.countBySenderIdAndStatus(userId, "PENDING"));
        stats.put("sent", repository.countBySenderIdAndStatus(userId, "SENT"));
        stats.put("cancelled", repository.countBySenderIdAndStatus(userId, "CANCELLED"));
        stats.put("failed", repository.countBySenderIdAndStatus(userId, "FAILED"));
        return stats;
    }

    private ScheduledMessageResponse toResponse(ScheduledMessage msg) {
        ScheduledMessageResponse r = new ScheduledMessageResponse();
        r.setId(msg.getId());
        r.setScheduleId(msg.getScheduleId());
        r.setSenderId(msg.getSenderId());
        r.setTargetType(msg.getTargetType());
        r.setTargetId(msg.getTargetId());
        r.setContent(msg.getContent());
        r.setType(msg.getType());
        r.setAttachmentUrl(msg.getAttachmentUrl());
        r.setAttachmentName(msg.getAttachmentName());
        r.setAttachmentSize(msg.getAttachmentSize());
        r.setScheduledTime(msg.getScheduledTime());
        r.setCreatedAt(msg.getCreatedAt());
        r.setSentAt(msg.getSentAt());
        r.setStatus(msg.getStatus());
        r.setFailReason(msg.getFailReason());
        r.setRetryCount(msg.getRetryCount());
        r.setRecurring(msg.getRecurring());
        r.setRecurrencePattern(msg.getRecurrencePattern());
        r.setRecurrenceInterval(msg.getRecurrenceInterval());
        r.setRecurrenceEndTime(msg.getRecurrenceEndTime());

        if (msg.getScheduledTime() != null && "PENDING".equals(msg.getStatus())) {
            long seconds = Duration.between(LocalDateTime.now(), msg.getScheduledTime()).getSeconds();
            r.setRemainingSeconds(Math.max(0, seconds));
        }
        return r;
    }
}
