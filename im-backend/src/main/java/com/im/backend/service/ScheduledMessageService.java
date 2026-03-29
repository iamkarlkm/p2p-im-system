package com.im.backend.service;

import com.im.backend.dto.ScheduledMessageDTO;
import com.im.backend.model.ScheduledMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 定时消息服务接口
 */
public interface ScheduledMessageService {

    /**
     * 创建定时消息
     */
    ScheduledMessageDTO createScheduledMessage(Long senderId, ScheduledMessageDTO dto);

    /**
     * 取消定时消息
     */
    ScheduledMessageDTO cancelScheduledMessage(Long messageId, Long senderId);

    /**
     * 删除定时消息
     */
    void deleteScheduledMessage(Long messageId, Long senderId);

    /**
     * 获取定时消息详情
     */
    Optional<ScheduledMessageDTO> getScheduledMessage(Long messageId, Long senderId);

    /**
     * 获取用户的定时消息列表
     */
    Page<ScheduledMessageDTO> getUserScheduledMessages(Long senderId, Pageable pageable);

    /**
     * 按状态筛选定时消息
     */
    Page<ScheduledMessageDTO> getUserScheduledMessagesByStatus(
            Long senderId, ScheduledMessage.Status status, Pageable pageable);

    /**
     * 更新定时消息
     */
    ScheduledMessageDTO updateScheduledMessage(Long messageId, Long senderId, ScheduledMessageDTO dto);

    /**
     * 获取待发送的消息列表（供调度器使用）
     */
    List<ScheduledMessageDTO> getPendingMessagesForSending();

    /**
     * 标记消息为已发送
     */
    void markAsSent(Long messageId);

    /**
     * 标记消息为发送失败
     */
    void markAsFailed(Long messageId, String reason);

    /**
     * 统计用户待发送消息数量
     */
    long countPendingMessages(Long senderId);
}
