package com.im.service.group.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群组成员响应 DTO
 */
@Data
public class GroupMemberResponse {
    private Long id;
    private String groupId;
    private String userId;
    private String nickname;
    private String role;
    private Boolean muted;
    private LocalDateTime mutedUntil;
    private LocalDateTime joinedAt;
    private LocalDateTime lastActiveAt;
}
