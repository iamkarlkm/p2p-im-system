package com.im.backend.modules.local.life.enums;

import lombok.Getter;

/**
 * 活动分类枚举
 */
@Getter
public enum ActivityCategory {

    GATHERING("GATHERING", "聚餐", "美食聚餐、饭局活动"),
    SPORT("SPORT", "运动", "跑步、健身、球类运动"),
    TRAVEL("TRAVEL", "旅行", "周边游、自驾游、徒步"),
    PARENT_CHILD("PARENT_CHILD", "亲子", "亲子活动、育儿交流"),
    PET("PET", "宠物", "遛狗、宠物聚会"),
    ENTERTAINMENT("ENTERTAINMENT", "娱乐", "KTV、桌游、电影"),
    STUDY("STUDY", "学习", "读书会、技能分享"),
    PARTY("PARTY", "派对", "生日派对、节日聚会"),
    CHARITY("CHARITY", "公益", "志愿者活动、公益行动"),
    BUSINESS("BUSINESS", "商务", "商务交流、行业沙龙"),
    OTHER("OTHER", "其他", "其他类型活动");

    private final String code;
    private final String name;
    private final String description;

    ActivityCategory(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static ActivityCategory fromCode(String code) {
        for (ActivityCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return OTHER;
    }
}
