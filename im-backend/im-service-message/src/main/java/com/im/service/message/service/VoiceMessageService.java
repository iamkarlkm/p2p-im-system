package com.im.service.message.service;

import com.im.dto.VoiceMessageRequest;
import com.im.dto.VoiceMessageResponse;
import com.im.dto.VoiceTranscriptionResponse;
import com.im.entity.Message;
import com.im.entity.VoiceMessage;
import com.im.repository.MessageRepository;
import com.im.repository.VoiceMessageRepository;
import com.im.storage.VoiceStorageService;
import com.im.websocket.WebSocketMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 语音消息服务
 * 处理语音消息的发送、播放、转文字等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceMessageService {

    private final MessageRepository messageRepository;
    private final VoiceMessageRepository voiceMessageRepository;
    private final VoiceStorageService storageService;
    private final WebSocketMessageHandler webSocketHandler;

    // 语音转文字服务（可以是本地ASR或云端API）
    private final SpeechToTextService speechToTextService;

    /**
     * 发送语音消息
     */
    @Transactional
    public VoiceMessageResponse sendVoiceMessage(Long senderId, VoiceMessageRequest request) {
        // 验证语音时长（限制60秒）
        if (request.getDuration() > 60) {
            throw new RuntimeException("语音消息不能超过60秒");
        }

        // 保存消息主表
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setContent("[语音] " + request.getDuration() + "''");
        message.setMessageType("VOICE");
        message.setSendTime(LocalDateTime.now());
        message.setStatus(0);
        Message savedMessage = messageRepository.save(message);

        // 保存语音消息详情
        VoiceMessage voiceMessage = new VoiceMessage();
        voiceMessage.setMessageId(savedMessage.getId());
        voiceMessage.setVoiceUrl(request.getVoiceUrl());
        voiceMessage.setDuration(request.getDuration());
        voiceMessage.setFileSize(request.getFileSize());
        voiceMessage.setFormat(request.getFormat());
        voiceMessage.setTranscriptionStatus(0); // 0-未转写
        voiceMessage.setUploadTime(LocalDateTime.now());
        VoiceMessage savedVoice = voiceMessageRepository.save(voiceMessage);

        // WebSocket推送
        webSocketHandler.sendPrivateMessage(request.getReceiverId(), 
                convertToDTO(savedMessage, savedVoice));

        log.info("语音消息发送成功: messageId={}, duration={}s", savedMessage.getId(), request.getDuration());
        return convertToResponse(savedMessage, savedVoice);
    }

    /**
     * 上传语音文件
     */
    public String uploadVoice(Long userId, MultipartFile file, Integer duration) {
        try {
            // 验证语音格式
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            if (!isValidVoiceFormat(extension)) {
                throw new RuntimeException("不支持的语音格式，仅支持: mp3, amr, wav, m4a");
            }

            // 验证文件大小（最大2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                throw new RuntimeException("语音文件不能超过2MB");
            }

            // 验证时长
            if (duration > 60) {
                throw new RuntimeException("语音消息不能超过60秒");
            }

            // 转换为标准格式（MP3）
            byte[] mp3Data = convertToMp3(file.getBytes(), extension);

            // 生成存储路径
            String fileName = UUID.randomUUID().toString() + ".mp3";
            String path = String.format("voices/%d/%s/%s", 
                    userId, 
                    LocalDateTime.now().toString().substring(0, 7), 
                    fileName);

            // 上传到存储服务
            String voiceUrl = storageService.uploadFile(path, mp3Data, "audio/mpeg");
            
            log.info("语音上传成功: userId={}, duration={}s", userId, duration);
            return voiceUrl;

        } catch (Exception e) {
            log.error("语音上传失败", e);
            throw new RuntimeException("语音上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取语音数据
     */
    public byte[] getVoiceData(Long userId, Long messageId) {
        VoiceMessage voiceMessage = voiceMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("语音消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权播放此语音");
        }

        // 标记为已播放
        if (!userId.equals(message.getSenderId())) {
            voiceMessage.setPlayed(true);
            voiceMessage.setPlayTime(LocalDateTime.now());
            voiceMessageRepository.save(voiceMessage);
        }

        return storageService.downloadFile(voiceMessage.getVoiceUrl());
    }

    /**
     * 语音转文字
     */
    @Transactional
    public VoiceTranscriptionResponse transcribeVoice(Long userId, Long messageId) {
        VoiceMessage voiceMessage = voiceMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("语音消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权转写此语音");
        }

        // 检查是否已转写
        if (voiceMessage.getTranscriptionStatus() == 2 && voiceMessage.getTranscription() != null) {
            VoiceTranscriptionResponse response = new VoiceTranscriptionResponse();
            response.setMessageId(messageId);
            response.setTranscription(voiceMessage.getTranscription());
            response.setStatus("COMPLETED");
            return response;
        }

        try {
            // 更新状态为转写中
            voiceMessage.setTranscriptionStatus(1);
            voiceMessageRepository.save(voiceMessage);

            // 下载语音数据
            byte[] voiceData = storageService.downloadFile(voiceMessage.getVoiceUrl());

            // 调用ASR服务转写
            String transcription = speechToTextService.transcribe(voiceData, "zh-CN");

            // 保存转写结果
            voiceMessage.setTranscription(transcription);
            voiceMessage.setTranscriptionStatus(2);
            voiceMessage.setTranscriptionTime(LocalDateTime.now());
            voiceMessageRepository.save(voiceMessage);

            log.info("语音转文字完成: messageId={}, text={}", messageId, 
                    transcription.substring(0, Math.min(50, transcription.length())));

            VoiceTranscriptionResponse response = new VoiceTranscriptionResponse();
            response.setMessageId(messageId);
            response.setTranscription(transcription);
            response.setStatus("COMPLETED");
            return response;

        } catch (Exception e) {
            log.error("语音转文字失败", e);
            
            // 更新状态为失败
            voiceMessage.setTranscriptionStatus(3);
            voiceMessageRepository.save(voiceMessage);

            VoiceTranscriptionResponse response = new VoiceTranscriptionResponse();
            response.setMessageId(messageId);
            response.setStatus("FAILED");
            response.setErrorMessage("转写失败: " + e.getMessage());
            return response;
        }
    }

    /**
     * 获取转写结果
     */
    public VoiceTranscriptionResponse getTranscription(Long userId, Long messageId) {
        VoiceMessage voiceMessage = voiceMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("语音消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权查看此语音转写");
        }

        VoiceTranscriptionResponse response = new VoiceTranscriptionResponse();
        response.setMessageId(messageId);
        
        switch (voiceMessage.getTranscriptionStatus()) {
            case 0:
                response.setStatus("PENDING");
                break;
            case 1:
                response.setStatus("PROCESSING");
                break;
            case 2:
                response.setStatus("COMPLETED");
                response.setTranscription(voiceMessage.getTranscription());
                break;
            case 3:
                response.setStatus("FAILED");
                response.setErrorMessage("转写失败");
                break;
            default:
                response.setStatus("UNKNOWN");
        }
        
        return response;
    }

    /**
     * 删除语音消息
     */
    @Transactional
    public void deleteVoiceMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("只能删除自己发送的语音");
        }

        VoiceMessage voiceMessage = voiceMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("语音消息不存在"));

        // 删除存储的文件
        storageService.deleteFile(voiceMessage.getVoiceUrl());

        // 删除数据库记录
        voiceMessageRepository.delete(voiceMessage);
        messageRepository.delete(message);

        log.info("语音消息已删除: messageId={}", messageId);
    }

    /**
     * 获取聊天中的语音列表
     */
    public List<VoiceMessageResponse> getVoiceList(Long userId, Long targetUserId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendTime"));
        
        List<Message> messages = messageRepository.findPrivateChatHistory(userId, targetUserId, pageRequest);
        
        return messages.stream()
                .filter(msg -> "VOICE".equals(msg.getMessageType()))
                .map(msg -> {
                    VoiceMessage voice = voiceMessageRepository.findByMessageId(msg.getId())
                            .orElse(null);
                    return voice != null ? convertToResponse(msg, voice) : null;
                })
                .filter(resp -> resp != null)
                .collect(Collectors.toList());
    }

    /**
     * 标记语音为已播放
     */
    @Transactional
    public void markVoiceAsPlayed(Long userId, Long messageId) {
        VoiceMessage voiceMessage = voiceMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("语音消息不存在"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));

        // 只有接收者可以标记为已播放
        if (message.getReceiverId().equals(userId)) {
            voiceMessage.setPlayed(true);
            voiceMessage.setPlayTime(LocalDateTime.now());
            voiceMessageRepository.save(voiceMessage);
        }
    }

    // ============ 私有方法 ============

    private byte[] convertToMp3(byte[] voiceData, String sourceFormat) throws Exception {
        // 如果已经是MP3，直接返回
        if ("mp3".equalsIgnoreCase(sourceFormat)) {
            return voiceData;
        }

        // 否则进行格式转换（简化实现，实际可能需要FFmpeg）
        // 这里假设使用某个音频处理库进行转换
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(voiceData);
            // 实际转换逻辑...
            // 返回MP3格式数据
            return voiceData; // 临时返回原数据
        } catch (Exception e) {
            log.error("语音格式转换失败", e);
            throw new RuntimeException("语音格式转换失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidVoiceFormat(String extension) {
        return extension.matches("^(mp3|amr|wav|m4a|aac)$");
    }

    private Object convertToDTO(Message message, VoiceMessage voiceMessage) {
        return convertToResponse(message, voiceMessage);
    }

    private VoiceMessageResponse convertToResponse(Message message, VoiceMessage voiceMessage) {
        VoiceMessageResponse response = new VoiceMessageResponse();
        response.setMessageId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setVoiceUrl(voiceMessage.getVoiceUrl());
        response.setDuration(voiceMessage.getDuration());
        response.setFileSize(voiceMessage.getFileSize());
        response.setFormat(voiceMessage.getFormat());
        response.setPlayed(voiceMessage.isPlayed());
        response.setTranscriptionStatus(voiceMessage.getTranscriptionStatus());
        response.setTranscription(voiceMessage.getTranscription());
        response.setSendTime(message.getSendTime());
        return response;
    }
}
