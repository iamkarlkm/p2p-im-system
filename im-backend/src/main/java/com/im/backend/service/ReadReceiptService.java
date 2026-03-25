package com.im.backend.service;

import com.im.backend.entity.ReadReceipt;
import com.im.backend.repository.ReadReceiptRepository;
import com.im.backend.dto.ReadReceiptRequest;
import com.im.backend.dto.ReadReceiptResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadReceiptService {

    private final ReadReceiptRepository receiptRepository;

    @Transactional
    public ReadReceiptResponse markAsRead(ReadReceiptRequest request) {
        Long now = request.getReadAt() != null ? request.getReadAt() : System.currentTimeMillis();

        ReadReceipt receipt = ReadReceipt.builder()
                .userId(request.getUserId())
                .conversationId(request.getConversationId())
                .messageId(request.getMessageId())
                .readAt(now)
                .isDeleted(false)
                .build();
        receiptRepository.save(receipt);

        log.info("User {} marked message {} as read in conversation {}",
                request.getUserId(), request.getMessageId(), request.getConversationId());

        return ReadReceiptResponse.builder()
                .userId(request.getUserId())
                .conversationId(request.getConversationId())
                .messageId(request.getMessageId())
                .readAt(now)
                .build();
    }

    @Transactional
    public List<ReadReceiptResponse> markBatchAsRead(ReadReceiptRequest request) {
        Long now = request.getReadAt() != null ? request.getReadAt() : System.currentTimeMillis();
        List<String> messageIds = request.getMessageIds();

        List<ReadReceipt> receipts = messageIds.stream().map(msgId -> ReadReceipt.builder()
                .userId(request.getUserId())
                .conversationId(request.getConversationId())
                .messageId(msgId)
                .readAt(now)
                .isBatch(true)
                .build()
        ).collect(Collectors.toList());

        receiptRepository.saveAll(receipts);
        log.info("User {} batch-marked {} messages as read in conversation {}",
                request.getUserId(), messageIds.size(), request.getConversationId());

        return messageIds.stream().map(msgId -> ReadReceiptResponse.builder()
                .userId(request.getUserId())
                .conversationId(request.getConversationId())
                .messageId(msgId)
                .readAt(now)
                .build()
        ).collect(Collectors.toList());
    }

    public List<ReadReceiptResponse> getReadReceipts(String conversationId, String messageId) {
        return receiptRepository.findByConversationIdAndMessageId(conversationId, messageId)
                .stream().map(r -> ReadReceiptResponse.builder()
                        .userId(r.getUserId())
                        .conversationId(r.getConversationId())
                        .messageId(r.getMessageId())
                        .readAt(r.getReadAt())
                        .build()
                ).collect(Collectors.toList());
    }
}
