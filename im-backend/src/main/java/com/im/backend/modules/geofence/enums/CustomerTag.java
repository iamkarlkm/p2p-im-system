package com.im.backend.modules.geofence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 到店客户标签
 */
@Getter
@AllArgsConstructor
public enum CustomerTag {

    NEW("NEW", "新客户"),
    OLD("OLD", "老客户"),
    VIP("VIP", "VIP客户"),
    SILENT("SILENT", "沉默客户"),
    LOST("LOST", "流失客户");

    private final String code;
    private final String desc;

    public static CustomerTag fromCode(String code) {
        for (CustomerTag tag : values()) {
            if (tag.code.equals(code)) {
                return tag;
            }
        }
        return null;
    }
}
