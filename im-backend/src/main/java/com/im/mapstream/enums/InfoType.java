package com.im.mapstream.enums;

/**
 * 地图信息流类型枚举
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
public enum InfoType {
    
    /** 朋友圈动态 */
    FRIEND_MOMENT(1, "FRIEND_MOMENT", "朋友圈动态"),
    
    /** 直播 */
    LIVE_STREAM(2, "LIVE_STREAM", "直播"),
    
    /** 短视频 */
    SHORT_VIDEO(3, "SHORT_VIDEO", "短视频"),
    
    /** 本地资讯 */
    LOCAL_NEWS(4, "LOCAL_NEWS", "本地资讯"),
    
    /** 陌生人社交 */
    STRANGER_SOCIAL(5, "STRANGER_SOCIAL", "陌生人社交"),
    
    /** 商家推广 */
    MERCHANT_PROMO(6, "MERCHANT_PROMO", "商家推广"),
    
    /** 活动聚会 */
    EVENT_GATHERING(7, "EVENT_GATHERING", "活动聚会"),
    
    /** 其他类型 */
    OTHER(99, "OTHER", "其他");
    
    private final int code;
    private final String name;
    private final String description;
    
    InfoType(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public int getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    public static InfoType fromCode(int code) {
        for (InfoType type : values()) {
            if (type.code == code) return type;
        }
        return OTHER;
    }
}
