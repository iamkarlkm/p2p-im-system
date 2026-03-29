package com.im.federated.service;

import com.im.federated.entity.*;
import com.im.federated.dto.*;
import com.im.federated.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 联邦学习服务类
 * 提供联邦学习任务管理和训练的核心业务逻辑
 */
@Service
@Transactional
public class FederatedLearningService {

    private final FederatedLearningTaskRepository taskRepository;
    private final FederatedTrainingRecordRepository trainingRecordRepository;
    private final FederatedAggregationRecordRepository aggregationRecordRepository;
    private final FederatedModelRepository modelRepository;
    private final FederatedClientRepository clientRepository;

    @Autowired
    public FederatedLearningService(
            FederatedLearningTaskRepository taskRepository,
            FederatedTrainingRecordRepository trainingRecordRepository,
            FederatedAggregationRecordRepository aggregationRecordRepository,
            FederatedModelRepository modelRepository,
            FederatedClientRepository clientRepository) {
        this.taskRepository = taskRepository;
        this.trainingRecordRepository = trainingRecordRepository;
        this.aggregationRecordRepository = aggregationRecordRepository;
        this.modelRepository = modelRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * 创建联邦学习任务
     */
    public FederatedLearningEntity createTask(FederatedLearningTaskRequest request) {
        // 验证请求参数
        validateTaskRequest(request);
        
        // 创建任务实体
        FederatedLearningEntity task = new FederatedLearningEntity(
            request.getTaskName(),
            request.getModelType(),
            request.getAlgorithmType(),
            request.getNumClients(),
            request.getRoundsTotal(),
            request.getCreatedBy()
        );
        
        // 设置可选参数
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPrivacyProtectionLevel() != null) {
            task.setPrivacyProtectionLevel(request.getPrivacyProtectionLevel());
        }
        if (request.getEpsilon() != null) {
            task.setEpsilon(request.getEpsilon());
        }
        if (request.getDelta() != null) {
            task.setDelta(request.getDelta());
        }
        if (request.getClippingNorm() != null) {
            task.setClippingNorm(request.getClippingNorm());
        }
        if (request.getParticipationRate() != null) {
            task.setParticipationRate(request.getParticipationRate());
        }
        if (request.getBatchSize() != null) {
            task.setBatchSize(request.getBatchSize());
        }
        if (request.getLearningRate() != null) {
            task.setLearningRate(request.getLearningRate());
        }
        if (request.getConvergenceThreshold() != null) {
            task.setConvergenceThreshold(request.getConvergenceThreshold());
        }
        if (request.getMaxEpochsPerRound() != null) {
            task.setMaxEpochsPerRound(request.getMaxEpochsPerRound());
        }
        if (request.getAggregationMethod() != null) {
            task.setAggregationMethod(request.getAggregationMethod());
        }
        if (request.getSecureAggregationEnabled() != null) {
            task.setSecureAggregationEnabled(request.getSecureAggregationEnabled());
        }
        if (request.getCommunicationEfficient() != null) {
            task.setCommunicationEfficient(request.getCommunicationEfficient());
        }
        if (request.getCompressionRatio() != null) {
            task.setCompressionRatio(request.getCompressionRatio());
        }
        if (request.getHeterogeneousDataSupported() != null) {
            task.setHeterogeneousDataSupported(request.getHeterogeneousDataSupported());
        }
        if (request.getPersonalizationEnabled() != null) {
            task.setPersonalizationEnabled(request.getPersonalizationEnabled());
        }
        if (request.getFaultToleranceEnabled() != null) {
            task.setFaultToleranceEnabled(request.getFaultToleranceEnabled());
        }
        if (request.getMaxFailuresAllowed() != null) {
            task.setMaxFailuresAllowed(request.getMaxFailuresAllowed());
        }
        if (request.getDataDistributionType() != null) {
            task.setDataDistributionType(request.getDataDistributionType());
        }
        
        // 计算预计完成时间（基于经验公式）
        calculateEstimatedCompletionTime(task);
        
        // 保存任务
        return taskRepository.save(task);
    }

    /**
     * 启动联邦学习任务
     */
    public FederatedLearningEntity startTask(UUID taskId) {
        FederatedLearningEntity task = getTaskById(taskId);
        
        if (!"created".equals(task.getStatus())) {
            throw new IllegalStateException("Task must be in 'created' state to start. Current state: " + task.getStatus());
        }
        
        task.startTask();
        
        // 初始化客户端参与列表
        initializeClientParticipation(task);
        
        return taskRepository.save(task);
    }

