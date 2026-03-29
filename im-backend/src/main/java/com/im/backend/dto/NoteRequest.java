package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    private Long id;
    private String conversationId;
    private String content;
    private String quotedMessageId;
    private String quotedMessageContent;
    private List<Long> tagIds;
    private Integer page = 0;
    private Integer size = 20;
}
