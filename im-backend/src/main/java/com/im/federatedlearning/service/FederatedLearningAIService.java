package com.im.federatedlearning.service;

import com.im.federatedlearning.entity.*;
import com.im.federatedlearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 联邦学习AI服务
 * 负责联邦学习的核心业务逻辑，包括服务器管理、客户端管理、训练调度、模型聚合等
 * 
 * @version 1.0
 * @created 2026-03-23
 */
@Service
@Transactional
public class FederatedLearningAIService {

    @Autowired
    private FederatedLearningServerRepository serverRepository;

    @Autowired
    private FLModelUpdateRepository modelUpdateRepository;

    @Autowired
    private FLModelRepository modelRepository;

    @Autowired
    private FLClientRepository clientRepository;

    @Autowired
    private PrivacyBudgetRepository privacyBudgetRepository;

    // 内存缓存，提高性能
    private final Map<String, FederatedLearningServerEntity> serverCache = new ConcurrentHashMap<>();
    private final Map<String, FLModelEntity> modelCache = new ConcurrentHashMap<>();
    private final Map<String, List<FLModelUpdateEntity>> roundUpdatesCache = new ConcurrentHashMap<>();

    /**
     * 注册联邦学习服务器
     */
    public FederatedLearningServerEntity registerServer(String serverName, String serverUrl, String region, 
                                                        FederatedLearningServerEntity.ServerType serverType) {
        
        // 检查服务器是否已存在
        Optional<FederatedLearningServerEntity> existingServer = serverRepository.findByServerUrl(serverUrl);
        if (existingServer.isPresent()) {
            FederatedLearningServerEntity server = existingServer.get();
            server.setStatus(FederatedLearningServerEntity.ServerStatus.ACTIVE);
            server.updateHeartbeat();
            serverRepository.save(server);
            serverCache.put(server.getServerId(), server);
            return server;
        }

        // 创建新服务器
        FederatedLearningServerEntity server = new FederatedLearningServerEntity(serverName, serverUrl, region);
        server.setServerType(serverType);
        server.setStatus(FederatedLearningServerEntity.ServerStatus.ACTIVE);
        
        // 设置默认支持的模型和语言
        server.addSupportedModel("SMART_REPLY");
        server.addSupportedModel("SPAM_DETECTION");
        server.addSupportedModel("SENTIMENT_ANALYSIS");
        server.addSupportedModel("MESSAGE_CATEGORIZATION");
        
        server.addSupportedLanguage("en");  // 英语
        server.addSupportedLanguage("zh");  // 中文
        server.addSupportedLanguage("es");  // 西班牙语
        server.addSupportedLanguage("fr");  // 法语
        server.addSupportedLanguage("de");  // 德语
        
        serverRepository.save(server);
        serverCache.put(server.getServerId(), server);
        
        return server;
    }

    /**
     * 获取服务器状态
     */
    public Map<String, Object> getServerStatus(String serverId) {
        FederatedLearningServerEntity server = getServerById(serverId);
        
        Map<String, Object> status = new HashMap<>();
        status.put("serverId", server.getServerId());
        status.put("serverName", server.getServerName());
        status.put("serverUrl", server.getServerUrl());
        status.put("status", server.getStatus().toString());
        status.put("isHealthy", server.isHealthy());
        status.put("activeModelCount", server.getActiveModelCount());
        status.put("activeClientCount", server.getActiveClientCount());
        status.put("completedTrainingRounds", server.getCompletedTrainingRounds());
        status.put("averageTrainingAccuracy", server.getAverageTrainingAccuracy());
        status.put("averageTrainingLoss", server.getAverageTrainingLoss());
        status.put("averageRoundDurationMinutes", server.getAverageRoundDurationMinutes());
        status.put("lastHeartbeatTime", server.getLastHeartbeatTime());
        status.put("lastTrainingRoundTime", server.getLastTrainingRoundTime());
        status.put("canAcceptClient", server.canAcceptClient());
        status.put("supportedModels", server.getSupportedModels());
        status.put("supportedLanguages", server.getSupportedLanguages());
        
        // 统计信息
        long pendingUpdates = modelUpdateRepository.countByServerIdAndStatus(
            serverId, FLModelUpdateEntity.UpdateStatus.PENDING);
        long verifiedUpdates = modelUpdateRepository.countByServerIdAndStatus(
            serverId, FLModelUpdateEntity.UpdateStatus.VERIFIED);
        long aggregatedUpdates = modelUpdateRepository.countByServerIdAndStatus(
            serverId, FLModelUpdateEntity.UpdateStatus.AGGREGATED);
        
        status.put("pendingUpdates", pendingUpdates);
        status.put("verifiedUpdates", verifiedUpdates);
        status.put("aggregatedUpdates", aggregatedUpdates);
        
        return status;
    }

