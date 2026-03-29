package com.im.backend.service.impl;

import com.im.backend.dto.MessageEditDTO;
import com.im.backend.dto.MessageEditHistoryDTO;
import com.im.backend.model.MessageEditHistory;
import com.im.backend.repository.MessageEditHistoryRepository;
import com.im.backend.service.MessageEditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息编辑服务实现
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
@Service
public class MessageEditServiceImpl implements MessageEditService {

    private static final Logger logger = LoggerFactory.getLogger(MessageEditServiceImpl.class);

    @Autowired
    private MessageEditHistoryRepository editHistoryRepository;

    @Value("${message.edit.max-count:10}")
    private int maxEditCount;

    @Value("${message.edit.time-window-minutes:30}")
    private int editTimeWindowMinutes;

    @Value("${message.edit.allow-admin-edit:true}")
    private boolean allowAdminEdit;

    @Override
    @Transactional
    public MessageEditDTO editMessage(MessageEditDTO editDTO, Long userId) {
        logger.debug("Editing message: {}, user: {}", editDTO.getMessageId(), userId);

        // 检查编辑权限
        CanEditResult canEdit = canEditMessage(editDTO.getMessageId(), userId);
        if (!canEdit.isCanEdit()) {
            throw new IllegalStateException(canEdit.getReason());
        }

        // 获取当前编辑序号
        Integer currentSequence = editHistoryRepository.findLatestEditSequenceByMessageId(editDTO.getMessageId());
        int newSequence = currentSequence + 1;

        // 创建编辑历史记录
        MessageEditHistory editHistory = new MessageEditHistory();
        editHistory.setMessageId(editDTO.getMessageId());
        editHistory.setUserId(userId);
        editHistory.setOriginalContent(editDTO.getOriginalContent());
        editHistory.setEditedContent(editDTO.getEditedContent());
        editHistory.setEditReason(editDTO.getEditReason());
        editHistory.setEditSequence(newSequence);
        editHistory.setEditType(editDTO.getEditType() != null ? editDTO.getEditType() : MessageEditHistory.EditType.NORMAL);

        editHistoryRepository.save(editHistory);

        // 构建返回结果
        MessageEditDTO result = new MessageEditDTO();
        result.setId(editHistory.getId());
        result.setMessageId(editHistory.getMessageId());
        result.setUserId(editHistory.getUserId());
        result.setOriginalContent(editHistory.getOriginalContent());
        result.setEditedContent(editHistory.getEditedContent());
        result.setEditReason(editHistory.getEditReason());
        result.setEditSequence(editHistory.getEditSequence());
        result.setEditType(editHistory.getEditType());
        result.setEditedAt(editHistory.getCreatedAt());
        result.calculateChangeStats();
        result.generateEditMarkText();

        logger.info("Message edited successfully: {}, sequence: {}", editDTO.getMessageId(), newSequence);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageEditHistoryDTO getEditHistory(Long messageId) {
        logger.debug("Getting edit history for message: {}", messageId);

        List<MessageEditHistory> histories = editHistoryRepository.findByMessageIdOrderByEditSequenceAsc(messageId);
        
        MessageEditHistoryDTO dto = new MessageEditHistoryDTO();
        dto.setMessageId(messageId);
        dto.setTotalEditCount(histories.size());
        dto.setEditTimeWindowMinutes(editTimeWindowMinutes);
        dto.setMaxEditCount(maxEditCount);

        if (!histories.isEmpty()) {
            MessageEditHistory lastEdit = histories.get(histories.size() - 1);
            dto.setCurrentContent(lastEdit.getEditedContent());
            dto.setOriginalContent(histories.get(0).getOriginalContent());
            dto.setLastEditedAt(lastEdit.getCreatedAt());
            
            // 设置最后编辑用户信息（简化处理）
            MessageEditHistoryDTO.UserSummaryDTO userSummary = new MessageEditHistoryDTO.UserSummaryDTO();
            userSummary.setId(lastEdit.getUserId());
            dto.setLastEditedBy(userSummary);

            // 构建编辑历史项列表
            List<MessageEditHistoryDTO.EditHistoryItem> items = histories.stream()
                .map(this::convertToHistoryItem)
                .collect(Collectors.toList());
            dto.setEditHistory(items);

            // 计算统计信息
            dto.setStatistics(calculateStatistics(histories));
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageEditHistoryDTO.EditHistoryItem> getEditHistoryPage(Long messageId, Pageable pageable) {
        return editHistoryRepository.findByMessageIdOrderByEditSequenceDesc(messageId, pageable)
            .map(this::convertToHistoryItem);
    }

    @Override
    @Transactional(readOnly = true)
    public CanEditResult canEditMessage(Long messageId, Long userId) {
        // 检查编辑次数限制
        int currentEditCount = getEditCount(messageId);
        if (currentEditCount >= maxEditCount) {
            return new CanEditResult(false, "已达到最大编辑次数限制（" + maxEditCount + "次）");
        }

        // 检查时间窗口（简化处理，实际应该查询消息创建时间）
        Optional<MessageEditHistory> lastEdit = editHistoryRepository.findTopByMessageIdOrderByEditSequenceDesc(messageId);
        if (lastEdit.isPresent()) {
            LocalDateTime lastEditTime = lastEdit.get().getCreatedAt();
            long minutesSinceLastEdit = ChronoUnit.MINUTES.between(lastEditTime, LocalDateTime.now());
            if (minutesSinceLastEdit > editTimeWindowMinutes) {
                return new CanEditResult(false, "已超过编辑时间窗口（" + editTimeWindowMinutes + "分钟）");
            }
        }

        int remainingEdits = maxEditCount - currentEditCount;
        return new CanEditResult(true, "可以编辑", remainingEdits, editTimeWindowMinutes);
    }

    @Override
    @Transactional(readOnly = true)
    public int getEditCount(Long messageId) {
        return (int) editHistoryRepository.countByMessageId(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getEditCounts(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return Collections.emptyMap();
        }
        
        List<Object[]> results = editHistoryRepository.countByMessageIds(messageIds);
        Map<Long, Integer> counts = new HashMap<>();
        
        for (Object[] result : results) {
            Long messageId = (Long) result[0];
            Long count = (Long) result[1];
            counts.put(messageId, count.intValue());
        }
        
        // 填充未编辑的消息
        for (Long messageId : messageIds) {
            counts.putIfAbsent(messageId, 0);
        }
        
        return counts;
    }

    @Override
    @Transactional
    public MessageEditDTO revertToVersion(Long messageId, Integer editSequence, Long userId) {
        logger.debug("Reverting message {} to version {}", messageId, editSequence);

        List<MessageEditHistory> histories = editHistoryRepository.findByMessageIdOrderByEditSequenceAsc(messageId);
        
        // 查找目标版本
        MessageEditHistory targetVersion = histories.stream()
            .filter(h -> h.getEditSequence().equals(editSequence))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("指定的版本不存在"));

        // 获取当前最新内容
        String currentContent = histories.get(histories.size() - 1).getEditedContent();
        String targetContent = targetVersion.getEditedContent();

        // 创建回滚编辑记录
        MessageEditDTO revertDTO = new MessageEditDTO();
        revertDTO.setMessageId(messageId);
        revertDTO.setOriginalContent(currentContent);
        revertDTO.setEditedContent(targetContent);
        revertDTO.setEditReason("回滚到版本 #" + editSequence);
        revertDTO.setEditType(MessageEditHistory.EditType.REVERT);

        return editMessage(revertDTO, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageEditDTO> getUserEditHistory(Long userId, Pageable pageable) {
        return editHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(this::convertToDTO);
    }

    // ==================== 私有方法 ====================

    private MessageEditHistoryDTO.EditHistoryItem convertToHistoryItem(MessageEditHistory history) {
        MessageEditHistoryDTO.EditHistoryItem item = new MessageEditHistoryDTO.EditHistoryItem();
        item.setEditId(history.getId());
        item.setSequence(history.getEditSequence());
        item.setBeforeContent(history.getOriginalContent());
        item.setAfterContent(history.getEditedContent());
        item.setEditReason(history.getEditReason());
        item.setEditType(history.getEditType().name());
        item.setEditedAt(history.getCreatedAt());
        
        // 设置用户信息（简化处理）
        MessageEditHistoryDTO.UserSummaryDTO userSummary = new MessageEditHistoryDTO.UserSummaryDTO();
        userSummary.setId(history.getUserId());
        item.setEditedBy(userSummary);

        // 计算内容差异（简化实现）
        item.setContentDiff(calculateDiff(history.getOriginalContent(), history.getEditedContent()));

        return item;
    }

    private MessageEditDTO convertToDTO(MessageEditHistory history) {
        MessageEditDTO dto = new MessageEditDTO();
        dto.setId(history.getId());
        dto.setMessageId(history.getMessageId());
        dto.setUserId(history.getUserId());
        dto.setOriginalContent(history.getOriginalContent());
        dto.setEditedContent(history.getEditedContent());
        dto.setEditReason(history.getEditReason());
        dto.setEditSequence(history.getEditSequence());
        dto.setEditType(history.getEditType());
        dto.setEditedAt(history.getCreatedAt());
        return dto;
    }

    private MessageEditHistoryDTO.ContentDiff calculateDiff(String before, String after) {
        MessageEditHistoryDTO.ContentDiff diff = new MessageEditHistoryDTO.ContentDiff();
        
        // 简化差异计算
        List<MessageEditHistoryDTO.DiffSegment> segments = new ArrayList<>();
        
        if (!Objects.equals(before, after)) {
            if (before != null && !before.isEmpty()) {
                MessageEditHistoryDTO.DiffSegment removed = new MessageEditHistoryDTO.DiffSegment();
                removed.setType(MessageEditHistoryDTO.DiffSegment.DiffType.REMOVED);
                removed.setContent(before);
                segments.add(removed);
            }
            if (after != null && !after.isEmpty()) {
                MessageEditHistoryDTO.DiffSegment added = new MessageEditHistoryDTO.DiffSegment();
                added.setType(MessageEditHistoryDTO.DiffSegment.DiffType.ADDED);
                added.setContent(after);
                segments.add(added);
            }
        } else {
            MessageEditHistoryDTO.DiffSegment unchanged = new MessageEditHistoryDTO.DiffSegment();
            unchanged.setType(MessageEditHistoryDTO.DiffSegment.DiffType.UNCHANGED);
            unchanged.setContent(before);
            segments.add(unchanged);
        }
        
        diff.setSegments(segments);
        diff.setAddedCount(after != null ? after.length() : 0);
        diff.setRemovedCount(before != null ? before.length() : 0);
        
        return diff;
    }

    private MessageEditHistoryDTO.EditStatistics calculateStatistics(List<MessageEditHistory> histories) {
        MessageEditHistoryDTO.EditStatistics stats = new MessageEditHistoryDTO.EditStatistics();
        
        stats.setTotalEdits(histories.size());
        
        // 计算所有者编辑次数（简化处理）
        long ownerEdits = histories.stream()
            .filter(h -> h.getEditType() != MessageEditHistory.EditType.SYSTEM)
            .count();
        stats.setEditsByOwner((int) ownerEdits);

        // 计算平均编辑间隔
        if (histories.size() > 1) {
            double totalMinutes = 0;
            for (int i = 1; i < histories.size(); i++) {
                long minutes = ChronoUnit.MINUTES.between(
                    histories.get(i - 1).getCreatedAt(),
                    histories.get(i).getCreatedAt()
                );
                totalMinutes += minutes;
            }
            stats.setAverageEditIntervalMinutes(totalMinutes / (histories.size() - 1));
        }

        // 计算内容变化
        int totalAdded = 0;
        int totalRemoved = 0;
        for (MessageEditHistory history : histories) {
            int change = history.getEditedContent().length() - history.getOriginalContent().length();
            if (change > 0) {
                totalAdded += change;
            } else {
                totalRemoved += Math.abs(change);
            }
        }
        stats.setTotalContentAdded(totalAdded);
        stats.setTotalContentRemoved(totalRemoved);

        return stats;
    }
}
