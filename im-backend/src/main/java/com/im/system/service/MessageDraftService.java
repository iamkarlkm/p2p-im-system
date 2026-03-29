package com.im.system.service;

import com.im.system.entity.MessageDraftEntity;
import com.im.system.repository.MessageDraftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息草稿跨设备同步服务
 * 处理草稿的创建、更新、同步和冲突解决
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageDraftService {
    
    private final MessageDraftRepository messageDraftRepository;
    
    // 用户草稿缓存：userId -> conversationId -> draft
    private final Map<Long, Map<String, MessageDraftEntity>> userDraftCache = new ConcurrentHashMap<>();
    
    /**
     * 保存或更新草稿
     */
    @Transactional
    public MessageDraftEntity saveOrUpdateDraft(Long userId, String deviceId, String conversationId, 
                                                String draftContent, String draftType, boolean autoSave) {
        
        // 先检查缓存
        MessageDraftEntity draft = getCachedDraft(userId, conversationId);
        
        if (draft == null) {
            // 从数据库查询
            draft = messageDraftRepository.findByUserIdAndDeviceIdAndConversationId(userId, deviceId, conversationId)
                    .orElseGet(() -> {
                        MessageDraftEntity newDraft = new MessageDraftEntity();
                        newDraft.setUserId(userId);
                        newDraft.setDeviceId(deviceId);
                        newDraft.setConversationId(conversationId);
                        newDraft.setCreatedAt(LocalDateTime.now());
                        return newDraft;
                    });
        }
        
        // 更新草稿内容
        draft.setDraftContent(draftContent);
        draft.setDraftType(draftType);
        draft.setAutoSave(autoSave);
        draft.setCleared(draftContent == null || draftContent.trim().isEmpty());
        draft.setLastUpdatedAt(LocalDateTime.now());
        draft.setSyncStatus(MessageDraftEntity.SyncStatus.PENDING.name());
        draft.setActive(true); // 保存时标记为活跃
        
        // 增加本地版本号
        if (draft.getLocalVersion() == null) {
            draft.setLocalVersion(0L);
        }
        draft.setLocalVersion(draft.getLocalVersion() + 1);
        
        // 保存到数据库
        MessageDraftEntity savedDraft = messageDraftRepository.save(draft);
        
        // 更新缓存
        updateCache(userId, conversationId, savedDraft);
        
        log.info("保存草稿成功: userId={}, deviceId={}, conversationId={}, version={}", 
                userId, deviceId, conversationId, savedDraft.getLocalVersion());
        
        return savedDraft;
    }
    
    /**
     * 获取指定会话的草稿
     */
    public Optional<MessageDraftEntity> getDraft(Long userId, String conversationId) {
        // 先检查缓存
        MessageDraftEntity draft = getCachedDraft(userId, conversationId);
        if (draft != null) {
            return Optional.of(draft);
        }
        
        // 从数据库查询
        Optional<MessageDraftEntity> dbDraft = messageDraftRepository.findByUserIdAndConversationId(userId, conversationId);
        
        // 更新缓存
        dbDraft.ifPresent(value -> updateCache(userId, conversationId, value));
        
        return dbDraft;
    }
    
    /**
     * 获取用户所有设备的草稿列表
     */
    public List<MessageDraftEntity> getUserDrafts(Long userId) {
        // 先检查缓存中是否有数据
        Map<String, MessageDraftEntity> userCache = userDraftCache.get(userId);
        if (userCache != null && !userCache.isEmpty()) {
            return new ArrayList<>(userCache.values());
        }
        
        // 从数据库查询
        List<MessageDraftEntity> drafts = messageDraftRepository.findByUserId(userId);
        
        // 更新缓存
        for (MessageDraftEntity draft : drafts) {
            updateCache(userId, draft.getConversationId(), draft);
        }
        
        return drafts;
    }
    
    /**
     * 获取用户指定设备上的草稿
     */
    public List<MessageDraftEntity> getDeviceDrafts(Long userId, String deviceId) {
        return messageDraftRepository.findByUserIdAndDeviceId(userId, deviceId);
    }
    
    /**
     * 删除指定草稿
     */
    @Transactional
    public boolean deleteDraft(Long userId, String conversationId) {
        try {
            // 从数据库删除
            int deleted = messageDraftRepository.deleteByUserAndConversation(userId, conversationId);
            
            // 从缓存删除
            removeFromCache(userId, conversationId);
            
            log.info("删除草稿: userId={}, conversationId={}, deleted={}", userId, conversationId, deleted);
            return deleted > 0;
        } catch (Exception e) {
            log.error("删除草稿失败", e);
            return false;
        }
    }
    
    /**
     * 清除用户所有草稿
     */
    @Transactional
    public int clearUserDrafts(Long userId) {
        int cleared = messageDraftRepository.deleteAllByUser(userId);
        
        // 清除缓存
        userDraftCache.remove(userId);
        
        log.info("清除用户所有草稿: userId={}, cleared={}", userId, cleared);
        return cleared;
    }
    
    /**
     * 同步草稿到服务端
     */
    @Transactional
    public MessageDraftEntity syncDraft(Long userId, String deviceId, String conversationId, 
                                       String draftContent, Long localVersion) {
        
        Optional<MessageDraftEntity> draftOpt = messageDraftRepository
                .findByUserIdAndDeviceIdAndConversationId(userId, deviceId, conversationId);
        
        if (draftOpt.isEmpty()) {
            // 创建新草稿
            MessageDraftEntity newDraft = new MessageDraftEntity();
            newDraft.setUserId(userId);
            newDraft.setDeviceId(deviceId);
            newDraft.setConversationId(conversationId);
            newDraft.setDraftContent(draftContent);
            newDraft.setLocalVersion(localVersion);
            newDraft.setServerVersion(0L);
            newDraft.setSyncStatus(MessageDraftEntity.SyncStatus.SYNCED.name());
            newDraft.setLastUpdatedAt(LocalDateTime.now());
            newDraft.setCreatedAt(LocalDateTime.now());
            
            MessageDraftEntity savedDraft = messageDraftRepository.save(newDraft);
            updateCache(userId, conversationId, savedDraft);
            
            log.info("创建新草稿并同步: userId={}, conversationId={}", userId, conversationId);
            return savedDraft;
        }
        
        MessageDraftEntity draft = draftOpt.get();
        
        // 检查版本冲突
        if (draft.getServerVersion() > localVersion) {
            // 服务端版本更新，存在冲突
            draft.setSyncStatus(MessageDraftEntity.SyncStatus.CONFLICT.name());
            draft.setConflictInfo(String.format("{\"clientVersion\": %d, \"serverVersion\": %d, \"timestamp\": \"%s\"}", 
                    localVersion, draft.getServerVersion(), LocalDateTime.now()));
            
            MessageDraftEntity conflictedDraft = messageDraftRepository.save(draft);
            updateCache(userId, conversationId, conflictedDraft);
            
            log.warn("草稿同步冲突: userId={}, conversationId={}, clientVersion={}, serverVersion={}", 
                    userId, conversationId, localVersion, draft.getServerVersion());
            return conflictedDraft;
        }
        
        // 更新草稿内容
        draft.setDraftContent(draftContent);
        draft.setLocalVersion(localVersion);
        draft.setServerVersion(localVersion); // 更新服务端版本
        draft.setSyncStatus(MessageDraftEntity.SyncStatus.SYNCED.name());
        draft.setLastUpdatedAt(LocalDateTime.now());
        
        MessageDraftEntity syncedDraft = messageDraftRepository.save(draft);
        updateCache(userId, conversationId, syncedDraft);
        
        log.info("草稿同步成功: userId={}, conversationId={}, version={}", 
                userId, conversationId, localVersion);
        
        return syncedDraft;
    }
    
    /**
     * 解决草稿冲突
     */
    @Transactional
    public MessageDraftEntity resolveConflict(Long draftId, String resolvedContent, Long newVersion) {
        Optional<MessageDraftEntity> draftOpt = messageDraftRepository.findById(draftId);
        
        if (draftOpt.isEmpty()) {
            throw new IllegalArgumentException("草稿不存在: " + draftId);
        }
        
        MessageDraftEntity draft = draftOpt.get();
        
        if (!MessageDraftEntity.SyncStatus.CONFLICT.name().equals(draft.getSyncStatus())) {
            throw new IllegalStateException("草稿不是冲突状态: " + draftId);
        }
        
        // 解决冲突
        draft.setDraftContent(resolvedContent);
        draft.setLocalVersion(newVersion);
        draft.setServerVersion(newVersion);
        draft.setSyncStatus(MessageDraftEntity.SyncStatus.SYNCED.name());
        draft.setConflictInfo(null);
        draft.setLastUpdatedAt(LocalDateTime.now());
        
        MessageDraftEntity resolvedDraft = messageDraftRepository.save(draft);
        updateCache(draft.getUserId(), draft.getConversationId(), resolvedDraft);
        
        log.info("草稿冲突已解决: draftId={}, userId={}", draftId, draft.getUserId());
        
        return resolvedDraft;
    }
    
    /**
     * 获取需要同步的草稿列表
     */
    public List<MessageDraftEntity> getPendingSyncDrafts(Long userId) {
        return messageDraftRepository.findByUserIdAndSyncStatus(userId.toString(), MessageDraftEntity.SyncStatus.PENDING.name());
    }
    
    /**
     * 标记草稿为已同步
     */
    @Transactional
    public void markAsSynced(Long draftId) {
        messageDraftRepository.updateSyncStatus(draftId, MessageDraftEntity.SyncStatus.SYNCED.name(), LocalDateTime.now());
        
        // 更新缓存
        Optional<MessageDraftEntity> draftOpt = messageDraftRepository.findById(draftId);
        draftOpt.ifPresent(draft -> updateCache(draft.getUserId(), draft.getConversationId(), draft));
    }
    
    /**
     * 获取用户的草稿统计信息
     */
    public Map<String, Object> getDraftStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalDrafts = messageDraftRepository.countByUser(userId);
        Long pendingCount = messageDraftRepository.countByUserIdAndSyncStatus(userId, MessageDraftEntity.SyncStatus.PENDING.name());
        Long conflictCount = messageDraftRepository.countByUserIdAndSyncStatus(userId, MessageDraftEntity.SyncStatus.CONFLICT.name());
        
        stats.put("totalDrafts", totalDrafts);
        stats.put("pendingSync", pendingCount);
        stats.put("conflicts", conflictCount);
        stats.put("lastUpdated", LocalDateTime.now().toString());
        
        return stats;
    }
    
    /**
     * 批量同步草稿
     */
    @Transactional
    public List<MessageDraftEntity> batchSyncDrafts(Long userId, List<Map<String, Object>> draftChanges) {
        List<MessageDraftEntity> results = new ArrayList<>();
        
        for (Map<String, Object> change : draftChanges) {
            String deviceId = (String) change.get("deviceId");
            String conversationId = (String) change.get("conversationId");
            String draftContent = (String) change.get("draftContent");
            Long localVersion = ((Number) change.get("localVersion")).longValue();
            
            try {
                MessageDraftEntity syncedDraft = syncDraft(userId, deviceId, conversationId, draftContent, localVersion);
                results.add(syncedDraft);
            } catch (Exception e) {
                log.error("批量同步草稿失败: userId={}, conversationId={}", userId, conversationId, e);
            }
        }
        
        log.info("批量同步草稿完成: userId={}, total={}, success={}", userId, draftChanges.size(), results.size());
        
        return results;
    }
    
    /**
     * 清理旧草稿（定时任务）
     */
    @Transactional
    public int cleanOldDrafts(int daysToKeep) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        
        // 查找需要清理的草稿
        List<MessageDraftEntity> oldDrafts = messageDraftRepository.findByCreatedAtBefore(cutoffTime);
        
        // 从缓存中移除
        for (MessageDraftEntity draft : oldDrafts) {
            removeFromCache(draft.getUserId(), draft.getConversationId());
        }
        
        // 从数据库删除
        int deleted = messageDraftRepository.deleteOldDrafts(cutoffTime);
        
        log.info("清理旧草稿: cutoff={}, found={}, deleted={}", cutoffTime, oldDrafts.size(), deleted);
        
        return deleted;
    }
    
    /**
     * 更新草稿活跃状态
     */
    @Transactional
    public void updateActiveStatus(Long userId, String deviceId, String conversationId, boolean active) {
        Optional<MessageDraftEntity> draftOpt = messageDraftRepository
                .findByUserIdAndDeviceIdAndConversationId(userId, deviceId, conversationId);
        
        if (draftOpt.isPresent()) {
            MessageDraftEntity draft = draftOpt.get();
            draft.setActive(active);
            draft.setLastUpdatedAt(LocalDateTime.now());
            messageDraftRepository.save(draft);
            updateCache(userId, conversationId, draft);
        }
    }
    
    /**
     * 从缓存获取草稿
     */
    private MessageDraftEntity getCachedDraft(Long userId, String conversationId) {
        Map<String, MessageDraftEntity> userCache = userDraftCache.get(userId);
        if (userCache != null) {
            return userCache.get(conversationId);
        }
        return null;
    }
    
    /**
     * 更新缓存
     */
    private void updateCache(Long userId, String conversationId, MessageDraftEntity draft) {
        Map<String, MessageDraftEntity> userCache = userDraftCache.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        userCache.put(conversationId, draft);
    }
    
    /**
     * 从缓存移除草稿
     */
    private void removeFromCache(Long userId, String conversationId) {
        Map<String, MessageDraftEntity> userCache = userDraftCache.get(userId);
        if (userCache != null) {
            userCache.remove(conversationId);
            if (userCache.isEmpty()) {
                userDraftCache.remove(userId);
            }
        }
    }
}