    /**
     * 开始新的训练轮次
     */
    public Map<String, Object> startTrainingRound(String modelId, Integer targetClientCount) {
        FLModelEntity model = getModelById(modelId);
        
        // 检查模型状态
        if (model.getStatus() != FLModelEntity.ModelStatus.ACTIVE && 
            model.getStatus() != FLModelEntity.ModelStatus.TRAINING) {
            throw new IllegalStateException("Model is not in a valid state for training: " + model.getStatus());
        }
        
        // 更新模型状态
        model.setStatus(FLModelEntity.ModelStatus.TRAINING);
        model.setCurrentTrainingRound(model.getCurrentTrainingRound() + 1);
        model.setRoundStartTime(LocalDateTime.now());
        modelRepository.save(model);
        
        // 获取可用的客户端
        List<FLClientEntity> availableClients = clientRepository.findEligibleClientsForTraining(
            model.getServerId(), 
            targetClientCount,
            model.getRequireChargingForTraining(),
            model.getRequireWifiForTraining(),
            model.getMinimumBatteryLevel()
        );
        
        // 选择客户端
        List<FLClientEntity> selectedClients = selectClientsForRound(availableClients, targetClientCount);
        
        // 创建训练轮次记录
        FLTrainingRoundEntity trainingRound = new FLTrainingRoundEntity();
        trainingRound.setModelId(modelId);
        trainingRound.setRoundNumber(model.getCurrentTrainingRound());
        trainingRound.setServerId(model.getServerId());
        trainingRound.setTargetClientCount(targetClientCount);
        trainingRound.setSelectedClientCount(selectedClients.size());
        trainingRound.setStatus(FLTrainingRoundEntity.RoundStatus.SELECTING_CLIENTS);
        trainingRound.setStartTime(LocalDateTime.now());
        
        // TODO: 保存训练轮次到数据库
        
        // 准备响应
        Map<String, Object> response = new HashMap<>();
        response.put("roundId", trainingRound.getRoundId());
        response.put("modelId", modelId);
        response.put("roundNumber", model.getCurrentTrainingRound());
        response.put("selectedClients", selectedClients.stream()
            .map(client -> Map.of(
                "clientId", client.getClientId(),
                "deviceType", client.getDeviceType(),
                "lastTrainingTime", client.getLastTrainingTime()
            ))
            .collect(Collectors.toList()));
        response.put("targetAccuracy", model.getTargetAccuracy());
        response.put("maxTrainingRounds", model.getMaxTrainingRounds());
        response.put("privacyBudgetPerRound", model.getPrivacyBudgetPerRound());
        response.put("estimatedCompletionTime", LocalDateTime.now().plusMinutes(model.getTrainingRoundDurationMinutes()));
        
        return response;
    }

