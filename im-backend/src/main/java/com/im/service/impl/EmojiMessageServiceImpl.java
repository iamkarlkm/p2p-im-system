package com.im.service.impl;

import com.im.dto.EmojiMessageRequest;
import com.im.dto.EmojiMessageResponse;
import com.im.entity.ConversationType;
import com.im.entity.EmojiMessage;
import com.im.entity.EmojiType;
import com.im.repository.EmojiMessageRepository;
import com.im.service.EmojiMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表情消息服务实现
 * 功能#23: 表情消息
 */
@Service
public class EmojiMessageServiceImpl implements EmojiMessageService {

    @Autowired
    private EmojiMessageRepository emojiRepository;

    @Override
    @Transactional
    public EmojiMessageResponse sendEmojiMessage(Long senderId, EmojiMessageRequest request) {
        EmojiMessage emoji = new EmojiMessage();
        emoji.setMessageId(System.currentTimeMillis());
        emoji.setEmojiCode(request.getEmojiCode());
        emoji.setEmojiType(EmojiType.valueOf(request.getEmojiType().toUpperCase()));
        emoji.setEmojiUrl(request.getEmojiUrl());
        emoji.setEmojiName(request.getEmojiName());
        emoji.setEmojiCategory(request.getEmojiCategory());
        emoji.setIsCustom(request.getIsCustom() != null ? request.getIsCustom() : false);
        emoji.setIsAnimated(request.getIsAnimated() != null ? request.getIsAnimated() : false);
        emoji.setSenderId(senderId);
        emoji.setReceiverId(request.getReceiverId());
        emoji.setGroupId(request.getGroupId());
        emoji.setConversationType(ConversationType.valueOf(request.getConversationType().toUpperCase()));
        emoji.setSendTime(LocalDateTime.now());
        emoji.setIsRead(false);

        EmojiMessage saved = emojiRepository.save(emoji);
        return convertToResponse(saved);
    }

    @Override
    public List<EmojiMessageResponse> getEmojiMessageHistory(Long conversationId, String conversationType, Integer limit) {
        ConversationType type = ConversationType.valueOf(conversationType.toUpperCase());
        List<EmojiMessage> messages;
        
        if (type == ConversationType.PRIVATE) {
            messages = emojiRepository.findByReceiverIdAndConversationType(conversationId, type);
        } else {
            messages = emojiRepository.findByGroupIdAndConversationType(conversationId, type);
        }
        
        return messages.stream()
            .limit(limit != null ? limit : 50)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean markAsRead(Long messageId, Long userId) {
        Optional<EmojiMessage> messageOpt = emojiRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            EmojiMessage message = messageOpt.get();
            message.setIsRead(true);
            emojiRepository.save(message);
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getTopEmojis(Long userId, Integer topN) {
        List<Object[]> results = emojiRepository.findTopEmojisBySenderId(userId);
        return results.stream()
            .limit(topN != null ? topN : 10)
            .map(result -> {
                Map<String, Object> map = new HashMap<>();
                map.put("emojiCode", result[0]);
                map.put("count", result[1]);
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getEmojiStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalCount = emojiRepository.countBySenderIdAndSendTimeAfter(userId, LocalDateTime.now().minusDays(30));
        stats.put("totalCount30Days", totalCount);
        
        List<EmojiMessage> allEmojis = emojiRepository.findBySenderId(userId);
        stats.put("totalCountAllTime", allEmojis.size());
        
        Map<String, Long> typeCount = allEmojis.stream()
            .collect(Collectors.groupingBy(e -> e.getEmojiType().name(), Collectors.counting()));
        stats.put("typeDistribution", typeCount);
        
        return stats;
    }

    @Override
    public List<String> getEmojiCategories() {
        return Arrays.asList("smileys", "people", "animals", "food", "activities", "travel", "objects", "symbols", "flags");
    }

    @Override
    public List<EmojiMessageResponse> getEmojisByCategory(String category, Integer limit) {
        List<EmojiMessage> emojis = emojiRepository.findByEmojiCategory(category);
        return emojis.stream()
            .limit(limit != null ? limit : 50)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private EmojiMessageResponse convertToResponse(EmojiMessage emoji) {
        EmojiMessageResponse response = new EmojiMessageResponse();
        response.setId(emoji.getId());
        response.setMessageId(emoji.getMessageId());
        response.setEmojiCode(emoji.getEmojiCode());
        response.setEmojiType(emoji.getEmojiType().name());
        response.setEmojiUrl(emoji.getEmojiUrl());
        response.setEmojiName(emoji.getEmojiName());
        response.setEmojiCategory(emoji.getEmojiCategory());
        response.setIsCustom(emoji.getIsCustom());
        response.setIsAnimated(emoji.getIsAnimated());
        response.setSenderId(emoji.getSenderId());
        response.setReceiverId(emoji.getReceiverId());
        response.setGroupId(emoji.getGroupId());
        response.setConversationType(emoji.getConversationType().name());
        response.setSendTime(emoji.getSendTime());
        response.setIsRead(emoji.getIsRead());
        return response;
    }
}
