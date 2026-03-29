package com.im.backend.modules.merchant.review.enums;

/**
 * 情感分析结果枚举
 */
public enum SentimentResult {
    VERY_NEGATIVE(-0.8, "非常负面"),
    NEGATIVE(-0.4, "负面"),
    NEUTRAL(0.0, "中性"),
    POSITIVE(0.4, "正面"),
    VERY_POSITIVE(0.8, "非常正面");

    private final double threshold;
    private final String desc;

    SentimentResult(double threshold, String desc) {
        this.threshold = threshold;
        this.desc = desc;
    }

    public double getThreshold() {
        return threshold;
    }

    public String getDesc() {
        return desc;
    }

    public static SentimentResult fromScore(double score) {
        if (score <= -0.8) return VERY_NEGATIVE;
        if (score <= -0.4) return NEGATIVE;
        if (score <= 0.4) return NEUTRAL;
        if (score <= 0.8) return POSITIVE;
        return VERY_POSITIVE;
    }
}
