package com.im.server.reaction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class MessageReactionController {

    private final MessageReactionService reactionService;

    @PostMapping("/add")
    public ResponseEntity<?> addReaction(@RequestParam String messageId,
                                         @RequestParam String userId,
                                         @RequestParam String emoji,
                                         @RequestParam(defaultValue = "EMOJI") String type) {
        return ResponseEntity.ok(reactionService.addReaction(messageId, userId, emoji, type));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeReaction(@RequestParam String messageId,
                                            @RequestParam String userId,
                                            @RequestParam String emoji) {
        return ResponseEntity.ok(reactionService.removeReaction(messageId, userId, emoji));
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<ReactionWithUsers>> getReactions(@PathVariable String messageId) {
        return ResponseEntity.ok(reactionService.getReactionsForMessage(messageId));
    }

    @GetMapping("/stats/{messageId}")
    public ResponseEntity<ReactionStats> getStats(@PathVariable String messageId) {
        return ResponseEntity.ok(reactionService.getReactionStats(messageId));
    }

    public record ReactionWithUsers(String emoji, Long count, List<String> userIds) {}
    public record ReactionStats(String messageId, java.util.Map<String, Long> counts) {}
}
