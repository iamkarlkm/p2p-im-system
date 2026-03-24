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
public class ReplyChainResponse {
    
    private Long id;
    private Long conversationId;
    private Long rootMessageId;
    private Long parentMessageId;
    private Long userId;
    private String userNickname;
    private Integer depth;
    private String branchPath;
    private Boolean isBranchNode;
    private Boolean isRoot;
    private Boolean isLeaf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReplyNodeResponse> branchNodes;
    private MessageContext context;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReplyNodeResponse {
        private Long id;
        private Long messageId;
        private Long userId;
        private String userNickname;
        private String contentPreview;
        private String messageType;
        private Integer positionInBranch;
        private LocalDateTime createdAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageContext {
        private Long messageId;
        private String content;
        private String senderName;
        private String messageType;
        private LocalDateTime timestamp;
        private String thumbnailUrl;
    }
}
