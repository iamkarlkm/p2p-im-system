package com.im.backend.repository;

import com.im.backend.entity.MessageAnnotationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageAnnotationRepository extends JpaRepository<MessageAnnotationEntity, Long> {

    Optional<MessageAnnotationEntity> findByIdAndUserId(Long id, Long userId);

    Optional<MessageAnnotationEntity> findByUserIdAndMessageId(Long userId, Long messageId);

    Page<MessageAnnotationEntity> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<MessageAnnotationEntity> findByUserIdAndConversationIdAndDeletedFalse(Long userId, Long conversationId);

    Page<MessageAnnotationEntity> findByUserIdAndStarredTrueAndDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<MessageAnnotationEntity> findByUserIdAndAnnotationTypeAndDeletedFalse(Long userId, String annotationType, Pageable pageable);

    // === 统计 ===

    long countByUserIdAndDeletedFalse(Long userId);

    long countByConversationIdAndDeletedFalse(Long conversationId);

    long countByUserIdAndStarredTrueAndDeletedFalse(Long userId);

    @Query("SELECT m.annotationType, COUNT(m) FROM MessageAnnotationEntity m " +
           "WHERE m.userId = :userId AND m.deleted = false GROUP BY m.annotationType")
    List<Object[]> countByAnnotationType(@Param("userId") Long userId);

    @Query("SELECT m.conversationId, COUNT(m) FROM MessageAnnotationEntity m " +
           "WHERE m.userId = :userId AND m.starred = true AND m.deleted = false GROUP BY m.conversationId")
    List<Object[]> countStarredGroupByConversation(@Param("userId") Long userId);

    // === 删除与更新 ===

    @Query("UPDATE MessageAnnotationEntity m SET m.starred = :starred WHERE m.id = :id AND m.userId = :userId")
    void updateStarred(@Param("id") Long id, @Param("userId") Long userId, @Param("starred") Boolean starred);

    @Query("UPDATE MessageAnnotationEntity m SET m.note = :note, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :id AND m.userId = :userId")
    void updateNote(@Param("id") Long id, @Param("userId") Long userId, @Param("note") String note);

    void deleteByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(m) > 0 FROM MessageAnnotationEntity m WHERE m.userId = :userId AND m.messageId = :messageId AND m.starred = true AND m.deleted = false")
    boolean isMessageStarred(@Param("userId") Long userId, @Param("messageId") Long messageId);

    @Query("SELECT m FROM MessageAnnotationEntity m WHERE m.userId = :userId AND m.linkedNoteId = :noteId AND m.deleted = false")
    List<MessageAnnotationEntity> findByLinkedNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);
}
