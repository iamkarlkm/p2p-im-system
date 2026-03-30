package com.im.service.impl;

import com.im.dto.ImageMessageRequest;
import com.im.dto.ImageMessageResponse;
import com.im.entity.ConversationType;
import com.im.entity.ImageMessage;
import com.im.repository.ImageMessageRepository;
import com.im.service.ImageMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 图片消息服务实现
 * 功能#24: 图片消息
 */
@Service
public class ImageMessageServiceImpl implements ImageMessageService {

    @Autowired
    private ImageMessageRepository imageRepository;

    @Override
    @Transactional
    public ImageMessageResponse sendImageMessage(Long senderId, ImageMessageRequest request) {
        ImageMessage image = new ImageMessage();
        image.setMessageId(System.currentTimeMillis());
        image.setImageUrl(request.getImageUrl());
        image.setThumbnailUrl(request.getThumbnailUrl());
        image.setOriginalUrl(request.getOriginalUrl());
        image.setImageName(request.getImageName());
        image.setImageType(request.getImageType());
        image.setFileSize(request.getFileSize());
        image.setWidth(request.getWidth());
        image.setHeight(request.getHeight());
        image.setSenderId(senderId);
        image.setReceiverId(request.getReceiverId());
        image.setGroupId(request.getGroupId());
        image.setConversationType(ConversationType.valueOf(request.getConversationType().toUpperCase()));
        image.setIsCompressed(request.getIsCompressed() != null ? request.getIsCompressed() : true);
        image.setSendTime(LocalDateTime.now());
        image.setIsRead(false);
        image.setUploadStatus("COMPLETED");

        ImageMessage saved = imageRepository.save(image);
        return convertToResponse(saved);
    }

    @Override
    public ImageMessageResponse getImageMessage(Long messageId) {
        Optional<ImageMessage> messageOpt = imageRepository.findByMessageId(messageId);
        return messageOpt.map(this::convertToResponse).orElse(null);
    }

    @Override
    public List<ImageMessageResponse> getImageMessageHistory(Long conversationId, String conversationType, Integer limit) {
        ConversationType type = ConversationType.valueOf(conversationType.toUpperCase());
        List<ImageMessage> messages;
        
        if (type == ConversationType.PRIVATE) {
            messages = imageRepository.findByReceiverIdAndConversationType(conversationId, type);
        } else {
            messages = imageRepository.findByGroupIdAndConversationType(conversationId, type);
        }
        
        return messages.stream()
            .limit(limit != null ? limit : 50)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean markAsRead(Long messageId, Long userId) {
        Optional<ImageMessage> messageOpt = imageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            ImageMessage message = messageOpt.get();
            message.setIsRead(true);
            imageRepository.save(message);
            return true;
        }
        return false;
    }

    @Override
    public String getOriginalImageUrl(Long messageId) {
        Optional<ImageMessage> messageOpt = imageRepository.findByMessageId(messageId);
        return messageOpt.map(ImageMessage::getOriginalUrl).orElse(null);
    }

    @Override
    public List<ImageMessageResponse> getRecentImages(Long userId, Integer limit) {
        List<ImageMessage> images = imageRepository.findRecentImagesBySenderId(userId);
        return images.stream()
            .limit(limit != null ? limit : 20)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getImageStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalCount = imageRepository.countBySenderIdAndSendTimeAfter(userId, LocalDateTime.now().minusDays(30));
        stats.put("totalCount30Days", totalCount);
        
        List<ImageMessage> allImages = imageRepository.findBySenderId(userId);
        stats.put("totalCountAllTime", allImages.size());
        
        long totalSize = allImages.stream().mapToLong(ImageMessage::getFileSize).sum();
        stats.put("totalSizeBytes", totalSize);
        
        Map<String, Long> typeCount = allImages.stream()
            .collect(Collectors.groupingBy(ImageMessage::getImageType, Collectors.counting()));
        stats.put("typeDistribution", typeCount);
        
        return stats;
    }

    private ImageMessageResponse convertToResponse(ImageMessage image) {
        ImageMessageResponse response = new ImageMessageResponse();
        response.setId(image.getId());
        response.setMessageId(image.getMessageId());
        response.setImageUrl(image.getImageUrl());
        response.setThumbnailUrl(image.getThumbnailUrl());
        response.setOriginalUrl(image.getOriginalUrl());
        response.setImageName(image.getImageName());
        response.setImageType(image.getImageType());
        response.setFileSize(image.getFileSize());
        response.setWidth(image.getWidth());
        response.setHeight(image.getHeight());
        response.setSenderId(image.getSenderId());
        response.setReceiverId(image.getReceiverId());
        response.setGroupId(image.getGroupId());
        response.setConversationType(image.getConversationType().name());
        response.setSendTime(image.getSendTime());
        response.setIsRead(image.getIsRead());
        response.setIsCompressed(image.getIsCompressed());
        response.setUploadStatus(image.getUploadStatus());
        return response;
    }
}
