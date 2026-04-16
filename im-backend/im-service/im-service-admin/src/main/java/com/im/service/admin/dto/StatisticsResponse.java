package com.im.service.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计数据响应DTO
 * 返回系统各类统计数据
 *
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsResponse {

    // ========== 用户统计 ==========

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;

    /**
     * 活跃用户数 (今日)
     */
    private Long activeUsersToday;

    /**
     * 活跃用户数 (本周)
     */
    private Long activeUsersThisWeek;

    /**
     * 活跃用户数 (本月)
     */
    private Long activeUsersThisMonth;

    /**
     * 封禁用户数
     */
    private Long bannedUsers;

    /**
     * 在线用户数
     */
    private Long onlineUsers;

    // ========== 消息统计 ==========

    /**
     * 总消息数
     */
    private Long totalMessages;

    /**
     * 今日消息数
     */
    private Long todayMessages;

    /**
     * 本周消息数
     */
    private Long weekMessages;

    /**
     * 本月消息数
     */
    private Long monthMessages;

    /**
     * 今日私聊消息数
     */
    private Long todayPrivateMessages;

    /**
     * 今日群聊消息数
     */
    private Long todayGroupMessages;

    // ========== 群组统计 ==========

    /**
     * 总群组数
     */
    private Long totalGroups;

    /**
     * 今日新增群组数
     */
    private Long todayNewGroups;

    /**
     * 待审核群组数
     */
    private Long pendingAuditGroups;

    /**
     * 已解散群组数
     */
    private Long dissolvedGroups;

    /**
     * 活跃群组数 (有消息)
     */
    private Long activeGroups;

    // ========== 文件统计 ==========

    /**
     * 总文件数
     */
    private Long totalFiles;

    /**
     * 今日上传文件数
     */
    private Long todayUploadedFiles;

    /**
     * 总存储大小 (字节)
     */
    private Long totalStorageSize;

    /**
     * 格式化后的存储大小
     */
    private String formattedStorageSize;

    // ========== 好友关系统计 ==========

    /**
     * 总好友关系数
     */
    private Long totalFriendships;

    /**
     * 今日新增好友数
     */
    private Long todayNewFriendships;

    // ========== 会话统计 ==========

    /**
     * 总会话数
     */
    private Long totalConversations;

    /**
     * 活跃会话数
     */
    private Long activeConversations;

    // ========== 性能指标 ==========

    /**
     * 平均响应时间 (毫秒)
     */
    private Double avgResponseTime;

    /**
     * 峰值并发用户数
     */
    private Integer peakConcurrentUsers;

    /**
     * 系统 uptime (秒)
     */
    private Long systemUptime;

    // ========== 时间信息 ==========

    /**
     * 统计开始时间
     */
    private LocalDateTime statsStartTime;

    /**
     * 统计结束时间
     */
    private LocalDateTime statsEndTime;

    /**
     * 统计时间点
     */
    private LocalDateTime generatedAt;

    // ========== 趋势数据 ==========

    /**
     * 用户增长趋势 (每日数据)
     */
    private List<DailyTrend> userGrowthTrend;

    /**
     * 消息趋势 (每日数据)
     */
    private List<DailyTrend> messageTrend;

    /**
     * 活跃用户趋势 (每日数据)
     */
    private List<DailyTrend> activeUserTrend;

    // ========== 分布数据 ==========

    /**
     * 用户设备分布
     */
    private Map<String, Long> userDeviceDistribution;

    /**
     * 消息类型分布
     */
    private Map<String, Long> messageTypeDistribution;

    /**
     * 文件类型分布
     */
    private Map<String, Long> fileTypeDistribution;

    // ========== 内部类 ==========

    /**
     * 每日趋势数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrend {
        private String date;
        private Long value;
        private Double changeRate;
    }

    /**
     * 统计摘要
     */
    public static class Summary {
        private Long totalUsers;
        private Long totalMessages;
        private Long totalGroups;
        private Long totalFiles;
        private Long onlineUsers;
        private LocalDateTime generatedAt;

        public Summary(Long totalUsers, Long totalMessages, Long totalGroups, 
                       Long totalFiles, Long onlineUsers) {
            this.totalUsers = totalUsers;
            this.totalMessages = totalMessages;
            this.totalGroups = totalGroups;
            this.totalFiles = totalFiles;
            this.onlineUsers = onlineUsers;
            this.generatedAt = LocalDateTime.now();
        }

        // Getters
        public Long getTotalUsers() { return totalUsers; }
        public Long getTotalMessages() { return totalMessages; }
        public Long getTotalGroups() { return totalGroups; }
        public Long getTotalFiles() { return totalFiles; }
        public Long getOnlineUsers() { return onlineUsers; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
    }

    // ========== 便捷方法 ==========

    /**
     * 创建简单统计响应
     */
    public static StatisticsResponse simple(Long users, Long messages, Long groups, Long files, Long online) {
        return StatisticsResponse.builder()
                .totalUsers(users)
                .totalMessages(messages)
                .totalGroups(groups)
                .totalFiles(files)
                .onlineUsers(online)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 格式化存储大小
     */
    public static String formatStorageSize(Long bytes) {
        if (bytes == null || bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
