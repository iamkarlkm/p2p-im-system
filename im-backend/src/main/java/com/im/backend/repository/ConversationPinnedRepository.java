package com.im.backend.repository;

import com.im.backend.entity.ConversationPinned;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationPinnedRepository extends JpaRepository<ConversationPinned, Long> {
    List<ConversationPinned> findByUserIdOrderBySortOrderAsc(Long userId);
    
    Optional<ConversationPinned> findByUserIdAndConversationId(Long userId, Long conversationId);
    
    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);
    
    @Query("SELECT COALESCE(MAX(cp.sortOrder), 0) FROM ConversationPinned cp WHERE cp.userId = :userId")
    Integer findMaxSortOrder(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE ConversationPinned cp SET cp.sortOrder = cp.sortOrder - 1 WHERE cp.userId = :userId AND cp.sortOrder > :order")
    void decrementOrderAbove(@Param("userId") Long userId, @Param("order") Integer order);
    
    void deleteByUserIdAndConversationId(Long userId, Long conversationId);
}
