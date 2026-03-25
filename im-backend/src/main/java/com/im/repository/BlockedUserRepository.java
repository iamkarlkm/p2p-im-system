package com.im.repository;

import com.im.entity.BlockedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 用户黑名单仓储
 */
@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUserEntity, Long> {
    
    /**
     * 查询指定用户的黑名单列表
     */
    List<BlockedUserEntity> findByBlockerId(Long blockerId);
    
    /**
     * 检查用户是否被拉黑
     */
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    
    /**
     * 查询指定拉黑关系
     */
    Optional<BlockedUserEntity> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    
    /**
     * 获取用户的所有黑名单ID
     */
    @Query("SELECT b.blockedId FROM BlockedUserEntity b WHERE b.blockerId = :blockerId")
    List<Long> findBlockedIdsByBlockerId(@Param("blockerId") Long blockerId);
    
    /**
     * 批量检查用户是否在黑名单中
     */
    @Query("SELECT b.blockedId FROM BlockedUserEntity b WHERE b.blockerId = :blockerId AND b.blockedId IN :blockedIds")
    List<Long> findBlockedIdsInList(@Param("blockerId") Long blockerId, @Param("blockedIds") List<Long> blockedIds);
    
    /**
     * 解除拉黑
     */
    @Modifying
    @Query("DELETE FROM BlockedUserEntity b WHERE b.blockerId = :blockerId AND b.blockedId = :blockedId")
    void unblockUser(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
    
    /**
     * 获取黑名单数量
     */
    @Query("SELECT COUNT(b) FROM BlockedUserEntity b WHERE b.blockerId = :blockerId")
    long countByBlockerId(@Param("blockerId") Long blockerId);
    
    /**
     * 检查双向拉黑
     */
    @Query("SELECT COUNT(b) > 0 FROM BlockedUserEntity b WHERE b.blockerId = :userId1 AND b.blockedId = :userId2")
    boolean isBlockedByUser(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
