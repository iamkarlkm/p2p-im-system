package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadReceiptResponse {
    private Long userId;
    private String conversationId;
    private String messageId;
    private Long readAt;
    private List<String> readByUsers;
}
