package com.im.service.agent.controller;

import com.im.service.agent.dto.*;
import com.im.service.agent.service.EdgeAIInferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 边缘端侧AI推理加速控制器
 * 功能#350: 模型轻量化与端侧部署
 */
@Slf4j
@RestController
@RequestMapping("/api/agent/edge-ai")
@RequiredArgsConstructor
public class EdgeAIInferenceController {

    private final EdgeAIInferenceService edgeAIService;

    /**
     * 模型量化
     */
    @PostMapping("/model/quantize")
    public ResponseEntity<ModelQuantizationResponse> quantizeModel(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ModelQuantizationRequest request) {
        log.info("商家 {} 执行模型量化: {} -> {}", merchantId, request.getModelName(), request.getTargetPrecision());
        ModelQuantizationResponse response = edgeAIService.quantizeModel(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 模型剪枝
     */
    @PostMapping("/model/prune")
    public ResponseEntity<ModelPruningResponse> pruneModel(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ModelPruningRequest request) {
        log.info("商家 {} 执行模型剪枝: {}", merchantId, request.getModelName());
        ModelPruningResponse response = edgeAIService.pruneModel(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 模型蒸馏
     */
    @PostMapping("/model/distill")
    public ResponseEntity<ModelDistillationResponse> distillModel(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody ModelDistillationRequest request) {
        log.info("商家 {} 执行模型蒸馏: 教师={}, 学生={}", merchantId, 
                request.getTeacherModelName(), request.getStudentModelName());
        ModelDistillationResponse response = edgeAIService.distillModel(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 部署模型到边缘设备
     */
    @PostMapping("/model/deploy")
    public ResponseEntity<EdgeDeploymentResponse> deployModelToEdge(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody EdgeDeploymentRequest request) {
        log.info("商家 {} 部署模型到边缘设备: {} -> {}", merchantId, 
                request.getModelId(), request.getDeviceId());
        EdgeDeploymentResponse response = edgeAIService.deployModelToEdge(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 上传端侧模型
     */
    @PostMapping("/model/upload-tiny")
    public ResponseEntity<TinyModelResponse> uploadTinyModel(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String modelName,
            @RequestParam String modelType) {
        log.info("商家 {} 上传端侧模型: {}", merchantId, modelName);
        TinyModelResponse response = edgeAIService.uploadTinyModel(merchantId, file, modelName, modelType);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取可用的小模型列表
     */
    @GetMapping("/models/tiny")
    public ResponseEntity<List<TinyModelResponse>> getTinyModels(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam(required = false) String modelType) {
        List<TinyModelResponse> models = edgeAIService.getTinyModels(merchantId, modelType);
        return ResponseEntity.ok(models);
    }

    /**
     * 执行边缘推理
     */
    @PostMapping("/inference")
    public ResponseEntity<EdgeInferenceResponse> executeInference(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody EdgeInferenceRequest request) {
        log.debug("商家 {} 执行边缘推理: model={}", merchantId, request.getModelId());
        EdgeInferenceResponse response = edgeAIService.executeInference(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 执行云端协同推理
     */
    @PostMapping("/inference/hybrid")
    public ResponseEntity<HybridInferenceResponse> executeHybridInference(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody HybridInferenceRequest request) {
        log.info("商家 {} 执行云端协同推理", merchantId);
        HybridInferenceResponse response = edgeAIService.executeHybridInference(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取设备推理性能
     */
    @GetMapping("/device/{deviceId}/performance")
    public ResponseEntity<DevicePerformanceResponse> getDevicePerformance(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String deviceId) {
        DevicePerformanceResponse response = edgeAIService.getDevicePerformance(merchantId, deviceId);
        return ResponseEntity.ok(response);
    }

    /**
     * 模型热更新
     */
    @PostMapping("/model/{modelId}/hot-update")
    public ResponseEntity<HotUpdateResponse> hotUpdateModel(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable String modelId,
            @RequestParam String newVersion,
            @RequestParam(defaultValue = "false") Boolean grayRelease) {
        log.info("商家 {} 热更新模型 {} 到版本 {}, 灰度={}", merchantId, modelId, newVersion, grayRelease);
        HotUpdateResponse response = edgeAIService.hotUpdateModel(merchantId, modelId, newVersion, grayRelease);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取推理统计
     */
    @GetMapping("/inference/stats")
    public ResponseEntity<InferenceStatsResponse> getInferenceStats(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        InferenceStatsResponse response = edgeAIService.getInferenceStats(merchantId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * 注册边缘设备
     */
    @PostMapping("/device/register")
    public ResponseEntity<DeviceRegistrationResponse> registerDevice(
            @RequestHeader("X-Merchant-Id") Long merchantId,
            @Valid @RequestBody DeviceRegistrationRequest request) {
        log.info("商家 {} 注册边缘设备: {}", merchantId, request.getDeviceName());
        DeviceRegistrationResponse response = edgeAIService.registerDevice(merchantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取设备列表
     */
    @GetMapping("/devices")
    public ResponseEntity<List<DeviceInfoResponse>> getDevices(
            @RequestHeader("X-Merchant-Id") Long merchantId) {
        List<DeviceInfoResponse> devices = edgeAIService.getDevices(merchantId);
        return ResponseEntity.ok(devices);
    }
}
