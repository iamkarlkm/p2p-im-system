package com.im.backend.controller;

import com.im.backend.entity.ConversationFolder;
import com.im.backend.entity.ConversationFolderMembership;
import com.im.backend.service.ConversationFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/conversation-folders")
@RequiredArgsConstructor
public class ConversationFolderController {

    private final ConversationFolderService folderService;

    @GetMapping
    public ResponseEntity<List<ConversationFolder>> getFolders(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(folderService.getUserFolders(userId));
    }

    @PostMapping
    public ResponseEntity<ConversationFolder> createFolder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> body) {
        ConversationFolder folder = folderService.createFolder(
                userId, body.get("name"), body.get("icon"), body.get("color"));
        return ResponseEntity.ok(folder);
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<ConversationFolder> updateFolder(
            @PathVariable Long folderId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> body) {
        ConversationFolder folder = folderService.updateFolder(
                folderId, userId,
                (String) body.get("name"),
                (String) body.get("icon"),
                (String) body.get("color"),
                (Integer) body.get("sortOrder"),
                (Boolean) body.get("isCollapsed"));
        return ResponseEntity.ok(folder);
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long folderId,
            @RequestHeader("X-User-Id") Long userId) {
        folderService.deleteFolder(folderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{folderId}/conversations/{conversationId}")
    public ResponseEntity<Void> addConversation(
            @PathVariable Long folderId,
            @PathVariable Long conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        folderService.addConversationToFolder(folderId, conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{folderId}/conversations/{conversationId}")
    public ResponseEntity<Void> removeConversation(
            @PathVariable Long folderId,
            @PathVariable Long conversationId) {
        folderService.removeConversationFromFolder(folderId, conversationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{folderId}/conversations")
    public ResponseEntity<List<ConversationFolderMembership>> getConversations(
            @PathVariable Long folderId) {
        return ResponseEntity.ok(folderService.getFolderMembers(folderId));
    }
}
