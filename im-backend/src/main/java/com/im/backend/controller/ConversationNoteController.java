package com.im.backend.controller;

import com.im.backend.entity.*;
import com.im.backend.service.ConversationNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class ConversationNoteController {

    private final ConversationNoteService noteService;

    // ==================== 笔记 API ====================

    @PostMapping
    public ResponseEntity<ConversationNoteEntity> createNote(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long conversationId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) List<String> tags) {
        ConversationNoteEntity note = noteService.createNote(userId, conversationId, title, content, color, tags);
        return ResponseEntity.ok(note);
    }

    @GetMapping
    public ResponseEntity<Page<ConversationNoteEntity>> getNotes(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(noteService.getNotes(userId, conversationId, pageable));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ConversationNoteEntity> getNote(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long noteId) {
        return noteService.getNoteById(noteId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ConversationNoteEntity> updateNote(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long noteId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) List<String> tags) {
        return ResponseEntity.ok(noteService.updateNote(noteId, userId, title, content, color, tags));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long noteId) {
        noteService.deleteNote(noteId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<ConversationNoteEntity> pinNote(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long noteId,
            @RequestParam(defaultValue = "true") boolean pinned) {
        return ResponseEntity.ok(noteService.pinNote(noteId, userId, pinned));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ConversationNoteEntity>> searchNotes(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(noteService.searchNotes(userId, keyword, pageable));
    }

    @GetMapping("/by-tag")
    public ResponseEntity<Page<ConversationNoteEntity>> getNotesByTag(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(noteService.getNotesByTag(userId, tag, pageable));
    }

    // ==================== 标签 API ====================

    @PostMapping("/tags")
    public ResponseEntity<NoteTagEntity> createTag(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String tagName,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String icon) {
        return ResponseEntity.ok(noteService.createTag(userId, tagName, color, icon));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<NoteTagEntity>> getAllTags(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(noteService.getAllTags(userId));
    }

    @GetMapping("/tags/top")
    public ResponseEntity<List<NoteTagEntity>> getTopTags(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(noteService.getTopTags(userId, limit));
    }

    @PutMapping("/tags/{tagId}")
    public ResponseEntity<NoteTagEntity> updateTag(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long tagId,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String icon) {
        return ResponseEntity.ok(noteService.updateTag(tagId, userId, tagName, color, icon));
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long tagId) {
        noteService.deleteTag(tagId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 消息标注 API ====================

    @PostMapping("/annotations")
    public ResponseEntity<MessageAnnotationEntity> annotateMessage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long messageId,
            @RequestParam Long conversationId,
            @RequestParam(required = false) String annotationType,
            @RequestParam(defaultValue = "false") boolean starred,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String emoji) {
        return ResponseEntity.ok(noteService.annotateMessage(userId, messageId, conversationId,
                annotationType, starred, note, color, emoji));
    }

    @GetMapping("/annotations")
    public ResponseEntity<Page<MessageAnnotationEntity>> getAnnotations(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(noteService.getAnnotations(userId, conversationId, pageable));
    }

    @GetMapping("/annotations/starred")
    public ResponseEntity<Page<MessageAnnotationEntity>> getStarredMessages(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(noteService.getStarredMessages(userId, pageable));
    }

    @PatchMapping("/annotations/{annotationId}/star")
    public ResponseEntity<Void> toggleStar(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long annotationId) {
        noteService.toggleStar(annotationId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/annotations/{annotationId}")
    public ResponseEntity<Void> deleteAnnotation(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long annotationId) {
        noteService.deleteAnnotation(annotationId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 统计 API ====================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(noteService.getNoteStats(userId));
    }
}
