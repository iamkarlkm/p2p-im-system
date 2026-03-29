package com.im.system.repository;

import com.im.system.entity.SensitiveWordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 敏感词仓储接口
 */
@Repository
public interface SensitiveWordRepository extends JpaRepository<SensitiveWordEntity, Long> {
    
    // 基础查询
    Optional<SensitiveWordEntity> findByWord(String word);
    List<SensitiveWordEntity> findByEnabled(Boolean enabled);
    List<SensitiveWordEntity> findByCategory(String category);
    List<SensitiveWordEntity> findByLevel(String level);
    
    // 分页查询
    Page<SensitiveWordEntity> findByEnabled(Boolean enabled, Pageable pageable);
    Page<SensitiveWordEntity> findByCategory(String category, Pageable pageable);
    Page<SensitiveWordEntity> findByLevel(String level, Pageable pageable);
    
    // 复合查询
    List<SensitiveWordEntity> findByCategoryAndEnabled(String category, Boolean enabled);
    List<SensitiveWordEntity> findByLevelAndEnabled(String level, Boolean enabled);
    Page<SensitiveWordEntity> findByCategoryAndEnabled(String category, Boolean enabled, Pageable pageable);
    
    // 模糊搜索
    Page<SensitiveWordEntity> findByWordContaining(String keyword, Pageable pageable);
    Page<SensitiveWordEntity> findByWordContainingAndEnabled(String keyword, Boolean enabled, Pageable pageable);
    
    // 统计
    Long countByEnabled(Boolean enabled);
    Long countByCategory(String category);
    Long countByLevel(String level);
    Long countByCategoryAndEnabled(String category, Boolean enabled);
    
    // 批量操作
    @Modifying
    @Query("UPDATE SensitiveWordEntity s SET s.enabled = :enabled WHERE s.id IN :ids")
    int batchUpdateEnabled(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled);
    
    @Modifying
    @Query("UPDATE SensitiveWordEntity s SET s.category = :category WHERE s.id IN :ids")
    int batchUpdateCategory(@Param("ids") List<Long> ids, @Param("category") String category);
    
    @Modifying
    @Query("DELETE FROM SensitiveWordEntity s WHERE s.id IN :ids")
    int batchDelete(@Param("ids") List<Long> ids);
    
    @Modifying
    @Query("UPDATE SensitiveWordEntity s SET s.matchCount = s.matchCount + 1 WHERE s.word = :word")
    int incrementMatchCount(@Param("word") String word);
    
    // 获取所有启用的敏感词
    @Query("SELECT s FROM SensitiveWordEntity s WHERE s.enabled = true ORDER BY s.level DESC, s.word ASC")
    List<SensitiveWordEntity> findAllEnabled();
    
    // 按匹配次数排序
    Page<SensitiveWordEntity> findAllByOrderByMatchCountDesc(Pageable pageable);
    Page<SensitiveWordEntity> findByEnabledOrderByMatchCountDesc(Boolean enabled, Pageable pageable);
    
    // 按创建时间排序
    Page<SensitiveWordEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 统计各分类数量
    @Query("SELECT s.category, COUNT(s) FROM SensitiveWordEntity s GROUP BY s.category")
    List<Object[]> countByCategory();
    
    // 统计各等级数量
    @Query("SELECT s.level, COUNT(s) FROM SensitiveWordEntity s GROUP BY s.level")
    List<Object[]> countByLevel();
    
    // 获取高匹配次数敏感词
    List<SensitiveWordEntity> findByMatchCountGreaterThanEqual(Integer minCount);
    
    // 清理（软删除）
    @Modifying
    @Query("DELETE FROM SensitiveWordEntity s WHERE s.enabled = false AND s.updatedAt < :cutoffDate")
    int deleteDisabledOld(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}