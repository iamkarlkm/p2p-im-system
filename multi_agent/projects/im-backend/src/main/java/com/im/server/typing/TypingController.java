package com.im.server.typing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/typing")
@RequiredArgsConstructor
public class TypingController {

    private final TypingService typingService;

    @PostMapping("/start")
    public ResponseEntity<Void> startTyping(@RequestParam String conversationId,
                                            @RequestParam String userId) {
        typingService.onTypingStart(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopTyping(@RequestParam String conversationId,
                                           @RequestParam String userId) {
        typingService.onTypingStop(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active/{conversationId}")
    public ResponseEntity<Map<String, Long>> getActiveTyping(@PathVariable String conversationId) {
        return ResponseEntity.ok(typingService.getTypingUsers(conversationId));
    }
}
