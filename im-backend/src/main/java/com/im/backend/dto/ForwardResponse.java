package com.im.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardResponse {
    private boolean success;
    private String message;
    private List<Long> newMessageIds;
    private String mergedForwardId;
    private LocalDateTime forwardedAt;

    public static ForwardResponse success(List<Long> newMessageIds, LocalDateTime forwardedAt) {
        return ForwardResponse.builder()
                .success(true)
                .message("Forward successful")
                .newMessageIds(newMessageIds)
                .forwardedAt(forwardedAt)
                .build();
    }

    public static ForwardResponse mergedSuccess(String mergedForwardId, LocalDateTime forwardedAt) {
        return ForwardResponse.builder()
                .success(true)
                .message("Merged forward successful")
                .mergedForwardId(mergedForwardId)
                .forwardedAt(forwardedAt)
                .build();
    }

    public static ForwardResponse error(String message) {
        return ForwardResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
