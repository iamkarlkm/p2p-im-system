package com.im.repository;

import com.im.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    
    Optional<VoteEntity> findByMessageId(Long messageId);
    
    List<VoteEntity> findByGroupId(Long groupId);
    
    List<VoteEntity> findByUserId(Long userId);
    
    List<VoteEntity> findByGroupIdAndIsClosed(Long groupId, Boolean isClosed);
    
    @Query("SELECT v FROM VoteEntity v WHERE v.endTime < :now AND v.isClosed = false")
    List<VoteEntity> findExpiredVotes(@Param("now") LocalDateTime now);
    
    @Query("SELECT v FROM VoteEntity v WHERE v.groupId = :groupId AND v.isClosed = false ORDER BY v.createdAt DESC")
    List<VoteEntity> findActiveVotesByGroup(@Param("groupId") Long groupId);
    
    @Modifying
    @Query("UPDATE VoteEntity v SET v.isClosed = true WHERE v.id = :id")
    void closeVote(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE VoteEntity v SET v.totalVotes = v.totalVotes + 1 WHERE v.id = :id")
    void incrementTotalVotes(@Param("id") Long id);
    
    @Query(value = "SELECT COUNT(DISTINCT vp.user_id) FROM vote_participants vp WHERE vp.vote_id = :voteId", nativeQuery = true)
    Long countUniqueParticipants(@Param("voteId") Long voteId);
    
    @Query(value = "SELECT EXISTS (SELECT 1 FROM vote_participants vp WHERE vp.vote_id = :voteId AND vp.user_id = :userId)", nativeQuery = true)
    Boolean hasUserVoted(@Param("voteId") Long voteId, @Param("userId") Long userId);
}