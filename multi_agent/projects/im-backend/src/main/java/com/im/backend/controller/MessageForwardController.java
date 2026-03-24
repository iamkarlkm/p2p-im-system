package com.im.backend.controller;

import com.im.backend.dto.ForwardRequest;
import com.im.backend.dto.ForwardResponse;
import com.im.backend.service.MessageForwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages/forward")
@RequiredArgsConstructor
public class MessageForwardController {
    private final MessageForwardService forwardService;

    @PostMapping
    public ResponseEntity<ForwardResponse> forwardMessage(
            @Valid @RequestBody ForwardRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        ForwardResponse response = forwardService.forwardMessages(request, userId);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchForward(
            @RequestBody List<ForwardRequest> requests,
            @RequestHeader("X-User-Id") Long userId) {
        List<ForwardResponse> results = requests.stream()
                .map(r -> forwardService.forwardMessages(r, userId))
                .toList();
        long successCount = results.stream().filter(ForwardResponse::isSuccess).count();
        return ResponseEntity.ok(Map.of(
                "total", requests.size(),
                "success", successCount,
                "failed", requests.size() - successCount,
                "results", results
        ));
    }

    @GetMapping("/history/{messageId}")
    public ResponseEntity<List<?>> getForwardHistory(@PathVariable Long messageId) {
        return ResponseEntity.ok(forwardService.getForwardHistory(messageId));
    }
}
