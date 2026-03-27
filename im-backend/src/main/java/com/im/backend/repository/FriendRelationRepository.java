package com.im.backend.repository;

import com.im.backend.model.FriendRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {

    List<FriendRelation> findAllByUserId(Long userId);

    List<FriendRelation> findAllByUserIdAndStarredTrue(Long userId);

    List<FriendRelation> findAllByUserIdAndMutedTrue(Long userId);

    @Query("SELECT f FROM FriendRelation f WHERE f.userId = :userId AND f.friendId IN :friendIds")
    List<FriendRelation> findAllByUserIdAndFriendIdIn(@Param("userId") Long userId, @Param("friendIds") List<Long> friendIds);

    Optional<FriendRelation> findByUserIdAndFriendId(Long userId, Long friendId);

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    long countByUserId(Long userId);

    long countByUserIdAndStarredTrue(Long userId);

    long countByUserIdAndMutedTrue(Long userId);

    @Modifying
    @Query("UPDATE FriendRelation f SET f.starred = :starred WHERE f.userId = :userId AND f.friendId = :friendId")
    void updateStarred(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("starred") Boolean starred);

    @Modifying
    @Query("UPDATE FriendRelation f SET f.muted = :muted WHERE f.userId = :userId AND f.friendId = :friendId")
    void updateMuted(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("muted") Boolean muted);
}
