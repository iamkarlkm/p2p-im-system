package com.im.backend.scheduler;

import com.im.backend.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 翻译服务定时任务
 */
@Component
public class TranslationScheduler {

    @Autowired
    private TranslationService translationService;

    /**
     * 每小时清理过期缓存
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void clearExpiredCache() {
        System.out.println("[" + LocalDateTime.now() + "] Clearing expired translation cache...");
        translationService.clearExpiredCache();
    }

    /**
     * 每天凌晨2点生成翻译统计报告
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateDailyStats() {
        System.out.println("[" + LocalDateTime.now() + "] Generating daily translation stats...");
        TranslationService.TranslationStats stats = translationService.getStats();
        System.out.println("Daily Stats - Total: " + stats.getTotalRequests() + 
                          ", Cache Hit Rate: " + String.format("%.2f%%", stats.getCacheHitRate() * 100));
    }
}
