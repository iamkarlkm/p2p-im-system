package com.im.system.service;

import com.im.system.entity.SensitiveWordEntity;
import com.im.system.repository.SensitiveWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 敏感词服务
 */
@Service
@Transactional
public class SensitiveWordService {
    
    @Autowired
    private SensitiveWordRepository sensitiveWordRepository;
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    private Map<String, Pattern> compiledPatterns = new ConcurrentHashMap<>();
    
    // ==================== CRUD 操作 ====================
    
    /**
     * 创建敏感词
     */
    public SensitiveWordEntity create(SensitiveWordEntity entity) {
        if (entity == null || !StringUtils.hasText(entity.getWord())) {
            throw new IllegalArgumentException("敏感词不能为空");
        }
        
        // 检查是否已存在
        Optional<SensitiveWordEntity> existing = sensitiveWordRepository.findByWord(entity.getWord());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("敏感词已存在：" + entity.getWord());
        }
        
        // 设置默认值
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        if (entity.getMatchCount() == null) {
            entity.setMatchCount(0);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        
        return sensitiveWordRepository.save(entity);
    }
    
    /**
     * 批量创建敏感词
     */
    public List<SensitiveWordEntity> batchCreate(List<SensitiveWordEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SensitiveWordEntity> saved = new ArrayList<>();
        for (SensitiveWordEntity entity : entities) {
            try {
                saved.add(create(entity));
            } catch (IllegalArgumentException e) {
                // 跳过已存在的词
            }
        }
        
        return saved;
    }
    
    /**
     * 批量导入（从文本）
     */
    public List<SensitiveWordEntity> importWords(String text, String category, String level) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SensitiveWordEntity> entities = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        
        for (String line : lines) {
            String word = line.trim();
            if (StringUtils.hasText(word)) {
                SensitiveWordEntity entity = new SensitiveWordEntity(word);
                entity.setCategory(category);
                entity.setLevel(level);
                entity.setEnabled(true);
                entities.add(entity);
            }
        }
        
