package com.im.backend.modules.local.search.enums;

import lombok.Getter;

/**
 * 搜索来源枚举
 */
@Getter
public enum SearchSource {

    TEXT_INPUT("TEXT", "文本输入", "用户手动输入搜索"),
    VOICE_INPUT("VOICE", "语音输入", "语音搜索"),
    SCAN("SCAN", "扫码", "扫描二维码"),
    RECOMMENDATION("RECOMMEND", "推荐", "系统推荐点击"),
    HISTORY("HISTORY", "历史记录", "历史搜索点击"),
    HOT_KEYWORD("HOT", "热词", "热词点击");

    private final String code;
    private final String name;
    private final String description;

    SearchSource(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
