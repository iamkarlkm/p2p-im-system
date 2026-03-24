package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "im_reply_chain_node",
       indexes = {
           @Index(name = "idx_chain_id", columnList = "chainId"),
           @Index(name = "idx_message_id", columnList = "messageId")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyChainNode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long chainId;
    
    @Column(nullable = false)
    private Long messageId;
    
    @Column(nullable = false)
    private Long userId;
    
    private String userNickname;
    
    @Column(length = 500)
    private String contentPreview;
    
    @Column(length = 50)
    private String messageType;
    
    @Column(nullable = false)
    private Integer positionInBranch;
    
    @Column(nullable = false)
    private Boolean isDeleted;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
        if (positionInBranch == null) positionInBranch = 0;
    }
}
