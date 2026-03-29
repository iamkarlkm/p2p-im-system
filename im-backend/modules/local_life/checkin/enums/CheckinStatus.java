package com.im.backend.modules.local_life.checkin.enums;

/**
 * 签到状态枚举
 */
public enum CheckinStatus {
    /**
     * 签到成功
     */
    SUCCESS("SUCCESS", "签到成功"),
    
    /**
     * 已签到
     */
    ALREADY_CHECKED("ALREADY_CHECKED", "今日已签到"),
    
    /**
     * 位置验证失败
     */
    LOCATION_INVALID("LOCATION_INVALID", "位置验证失败"),
    
    /**
     * 距离过远
     */
    DISTANCE_TOO_FAR("DISTANCE_TOO_FAR", "距离POI过远"),
    
    /**
     * 疑似作弊
     */
    SUSPECTED_CHEAT("SUSPECTED_CHEAT", "疑似作弊行为"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误");
    
    private final String code;
    private final String desc;
    
    CheckinStatus(String code, String desc) {
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
