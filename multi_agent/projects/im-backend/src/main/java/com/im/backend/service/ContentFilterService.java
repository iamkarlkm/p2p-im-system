package com.im.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ContentFilterService {
    
    private final Set<String> sensitiveWords = new HashSet<>();
    private final Set<String> spamPatterns = new HashSet<>();
    private final Map<String, ContentCategory> categoryPatterns = new HashMap<>();
    
    public ContentFilterService() {
        initSensitiveWords();
        initSpamPatterns();
        initCategoryPatterns();
    }
    
    private void initSensitiveWords() {
        sensitiveWords.addAll(Arrays.asList(
            "spam", "scam", "phishing", "malware", "virus",
            "赌博", "彩票", "诈骗", "敏感词", "违禁品"
        ));
    }
    
    private void initSpamPatterns() {
        spamPatterns.add("http[s]?://\\S+");
        spamPatterns.add("\\d{11}");
        spamPatterns.add("\\$\\d+");
        spamPatterns.add("免费.*领取");
        spamPatterns.add("点击.*链接");
    }
    
    private void initCategoryPatterns() {
        categoryPatterns.put("url", Pattern.compile("http[s]?://\\S+").pattern());
        categoryPatterns.put("phone", Pattern.compile("\\d{11}").pattern());
        categoryPatterns.put("email", Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").pattern());
        categoryPatterns.put("money", Pattern.compile("\\$\\d+|¥\\d+|￥\\d+").pattern());
    }
    
    public FilterResult filterContent(String content) {
        FilterResult result = new FilterResult();
        result.setContent(content);
        result.setBlocked(false);
        result.setCategories(new ArrayList<>());
        result.setSensitiveWordsFound(new ArrayList<>());
        result.setSpamScore(0.0);
        
        if (content == null || content.isEmpty()) {
            return result;
        }
        
        String lowerContent = content.toLowerCase();
        
        for (String word : sensitiveWords) {
            if (lowerContent.contains(word.toLowerCase())) {
                result.setBlocked(true);
                result.getSensitiveWordsFound().add(word);
                log.warn("Sensitive word detected: {}", word);
            }
        }
        
        for (String pattern : spamPatterns) {
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(content).find()) {
                result.setSpamScore(result.getSpamScore() + 0.2);
                log.debug("Spam pattern matched: {}", pattern);
            }
        }
        
        if (result.getSpamScore() > 0.5) {
            result.setBlocked(true);
            log.warn("Content blocked due to high spam score: {}", result.getSpamScore());
        }
        
        for (Map.Entry<String, ContentCategory> entry : categoryPatterns.entrySet()) {
            if (Pattern.compile(entry.getValue().getPattern(), Pattern.CASE_INSENSITIVE).matcher(content).find()) {
                result.getCategories().add(entry.getKey());
            }
        }
        
        result.setSafe(!result.getBlocked());
        return result;
    }
    
    public boolean addSensitiveWord(String word) {
        return sensitiveWords.add(word);
    }
    
    public boolean removeSensitiveWord(String word) {
        return sensitiveWords.remove(word);
    }
    
    public Set<String> getSensitiveWords() {
        return new HashSet<>(sensitiveWords);
    }
    
    public void addSensitiveWords(List<String> words) {
        sensitiveWords.addAll(words);
        log.info("Added {} sensitive words", words.size());
    }
    
    public static class FilterResult {
        private String content;
        private boolean blocked;
        private boolean safe;
        private double spamScore;
        private List<String> sensitiveWordsFound;
        private List<String> categories;
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public boolean isBlocked() { return blocked; }
        public void setBlocked(boolean blocked) { this.blocked = blocked; }
        public boolean isSafe() { return safe; }
        public void setSafe(boolean safe) { this.safe = safe; }
        public double getSpamScore() { return spamScore; }
        public void setSpamScore(double spamScore) { this.spamScore = spamScore; }
        public List<String> getSensitiveWordsFound() { return sensitiveWordsFound; }
        public void setSensitiveWordsFound(List<String> sensitiveWordsFound) { this.sensitiveWordsFound = sensitiveWordsFound; }
        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
    }
    
    public static class ContentCategory {
        private final String pattern;
        
        public ContentCategory(String pattern) {
            this.pattern = pattern;
        }
        
        public String getPattern() { return pattern; }
    }
}
