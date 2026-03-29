package com.im.backend.service;

import com.im.backend.model.kafka.PartitionMetrics;
import com.im.backend.model.kafka.ConsumerMetrics;
import com.im.backend.model.kafka.TopicMetrics;
import com.im.backend.model.kafka.PartitionAssignment;
import com.im.backend.model.kafka.SchedulingDecision;
import com.im.backend.repository.PartitionMetricsRepository;
import com.im.backend.repository.ConsumerMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 智能分区调度服务
 * 基于负载动态调整分区与消费者之间的映射关系
 * 实现分区热点检测、负载均衡重分配、分区合并与分裂等功能
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class IntelligentPartitionScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(IntelligentPartitionScheduler.class);
    
    // 调度配置参数
    private static final long SCHEDULING_INTERVAL_MS = 30000; // 30秒调度周期
    private static final long HOTSPOT_THRESHOLD_LAG = 10000; // 热点分区消息积压阈值
    private static final double HOTSPOT_THRESHOLD_CPU = 0.8; // 热点CPU使用率阈值
    private static final long REBALANCE_COOLDOWN_MS = 60000; // 重平衡冷却期
    private static final double IMBALANCE_THRESHOLD = 0.3; // 负载不均衡阈值
    private static final int MAX_PARTITIONS_PER_CONSUMER = 8; // 每个消费者最大分区数
    private static final int MIN_PARTITIONS_PER_CONSUMER = 1; // 每个消费者最小分区数
    
    @Autowired
    private PartitionMetricsRepository partitionMetricsRepo;
    
    @Autowired
    private ConsumerMetricsRepository consumerMetricsRepo;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(4);
    
    // 调度状态
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicLong lastRebalanceTime = new AtomicLong(0);
    private final AtomicReference<Map<String, PartitionAssignment>> currentAssignments = 
        new AtomicReference<>(new ConcurrentHashMap<>());
    
    // 调度历史
    private final Queue<SchedulingDecision> decisionHistory = new ConcurrentLinkedQueue<>();
    private static final int MAX_HISTORY_SIZE = 100;
    
    // 分区热点检测状态
    private final Map<Integer, HotspotDetectionResult> hotspotCache = new ConcurrentHashMap<>();
    private static final long HOTSPOT_CACHE_TTL_MS = 60000;
    
    /**
     * 启动调度服务
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("启动智能分区调度服务...");
            
            // 调度任务
            scheduler.scheduleAtFixedRate(
                this::performScheduling,
                SCHEDULING_INTERVAL_MS,
                SCHEDULING_INTERVAL_MS,
                TimeUnit.MILLISECONDS
            );
            
            // 热点检测任务
            scheduler.scheduleAtFixedRate(
                this::detectHotspots,
                10000,
                10000,
                TimeUnit.MILLISECONDS
            );
            
            logger.info("智能分区调度服务已启动，调度周期: {}ms", SCHEDULING_INTERVAL_MS);
        }
    }
    
    /**
     * 停止调度服务
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("停止智能分区调度服务...");
            scheduler.shutdown();
            taskExecutor.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    taskExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                taskExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("智能分区调度服务已停止");
        }
    }
    
    /**
     * 执行调度
     */
    private void performScheduling() {
        if (!isRunning.get()) return;
        
        try {
            long startTime = System.currentTimeMillis();
            logger.debug("开始执行分区调度...");
            
            // 检查冷却期
            long lastRebalance = lastRebalanceTime.get();
            if (System.currentTimeMillis() - lastRebalance < REBALANCE_COOLDOWN_MS) {
                logger.debug("处于重平衡冷却期，跳过本次调度");
                return;
            }
            
            // 获取所有Topic的指标
            List<TopicMetrics> topicMetrics = getAllTopicMetrics();
            
            for (TopicMetrics topic : topicMetrics) {
                // 分析分区负载
                List<PartitionMetrics> partitions = partitionMetricsRepo.findByTopic(topic.getTopicName());
                List<ConsumerMetrics> consumers = consumerMetricsRepo.findActiveConsumers(topic.getTopicName());
                
                if (partitions.isEmpty() || consumers.isEmpty()) {
                    continue;
                }
                
                // 评估当前分配的健康度
                AssignmentHealth health = evaluateAssignmentHealth(topic.getTopicName(), partitions, consumers);
                
                // 如果需要重平衡
                if (health.needsRebalance()) {
                    logger.info("Topic [{}] 需要重平衡，原因: {}", topic.getTopicName(), health.getReason());
                    
                    // 计算新的分配方案
                    Map<String, List<Integer>> newAssignment = calculateOptimalAssignment(
                        topic.getTopicName(), partitions, consumers
                    );
                    
                    // 执行重平衡
                    if (newAssignment != null && !newAssignment.isEmpty()) {
                        executeRebalance(topic.getTopicName(), newAssignment, health.getReason());
                    }
                }
                
                // 检查是否需要分区合并或分裂
                checkPartitionScaling(topic.getTopicName(), partitions, consumers);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("分区调度完成，耗时: {}ms", duration);
            
        } catch (Exception e) {
            logger.error("分区调度执行失败", e);
        }
    }
    
    /**
     * 计算最优分区分配
     */
    private Map<String, List<Integer>> calculateOptimalAssignment(
            String topic,
            List<PartitionMetrics> partitions,
            List<ConsumerMetrics> consumers) {
        
        // 按处理能力对消费者排序
        List<ConsumerMetrics> sortedConsumers = consumers.stream()
            .sorted(Comparator.comparingDouble(ConsumerMetrics::getProcessingCapacity).reversed())
            .collect(Collectors.toList());
        
        // 按负载对分区排序（高负载分区优先分配）
        List<PartitionMetrics> sortedPartitions = partitions.stream()
            .sorted(Comparator.comparingLong(PartitionMetrics::getMessageLag).reversed())
            .collect(Collectors.toList());
        
        // 计算消费者总处理能力
        double totalCapacity = sortedConsumers.stream()
            .mapToDouble(ConsumerMetrics::getProcessingCapacity)
            .sum();
        
        // 计算分区总负载
        long totalLag = sortedPartitions.stream()
            .mapToLong(PartitionMetrics::getMessageLag)
            .sum();
        
        // 初始化分配结果
        Map<String, List<Integer>> assignment = new HashMap<>();
        Map<String, Double> consumerLoad = new HashMap<>();
        
        for (ConsumerMetrics consumer : sortedConsumers) {
            assignment.put(consumer.getConsumerId(), new ArrayList<>());
            consumerLoad.put(consumer.getConsumerId(), 0.0);
        }
        
        // 贪婪算法：将高负载分区分配给处理能力强的消费者
        for (PartitionMetrics partition : sortedPartitions) {
            // 找到当前负载率最低且有能力处理更多分区的消费者
            String bestConsumer = null;
            double bestLoadRatio = Double.MAX_VALUE;
            
            for (ConsumerMetrics consumer : sortedConsumers) {
                List<Integer> assignedPartitions = assignment.get(consumer.getConsumerId());
                
                // 检查是否超过最大分区数限制
                if (assignedPartitions.size() >= MAX_PARTITIONS_PER_CONSUMER) {
                    continue;
                }
                
                // 计算如果分配此分区后的负载率
                double currentLoad = consumerLoad.get(consumer.getConsumerId());
                double partitionLoadShare = (double) partition.getMessageLag() / totalLag * totalCapacity;
                double projectedLoad = currentLoad + partitionLoadShare;
                double loadRatio = projectedLoad / consumer.getProcessingCapacity();
                
                if (loadRatio < bestLoadRatio) {
                    bestLoadRatio = loadRatio;
                    bestConsumer = consumer.getConsumerId();
                }
            }
            
            if (bestConsumer != null) {
                assignment.get(bestConsumer).add(partition.getPartitionId());
                double newLoad = consumerLoad.get(bestConsumer) + 
                    (double) partition.getMessageLag() / totalLag * totalCapacity;
                consumerLoad.put(bestConsumer, newLoad);
            } else {
                // 没有合适的消费者，分配给分区数最少的
                String fallbackConsumer = sortedConsumers.stream()
                    .min(Comparator.comparingInt(c -> assignment.get(c.getConsumerId()).size()))
                    .map(ConsumerMetrics::getConsumerId)
                    .orElse(sortedConsumers.get(0).getConsumerId());
                assignment.get(fallbackConsumer).add(partition.getPartitionId());
            }
        }
        
        // 确保每个消费者至少有一个分区
        balanceMinimumPartitions(assignment, sortedPartitions, sortedConsumers);
        
        return assignment;
    }
    
    /**
     * 确保每个消费者至少有最小分区数
     */
    private void balanceMinimumPartitions(
            Map<String, List<Integer>> assignment,
            List<PartitionMetrics> partitions,
            List<ConsumerMetrics> consumers) {
        
        List<String> overloadedConsumers = assignment.entrySet().stream()
            .filter(e -> e.getValue().size() > MAX_PARTITIONS_PER_CONSUMER)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        List<String> underloadedConsumers = consumers.stream()
            .filter(c -> assignment.getOrDefault(c.getConsumerId(), Collections.emptyList()).isEmpty())
            .map(ConsumerMetrics::getConsumerId)
            .collect(Collectors.toList());
        
        for (String overloaded : overloadedConsumers) {
            List<Integer> parts = assignment.get(overloaded);
            while (parts.size() > MAX_PARTITIONS_PER_CONSUMER && !underloadedConsumers.isEmpty()) {
                String target = underloadedConsumers.remove(0);
                if (!parts.isEmpty()) {
                    Integer movedPartition = parts.remove(parts.size() - 1);
                    assignment.get(target).add(movedPartition);
                    logger.debug("平衡分区: 将分区 {} 从 {} 移动到 {}", movedPartition, overloaded, target);
                }
            }
        }
    }
    
    /**
     * 评估当前分配健康度
     */
    private AssignmentHealth evaluateAssignmentHealth(
            String topic,
            List<PartitionMetrics> partitions,
            List<ConsumerMetrics> consumers) {
        
        Map<String, List<Integer>> currentAssignment = currentAssignments.get().get(topic);
        if (currentAssignment == null || currentAssignment.isEmpty()) {
            return new AssignmentHealth(true, "INITIAL_ASSIGNMENT", "首次分配");
        }
        
        // 计算每个消费者的实际负载
        Map<String, Long> consumerLoads = new HashMap<>();
        for (ConsumerMetrics consumer : consumers) {
            List<Integer> assignedParts = currentAssignment.getOrDefault(
                consumer.getConsumerId(), Collections.emptyList()
            );
            long load = assignedParts.stream()
                .mapToLong(pid -> partitions.stream()
                    .filter(p -> p.getPartitionId() == pid)
                    .mapToLong(PartitionMetrics::getMessageLag)
                    .findFirst().orElse(0))
                .sum();
            consumerLoads.put(consumer.getConsumerId(), load);
        }
        
        // 计算负载标准差
        double avgLoad = consumerLoads.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);
        
        double variance = consumerLoads.values().stream()
            .mapToDouble(load -> Math.pow(load - avgLoad, 2))
            .average()
            .orElse(0);
        
        double stdDev = Math.sqrt(variance);
        double cv = avgLoad > 0 ? stdDev / avgLoad : 0; // 变异系数
        
        // 检测消费者离线
        Set<String> assignedConsumers = currentAssignment.keySet();
        Set<String> activeConsumers = consumers.stream()
            .map(ConsumerMetrics::getConsumerId)
            .collect(Collectors.toSet());
        
        Set<String> offlineConsumers = new HashSet<>(assignedConsumers);
        offlineConsumers.removeAll(activeConsumers);
        
        if (!offlineConsumers.isEmpty()) {
            return new AssignmentHealth(true, "CONSUMER_OFFLINE", 
                "消费者离线: " + offlineConsumers);
        }
        
        // 检测新消费者加入
        Set<String> newConsumers = new HashSet<>(activeConsumers);
        newConsumers.removeAll(assignedConsumers);
        
        if (!newConsumers.isEmpty()) {
            return new AssignmentHealth(true, "NEW_CONSUMER_JOINED", 
                "新消费者加入: " + newConsumers);
        }
        
        // 检测负载不均衡
        if (cv > IMBALANCE_THRESHOLD) {
            return new AssignmentHealth(true, "LOAD_IMBALANCE", 
                String.format("负载不均衡 (变异系数: %.2f)", cv));
        }
        
        // 检测分区热点
        for (PartitionMetrics partition : partitions) {
            if (partition.getMessageLag() > HOTSPOT_THRESHOLD_LAG) {
                return new AssignmentHealth(true, "HOTSPOT_DETECTED", 
                    "热点分区: " + partition.getPartitionId());
            }
        }
        
        return new AssignmentHealth(false, "HEALTHY", "分配健康");
    }
    
    /**
     * 执行重平衡
     */
    private void executeRebalance(String topic, 
                                   Map<String, List<Integer>> newAssignment,
                                   String reason) {
        try {
            logger.info("执行Topic [{}] 的重平衡, 原因: {}", topic, reason);
            
            long startTime = System.currentTimeMillis();
            
            // 记录调度决策
            SchedulingDecision decision = new SchedulingDecision();
            decision.setTopic(topic);
            decision.setTimestamp(Instant.now());
            decision.setReason(reason);
            decision.setOldAssignment(currentAssignments.get().get(topic));
            decision.setNewAssignment(newAssignment);
            
            // 实际执行重平衡（触发Kafka消费者重平衡）
            triggerKafkaRebalance(topic, newAssignment);
            
            // 更新当前分配
            currentAssignments.get().put(topic, newAssignment);
            
            // 记录历史
            recordDecision(decision);
            
            // 更新最后重平衡时间
            lastRebalanceTime.set(System.currentTimeMillis());
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Topic [{}] 重平衡完成, 耗时: {}ms", topic, duration);
            
        } catch (Exception e) {
            logger.error("执行重平衡失败: Topic [{}]", topic, e);
        }
    }
    
    /**
     * 触发Kafka重平衡
     */
    private void triggerKafkaRebalance(String topic, 
                                        Map<String, List<Integer>> assignment) {
        // 这里调用Kafka AdminClient或协调器触发重平衡
        // 实际实现取决于具体的Kafka客户端版本和配置
        logger.debug("触发Kafka重平衡: Topic={}, Assignment={}", topic, assignment);
        
        // TODO: 集成实际的Kafka重平衡API
        // 可以通过修改消费者组的分区分配策略或手动触发再均衡
    }
    
    /**
     * 检测分区热点
     */
    private void detectHotspots() {
        try {
            List<PartitionMetrics> allPartitions = partitionMetricsRepo.findAll();
            
            for (PartitionMetrics partition : allPartitions) {
                boolean isHotspot = partition.getMessageLag() > HOTSPOT_THRESHOLD_LAG ||
                                   partition.getCpuUsage() > HOTSPOT_THRESHOLD_CPU;
                
                HotspotDetectionResult result = new HotspotDetectionResult(
                    partition.getPartitionId(),
                    isHotspot,
                    partition.getMessageLag(),
                    partition.getCpuUsage(),
                    System.currentTimeMillis()
                );
                
                hotspotCache.put(partition.getPartitionId(), result);
                
                if (isHotspot) {
                    logger.warn("检测到热点分区: Topic={}, Partition={}, Lag={}, CPU={}%",
                        partition.getTopicName(),
                        partition.getPartitionId(),
                        partition.getMessageLag(),
                        String.format("%.1f", partition.getCpuUsage() * 100));
                }
            }
            
            // 清理过期缓存
            cleanupHotspotCache();
            
        } catch (Exception e) {
            logger.error("热点检测失败", e);
        }
    }
    
    /**
     * 检查分区扩缩容
     */
    private void checkPartitionScaling(String topic,
                                        List<PartitionMetrics> partitions,
                                        List<ConsumerMetrics> consumers) {
        // 检测是否需要分裂热点分区
        for (PartitionMetrics partition : partitions) {
            HotspotDetectionResult hotspot = hotspotCache.get(partition.getPartitionId());
            if (hotspot != null && hotspot.isHotspot() && 
                System.currentTimeMillis() - hotspot.getDetectTime() < HOTSPOT_CACHE_TTL_MS) {
                
                // 连续热点检测，建议分裂
                logger.info("建议分裂热点分区: Topic={}, Partition={}", 
                    topic, partition.getPartitionId());
                
                // 记录分裂建议（实际分裂需要更复杂的协调）
                suggestPartitionSplit(topic, partition.getPartitionId());
            }
        }
        
        // 检测是否需要合并低负载分区
        List<PartitionMetrics> lowLoadPartitions = partitions.stream()
            .filter(p -> p.getMessageLag() < 100 && p.getCpuUsage() < 0.1)
            .collect(Collectors.toList());
        
        if (lowLoadPartitions.size() >= 2) {
            logger.info("检测到{}个低负载分区,建议合并", lowLoadPartitions.size());
        }
    }
    
    /**
     * 建议分区分裂
     */
    private void suggestPartitionSplit(String topic, int partitionId) {
        // 记录分裂建议，等待人工确认或自动执行
        logger.info("分区分裂建议: Topic={}, Partition={}", topic, partitionId);
        // TODO: 实现实际的分区分裂逻辑
    }
    
    /**
     * 获取所有Topic指标
     */
    private List<TopicMetrics> getAllTopicMetrics() {
        // 从仓库获取所有Topic指标
        return partitionMetricsRepo.findAllTopics();
    }
    
    /**
     * 记录调度决策
     */
    private void recordDecision(SchedulingDecision decision) {
        decisionHistory.offer(decision);
        while (decisionHistory.size() > MAX_HISTORY_SIZE) {
            decisionHistory.poll();
        }
    }
    
    /**
     * 清理热点缓存
     */
    private void cleanupHotspotCache() {
        long now = System.currentTimeMillis();
        hotspotCache.entrySet().removeIf(entry -> 
            now - entry.getValue().getDetectTime() > HOTSPOT_CACHE_TTL_MS * 2
        );
    }
    
    /**
     * 获取当前分配
     */
    public Map<String, List<Integer>> getCurrentAssignment(String topic) {
        return currentAssignments.get().get(topic);
    }
    
    /**
     * 获取调度历史
     */
    public List<SchedulingDecision> getDecisionHistory(int limit) {
        return decisionHistory.stream()
            .sorted(Comparator.comparing(SchedulingDecision::getTimestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取热点分区列表
     */
    public List<HotspotDetectionResult> getHotspotPartitions() {
        return hotspotCache.values().stream()
            .filter(HotspotDetectionResult::isHotspot)
            .collect(Collectors.toList());
    }
    
    /**
     * 手动触发重平衡
     */
    public boolean triggerManualRebalance(String topic) {
        logger.info("手动触发重平衡: Topic={}", topic);
        
        List<PartitionMetrics> partitions = partitionMetricsRepo.findByTopic(topic);
        List<ConsumerMetrics> consumers = consumerMetricsRepo.findActiveConsumers(topic);
        
        if (partitions.isEmpty() || consumers.isEmpty()) {
            logger.warn("无法重平衡: 分区或消费者为空");
            return false;
        }
        
        Map<String, List<Integer>> newAssignment = calculateOptimalAssignment(
            topic, partitions, consumers
        );
        
        if (newAssignment != null && !newAssignment.isEmpty()) {
            executeRebalance(topic, newAssignment, "MANUAL_TRIGGER");
            return true;
        }
        
        return false;
    }
    
    // ============ 内部类 ============
    
    /**
     * 分配健康度评估结果
     */
    private static class AssignmentHealth {
        private final boolean needsRebalance;
        private final String code;
        private final String reason;
        
        public AssignmentHealth(boolean needsRebalance, String code, String reason) {
            this.needsRebalance = needsRebalance;
            this.code = code;
            this.reason = reason;
        }
        
        public boolean needsRebalance() {
            return needsRebalance;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getReason() {
            return reason;
        }
    }
    
    /**
     * 热点检测结果
     */
    public static class HotspotDetectionResult {
        private final int partitionId;
        private final boolean hotspot;
        private final long messageLag;
        private final double cpuUsage;
        private final long detectTime;
        
        public HotspotDetectionResult(int partitionId, boolean hotspot, 
                                       long messageLag, double cpuUsage, long detectTime) {
            this.partitionId = partitionId;
            this.hotspot = hotspot;
            this.messageLag = messageLag;
            this.cpuUsage = cpuUsage;
            this.detectTime = detectTime;
        }
        
        public int getPartitionId() {
            return partitionId;
        }
        
        public boolean isHotspot() {
            return hotspot;
        }
        
        public long getMessageLag() {
            return messageLag;
        }
        
        public double getCpuUsage() {
            return cpuUsage;
        }
        
        public long getDetectTime() {
            return detectTime;
        }
    }
}
