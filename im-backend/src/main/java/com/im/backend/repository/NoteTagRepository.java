package com.im.backend.repository;

import com.im.backend.entity.NoteTagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteTagRepository extends JpaRepository<NoteTagEntity, Long> {

    Optional<NoteTagEntity> findByIdAndUserId(Long id, Long userId);

    Optional<NoteTagEntity> findByUserIdAndTagName(Long userId, String tagName);

    List<NoteTagEntity> findByUserIdOrderBySortOrderAsc(Long userId);

    Page<NoteTagEntity> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndTagName(Long userId, String tagName);

    long countByUserId(Long userId);

    @Modifying
    @Query("UPDATE NoteTagEntity t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsageCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE NoteTagEntity t SET t.usageCount = t.usageCount - 1 WHERE t.id = :id AND t.usageCount > 0")
    void decrementUsageCount(@Param("id") Long id);

    @Query("SELECT t FROM NoteTagEntity t WHERE t.userId = :userId ORDER BY t.usageCount DESC LIMIT :limit")
    List<NoteTagEntity> findTopByUserIdOrderByUsageCountDesc(@Param("userId") Long userId, @Param("limit") int limit);

    void deleteByIdAndUserId(Long id, Long userId);

    @Query("SELECT t.tagName FROM NoteTagEntity t WHERE t.userId = :userId")
    List<String> findAllTagNamesByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM NoteTagEntity t WHERE t.userId = :userId AND LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NoteTagEntity> searchTags(@Param("userId") Long userId, @Param("keyword") String keyword);
}
