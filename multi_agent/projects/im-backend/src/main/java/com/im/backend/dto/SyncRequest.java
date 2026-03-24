package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncRequest {

    private String deviceId;
    private Long conversationId;
    private Long lastMessageId;
    private String syncToken;
    private Integer limit;
    private LocalDateTime since;
}
