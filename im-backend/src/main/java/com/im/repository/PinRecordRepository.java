package com.im.repository;

import com.im.entity.PinRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 置顶记录仓储层
 */
@Repository
public interface PinRecordRepository extends JpaRepository<PinRecordEntity, Long> {

    List<PinRecordEntity> findByUserIdAndPinTypeOrderBySortOrderDesc(String userId, String pinType);

    List<PinRecordEntity> findByConversationIdAndUserIdAndPinType(String conversationId, String userId, String pinType);

    Optional<PinRecordEntity> findByConversationIdAndMessageIdAndUserIdAndPinType(
            String conversationId, String messageId, String userId, String pinType);

    boolean existsByConversationIdAndUserIdAndPinType(String conversationId, String userId, String pinType);

    @Modifying
    @Query("DELETE FROM PinRecordEntity p WHERE p.conversationId = :conversationId AND p.userId = :userId AND p.pinType = :pinType")
    int deleteByConversationPin(@Param("conversationId") String conversationId,
                                 @Param("userId") String userId,
                                 @Param("pinType") String pinType);

    @Query("SELECT MAX(p.sortOrder) FROM PinRecordEntity p WHERE p.userId = :userId AND p.pinType = :pinType")
    Long findMaxSortOrder(@Param("userId") String userId, @Param("pinType") String pinType);
}