    /**
     * 提交客户端训练结果
     */
    public FederatedTrainingRecordEntity submitClientTrainingResult(
            UUID taskId, 
            String clientId, 
            ClientTrainingResultRequest request) {
        
        FederatedLearningEntity task = getTaskById(taskId);
        
        if (!"running".equals(task.getStatus())) {
            throw new IllegalStateException("Task must be in 'running' state. Current state: " + task.getStatus());
        }
        
        // 检查轮次是否匹配
        if (!request.getRoundNumber().equals(task.getCurrentRound())) {
            throw new IllegalArgumentException(
                String.format("Round mismatch. Expected round %d, got %d", 
                    task.getCurrentRound(), request.getRoundNumber()));
        }
        
        // 创建训练记录
        FederatedTrainingRecordEntity record = new FederatedTrainingRecordEntity(
            taskId, clientId, request.getRoundNumber(), "gradient_update"
        );
        
        record.setLocalAccuracy(request.getLocalAccuracy());
        record.setLocalLoss(request.getLocalLoss());
        record.setTrainingTimeSeconds(request.getTrainingTimeSeconds());
        record.setDataSamplesUsed(request.getDataSamplesUsed());
        record.setGradientSizeBytes(request.getGradientSizeBytes());
        
        // 设置隐私保护参数
        if (request.getPrivacyBudgetUsed() != null) {
            record.setPrivacyBudgetUsed(request.getPrivacyBudgetUsed());
        }
        if (request.getNoiseAddedLevel() != null) {
            record.setNoiseAddedLevel(request.getNoiseAddedLevel());
        }
        
        // 设置梯度裁剪参数
        if (request.getGradientNormBeforeClipping() != null) {
            record.setGradientNormBeforeClipping(request.getGradientNormBeforeClipping());
        }
        if (request.getGradientNormAfterClipping() != null) {
            record.setGradientNormAfterClipping(request.getGradientNormAfterClipping());
            record.setClippingApplied(true);
        }
        
        // 设置压缩参数
        if (request.getCompressionApplied() != null) {
            record.setCompressionApplied(request.getCompressionApplied());
        }
        if (request.getCompressionRatioActual() != null) {
            record.setCompressionRatioActual(request.getCompressionRatioActual());
        }
        if (request.getCompressionError() != null) {
            record.setCompressionError(request.getCompressionError());
        }
        
        // 设置安全聚合参数
        if (request.getSecureAggregationUsed() != null) {
            record.setSecureAggregationUsed(request.getSecureAggregationUsed());
        }
        
        // 设置其他训练参数
        if (request.getOptimizerType() != null) {
            record.setOptimizerType(request.getOptimizerType());
        }
        if (request.getLearningRateActual() != null) {
            record.setLearningRateActual(request.getLearningRateActual());
        }
        if (request.getBatchSizeActual() != null) {
            record.setBatchSizeActual(request.getBatchSizeActual());
        }
        if (request.getEpochsCompleted() != null) {
            record.setEpochsCompleted(request.getEpochsCompleted());
        }
        
        // 设置指标
        if (request.getMetrics() != null) {
            record.setMetrics(request.getMetrics());
        }
        
        // 设置文件路径
        if (request.getGradientDataPath() != null) {
            record.setGradientDataPath(request.getGradientDataPath());
        }
        if (request.getModelUpdatePath() != null) {
            record.setModelUpdatePath(request.getModelUpdatePath());
        }
        if (request.getCheckpointPath() != null) {
            record.setCheckpointPath(request.getCheckpointPath());
        }
        
        // 设置数据分布信息
        if (request.getDataDistributionLabel() != null) {
            record.setDataDistributionLabel(request.getDataDistributionLabel());
        }
        
        // 设置客户端能力信息
        if (request.getClientComputationCapacity() != null) {
            record.setClientComputationCapacity(request.getClientComputationCapacity());
        }
        if (request.getClientNetworkBandwidth() != null) {
            record.setClientNetworkBandwidth(request.getClientNetworkBandwidth());
        }
        if (request.getClientStorageAvailable() != null) {
            record.setClientStorageAvailable(request.getClientStorageAvailable());
        }
        
        // 设置哈希和签名
        if (request.getHashValue() != null) {
            record.setHashValue(request.getHashValue());
        }
        if (request.getSignature() != null) {
            record.setSignature(request.getSignature());
        }
        
        record.startProcessing();
        record.completeProcessing(
            request.getLocalAccuracy(), 
            request.getLocalLoss(), 
            request.getTrainingTimeSeconds()
        );
        record.submitForAggregation();
        
        // 更新任务统计信息
        updateTaskStatistics(task, record);
        
        // 添加客户端到任务参与列表
        task.addActiveClient(clientId);
        
        // 保存记录
        trainingRecordRepository.save(record);
        taskRepository.save(task);
        
        return record;
    }

