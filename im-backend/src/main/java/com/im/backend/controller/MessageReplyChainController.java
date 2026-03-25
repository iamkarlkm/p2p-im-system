package com.im.backend.controller;

import com.im.backend.dto.ReplyChainRequest;
import com.im.backend.dto.ReplyChainResponse;
import com.im.backend.dto.ReplyChainResponse.MessageContext;
import com.im.backend.service.MessageReplyChainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reply-chain")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessageReplyChainController {
    
    private final MessageReplyChainService chainService;
    
    @PostMapping("/create")
    public ResponseEntity<ReplyChainResponse> createReplyChain(
            @Valid @RequestBody ReplyChainRequest request,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Nickname", defaultValue = "Unknown") String userNickname) {
        log.info("Create reply chain request: {}", request);
        ReplyChainResponse response = chainService.createReplyChain(request, userId, userNickname);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{chainId}")
    public ResponseEntity<ReplyChainResponse> getReplyChain(@PathVariable Long chainId) {
        return ResponseEntity.ok(chainService.getReplyChain(chainId));
    }
    
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<ReplyChainResponse>> getConversationReplyChains(
            @PathVariable Long conversationId) {
        return ResponseEntity.ok(chainService.getConversationReplyChains(conversationId));
    }
    
    @GetMapping("/branch/{rootMessageId}")
    public ResponseEntity<ReplyChainResponse> getBranchTree(@PathVariable Long rootMessageId) {
        return ResponseEntity.ok(chainService.getBranchTree(rootMessageId));
    }
    
    @PutMapping("/message/{messageId}/deleted")
    public ResponseEntity<Void> markMessageDeleted(@PathVariable Long messageId) {
        chainService.markMessageDeleted(messageId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/context/{messageId}")
    public ResponseEntity<MessageContext> getMessageContext(@PathVariable Long messageId) {
        return ResponseEntity.ok(chainService.getMessageContext(messageId));
    }
    
    @DeleteMapping("/{chainId}")
    public ResponseEntity<Void> deleteChain(@PathVariable Long chainId) {
        chainService.deleteChain(chainId);
        return ResponseEntity.ok().build();
    }
}
