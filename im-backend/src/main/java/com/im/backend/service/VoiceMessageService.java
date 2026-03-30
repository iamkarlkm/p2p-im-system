package com.im.backend.service;

import com.im.backend.dto.VoiceMessageDTO;
import com.im.backend.dto.VoiceSendRequest;
import com.im.backend.dto.VoiceUploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 语音消息服务接口
 */
public interface VoiceMessageService {
    
    /**
     * 上传语音文件
     */
    String uploadVoiceFile(Long uploaderId, MultipartFile file, VoiceUploadRequest request);
    
    /**
     * 下载语音文件
     */
    Resource downloadVoiceFile(String voiceId);
    
    /**
     * 发送语音消息
     */
    VoiceMessageDTO sendVoiceMessage(Long senderId, VoiceSendRequest request);
    
    /**
     * 获取语音消息详情
     */
    VoiceMessageDTO getVoiceMessage(String messageId);
    
    /**
     * 获取语音时长
     */
    Integer getVoiceDuration(String voiceId);
    
    /**
     * 标记语音消息为已读
     */
    void markAsRead(String messageId);
    
    /**
     * 记录语音播放
     */
    void recordPlay(String messageId);
    
    /**
     * 语音转文字
     */
    String convertToText(String voiceId);
    
    /**
     * 获取用户的语音消息列表
     */
    Page<VoiceMessageDTO> getMyVoiceMessages(Long userId, Pageable pageable);
    
    /**
     * 获取未读语音消息数量
     */
    long getUnreadCount(Long userId);
    
    /**
     * 删除语音文件
     */
    void deleteVoiceFile(String voiceId, Long userId);
    
    /**
     * 获取会话的语音消息历史
     */
    List<VoiceMessageDTO> getConversationVoiceMessages(Long userId, Long otherUserId);
}
