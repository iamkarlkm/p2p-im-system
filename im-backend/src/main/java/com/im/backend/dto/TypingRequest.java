package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingRequest {
    private String conversationId;
    private String conversationType; // PRIVATE / GROUP
}
