package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptRequest {
    private Long userId;
    private String conversationId;
    private String messageId;
    private List<String> messageIds;
    private Long readAt;
}
