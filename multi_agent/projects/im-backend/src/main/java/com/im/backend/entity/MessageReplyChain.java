package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "im_message_reply_chain",
       indexes = {
           @Index(name = "idx_conversation", columnList = "conversationId"),
           @Index(name = "idx_root_message", columnList = "rootMessageId"),
           @Index(name = "idx_parent_message", columnList = "parentMessageId")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReplyChain {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long conversationId;
    
    @Column(nullable = false)
    private Long rootMessageId;
    
    @Column(nullable = false)
    private Long parentMessageId;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Integer depth;
    
    @Column(length = 2048)
    private String branchPath;
    
    @Column(nullable = false)
    private Boolean isBranchNode;
    
    @Column(nullable = false)
    private Boolean isRoot;
    
    @Column(nullable = false)
    private Boolean isLeaf;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (depth == null) depth = 0;
        if (isBranchNode == null) isBranchNode = false;
        if (isRoot == null) isRoot = false;
        if (isLeaf == null) isLeaf = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
