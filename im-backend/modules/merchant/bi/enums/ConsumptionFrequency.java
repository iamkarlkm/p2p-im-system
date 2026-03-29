package com.im.backend.modules.merchant.bi.enums;

/**
 * 消费频次枚举
 */
public enum ConsumptionFrequency {

    /** 低频 */
    LOW("low", "低频", 0, 2),

    /** 中频 */
    MEDIUM("medium", "中频", 3, 8),

    /** 高频 */
    HIGH("high", "高频", 9, Integer.MAX_VALUE);

    private final String code;
    private final String desc;
    private final int minCount;
    private final int maxCount;

    ConsumptionFrequency(String code, String desc, int minCount, int maxCount) {
        this.code = code;
        this.desc = desc;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getMinCount() {
        return minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    /**
     * 根据消费次数获取频次类型
     */
    public static ConsumptionFrequency fromCount(int count) {
        for (ConsumptionFrequency frequency : values()) {
            if (count >= frequency.minCount && count <= frequency.maxCount) {
                return frequency;
            }
        }
        return LOW;
    }
}
