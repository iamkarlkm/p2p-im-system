package com.im.backend.service.impl;

import com.im.backend.dto.CreateQuoteReplyRequest;
import com.im.backend.dto.MessageQuoteReplyDTO;
import com.im.backend.dto.MessageQuoteReplyDTO.QuotedMessageInfo;
import com.im.backend.model.Message;
import com.im.backend.model.MessageQuoteReply;
import com.im.backend.model.MessageQuoteReply.QuoteStatus;
import com.im.backend.model.MessageQuoteReply.QuoteType;
import com.im.backend.model.User;
import com.im.backend.repository.MessageQuoteReplyRepository;
import com.im.backend.repository.MessageRepository;
import com.im.backend.repository.UserRepository;
import com.im.backend.service.ConversationService;
import com.im.backend.service.MessageQuoteReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageQuoteReplyServiceImpl implements MessageQuoteReplyService {

    @Autowired
    private MessageQuoteReplyRepository quoteReplyRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationService conversationService;

    @Override
    @Transactional
    public MessageQuoteReplyDTO createQuoteReply(Long senderId, CreateQuoteReplyRequest request) {
        Message quotedMessage = messageRepository.findById(request.getQuotedMessageId())
            .orElseThrow(() -> new RuntimeException("引用的消息不存在"));

        if (!canQuoteMessage(senderId, request.getQuotedMessageId())) {
            throw new RuntimeException("无权引用此消息");
        }

        MessageQuoteReply quoteReply = new MessageQuoteReply();
        quoteReply.setQuotedMessageId(request.getQuotedMessageId());
        quoteReply.setConversationId(request.getConversationId());
        quoteReply.setSenderId(senderId);
        quoteReply.setReplyContent(request.getReplyContent());
        quoteReply.setIncludeOriginal(request.getIncludeOriginal());
        quoteReply.setHighlightKeywords(request.getHighlightKeywords());

        List<Long> batchIds = request.getBatchQuotedMessageIds();
        if (batchIds != null && !batchIds.isEmpty()) {
            quoteReply.setIsBatchQuote(true);
            quoteReply.setBatchQuotedMessageIds(batchIds);
            quoteReply.setQuoteType(QuoteType.MULTI);
        } else {
            quoteReply.setIsBatchQuote(false);
            quoteReply.setQuoteType(QuoteType.SINGLE);
        }

        if (request.getParentQuoteId() != null) {
            MessageQuoteReply parentQuote = quoteReplyRepository.findById(request.getParentQuoteId())
                .orElseThrow(() -> new RuntimeException("父引用不存在"));
            quoteReply.setParentQuoteId(request.getParentQuoteId());
            quoteReply.setQuoteLevel(parentQuote.getQuoteLevel() + 1);
            quoteReply.setRootQuoteId(parentQuote.getRootQuoteId() != null ? parentQuote.getRootQuoteId() : parentQuote.getId());
            quoteReply.setQuoteType(QuoteType.NESTED);

            List<Long> newChain = new ArrayList<>(parentQuote.getQuoteChain());
            newChain.add(parentQuote.getQuotedMessageId());
            quoteReply.setQuoteChain(newChain);
        } else {
            quoteReply.setQuoteLevel(1);
        }

        MessageQuoteReply saved = quoteReplyRepository.save(quoteReply);

        Message newMessage = new Message();
        newMessage.setConversationId(request.getConversationId());
        newMessage.setSenderId(senderId);
        newMessage.setContent(request.getReplyContent());
        newMessage.setMessageType("QUOTE_REPLY");
        newMessage.setMetadata("quoteReplyId", saved.getId().toString());
        Message savedMessage = messageRepository.save(newMessage);

        saved.setMessageId(savedMessage.getId());
        quoteReplyRepository.save(saved);

        return convertToDTO(saved);
    }

    @Override
    public MessageQuoteReplyDTO getQuoteReplyById(Long id) {
        MessageQuoteReply quoteReply = quoteReplyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("引用回复不存在"));
        return convertToDTO(quoteReply);
    }

    @Override
    public MessageQuoteReplyDTO getQuoteReplyByMessageId(Long messageId) {
        MessageQuoteReply quoteReply = quoteReplyRepository.findByMessageId(messageId)
            .stream().findFirst()
            .orElseThrow(() -> new RuntimeException("该消息没有引用回复"));
        return convertToDTO(quoteReply);
    }

    @Override
    public List<MessageQuoteReplyDTO> getQuoteRepliesByConversation(Long conversationId) {
        return quoteReplyRepository.findByConversationId(conversationId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<MessageQuoteReplyDTO> getQuoteRepliesBySender(Long senderId, Long conversationId, Pageable pageable) {
        return quoteReplyRepository.findBySenderIdAndConversationId(senderId, conversationId, pageable)
            .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public MessageQuoteReplyDTO updateQuoteReply(Long id, Long senderId, String newContent) {
        MessageQuoteReply quoteReply = quoteReplyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("引用回复不存在"));

        if (!quoteReply.getSenderId().equals(senderId)) {
            throw new RuntimeException("无权修改此引用回复");
        }

        quoteReply.setReplyContent(newContent);
        quoteReply.setStatus(QuoteStatus.EDITED);

        MessageQuoteReply updated = quoteReplyRepository.save(quoteReply);

        Message message = messageRepository.findById(quoteReply.getMessageId())
            .orElse(null);
        if (message != null) {
            message.setContent(newContent);
            messageRepository.save(message);
        }

        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteQuoteReply(Long id, Long senderId) {
        MessageQuoteReply quoteReply = quoteReplyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("引用回复不存在"));

        if (!quoteReply.getSenderId().equals(senderId)) {
            throw new RuntimeException("无权删除此引用回复");
        }

        quoteReply.setStatus(QuoteStatus.DELETED);
        quoteReplyRepository.save(quoteReply);

        Message message = messageRepository.findById(quoteReply.getMessageId())
            .orElse(null);
        if (message != null) {
            message.setDeleted(true);
            messageRepository.save(message);
        }
    }

    @Override
    public List<MessageQuoteReplyDTO> getQuoteTree(Long rootQuoteId) {
        return quoteReplyRepository.findQuoteTreeByRootId(rootQuoteId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<MessageQuoteReplyDTO> getNestedQuotes(Long parentQuoteId) {
        return quoteReplyRepository.findByParentQuoteId(parentQuoteId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Long countQuotesByMessage(Long messageId) {
        return quoteReplyRepository.countActiveQuotesByMessageId(messageId);
    }

    @Override
    public List<MessageQuoteReplyDTO> getQuotesByMessage(Long messageId) {
        return quoteReplyRepository.findActiveQuotesByMessageId(messageId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<MessageQuoteReplyDTO> getQuotesContainingInChain(Long conversationId, Long messageId) {
        return quoteReplyRepository.findByQuoteChainContaining(conversationId, messageId.toString())
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageQuoteReplyDTO recallQuoteReply(Long id, Long senderId) {
        MessageQuoteReply quoteReply = quoteReplyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("引用回复不存在"));

        if (!quoteReply.getSenderId().equals(senderId)) {
            throw new RuntimeException("无权撤回此引用回复");
        }

        quoteReply.setStatus(QuoteStatus.RECALLED);
        MessageQuoteReply recalled = quoteReplyRepository.save(quoteReply);

        Message message = messageRepository.findById(quoteReply.getMessageId())
            .orElse(null);
        if (message != null) {
            message.setRecalled(true);
            messageRepository.save(message);
        }

        return convertToDTO(recalled);
    }

    @Override
    public boolean canQuoteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null || message.getDeleted() || message.getRecalled()) {
            return false;
        }
        return conversationService.isUserInConversation(userId, message.getConversationId());
    }

    private MessageQuoteReplyDTO convertToDTO(MessageQuoteReply quoteReply) {
        MessageQuoteReplyDTO dto = new MessageQuoteReplyDTO();
        dto.setId(quoteReply.getId());
        dto.setMessageId(quoteReply.getMessageId());
        dto.setQuotedMessageId(quoteReply.getQuotedMessageId());
        dto.setConversationId(quoteReply.getConversationId());
        dto.setSenderId(quoteReply.getSenderId());
        dto.setReplyContent(quoteReply.getReplyContent());
        dto.setQuoteLevel(quoteReply.getQuoteLevel());
        dto.setRootQuoteId(quoteReply.getRootQuoteId());
        dto.setParentQuoteId(quoteReply.getParentQuoteId());
        dto.setQuoteChain(quoteReply.getQuoteChain());
        dto.setQuoteType(quoteReply.getQuoteType());
        dto.setIncludeOriginal(quoteReply.getIncludeOriginal());
        dto.setHighlightKeywords(quoteReply.getHighlightKeywords());
        dto.setIsBatchQuote(quoteReply.getIsBatchQuote());
        dto.setBatchQuotedMessageIds(quoteReply.getBatchQuotedMessageIds());
        dto.setStatus(quoteReply.getStatus());
        dto.setCreatedAt(quoteReply.getCreatedAt());
        dto.setUpdatedAt(quoteReply.getUpdatedAt());

        User sender = userRepository.findById(quoteReply.getSenderId()).orElse(null);
        if (sender != null) {
            dto.setSenderName(sender.getNickname());
            dto.setSenderAvatar(sender.getAvatarUrl());
        }

        Message quotedMessage = messageRepository.findById(quoteReply.getQuotedMessageId()).orElse(null);
        if (quotedMessage != null) {
            QuotedMessageInfo info = new QuotedMessageInfo();
            info.setMessageId(quotedMessage.getId());
            info.setSenderId(quotedMessage.getSenderId());
            info.setContent(quotedMessage.getContent());
            info.setMessageType(quotedMessage.getMessageType());
            info.setSentAt(quotedMessage.getSentAt());

            User quotedSender = userRepository.findById(quotedMessage.getSenderId()).orElse(null);
            if (quotedSender != null) {
                info.setSenderName(quotedSender.getNickname());
                info.setSenderAvatar(quotedSender.getAvatarUrl());
            }
            dto.setQuotedMessageInfo(info);
        }

        List<QuotedMessageInfo> chainDetails = new ArrayList<>();
        if (quoteReply.getQuoteChain() != null) {
            for (Long msgId : quoteReply.getQuoteChain()) {
                Message chainMsg = messageRepository.findById(msgId).orElse(null);
                if (chainMsg != null) {
                    QuotedMessageInfo chainInfo = new QuotedMessageInfo();
                    chainInfo.setMessageId(chainMsg.getId());
                    chainInfo.setSenderId(chainMsg.getSenderId());
                    chainInfo.setContent(chainMsg.getContent());
                    chainInfo.setMessageType(chainMsg.getMessageType());
                    chainInfo.setSentAt(chainMsg.getSentAt());

                    User chainSender = userRepository.findById(chainMsg.getSenderId()).orElse(null);
                    if (chainSender != null) {
                        chainInfo.setSenderName(chainSender.getNickname());
                        chainInfo.setSenderAvatar(chainSender.getAvatarUrl());
                    }
                    chainDetails.add(chainInfo);
                }
            }
        }
        dto.setQuoteChainDetails(chainDetails);

        return dto;
    }
}
