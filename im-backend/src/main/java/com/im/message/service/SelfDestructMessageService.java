package com.im.message.service;

import com.im.message.dto.SelfDestructMessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 阅后即焚消息服务接口
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface SelfDestructMessageService {

    /**
     * 创建阅后即焚消息
     *
     * @param senderId 发送者ID
     * @param request 创建请求
     * @return 创建的消息DTO
     */
    SelfDestructMessageDTO createMessage(String senderId, SelfDestructMessageDTO.CreateRequest request);

    /**
     * 阅读消息
     *
     * @param messageId 消息ID
     * @param receiverId 接收者ID
     * @return 阅读响应（包含内容和剩余时间）
     */
    SelfDestructMessageDTO.ReadResponse readMessage(String messageId, String receiverId);

    /**
     * 获取消息详情（发送者）
     *
     * @param messageId 消息ID
     * @param senderId 发送者ID
     * @return 消息DTO
     */
    SelfDestructMessageDTO getMessageForSender(String messageId, String senderId);

    /**
     * 获取消息详情（接收者）
     *
     * @param messageId 消息ID
     * @param receiverId 接收者ID
     * @return 消息DTO
     */
    SelfDestructMessageDTO getMessageForReceiver(String messageId, String receiverId);

    /**
     * 获取会话中的消息列表
     *
     * @param conversationId 会话ID
     * @param userId 当前用户ID
     * @return 消息列表
     */
    List<SelfDestructMessageDTO> getMessagesByConversation(String conversationId, String userId);

    /**
     * 分页获取会话消息
     *
     * @param conversationId 会话ID
     * @param userId 当前用户ID
     * @param pageable 分页参数
     * @return 分页消息
     */
    Page<SelfDestructMessageDTO> getMessagesByConversation(String conversationId, String userId, Pageable pageable);

    /**
     * 获取发送的消息
     *
     * @param senderId 发送者ID
     * @return 消息列表
     */
    List<SelfDestructMessageDTO> getSentMessages(String senderId);

    /**
     * 获取接收的消息
     *
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<SelfDestructMessageDTO> getReceivedMessages(String receiverId);

    /**
     * 获取未读消息数量
     *
     * @param receiverId 接收者ID
     * @return 未读数量
     */
    Long getUnreadCount(String receiverId);

    /**
     * 获取会话未读数量
     *
     * @param conversationId 会话ID
     * @param receiverId 接收者ID
     * @return 未读数量
     */
    Long getUnreadCountByConversation(String conversationId, String receiverId);

    /**
     * 删除消息（发送者可删除自己发送的）
     *
     * @param messageId 消息ID
     * @param senderId 发送者ID
     */
    void deleteMessage(String messageId, String senderId);

    /**
     * 检测截图
     *
     * @param messageId 消息ID
     * @param request 检测请求
     * @param detectorId 检测者ID
     * @return 检测结果
     */
    SelfDestructMessageDTO.ScreenshotDetectResponse detectScreenshot(String messageId, SelfDestructMessageDTO.ScreenshotDetectRequest request, String detectorId);

    /**
     * 获取被截图的消息列表（发送者查看谁截图了自己的消息）
     *
     * @param senderId 发送者ID
     * @return 被截图的消息列表
     */
    List<SelfDestructMessageDTO> getScreenshotDetectedMessages(String senderId);

    /**
     * 手动销毁消息
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     */
    void destroyMessage(String messageId, String userId);

    /**
     * 检查消息是否已销毁
     *
     * @param messageId 消息ID
     * @return 是否已销毁
     */
    Boolean isMessageDestroyed(String messageId);

    /**
     * 获取消息剩余时间
     *
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 剩余秒数
     */
    Integer getRemainingSeconds(String messageId, String userId);

    /**
     * 清理已销毁的旧消息（定时任务用）
     */
    void cleanupOldDestroyedMessages();

    /**
     * 处理过期消息（定时任务用）
     */
    void processExpiredMessages();
}