    /**
     * 处理客户端模型更新
     */
    public FLModelUpdateEntity processClientUpdate(String modelId, String clientId, 
                                                   Map<String, Object> updateData) {
        
        FLModelEntity model = getModelById(modelId);
        FLClientEntity client = getClientById(clientId);
        
        // 验证客户端是否被选中参与当前轮次
        if (!isClientSelectedForRound(clientId, model.getCurrentTrainingRound())) {
            throw new IllegalArgumentException("Client is not selected for current training round");
        }
        
        // 检查隐私预算
        PrivacyBudgetEntity privacyBudget = privacyBudgetRepository.findByClientIdAndModelId(clientId, modelId)
            .orElseGet(() -> createPrivacyBudget(clientId, modelId, model.getPrivacyBudgetPerRound()));
        
        if (privacyBudget.getRemainingEpsilon() < model.getPrivacyBudgetPerRound()) {
            throw new IllegalStateException("Insufficient privacy budget for client: " + 
                privacyBudget.getRemainingEpsilon() + " < " + model.getPrivacyBudgetPerRound());
        }
        
        // 创建模型更新记录
        FLModelUpdateEntity modelUpdate = new FLModelUpdateEntity(modelId, model.getServerId(), clientId, 
                                                                  model.getCurrentTrainingRound());
        
        // 设置更新数据
        modelUpdate.setEncryptedUpdateData((String) updateData.get("encryptedUpdateData"));
        modelUpdate.setEncryptionKeyId((String) updateData.get("encryptionKeyId"));
        modelUpdate.setDataSizeBytes((Integer) updateData.get("dataSizeBytes"));
        modelUpdate.setLocalTrainingSamples((Integer) updateData.get("localTrainingSamples"));
        modelUpdate.setTrainingLoss((Double) updateData.get("trainingLoss"));
        modelUpdate.setTrainingAccuracy((Double) updateData.get("trainingAccuracy"));
        modelUpdate.setTrainingEpochs((Integer) updateData.get("trainingEpochs"));
        modelUpdate.setTrainingBatchSize((Integer) updateData.get("trainingBatchSize"));
        modelUpdate.setLearningRate((Double) updateData.get("learningRate"));
        modelUpdate.setTrainingDurationMs((Long) updateData.get("trainingDurationMs"));
        
        // 设备信息
        modelUpdate.setDeviceCpuUsage((Double) updateData.get("deviceCpuUsage"));
        modelUpdate.setDeviceMemoryUsage((Double) updateData.get("deviceMemoryUsage"));
        modelUpdate.setDeviceBatteryLevel((Integer) updateData.get("deviceBatteryLevel"));
        modelUpdate.setDeviceWasCharging((Boolean) updateData.get("deviceWasCharging"));
        
        // 网络信息
        modelUpdate.setNetworkType((String) updateData.get("networkType"));
        modelUpdate.setNetworkBandwidthMbps((Double) updateData.get("networkBandwidthMbps"));
        modelUpdate.setNetworkLatencyMs((Double) updateData.get("networkLatencyMs"));
        
        // 隐私保护设置
        modelUpdate.setEnableDifferentialPrivacy(model.getEnableDifferentialPrivacy());
        modelUpdate.setPrivacyEpsilonUsed(model.getPrivacyBudgetPerRound());
        modelUpdate.setPrivacyDeltaUsed(model.getPrivacyDelta());
        modelUpdate.setDpNoiseScale(model.getDpNoiseScale());
        modelUpdate.setDpClipNorm(model.getDpClipNorm());
        modelUpdate.setPrivacyLevel(FLModelUpdateEntity.PrivacyLevel.valueOf(model.getPrivacyLevel()));
        
        // 安全聚合设置
        modelUpdate.setEnableSecureAggregation(model.getEnableSecureAggregation());
        modelUpdate.setAggregationGroupId(generateAggregationGroupId(modelId, model.getCurrentTrainingRound()));
        
        // 模型压缩设置
        modelUpdate.setEnableModelCompression(model.getEnableModelCompression());
        modelUpdate.setCompressionRatio(model.getModelCompressionRatio());
        
        // 计算质量分数
        modelUpdate.calculateQualityScore();
        modelUpdate.setContributionScore(modelUpdate.calculateContributionScore());
        
        // 检查异常
        checkForAnomalies(modelUpdate);
        
        // 保存更新
        modelUpdateRepository.save(modelUpdate);
        
        // 更新隐私预算
        privacyBudget.setUsedEpsilon(privacyBudget.getUsedEpsilon() + model.getPrivacyBudgetPerRound());
        privacyBudget.setRemainingEpsilon(privacyBudget.getTotalEpsilon() - privacyBudget.getUsedEpsilon());
        privacyBudgetRepository.save(privacyBudget);
        
        // 更新客户端统计
        client.setLastTrainingTime(LocalDateTime.now());
        client.setTotalTrainingRounds(client.getTotalTrainingRounds() + 1);
        client.setLastTrainingAccuracy(modelUpdate.getTrainingAccuracy());
        clientRepository.save(client);
        
        // 缓存更新
        String roundKey = modelId + "_" + model.getCurrentTrainingRound();
        roundUpdatesCache.computeIfAbsent(roundKey, k -> new ArrayList<>()).add(modelUpdate);
        
        return modelUpdate;
    }

