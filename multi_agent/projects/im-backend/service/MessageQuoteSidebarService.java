package com.im.backend.service;

import com.im.backend.entity.MessageQuoteSidebarEntity;
import com.im.backend.repository.MessageQuoteSidebarRepository;
import com.im.backend.repository.MessageQuoteRepository;
import com.im.backend.entity.MessageQuoteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 引用消息侧边栏服务
 * 管理引用消息侧边栏的CRUD操作和业务逻辑
 */
@Service
@Transactional
public class MessageQuoteSidebarService {

    @Autowired
    private MessageQuoteSidebarRepository sidebarRepository;
    
    @Autowired
    private MessageQuoteRepository quoteRepository;

    // 基础CRUD操作

    /**
     * 添加引用消息到侧边栏
     */
    public MessageQuoteSidebarEntity addToSidebar(Long userId, Long sessionId, Long quoteId) {
        // 检查是否已存在
        Optional<MessageQuoteSidebarEntity> existing = sidebarRepository.findByUserIdAndQuoteId(userId, quoteId);
        if (existing.isPresent()) {
            MessageQuoteSidebarEntity entity = existing.get();
            entity.setLastViewedAt(Instant.now());
            return sidebarRepository.save(entity);
        }
        
        // 获取引用消息详情
        Optional<MessageQuoteEntity> quoteOpt = quoteRepository.findById(quoteId);
        if (!quoteOpt.isPresent()) {
            throw new IllegalArgumentException("引用消息不存在: " + quoteId);
        }
        
        MessageQuoteEntity quote = quoteOpt.get();
        
        // 创建侧边栏记录
        MessageQuoteSidebarEntity sidebarEntity = new MessageQuoteSidebarEntity(
            quoteId,
            userId,
            sessionId,
            truncatePreview(quote.getQuoteContent(), 100),
            quote.getSenderId(),
            quote.getSenderNickname(),
            quote.getMessageType(),
            quote.getCreatedAt()
        );
        
        return sidebarRepository.save(sidebarEntity);
    }

    /**
     * 批量添加引用消息到侧边栏
     */
    public List<MessageQuoteSidebarEntity> batchAddToSidebar(Long userId, Long sessionId, List<Long> quoteIds) {
        return quoteIds.stream()
                .map(quoteId -> addToSidebar(userId, sessionId, quoteId))
                .collect(Collectors.toList());
    }

    /**
     * 从侧边栏移除引用消息
     */
    public void removeFromSidebar(Long userId, Long quoteId) {
        Optional<MessageQuoteSidebarEntity> entity = sidebarRepository.findByUserIdAndQuoteId(userId, quoteId);
        if (entity.isPresent()) {
            sidebarRepository.delete(entity.get());
        }
    }

    /**
     * 批量从侧边栏移除引用消息
     */
    public void batchRemoveFromSidebar(Long userId, List<Long> quoteIds) {
        quoteIds.forEach(quoteId -> removeFromSidebar(userId, quoteId));
    }

    /**
     * 清除会话的所有侧边栏记录
     */
    public void clearSessionSidebar(Long userId, Long sessionId) {
        sidebarRepository.deleteByUserIdAndSessionId(userId, sessionId);
    }

    /**
     * 获取用户的侧边栏记录列表
     */
    public List<MessageQuoteSidebarEntity> getUserSidebarItems(Long userId, Long sessionId) {
        return sidebarRepository.findByUserIdAndSessionId(userId, sessionId);
    }

    /**
     * 获取用户的侧边栏记录列表（分页）
     */
    public Page<MessageQuoteSidebarEntity> getUserSidebarItemsPage(Long userId, Long sessionId, Pageable pageable) {
        return sidebarRepository.findByUserIdAndSessionId(userId, sessionId, pageable);
    }

    /**
     * 获取用户最近查看的侧边栏记录
     */
    public List<MessageQuoteSidebarEntity> getRecentSidebarItems(Long userId, Long sessionId) {
        return sidebarRepository.findRecentSidebarItemsByUserAndSession(userId, sessionId);
    }

    /**
     * 获取用户所有固定的侧边栏记录
     */
    public List<MessageQuoteSidebarEntity> getPinnedSidebarItems(Long userId) {
        return sidebarRepository.findPinnedSidebarItemsByUserId(userId);
    }

    /**
     * 获取会话中固定的侧边栏记录
     */
    public List<MessageQuoteSidebarEntity> getPinnedSidebarItemsInSession(Long userId, Long sessionId) {
        return sidebarRepository.findByUserIdAndSessionIdAndIsPinned(userId, sessionId, true);
    }

    /**
     * 更新侧边栏记录的固定状态
     */
    public MessageQuoteSidebarEntity togglePinStatus(Long userId, Long quoteId, boolean isPinned) {
        Optional<MessageQuoteSidebarEntity> entityOpt = sidebarRepository.findByUserIdAndQuoteId(userId, quoteId);
        if (!entityOpt.isPresent()) {
            throw new IllegalArgumentException("侧边栏记录不存在");
        }
        
        MessageQuoteSidebarEntity entity = entityOpt.get();
        entity.setIsPinned(isPinned);
        
        // 如果固定，更新位置索引为最大值+1
        if (isPinned) {
            List<MessageQuoteSidebarEntity> pinnedItems = sidebarRepository.findPinnedSidebarItemsByUserId(userId);
            int maxIndex = pinnedItems.stream()
                    .mapToInt(MessageQuoteSidebarEntity::getSidebarIndex)
                    .max()
                    .orElse(-1);
            entity.setSidebarIndex(maxIndex + 1);
        }
        
        return sidebarRepository.save(entity);
    }

