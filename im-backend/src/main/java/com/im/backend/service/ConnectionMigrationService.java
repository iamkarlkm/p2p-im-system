package com.im.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket连接无感知迁移服务
 * 实现连接在集群节点间的平滑迁移，保证扩缩容时用户体验不中断
 */
@Service
public class ConnectionMigrationService {
    
    @Autowired
    private NodeManagementService nodeManagement;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private MessageQueueService messageQueue;
    
    // 迁移任务线程池
    private final ExecutorService migrationExecutor = Executors.newFixedThreadPool(4);
    
    // 活跃迁移任务
    private final Map<String, MigrationTask> activeMigrations = new ConcurrentHashMap<>();
    
    // 迁移历史统计
    private final AtomicLong totalMigrations = new AtomicLong(0);
    private final AtomicLong successfulMigrations = new AtomicLong(0);
    private final AtomicLong failedMigrations = new AtomicLong(0);
    private final AtomicLong recentMigrationCount = new AtomicLong(0);
    
    // 最近迁移计数重置调度
    private ScheduledExecutorService resetScheduler;
    
    // 迁移配置
    private static final int MIGRATION_BATCH_SIZE = 50;
    private static final long MIGRATION_BATCH_INTERVAL_MS = 100; // 批次间隔
    private static final int MAX_CONCURRENT_MIGRATIONS = 10;
    private static final long SESSION_STATE_SYNC_TIMEOUT_MS = 5000;
    private static final int MIGRATION_RETRY_ATTEMPTS = 3;
    
    /**
     * 迁移任务
     */
    public static class MigrationTask {
        public enum Status {
            PENDING, PREPARING, MIGRATING, COMPLETED, FAILED, CANCELLED
        }
        
        public String taskId;
        public String sourceNodeId;
        public String targetNodeId;
        public List<String> sessionIds;
        public Status status;
        public long startTime;
        public long endTime;
        public int totalSessions;
        public int migratedCount;
        public int failedCount;
        public String errorMessage;
        public Map<String, Object> context;
        
        public MigrationTask() {
            this.taskId = UUID.randomUUID().toString();
            this.startTime = System.currentTimeMillis();
            this.sessionIds = new ArrayList<>();
            this.context = new ConcurrentHashMap<>();
            this.status = Status.PENDING;
        }
        
        public long getDurationMs() {
            if (endTime > 0) {
                return endTime - startTime;
            }
            return System.currentTimeMillis() - startTime;
        }
        
        public double getProgressPercent() {
            if (totalSessions == 0) return 0;
            return (double) (migratedCount + failedCount) / totalSessions * 100;
        }
    }
    
    /**
     * 会话迁移上下文
     */
    public static class SessionMigrationContext {
        public String sessionId;
        public String userId;
        public String sourceNodeId;
        public String targetNodeId;
        public Map<String, Object> sessionState;
        public List<PendingMessage> pendingMessages;
        public long sequenceNumber;
        public boolean needsAck;
        
        public SessionMigrationContext(String sessionId, String userId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.sessionState = new HashMap<>();
            this.pendingMessages = new ArrayList<>();
        }
    }
    
    /**
     * 待处理消息
     */
    public static class PendingMessage {
        public String messageId;
        public String content;
        public long timestamp;
        public boolean delivered;
        
