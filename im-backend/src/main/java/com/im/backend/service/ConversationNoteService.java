package com.im.backend.service;

import com.im.backend.entity.*;
import com.im.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationNoteService {

    private final ConversationNoteRepository noteRepository;
    private final NoteTagRepository tagRepository;
    private final MessageAnnotationRepository annotationRepository;

    // ==================== 笔记 (Note) CRUD ====================

    @Transactional
    public ConversationNoteEntity createNote(Long userId, Long conversationId, String title,
            String content, String color, List<String> tagList) {
        ConversationNoteEntity note = ConversationNoteEntity.builder()
                .userId(userId)
                .conversationId(conversationId)
                .title(title)
                .content(content)
                .color(color != null ? color : "#FFF9C4")
                .tags(tagList != null ? String.join(",", tagList) : null)
                .build();
        ConversationNoteEntity saved = noteRepository.save(note);

        // 更新标签使用次数
        if (tagList != null) {
            for (String tagName : tagList) {
                tagRepository.findByUserIdAndTagName(userId, tagName)
                        .ifPresent(tag -> tagRepository.incrementUsageCount(tag.getId()));
            }
        }
        return saved;
    }

    public Page<ConversationNoteEntity> getNotes(Long userId, Long conversationId, Pageable pageable) {
        if (conversationId != null) {
            return noteRepository.findByUserIdAndConversationIdAndDeletedFalse(userId, conversationId, pageable);
        }
        return noteRepository.findByUserIdAndDeletedFalseOrderByPinnedDescCreatedAtDesc(userId, pageable);
    }

    public Optional<ConversationNoteEntity> getNoteById(Long noteId, Long userId) {
        return noteRepository.findByIdAndUserId(noteId, userId);
    }

    @Transactional
    public ConversationNoteEntity updateNote(Long noteId, Long userId, String title,
            String content, String color, List<String> tagList) {
        return noteRepository.findByIdAndUserId(noteId, userId).map(note -> {
            if (title != null) note.setTitle(title);
            if (content != null) note.setContent(content);
            if (color != null) note.setColor(color);
            if (tagList != null) {
                note.setTags(String.join(",", tagList));
            }
            return noteRepository.save(note);
        }).orElseThrow(() -> new RuntimeException("Note not found or access denied"));
    }

    @Transactional
    public void deleteNote(Long noteId, Long userId) {
        noteRepository.softDeleteByIdAndUserId(noteId, userId);
    }

    @Transactional
    public ConversationNoteEntity pinNote(Long noteId, Long userId, boolean pinned) {
        noteRepository.updatePinned(noteId, userId, pinned);
        return noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
    }

    public Page<ConversationNoteEntity> searchNotes(Long userId, String keyword, Pageable pageable) {
        return noteRepository.searchNotes(userId, keyword, pageable);
    }

    public Page<ConversationNoteEntity> getNotesByTag(Long userId, String tag, Pageable pageable) {
        return noteRepository.findByUserIdAndTag(userId, tag, pageable);
    }

    // ==================== 标签 (Tag) CRUD ====================

    @Transactional
    public NoteTagEntity createTag(Long userId, String tagName, String color, String icon) {
        if (tagRepository.existsByUserIdAndTagName(userId, tagName)) {
            throw new RuntimeException("Tag already exists");
        }
        NoteTagEntity tag = NoteTagEntity.builder()
                .userId(userId)
                .tagName(tagName)
                .color(color != null ? color : "#90CAF9")
                .icon(icon != null ? icon : "🏷️")
                .build();
        return tagRepository.save(tag);
    }

    public List<NoteTagEntity> getAllTags(Long userId) {
        return tagRepository.findByUserIdOrderBySortOrderAsc(userId);
    }

    public List<NoteTagEntity> getTopTags(Long userId, int limit) {
        return tagRepository.findTopByUserIdOrderByUsageCountDesc(userId, limit);
    }

    @Transactional
    public NoteTagEntity updateTag(Long tagId, Long userId, String tagName, String color, String icon) {
        return tagRepository.findByIdAndUserId(tagId, userId).map(tag -> {
            if (tagName != null) tag.setTagName(tagName);
            if (color != null) tag.setColor(color);
            if (icon != null) tag.setIcon(icon);
            return tagRepository.save(tag);
        }).orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    @Transactional
    public void deleteTag(Long tagId, Long userId) {
        tagRepository.deleteByIdAndUserId(tagId, userId);
    }

    // ==================== 消息标注 (Message Annotation) ====================

    @Transactional
    public MessageAnnotationEntity annotateMessage(Long userId, Long messageId, Long conversationId,
            String annotationType, boolean starred, String note, String color, String emoji) {
        // 检查是否已存在标注
        Optional<MessageAnnotationEntity> existing = annotationRepository.findByUserIdAndMessageId(userId, messageId);
        if (existing.isPresent()) {
            MessageAnnotationEntity annot = existing.get();
            if (annotationType != null) annot.setAnnotationType(annotationType);
            if (starred) annot.setStarred(true);
            if (note != null) annot.setNote(note);
            if (color != null) annot.setColor(color);
            if (emoji != null) annot.setEmoji(emoji);
            annot.setRead(true);
            return annotationRepository.save(annot);
        }

        MessageAnnotationEntity annotation = MessageAnnotationEntity.builder()
                .userId(userId)
                .messageId(messageId)
                .conversationId(conversationId)
                .annotationType(annotationType != null ? annotationType : "BOOKMARK")
                .starred(starred)
                .note(note)
                .color(color != null ? color : "#FFECB3")
                .emoji(emoji != null ? emoji : "⭐")
                .build();
        return annotationRepository.save(annotation);
    }

    public Page<MessageAnnotationEntity> getAnnotations(Long userId, Long conversationId, Pageable pageable) {
        if (conversationId != null) {
            List<MessageAnnotationEntity> list = annotationRepository
                    .findByUserIdAndConversationIdAndDeletedFalse(userId, conversationId);
            return new PageImpl<>(list, pageable, list.size());
        }
        return annotationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<MessageAnnotationEntity> getStarredMessages(Long userId, Pageable pageable) {
        return annotationRepository.findByUserIdAndStarredTrueAndDeletedFalseOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public void toggleStar(Long annotationId, Long userId) {
        annotationRepository.findByIdAndUserId(annotationId, userId).ifPresent(annot -> {
            annot.setStarred(!annot.getStarred());
            annotationRepository.save(annot);
        });
    }

    @Transactional
    public void deleteAnnotation(Long annotationId, Long userId) {
        annotationRepository.deleteByIdAndUserId(annotationId, userId);
    }

    // ==================== 统计 ====================

    public Map<String, Object> getNoteStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNotes", noteRepository.countByUserIdAndDeletedFalse(userId));
        stats.put("pinnedNotes", noteRepository.countPinnedByUserId(userId));
        stats.put("totalTags", tagRepository.countByUserId(userId));
        stats.put("totalAnnotations", annotationRepository.countByUserIdAndDeletedFalse(userId));
        stats.put("starredMessages", annotationRepository.countByUserIdAndStarredTrueAndDeletedFalse(userId));
        stats.put("notesByConversation", noteRepository.countNotesGroupByConversation(userId));
        stats.put("annotationsByType", annotationRepository.countByAnnotationType(userId));
        return stats;
    }
}
