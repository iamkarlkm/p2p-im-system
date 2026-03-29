package com.im.backend.modules.local.life.enums;

import lombok.Getter;

/**
 * 圈子成员角色枚举
 */
@Getter
public enum CircleMemberRole {

    OWNER("OWNER", "圈主", "圈子的创建者,拥有最高权限"),
    ADMIN("ADMIN", "管理员", "圈主任命的管理员"),
    VIP("VIP", "资深成员", "活跃度高、贡献大的成员"),
    MEMBER("MEMBER", "普通成员", "普通圈子成员"),
    GUEST("GUEST", "游客", "临时访问用户");

    private final String code;
    private final String name;
    private final String description;

    CircleMemberRole(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static CircleMemberRole fromCode(String code) {
        for (CircleMemberRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return MEMBER;
    }

    /**
     * 是否有管理权限
     */
    public boolean hasAdminPermission() {
        return this == OWNER || this == ADMIN;
    }
}
