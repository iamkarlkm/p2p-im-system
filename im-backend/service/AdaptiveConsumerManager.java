package com.im.backend.service;

import com.im.backend.model.kafka.ConsumerMetrics;
import com.im.backend.model.kafka.ConsumerGroupInfo;
import com.im.backend.model.kafka.ConsumerScalingDecision;
import com.im.backend.model.kafka.TopicMetrics;
import com.im.backend.repository.ConsumerMetricsRepository;
import com.im.backend.repository.TopicMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 自适应消费者管理服务
 * 根据消息队列负载动态调整消费者数量
 * 实现消费者自动扩缩容、健康检查、故障恢复、优雅下线等功能
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class AdaptiveConsumerManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AdaptiveConsumerManager.class);
    
    // 扩缩容配置
    private static final long SCALING_CHECK_INTERVAL_MS = 15000; // 15秒检查一次
    private static final long HEALTH_CHECK_INTERVAL_MS = 10000; // 10秒健康检查
    private static final long SCALE_UP_COOLDOWN_MS = 60000; // 扩容冷却期1分钟
    private static final long SCALE_DOWN_COOLDOWN_MS = 180000; // 缩容冷却期3分钟
    private static final double SCALE_UP_THRESHOLD = 0.75; // 扩容阈值75%
    private static final double SCALE_DOWN_THRESHOLD = 0.25; // 缩容阈值25%
    private static final int MIN_CONSUMERS = 2; // 最小消费者数
    private static final int MAX_CONSUMERS = 50; // 最大消费者数
    private static final int SCALE_UP_STEP = 2; // 扩容步长
    private static final int SCALE_DOWN_STEP = 1; // 缩容步长
    private static final long MESSAGE_LAG_THRESHOLD = 5000; // 消息积压阈值
    
    @Autowired
    private ConsumerMetricsRepository consumerMetricsRepo;
    
    @Autowired
    private TopicMetricsRepository topicMetricsRepo;
    
    @Autowired
    private AILoadPredictionService predictionService;
    
    @Autowired
    private IntelligentPartitionScheduler partitionScheduler;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final ExecutorService scalingExecutor = Executors.newFixedThreadPool(4);
    
    // 运行状态
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    // 消费者组状态
    private final ConcurrentHashMap<String, ConsumerGroupState> groupStates = new ConcurrentHashMap<>();
    
    // 扩缩容历史
    private final Queue<ScalingEvent> scalingHistory = new ConcurrentLinkedQueue<>();
    private static final int MAX_HISTORY_SIZE = 200;
    
    // 冷却期追踪
    private final ConcurrentHashMap<String, ScalingCooldown> cooldownTracker = new ConcurrentHashMap<>();
    
    // 消费者实例管理器
    private final AtomicInteger consumerIdCounter = new AtomicInteger(0);
    private final ConcurrentHashMap<String, ConsumerInstance> consumerInstances = new ConcurrentHashMap<>();
    
    /**
     * 启动管理服务
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("启动自适应消费者管理服务...");
            
            // 负载监控任务
            scheduler.scheduleAtFixedRate(
                this::monitorAndScale,
                5000,
                SCALING_CHECK_INTERVAL_MS,
                TimeUnit.MILLISECONDS
            );
            
            // 健康检查任务
            scheduler.scheduleAtFixedRate(
                this::performHealthCheck,
                3000,
                HEALTH_CHECK_INTERVAL_MS,
                TimeUnit.MILLISECONDS
            );
            
            // 消费者池维护任务
            scheduler.scheduleAtFixedRate(
                this::maintainConsumerPool,
                60000,
                60000,
                TimeUnit.MILLISECONDS
            );
            
            logger.info("自适应消费者管理服务已启动");
        }
    }
    
    /**
     * 停止管理服务
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("停止自适应消费者管理服务...");
            scheduler.shutdown();
            scalingExecutor.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
                scalingExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("自适应消费者管理服务已停止");
        }
    }
    
    /**
     * 监控并执行扩缩容
     */
    private void monitorAndScale() {
        if (!isRunning.get()) return;
        
        try {
            // 获取所有消费者组
            List<ConsumerGroupInfo> groups = consumerMetricsRepo.findAllConsumerGroups();
            
            for (ConsumerGroupInfo group : groups) {
                String groupId = group.getGroupId();
                String topic = group.getTopic();
                
                // 检查冷却期
                if (isInCooldown(groupId)) {
                    continue;
                }
                
                // 获取当前消费者列表
                List<ConsumerMetrics> consumers = consumerMetricsRepo.findByGroupId(groupId);
                int currentCount = consumers.size();
                
                // 获取Topic指标
                TopicMetrics topicMetrics = topicMetricsRepo.findByTopic(topic);
                if (topicMetrics == null) {
                    continue;
                }
                
                // 计算负载指标
                LoadMetrics loadMetrics = calculateLoadMetrics(consumers, topicMetrics);
                
                // 决策扩缩容
                ScalingDecision decision = makeScalingDecision(
                    groupId, topic, currentCount, loadMetrics
                );
                
                // 执行决策
                if (decision != ScalingDecision.NO_ACTION) {
                    executeScalingDecision(groupId, topic, decision, loadMetrics);
                }
                
                // 更新组状态
                updateGroupState(groupId, currentCount, loadMetrics);
            }
            
        } catch (Exception e) {
            logger.error("监控扩缩容失败", e);
        }
    }
    
    /**
     * 计算负载指标
     */
    private LoadMetrics calculateLoadMetrics(List<ConsumerMetrics> consumers, TopicMetrics topic) {
        if (consumers.isEmpty()) {
            return new LoadMetrics(0, 0, 0, 0, 0);
        }
        
        // 计算平均CPU使用率
        double avgCpuUsage = consumers.stream()
            .mapToDouble(ConsumerMetrics::getCpuUsage)
            .average()
            .orElse(0);
        
        // 计算平均内存使用率
        double avgMemoryUsage = consumers.stream()
            .mapToDouble(ConsumerMetrics::getMemoryUsage)
            .average()
            .orElse(0);
        
        // 计算总消息处理速率
        long totalProcessingRate = consumers.stream()
            .mapToLong(ConsumerMetrics::getMessagesPerSecond)
            .sum();
        
        // 计算消息积压
        long messageLag = topic.getTotalMessageLag();
        
        // 计算消费者利用率
        double consumerUtilization = calculateConsumerUtilization(consumers);
        
        return new LoadMetrics(avgCpuUsage, avgMemoryUsage, totalProcessingRate, messageLag, consumerUtilization);
    }
    
    /**
     * 计算消费者利用率
     */
    private double calculateConsumerUtilization(List<ConsumerMetrics> consumers) {
        if (consumers.isEmpty()) return 0;
        
        int activeConsumers = (int) consumers.stream()
            .filter(c -> c.getState() == ConsumerMetrics.ConsumerState.ACTIVE)
            .count();
        
        int busyConsumers = (int) consumers.stream()
            .filter(c -> c.getMessagesPerSecond() > c.getCapacity() * 0.7)
            .count();
        
        return (double) busyConsumers / activeConsumers;
    }
    
    /**
     * 做出扩缩容决策
     */
    private ScalingDecision makeScalingDecision(String groupId, String topic, 
                                                 int currentCount, LoadMetrics metrics) {
        // 获取AI预测结果
        double predictedLoad = getPredictedLoad(topic);
        
        // 决策逻辑
        boolean shouldScaleUp = false;
        boolean shouldScaleDown = false;
        String reason = "";
        
        // 条件1: CPU使用率超过阈值
        if (metrics.getAvgCpuUsage() > SCALE_UP_THRESHOLD) {
            shouldScaleUp = true;
            reason = String.format("CPU使用率过高 (%.1f%%)", metrics.getAvgCpuUsage() * 100);
        }
        
        // 条件2: 消息积压超过阈值
        if (metrics.getMessageLag() > MESSAGE_LAG_THRESHOLD * currentCount) {
            shouldScaleUp = true;
            reason = String.format("消息积压严重 (%d)", metrics.getMessageLag());
        }
        
        // 条件3: 消费者利用率过高
        if (metrics.getConsumerUtilization() > SCALE_UP_THRESHOLD) {
            shouldScaleUp = true;
            reason = String.format("消费者利用率过高 (%.1f%%)", metrics.getConsumerUtilization() * 100);
        }
        
        // 条件4: AI预测显示负载将增加
        if (predictedLoad > SCALE_UP_THRESHOLD && currentCount < MAX_CONSUMERS) {
            shouldScaleUp = true;
            reason = String.format("AI预测负载将增加 (%.1f%%)", predictedLoad * 100);
        }
        
        // 缩容条件
        if (currentCount > MIN_CONSUMERS) {
            boolean lowCpu = metrics.getAvgCpuUsage() < SCALE_DOWN_THRESHOLD;
            boolean lowLag = metrics.getMessageLag() < MESSAGE_LAG_THRESHOLD / 2;
            boolean lowUtil = metrics.getConsumerUtilization() < SCALE_DOWN_THRESHOLD;
            
            if (lowCpu && lowLag && lowUtil) {
                shouldScaleDown = true;
                reason = "负载持续低迷";
            }
            
            // AI预测显示负载将下降
            if (predictedLoad < SCALE_DOWN_THRESHOLD && lowUtil) {
                shouldScaleDown = true;
                reason = String.format("AI预测负载将下降 (%.1f%%)", predictedLoad * 100);
            }
        }
        
        // 边界检查
        if (shouldScaleUp && currentCount >= MAX_CONSUMERS) {
            shouldScaleUp = false;
            logger.warn("消费者组 [{}] 已达到最大消费者数限制 {}", groupId, MAX_CONSUMERS);
        }
        
        if (shouldScaleDown && currentCount <= MIN_CONSUMERS) {
            shouldScaleDown = false;
        }
        
        // 返回决策
        if (shouldScaleUp) {
            return new ScalingDecision(ScalingAction.SCALE_UP, SCALE_UP_STEP, reason);
        } else if (shouldScaleDown) {
            return new ScalingDecision(ScalingAction.SCALE_DOWN, SCALE_DOWN_STEP, reason);
        }
        
        return ScalingDecision.NO_ACTION;
    }
    
    /**
     * 获取AI预测的负载
     */
    private double getPredictedLoad(String topic) {
        try {
            // 获取所有分区的预测
            Map<Integer, AILoadPredictionService.LoadForecast> forecasts = 
                predictionService.getTopicForecasts(topic);
            
            if (forecasts.isEmpty()) {
                return 0.5; // 默认中等负载
            }
            
            // 计算平均预测负载
            double avgLoad = forecasts.values().stream()
                .mapToDouble(f -> {
                    List<Double> predictedLag = f.getPredictedLag();
                    if (predictedLag.isEmpty()) return 0;
                    return predictedLag.get(0) / MESSAGE_LAG_THRESHOLD;
                })
                .average()
                .orElse(0.5);
            
            return Math.min(1.0, Math.max(0, avgLoad));
            
        } catch (Exception e) {
            logger.error("获取AI预测负载失败", e);
            return 0.5;
        }
    }
    
    /**
     * 执行扩缩容决策
     */
    private void executeScalingDecision(String groupId, String topic, 
                                        ScalingDecision decision, LoadMetrics metrics) {
        try {
            logger.info("执行扩缩容决策: Group={}, Action={}, Count={}, Reason={}",
                groupId, decision.getAction(), decision.getCount(), decision.getReason());
            
            long startTime = System.currentTimeMillis();
            
            if (decision.getAction() == ScalingAction.SCALE_UP) {
                // 扩容
                scaleUp(groupId, topic, decision.getCount());
                setCooldown(groupId, ScalingAction.SCALE_UP);
            } else if (decision.getAction() == ScalingAction.SCALE_DOWN) {
                // 缩容
                scaleDown(groupId, topic, decision.getCount());
                setCooldown(groupId, ScalingAction.SCALE_DOWN);
            }
            
            // 记录事件
            ScalingEvent event = new ScalingEvent(
                groupId,
                topic,
                decision.getAction(),
                decision.getCount(),
                decision.getReason(),
                Instant.now(),
                System.currentTimeMillis() - startTime
            );
            recordScalingEvent(event);
            
            logger.info("扩缩容完成: Group={}, 耗时={}ms", groupId, 
                System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            logger.error("执行扩缩容失败: Group={}", groupId, e);
        }
    }
    
    /**
     * 扩容操作
     */
    private void scaleUp(String groupId, String topic, int count) {
        logger.info("扩容消费者: Group={}, 新增={}", groupId, count);
        
        for (int i = 0; i < count; i++) {
            String consumerId = generateConsumerId(groupId);
            
            try {
                // 创建消费者实例
                ConsumerInstance instance = createConsumerInstance(consumerId, groupId, topic);
                consumerInstances.put(consumerId, instance);
                
                // 启动消费者
                instance.start();
                
                logger.info("新消费者已启动: ConsumerId={}", consumerId);
                
            } catch (Exception e) {
                logger.error("创建消费者失败: ConsumerId={}", consumerId, e);
            }
        }
        
        // 触发分区重平衡
        partitionScheduler.triggerManualRebalance(topic);
    }
    
    /**
     * 缩容操作
     */
    private void scaleDown(String groupId, String topic, int count) {
        logger.info("缩容消费者: Group={}, 减少={}", groupId, count);
        
        // 获取该组的所有消费者
        List<ConsumerInstance> groupConsumers = consumerInstances.values().stream()
            .filter(c -> c.getGroupId().equals(groupId))
            .filter(c -> c.getState() == ConsumerState.ACTIVE)
            .sorted(Comparator.comparingLong(ConsumerInstance::getMessageLag))
            .collect(Collectors.toList());
        
        // 选择负载最低的消费者进行下线
        int removed = 0;
        for (ConsumerInstance consumer : groupConsumers) {
            if (removed >= count) break;
            
            try {
                // 优雅下线
                gracefulShutdownConsumer(consumer);
                removed++;
                
                logger.info("消费者已优雅下线: ConsumerId={}", consumer.getConsumerId());
                
            } catch (Exception e) {
                logger.error("下线消费者失败: ConsumerId={}", consumer.getConsumerId(), e);
            }
        }
        
        // 触发分区重平衡
        if (removed > 0) {
            partitionScheduler.triggerManualRebalance(topic);
        }
    }
    
    /**
     * 创建消费者实例
     */
    private ConsumerInstance createConsumerInstance(String consumerId, String groupId, String topic) {
        ConsumerInstance instance = new ConsumerInstance(consumerId, groupId, topic);
        instance.setState(ConsumerState.INITIALIZING);
        return instance;
    }
    
    /**
     * 优雅下线消费者
     */
    private void gracefulShutdownConsumer(ConsumerInstance consumer) {
        logger.info("开始优雅下线消费者: ConsumerId={}", consumer.getConsumerId());
        
        // 1. 标记为 draining 状态
        consumer.setState(ConsumerState.DRAINING);
        
        // 2. 等待当前消息处理完成（最多30秒）
        long startTime = System.currentTimeMillis();
        while (consumer.hasPendingMessages() && 
               System.currentTimeMillis() - startTime < 30000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // 3. 停止消费者
        consumer.stop();
        
        // 4. 从管理器中移除
        consumerInstances.remove(consumer.getConsumerId());
    }
    
    /**
     * 执行健康检查
     */
    private void performHealthCheck() {
        try {
            for (ConsumerInstance consumer : consumerInstances.values()) {
                // 检查消费者健康状态
                HealthStatus status = checkConsumerHealth(consumer);
                
                if (status == HealthStatus.UNHEALTHY) {
                    logger.warn("检测到不健康消费者: ConsumerId={}, 将执行恢复",
                        consumer.getConsumerId());
                    
                    // 执行恢复
                    recoverConsumer(consumer);
                    
                } else if (status == HealthStatus.DEGRADED) {
                    logger.warn("消费者性能降级: ConsumerId={}", consumer.getConsumerId());
                    
                    // 降级处理
                    handleDegradedConsumer(consumer);
                }
            }
            
        } catch (Exception e) {
            logger.error("健康检查失败", e);
        }
    }
    
    /**
     * 检查消费者健康
     */
    private HealthStatus checkConsumerHealth(ConsumerInstance consumer) {
        ConsumerMetrics metrics = consumerMetricsRepo.findByConsumerId(consumer.getConsumerId());
        
        if (metrics == null) {
            return HealthStatus.UNHEALTHY;
        }
        
        // 检查心跳
        long lastHeartbeat = metrics.getLastHeartbeat();
        long heartbeatTimeout = 30000; // 30秒
        
        if (System.currentTimeMillis() - lastHeartbeat > heartbeatTimeout) {
            return HealthStatus.UNHEALTHY;
        }
        
        // 检查错误率
        if (metrics.getErrorRate() > 0.1) { // 错误率超过10%
            return HealthStatus.DEGRADED;
        }
        
        // 检查处理延迟
        if (metrics.getAverageProcessingLatency() > 5000) { // 延迟超过5秒
            return HealthStatus.DEGRADED;
        }
        
        // 检查资源使用
        if (metrics.getCpuUsage() > 0.95 || metrics.getMemoryUsage() > 0.95) {
            return HealthStatus.DEGRADED;
        }
        
        return HealthStatus.HEALTHY;
    }
    
    /**
     * 恢复消费者
     */
    private void recoverConsumer(ConsumerInstance consumer) {
        logger.info("恢复消费者: ConsumerId={}", consumer.getConsumerId());
        
        try {
            // 1. 尝试重启
            consumer.restart();
            
            // 2. 如果重启失败，创建新消费者替代
            if (consumer.getState() != ConsumerState.ACTIVE) {
                String newConsumerId = generateConsumerId(consumer.getGroupId());
                ConsumerInstance newInstance = createConsumerInstance(
                    newConsumerId, consumer.getGroupId(), consumer.getTopic()
                );
                
                consumerInstances.remove(consumer.getConsumerId());
                consumerInstances.put(newConsumerId, newInstance);
                newInstance.start();
                
                // 触发重平衡
                partitionScheduler.triggerManualRebalance(consumer.getTopic());
            }
            
        } catch (Exception e) {
            logger.error("恢复消费者失败: ConsumerId={}", consumer.getConsumerId(), e);
        }
    }
    
    /**
     * 处理降级消费者
     */
    private void handleDegradedConsumer(ConsumerInstance consumer) {
        // 减少分配给该消费者的分区数
        // 或者迁移部分分区到其他消费者
        logger.info("处理降级消费者: ConsumerId={}", consumer.getConsumerId());
    }
    
    /**
     * 维护消费者池
     */
    private void maintainConsumerPool() {
        try {
            // 清理已停止的消费者
            consumerInstances.entrySet().removeIf(entry -> {
                ConsumerInstance consumer = entry.getValue();
                if (consumer.getState() == ConsumerState.STOPPED) {
                    logger.debug("清理已停止的消费者: ConsumerId={}", consumer.getConsumerId());
                    return true;
                }
                return false;
            });
            
            // 清理过期历史记录
            while (scalingHistory.size() > MAX_HISTORY_SIZE) {
                scalingHistory.poll();
            }
            
        } catch (Exception e) {
            logger.error("维护消费者池失败", e);
        }
    }
    
    /**
     * 检查是否在冷却期
     */
    private boolean isInCooldown(String groupId) {
        ScalingCooldown cooldown = cooldownTracker.get(groupId);
        if (cooldown == null) return false;
        
        return System.currentTimeMillis() < cooldown.getExpiryTime();
    }
    
    /**
     * 设置冷却期
     */
    private void setCooldown(String groupId, ScalingAction action) {
        long cooldownMs = action == ScalingAction.SCALE_UP ? 
            SCALE_UP_COOLDOWN_MS : SCALE_DOWN_COOLDOWN_MS;
        
        cooldownTracker.put(groupId, new ScalingCooldown(
            groupId, action, System.currentTimeMillis() + cooldownMs
        ));
    }
    
    /**
     * 更新组状态
     */
    private void updateGroupState(String groupId, int consumerCount, LoadMetrics metrics) {
        ConsumerGroupState state = new ConsumerGroupState(
            groupId, consumerCount, metrics, Instant.now()
        );
        groupStates.put(groupId, state);
    }
    
    /**
     * 记录扩缩容事件
     */
    private void recordScalingEvent(ScalingEvent event) {
        scalingHistory.offer(event);
    }
    
    /**
     * 生成消费者ID
     */
    private String generateConsumerId(String groupId) {
        return String.format("%s-consumer-%d-%d", 
            groupId, System.currentTimeMillis(), consumerIdCounter.incrementAndGet());
    }
    
    // ============ 公共API ============
    
    /**
     * 获取消费者组状态
     */
    public ConsumerGroupState getGroupState(String groupId) {
        return groupStates.get(groupId);
    }
    
    /**
     * 获取所有消费者组状态
     */
    public Map<String, ConsumerGroupState> getAllGroupStates() {
        return new HashMap<>(groupStates);
    }
    
    /**
     * 获取扩缩容历史
     */
    public List<ScalingEvent> getScalingHistory(int limit) {
        return scalingHistory.stream()
            .sorted(Comparator.comparing(ScalingEvent::getTimestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 手动触发扩容
     */
    public boolean manualScaleUp(String groupId, int count) {
        ConsumerGroupState state = groupStates.get(groupId);
        if (state == null) {
            return false;
        }
        
        try {
            scaleUp(groupId, state.getTopic(), count);
            return true;
        } catch (Exception e) {
            logger.error("手动扩容失败: Group={}", groupId, e);
            return false;
        }
    }
    
    /**
     * 手动触发缩容
     */
    public boolean manualScaleDown(String groupId, int count) {
        ConsumerGroupState state = groupStates.get(groupId);
        if (state == null) {
            return false;
        }
        
        try {
            scaleDown(groupId, state.getTopic(), count);
            return true;
        } catch (Exception e) {
            logger.error("手动缩容失败: Group={}", groupId, e);
            return false;
        }
    }
    
    // ============ 内部类 ============
    
    /**
     * 消费者状态
     */
    public enum ConsumerState {
        INITIALIZING,   // 初始化中
        ACTIVE,         // 活跃
        DRAINING,       // 排空（准备下线）
        STOPPED,        // 已停止
        ERROR           // 错误
    }
    
    /**
     * 健康状态
     */
    private enum HealthStatus {
        HEALTHY,    // 健康
        DEGRADED,   // 降级
        UNHEALTHY   // 不健康
    }
    
    /**
     * 扩缩容动作
     */
    public enum ScalingAction {
        SCALE_UP,    // 扩容
        SCALE_DOWN,  // 缩容
        NO_ACTION    // 无操作
    }
    
    /**
     * 负载指标
     */
    public static class LoadMetrics {
        private final double avgCpuUsage;
        private final double avgMemoryUsage;
        private final long totalProcessingRate;
        private final long messageLag;
        private final double consumerUtilization;
        
        public LoadMetrics(double avgCpuUsage, double avgMemoryUsage, 
                           long totalProcessingRate, long messageLag, 
                           double consumerUtilization) {
            this.avgCpuUsage = avgCpuUsage;
            this.avgMemoryUsage = avgMemoryUsage;
            this.totalProcessingRate = totalProcessingRate;
            this.messageLag = messageLag;
            this.consumerUtilization = consumerUtilization;
        }
        
        public double getAvgCpuUsage() { return avgCpuUsage; }
        public double getAvgMemoryUsage() { return avgMemoryUsage; }
        public long getTotalProcessingRate() { return totalProcessingRate; }
        public long getMessageLag() { return messageLag; }
        public double getConsumerUtilization() { return consumerUtilization; }
    }
    
    /**
     * 扩缩容决策
     */
    public static class ScalingDecision {
        public static final ScalingDecision NO_ACTION = 
            new ScalingDecision(ScalingAction.NO_ACTION, 0, "");
        
        private final ScalingAction action;
        private final int count;
        private final String reason;
        
        public ScalingDecision(ScalingAction action, int count, String reason) {
            this.action = action;
            this.count = count;
            this.reason = reason;
        }
        
        public ScalingAction getAction() { return action; }
        public int getCount() { return count; }
        public String getReason() { return reason; }
    }
    
    /**
     * 扩缩容事件
     */
    public static class ScalingEvent {
        private final String groupId;
        private final String topic;
        private final ScalingAction action;
        private final int count;
        private final String reason;
        private final Instant timestamp;
        private final long durationMs;
        
        public ScalingEvent(String groupId, String topic, ScalingAction action,
                            int count, String reason, Instant timestamp, long durationMs) {
            this.groupId = groupId;
            this.topic = topic;
            this.action = action;
            this.count = count;
            this.reason = reason;
            this.timestamp = timestamp;
            this.durationMs = durationMs;
        }
        
        public String getGroupId() { return groupId; }
        public String getTopic() { return topic; }
        public ScalingAction getAction() { return action; }
        public int getCount() { return count; }
        public String getReason() { return reason; }
        public Instant getTimestamp() { return timestamp; }
        public long getDurationMs() { return durationMs; }
    }
    
    /**
     * 冷却期追踪
     */
    private static class ScalingCooldown {
        private final String groupId;
        private final ScalingAction lastAction;
        private final long expiryTime;
        
        public ScalingCooldown(String groupId, ScalingAction lastAction, long expiryTime) {
            this.groupId = groupId;
            this.lastAction = lastAction;
            this.expiryTime = expiryTime;
        }
        
        public long getExpiryTime() { return expiryTime; }
    }
    
    /**
     * 消费者组状态
     */
    public static class ConsumerGroupState {
        private final String groupId;
        private final int consumerCount;
        private final LoadMetrics loadMetrics;
        private final Instant lastUpdate;
        private String topic;
        
        public ConsumerGroupState(String groupId, int consumerCount, 
                                   LoadMetrics loadMetrics, Instant lastUpdate) {
            this.groupId = groupId;
            this.consumerCount = consumerCount;
            this.loadMetrics = loadMetrics;
            this.lastUpdate = lastUpdate;
        }
        
        public String getGroupId() { return groupId; }
        public int getConsumerCount() { return consumerCount; }
        public LoadMetrics getLoadMetrics() { return loadMetrics; }
        public Instant getLastUpdate() { return lastUpdate; }
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }
    
    /**
     * 消费者实例
     */
    private static class ConsumerInstance {
        private final String consumerId;
        private final String groupId;
        private final String topic;
        private volatile ConsumerState state = ConsumerState.INITIALIZING;
        private volatile long messageLag = 0;
        
        public ConsumerInstance(String consumerId, String groupId, String topic) {
            this.consumerId = consumerId;
            this.groupId = groupId;
            this.topic = topic;
        }
        
        public String getConsumerId() { return consumerId; }
        public String getGroupId() { return groupId; }
        public String getTopic() { return topic; }
        public ConsumerState getState() { return state; }
        public void setState(ConsumerState state) { this.state = state; }
        public long getMessageLag() { return messageLag; }
        public void setMessageLag(long lag) { this.messageLag = lag; }
        
        public void start() {
            state = ConsumerState.ACTIVE;
            // 实际启动逻辑
        }
        
        public void stop() {
            state = ConsumerState.STOPPED;
            // 实际停止逻辑
        }
        
        public void restart() {
            stop();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            start();
        }
        
        public boolean hasPendingMessages() {
            return messageLag > 0;
        }
    }
}
