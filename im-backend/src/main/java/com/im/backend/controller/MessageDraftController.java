package com.im.backend.controller;

import com.im.backend.service.MessageDraftService;
import com.im.backend.dto.DraftRequest;
import com.im.backend.dto.DraftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/draft")
@RequiredArgsConstructor
public class MessageDraftController {

    private final MessageDraftService draftService;

    @PostMapping("/save")
    public ResponseEntity<DraftResponse> saveDraft(@RequestBody DraftRequest request) {
        DraftResponse response = draftService.saveDraft(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteDraft(@RequestParam Long userId,
                                                            @RequestParam String conversationId) {
        draftService.deleteDraft(userId, conversationId);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    @GetMapping("/get")
    public ResponseEntity<DraftResponse> getDraft(@RequestParam Long userId,
                                                   @RequestParam String conversationId) {
        Optional<DraftResponse> draft = draftService.getDraft(userId, conversationId);
        return draft.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/list")
    public ResponseEntity<List<DraftResponse>> getAllDrafts(@RequestParam Long userId) {
        List<DraftResponse> drafts = draftService.getAllDrafts(userId);
        return ResponseEntity.ok(drafts);
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> syncDraft(@RequestParam Long userId,
                                                         @RequestParam String conversationId,
                                                         @RequestParam Long deviceId) {
        draftService.syncDraftToDevice(userId, conversationId, deviceId);
        return ResponseEntity.ok(Map.of("status", "synced"));
    }
}
