package com.im.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.im.message.dto.MessageQueryRequest;
import com.im.message.dto.MessageRecallRequest;
import com.im.message.dto.MessageSearchResponse;
import com.im.message.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息服务接口 - 消息存储与检索核心服务
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface MessageService {
    
    /**
     * 保存消息
     * 
     * @param message 消息实体
     * @return 保存后的消息
     */
    Message saveMessage(Message message);
    
    /**
     * 批量保存消息
     * 
     * @param messages 消息列表
     * @return 保存数量
     */
    int batchSaveMessages(List<Message> messages);
    
    /**
     * 根据ID查询消息
     * 
     * @param messageId 消息ID
     * @return 消息实体
     */
    Message getMessageById(Long messageId);
    
    /**
     * 根据UUID查询消息
     * 
     * @param messageUuid 消息UUID
     * @return 消息实体
     */
    Message getMessageByUuid(String messageUuid);
    
    /**
     * 查询会话历史消息
     * 
     * @param request 查询请求
     * @return 消息列表
     */
    List<MessageSearchResponse> queryHistoryMessages(MessageQueryRequest request);
    
    /**
     * 分页查询消息
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<MessageSearchResponse> queryMessagePage(MessageQueryRequest request);
    
    /**
     * 撤回消息
     * 
     * @param request 撤回请求
     * @return 是否成功
     */
    boolean recallMessage(MessageRecallRequest request);
    
    /**
     * 标记消息已读
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markMessageAsRead(Long messageId, Long userId);
    
    /**
     * 批量标记已读
     * 
     * @param messageIds 消息ID列表
     * @param userId 用户ID
     * @return 成功数量
     */
    int batchMarkAsRead(List<Long> messageIds, Long userId);
    
    /**
     * 删除消息(软删除)
     * 
     * @param messageId 消息ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean deleteMessage(Long messageId, Long userId);
    
    /**
     * 获取会话最新消息ID
     * 
     * @param conversationType 会话类型
     * @param conversationId 会话ID
     * @return 最新消息ID
     */
    Long getLatestMessageId(Integer conversationType, Long conversationId);
    
    /**
     * 获取会话未读消息数
     * 
     * @param conversationType 会话类型
     * @param conversationId 会话ID
     * @return 未读数量
     */
    int getUnreadCount(Integer conversationType, Long conversationId);
    
    /**
     * 查询@我的消息
     * 
     * @param request 查询请求
     * @return 消息列表
     */
    List<MessageSearchResponse> queryMentionMessages(MessageQueryRequest request);
    
    /**
     * 查询包含附件的消息
     * 
     * @param request 查询请求
     * @return 消息列表
     */
    List<MessageSearchResponse> queryMessagesWithAttachment(MessageQueryRequest request);
    
    /**
     * 获取某时间段内的消息数量
     * 
     * @param conversationType 会话类型
     * @param conversationId 会话ID
     * @param sinceTime 起始时间
     * @return 消息数量
     */
    int getMessageCountSince(Integer conversationType, Long conversationId, LocalDateTime sinceTime);
}
