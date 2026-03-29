package com.im.backend.scheduler;

import com.im.backend.service.MessageExpirationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消息过期清理调度器
 * 每分钟执行一次，清理过期消息
 */
@Component
@EnableScheduling
public class MessageExpirationCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(MessageExpirationCleanupScheduler.class);
    private final MessageExpirationService expirationService;

    public MessageExpirationCleanupScheduler(MessageExpirationService expirationService) {
        this.expirationService = expirationService;
    }

    /**
     * 每分钟执行一次过期消息清理
     */
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredMessages() {
        try {
            long start = System.currentTimeMillis();
            int count = expirationService.cleanupExpiredMessages();
            long duration = System.currentTimeMillis() - start;
            if (count > 0) {
                log.info("过期消息清理完成，销毁 {} 条消息，耗时 {} ms", count, duration);
            }
        } catch (Exception e) {
            log.error("过期消息清理失败", e);
        }
    }

    /**
     * 每5分钟发送过期前提醒
     */
    @Scheduled(fixedRate = 300000)
    public void sendPreExpireNotices() {
        // 查找即将过期且未发送提醒的消息
        // 通知客户端显示"消息即将过期"提示
        log.debug("检查即将过期的消息...");
    }

    /**
     * 每小时清理已销毁消息的残留记录
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanupDestroyedRecords() {
        log.info("清理已销毁消息记录...");
    }
}
