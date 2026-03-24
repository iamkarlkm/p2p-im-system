package com.im.service;

import com.im.dto.MuteSettingDTO;
import com.im.dto.MuteSettingRequest;
import com.im.entity.MuteSettingEntity;
import com.im.entity.ConversationEntity;
import com.im.repository.MuteSettingRepository;
import com.im.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 免打扰设置服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MuteSettingService {
    
    private final MuteSettingRepository muteSettingRepository;
    private final ConversationRepository conversationRepository;
    
    /**
     * 设置会话静音
     */
    @Transactional
    public MuteSettingDTO muteConversation(Long userId, Long conversationId) {
        MuteSettingEntity entity = muteSettingRepository
                .findByUserIdAndConversationId(userId, conversationId)
                .orElse(MuteSettingEntity.builder()
                        .userId(userId)
                        .conversationId(conversationId)
                        .isMuted(false)
                        .dndEnabled(false)
                        .build());
        
        entity.setIsMuted(true);
        entity = muteSettingRepository.save(entity);
        
        log.info("用户 {} 静音会话 {}", userId, conversationId);
        return toDTO(entity);
    }
    
    /**
     * 取消会话静音
     */
    @Transactional
    public MuteSettingDTO unmuteConversation(Long userId, Long conversationId) {
        MuteSettingEntity entity = muteSettingRepository
                .findByUserIdAndConversationId(userId, conversationId)
                .orElse(null);
        
        if (entity != null) {
            entity.setIsMuted(false);
            entity = muteSettingRepository.save(entity);
            log.info("用户 {} 取消静音会话 {}", userId, conversationId);
            return toDTO(entity);
        }
        
        return null;
    }
    
    /**
     * 设置全局免打扰时段
     */
    @Transactional
    public MuteSettingDTO setGlobalDnd(Long userId, MuteSettingRequest request) {
        MuteSettingEntity entity = muteSettingRepository
                .findByUserIdAndConversationIdIsNull(userId)
                .orElse(MuteSettingEntity.builder()
                        .userId(userId)
                        .isMuted(false)
                        .dndEnabled(false)
                        .build());
        
        if (request.getDndEnabled() != null) {
            entity.setDndEnabled(request.getDndEnabled());
        }
        if (request.getDndStartTime() != null) {
            entity.setDndStartTime(request.getDndStartTime());
        }
        if (request.getDndEndTime() != null) {
            entity.setDndEndTime(request.getDndEndTime());
        }
        if (request.getDndRepeatDays() != null) {
            entity.setDndRepeatDays(request.getDndRepeatDays());
        }
        
        entity = muteSettingRepository.save(entity);
        log.info("用户 {} 设置全局免打扰: {} - {}, enabled={}", 
                userId, entity.getDndStartTime(), entity.getDndEndTime(), entity.getDndEnabled());
        
        return toDTO(entity);
    }
    
    /**
     * 获取用户的全局免打扰设置
     */
    public MuteSettingDTO getGlobalDnd(Long userId) {
        return muteSettingRepository.findByUserIdAndConversationIdIsNull(userId)
                .map(this::toDTO)
                .orElse(null);
    }
    
    /**
     * 获取用户所有会话的静音设置
     */
    public List<MuteSettingDTO> getConversationMuteSettings(Long userId) {
        return muteSettingRepository.findByUserIdAndConversationIdIsNotNull(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户所有已静音的会话ID列表
     */
    public List<Long> getMutedConversationIds(Long userId) {
        return muteSettingRepository.findMutedConversations(userId)
                .stream()
                .map(MuteSettingEntity::getConversationId)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查会话是否被静音
     */
    public boolean isConversationMuted(Long userId, Long conversationId) {
        return muteSettingRepository.isConversationMuted(userId, conversationId);
    }
    
    /**
     * 检查用户是否可以接收消息 (考虑静音和免打扰时段)
     */
    public boolean canReceiveNotification(Long userId, Long conversationId) {
        // 检查会话是否被静音
        if (isConversationMuted(userId, conversationId)) {
            return false;
        }
        
        // 检查全局免打扰设置
        MuteSettingEntity globalSetting = muteSettingRepository
                .findByUserIdAndConversationIdIsNull(userId)
                .orElse(null);
        
        if (globalSetting != null && Boolean.TRUE.equals(globalSetting.getDndEnabled())) {
            // 检查是否在免打扰时段内
            if (globalSetting.isInDndPeriod()) {
                // 检查是否在重复周期内
                String repeatDays = globalSetting.getDndRepeatDays();
                if (repeatDays == null || repeatDays.isEmpty() || repeatDays.equals("daily")) {
                    return false;
                }
                
                DayOfWeek today = LocalDateTime.now().getDayOfWeek();
                String todayName = today.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
                if (repeatDays.contains(todayName)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 删除会话的静音设置
     */
    @Transactional
    public void deleteConversationSetting(Long userId, Long conversationId) {
        muteSettingRepository.deleteConversationSetting(userId, conversationId);
        log.info("用户 {} 删除会话 {} 的静音设置", userId, conversationId);
    }
    
    /**
     * 删除全局免打扰设置
     */
    @Transactional
    public void deleteGlobalDnd(Long userId) {
        muteSettingRepository.deleteGlobalSetting(userId);
        log.info("用户 {} 删除全局免打扰设置", userId);
    }
    
    /**
     * 批量检查会话是否被静音
     */
    public List<Long> batchCheckMuted(Long userId, List<Long> conversationIds) {
        if (conversationIds == null || conversationIds.isEmpty()) {
            return List.of();
        }
        return muteSettingRepository.findMutedConversationIds(userId, conversationIds);
    }
    
    /**
     * 实体转DTO
     */
    private MuteSettingDTO toDTO(MuteSettingEntity entity) {
        if (entity == null) return null;
        
        String conversationType = null;
        if (entity.getConversationId() != null) {
            conversationType = conversationRepository.findById(entity.getConversationId())
                    .map(ConversationEntity::getType)
                    .map(Enum::name)
                    .orElse(null);
        }
        
        return MuteSettingDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .conversationId(entity.getConversationId())
                .conversationType(conversationType)
                .isMuted(entity.getIsMuted())
                .dndStartTime(entity.getDndStartTime())
                .dndEndTime(entity.getDndEndTime())
                .dndEnabled(entity.getDndEnabled())
                .dndRepeatDays(entity.getDndRepeatDays())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .inDndPeriod(entity.isInDndPeriod())
                .build();
    }
}