    /**
     * 执行模型聚合
     */
    public FederatedAggregationRecordEntity performModelAggregation(UUID taskId, Integer roundNumber) {
        FederatedLearningEntity task = getTaskById(taskId);
        
        if (!"running".equals(task.getStatus())) {
            throw new IllegalStateException("Task must be in 'running' state. Current state: " + task.getStatus());
        }
        
        if (!roundNumber.equals(task.getCurrentRound())) {
            throw new IllegalArgumentException(
                String.format("Round mismatch. Expected round %d, got %d", 
                    task.getCurrentRound(), roundNumber));
        }
        
        // 获取本轮所有训练记录
        List<FederatedTrainingRecordEntity> trainingRecords = 
            trainingRecordRepository.findByTaskIdAndRoundNumber(taskId, roundNumber);
        
        if (trainingRecords.isEmpty()) {
            throw new IllegalStateException("No training records found for round " + roundNumber);
        }
        
        // 创建聚合记录
        FederatedAggregationRecordEntity aggregationRecord = 
            new FederatedAggregationRecordEntity(taskId, roundNumber, task.getAggregationMethod());
        
        aggregationRecord.startAggregation();
        
        // 执行聚合算法
        AggregationResult aggregationResult = executeAggregationAlgorithm(task, trainingRecords);
        
        // 更新聚合记录
        aggregationRecord.setTotalClientsParticipated(trainingRecords.size());
        aggregationRecord.setClientIds(
            trainingRecords.stream()
                .map(FederatedTrainingRecordEntity::getClientId)
                .collect(Collectors.toList())
        );
        
        // 计算成功和失败的客户端
        long successfulClients = trainingRecords.stream()
            .filter(r -> "completed".equals(r.getStatus()))
            .count();
        aggregationRecord.setClientsSuccessful((int) successfulClients);
        aggregationRecord.setClientsFailed((int) (trainingRecords.size() - successfulClients));
        
        // 设置聚合结果
        aggregationRecord.setAverageAccuracyBefore(aggregationResult.getAverageAccuracyBefore());
        aggregationRecord.setAverageLossBefore(aggregationResult.getAverageLossBefore());
        aggregationRecord.setAccuracyAfter(aggregationResult.getAccuracyAfter());
        aggregationRecord.setLossAfter(aggregationResult.getLossAfter());
        aggregationRecord.setAggregationTimeSeconds(aggregationResult.getAggregationTimeSeconds());
        aggregationRecord.setModelUpdateSizeBytes(aggregationResult.getModelUpdateSizeBytes());
        aggregationRecord.setAggregatedModelSizeBytes(aggregationResult.getAggregatedModelSizeBytes());
        
        // 设置客户端权重
        aggregationRecord.setClientWeights(aggregationResult.getClientWeights());
        
        // 设置总数据样本数
        long totalSamples = trainingRecords.stream()
            .filter(r -> r.getDataSamplesUsed() != null)
            .mapToLong(FederatedTrainingRecordEntity::getDataSamplesUsed)
            .sum();
        aggregationRecord.setTotalDataSamples(totalSamples);
        
        // 设置隐私参数
        if (task.getEpsilon() != null) {
            aggregationRecord.setDifferentialPrivacyEpsilon(task.getEpsilon());
        }
        if (task.getDelta() != null) {
            aggregationRecord.setDifferentialPrivacyDelta(task.getDelta());
        }
        if (task.getPrivacyProtectionLevel() != null && !"none".equals(task.getPrivacyProtectionLevel())) {
            double privacyBudgetUsed = trainingRecords.stream()
                .filter(r -> r.getPrivacyBudgetUsed() != null)
                .mapToDouble(FederatedTrainingRecordEntity::getPrivacyBudgetUsed)
                .sum();
            aggregationRecord.setPrivacyBudgetConsumed(privacyBudgetUsed);
            
            double noiseLevel = trainingRecords.stream()
                .filter(r -> r.getNoiseAddedLevel() != null)
                .mapToDouble(FederatedTrainingRecordEntity::getNoiseAddedLevel)
                .average()
                .orElse(0.0);
            aggregationRecord.setNoiseAddedLevel(noiseLevel);
        }
        
        // 设置安全聚合参数
        if (task.getSecureAggregationEnabled() != null && task.getSecureAggregationEnabled()) {
            aggregationRecord.setSecureAggregationApplied(true);
            aggregationRecord.setSecureAggregationProtocol("secure-aggregation-v1");
            // 在实际应用中，这里会设置实际的密钥ID
        }
        
        // 设置梯度裁剪参数
        if (task.getClippingNorm() != null) {
            aggregationRecord.setClippingApplied(true);
            aggregationRecord.setClippingThreshold(task.getClippingNorm());
        }
        
        // 设置压缩参数
        if (task.getCompressionRatio() != null) {
            aggregationRecord.setCompressionApplied(true);
            aggregationRecord.setCompressionRatio(task.getCompressionRatio());
            
            double compressionError = trainingRecords.stream()
                .filter(r -> r.getCompressionError() != null)
                .mapToDouble(FederatedTrainingRecordEntity::getCompressionError)
                .average()
                .orElse(0.0);
            aggregationRecord.setCompressionError(compressionError);
        }
        
        // 设置聚合指标
        aggregationRecord.setMetrics(aggregationResult.getMetrics());
        
        // 计算质量评分
        aggregationRecord.calculateQualityScores();
        
        // 设置文件路径
        aggregationRecord.setAggregatedModelPath(aggregationResult.getModelPath());
        aggregationRecord.setAggregatedGradientPath(aggregationResult.getGradientPath());
        
        // 完成聚合
        aggregationRecord.completeAggregation(
            aggregationResult.getAccuracyAfter(),
            aggregationResult.getLossAfter(),
            aggregationResult.getAggregationTimeSeconds()
        );
        
        // 广播模型（在实际应用中，这里会触发模型广播到客户端）
        aggregationRecord.broadcastModel();
        
        // 更新任务轮次
        task.incrementCurrentRound();
        
        // 更新任务指标
        updateTaskAggregationMetrics(task, aggregationResult);
        
        // 检查任务是否完成
        if (task.getCurrentRound() >= task.getRoundsTotal()) {
            task.completeTask(aggregationResult.getAccuracyAfter(), aggregationResult.getLossAfter());
        }
        
        // 保存聚合记录
        aggregationRecordRepository.save(aggregationRecord);
        taskRepository.save(task);
        
        return aggregationRecord;
    }

