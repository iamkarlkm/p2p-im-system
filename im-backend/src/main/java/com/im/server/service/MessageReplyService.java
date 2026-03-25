package com.im.server.service;

import com.im.server.entity.Message;
import com.im.server.entity.MessageReply;
import com.im.server.repository.MessageReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息引用/回复服务
 * 
 * 功能特性：
 * - 消息引用和回复
 * - 引用链追踪（最多3层嵌套）
 * - 引用消息预览
 * - 引用消息全文预览（长消息截断）
 * - 回复线程查询
 * - 引用链展开/折叠
 * - 回复统计
 * - 被引用消息高亮
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReplyService {

    private final MessageReplyRepository messageReplyRepository;
    private final MessageService messageService;

    /** 最大引用嵌套深度 */
    private static final int MAX_REPLY_DEPTH = 3;

    /** 消息预览最大长度 */
    private static final int PREVIEW_MAX_LENGTH = 200;

    /**
     * 发送带引用的回复消息
     * 
     * @param replyUserId      回复者ID
     * @param replyNickname    回复者昵称
     * @param originalMsgId    被引用的原消息ID
     * @param replyContent     回复内容（可为null表示纯引用）
     * @param replyMsgType     回复消息类型
     * @param chatType         聊天类型（1私聊 2群聊）
     * @param chatId           聊天ID
     * @param replyRemark      引用备注（可选）
     * @return 创建的引用记录
     */
    @Transactional
    public MessageReply createReply(Long replyUserId, String replyNickname,
                                    String originalMsgId, String replyContent,
                                    Integer replyMsgType, Integer chatType,
                                    Long chatId, String replyRemark) {
        // 获取原消息
        Message originalMsg = messageService.getMessageById(originalMsgId);
        if (originalMsg == null) {
            throw new RuntimeException("引用的消息不存在");
        }

        // 计算引用深度
        Integer parentDepth = messageReplyRepository.findMaxDepthByOriginalMsgId(originalMsgId);
        int replyDepth = (parentDepth == null ? 0 : Math.min(parentDepth + 1, MAX_REPLY_DEPTH));

        // 生成引用链ID
        String replyChainId = buildReplyChainId(originalMsgId, replyDepth);

        // 生成回复消息ID
        String replyMsgId = java.util.UUID.randomUUID().toString();

        // 构建回复记录
        MessageReply reply = new MessageReply();
        reply.setOriginalMsgId(originalMsgId);
        reply.setReplyMsgId(replyMsgId);
        reply.setOriginalSenderId(originalMsg.getFromUserId());
        reply.setOriginalSenderNickname(originalMsg.getFromUserId().toString()); // TODO: 需查询昵称
        reply.setOriginalContentPreview(truncateContent(originalMsg.getContent(), PREVIEW_MAX_LENGTH));
        reply.setOriginalMsgType(originalMsg.getMsgType());
        reply.setOriginalMsgTime(originalMsg.getCreateTime());
        reply.setReplyDepth(replyDepth);
        reply.setReplyChainId(replyChainId);
        reply.setChainDepth(replyDepth + 1);
        reply.setOriginalRecalled(originalMsg.getIsRecalled() != null && originalMsg.getIsRecalled());
        reply.setReplyRemark(replyRemark);
        reply.setHighlight(false);
        reply.setChatType(chatType);
        reply.setChatId(chatId);
        reply.setReplyUserId(replyUserId);
        reply.setReplyUserNickname(replyNickname);
        reply.setReplyContent(replyContent);
        reply.setReplyMsgType(replyMsgType != null ? replyMsgType : 1);
        reply.setDeleted(0);

        MessageReply saved = messageReplyRepository.save(reply);
        log.info("创建消息引用成功: replyId={}, originalMsgId={}, replyUserId={}",
                 saved.getId(), originalMsgId, replyUserId);
        return saved;
    }

    /**
     * 获取消息的引用详情
     */
    public MessageReply getReplyByOriginalMsgId(String originalMsgId, Long replyUserId) {
        List<MessageReply> replies = messageReplyRepository.findByOriginalMsgIdAndReplyUserIdOrderByCreateTimeDesc(
                originalMsgId, replyUserId);
        return replies.isEmpty() ? null : replies.get(0);
    }

    /**
     * 获取消息的所有直接回复（不含嵌套）
     */
    public List<MessageReply> getDirectReplies(String originalMsgId) {
        return messageReplyRepository.findByOriginalMsgIdAndDeletedOrderByCreateTimeAsc(originalMsgId, 0);
    }

    /**
     * 获取消息的引用链（包含所有层级的回复）
     */
    public List<MessageReply> getReplyChain(String originalMsgId) {
        List<MessageReply> allReplies = new ArrayList<>();
        collectRepliesRecursively(originalMsgId, allReplies, 0);
        return allReplies;
    }

    private void collectRepliesRecursively(String msgId, List<MessageReply> result, int depth) {
        if (depth >= MAX_REPLY_DEPTH) return;
        List<MessageReply> replies = messageReplyRepository.findByOriginalMsgIdAndDeletedOrderByCreateTimeAsc(msgId, 0);
        for (MessageReply reply : replies) {
            result.add(reply);
            // 继续查询该回复的回复
            collectRepliesRecursively(reply.getReplyMsgId(), result, depth + 1);
        }
    }

    /**
     * 获取回复线程（引用链 + 回复内容）
     * 包含原消息信息和所有层级的回复
     */
    public ReplyThread getReplyThread(String originalMsgId) {
        Message originalMsg = messageService.getMessageById(originalMsgId);
        if (originalMsg == null) {
            throw new RuntimeException("消息不存在");
        }
        List<MessageReply> chain = getReplyChain(originalMsgId);
        return new ReplyThread(originalMsg, chain);
    }

    /**
     * 获取回复数量统计
     */
    public ReplyStats getReplyStats(String originalMsgId) {
        List<MessageReply> allReplies = getReplyChain(originalMsgId);
        int totalCount = allReplies.size();
        int textCount = 0;
        int imageCount = 0;
        int fileCount = 0;
        for (MessageReply r : allReplies) {
            Integer t = r.getReplyMsgType();
            if (t == null) continue;
            if (t == 1) textCount++;
            else if (t == 2) imageCount++;
            else if (t == 3) fileCount++;
        }
        return new ReplyStats(originalMsgId, totalCount, textCount, imageCount, fileCount);
    }

    /**
     * 将回复标记为已删除（软删除）
     */
    @Transactional
    public void deleteReply(Long replyId, Long userId) {
        MessageReply reply = messageReplyRepository.findById(replyId).orElse(null);
        if (reply == null) {
            throw new RuntimeException("回复不存在");
        }
        if (!reply.getReplyUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的回复");
        }
        reply.setDeleted(1);
        messageReplyRepository.save(reply);
        log.info("软删除消息回复: replyId={}, userId={}", replyId, userId);
    }

    /**
     * 高亮/取消高亮引用
     */
    @Transactional
    public void toggleHighlight(Long replyId, Long userId, boolean highlight) {
        MessageReply reply = messageReplyRepository.findById(replyId).orElse(null);
        if (reply == null) {
            throw new RuntimeException("回复不存在");
        }
        // 只有原消息发送者或回复者可以高亮
        if (!reply.getOriginalSenderId().equals(userId) && !reply.getReplyUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        reply.setHighlight(highlight);
        messageReplyRepository.save(reply);
    }

    /**
     * 获取某用户最近参与的所有引用回复（跨会话）
     */
    public List<MessageReply> getUserRecentReplies(Long userId, int limit) {
        return messageReplyRepository.findTopByReplyUserIdOrderByCreateTimeDesc(userId, 
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    /**
     * 获取某会话中所有带引用的消息
     */
    public List<MessageReply> getRepliesInChat(Integer chatType, Long chatId) {
        return messageReplyRepository.findByChatTypeAndChatIdAndDeletedOrderByCreateTimeDesc(
                chatType, chatId, 0);
    }

    /**
     * 检查消息是否被引用过
     */
    public boolean isMessageReplied(String msgId) {
        return messageReplyRepository.existsByOriginalMsgId(msgId);
    }

    // ─── 内部工具方法 ───────────────────────────────────────────

    private String buildReplyChainId(String originalMsgId, int depth) {
        // 引用链ID格式: originalMsgId[:replyMsgId]*，按深度截断
        // 这里简化处理，存储为 originalMsgId + depth
        return originalMsgId + ":" + depth;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength - 3) + "...";
    }

    // ─── 内部类 ─────────────────────────────────────────────────

    /** 回复线程，包含原消息和所有回复 */
    public static class ReplyThread {
        public final Message originalMessage;
        public final List<MessageReply> replies;

        public ReplyThread(Message originalMessage, List<MessageReply> replies) {
            this.originalMessage = originalMessage;
            this.replies = replies;
        }
    }

    /** 回复统计 */
    public static class ReplyStats {
        public final String originalMsgId;
        public final int totalCount;
        public final int textCount;
        public final int imageCount;
        public final int fileCount;

        public ReplyStats(String originalMsgId, int totalCount, int textCount, int imageCount, int fileCount) {
            this.originalMsgId = originalMsgId;
            this.totalCount = totalCount;
            this.textCount = textCount;
            this.imageCount = imageCount;
            this.fileCount = fileCount;
        }
    }
}
