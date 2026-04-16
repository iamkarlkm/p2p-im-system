package com.im.service.group.repository;

import com.im.service.group.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    Optional<Group> findByIdAndDissolvedFalse(String id);

    List<Group> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    Page<Group> findByTypeAndDissolvedFalseOrderByCreatedAtDesc(String type, Pageable pageable);

    Page<Group> findByNameContainingAndDissolvedFalseOrderByCreatedAtDesc(String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Group g SET g.memberCount = g.memberCount + :delta, g.updatedAt = :now WHERE g.id = :id")
    int updateMemberCount(@Param("id") String id, @Param("delta") int delta, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Group g SET g.dissolved = true, g.dissolvedAt = :now, g.updatedAt = :now WHERE g.id = :id")
    int dissolveGroup(@Param("id") String id, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Group g SET g.ownerId = :newOwnerId, g.updatedAt = :now WHERE g.id = :id")
    int updateOwner(@Param("id") String id, @Param("newOwnerId") String newOwnerId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Group g SET g.ownerId = :newOwnerId, g.updatedAt = :now WHERE g.id = :id")
    int transferOwnership(@Param("id") String id, @Param("newOwnerId") String newOwnerId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Group g SET g.updatedAt = :now WHERE g.id = :id")
    int unmuteAll(@Param("id") String id, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Group g SET g.announcement = :announcement, g.updatedAt = :now WHERE g.id = :id")
    int updateAnnouncement(@Param("id") String id, @Param("announcement") String announcement, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Group g SET g.muteAll = true, g.muteAllUntil = :until, g.updatedAt = :now WHERE g.id = :id")
    int muteAll(@Param("id") String id, @Param("until") LocalDateTime until, @Param("now") LocalDateTime now);
}