    /**
     * 暂停联邦学习任务
     */
    public FederatedLearningEntity pauseTask(UUID taskId) {
        FederatedLearningEntity task = getTaskById(taskId);
        
        if (!"running".equals(task.getStatus())) {
            throw new IllegalStateException("Task must be in 'running' state to pause. Current state: " + task.getStatus());
        }
        
        task.pauseTask();
        return taskRepository.save(task);
    }

    /**
     * 恢复联邦学习任务
     */
    public FederatedLearningEntity resumeTask(UUID taskId) {
        FederatedLearningEntity task = getTaskById(taskId);
        
        if (!"paused".equals(task.getStatus())) {
            throw new IllegalStateException("Task must be in 'paused' state to resume. Current state: " + task.getStatus());
        }
        
        task.resumeTask();
        return taskRepository.save(task);
    }

    /**
     * 获取任务详情
     */
    public FederatedLearningEntity getTask(UUID taskId) {
        return getTaskById(taskId);
    }

    /**
     * 获取任务列表
     */
    public List<FederatedLearningEntity> getTasks(String status, String createdBy) {
        if (status != null && createdBy != null) {
            return taskRepository.findByStatusAndCreatedBy(status, createdBy);
        } else if (status != null) {
            return taskRepository.findByStatus(status);
        } else if (createdBy != null) {
            return taskRepository.findByCreatedBy(createdBy);
        } else {
            return taskRepository.findAll();
        }
    }

