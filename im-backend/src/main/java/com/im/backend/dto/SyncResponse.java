package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncResponse {

    private List<MessageSyncItem> messages;
    private Long nextMessageId;
    private String nextSyncToken;
    private LocalDateTime syncTimestamp;
    private Boolean hasMore;
    private Integer totalSynced;
}
