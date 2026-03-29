package com.im.backend.service;

import com.im.backend.dto.ReminderRequest;
import com.im.backend.dto.ReminderResponse;
import com.im.backend.entity.MessageReminder;
import com.im.backend.entity.Message;
import com.im.backend.entity.Conversation;
import com.im.backend.repository.MessageReminderRepository;
import com.im.backend.repository.MessageRepository;
import com.im.backend.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReminderService {

    private final MessageReminderRepository reminderRepository;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    @Transactional
    public ReminderResponse createReminder(Long userId, ReminderRequest request) {
        if (reminderRepository.existsByMessageIdAndUserId(request.getMessageId(), userId)) {
            throw new RuntimeException("Reminder already exists for this message");
        }

        MessageReminder reminder = MessageReminder.builder()
                .userId(userId)
                .messageId(request.getMessageId())
                .conversationId(request.getConversationId())
                .reminderTime(request.getReminderTime())
                .note(request.getNote())
                .repeatType(request.getRepeatType())
                .remindBeforeMinutes(request.getRemindBeforeMinutes())
                .isTriggered(false)
                .isDismissed(false)
                .build();

        reminder = reminderRepository.save(reminder);
        log.info("Created reminder {} for user {} at {}", reminder.getId(), userId, request.getReminderTime());

        return toResponse(reminder);
    }

    @Transactional
    public ReminderResponse updateReminder(Long userId, Long reminderId, ReminderRequest request) {
        MessageReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (!reminder.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        reminder.setReminderTime(request.getReminderTime());
        reminder.setNote(request.getNote());
        reminder.setRepeatType(request.getRepeatType());
        reminder.setRemindBeforeMinutes(request.getRemindBeforeMinutes());
        reminder.setIsTriggered(false);
        reminder.setIsDismissed(false);

        reminder = reminderRepository.save(reminder);
        return toResponse(reminder);
    }

    @Transactional
    public void deleteReminder(Long userId, Long reminderId) {
        MessageReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (!reminder.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        reminderRepository.delete(reminder);
    }

    @Transactional
    public ReminderResponse dismissReminder(Long userId, Long reminderId) {
        MessageReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (!reminder.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        reminder.setIsDismissed(true);
        reminder = reminderRepository.save(reminder);
        return toResponse(reminder);
    }

    public List<ReminderResponse> getUserReminders(Long userId) {
        return reminderRepository.findByUserIdOrderByReminderTimeDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReminderResponse> getPendingReminders(Long userId) {
        return reminderRepository.findByUserIdAndIsTriggeredFalseAndIsDismissedFalse(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ReminderResponse> triggerDueReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<MessageReminder> dueReminders = reminderRepository.findRemindersDueBefore(now);

        for (MessageReminder reminder : dueReminders) {
            reminder.setIsTriggered(true);
            reminderRepository.save(reminder);
            log.info("Triggered reminder {} for user {}", reminder.getId(), reminder.getUserId());
        }

        return dueReminders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public int getPendingCount(Long userId) {
        return reminderRepository.countByUserIdAndIsTriggeredFalse(userId);
    }

    private ReminderResponse toResponse(MessageReminder reminder) {
        String messagePreview = messageRepository.findById(reminder.getMessageId())
                .map(Message::getContent)
                .orElse("[Message not found]");

        String conversationName = conversationRepository.findById(reminder.getConversationId())
                .map(Conversation::getName)
                .orElse("[Conversation not found]");

        return ReminderResponse.builder()
                .id(reminder.getId())
                .messageId(reminder.getMessageId())
                .conversationId(reminder.getConversationId())
                .reminderTime(reminder.getReminderTime())
                .note(reminder.getNote())
                .isTriggered(reminder.getIsTriggered())
                .isDismissed(reminder.getIsDismissed())
                .createdAt(reminder.getCreatedAt())
                .repeatType(reminder.getRepeatType())
                .remindBeforeMinutes(reminder.getRemindBeforeMinutes())
                .messagePreview(messagePreview.length() > 100 ?
                        messagePreview.substring(0, 100) : messagePreview)
                .conversationName(conversationName)
                .build();
    }
}
