package com.im.backend.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 语言检测服务
 * 支持自动检测文本语言，置信度评估
 */
@Service
public class LanguageDetector {

    // 语言特征映射
    private final Map<String, LanguageProfile> languageProfiles;
    
    // 常用语言的Unicode范围
    private static final Map<String, UnicodeRange> UNICODE_RANGES = new HashMap<>();
    
    // 常用词特征
    private static final Map<String, List<String>> COMMON_WORDS = new HashMap<>();

    public LanguageDetector() {
        this.languageProfiles = new HashMap<>();
        initializeUnicodeRanges();
        initializeCommonWords();
        initializeProfiles();
    }

    /**
     * 检测文本语言
     * @param text 待检测文本
     * @return 语言代码 (如: zh, en, ja, ko, fr, de, es, ru, ar)
     */
    public String detect(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "unknown";
        }

        String cleanText = text.trim();
        
        // 1. 首先通过Unicode范围检测
        String unicodeLang = detectByUnicode(cleanText);
        if (unicodeLang != null) {
            return unicodeLang;
        }

        // 2. 通过N-gram分析检测
        return detectByNgram(cleanText);
    }

    /**
     * 检测语言并返回置信度
     */
    public LanguageDetectionResult detectWithConfidence(String text) {
        String detectedLang = detect(text);
        double confidence = calculateConfidence(text, detectedLang);
        
        return new LanguageDetectionResult(detectedLang, confidence);
    }

    /**
     * 获取可能的语言列表（按概率排序）
     */
    public List<LanguageProbability> detectProbabilities(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Double> scores = new HashMap<>();
        String cleanText = text.trim().toLowerCase();

        // 为每种语言计算得分
        for (Map.Entry<String, LanguageProfile> entry : languageProfiles.entrySet()) {
            String langCode = entry.getKey();
            LanguageProfile profile = entry.getValue();
            double score = calculateLanguageScore(cleanText, profile);
            scores.put(langCode, score);
        }

        // 排序并返回
        List<LanguageProbability> results = new ArrayList<>();
        scores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .forEach(e -> results.add(new LanguageProbability(e.getKey(), e.getValue())));

        return results;
    }

    /**
     * 批量检测多条文本
     */
    public Map<String, String> detectBatch(List<String> texts) {
        Map<String, String> results = new HashMap<>();
        for (String text : texts) {
            results.put(text, detect(text));
        }
        return results;
    }

    /**
     * 通过Unicode范围检测
     */
    private String detectByUnicode(String text) {
        // 统计各语言Unicode字符数量
        Map<String, Integer> charCounts = new HashMap<>();
        
        for (char c : text.toCharArray()) {
            for (Map.Entry<String, UnicodeRange> entry : UNICODE_RANGES.entrySet()) {
                if (entry.getValue().contains(c)) {
                    charCounts.merge(entry.getKey(), 1, Integer::sum);
                }
            }
        }

        // 返回占比最高的语言
        if (charCounts.isEmpty()) {
            return null;
        }

        return charCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * 通过N-gram分析检测
     */
    private String detectByNgram(String text) {
        String cleanText = text.toLowerCase();
        Map<String, Double> scores = new HashMap<>();

        for (Map.Entry<String, LanguageProfile> entry : languageProfiles.entrySet()) {
            String langCode = entry.getKey();
            LanguageProfile profile = entry.getValue();
            double score = calculateNgramScore(cleanText, profile);
            scores.put(langCode, score);
        }

        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("unknown");
    }

    /**
     * 计算N-gram得分
     */
    private double calculateNgramScore(String text, LanguageProfile profile) {
        double score = 0;
        
        // 生成bigram
        List<String> bigrams = generateNgrams(text, 2);
        for (String bigram : bigrams) {
            score += profile.getBigramFrequency(bigram);
        }

        // 生成trigram
        List<String> trigrams = generateNgrams(text, 3);
        for (String trigram : trigrams) {
            score += profile.getTrigramFrequency(trigram) * 1.5; // trigram权重更高
        }

        return score / (bigrams.size() + trigrams.size() + 1);
    }

    /**
     * 生成N-gram
     */
    private List<String> generateNgrams(String text, int n) {
        List<String> ngrams = new ArrayList<>();
        String padded = "_" + text + "_";
        
        for (int i = 0; i < padded.length() - n + 1; i++) {
            ngrams.add(padded.substring(i, i + n));
        }
        
        return ngrams;
    }

    /**
     * 计算语言得分（综合多种特征）
     */
    private double calculateLanguageScore(String text, LanguageProfile profile) {
        double score = 0;

        // 1. 常用词匹配
        List<String> words = tokenize(text);
        for (String word : words) {
            if (profile.getCommonWords().contains(word)) {
                score += 2.0;
            }
        }

        // 2. 字符频率匹配
        Map<Character, Integer> charFreq = calculateCharFrequency(text);
        for (Map.Entry<Character, Integer> entry : charFreq.entrySet()) {
            double profileFreq = profile.getCharFrequency(entry.getKey());
            score += Math.abs(profileFreq - entry.getValue() / (double) text.length());
        }

        // 3. 平均词长匹配
        double avgWordLength = text.length() / (double) Math.max(words.size(), 1);
        score -= Math.abs(avgWordLength - profile.getAverageWordLength());

        return score;
    }

    /**
     * 计算置信度
     */
    private double calculateConfidence(String text, String detectedLang) {
        // 基于文本长度和语言特征的置信度计算
        double confidence = Math.min(text.length() / 50.0, 1.0);
        
        // 如果通过Unicode检测，增加置信度
        String unicodeLang = detectByUnicode(text);
        if (detectedLang.equals(unicodeLang)) {
            confidence = Math.min(confidence + 0.3, 1.0);
        }

        return confidence;
    }

    /**
     * 分词
     */
    private List<String> tokenize(String text) {
        List<String> words = new ArrayList<>();
        String[] tokens = text.split("\\s+|[\\p{Punct}]");
        for (String token : tokens) {
            if (token.length() > 1) {
                words.add(token.toLowerCase());
            }
        }
        return words;
    }

    /**
     * 计算字符频率
     */
    private Map<Character, Integer> calculateCharFrequency(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                freq.merge(Character.toLowerCase(c), 1, Integer::sum);
            }
        }
        return freq;
    }

    /**
     * 初始化Unicode范围
     */
    private void initializeUnicodeRanges() {
        // 中文
        UNICODE_RANGES.put("zh", new UnicodeRange(0x4E00, 0x9FFF));
        // 日文平假名
        UNICODE_RANGES.put("ja", new UnicodeRange(0x3040, 0x309F));
        // 日文片假名
        UNICODE_RANGES.put("ja", new UnicodeRange(0x30A0, 0x30FF));
        // 韩文
        UNICODE_RANGES.put("ko", new UnicodeRange(0xAC00, 0xD7AF));
        // 阿拉伯文
        UNICODE_RANGES.put("ar", new UnicodeRange(0x0600, 0x06FF));
        // 西里尔字母（俄语）
        UNICODE_RANGES.put("ru", new UnicodeRange(0x0400, 0x04FF));
        // 泰文
        UNICODE_RANGES.put("th", new UnicodeRange(0x0E00, 0x0E7F));
        // 希伯来文
        UNICODE_RANGES.put("he", new UnicodeRange(0x0590, 0x05FF));
    }

    /**
     * 初始化常用词
     */
    private void initializeCommonWords() {
        COMMON_WORDS.put("en", Arrays.asList(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i"
        ));
        COMMON_WORDS.put("zh", Arrays.asList(
            "的", "了", "在", "是", "我", "有", "和", "就", "不", "人"
        ));
        COMMON_WORDS.put("ja", Arrays.asList(
            "の", "に", "は", "を", "た", "が", "で", "て", "と", "し"
        ));
        COMMON_WORDS.put("fr", Arrays.asList(
            "le", "de", "et", "à", "un", "il", "être", "et", "en", "avoir"
        ));
        COMMON_WORDS.put("de", Arrays.asList(
            "der", "die", "und", "in", "den", "von", "zu", "das", "mit", "sich"
        ));
        COMMON_WORDS.put("es", Arrays.asList(
            "el", "de", "que", "y", "a", "en", "un", "ser", "se", "no"
        ));
    }

    /**
     * 初始化语言特征档案
     */
    private void initializeProfiles() {
        // 英语
        languageProfiles.put("en", new LanguageProfile("en", 4.5, COMMON_WORDS.get("en")));
        // 中文
        languageProfiles.put("zh", new LanguageProfile("zh", 1.5, COMMON_WORDS.get("zh")));
        // 日语
        languageProfiles.put("ja", new LanguageProfile("ja", 2.0, COMMON_WORDS.get("ja")));
        // 法语
        languageProfiles.put("fr", new LanguageProfile("fr", 4.8, COMMON_WORDS.get("fr")));
        // 德语
        languageProfiles.put("de", new LanguageProfile("de", 5.2, COMMON_WORDS.get("de")));
        // 西班牙语
        languageProfiles.put("es", new LanguageProfile("es", 4.6, COMMON_WORDS.get("es")));
        // 俄语
        languageProfiles.put("ru", new LanguageProfile("ru", 5.0, Collections.emptyList()));
        // 韩语
        languageProfiles.put("ko", new LanguageProfile("ko", 2.0, Collections.emptyList()));
    }

    /**
     * Unicode范围类
     */
    private static class UnicodeRange {
        final int start;
        final int end;

        UnicodeRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean contains(char c) {
            return c >= start && c <= end;
        }
    }

    /**
     * 语言档案类
     */
    private static class LanguageProfile {
        private final String languageCode;
        private final double averageWordLength;
        private final List<String> commonWords;
        private final Map<String, Double> bigramFreq = new HashMap<>();
        private final Map<String, Double> trigramFreq = new HashMap<>();
        private final Map<Character, Double> charFreq = new HashMap<>();

        LanguageProfile(String code, double avgWordLen, List<String> words) {
            this.languageCode = code;
            this.averageWordLength = avgWordLen;
            this.commonWords = words != null ? words : Collections.emptyList();
        }

        double getBigramFrequency(String bigram) {
            return bigramFreq.getOrDefault(bigram, 0.0);
        }

        double getTrigramFrequency(String trigram) {
            return trigramFreq.getOrDefault(trigram, 0.0);
        }

        double getCharFrequency(char c) {
            return charFreq.getOrDefault(c, 0.0);
        }

        List<String> getCommonWords() {
            return commonWords;
        }

        double getAverageWordLength() {
            return averageWordLength;
        }
    }

    /**
     * 语言检测结果
     */
    public static class LanguageDetectionResult {
        private final String languageCode;
        private final double confidence;

        public LanguageDetectionResult(String languageCode, double confidence) {
            this.languageCode = languageCode;
            this.confidence = confidence;
        }

        public String getLanguageCode() { return languageCode; }
        public double getConfidence() { return confidence; }
    }

    /**
     * 语言概率
     */
    public static class LanguageProbability {
        private final String languageCode;
        private final double probability;

        public LanguageProbability(String languageCode, double probability) {
            this.languageCode = languageCode;
            this.probability = probability;
        }

        public String getLanguageCode() { return languageCode; }
        public double getProbability() { return probability; }
    }
}
