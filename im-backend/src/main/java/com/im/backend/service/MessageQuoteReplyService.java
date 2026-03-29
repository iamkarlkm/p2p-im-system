package com.im.backend.service;

import com.im.backend.dto.CreateQuoteReplyRequest;
import com.im.backend.dto.MessageQuoteReplyDTO;
import com.im.backend.model.MessageQuoteReply.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 消息引用回复服务接口
 */
public interface MessageQuoteReplyService {

    MessageQuoteReplyDTO createQuoteReply(Long senderId, CreateQuoteReplyRequest request);

    MessageQuoteReplyDTO getQuoteReplyById(Long id);

    MessageQuoteReplyDTO getQuoteReplyByMessageId(Long messageId);

    List<MessageQuoteReplyDTO> getQuoteRepliesByConversation(Long conversationId);

    Page<MessageQuoteReplyDTO> getQuoteRepliesBySender(Long senderId, Long conversationId, Pageable pageable);

    MessageQuoteReplyDTO updateQuoteReply(Long id, Long senderId, String newContent);

    void deleteQuoteReply(Long id, Long senderId);

    List<MessageQuoteReplyDTO> getQuoteTree(Long rootQuoteId);

    List<MessageQuoteReplyDTO> getNestedQuotes(Long parentQuoteId);

    Long countQuotesByMessage(Long messageId);

    List<MessageQuoteReplyDTO> getQuotesByMessage(Long messageId);

    List<MessageQuoteReplyDTO> getQuotesContainingInChain(Long conversationId, Long messageId);

    MessageQuoteReplyDTO recallQuoteReply(Long id, Long senderId);

    boolean canQuoteMessage(Long userId, Long messageId);
}
