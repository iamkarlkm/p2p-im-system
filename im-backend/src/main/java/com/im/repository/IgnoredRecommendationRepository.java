package com.im.repository;

import com.im.model.IgnoredRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 忽略推荐仓库
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Repository
public interface IgnoredRecommendationRepository extends JpaRepository<IgnoredRecommendation, Long> {

    /**
     * 根据用户ID查询忽略列表
     */
    List<IgnoredRecommendation> findByUserId(Long userId);

    /**
     * 统计用户忽略数量
     */
    long countByUserId(Long userId);

    /**
     * 查询特定忽略记录
     */
    Optional<IgnoredRecommendation> findByUserIdAndTargetUserId(Long userId, Long targetUserId);

    /**
     * 检查是否已忽略
     */
    boolean existsByUserIdAndTargetUserId(Long userId, Long targetUserId);

    /**
     * 删除忽略记录
     */
    @Modifying
    @Query("DELETE FROM IgnoredRecommendation i WHERE i.userId = :userId AND i.targetUserId = :targetUserId")
    void deleteByUserIdAndTargetUserId(@Param("userId") Long userId, @Param("targetUserId") Long targetUserId);

    /**
     * 删除用户的所有忽略记录
     */
    @Modifying
    @Query("DELETE FROM IgnoredRecommendation i WHERE i.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除过期忽略记录（超过30天）
     */
    @Modifying
    @Query("DELETE FROM IgnoredRecommendation i WHERE i.ignoredAt < :expireTime")
    void deleteExpiredIgnores(@Param("expireTime") LocalDateTime expireTime);
}
