package com.im.backend.repository;

import com.im.backend.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索历史数据访问层
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    /**
     * 获取用户的搜索历史（按时间倒序）
     */
    List<SearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 获取用户的搜索历史（去重，只取最新）
     */
    @Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId " +
           "AND sh.createdAt = (SELECT MAX(sh2.createdAt) FROM SearchHistory sh2 " +
           "WHERE sh2.userId = sh.userId AND sh2.keyword = sh.keyword) " +
           "ORDER BY sh.createdAt DESC")
    List<SearchHistory> findDistinctByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 获取热门搜索关键词
     */
    @Query("SELECT sh.keyword, COUNT(sh) as count FROM SearchHistory sh " +
           "WHERE sh.createdAt >= :since GROUP BY sh.keyword ORDER BY count DESC")
    List<Object[]> findHotKeywords(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * 删除用户的单条搜索历史
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SearchHistory sh WHERE sh.userId = :userId AND sh.keyword = :keyword")
    void deleteByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 清空用户搜索历史
     */
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    /**
     * 删除旧的历史记录
     */
    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime before);

    /**
     * 检查用户是否搜索过该关键词
     */
    boolean existsByUserIdAndKeyword(Long userId, String keyword);
}
