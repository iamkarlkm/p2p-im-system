package com.im.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Search Service Configuration
 * 
 * 配置消息搜索服务的相关组件：
 * - Redis缓存（搜索结果缓存）
 * - 异步索引线程池
 * - 搜索配置参数
 */
@Configuration
@EnableAsync
public class SearchConfig {

    /**
     * 搜索结果缓存过期时间（秒）
     * 默认 5 分钟
     */
    public static final int SEARCH_CACHE_TTL_SECONDS = 300;

    /**
     * 搜索结果最大缓存条数
     */
    public static final int SEARCH_CACHE_MAX_SIZE = 1000;

    /**
     * 单次搜索最大返回条数
     */
    public static final int MAX_SEARCH_RESULTS = 100;

    /**
     * 搜索关键词最小长度
     */
    public static final int MIN_KEYWORD_LENGTH = 1;

    /**
     * 搜索关键词最大长度
     */
    public static final int MAX_KEYWORD_LENGTH = 100;

    /**
     * 搜索结果高亮标签前缀
     */
    public static final String HIGHLIGHT_PREFIX = "<em>";

    /**
     * 搜索结果高亮标签后缀
     */
    public static final String HIGHLIGHT_SUFFIX = "</em>";

    /**
     * 热门搜索缓存前缀
     */
    public static final String HOT_SEARCH_PREFIX = "im:hot_search:";

    /**
     * 搜索历史缓存前缀
     */
    public static final String SEARCH_HISTORY_PREFIX = "im:search_history:";

    /**
     * 搜索统计缓存前缀
     */
    public static final String SEARCH_STATS_PREFIX = "im:search_stats:";

    /**
     * 异步索引线程池
     */
    @Bean(name = "searchIndexExecutor")
    public Executor searchIndexExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("search-index-");
        executor.initialize();
        return executor;
    }

    /**
     * 搜索建议线程池
     */
    @Bean(name = "searchSuggestExecutor")
    public Executor searchSuggestExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("search-suggest-");
        executor.initialize();
        return executor;
    }
}