    /**
     * 执行模型聚合
     */
    public Map<String, Object> aggregateModelUpdates(String modelId, Integer roundNumber) {
        FLModelEntity model = getModelById(modelId);
        
        // 获取该轮次的所有更新
        List<FLModelUpdateEntity> updates = modelUpdateRepository.findByModelIdAndTrainingRoundAndStatus(
            modelId, roundNumber, FLModelUpdateEntity.UpdateStatus.VERIFIED);
        
        if (updates.isEmpty()) {
            throw new IllegalStateException("No verified updates found for aggregation");
        }
        
        // 过滤掉异常更新
        List<FLModelUpdateEntity> validUpdates = updates.stream()
            .filter(update -> !update.getIsAnomalous())
            .filter(update -> update.getQualityScore() >= 0.7)
            .collect(Collectors.toList());
        
        if (validUpdates.size() < model.getMinClientsPerRound()) {
            throw new IllegalStateException("Insufficient valid updates for aggregation: " + 
                validUpdates.size() + " < " + model.getMinClientsPerRound());
        }
        
        // 执行聚合
        Map<String, Object> aggregationResult = performAggregation(validUpdates, model);
        
        // 更新模型
        model.setStatus(FLModelEntity.ModelStatus.AGGREGATING);
        model.setLastAggregationTime(LocalDateTime.now());
        model.setLastAggregationRound(roundNumber);
        model.setAggregatedClientCount(validUpdates.size());
        model.setAverageClientAccuracy(validUpdates.stream()
            .mapToDouble(FLModelUpdateEntity::getTrainingAccuracy)
            .average()
            .orElse(0.0));
        
        // 检查模型是否收敛
        boolean isConverged = checkModelConvergence(model, aggregationResult);
        if (isConverged) {
            model.setStatus(FLModelEntity.ModelStatus.CONVERGED);
            model.setConvergenceRound(roundNumber);
            model.setConvergenceTime(LocalDateTime.now());
        } else if (roundNumber >= model.getMaxTrainingRounds()) {
            model.setStatus(FLModelEntity.ModelStatus.MAX_ROUNDS_REACHED);
        } else {
            model.setStatus(FLModelEntity.ModelStatus.ACTIVE);
        }
        
        modelRepository.save(model);
        
        // 标记更新为已聚合
        validUpdates.forEach(update -> {
            update.updateStatus(FLModelUpdateEntity.UpdateStatus.AGGREGATED);
            update.setIncludedInAggregation(true);
            update.setAggregationRound(roundNumber);
            modelUpdateRepository.save(update);
        });
        
        // 更新服务器统计
        FederatedLearningServerEntity server = getServerById(model.getServerId());
        server.updateAfterTrainingRound(
            model.getAverageClientAccuracy(),
            validUpdates.stream().mapToDouble(FLModelUpdateEntity::getTrainingLoss).average().orElse(0.0),
            model.getTrainingRoundDurationMinutes()
        );
        serverRepository.save(server);
        
        // 准备响应
        Map<String, Object> response = new HashMap<>();
        response.put("modelId", modelId);
        response.put("roundNumber", roundNumber);
        response.put("aggregatedUpdates", validUpdates.size());
        response.put("totalUpdates", updates.size());
        response.put("averageAccuracy", model.getAverageClientAccuracy());
        response.put("aggregationAlgorithm", model.getAggregationAlgorithm());
        response.put("modelConverged", isConverged);
        response.put("newModelVersion", model.getVersion() + "." + roundNumber);
        response.put("aggregationResult", aggregationResult);
        
        return response;
    }

