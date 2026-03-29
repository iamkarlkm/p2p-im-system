package com.im.backend.modules.local.enums;

/**
 * 资源调度类型枚举
 */
public enum DispatchType {
    
    PRE_DISPATCH(1, "预调度"),
    EMERGENCY(2, "紧急调度"),
    CROSS_FENCE(3, "跨围栏调度"),
    LOAD_BALANCE(4, "负载均衡调度");
    
    private final Integer code;
    private final String desc;
    
    DispatchType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static DispatchType getByCode(Integer code) {
        for (DispatchType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
