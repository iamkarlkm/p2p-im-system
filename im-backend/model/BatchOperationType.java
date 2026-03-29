// 批量操作类型枚举
package com.im.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "批量操作类型")
public enum BatchOperationType {
    
    FORWARD("转发", "将多条消息转发到其他会话"),
    DELETE("删除", "批量删除消息"),
    RECALL("撤回", "批量撤回已发送消息"),
    FAVORITE("收藏", "批量收藏消息"),
    PIN("置顶", "批量置顶消息"),
    COPY("复制", "复制消息内容"),
    MOVE("移动", "将消息移动到其他会话"),
    ARCHIVE("归档", "批量归档消息"),
    MARK_READ("标记已读", "批量标记消息为已读"),
    MARK_UNREAD("标记未读", "批量标记消息为未读"),
    ADD_TAG("添加标签", "批量添加消息标签"),
    REMOVE_TAG("移除标签", "批量移除消息标签"),
    EXPORT("导出", "批量导出消息"),
    SCHEDULE("定时发送", "批量设置定时发送"),
    REMIND("设置提醒", "批量设置消息提醒"),
    REACTION("添加表情", "批量添加消息表情反应"),
    TRANSLATE("翻译", "批量翻译消息"),
    SUMMARIZE("总结", "批量总结消息内容");

    private final String displayName;
    private final String description;

    BatchOperationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isForward() {
        return this == FORWARD;
    }

    public boolean isDelete() {
        return this == DELETE;
    }

    public boolean isRecall() {
        return this == RECALL;
    }

    public boolean isFavorite() {
        return this == FAVORITE;
    }

    public boolean isPin() {
        return this == PIN;
    }

    public boolean isCopy() {
        return this == COPY;
    }

    public boolean isMove() {
        return this == MOVE;
    }

    public boolean isReadOperation() {
        return this == MARK_READ || this == MARK_UNREAD;
    }

    public boolean isTagOperation() {
        return this == ADD_TAG || this == REMOVE_TAG;
    }

    public boolean requiresTarget() {
        return this == FORWARD || this == MOVE;
    }

    public boolean isAsyncPreferred(int messageCount) {
        return messageCount > 50 && (this == EXPORT || this == SUMMARIZE || this == TRANSLATE);
    }
}