    /**
     * 获取任务训练记录
     */
    public List<FederatedTrainingRecordEntity> getTaskTrainingRecords(UUID taskId, Integer roundNumber) {
        if (roundNumber != null) {
            return trainingRecordRepository.findByTaskIdAndRoundNumber(taskId, roundNumber);
        } else {
            return trainingRecordRepository.findByTaskId(taskId);
        }
    }

    /**
     * 获取任务聚合记录
     */
    public List<FederatedAggregationRecordEntity> getTaskAggregationRecords(UUID taskId) {
        return aggregationRecordRepository.findByTaskId(taskId);
    }

    /**
     * 获取任务统计信息
     */
    public FederatedTaskStatistics getTaskStatistics(UUID taskId) {
        FederatedLearningEntity task = getTaskById(taskId);
        
        FederatedTaskStatistics statistics = new FederatedTaskStatistics();
        statistics.setTaskId(taskId);
        statistics.setTaskName(task.getTaskName());
        statistics.setCurrentRound(task.getCurrentRound());
        statistics.setTotalRounds(task.getRoundsTotal());
        statistics.setStatus(task.getStatus());
        statistics.setAccuracy(task.getAccuracy());
        statistics.setLoss(task.getLoss());
        statistics.setActiveClients(task.getActiveClients());
        statistics.setTotalClients(task.getNumClients());
        
        // 计算训练统计
        List<FederatedTrainingRecordEntity> trainingRecords = trainingRecordRepository.findByTaskId(taskId);
        if (!trainingRecords.isEmpty()) {
            double avgAccuracy = trainingRecords.stream()
                .filter(r -> r.getLocalAccuracy() != null)
                .mapToDouble(FederatedTrainingRecordEntity::getLocalAccuracy)
                .average()
                .orElse(0.0);
            double avgLoss = trainingRecords.stream()
                .filter(r -> r.getLocalLoss() != null)
                .mapToDouble(FederatedTrainingRecordEntity::getLocalLoss)
                .average()
                .orElse(0.0);
            long totalTrainingTime = trainingRecords.stream()
                .filter(r -> r.getTrainingTimeSeconds() != null)
                .mapToLong(FederatedTrainingRecordEntity::getTrainingTimeSeconds)
                .sum();
            long totalSamples = trainingRecords.stream()
                .filter(r -> r.getDataSamplesUsed() != null)
                .mapToLong(FederatedTrainingRecordEntity::getDataSamplesUsed)
                .sum();
            
            statistics.setAverageAccuracy(avgAccuracy);
            statistics.setAverageLoss(avgLoss);
            statistics.setTotalTrainingTimeSeconds(totalTrainingTime);
            statistics.setTotalDataSamples(totalSamples);
            statistics.setTotalTrainingRecords(trainingRecords.size());
        }
        
        // 计算聚合统计
        List<FederatedAggregationRecordEntity> aggregationRecords = aggregationRecordRepository.findByTaskId(taskId);
        if (!aggregationRecords.isEmpty()) {
            double avgAggregationTime = aggregationRecords.stream()
                .filter(r -> r.getAggregationTimeSeconds() != null)
                .mapToLong(FederatedAggregationRecordEntity::getAggregationTimeSeconds)
                .average()
                .orElse(0.0);
            double avgAccuracyImprovement = aggregationRecords.stream()
                .filter(r -> r.getAccuracyImprovement() != null)
                .mapToDouble(FederatedAggregationRecordEntity::getAccuracyImprovement)
                .average()
                .orElse(0.0);
            
            statistics.setAverageAggregationTimeSeconds(avgAggregationTime);
            statistics.setAverageAccuracyImprovement(avgAccuracyImprovement);
            statistics.setTotalAggregationRecords(aggregationRecords.size());
        }
        
        return statistics;
    }