        public PendingMessage(String messageId, String content) {
            this.messageId = messageId;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * 迁移结果
     */
    public static class MigrationResult {
        public boolean success;
        public String taskId;
        public int totalSessions;
        public int migratedCount;
        public int failedCount;
        public long durationMs;
        public List<String> failedSessionIds;
        public String errorMessage;
        
        public MigrationResult() {
            this.failedSessionIds = new ArrayList<>();
        }
    }
    
    @PostConstruct
    public void init() {
        // 每小时重置最近迁移计数
        resetScheduler = Executors.newSingleThreadScheduledExecutor();
        resetScheduler.scheduleAtFixedRate(
            () -> recentMigrationCount.set(0),
            1, 1, TimeUnit.HOURS
        );
    }
    
    /**
     * 从指定节点迁移所有连接
     */
    public boolean migrateConnectionsFromNode(String sourceNodeId, long timeoutMs) {
        // 获取源节点的所有会话
        List<String> sessionIds = sessionManager.getSessionsOnNode(sourceNodeId);
        
        if (sessionIds.isEmpty()) {
            return true; // 没有连接需要迁移
        }
        
        // 创建迁移任务
        MigrationTask task = new MigrationTask();
        task.sourceNodeId = sourceNodeId;
        task.sessionIds = sessionIds;
        task.totalSessions = sessionIds.size();
        task.status = MigrationTask.Status.PENDING;
        
        activeMigrations.put(task.taskId, task);
        
        try {
            // 执行批量迁移
            MigrationResult result = executeBatchMigration(task, timeoutMs);
            
            if (result.success) {
                task.status = MigrationTask.Status.COMPLETED;
                successfulMigrations.addAndGet(result.migratedCount);
            } else {
                task.status = MigrationTask.Status.FAILED;
                task.errorMessage = result.errorMessage;
                failedMigrations.addAndGet(result.failedCount);
            }
            
            task.endTime = System.currentTimeMillis();
            totalMigrations.addAndGet(result.totalSessions);
            recentMigrationCount.addAndGet(result.migratedCount);
            
            return result.success;
            
        } finally {
            activeMigrations.remove(task.taskId);
        }
    }
    
    /**
     * 执行批量迁移
     */
    private MigrationResult executeBatchMigration(MigrationTask task, long timeoutMs) {
        MigrationResult result = new MigrationResult();
        result.taskId = task.taskId;
        result.totalSessions = task.totalSessions;
        
        task.status = MigrationTask.Status.PREPARING;
        
        // 选择目标节点
        Map<String, Integer> targetNodes = selectTargetNodes(task.sessionIds.size());
        if (targetNodes.isEmpty()) {
            result.success = false;
            result.errorMessage = "No available target nodes";
            return result;
        }
        
        task.status = MigrationTask.Status.MIGRATING;
        
        // 分批处理
        List<List<String>> batches = createBatches(task.sessionIds, MIGRATION_BATCH_SIZE);
        long deadline = System.currentTimeMillis() + timeoutMs;
        
        for (List<String> batch : batches) {
            if (System.currentTimeMillis() > deadline) {
                result.errorMessage = "Migration timeout";
                break;
            }
            
            // 并行处理批次内的迁移
            List<Future<Boolean>> futures = new ArrayList<>();
            
            for (String sessionId : batch) {
                String targetNode = selectTargetForSession(sessionId, targetNodes);
                Future<Boolean> future = migrationExecutor.submit(() -> 
                    migrateSingleSession(sessionId, task.sourceNodeId, targetNode));
                futures.add(future);
            }
            
            // 等待批次完成
            for (int i = 0; i < futures.size(); i++) {
                try {
                    boolean success = futures.get(i).get(10, TimeUnit.SECONDS);
                    if (success) {
                        result.migratedCount++;
                        task.migratedCount++;
                    } else {
                        result.failedCount++;
                        result.failedSessionIds.add(batch.get(i));
                        task.failedCount++;
                    }
                } catch (Exception e) {
                    result.failedCount++;
                    result.failedSessionIds.add(batch.get(i));
                    task.failedCount++;
                }
            }
            
            // 批次间隔，避免瞬时压力
            try {
                Thread.sleep(MIGRATION_BATCH_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        result.success = result.failedCount == 0;
        result.durationMs = task.getDurationMs();
        
        return result;
    }
    
    /**
     * 迁移单个会话
     */
    private boolean migrateSingleSession(String sessionId, String sourceNodeId, String targetNodeId) {
        int attempts = 0;
        
        while (attempts < MIGRATION_RETRY_ATTEMPTS) {
            attempts++;
            
            try {
                // 1. 获取会话信息
                WebSocketSession session = sessionManager.getSession(sessionId);
                if (session == null || !session.isOpen()) {
                    return true; // 会话已关闭，视为成功
                }
                
                String userId = sessionManager.getUserId(sessionId);
                
                // 2. 创建迁移上下文
                SessionMigrationContext context = new SessionMigrationContext(sessionId, userId);
                context.sourceNodeId = sourceNodeId;
                context.targetNodeId = targetNodeId;
                
                // 3. 收集会话状态
                collectSessionState(context);
                
                // 4. 暂停消息处理
                sessionManager.pauseSession(sessionId);
                
                // 5. 同步会话状态到目标节点
                boolean stateSynced = syncSessionStateToTarget(context);
                if (!stateSynced) {
                    sessionManager.resumeSession(sessionId);
                    continue; // 重试
                }
                
                // 6. 在目标节点创建会话
                String newSessionId = createSessionOnTarget(context);
                if (newSessionId == null) {
                    sessionManager.resumeSession(sessionId);
                    continue; // 重试
                }
                
                // 7. 更新路由表
                sessionManager.updateSessionRoute(sessionId, newSessionId, targetNodeId);
                
                // 8. 重定向客户端
                notifyClientMigration(session, targetNodeId, newSessionId);
                
                // 9. 清理旧会话
                sessionManager.closeSessionGracefully(sessionId);
                
                // 10. 恢复消息处理
                sessionManager.resumeSession(newSessionId);
                
                return true;
                
            } catch (Exception e) {
                if (attempts >= MIGRATION_RETRY_ATTEMPTS) {
                    recordError("Session migration failed after " + attempts + " attempts: " + sessionId, e);
                    return false;
                }
                
                // 指数退避重试
                try {
                    Thread.sleep(100 * (1 << attempts));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 收集会话状态
     */
    private void collectSessionState(SessionMigrationContext context) {
        // 获取会话属性
        Map<String, Object> attributes = sessionManager.getSessionAttributes(context.sessionId);
        context.sessionState.putAll(attributes);
        
        // 获取待处理消息
        List<PendingMessage> pendingMessages = messageQueue.getPendingMessages(context.userId);
        context.pendingMessages.addAll(pendingMessages);
        
        // 获取序列号
        context.sequenceNumber = sessionManager.getSequenceNumber(context.sessionId);
        
        // 是否需要确认
        context.needsAck = sessionManager.requiresAck(context.sessionId);
    }
    
    /**
     * 同步会话状态到目标节点
     */
    private boolean syncSessionStateToTarget(SessionMigrationContext context) {
        try {
            // 通过内部API同步状态
            return nodeManagement.syncSessionState(
                context.targetNodeId, 
                context.sessionId,
                context.sessionState,
                context.pendingMessages,
                context.sequenceNumber
            );
        } catch (Exception e) {
            recordError("Session state sync failed", e);
            return false;
        }
    }
    
    /**
     * 在目标节点创建会话
     */
    private String createSessionOnTarget(SessionMigrationContext context) {
        try {
            return nodeManagement.createSessionOnNode(
                context.targetNodeId,
                context.userId,
                context.sessionState
            );
        } catch (Exception e) {
            recordError("Create session on target failed", e);
            return null;
        }
    }
    
    /**
     * 通知客户端迁移
     */
    private void notifyClientMigration(WebSocketSession session, String targetNodeId, String newSessionId) {
        try {
            Map<String, Object> migrationMsg = new HashMap<>();
            migrationMsg.put("type", "MIGRATION");
            migrationMsg.put("targetNode", targetNodeId);
            migrationMsg.put("newSessionId", newSessionId);
            migrationMsg.put("timestamp", System.currentTimeMillis());
            
            String message = toJson(migrationMsg);
            session.sendMessage(new org.springframework.web.socket.TextMessage(message));
            
        } catch (Exception e) {
            recordError("Failed to notify client of migration", e);
        }
    }
    
    /**
     * 选择目标节点
     */
    private Map<String, Integer> selectTargetNodes(int totalSessions) {
        Map<String, Integer> targets = new HashMap<>();
        List<NodeManagementService.NodeInfo> nodes = nodeManagement.getActiveNodes();
        
        // 过滤可用节点（不包括源节点和draining节点）
        List<NodeManagementService.NodeInfo> availableNodes = nodes.stream()
            .filter(n -> n.isReady() && !n.isDraining())
            .sorted(Comparator.comparingInt(NodeManagementService.NodeInfo::getActiveConnections))
            .collect(java.util.stream.Collectors.toList());
        
        if (availableNodes.isEmpty()) {
            return targets;
        }
        
        // 计算每个节点的容量
        int sessionsPerNode = Math.max(1, totalSessions / availableNodes.size());
        
        for (NodeManagementService.NodeInfo node : availableNodes) {
            int availableSlots = node.getMaxConnections() - node.getActiveConnections();
            int allocation = Math.min(sessionsPerNode, availableSlots);
            if (allocation > 0) {
                targets.put(node.getNodeId(), allocation);
            }
        }
        
        return targets;
    }
    
    /**
     * 为会话选择目标节点
     */
    private String selectTargetForSession(String sessionId, Map<String, Integer> targetNodes) {
        // 基于会话ID的哈希选择，保持同一用户的会话路由到相同节点
        int hash = sessionId.hashCode();
        List<String> nodes = new ArrayList<>(targetNodes.keySet());
        return nodes.get(Math.abs(hash) % nodes.size());
    }
    
    /**
     * 创建批次
     */
    private List<List<String>> createBatches(List<String> items, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            batches.add(items.subList(i, Math.min(i + batchSize, items.size())));
        }
        return batches;
    }
    
    /**
     * 重新均衡连接
     */
    public void rebalanceConnections() {
        List<NodeManagementService.NodeInfo> nodes = nodeManagement.getActiveNodes();
        
        if (nodes.size() < 2) {
            return; // 不需要均衡
        }
        
        // 计算平均负载
        int totalConnections = nodes.stream()
            .mapToInt(NodeManagementService.NodeInfo::getActiveConnections)
            .sum();
        double avgConnections = (double) totalConnections / nodes.size();
        
        // 找出负载过高和过低的节点
        List<NodeManagementService.NodeInfo> overloadedNodes = nodes.stream()
            .filter(n -> n.getActiveConnections() > avgConnections * 1.3)
            .sorted((a, b) -> Integer.compare(b.getActiveConnections(), a.getActiveConnections()))
            .collect(java.util.stream.Collectors.toList());
        
        List<NodeManagementService.NodeInfo> underloadedNodes = nodes.stream()
            .filter(n -> n.getActiveConnections() < avgConnections * 0.7)
            .sorted(Comparator.comparingInt(NodeManagementService.NodeInfo::getActiveConnections))
            .collect(java.util.stream.Collectors.toList());
        
        // 从过载节点迁移到欠载节点
        for (NodeManagementService.NodeInfo overloaded : overloadedNodes) {
            if (underloadedNodes.isEmpty()) break;
            
            int excessConnections = (int) (overloaded.getActiveConnections() - avgConnections);
            int connectionsToMove = Math.min(excessConnections / 2, 20); // 每次最多移动20个
            
            if (connectionsToMove <= 0) continue;
            
            // 选择要移动的会话
            List<String> sessionsToMove = sessionManager.selectSessionsForMigration(
                overloaded.getNodeId(), connectionsToMove);
            
            for (String sessionId : sessionsToMove) {
                if (underloadedNodes.isEmpty()) break;
                
                NodeManagementService.NodeInfo target = underloadedNodes.get(0);
                
                // 异步执行迁移
                migrationExecutor.submit(() -> 
                    migrateSingleSession(sessionId, overloaded.getNodeId(), target.getNodeId()));
                
                // 更新计数
                target = new NodeManagementService.NodeInfo(target.getNodeId()) {{
                    // 模拟增加连接数
                }};
                
                if (target.getActiveConnections() >= avgConnections) {
                    underloadedNodes.remove(0);
                }
            }
        }
    }
    
    /**
     * 获取迁移统计
     */
    public MigrationStatistics getStatistics() {
        MigrationStatistics stats = new MigrationStatistics();
        stats.totalMigrations = totalMigrations.get();
        stats.successfulMigrations = successfulMigrations.get();
        stats.failedMigrations = failedMigrations.get();
        stats.recentMigrationCount = recentMigrationCount.get();
        stats.activeMigrationCount = activeMigrations.size();
        stats.successRate = totalMigrations.get() > 0 
            ? (double) successfulMigrations.get() / totalMigrations.get() * 100 
            : 0;
        return stats;
    }
    
    /**
     * 获取最近迁移计数
     */
    public long getRecentMigrationCount() {
        return recentMigrationCount.get();
    }
    
    /**
     * 获取活跃迁移任务
     */
    public List<MigrationTask> getActiveMigrations() {
        return new ArrayList<>(activeMigrations.values());
    }
    
    /**
     * 迁移统计
     */
    public static class MigrationStatistics {
        public long totalMigrations;
        public long successfulMigrations;
        public long failedMigrations;
        public long recentMigrationCount;
        public int activeMigrationCount;
        public double successRate;
    }
    
    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else {
                sb.append(value);
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    private void recordError(String message, Exception e) {
        System.err.println("[ConnectionMigrationService] " + message + ": " + e.getMessage());
    }
}