package com.im.backend.service.impl;

import com.im.backend.dto.ReplyMessageDTO;
import com.im.backend.dto.ReplyMessageRequest;
import com.im.backend.entity.MessageReply;
import com.im.backend.repository.MessageReplyRepository;
import com.im.backend.service.MessageReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息引用回复服务实现
 */
@Service
public class MessageReplyServiceImpl implements MessageReplyService {

    @Autowired
    private MessageReplyRepository replyRepository;

    @Override
    @Transactional
    public ReplyMessageDTO sendReply(Long senderId, String senderName, ReplyMessageRequest request) {
        MessageReply reply = new MessageReply();
        reply.setOriginalMessageId(request.getOriginalMessageId());
        reply.setSenderId(senderId);
        reply.setSenderName(senderName);
        reply.setConversationType(request.getConversationType());
        reply.setConversationId(request.getConversationId());
        reply.setReplyContent(request.getReplyContent());
        
        // 设置嵌套引用层级
        if (request.getParentReplyId() != null) {
            reply.setParentReplyId(request.getParentReplyId());
            MessageReply parentReply = replyRepository.findById(request.getParentReplyId())
                .orElse(null);
            if (parentReply != null) {
                reply.setReplyLevel(parentReply.getReplyLevel() + 1);
            } else {
                reply.setReplyLevel(1);
            }
        } else {
            reply.setReplyLevel(1);
        }
        
        // 生成回复消息ID（实际中应该由消息服务生成）
        reply.setReplyMessageId(System.currentTimeMillis());
        
        // 设置原消息预览（实际中应该查询原消息获取）
        reply.setOriginalContentPreview(generatePreview(request.getReplyContent()));
        reply.setOriginalSenderName("原发送者");
        
        MessageReply saved = replyRepository.save(reply);
        return convertToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyMessageDTO> getRepliesByOriginalMessage(Long originalMessageId) {
        List<MessageReply> replies = replyRepository.findByOriginalMessageIdOrderByCreatedAtDesc(originalMessageId);
        return replies.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReplyMessageDTO> getRepliesByConversation(String conversationType, Long conversationId, Pageable pageable) {
        Page<MessageReply> replyPage = replyRepository.findByConversationTypeAndConversationIdOrderByCreatedAtDesc(
            conversationType, conversationId, pageable);
        return replyPage.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyMessageDTO getReplyWithNested(Long replyId) {
        MessageReply reply = replyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("Reply not found"));
        
        ReplyMessageDTO dto = convertToDTO(reply);
        
        // 加载嵌套回复
        List<MessageReply> nested = replyRepository.findByParentReplyIdOrderByCreatedAtAsc(replyId);
        if (!nested.isEmpty()) {
            dto.setNestedReplies(nested.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyMessageDTO getReplyById(Long replyId) {
        MessageReply reply = replyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("Reply not found"));
        return convertToDTO(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyMessageDTO> getAllRelatedReplies(Long messageId) {
        List<MessageReply> replies = replyRepository.findAllRelatedReplies(messageId);
        return replies.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId, Long operatorId) {
        MessageReply reply = replyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("Reply not found"));
        
        // 检查权限：只有发送者可以删除
        if (!reply.getSenderId().equals(operatorId)) {
            throw new RuntimeException("No permission to delete this reply");
        }
        
        replyRepository.deleteById(replyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOriginalMessageId(Long replyId) {
        MessageReply reply = replyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("Reply not found"));
        return reply.getOriginalMessageId();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isReplyMessage(Long messageId) {
        return replyRepository.findByReplyMessageId(messageId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long getReplyCount(Long originalMessageId) {
        return replyRepository.countByOriginalMessageId(originalMessageId);
    }

    private String generatePreview(String content) {
        if (content == null) return "";
        if (content.length() <= 100) return content;
        return content.substring(0, 100) + "...";
    }

    private ReplyMessageDTO convertToDTO(MessageReply reply) {
        ReplyMessageDTO dto = new ReplyMessageDTO();
        dto.setId(reply.getId());
        dto.setOriginalMessageId(reply.getOriginalMessageId());
        dto.setReplyMessageId(reply.getReplyMessageId());
        dto.setSenderId(reply.getSenderId());
        dto.setSenderName(reply.getSenderName());
        dto.setConversationType(reply.getConversationType());
        dto.setConversationId(reply.getConversationId());
        dto.setReplyContent(reply.getReplyContent());
        dto.setOriginalContentPreview(reply.getOriginalContentPreview());
        dto.setOriginalSenderName(reply.getOriginalSenderName());
        dto.setParentReplyId(reply.getParentReplyId());
        dto.setReplyLevel(reply.getReplyLevel());
        dto.setCreatedAt(reply.getCreatedAt());
        return dto;
    }
}