    /**
     * 批量更新固定状态
     */
    public void batchUpdatePinStatus(Long userId, List<Long> quoteIds, boolean isPinned) {
        List<Long> existingIds = quoteIds.stream()
                .map(quoteId -> sidebarRepository.findByUserIdAndQuoteId(userId, quoteId))
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getId())
                .collect(Collectors.toList());
        
        if (!existingIds.isEmpty()) {
            sidebarRepository.updatePinnedStatus(existingIds, isPinned);
        }
    }

    /**
     * 更新侧边栏位置索引（用于重排序）
     */
    public MessageQuoteSidebarEntity updateSidebarIndex(Long userId, Long quoteId, Integer newIndex) {
        Optional<MessageQuoteSidebarEntity> entityOpt = sidebarRepository.findByUserIdAndQuoteId(userId, quoteId);
        if (!entityOpt.isPresent()) {
            throw new IllegalArgumentException("侧边栏记录不存在");
        }
        
        MessageQuoteSidebarEntity entity = entityOpt.get();
        entity.setSidebarIndex(newIndex);
        return sidebarRepository.save(entity);
    }

    /**
     * 批量更新侧边栏位置索引
     */
    public void batchUpdateSidebarIndices(Long userId, List<Long> quoteIds, List<Integer> indices) {
        if (quoteIds.size() != indices.size()) {
            throw new IllegalArgumentException("ID列表和索引列表长度不一致");
        }
        
        for (int i = 0; i < quoteIds.size(); i++) {
            updateSidebarIndex(userId, quoteIds.get(i), indices.get(i));
        }
    }

    /**
     * 更新最后查看时间
     */
    public void updateLastViewedAt(Long userId, Long quoteId) {
        Optional<MessageQuoteSidebarEntity> entityOpt = sidebarRepository.findByUserIdAndQuoteId(userId, quoteId);
        if (entityOpt.isPresent()) {
            MessageQuoteSidebarEntity entity = entityOpt.get();
            entity.setLastViewedAt(Instant.now());
            sidebarRepository.save(entity);
        }
    }

    /**
     * 批量更新最后查看时间
     */
    public void batchUpdateLastViewedAt(Long userId, List<Long> quoteIds) {
        List<Long> existingIds = quoteIds.stream()
                .map(quoteId -> sidebarRepository.findByUserIdAndQuoteId(userId, quoteId))
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getId())
                .collect(Collectors.toList());
        
        if (!existingIds.isEmpty()) {
            sidebarRepository.batchUpdateLastViewedAt(existingIds, Instant.now());
        }
    }

    // 清理和维护操作

    /**
     * 清理超过指定天数未查看的非固定侧边栏记录
     */
    public int cleanupStaleSidebarItems(int daysThreshold) {
        Instant threshold = Instant.now().minus(daysThreshold, ChronoUnit.DAYS);
        return sidebarRepository.deleteStaleSidebarItems(threshold);
    }

    /**
     * 自动清理侧边栏记录（默认清理30天未查看的非固定记录）
     */
    public int autoCleanupSidebarItems() {
        return cleanupStaleSidebarItems(30);
    }

    // 搜索功能

    /**
     * 搜索侧边栏记录
     */
    public List<MessageQuoteSidebarEntity> searchSidebarItems(Long userId, String keyword) {
        return sidebarRepository.searchSidebarItems(userId, keyword);
    }

    /**
     * 按消息类型搜索侧边栏记录
     */
    public List<MessageQuoteSidebarEntity> searchByMessageType(Long userId, String messageType) {
        return sidebarRepository.findByMessageType(userId, messageType);
    }

    /**
     * 按发送者搜索侧边栏记录
     */
    public List<MessageQuoteSidebarEntity> searchBySender(Long userId, Long senderId) {
        return sidebarRepository.findBySenderId(userId, senderId);
    }

    // 统计功能

    /**
     * 统计用户侧边栏记录数量
     */
    public long countUserSidebarItems(Long userId) {
        return sidebarRepository.countByUserId(userId);
    }

    /**
     * 统计会话侧边栏记录数量
     */
    public long countSessionSidebarItems(Long sessionId) {
        return sidebarRepository.countBySessionId(sessionId);
    }

    /**
     * 统计用户固定侧边栏记录数量
     */
    public long countPinnedSidebarItems(Long userId) {
        return sidebarRepository.countPinnedByUserId(userId);
    }

    /**
     * 按消息类型统计侧边栏记录
     */
    public List<Object[]> getSidebarStatsByMessageType(Long userId) {
        return sidebarRepository.countByMessageType(userId);
    }

    // 辅助方法

    private String truncatePreview(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    /**
     * 检查引用消息是否已在侧边栏
     */
    public boolean isInSidebar(Long userId, Long quoteId) {
        return sidebarRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
    }

    /**
     * 获取侧边栏记录详情
     */
    public Optional<MessageQuoteSidebarEntity> getSidebarItem(Long userId, Long quoteId) {
        return sidebarRepository.findByUserIdAndQuoteId(userId, quoteId);
    }

    /**
     * 获取所有侧边栏记录（管理用）
     */
    public List<MessageQuoteSidebarEntity> getAllSidebarItems() {
        return sidebarRepository.findAll();
    }

    /**
     * 获取侧边栏记录（分页，管理用）
     */
    public Page<MessageQuoteSidebarEntity> getAllSidebarItemsPage(Pageable pageable) {
        return sidebarRepository.findAll(pageable);
    }
}