package com.im.backend.repository;

import com.im.backend.model.TranslationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 翻译缓存数据访问层
 */
@Repository
public interface TranslationCacheRepository extends JpaRepository<TranslationCache, String> {

    /**
     * 根据缓存键查找
     */
    Optional<TranslationCache> findByCacheKey(String cacheKey);

    /**
     * 根据源语言和目标语言查找
     */
    List<TranslationCache> findBySourceLanguageAndTargetLanguage(
        String sourceLanguage, 
        String targetLanguage
    );

    /**
     * 删除过期的缓存
     */
    void deleteByExpiresAtBefore(LocalDateTime time);

    /**
     * 查找即将过期的缓存
     */
    List<TranslationCache> findByExpiresAtBefore(LocalDateTime time);

    /**
     * 根据原始文本查找
     */
    @Query("SELECT t FROM TranslationCache t WHERE t.originalText = :text " +
           "AND t.sourceLanguage = :sourceLang AND t.targetLanguage = :targetLang")
    Optional<TranslationCache> findByOriginalTextAndLanguages(
        @Param("text") String text,
        @Param("sourceLang") String sourceLanguage,
        @Param("targetLang") String targetLanguage
    );

    /**
     * 获取最热门的缓存条目
     */
    @Query("SELECT t FROM TranslationCache t ORDER BY t.hitCount DESC")
    List<TranslationCache> findTopByHitCount(org.springframework.data.domain.Pageable pageable);

    /**
     * 统计缓存总数
     */
    long count();

    /**
     * 统计特定语言对的缓存数量
     */
    long countBySourceLanguageAndTargetLanguage(String sourceLanguage, String targetLanguage);
}