    /**
     * 获取智能回复建议
     */
    public Map<String, Object> getSmartReplySuggestions(String userId, String message, String context, 
                                                        String language, Integer maxSuggestions) {
        
        // 获取用户的个性化模型
        FLModelEntity personalizedModel = getOrCreatePersonalizedModel(userId, "SMART_REPLY", language);
        
        // 准备推理请求
        Map<String, Object> inferenceRequest = new HashMap<>();
        inferenceRequest.put("modelId", personalizedModel.getModelId());
        inferenceRequest.put("userId", userId);
        inferenceRequest.put("message", message);
        inferenceRequest.put("context", context);
        inferenceRequest.put("language", language);
        inferenceRequest.put("maxSuggestions", maxSuggestions != null ? maxSuggestions : 5);
        inferenceRequest.put("privacyLevel", personalizedModel.getPrivacyLevel());
        
        // TODO: 调用模型推理服务
        // 这里简化处理，返回示例回复
        List<String> suggestions = generateExampleSuggestions(message, language, maxSuggestions);
        
        // 记录推理历史（隐私保护）
        recordInferenceHistory(userId, "SMART_REPLY", message, suggestions, personalizedModel.getPrivacyLevel());
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("modelId", personalizedModel.getModelId());
        response.put("modelVersion", personalizedModel.getVersion());
        response.put("suggestions", suggestions);
        response.put("privacyProtected", true);
        response.put("localTrainingOnly", true);
        response.put("generatedAt", LocalDateTime.now());
        
        return response;
    }

