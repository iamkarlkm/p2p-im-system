package com.im.ai.service;

import com.im.ai.model.*;
import com.im.ai.repository.KnowledgeBaseRepository;
import com.im.nlp.client.NlpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识库服务
 * 管理知识条目,提供智能检索和问答能力
 */
@Slf4j
@Service
public class KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseRepository repository;
    
    @Autowired
    private NlpClient nlpClient;
    
    @Autowired
    private EmbeddingService embeddingService;

    // 缓存
    private final Map<String, KnowledgeEntry> entryCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> categoryIndex = new ConcurrentHashMap<>();

    /**
     * 搜索知识条目
     */
    public List<KnowledgeEntry> search(List<String> keywords, int limit) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }

        // 步骤1: 精确匹配
        List<KnowledgeEntry> exactMatches = searchExactMatches(keywords, limit);
        
        // 步骤2: 语义搜索
        List<KnowledgeEntry> semanticMatches = searchSemantic(keywords, limit);
        
        // 步骤3: 合并去重并排序
        Set<String> seenIds = new HashSet<>();
        List<KnowledgeEntry> results = new ArrayList<>();
        
        for (KnowledgeEntry entry : exactMatches) {
            if (seenIds.add(entry.getId())) {
                results.add(entry);
            }
        }
        
        for (KnowledgeEntry entry : semanticMatches) {
            if (seenIds.add(entry.getId()) && results.size() < limit) {
                results.add(entry);
            }
        }
        
        // 按相关度排序
        results.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
        
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 基于问题的智能搜索
     */
    public List<KnowledgeEntry> searchByQuestion(String question, int limit) {
        // 提取关键词
        List<String> keywords = extractKeywords(question);
        
        // 扩展同义词
        List<String> expandedKeywords = expandKeywords(keywords);
        
        return search(expandedKeywords, limit);
    }

    /**
     * 精确匹配搜索
     */
    private List<KnowledgeEntry> searchExactMatches(List<String> keywords, int limit) {
        List<KnowledgeEntry> results = new ArrayList<>();
        
        for (String keyword : keywords) {
            // 标题匹配
            results.addAll(repository.findByTitleContaining(keyword));
            
            // 内容匹配
            results.addAll(repository.findByContentContaining(keyword));
            
            // 标签匹配
            results.addAll(repository.findByTagsContaining(keyword));
        }
        
        // 计算相关度分数
        results.forEach(entry -> {
            double score = calculateRelevanceScore(entry, keywords);
            entry.setRelevanceScore(score);
        });
        
        return results.stream()
            .distinct()
            .sorted((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 语义搜索(基于向量相似度)
     */
    private List<KnowledgeEntry> searchSemantic(List<String> keywords, int limit) {
        try {
            // 生成查询向量
            String queryText = String.join(" ", keywords);
            float[] queryVector = embeddingService.generateEmbedding(queryText);
            
            // 向量相似度搜索
            return repository.findByVectorSimilarity(queryVector, limit);
            
        } catch (Exception e) {
            log.warn("语义搜索失败,回退到关键词搜索", e);
            return Collections.emptyList();
        }
    }

    /**
     * 按分类搜索
     */
    public List<KnowledgeEntry> searchByCategory(String category, int limit) {
        return repository.findByCategoryAndEnabledTrueOrderByPriorityDesc(category)
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 获取热门知识条目
     */
    public List<KnowledgeEntry> getPopularEntries(int limit) {
        return repository.findTopEntriesByViewCount(limit);
    }

    /**
     * 创建知识条目
     */
    public KnowledgeEntry createEntry(KnowledgeEntry entry) {
        entry.setId(UUID.randomUUID().toString());
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        entry.setViewCount(0);
        entry.setHelpfulCount(0);
        entry.setEnabled(true);
        
        // 生成向量嵌入
        try {
            String text = entry.getTitle() + " " + entry.getContent();
            float[] embedding = embeddingService.generateEmbedding(text);
            entry.setEmbeddingVector(embedding);
        } catch (Exception e) {
            log.warn("生成知识条目向量失败: {}", entry.getId(), e);
        }
        
        KnowledgeEntry saved = repository.save(entry);
        
        // 更新缓存和索引
        entryCache.put(saved.getId(), saved);
        updateCategoryIndex(saved);
        
        log.info("创建知识条目: {}, 标题: {}", saved.getId(), saved.getTitle());
        
        return saved;
    }

    /**
     * 更新知识条目
     */
    public KnowledgeEntry updateEntry(String entryId, KnowledgeEntry update) {
        KnowledgeEntry existing = repository.findById(entryId)
            .orElseThrow(() -> new KnowledgeNotFoundException("知识条目不存在: " + entryId));
        
        // 更新字段
        if (update.getTitle() != null) {
            existing.setTitle(update.getTitle());
        }
        if (update.getContent() != null) {
            existing.setContent(update.getContent());
        }
        if (update.getCategory() != null) {
            existing.setCategory(update.getCategory());
        }
        if (update.getTags() != null) {
            existing.setTags(update.getTags());
        }
        if (update.getPriority() != null) {
            existing.setPriority(update.getPriority());
        }
        if (update.getRelatedEntries() != null) {
            existing.setRelatedEntries(update.getRelatedEntries());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        
        // 重新生成向量
        try {
            String text = existing.getTitle() + " " + existing.getContent();
            float[] embedding = embeddingService.generateEmbedding(text);
            existing.setEmbeddingVector(embedding);
        } catch (Exception e) {
            log.warn("更新知识条目向量失败: {}", entryId, e);
        }
        
        KnowledgeEntry saved = repository.save(existing);
        entryCache.put(saved.getId(), saved);
        updateCategoryIndex(saved);
        
        log.info("更新知识条目: {}", entryId);
        
        return saved;
    }

    /**
     * 删除知识条目
     */
    public void deleteEntry(String entryId) {
        repository.deleteById(entryId);
        entryCache.remove(entryId);
        
        // 从分类索引中移除
        categoryIndex.values().forEach(list -> list.remove(entryId));
        
        log.info("删除知识条目: {}", entryId);
    }

    /**
     * 获取知识条目详情
     */
    public KnowledgeEntry getEntry(String entryId) {
        // 先查缓存
        KnowledgeEntry cached = entryCache.get(entryId);
        if (cached != null) {
            incrementViewCount(entryId);
            return cached;
        }
        
        // 查数据库
        KnowledgeEntry entry = repository.findById(entryId)
            .orElseThrow(() -> new KnowledgeNotFoundException("知识条目不存在: " + entryId));
        
        // 更新缓存
        entryCache.put(entryId, entry);
        incrementViewCount(entryId);
        
        return entry;
    }

    /**
     * 反馈条目是否有帮助
     */
    public void feedbackHelpful(String entryId, boolean isHelpful) {
        if (isHelpful) {
            repository.incrementHelpfulCount(entryId);
        } else {
            repository.incrementNotHelpfulCount(entryId);
        }
        log.debug("知识条目反馈: {}, 有帮助: {}", entryId, isHelpful);
    }

    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        return repository.findDistinctCategories();
    }

    /**
     * 获取分类统计
     */
    public Map<String, Long> getCategoryStats() {
        return repository.countByCategory();
    }

    /**
     * 批量导入知识条目
     */
    public List<KnowledgeEntry> batchImport(List<KnowledgeEntryImport> imports) {
        List<KnowledgeEntry> entries = imports.stream()
            .map(this::convertImportToEntry)
            .collect(Collectors.toList());
        
        List<KnowledgeEntry> saved = repository.saveAll(entries);
        
        // 更新缓存
        saved.forEach(entry -> {
            entryCache.put(entry.getId(), entry);
            updateCategoryIndex(entry);
        });
        
        log.info("批量导入知识条目: {} 条", saved.size());
        
        return saved;
    }

    /**
     * 搜索FAQ
     */
    public List<KnowledgeEntry> searchFaq(String question, int limit) {
        return repository.findByEntryTypeAndEnabledTrueOrderByPriorityDesc(EntryType.FAQ)
            .stream()
            .filter(entry -> isFaqMatch(entry, question))
            .sorted((a, b) -> Double.compare(
                calculateFaqRelevance(b, question),
                calculateFaqRelevance(a, question)
            ))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 获取相关问题
     */
    public List<KnowledgeEntry> getRelatedEntries(String entryId, int limit) {
        KnowledgeEntry entry = getEntry(entryId);
        
        if (entry.getRelatedEntries() != null && !entry.getRelatedEntries().isEmpty()) {
            return entry.getRelatedEntries().stream()
                .limit(limit)
                .map(this::getEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
        
        // 基于相似度找相关条目
        List<String> keywords = extractKeywords(entry.getTitle() + " " + entry.getContent());
        return search(keywords, limit).stream()
            .filter(e -> !e.getId().equals(entryId))
            .collect(Collectors.toList());
    }

    // ============ 辅助方法 ============

    private double calculateRelevanceScore(KnowledgeEntry entry, List<String> keywords) {
        double score = 0.0;
        String titleLower = entry.getTitle().toLowerCase();
        String contentLower = entry.getContent().toLowerCase();
        
        for (String keyword : keywords) {
            String kw = keyword.toLowerCase();
            
            // 标题匹配权重高
            if (titleLower.contains(kw)) {
                score += 0.4;
                if (titleLower.startsWith(kw)) {
                    score += 0.2;
                }
            }
            
            // 内容匹配
            if (contentLower.contains(kw)) {
                score += 0.2;
            }
            
            // 标签匹配
            if (entry.getTags() != null) {
                for (String tag : entry.getTags()) {
                    if (tag.toLowerCase().contains(kw)) {
                        score += 0.3;
                    }
                }
            }
        }
        
        // 考虑优先级和热门度
        score += entry.getPriority() * 0.05;
        score += Math.min(entry.getViewCount() / 1000.0, 0.1);
        score += Math.min(entry.getHelpfulCount() / 100.0, 0.1);
        
        return Math.min(score, 1.0);
    }

    private double calculateFaqRelevance(KnowledgeEntry entry, String question) {
        List<String> questionKeywords = extractKeywords(question);
        List<String> faqKeywords = extractKeywords(entry.getTitle());
        
        long matches = questionKeywords.stream()
            .filter(faqKeywords::contains)
            .count();
        
        return (double) matches / Math.max(questionKeywords.size(), 1);
    }

    private boolean isFaqMatch(KnowledgeEntry entry, String question) {
        return calculateFaqRelevance(entry, question) > 0.3;
    }

    private List<String> extractKeywords(String text) {
        try {
            return nlpClient.extractKeywords(text, 10);
        } catch (Exception e) {
            // 简单分词回退
            return Arrays.asList(text.split("\\s+"));
        }
    }

    private List<String> expandKeywords(List<String> keywords) {
        Set<String> expanded = new HashSet<>(keywords);
        
        for (String keyword : keywords) {
            try {
                List<String> synonyms = nlpClient.getSynonyms(keyword);
                expanded.addAll(synonyms);
            } catch (Exception e) {
                // 忽略同义词扩展失败
            }
        }
        
        return new ArrayList<>(expanded);
    }

    private void updateCategoryIndex(KnowledgeEntry entry) {
        String category = entry.getCategory();
        if (category != null) {
            categoryIndex.computeIfAbsent(category, k -> new ArrayList<>())
                .add(entry.getId());
        }
    }

    private void incrementViewCount(String entryId) {
        repository.incrementViewCount(entryId);
        
        KnowledgeEntry cached = entryCache.get(entryId);
        if (cached != null) {
            cached.setViewCount(cached.getViewCount() + 1);
        }
    }

    private KnowledgeEntry convertImportToEntry(KnowledgeEntryImport imp) {
        KnowledgeEntry entry = new KnowledgeEntry();
        entry.setId(UUID.randomUUID().toString());
        entry.setTitle(imp.getTitle());
        entry.setContent(imp.getContent());
        entry.setCategory(imp.getCategory());
        entry.setTags(imp.getTags());
        entry.setEntryType(imp.getEntryType());
        entry.setPriority(imp.getPriority());
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        entry.setEnabled(true);
        entry.setViewCount(0);
        entry.setHelpfulCount(0);
        return entry;
    }

    /**
     * 清理缓存
     */
    public void clearCache() {
        entryCache.clear();
        categoryIndex.clear();
        log.info("知识库缓存已清理");
    }

    /**
     * 获取统计信息
     */
    public KnowledgeStats getStats() {
        return KnowledgeStats.builder()
            .totalEntries(repository.count())
            .enabledEntries(repository.countByEnabledTrue())
            .totalViewCount(repository.sumViewCount())
            .totalHelpfulCount(repository.sumHelpfulCount())
            .categoryCount(getAllCategories().size())
            .build();
    }
}