        return batchCreate(entities);
    }
    
    /**
     * 更新敏感词
     */
    public SensitiveWordEntity update(Long id, SensitiveWordEntity entity) {
        SensitiveWordEntity existing = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("敏感词不存在：" + id));
        
        if (entity.getWord() != null) {
            existing.setWord(entity.getWord());
        }
        if (entity.getCategory() != null) {
            existing.setCategory(entity.getCategory());
        }
        if (entity.getLevel() != null) {
            existing.setLevel(entity.getLevel());
        }
        if (entity.getEnabled() != null) {
            existing.setEnabled(entity.getEnabled());
        }
        if (entity.getReplacement() != null) {
            existing.setReplacement(entity.getReplacement());
        }
        if (entity.getPattern() != null) {
            existing.setPattern(entity.getPattern());
            compiledPatterns.remove(existing.getWord());
        }
        if (entity.getDescription() != null) {
            existing.setDescription(entity.getDescription());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        
        return sensitiveWordRepository.save(existing);
    }
    
    /**
     * 删除敏感词
     */
    public boolean delete(Long id) {
        if (!sensitiveWordRepository.existsById(id)) {
            return false;
        }
        sensitiveWordRepository.deleteById(id);
        return true;
    }
    
    /**
     * 批量删除
     */
    public int batchDelete(List<Long> ids) {
        return sensitiveWordRepository.batchDelete(ids);
    }
    
    /**
     * 启用/禁用敏感词
     */
    public SensitiveWordEntity toggleEnabled(Long id, Boolean enabled) {
        SensitiveWordEntity entity = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("敏感词不存在：" + id));
        entity.setEnabled(enabled);
        entity.setUpdatedAt(LocalDateTime.now());
        return sensitiveWordRepository.save(entity);
    }
    
    /**
     * 批量启用/禁用
     */
    public int batchToggle(List<Long> ids, Boolean enabled) {
        return sensitiveWordRepository.batchUpdateEnabled(ids, enabled);
    }
    
    // ==================== 查询操作 ====================
    
    /**
     * 根据 ID 查询
     */
    public Optional<SensitiveWordEntity> getById(Long id) {
        return sensitiveWordRepository.findById(id);
    }
    
    /**
     * 根据词查询
     */
    public Optional<SensitiveWordEntity> getByWord(String word) {
        return sensitiveWordRepository.findByWord(word);
    }
    
    /**
     * 分页查询
     */
    public Page<SensitiveWordEntity> getAll(int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return sensitiveWordRepository.findAll(pageable);
    }
    
    /**
     * 按启用状态查询
     */
    public Page<SensitiveWordEntity> getByEnabled(Boolean enabled, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return sensitiveWordRepository.findByEnabled(enabled, pageable);
    }
    
    /**
     * 按分类查询
     */
    public Page<SensitiveWordEntity> getByCategory(String category, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return sensitiveWordRepository.findByCategory(category, pageable);
    }
    
    /**
     * 按等级查询
     */
    public Page<SensitiveWordEntity> getByLevel(String level, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return sensitiveWordRepository.findByLevel(level, pageable);
    }
    
    /**
     * 搜索敏感词
     */
    public Page<SensitiveWordEntity> search(String keyword, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return sensitiveWordRepository.findByWordContaining(keyword, pageable);
    }
    
    /**
     * 获取所有启用的敏感词
     */
    public List<SensitiveWordEntity> getAllEnabled() {
        return sensitiveWordRepository.findAllEnabled();
    }
    
    /**
     * 按匹配次数排序
     */
    public Page<SensitiveWordEntity> getByMatchCount(int page, int size) {
        Pageable pageable = createPageable(page, size);
        return sensitiveWordRepository.findAllByOrderByMatchCountDesc(pageable);
    }
    
    // ==================== 统计操作 ====================
    
    /**
     * 统计总数
     */
    public long count() {
        return sensitiveWordRepository.count();
    }
    
    /**
     * 统计启用数量
     */
    public long countEnabled() {
        return sensitiveWordRepository.countByEnabled(true);
    }
    
    /**
     * 统计禁用数量
     */
    public long countDisabled() {
        return sensitiveWordRepository.countByEnabled(false);
    }
    
    /**
     * 按分类统计
     */
    public Map<String, Long> countByCategory() {
        List<Object[]> results = sensitiveWordRepository.countByCategory();
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    /**
     * 按等级统计
     */
    public Map<String, Long> countByLevel() {
        List<Object[]> results = sensitiveWordRepository.countByLevel();
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    /**
     * 获取高匹配次数敏感词
     */
    public List<SensitiveWordEntity> getHighMatchWords(int minCount) {
        return sensitiveWordRepository.findByMatchCountGreaterThanEqual(minCount);
    }
    
    // ==================== 敏感词检测 ====================
    
    /**
     * 检测文本是否包含敏感词
     */
    public Map<String, Object> detectSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasSensitive", false);
            result.put("words", Collections.emptyList());
            return result;
        }
        
        List<SensitiveWordEntity> enabledWords = getAllEnabled();
        List<Map<String, Object>> foundWords = new ArrayList<>();
        
        for (SensitiveWordEntity wordEntity : enabledWords) {
            String word = wordEntity.getWord();
            if (text.contains(word)) {
                Map<String, Object> wordInfo = new HashMap<>();
                wordInfo.put("word", word);
                wordInfo.put("id", wordEntity.getId());
                wordInfo.put("category", wordEntity.getCategory());
                wordInfo.put("level", wordEntity.getLevel());
                wordInfo.put("replacement", wordEntity.getReplacement());
                foundWords.add(wordInfo);
                
                // 增加匹配次数
                sensitiveWordRepository.incrementMatchCount(word);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("hasSensitive", !foundWords.isEmpty());
        result.put("words", foundWords);
        result.put("count", foundWords.size());
        
        return result;
    }
    
    /**
     * 过滤敏感词
     */
    public String filterSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        List<SensitiveWordEntity> enabledWords = getAllEnabled();
        String result = text;
        
        for (SensitiveWordEntity wordEntity : enabledWords) {
            String word = wordEntity.getWord();
            String replacement = wordEntity.getReplacement();
            if (replacement == null || replacement.isEmpty()) {
                replacement = "*".repeat(word.length());
            }
            result = result.replace(word, replacement);
        }
        
        return result;
    }
    
    /**
     * 正则匹配检测
     */
    public Map<String, Object> detectWithRegex(String text) {
        if (text == null || text.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasSensitive", false);
            result.put("matches", Collections.emptyList());
            return result;
        }
        
        List<SensitiveWordEntity> enabledWords = getAllEnabled();
        List<Map<String, Object>> matches = new ArrayList<>();
        
        for (SensitiveWordEntity wordEntity : enabledWords) {
            String pattern = wordEntity.getPattern();
            if (StringUtils.hasText(pattern)) {
                try {
                    Pattern regex = compiledPatterns.computeIfAbsent(
                            wordEntity.getWord() + "_" + pattern,
                            k -> Pattern.compile(pattern)
                    );
                    
                    if (regex.matcher(text).find()) {
                        Map<String, Object> matchInfo = new HashMap<>();
                        matchInfo.put("word", wordEntity.getWord());
                        matchInfo.put("pattern", pattern);
                        matchInfo.put("category", wordEntity.getCategory());
                        matchInfo.put("level", wordEntity.getLevel());
                        matches.add(matchInfo);
                    }
                } catch (Exception e) {
                    // 忽略无效的正则表达式
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("hasSensitive", !matches.isEmpty());
        result.put("matches", matches);
        result.put("count", matches.size());
        
        return result;
    }
    
    // ==================== 辅助方法 ====================
    
    private Pageable createPageable(int page, int size, Sort sort) {
        if (page < 0) page = 0;
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        return PageRequest.of(page, size, sort);
    }
}