    /**
     * 验证任务请求参数
     */
    private void validateTaskRequest(FederatedLearningTaskRequest request) {
        if (request.getTaskName() == null || request.getTaskName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }
        if (request.getModelType() == null || request.getModelType().trim().isEmpty()) {
            throw new IllegalArgumentException("Model type is required");
        }
        if (request.getAlgorithmType() == null || request.getAlgorithmType().trim().isEmpty()) {
            throw new IllegalArgumentException("Algorithm type is required");
        }
        if (request.getNumClients() == null || request.getNumClients() <= 0) {
            throw new IllegalArgumentException("Number of clients must be greater than 0");
        }
        if (request.getRoundsTotal() == null || request.getRoundsTotal() <= 0) {
            throw new IllegalArgumentException("Total rounds must be greater than 0");
        }
        if (request.getCreatedBy() == null || request.getCreatedBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Created by is required");
        }
    }

    /**
     * 初始化客户端参与
     */
    private void initializeClientParticipation(FederatedLearningEntity task) {
        // 在实际应用中，这里会从客户端注册表中选择参与训练的客户端
        // 这里只是示例代码
        List<String> initialClients = new ArrayList<>();
        for (int i = 1; i <= task.getNumClients(); i++) {
            initialClients.add("client-" + i);
        }
        task.setClientIds(initialClients);
    }

    /**
     * 计算预计完成时间
     */
    private void calculateEstimatedCompletionTime(FederatedLearningEntity task) {
        // 基于经验公式计算预计完成时间
        // 每个轮次大约需要：客户端训练时间 + 聚合时间
        // 这里使用简化的估算
        int estimatedMinutesPerRound = 10; // 每个轮次大约10分钟
        int totalMinutes = task.getRoundsTotal() * estimatedMinutesPerRound;
        
        LocalDateTime estimatedCompletion = task.getCreatedAt().plusMinutes(totalMinutes);
        task.setEstimatedCompletionTime(estimatedCompletion);
    }

    /**
     * 更新任务统计信息
     */
    private void updateTaskStatistics(FederatedLearningEntity task, FederatedTrainingRecordEntity record) {
        // 更新总训练时间
        if (record.getTrainingTimeSeconds() != null) {
            task.setTotalTrainingTimeMinutes(
                task.getTotalTrainingTimeMinutes() + record.getTrainingTimeSeconds() / 60
            );
        }
        
        // 更新总通信数据大小
        if (record.getGradientSizeBytes() != null) {
            task.setTotalCommunicationSizeMB(
                task.getTotalCommunicationSizeMB() + record.getGradientSizeBytes() / (1024 * 1024)
            );
        }
        
        // 更新总模型更新次数
        task.setTotalModelUpdates(task.getTotalModelUpdates() + 1);
        
        // 更新隐私预算消耗
        if (record.getPrivacyBudgetUsed() != null) {
            task.setPrivacyBudgetConsumed(
                task.getPrivacyBudgetConsumed() + record.getPrivacyBudgetUsed()
            );
        }
    }

    /**
     * 更新任务聚合指标
     */
    private void updateTaskAggregationMetrics(FederatedLearningEntity task, AggregationResult result) {
        task.setAccuracy(result.getAccuracyAfter());
        task.setLoss(result.getLossAfter());
        
        // 更新指标映射
        if (task.getMetrics() == null) {
            task.setMetrics(new HashMap<>());
        }
        
        // 记录每轮指标
        String roundKey = "round_" + task.getCurrentRound() + "_accuracy";
        task.getMetrics().put(roundKey, result.getAccuracyAfter());
        
        roundKey = "round_" + task.getCurrentRound() + "_loss";
        task.getMetrics().put(roundKey, result.getLossAfter());
        
        // 更新全局最佳指标
        updateGlobalBestMetrics(task, result);
    }

    /**
     * 更新全局最佳指标
     */
    private void updateGlobalBestMetrics(FederatedLearningEntity task, AggregationResult result) {
        if (task.getMetrics() == null) {
            task.setMetrics(new HashMap<>());
        }
        
        // 获取当前最佳准确率
        Double bestAccuracy = task.getMetrics().get("best_accuracy");
        if (bestAccuracy == null || result.getAccuracyAfter() > bestAccuracy) {
            task.getMetrics().put("best_accuracy", result.getAccuracyAfter());
            task.getMetrics().put("best_accuracy_round", (double) task.getCurrentRound());
        }
        
        // 获取当前最佳损失
        Double bestLoss = task.getMetrics().get("best_loss");
        if (bestLoss == null || result.getLossAfter() < bestLoss) {
            task.getMetrics().put("best_loss", result.getLossAfter());
            task.getMetrics().put("best_loss_round", (double) task.getCurrentRound());
        }
    }

