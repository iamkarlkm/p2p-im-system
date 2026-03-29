package com.im.repository;

import com.im.model.RecommendationScore;
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

/**
 * 推荐数据仓库
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationScore, Long> {

    /**
     * 根据用户ID查询推荐列表
     */
    List<RecommendationScore> findByUserId(Long userId);

    /**
     * 根据用户ID分页查询推荐列表
     */
    Page<RecommendationScore> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和算法类型查询
     */
    List<RecommendationScore> findByUserIdAndAlgorithmType(Long userId, String algorithmType);

    /**
     * 统计用户推荐数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户特定算法推荐数量
     */
    long countByUserIdAndAlgorithmType(Long userId, String algorithmType);

    /**
     * 删除用户的所有推荐记录
     */
    @Modifying
    @Query("DELETE FROM RecommendationScore r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除过期推荐
     */
    @Modifying
    @Query("DELETE FROM RecommendationScore r WHERE r.expiresAt < :now")
    void deleteExpiredRecommendations(@Param("now") LocalDateTime now);

    /**
     * 更新推荐反馈
     */
    @Modifying
    @Query("UPDATE RecommendationScore r SET r.isHelpful = :isHelpful WHERE r.userId = :userId AND r.targetUserId = :targetUserId")
    void updateFeedback(@Param("userId") Long userId, @Param("targetUserId") Long targetUserId, @Param("isHelpful") Boolean isHelpful);

    /**
     * 查询特定推荐记录
     */
    Optional<RecommendationScore> findByUserIdAndTargetUserId(Long userId, Long targetUserId);

    /**
     * 查询高分推荐
     */
    @Query("SELECT r FROM RecommendationScore r WHERE r.userId = :userId AND r.score >= :minScore ORDER BY r.score DESC")
    List<RecommendationScore> findHighScoreRecommendations(@Param("userId") Long userId, @Param("minScore") Double minScore);
}
