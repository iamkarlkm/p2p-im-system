package com.im.backend.service.impl;

import com.im.backend.dto.VoiceMessageDTO;
import com.im.backend.dto.VoiceSendRequest;
import com.im.backend.dto.VoiceUploadRequest;
import com.im.backend.model.entity.User;
import com.im.backend.model.entity.VoiceFile;
import com.im.backend.model.entity.VoiceMessage;
import com.im.backend.repository.UserRepository;
import com.im.backend.repository.VoiceFileRepository;
import com.im.backend.repository.VoiceMessageRepository;
import com.im.backend.service.VoiceMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 语音消息服务实现类
 */
@Service
public class VoiceMessageServiceImpl implements VoiceMessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(VoiceMessageServiceImpl.class);
    
    @Autowired
    private VoiceFileRepository voiceFileRepository;
    
    @Autowired
    private VoiceMessageRepository voiceMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.voice.upload-dir:uploads/voices}")
    private String uploadDir;
    
    @Override
    @Transactional
    public String uploadVoiceFile(Long uploaderId, MultipartFile file, VoiceUploadRequest request) {
        try {
            String fileId = UUID.randomUUID().toString().replace("-", "");
            String originalName = file.getOriginalFilename();
            String extension = originalName != null ? 
                originalName.substring(originalName.lastIndexOf(".") + 1) : "mp3";
            String storedName = fileId + "." + extension;
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(storedName);
            Files.copy(file.getInputStream(), filePath);
            
            VoiceFile voiceFile = new VoiceFile();
            voiceFile.setFileId(fileId);
            voiceFile.setOriginalName(originalName);
            voiceFile.setStoredName(storedName);
            voiceFile.setFilePath(filePath.toString());
            voiceFile.setFileSize(file.getSize());
            voiceFile.setDuration(request.getDuration());
            voiceFile.setFormat(request.getFormat());
            voiceFile.setBitrate(request.getBitrate());
            voiceFile.setSampleRate(request.getSampleRate());
            voiceFile.setUploaderId(uploaderId);
            voiceFile.setCreatedAt(LocalDateTime.now());
            voiceFile.setExpiredAt(LocalDateTime.now().plusDays(30));
            
            voiceFileRepository.save(voiceFile);
            logger.info("语音文件上传成功: fileId={}, uploaderId={}", fileId, uploaderId);
            
            return fileId;
        } catch (IOException e) {
            logger.error("语音文件上传失败", e);
            throw new RuntimeException("语音文件上传失败", e);
        }
    }
    
    @Override
    public Resource downloadVoiceFile(String voiceId) {
        try {
            VoiceFile voiceFile = voiceFileRepository.findByFileId(voiceId)
                .orElseThrow(() -> new RuntimeException("语音文件不存在"));
            
            Path filePath = Paths.get(voiceFile.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("语音文件无法读取");
            }
        } catch (IOException e) {
            logger.error("语音文件下载失败", e);
            throw new RuntimeException("语音文件下载失败", e);
        }
    }
    
    @Override
    @Transactional
    public VoiceMessageDTO sendVoiceMessage(Long senderId, VoiceSendRequest request) {
        VoiceFile voiceFile = voiceFileRepository.findByFileId(request.getVoiceFileId())
            .orElseThrow(() -> new RuntimeException("语音文件不存在"));
        
        String messageId = UUID.randomUUID().toString().replace("-", "");
        
        VoiceMessage message = new VoiceMessage();
        message.setMessageId(messageId);
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setGroupId(request.getGroupId());
        message.setVoiceFileId(request.getVoiceFileId());
        message.setDuration(request.getDuration());
        message.setTextContent(request.getTextContent());
        message.setIsConverted(request.getTextContent() != null && !request.getTextContent().isEmpty());
        message.setMessageType(request.getGroupId() != null ? 
            VoiceMessage.MessageType.GROUP : VoiceMessage.MessageType.PRIVATE);
        
        voiceMessageRepository.save(message);
        logger.info("语音消息发送成功: messageId={}, senderId={}, receiverId={}", 
            messageId, senderId, request.getReceiverId());
        
        return convertToDTO(message);
    }
    
    @Override
    public VoiceMessageDTO getVoiceMessage(String messageId) {
        VoiceMessage message = voiceMessageRepository.findByMessageId(messageId)
            .orElseThrow(() -> new RuntimeException("语音消息不存在"));
        return convertToDTO(message);
    }
    
    @Override
    public Integer getVoiceDuration(String voiceId) {
        VoiceFile voiceFile = voiceFileRepository.findByFileId(voiceId)
            .orElseThrow(() -> new RuntimeException("语音文件不存在"));
        return voiceFile.getDuration();
    }
    
    @Override
    @Transactional
    public void markAsRead(String messageId) {
        VoiceMessage message = voiceMessageRepository.findByMessageId(messageId)
            .orElseThrow(() -> new RuntimeException("语音消息不存在"));
        voiceMessageRepository.markAsRead(message.getId(), LocalDateTime.now());
    }
    
    @Override
    @Transactional
    public void recordPlay(String messageId) {
        VoiceMessage message = voiceMessageRepository.findByMessageId(messageId)
            .orElseThrow(() -> new RuntimeException("语音消息不存在"));
        voiceMessageRepository.recordPlay(message.getId(), LocalDateTime.now());
    }
    
    @Override
    public String convertToText(String voiceId) {
        logger.info("语音转文字请求: voiceId={}", voiceId);
        return "语音转文字功能需要集成ASR服务";
    }
    
    @Override
    public Page<VoiceMessageDTO> getMyVoiceMessages(Long userId, Pageable pageable) {
        Page<VoiceMessage> messages = voiceMessageRepository
            .findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId, pageable);
        return messages.map(this::convertToDTO);
    }
    
    @Override
    public long getUnreadCount(Long userId) {
        return voiceMessageRepository.countUnreadByReceiverId(userId);
    }
    
    @Override
    @Transactional
    public void deleteVoiceFile(String voiceId, Long userId) {
        VoiceFile voiceFile = voiceFileRepository.findByFileId(voiceId)
            .orElseThrow(() -> new RuntimeException("语音文件不存在"));
        
        if (!voiceFile.getUploaderId().equals(userId)) {
            throw new RuntimeException("无权删除该语音文件");
        }
        
        voiceFileRepository.softDeleteByFileId(voiceId);
        logger.info("语音文件删除成功: fileId={}", voiceId);
    }
    
    @Override
    public List<VoiceMessageDTO> getConversationVoiceMessages(Long userId, Long otherUserId) {
        List<VoiceMessage> messages = voiceMessageRepository
            .findBySenderIdAndReceiverIdOrderByCreatedAtDesc(userId, otherUserId);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    private VoiceMessageDTO convertToDTO(VoiceMessage message) {
        VoiceMessageDTO dto = new VoiceMessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setGroupId(message.getGroupId());
        dto.setVoiceFileId(message.getVoiceFileId());
        dto.setDuration(message.getDuration());
        dto.setTextContent(message.getTextContent());
        dto.setIsConverted(message.getIsConverted());
        dto.setIsRead(message.getIsRead());
        dto.setIsPlayed(message.getIsPlayed());
        dto.setPlayCount(message.getPlayCount());
        dto.setMessageType(message.getMessageType() != null ? message.getMessageType().name() : null);
        dto.setCreatedAt(message.getCreatedAt());
        
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        if (sender != null) {
            dto.setSenderName(sender.getUsername());
        }
        
        dto.setVoiceUrl("/api/voice/download/" + message.getVoiceFileId());
        
        return dto;
    }
}