    /**
     * 执行聚合算法（简化版）
     */
    private AggregationResult executeAggregationAlgorithm(
            FederatedLearningEntity task, 
            List<FederatedTrainingRecordEntity> trainingRecords) {
        
        AggregationResult result = new AggregationResult();
        
        // 计算聚合前平均指标
        double avgAccuracy = trainingRecords.stream()
            .filter(r -> r.getLocalAccuracy() != null)
            .mapToDouble(FederatedTrainingRecordEntity::getLocalAccuracy)
            .average()
            .orElse(0.0);
        double avgLoss = trainingRecords.stream()
            .filter(r -> r.getLocalLoss() != null)
            .mapToDouble(FederatedTrainingRecordEntity::getLocalLoss)
            .average()
            .orElse(0.0);
        
        result.setAverageAccuracyBefore(avgAccuracy);
        result.setAverageLossBefore(avgLoss);
        
        // 根据聚合方法执行聚合
        switch (task.getAggregationMethod()) {
            case "fedavg":
                result = executeFedAvgAggregation(task, trainingRecords, avgAccuracy, avgLoss);
                break;
            case "fedprox":
                result = executeFedProxAggregation(task, trainingRecords, avgAccuracy, avgLoss);
                break;
            case "fednova":
                result = executeFedNovaAggregation(task, trainingRecords, avgAccuracy, avgLoss);
                break;
            default:
                result = executeFedAvgAggregation(task, trainingRecords, avgAccuracy, avgLoss);
        }
        
        // 计算总数据大小
        long totalGradientSize = trainingRecords.stream()
            .filter(r -> r.getGradientSizeBytes() != null)
            .mapToLong(FederatedTrainingRecordEntity::getGradientSizeBytes)
            .sum();
        result.setModelUpdateSizeBytes(totalGradientSize);
        
        // 假设聚合后模型大小与总梯度大小相同（简化）
        result.setAggregatedModelSizeBytes(totalGradientSize);
        
        // 设置文件路径
        result.setModelPath(String.format("/models/task-%s/round-%d/model.bin", 
            task.getId(), task.getCurrentRound()));
        result.setGradientPath(String.format("/models/task-%s/round-%d/gradient.bin", 
            task.getId(), task.getCurrentRound()));
        
        return result;
    }

    /**
     * 执行FedAvg聚合
     */
    private AggregationResult executeFedAvgAggregation(
            FederatedLearningEntity task, 
            List<FederatedTrainingRecordEntity> trainingRecords,
            double avgAccuracyBefore,
            double avgLossBefore) {
        
        AggregationResult result = new AggregationResult();
        result.setAverageAccuracyBefore(avgAccuracyBefore);
        result.setAverageLossBefore(avgLossBefore);
        
        // 简化版的FedAvg：计算加权平均
        // 在实际应用中，这里会根据客户端的数据样本数计算权重
        
        Map<String, Double> clientWeights = new HashMap<>();
        long totalSamples = 0;
        
        // 计算总样本数
        for (FederatedTrainingRecordEntity record : trainingRecords) {
            if (record.getDataSamplesUsed() != null) {
                totalSamples += record.getDataSamplesUsed();
            }
        }
        
        // 计算每个客户端的权重
        for (FederatedTrainingRecordEntity record : trainingRecords) {
            double weight = 0.0;
            if (record.getDataSamplesUsed() != null && totalSamples > 0) {
                weight = (double) record.getDataSamplesUsed() / totalSamples;
            } else {
                weight = 1.0 / trainingRecords.size(); // 均匀分配
            }
            clientWeights.put(record.getClientId(), weight);
        }
        
        result.setClientWeights(clientWeights);
        
        // 模拟聚合后的指标改进
        // 在实际应用中，这些指标会从实际聚合结果中获取
        double accuracyImprovement = 0.01 * (task.getCurrentRound() + 1); // 模拟每轮改进1%
        double lossReduction = 0.005 * (task.getCurrentRound() + 1); // 模拟每轮减少0.5%
        
        result.setAccuracyAfter(avgAccuracyBefore + accuracyImprovement);
        result.setLossAfter(Math.max(avgLossBefore - lossReduction, 0.001)); // 防止损失为0
        
        result.setAggregationTimeSeconds(30L); // 假设聚合耗时30秒
        
        // 设置聚合指标
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy_improvement", accuracyImprovement);
        metrics.put("loss_reduction", lossReduction);
        metrics.put("participation_rate", (double) trainingRecords.size() / task.getNumClients());
        result.setMetrics(metrics);
        
        return result;
    }