    /**
     * 检测垃圾消息
     */
    public Map<String, Object> detectSpamMessage(String userId, String message, String sender, 
                                                 Map<String, Object> messageMetadata) {
        
        FLModelEntity spamModel = getModelByType("SPAM_DETECTION", "GLOBAL");
        
        // 准备检测请求
        Map<String, Object> detectionRequest = new HashMap<>();
        detectionRequest.put("modelId", spamModel.getModelId());
        detectionRequest.put("userId", userId);
        detectionRequest.put("message", message);
        detectionRequest.put("sender", sender);
        detectionRequest.put("metadata", messageMetadata);
        detectionRequest.put("privacyLevel", spamModel.getPrivacyLevel());
        
        // TODO: 调用垃圾检测模型
        // 这里简化处理，返回示例检测结果
        boolean isSpam = Math.random() < 0.1; // 10%概率检测为垃圾
        double confidence = isSpam ? 0.85 + Math.random() * 0.15 : 0.1 + Math.random() * 0.1;
        List<String> reasons = isSpam ? Arrays.asList("SUSPICIOUS_PATTERNS", "UNKNOWN_SENDER") : Collections.emptyList();
        
        // 记录检测结果（隐私保护）
        if (isSpam) {
            recordSpamDetection(userId, message, sender, confidence, reasons, spamModel.getPrivacyLevel());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("messageId", messageMetadata.get("messageId"));
        response.put("isSpam", isSpam);
        response.put("confidence", confidence);
        response.put("reasons", reasons);
        response.put("modelId", spamModel.getModelId());
        response.put("modelVersion", spamModel.getVersion());
        response.put("privacyProtected", true);
        response.put("detectedAt", LocalDateTime.now());
        
        return response;
    }

    /**
     * 分析消息情感
     */
    public Map<String, Object> analyzeMessageSentiment(String userId, String message, String language) {
        
        FLModelEntity sentimentModel = getModelByType("SENTIMENT_ANALYSIS", language.toUpperCase());
        
        // 准备分析请求
        Map<String, Object> analysisRequest = new HashMap<>();
        analysisRequest.put("modelId", sentimentModel.getModelId());
        analysisRequest.put("userId", userId);
        analysisRequest.put("message", message);
        analysisRequest.put("language", language);
        analysisRequest.put("privacyLevel", sentimentModel.getPrivacyLevel());
        
        // TODO: 调用情感分析模型
        // 这里简化处理，返回示例情感分析
        String sentiment = getRandomSentiment();
        double positiveScore = Math.random();
        double negativeScore = Math.random();
        double neutralScore = Math.random();
        
        // 归一化
        double total = positiveScore + negativeScore + neutralScore;
        positiveScore /= total;
        negativeScore /= total;
        neutralScore /= total;
        
        // 记录分析结果（隐私保护）
        recordSentimentAnalysis(userId, message, sentiment, positiveScore, sentimentModel.getPrivacyLevel());
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("message", message.substring(0, Math.min(message.length(), 50)) + "...");
        response.put("sentiment", sentiment);
        response.put("positiveScore", positiveScore);
        response.put("negativeScore", negativeScore);
        response.put("neutralScore", neutralScore);
        response.put("dominantSentiment", getDominantSentiment(positiveScore, negativeScore, neutralScore));
        response.put("modelId", sentimentModel.getModelId());
        response.put("modelVersion", sentimentModel.getVersion());
        response.put("privacyProtected", true);
        response.put("analyzedAt", LocalDateTime.now());
        
        return response;
    }

    // 私有辅助方法
    private FederatedLearningServerEntity getServerById(String serverId) {
        return serverCache.computeIfAbsent(serverId, id -> 
            serverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Server not found: " + serverId))
        );
    }

    private FLModelEntity getModelById(String modelId) {
        return modelCache.computeIfAbsent(modelId, id -> 
            modelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId))
        );
    }

    private FLClientEntity getClientById(String clientId) {
        return clientRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
    }

    private FLModelEntity getModelByType(String modelType, String language) {
        return modelRepository.findByModelTypeAndLanguageAndStatus(
            modelType, language, FLModelEntity.ModelStatus.ACTIVE)
            .orElseThrow(() -> new IllegalArgumentException("Active model not found for type: " + modelType + ", language: " + language));
    }

    private List<FLClientEntity> selectClientsForRound(List<FLClientEntity> availableClients, Integer targetCount) {
        // 简单选择算法：基于上次训练时间和设备能力
        return availableClients.stream()
            .sorted(Comparator.comparing(FLClientEntity::getLastTrainingTime)
                .thenComparing(FLClientEntity::getDeviceCapabilityScore).reversed())
            .limit(Math.min(targetCount, availableClients.size()))
            .collect(Collectors.toList());
    }

    private boolean isClientSelectedForRound(String clientId, Integer roundNumber) {
        // TODO: 实现检查逻辑
        return true; // 简化处理
    }

    private PrivacyBudgetEntity createPrivacyBudget(String clientId, String modelId, Double budgetPerRound) {
        PrivacyBudgetEntity privacyBudget = new PrivacyBudgetEntity();
        privacyBudget.setClientId(clientId);
        privacyBudget.setModelId(modelId);
        privacyBudget.setTotalEpsilon(100.0); // 总预算
        privacyBudget.setUsedEpsilon(0.0);
        privacyBudget.setRemainingEpsilon(100.0);
        privacyBudget.setMaxRounds((int)(100.0 / budgetPerRound));
        privacyBudget.setResetSchedule("MONTHLY");
        privacyBudgetRepository.save(privacyBudget);
        return privacyBudget;
    }

    private String generateAggregationGroupId(String modelId, Integer roundNumber) {
        return modelId + "_round_" + roundNumber + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private Map<String, Object> performAggregation(List<FLModelUpdateEntity> updates, FLModelEntity model) {
        // 简化的聚合逻辑
        Map<String, Object> result = new HashMap<>();
        
        double totalSamples = updates.stream().mapToInt(FLModelUpdateEntity::getLocalTrainingSamples).sum();
        double averageAccuracy = updates.stream()
            .mapToDouble(FLModelUpdateEntity::getTrainingAccuracy)
            .average().orElse(0.0);
        double averageLoss = updates.stream()
            .mapToDouble(FLModelUpdateEntity::getTrainingLoss)
            .average().orElse(0.0);
        
        // 加权平均（基于样本数）
        double weightedAccuracy = updates.stream()
            .mapToDouble(update -> update.getTrainingAccuracy() * update.getLocalTrainingSamples())
            .sum() / totalSamples;
        
        result.put("aggregationAlgorithm", model.getAggregationAlgorithm());
        result.put("totalSamples", totalSamples);
        result.put("clientCount", updates.size());
        result.put("averageAccuracy", averageAccuracy);
        result.put("weightedAccuracy", weightedAccuracy);
        result.put("averageLoss", averageLoss);
        result.put("privacyEpsilonUsed", updates.stream()
            .mapToDouble(FLModelUpdateEntity::getPrivacyEpsilonUsed)
            .sum());
        result.put("aggregationTime", LocalDateTime.now());
        
        return result;
    }

    private boolean checkModelConvergence(FLModelEntity model, Map<String, Object> aggregationResult) {
        // 简化的收敛检查
        double currentAccuracy = (Double) aggregationResult.get("weightedAccuracy");
        double targetAccuracy = model.getTargetAccuracy();
        
        return currentAccuracy >= targetAccuracy;
    }

    private FLModelEntity getOrCreatePersonalizedModel(String userId, String modelType, String language) {
        // 尝试获取个性化模型
        Optional<FLModelEntity> existingModel = modelRepository.findByUserIdAndModelTypeAndLanguage(
            userId, modelType, language);
        
        if (existingModel.isPresent()) {
            return existingModel.get();
        }
        
        // 创建新的个性化模型
        FLModelEntity model = new FLModelEntity();
        model.setUserId(userId);
        model.setModelType(modelType);
        model.setLanguage(language);
        model.setModelName("Personalized_" + modelType + "_" + userId.substring(0, 8));
        model.setStatus(FLModelEntity.ModelStatus.INITIALIZING);
        model.setVersion("1.0.0");
        model.setAggregationAlgorithm("FedAvg");
        model.setTargetAccuracy(0.85);
        model.setMaxTrainingRounds(50);
        model.setPrivacyLevel("STANDARD");
        model.setEnableDifferentialPrivacy(true);
        model.setPrivacyBudgetPerRound(1.0);
        model.setCreatedAt(LocalDateTime.now());
        
        modelRepository.save(model);
        modelCache.put(model.getModelId(), model);
        
        return model;
    }

    private List<String> generateExampleSuggestions(String message, String language, Integer maxSuggestions) {
        List<String> suggestions = new ArrayList<>();
        
        if (language.startsWith("zh")) {
            suggestions.add("好的，明白了");
            suggestions.add("谢谢分享");
            suggestions.add("我同意");
            suggestions.add("稍后回复");
            suggestions.add("需要更多信息");
        } else {
            suggestions.add("OK, got it");
            suggestions.add("Thanks for sharing");
            suggestions.add("I agree");
            suggestions.add("Will reply later");
            suggestions.add("Need more information");
        }
        
        return suggestions.stream().limit(maxSuggestions).collect(Collectors.toList());
    }

    private String getRandomSentiment() {
        String[] sentiments = {"POSITIVE", "NEGATIVE", "NEUTRAL"};
        return sentiments[(int)(Math.random() * sentiments.length)];
    }

    private String getDominantSentiment(double positive, double negative, double neutral) {
        if (positive >= negative && positive >= neutral) return "POSITIVE";
        if (negative >= positive && negative >= neutral) return "NEGATIVE";
        return "NEUTRAL";
    }

    private void checkForAnomalies(FLModelUpdateEntity update) {
        // 简化的异常检测
        boolean isAnomalous = false;
        String anomalyReason = null;
        double anomalyScore = 0.0;
        
        // 检查准确率异常
        if (update.getTrainingAccuracy() > 0.99) {
            isAnomalous = true;
            anomalyReason = "UNUSUALLY_HIGH_ACCURACY";
            anomalyScore += 0.3;
        }
        
        // 检查样本数异常
        if (update.getLocalTrainingSamples() > 10000) {
            isAnomalous = true;
            anomalyReason = "EXCESSIVE_TRAINING_SAMPLES";
            anomalyScore += 0.4;
        }
        
        // 检查训练时间异常
        if (update.getTrainingDurationMs() < 1000) {
            isAnomalous = true;
            anomalyReason = "SUSPICIOUSLY_SHORT_TRAINING";
            anomalyScore += 0.3;
        }
        
        update.setIsAnomalous(isAnomalous);
        update.setAnomalyReason(anomalyReason);
        update.setAnomalyScore(anomalyScore);
    }

    private void recordInferenceHistory(String userId, String modelType, String message, 
                                        List<String> suggestions, String privacyLevel) {
        // 隐私保护的推理历史记录
        // TODO: 实现加密存储
    }

    private void recordSpamDetection(String userId, String message, String sender, 
                                     double confidence, List<String> reasons, String privacyLevel) {
        // 隐私保护的垃圾检测记录
        // TODO: 实现加密存储
    }

    private void recordSentimentAnalysis(String userId, String message, String sentiment, 
                                         double score, String privacyLevel) {
        // 隐私保护的情感分析记录
        // TODO: 实现加密存储
    }
}