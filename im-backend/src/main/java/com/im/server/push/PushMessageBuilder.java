package com.im.server.push;

import java.util.*;

/**
 * 推送消息构建器
 * 
 * 方便构造各种类型的推送消息
 */
public class PushMessageBuilder {

    /**
     * 构建聊天消息推送
     */
    public static PushMessage buildChatMessage(Long senderId, String senderName, String senderAvatar,
                                              String conversationId, String conversationType,
                                              Long messageId, String messageType, String messageContent,
                                              int unreadCount) {
        String title = senderName;
        String body = truncateContent(messageContent, 100);

        if (unreadCount > 1) {
            title = senderName + " (" + unreadCount + " 条新消息)";
        }

        Map<String, String> data = new HashMap<>();
        data.put("type", "chat_message");
        data.put("sender_id", String.valueOf(senderId));
        data.put("conversation_id", conversationId);
        data.put("conversation_type", conversationType);
        data.put("message_id", String.valueOf(messageId));
        data.put("message_type", messageType);
        data.put("content", messageContent);

        return PushMessage.builder()
                .title(title)
                .body(body)
                .senderId(senderId)
                .senderName(senderName)
                .senderAvatar(senderAvatar)
                .conversationId(conversationId)
                .conversationType(conversationType)
                .messageId(messageId)
                .messageType(messageType)
                .data(data)
                .category("chat_message")
                .priority(PushMessage.Priority.HIGH)
                .interruptionLevel("active")
                .mergeKey("chat:" + conversationId)
                .build();
    }

    /**
     * 构建系统通知推送
     */
    public static PushMessage buildSystemNotification(String title, String content, Map<String, String> extra) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "system_notification");
        data.put("title", title);
        data.put("content", content);
        if (extra != null) {
            data.putAll(extra);
        }

        return PushMessage.builder()
                .title(title)
                .body(content)
                .data(data)
                .category("system")
                .priority(PushMessage.Priority.NORMAL)
                .interruptionLevel("passive")
                .build();
    }

    /**
     * 构建好友请求推送
     */
    public static PushMessage buildFriendRequest(Long requesterId, String requesterName, String requesterAvatar) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "friend_request");
        data.put("requester_id", String.valueOf(requesterId));
        data.put("action", "new_friend_request");

        return PushMessage.builder()
                .title("新好友请求")
                .body(requesterName + " 请求添加你为好友")
                .senderId(requesterId)
                .senderName(requesterName)
                .senderAvatar(requesterAvatar)
                .data(data)
                .category("friend_request")
                .priority(PushMessage.Priority.HIGH)
                .interruptionLevel("active")
                .build();
    }

    /**
     * 构建群组邀请推送
     */
    public static PushMessage buildGroupInvitation(Long inviterId, String inviterName,
                                                   Long groupId, String groupName, String groupAvatar) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "group_invitation");
        data.put("inviter_id", String.valueOf(inviterId));
        data.put("group_id", String.valueOf(groupId));
        data.put("group_name", groupName);
        data.put("action", "new_group_invitation");

        return PushMessage.builder()
                .title("群聊邀请")
                .body(inviterName + " 邀请你加入群聊 " + groupName)
                .senderId(inviterId)
                .senderName(inviterName)
                .data(data)
                .category("group_invitation")
                .priority(PushMessage.Priority.HIGH)
                .interruptionLevel("active")
                .build();
    }

    /**
     * 构建通话邀请推送
     */
    public static PushMessage buildCallInvitation(Long callerId, String callerName, String callerAvatar,
                                                 String callType, String callId, Map<String, String> extras) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "call_invitation");
        data.put("call_id", callId);
        data.put("call_type", callType);
        data.put("caller_id", String.valueOf(callerId));
        data.put("action", "incoming_call");
        if (extras != null) {
            data.putAll(extras);
        }

        String body = callType.equals("video") ? "视频通话邀请" : "语音通话邀请";

        return PushMessage.builder()
                .title(callerName)
                .body(body)
                .senderId(callerId)
                .senderName(callerName)
                .senderAvatar(callerAvatar)
                .data(data)
                .pushType(PushMessage.PushType.VOIP)
                .category("incoming_call")
                .priority(PushMessage.Priority.HIGH)
                .interruptionLevel("timeSensitive")
                .ttl(60) // 通话邀请60秒过期
                .build();
    }

    /**
     * 构建阅后即焚提醒
     */
    public static PushMessage buildSelfDestructReminder(Long senderId, String senderName, String conversationId) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "self_destruct_reminder");
        data.put("conversation_id", conversationId);
        data.put("action", "view_message");

        return PushMessage.builder()
                .title("阅后即焚消息")
                .body(senderName + " 发送了一条阅后即焚消息，点击查看")
                .senderId(senderId)
                .senderName(senderName)
                .data(data)
                .category("self_destruct")
                .priority(PushMessage.Priority.HIGH)
                .interruptionLevel("active")
                .build();
    }

    /**
     * 构建消息引用提醒
     */
    public static PushMessage buildMessageReplyNotification(Long replierId, String replierName,
                                                            String conversationId, Long messageId,
                                                            String replyPreview) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "message_reply");
        data.put("conversation_id", conversationId);
        data.put("message_id", String.valueOf(messageId));
        data.put("action", "view_reply");

        return PushMessage.builder()
                .title(replierName + " 回复了你")
                .body(truncateContent(replyPreview, 100))
                .senderId(replierId)
                .senderName(replierName)
                .data(data)
                .category("message_reply")
                .priority(PushMessage.Priority.NORMAL)
                .interruptionLevel("active")
                .mergeKey("reply:" + conversationId)
                .build();
    }

    /**
     * 构建测试推送
     */
    public static PushMessage buildTestPush() {
        return PushMessage.builder()
                .title("测试推送")
                .body("这是一条测试推送消息")
                .data(new HashMap<String, String>() {{ put("type", "test"); }})
                .isTest(true)
                .build();
    }

    /**
     * 截断内容
     */
    private static String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
}