    /**
     * 执行FedProx聚合（简化版）
     */
    private AggregationResult executeFedProxAggregation(
            FederatedLearningEntity task, 
            List<FederatedTrainingRecordEntity> trainingRecords,
            double avgAccuracyBefore,
            double avgLossBefore) {
        
        // 简化版：与FedAvg相同，但添加了正则化项
        AggregationResult result = executeFedAvgAggregation(task, trainingRecords, avgAccuracyBefore, avgLossBefore);
        
        // 添加FedProx特定的指标
        result.getMetrics().put("proximal_term", 0.1); // 近端项强度
        result.getMetrics().put("regularization_strength", 0.01); // 正则化强度
        
        return result;
    }

    /**
     * 执行FedNova聚合（简化版）
     */
    private AggregationResult executeFedNovaAggregation(
            FederatedLearningEntity task, 
            List<FederatedTrainingRecordEntity> trainingRecords,
            double avgAccuracyBefore,
            double avgLossBefore) {
        
        // 简化版：与FedAvg相同，但考虑客户端异质性
        AggregationResult result = executeFedAvgAggregation(task, trainingRecords, avgAccuracyBefore, avgLossBefore);
        
        // 添加FedNova特定的指标
        result.getMetrics().put("normalization_coefficient", 1.2); // 归一化系数
        result.getMetrics().put("heterogeneity_factor", 0.3); // 异质性因子
        
        return result;
    }

    /**
     * 获取任务实体（内部使用）
     */
    private FederatedLearningEntity getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
    }

    /**
     * 聚合结果内部类
     */
    private static class AggregationResult {
        private Double averageAccuracyBefore;
        private Double averageLossBefore;
        private Double accuracyAfter;
        private Double lossAfter;
        private Long aggregationTimeSeconds;
        private Long modelUpdateSizeBytes;
        private Long aggregatedModelSizeBytes;
        private Map<String, Double> clientWeights;
        private Map<String, Double> metrics;
        private String modelPath;
        private String gradientPath;

        // Getter和Setter方法
        public Double getAverageAccuracyBefore() { return averageAccuracyBefore; }
        public void setAverageAccuracyBefore(Double averageAccuracyBefore) { this.averageAccuracyBefore = averageAccuracyBefore; }

        public Double getAverageLossBefore() { return averageLossBefore; }
        public void setAverageLossBefore(Double averageLossBefore) { this.averageLossBefore = averageLossBefore; }

        public Double getAccuracyAfter() { return accuracyAfter; }
        public void setAccuracyAfter(Double accuracyAfter) { this.accuracyAfter = accuracyAfter; }

        public Double getLossAfter() { return lossAfter; }
        public void setLossAfter(Double lossAfter) { this.lossAfter = lossAfter; }

        public Long getAggregationTimeSeconds() { return aggregationTimeSeconds; }
        public void setAggregationTimeSeconds(Long aggregationTimeSeconds) { this.aggregationTimeSeconds = aggregationTimeSeconds; }

        public Long getModelUpdateSizeBytes() { return modelUpdateSizeBytes; }
        public void setModelUpdateSizeBytes(Long modelUpdateSizeBytes) { this.modelUpdateSizeBytes = modelUpdateSizeBytes; }

        public Long getAggregatedModelSizeBytes() { return aggregatedModelSizeBytes; }
        public void setAggregatedModelSizeBytes(Long aggregatedModelSizeBytes) { this.aggregatedModelSizeBytes = aggregatedModelSizeBytes; }

        public Map<String, Double> getClientWeights() { return clientWeights; }
        public void setClientWeights(Map<String, Double> clientWeights) { this.clientWeights = clientWeights; }

        public Map<String, Double> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }

        public String getModelPath() { return modelPath; }
        public void setModelPath(String modelPath) { this.modelPath = modelPath; }

        public String getGradientPath() { return gradientPath; }
        public void setGradientPath(String gradientPath) { this.gradientPath = gradientPath; }
    }
}