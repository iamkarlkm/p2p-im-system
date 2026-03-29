package com.im.backend.modules.merchant.bi.enums;

/**
 * 统计时段类型枚举
 */
public enum StatsPeriodType {

    /** 实时 */
    REALTIME("realtime", "实时"),

    /** 今日 */
    TODAY("today", "今日"),

    /** 昨日 */
    YESTERDAY("yesterday", "昨日"),

    /** 近7天 */
    LAST_7_DAYS("last_7_days", "近7天"),

    /** 近30天 */
    LAST_30_DAYS("last_30_days", "近30天"),

    /** 本周 */
    THIS_WEEK("this_week", "本周"),

    /** 上周 */
    LAST_WEEK("last_week", "上周"),

    /** 本月 */
    THIS_MONTH("this_month", "本月"),

    /** 上月 */
    LAST_MONTH("last_month", "上月"),

    /** 本季度 */
    THIS_QUARTER("this_quarter", "本季度"),

    /** 本年 */
    THIS_YEAR("this_year", "本年"),

    /** 自定义 */
    CUSTOM("custom", "自定义");

    private final String code;
    private final String desc;

    StatsPeriodType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
