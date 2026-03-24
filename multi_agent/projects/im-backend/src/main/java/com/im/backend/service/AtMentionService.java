package com.im.backend.service;

import com.im.backend.dto.AtMentionRequest;
import com.im.backend.dto.AtMentionResponse;
import com.im.backend.entity.AtMention;
import com.im.backend.entity.AtMentionSettings;
import com.im.backend.repository.AtMentionRepository;
import com.im.backend.repository.AtMentionSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @提及服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AtMentionService {

    private final AtMentionRepository atMentionRepository;
    private final AtMentionSettingsRepository atMentionSettingsRepository;

    /**
     * 处理消息中的@提及
     */
    @Transactional
    public List<AtMention> processMentions(AtMentionRequest request, String messagePreview) {
        List<AtMention> mentions = new ArrayList<>();

        // 创建个人@提及记录
        if (request.getMentionedUserIds() != null) {
            for (Long userId : request.getMentionedUserIds()) {
                if (userId.equals(request.getSenderUserId())) continue; // 不能@自己

                AtMention mention = AtMention.builder()
                        .messageId(request.getMessageId())
                        .senderUserId(request.getSenderUserId())
                        .mentionedUserId(userId)
                        .roomId(request.getRoomId())
                        .conversationId(request.getConversationId())
                        .isAtAll(false)
                        .isRead(false)
                        .notified(false)
                        .mentionedAt(LocalDateTime.now())
                        .messagePreview(messagePreview)
                        .build();
                mentions.add(atMentionRepository.save(mention));
            }
        }

        // @所有人处理
        if (Boolean.TRUE.equals(request.getIsAtAll())) {
            AtMention atAllMention = AtMention.builder()
                    .messageId(request.getMessageId())
                    .senderUserId(request.getSenderUserId())
                    .mentionedUserId(0L) // 0表示@all
                    .roomId(request.getRoomId())
                    .conversationId(request.getConversationId())
                    .isAtAll(true)
                    .isRead(false)
                    .notified(false)
                    .mentionedAt(LocalDateTime.now())
                    .messagePreview(messagePreview)
                    .build();
            mentions.add(atMentionRepository.save(atAllMention));
        }

        // 触发通知
        for (AtMention mention : mentions) {
            sendMentionNotification(mention);
        }

        return mentions;
    }

    /**
     * 发送@提及强提醒通知
     */
    private void sendMentionNotification(AtMention mention) {
        if (mention.getMentionedUserId() == 0L) {
            // @all 通知逻辑
            log.info("发送@all通知: 消息ID={}, 群ID={}", mention.getMessageId(), mention.getRoomId());
        } else {
            // 个人@通知
            AtMentionSettings settings = atMentionSettingsRepository
                    .findByUserId(mention.getMentionedUserId()).orElse(null);

            if (settings != null && !settings.getEnabled()) {
                log.info("用户{}关闭了@提及提醒", mention.getMentionedUserId());
                return;
            }

            // 免打扰检查
            if (settings != null && settings.getDndEnabled()) {
                if (isInDndPeriod(settings.getDndStartTime(), settings.getDndEndTime())) {
                    log.info("用户{}在免打扰时段，跳过通知", mention.getMentionedUserId());
                    return;
                }
            }

            // TODO: 集成推送服务发送强提醒
            mention.setNotified(true);
            atMentionRepository.save(mention);
            log.info("发送@提及强提醒: 目标用户={}, 消息ID={}", 
                     mention.getMentionedUserId(), mention.getMessageId());
        }
    }

    /**
     * 检查当前时间是否在免打扰时段
     */
    private boolean isInDndPeriod(String start, String end) {
        if (start == null || end == null) return false;
        LocalDateTime now = LocalDateTime.now();
        int currentMinutes = now.getHour() * 60 + now.getMinute();
        int startMinutes = parseTimeToMinutes(start);
        int endMinutes = parseTimeToMinutes(end);
        if (startMinutes <= endMinutes) {
            return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
        } else {
            return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
        }
    }

    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * 获取用户@提及列表
     */
    public Page<AtMentionResponse> getUserMentions(Long userId, int page, int size) {
        Page<AtMention> pageData = atMentionRepository
                .findByMentionedUserIdOrderByMentionedAtDesc(userId, PageRequest.of(page, size));
        return pageData.map(this::toResponse);
    }

    /**
     * 获取未读@提及数量
     */
    public long getUnreadCount(Long userId) {
        return atMentionRepository.countByMentionedUserIdAndIsReadFalse(userId);
    }

    /**
     * 获取群聊未读@提及数量
     */
    public long getUnreadCountInRoom(Long userId, Long roomId) {
        return atMentionRepository.countByMentionedUserIdAndRoomIdAndIsReadFalse(userId, roomId);
    }

    /**
     * 标记已读
     */
    @Transactional
    public int markAsRead(Long userId, List<Long> mentionIds) {
        return atMentionRepository.markAsRead(userId, mentionIds);
    }

    /**
     * 标记群聊内所有@已读
     */
    @Transactional
    public int markAllAsReadInRoom(Long userId, Long roomId) {
        return atMentionRepository.markAllAsReadInRoom(userId, roomId);
    }

    /**
     * 获取用户@提及设置
     */
    public AtMentionSettings getSettings(Long userId) {
        return atMentionSettingsRepository.findByUserId(userId)
                .orElse(AtMentionSettings.builder().userId(userId).build());
    }

    /**
     * 更新@提及设置
     */
    @Transactional
    public AtMentionSettings updateSettings(Long userId, AtMentionSettings settings) {
        AtMentionSettings existing = atMentionSettingsRepository.findByUserId(userId)
                .orElse(AtMentionSettings.builder().userId(userId).build());
        existing.setEnabled(settings.getEnabled());
        existing.setOnlyAtAll(settings.getOnlyAtAll());
        existing.setAllowStrangerAt(settings.getAllowStrangerAt());
        existing.setSyncToOtherDevices(settings.getSyncToOtherDevices());
        existing.setDndEnabled(settings.getDndEnabled());
        existing.setDndStartTime(settings.getDndStartTime());
        existing.setDndEndTime(settings.getDndEndTime());
        return atMentionSettingsRepository.save(existing);
    }

    /**
     * 删除消息时清理@提及
     */
    @Transactional
    public void deleteByMessageId(Long messageId) {
        atMentionRepository.deleteByMessageId(messageId);
    }

    /**
     * 转换为响应DTO
     */
    private AtMentionResponse toResponse(AtMention mention) {
        return AtMentionResponse.builder()
                .id(mention.getId())
                .messageId(mention.getMessageId())
                .senderUserId(mention.getSenderUserId())
                .mentionedUserId(mention.getMentionedUserId())
                .roomId(mention.getRoomId())
                .isRead(mention.getIsRead())
                .isAtAll(mention.getIsAtAll())
                .notified(mention.getNotified())
                .mentionedAt(mention.getMentionedAt())
                .messagePreview(mention.getMessagePreview())
                .conversationId(mention.getConversationId())
                .build();
    }
}
