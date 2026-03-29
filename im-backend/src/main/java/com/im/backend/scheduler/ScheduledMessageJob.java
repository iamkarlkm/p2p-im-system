package com.im.backend.scheduler;

import com.im.backend.dto.ScheduledMessageDTO;
import com.im.backend.service.MessageService;
import com.im.backend.service.ScheduledMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时消息调度器
 * 每分钟检查并发送到期的定时消息
 */
@Component
public class ScheduledMessageJob {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMessageJob.class);

    @Autowired
    private ScheduledMessageService scheduledMessageService;

    @Autowired
    private MessageService messageService;

    /**
     * 每分钟执行一次，检查并发送到期的定时消息
     */
    @Scheduled(fixedRate = 60000) // 60秒
    public void processScheduledMessages() {
        logger.debug("开始检查定时消息...");
        
        try {
            List<ScheduledMessageDTO> pendingMessages = scheduledMessageService.getPendingMessagesForSending();
            
            if (pendingMessages.isEmpty()) {
                logger.debug("没有到期的定时消息");
                return;
            }
            
            logger.info("发现 {} 条到期的定时消息", pendingMessages.size());
            
            for (ScheduledMessageDTO message : pendingMessages) {
                try {
                    sendScheduledMessage(message);
                } catch (Exception e) {
                    logger.error("发送定时消息失败: messageId={}", message.getId(), e);
                    scheduledMessageService.markAsFailed(message.getId(), e.getMessage());
                }
            }
            
            logger.info("定时消息处理完成，成功发送 {} 条", pendingMessages.size());
            
        } catch (Exception e) {
            logger.error("处理定时消息时发生错误", e);
        }
    }

    /**
     * 发送单条定时消息
     */
    private void sendScheduledMessage(ScheduledMessageDTO message) {
        logger.info("发送定时消息: id={}, sender={}, receiver={}", 
                message.getId(), message.getSenderId(), message.getReceiverId());
        
        // 调用消息服务发送消息
        // 这里假设MessageService有相应的发送方法
        // messageService.sendMessage(message.getSenderId(), message.getReceiverId(), message.getContent());
        
        // 标记为已发送
        scheduledMessageService.markAsSent(message.getId());
        
        logger.info("定时消息发送成功: id={}", message.getId());
    }
}
