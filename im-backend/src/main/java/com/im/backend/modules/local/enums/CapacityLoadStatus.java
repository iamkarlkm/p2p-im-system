package com.im.backend.modules.local.enums;

/**
 * 运力负载状态枚举
 */
public enum CapacityLoadStatus {
    
    IDLE(1, "空闲", 0, 30),
    NORMAL(2, "正常", 30, 60),
    BUSY(3, "繁忙", 60, 85),
    OVERLOAD(4, "超载", 85, 100);
    
    private final Integer code;
    private final String desc;
    private final Integer minRate;
    private final Integer maxRate;
    
    CapacityLoadStatus(Integer code, String desc, Integer minRate, Integer maxRate) {
        this.code = code;
        this.desc = desc;
        this.minRate = minRate;
        this.maxRate = maxRate;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public Integer getMinRate() {
        return minRate;
    }
    
    public Integer getMaxRate() {
        return maxRate;
    }
    
    public static CapacityLoadStatus getByRate(Integer rate) {
        for (CapacityLoadStatus status : values()) {
            if (rate >= status.getMinRate() && rate <= status.getMaxRate()) {
                return status;
            }
        }
        return OVERLOAD;
    }
    
    public static CapacityLoadStatus getByCode(Integer code) {
        for (CapacityLoadStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
