package com.im.backend.repository;

import com.im.backend.entity.ConversationNoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationNoteRepository extends JpaRepository<ConversationNoteEntity, Long> {

    // === 基本查询 ===

    Optional<ConversationNoteEntity> findByIdAndUserId(Long id, Long userId);

    Page<ConversationNoteEntity> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    Page<ConversationNoteEntity> findByUserIdAndConversationIdAndDeletedFalse(Long userId, Long conversationId, Pageable pageable);

    List<ConversationNoteEntity> findByConversationIdAndDeletedFalse(Long conversationId);

    // === 置顶相关 ===

    List<ConversationNoteEntity> findByUserIdAndPinnedTrueAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    Page<ConversationNoteEntity> findByUserIdAndDeletedFalseOrderByPinnedDescCreatedAtDesc(Long userId, Pageable pageable);

    // === 搜索 ===

    Page<ConversationNoteEntity> findByUserIdAndTitleContainingIgnoreCaseAndDeletedFalse(Long userId, String keyword, Pageable pageable);

    Page<ConversationNoteEntity> findByUserIdAndContentContainingIgnoreCaseAndDeletedFalse(Long userId, String keyword, Pageable pageable);

    @Query("SELECT n FROM ConversationNoteEntity n WHERE n.userId = :userId AND n.deleted = false " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ConversationNoteEntity> searchNotes(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // === 标签相关 ===

    @Query("SELECT n FROM ConversationNoteEntity n WHERE n.userId = :userId AND n.deleted = false " +
           "AND n.tags LIKE CONCAT('%', :tag, '%')")
    Page<ConversationNoteEntity> findByUserIdAndTag(@Param("userId") Long userId, @Param("tag") String tag, Pageable pageable);

    List<ConversationNoteEntity> findByUserIdAndSourceMessageIdAndDeletedFalse(Long userId, Long messageId);

    // === 统计 ===

    long countByUserIdAndDeletedFalse(Long userId);

    long countByConversationIdAndDeletedFalse(Long conversationId);

    @Query("SELECT COUNT(n) FROM ConversationNoteEntity n WHERE n.userId = :userId AND n.pinned = true AND n.deleted = false")
    long countPinnedByUserId(@Param("userId") Long userId);

    @Query("SELECT n.conversationId, COUNT(n) FROM ConversationNoteEntity n " +
           "WHERE n.userId = :userId AND n.deleted = false GROUP BY n.conversationId")
    List<Object[]> countNotesGroupByConversation(@Param("userId") Long userId);

    // === 删除与清理 ===

    void deleteByIdAndUserId(Long id, Long userId);

    @Query("UPDATE ConversationNoteEntity n SET n.deleted = true, n.deletedAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.userId = :userId")
    void softDeleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("UPDATE ConversationNoteEntity n SET n.pinned = :pinned WHERE n.id = :id AND n.userId = :userId")
    void updatePinned(@Param("id") Long id, @Param("userId") Long userId, @Param("pinned") Boolean pinned);

    @Query("UPDATE ConversationNoteEntity n SET n.tags = :tags, n.updatedAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.userId = :userId")
    void updateTags(@Param("id") Long id, @Param("userId") Long userId, @Param("tags") String tags);
}
