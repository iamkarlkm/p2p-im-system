package com.im.service.agent.service;

import com.im.service.agent.dto.*;
import com.im.service.agent.entity.*;
import com.im.service.agent.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 边缘端侧AI推理加速服务
 * 功能#350: 模型量化/剪枝/蒸馏、端侧部署、云端协同、热更新
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EdgeAIInferenceService {

    private final EdgeModelRepository modelRepository;
    private final EdgeDeviceRepository deviceRepository;
    private final InferenceRecordRepository inferenceRepository;
    private final ModelVersionRepository versionRepository;

    // ========== 1. 模型轻量化 ==========

    /**
     * 模型量化 (INT4/INT8)
     */
    @Transactional
    public ModelQuantizationResponse quantizeModel(Long merchantId, ModelQuantizationRequest request) {
        log.info("执行模型量化: {} -> {}", request.getModelName(), request.getTargetPrecision());

        // 验证目标精度
        if (!Arrays.asList("INT8", "INT4", "FP16").contains(request.getTargetPrecision())) {
            throw new RuntimeException("不支持的目标精度: " + request.getTargetPrecision());
        }

        // 执行量化（模拟）
        String quantizedModelId = UUID.randomUUID().toString();
        
        // 计算压缩率
        double compressionRatio = calculateCompressionRatio(request.getTargetPrecision());
        long originalSize = request.getOriginalModelSize();
        long quantizedSize = (long) (originalSize / compressionRatio);

        // 估算精度损失
        double accuracyLoss = estimateAccuracyLoss(request.getTargetPrecision());

        // 保存量化模型
        EdgeModel quantizedModel = new EdgeModel();
        quantizedModel.setModelId(quantizedModelId);
        quantizedModel.setMerchantId(merchantId);
        quantizedModel.setModelName(request.getModelName() + "_" + request.getTargetPrecision());
        quantizedModel.setOriginalModelId(request.getModelId());
        quantizedModel.setPrecision(request.getTargetPrecision());
        quantizedModel.setModelSize(quantizedSize);
        quantizedModel.setCompressionRatio(compressionRatio);
        quantizedModel.setEstimatedAccuracyLoss(accuracyLoss);
        quantizedModel.setStatus("QUANTIZED");
        quantizedModel.setCreateTime(LocalDateTime.now());
        modelRepository.save(quantizedModel);

        ModelQuantizationResponse response = new ModelQuantizationResponse();
        response.setQuantizedModelId(quantizedModelId);
        response.setOriginalModelId(request.getModelId());
        response.setTargetPrecision(request.getTargetPrecision());
        response.setOriginalSize(originalSize);
        response.setQuantizedSize(quantizedSize);
        response.setCompressionRatio(compressionRatio);
        response.setEstimatedAccuracyLoss(accuracyLoss);
        response.setStatus("SUCCESS");

        return response;
    }

    /**
     * 模型剪枝
     */
    @Transactional
    public ModelPruningResponse pruneModel(Long merchantId, ModelPruningRequest request) {
        log.info("执行模型剪枝: {}, 稀疏度={}", request.getModelName(), request.getSparsity());

        // 验证稀疏度
        if (request.getSparsity() < 0.1 || request.getSparsity() > 0.9) {
            throw new RuntimeException("稀疏度必须在0.1-0.9之间");
        }

        // 执行剪枝（模拟）
        String prunedModelId = UUID.randomUUID().toString();
        
        // 计算剪枝后的模型大小
        double sizeReduction = 1 - (request.getSparsity() * 0.7); // 假设剪枝后大小减少约70%的稀疏度
        long originalSize = request.getOriginalModelSize();
        long prunedSize = (long) (originalSize * sizeReduction);

        // 估算精度损失
        double accuracyLoss = request.getSparsity() * 0.05; // 假设每10%稀疏度损失0.5%精度

        // 保存剪枝模型
        EdgeModel prunedModel = new EdgeModel();
        prunedModel.setModelId(prunedModelId);
        prunedModel.setMerchantId(merchantId);
        prunedModel.setModelName(request.getModelName() + "_pruned");
        prunedModel.setOriginalModelId(request.getModelId());
        prunedModel.setPruningSparsity(request.getSparsity());
        prunedModel.setPruningMethod(request.getPruningMethod());
        prunedModel.setModelSize(prunedSize);
        prunedModel.setSizeReductionRatio(1 - sizeReduction);
        prunedModel.setEstimatedAccuracyLoss(accuracyLoss);
        prunedModel.setStatus("PRUNED");
        prunedModel.setCreateTime(LocalDateTime.now());
        modelRepository.save(prunedModel);

        ModelPruningResponse response = new ModelPruningResponse();
        response.setPrunedModelId(prunedModelId);
        response.setOriginalModelId(request.getModelId());
        response.setSparsity(request.getSparsity());
        response.setOriginalSize(originalSize);
        response.setPrunedSize(prunedSize);
        response.setSizeReductionRatio(1 - sizeReduction);
        response.setEstimatedAccuracyLoss(accuracyLoss);
        response.setStatus("SUCCESS");

        return response;
    }

    /**
     * 模型蒸馏
     */
    @Transactional
    public ModelDistillationResponse distillModel(Long merchantId, ModelDistillationRequest request) {
        log.info("执行模型蒸馏: 教师={}, 学生={}", request.getTeacherModelName(), request.getStudentModelName());

        // 执行蒸馏（模拟）
        String distilledModelId = UUID.randomUUID().toString();

        // 学生模型通常比教师模型小很多
        double sizeRatio = 0.2; // 假设学生模型是教师的20%大小
        long teacherSize = request.getTeacherModelSize();
        long studentSize = (long) (teacherSize * sizeRatio);

        // 估算精度保持率
        double accuracyRetention = 0.95; // 假设保持95%精度

        // 保存蒸馏模型
        EdgeModel distilledModel = new EdgeModel();
        distilledModel.setModelId(distilledModelId);
        distilledModel.setMerchantId(merchantId);
        distilledModel.setModelName(request.getStudentModelName());
        distilledModel.setTeacherModelId(request.getTeacherModelId());
        distilledModel.setModelSize(studentSize);
        distilledModel.setTemperature(request.getTemperature());
        distilledModel.setAlpha(request.getAlpha());
        distilledModel.setAccuracyRetention(accuracyRetention);
        distilledModel.setStatus("DISTILLED");
        distilledModel.setCreateTime(LocalDateTime.now());
        modelRepository.save(distilledModel);

        ModelDistillationResponse response = new ModelDistillationResponse();
        response.setDistilledModelId(distilledModelId);
        response.setTeacherModelId(request.getTeacherModelId());
        response.setTeacherModelName(request.getTeacherModelName());
        response.setStudentModelName(request.getStudentModelName());
        response.setTeacherSize(teacherSize);
        response.setStudentSize(studentSize);
        response.setCompressionRatio(teacherSize / (double) studentSize);
        response.setAccuracyRetention(accuracyRetention);
        response.setStatus("SUCCESS");

        return response;
    }

    // ========== 2. 端侧部署 ==========

    /**
     * 部署模型到边缘设备
     */
    @Transactional
    public EdgeDeploymentResponse deployModelToEdge(Long merchantId, EdgeDeploymentRequest request) {
        log.info("部署模型到边缘设备: model={}, device={}", request.getModelId(), request.getDeviceId());

        // 验证模型
        EdgeModel model = modelRepository.findByModelId(request.getModelId())
                .orElseThrow(() -> new RuntimeException("模型不存在"));

        // 验证设备
        EdgeDevice device = deviceRepository.findByDeviceId(request.getDeviceId())
                .orElseThrow(() -> new RuntimeException("设备不存在"));

        // 检查设备兼容性
        if (!isDeviceCompatible(device, model)) {
            throw new RuntimeException("设备不兼容此模型");
        }

        // 检查设备存储空间
        if (device.getAvailableStorage() < model.getModelSize()) {
            throw new RuntimeException("设备存储空间不足");
        }

        // 执行部署（模拟）
        String deploymentId = UUID.randomUUID().toString();

        // 更新设备模型列表
        device.getDeployedModels().add(model.getModelId());
        device.setAvailableStorage(device.getAvailableStorage() - model.getModelSize());
        device.setLastUpdateTime(LocalDateTime.now());
        deviceRepository.save(device);

        // 创建部署记录
        ModelDeployment deployment = new ModelDeployment();
        deployment.setDeploymentId(deploymentId);
        deployment.setModelId(request.getModelId());
        deployment.setDeviceId(request.getDeviceId());
        deployment.setMerchantId(merchantId);
        deployment.setStatus("DEPLOYED");
        deployment.setDeployTime(LocalDateTime.now());
        // deploymentRepository.save(deployment);

        EdgeDeploymentResponse response = new EdgeDeploymentResponse();
        response.setDeploymentId(deploymentId);
        response.setModelId(request.getModelId());
        response.setDeviceId(request.getDeviceId());
        response.setStatus("DEPLOYED");
        response.setDeployTime(LocalDateTime.now());
        response.setEstimatedInferenceLatency(estimateInferenceLatency(device, model));

        return response;
    }

    /**
     * 上传端侧模型（TinyLLM等）
     */
    @Transactional
    public TinyModelResponse uploadTinyModel(Long merchantId, MultipartFile file, 
                                              String modelName, String modelType) {
        log.info("上传端侧模型: {}, 类型={}", modelName, modelType);

        try {
            // 验证模型类型
            if (!Arrays.asList("TinyLLM", "Phi-2", "Gemma", "LLaMA-cpp", "MobileBERT").contains(modelType)) {
                throw new RuntimeException("不支持的模型类型: " + modelType);
            }

            // 验证文件大小（最大500MB）
            if (file.getSize() > 500 * 1024 * 1024) {
                throw new RuntimeException("模型文件不能超过500MB");
            }

            String modelId = UUID.randomUUID().toString();

            // 保存模型（模拟）
            EdgeModel model = new EdgeModel();
            model.setModelId(modelId);
            model.setMerchantId(merchantId);
            model.setModelName(modelName);
            model.setModelType(modelType);
            model.setModelSize(file.getSize());
            model.setFormat(getModelFormat(file.getOriginalFilename()));
            model.setStatus("UPLOADED");
            model.setCreateTime(LocalDateTime.now());
            modelRepository.save(model);

            TinyModelResponse response = new TinyModelResponse();
            response.setModelId(modelId);
            response.setModelName(modelName);
            response.setModelType(modelType);
            response.setModelSize(file.getSize());
            response.setStatus("UPLOADED");
            response.setUploadTime(LocalDateTime.now());

            return response;

        } catch (Exception e) {
            log.error("上传端侧模型失败", e);
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取小模型列表
     */
    public List<TinyModelResponse> getTinyModels(Long merchantId, String modelType) {
        List<EdgeModel> models;
        
        if (modelType != null && !modelType.isEmpty()) {
            models = modelRepository.findByMerchantIdAndModelType(merchantId, modelType);
        } else {
            models = modelRepository.findByMerchantId(merchantId);
        }

        return models.stream()
                .filter(m -> m.getModelSize() <= 500 * 1024 * 1024) // 只返回小模型
                .map(this::convertToTinyModelResponse)
                .collect(Collectors.toList());
    }

    // ========== 3. 推理执行 ==========

    /**
     * 执行边缘推理
     */
    public EdgeInferenceResponse executeInference(Long merchantId, EdgeInferenceRequest request) {
        long startTime = System.currentTimeMillis();

        // 获取模型
        EdgeModel model = modelRepository.findByModelId(request.getModelId())
                .orElseThrow(() -> new RuntimeException("模型不存在"));

        // 执行推理（模拟）
        // 实际实现应该调用边缘设备上的推理引擎
        
        // 模拟推理延迟
        int inferenceLatency = simulateInferenceLatency(model);
        
        // 模拟推理结果
        Map<String, Object> result = new HashMap<>();
        result.put("output", "inference_result");
        result.put("confidence", 0.95);

        // 记录推理日志
        InferenceRecord record = new InferenceRecord();
        record.setRecordId(UUID.randomUUID().toString());
        record.setMerchantId(merchantId);
        record.setModelId(request.getModelId());
        record.setInputType(request.getInputType());
        record.setInferenceLatency(inferenceLatency);
        record.setTotalLatency((int) (System.currentTimeMillis() - startTime));
        record.setTimestamp(LocalDateTime.now());
        inferenceRepository.save(record);

        EdgeInferenceResponse response = new EdgeInferenceResponse();
        response.setRecordId(record.getRecordId());
        response.setModelId(request.getModelId());
        response.setInferenceLatencyMs(inferenceLatency);
        response.setTotalLatencyMs((int) (System.currentTimeMillis() - startTime));
        response.setResult(result);
        response.setDeviceType("EDGE");

        return response;
    }

    /**
     * 执行云端协同推理
     */
    public HybridInferenceResponse executeHybridInference(Long merchantId, HybridInferenceRequest request) {
        log.info("执行云端协同推理");

        long startTime = System.currentTimeMillis();

        // 判断任务应该在边缘还是云端执行
        ExecutionPlan plan = decideExecutionPlan(request);

        Map<String, Object> result;
        int edgeLatency = 0;
        int cloudLatency = 0;

        if ("EDGE".equals(plan.getExecutionLocation())) {
            // 边缘执行
            EdgeInferenceRequest edgeRequest = new EdgeInferenceRequest();
            edgeRequest.setModelId(request.getEdgeModelId());
            edgeRequest.setInputData(request.getInputData());
            EdgeInferenceResponse edgeResponse = executeInference(merchantId, edgeRequest);
            result = (Map<String, Object>) edgeResponse.getResult();
            edgeLatency = edgeResponse.getInferenceLatencyMs();
        } else {
            // 云端执行
            cloudLatency = simulateCloudInference();
            result = new HashMap<>();
            result.put("output", "cloud_inference_result");
        }

        HybridInferenceResponse response = new HybridInferenceResponse();
        response.setExecutionLocation(plan.getExecutionLocation());
        response.setEdgeLatencyMs(edgeLatency);
        response.setCloudLatencyMs(cloudLatency);
        response.setTotalLatencyMs((int) (System.currentTimeMillis() - startTime));
        response.setResult(result);
        response.setFallbackToCloud(plan.isFallbackToCloud());

        return response;
    }

    // ========== 4. 热更新 ==========

    /**
     * 模型热更新
     */
    @Transactional
    public HotUpdateResponse hotUpdateModel(Long merchantId, String modelId, 
                                            String newVersion, Boolean grayRelease) {
        log.info("热更新模型: {}, 版本={}, 灰度={}", modelId, newVersion, grayRelease);

        EdgeModel model = modelRepository.findByModelId(modelId)
                .orElseThrow(() -> new RuntimeException("模型不存在"));

        if (!model.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权更新此模型");
        }

        // 创建新版本
        ModelVersion version = new ModelVersion();
        version.setVersionId(UUID.randomUUID().toString());
        version.setModelId(modelId);
        version.setVersionNumber(newVersion);
        version.setStatus(grayRelease ? "GRAY" : "ACTIVE");
        version.setCreateTime(LocalDateTime.now());
        versionRepository.save(version);

        // 如果是灰度发布，设置灰度比例
        if (grayRelease) {
            // 实际实现应该配置灰度规则
            log.info("模型 {} 开始灰度发布，版本 {}", modelId, newVersion);
        } else {
            // 全量发布
            model.setCurrentVersion(newVersion);
            modelRepository.save(model);
        }

        HotUpdateResponse response = new HotUpdateResponse();
        response.setModelId(modelId);
        response.setNewVersion(newVersion);
        response.setOldVersion(model.getCurrentVersion());
        response.setUpdateType(grayRelease ? "GRAY" : "FULL");
        response.setStatus("SUCCESS");
        response.setUpdateTime(LocalDateTime.now());

        return response;
    }

    // ========== 5. 设备管理 ==========

    /**
     * 注册边缘设备
     */
    @Transactional
    public DeviceRegistrationResponse registerDevice(Long merchantId, DeviceRegistrationRequest request) {
        log.info("注册边缘设备: {}", request.getDeviceName());

        String deviceId = UUID.randomUUID().toString();

        EdgeDevice device = new EdgeDevice();
        device.setDeviceId(deviceId);
        device.setMerchantId(merchantId);
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setOsType(request.getOsType());
        device.setHardwareInfo(request.getHardwareInfo());
        device.setNpuType(request.getNpuType());
        device.setTotalStorage(request.getTotalStorage());
        device.setAvailableStorage(request.getTotalStorage());
        device.setStatus("ONLINE");
        device.setRegisterTime(LocalDateTime.now());
        device.setLastHeartbeat(LocalDateTime.now());

        deviceRepository.save(device);

        DeviceRegistrationResponse response = new DeviceRegistrationResponse();
        response.setDeviceId(deviceId);
        response.setDeviceName(device.getDeviceName());
        response.setStatus("REGISTERED");
        response.setRegisterTime(device.getRegisterTime());

        return response;
    }

    /**
     * 获取设备列表
     */
    public List<DeviceInfoResponse> getDevices(Long merchantId) {
        List<EdgeDevice> devices = deviceRepository.findByMerchantId(merchantId);
        return devices.stream()
                .map(this::convertToDeviceInfoResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取设备性能
     */
    public DevicePerformanceResponse getDevicePerformance(Long merchantId, String deviceId) {
        EdgeDevice device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在"));

        if (!device.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权查看此设备");
        }

        DevicePerformanceResponse response = new DevicePerformanceResponse();
        response.setDeviceId(deviceId);
        response.setDeviceName(device.getDeviceName());
        response.setCpuUsage(Math.random() * 100);
        response.setMemoryUsage(Math.random() * 100);
        response.setNpuUsage(Math.random() * 100);
        response.setTemperature(40 + Math.random() * 20);
        response.setInferenceThroughput(50 + Math.random() * 100);
        response.setAverageLatency(100 + (int) (Math.random() * 100));

        return response;
    }

    /**
     * 获取推理统计
     */
    public InferenceStatsResponse getInferenceStats(Long merchantId, String startDate, String endDate) {
        // 获取统计记录
        List<InferenceRecord> records = inferenceRepository.findByMerchantIdAndTimeRange(
                merchantId, startDate, endDate);

        int totalInferences = records.size();
        double avgLatency = records.stream()
                .mapToInt(InferenceRecord::getInferenceLatency)
                .average()
                .orElse(0);
        int minLatency = records.stream()
                .mapToInt(InferenceRecord::getInferenceLatency)
                .min()
                .orElse(0);
        int maxLatency = records.stream()
                .mapToInt(InferenceRecord::getInferenceLatency)
                .max()
                .orElse(0);

        InferenceStatsResponse response = new InferenceStatsResponse();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setTotalInferences(totalInferences);
        response.setAverageLatencyMs((int) avgLatency);
        response.setMinLatencyMs(minLatency);
        response.setMaxLatencyMs(maxLatency);
        response.setP95Latency((int) (avgLatency * 1.5));
        response.setP99Latency((int) (avgLatency * 2));

        return response;
    }

    // ========== 私有方法 ==========

    private double calculateCompressionRatio(String precision) {
        switch (precision) {
            case "INT8": return 4.0; // FP32 -> INT8 = 4x压缩
            case "INT4": return 8.0; // FP32 -> INT4 = 8x压缩
            case "FP16": return 2.0; // FP32 -> FP16 = 2x压缩
            default: return 1.0;
        }
    }

    private double estimateAccuracyLoss(String precision) {
        switch (precision) {
            case "INT8": return 0.01; // 约1%精度损失
            case "INT4": return 0.03; // 约3%精度损失
            case "FP16": return 0.005; // 约0.5%精度损失
            default: return 0;
        }
    }

    private boolean isDeviceCompatible(EdgeDevice device, EdgeModel model) {
        // 检查设备NPU类型是否支持模型
        return true; // 简化实现
    }

    private int estimateInferenceLatency(EdgeDevice device, EdgeModel model) {
        // 根据设备性能和模型大小估算延迟
        return 50 + (int) (model.getModelSize() / 1024 / 1024); // 基础50ms + 每MB 1ms
    }

    private String getModelFormat(String filename) {
        if (filename.endsWith(".onnx")) return "ONNX";
        if (filename.endsWith(".tflite")) return "TFLite";
        if (filename.endsWith(".pt") || filename.endsWith(".pth")) return "PyTorch";
        if (filename.endsWith(".gguf")) return "GGUF";
        return "UNKNOWN";
    }

    private int simulateInferenceLatency(EdgeModel model) {
        // 模拟推理延迟
        int baseLatency = 50;
        int sizeFactor = (int) (model.getModelSize() / 1024 / 1024 / 10);
        return baseLatency + sizeFactor + (int) (Math.random() * 20);
    }

    private int simulateCloudInference() {
        // 模拟云端推理延迟（通常比边缘慢，因为有网络延迟）
        return 200 + (int) (Math.random() * 100);
    }

    private ExecutionPlan decideExecutionPlan(HybridInferenceRequest request) {
        ExecutionPlan plan = new ExecutionPlan();
        
        // 根据输入数据大小和复杂度决定执行位置
        int inputSize = request.getInputData().toString().length();
        
        if (inputSize < 1000 && request.getEdgeModelId() != null) {
            // 小数据且有边缘模型，在边缘执行
            plan.setExecutionLocation("EDGE");
            plan.setFallbackToCloud(true);
        } else {
            // 大数据或没有边缘模型，在云端执行
            plan.setExecutionLocation("CLOUD");
            plan.setFallbackToCloud(false);
        }
        
        return plan;
    }

    private TinyModelResponse convertToTinyModelResponse(EdgeModel model) {
        TinyModelResponse response = new TinyModelResponse();
        response.setModelId(model.getModelId());
        response.setModelName(model.getModelName());
        response.setModelType(model.getModelType());
        response.setModelSize(model.getModelSize());
        response.setStatus(model.getStatus());
        response.setUploadTime(model.getCreateTime());
        return response;
    }

    private DeviceInfoResponse convertToDeviceInfoResponse(EdgeDevice device) {
        DeviceInfoResponse response = new DeviceInfoResponse();
        response.setDeviceId(device.getDeviceId());
        response.setDeviceName(device.getDeviceName());
        response.setDeviceType(device.getDeviceType());
        response.setOsType(device.getOsType());
        response.setNpuType(device.getNpuType());
        response.setStatus(device.getStatus());
        response.setDeployedModelCount(device.getDeployedModels().size());
        return response;
    }
}
