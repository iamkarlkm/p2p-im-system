package com.im.backend.service;

import com.im.backend.dto.MessageEditDTO;
import com.im.backend.dto.MessageEditHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 消息编辑服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
public interface MessageEditService {

    /**
     * 编辑消息
     * 
     * @param editDTO 编辑请求
     * @param userId 当前用户ID
     * @return 编辑结果
     */
    MessageEditDTO editMessage(MessageEditDTO editDTO, Long userId);

    /**
     * 获取消息的编辑历史
     * 
     * @param messageId 消息ID
     * @return 编辑历史
     */
    MessageEditHistoryDTO getEditHistory(Long messageId);

    /**
     * 分页获取消息的编辑历史
     * 
     * @param messageId 消息ID
     * @param pageable 分页参数
     * @return 编辑历史分页
     */
    Page<MessageEditHistoryDTO.EditHistoryItem> getEditHistoryPage(Long messageId, Pageable pageable);

    /**
     * 检查消息是否可以编辑
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 检查结果
     */
    CanEditResult canEditMessage(Long messageId, Long userId);

    /**
     * 获取消息的编辑次数
     * 
     * @param messageId 消息ID
     * @return 编辑次数
     */
    int getEditCount(Long messageId);

    /**
     * 批量获取消息的编辑次数
     * 
     * @param messageIds 消息ID列表
     * @return 编辑次数映射
     */
    java.util.Map<Long, Integer> getEditCounts(List<Long> messageIds);

    /**
     * 回滚到指定版本
     * 
     * @param messageId 消息ID
     * @param editSequence 编辑序号
     * @param userId 当前用户ID
     * @return 回滚后的编辑记录
     */
    MessageEditDTO revertToVersion(Long messageId, Integer editSequence, Long userId);

    /**
     * 获取用户的编辑记录
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 编辑记录分页
     */
    Page<MessageEditDTO> getUserEditHistory(Long userId, Pageable pageable);

    /**
     * 是否可以编辑检查结果
     */
    class CanEditResult {
        private boolean canEdit;
        private String reason;
        private Integer remainingEditCount;
        private Integer editTimeLimitMinutes;

        public CanEditResult(boolean canEdit, String reason) {
            this.canEdit = canEdit;
            this.reason = reason;
        }

        public CanEditResult(boolean canEdit, String reason, Integer remainingEditCount, Integer editTimeLimitMinutes) {
            this.canEdit = canEdit;
            this.reason = reason;
            this.remainingEditCount = remainingEditCount;
            this.editTimeLimitMinutes = editTimeLimitMinutes;
        }

        // Getters and Setters
        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Integer getRemainingEditCount() {
            return remainingEditCount;
        }

        public void setRemainingEditCount(Integer remainingEditCount) {
            this.remainingEditCount = remainingEditCount;
        }

        public Integer getEditTimeLimitMinutes() {
            return editTimeLimitMinutes;
        }

        public void setEditTimeLimitMinutes(Integer editTimeLimitMinutes) {
            this.editTimeLimitMinutes = editTimeLimitMinutes;
        }
    }
}
