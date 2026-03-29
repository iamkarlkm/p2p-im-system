/**
 * 多模态内容理解引擎 - Dart Flutter 服务
 */

import 'dart:convert';
import 'package:http/http.dart' as http;

// ==================== 数据模型 ====================

/// 多模态配置
class MultimodalConfig {
  final int? id;
  final String name;
  final String? description;
  final bool enabled;
  final int? version;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  
  // 文本理解配置
  final bool textEnabled;
  final String? textModel;
  final int? textMaxLength;
  final String? textLanguages;
  
  // 图像理解配置
  final bool imageEnabled;
  final String? imageModel;
  final int? imageMaxSize;
  final String? imageSupportedFormats;
  
  // 语音理解配置
  final bool audioEnabled;
  final String? audioModel;
  final int? audioMaxDuration;
  final String? audioSupportedFormats;
  
  // 视频理解配置
  final bool videoEnabled;
  final String? videoModel;
  final int? videoMaxDuration;
  final int? videoMaxSize;
  
  // 多模态融合配置
  final bool multimodalFusionEnabled;
  final String? fusionMethod;
  final String? crossModalWeighting;
  
  // 缓存配置
  final bool cacheEnabled;
  final int? cacheTtlHours;
  final int? cacheMaxSize;
  
  // 性能配置
  final int? concurrentWorkers;
  final int? timeoutMs;
  final int? batchSize;
  
  // 质量配置
  final double? confidenceThreshold;
  final bool? fallbackEnabled;
  final String? fallbackModel;
  
  // 监控配置
  final bool metricsEnabled;
  final int? metricsIntervalMinutes;
  final double? alertThresholdErrorRate;
  final int? alertThresholdLatencyMs;
  
  // 隐私配置
  final bool privacyEnabled;
  final bool? anonymizationEnabled;
  final int? dataRetentionDays;
  
  // 自定义配置
  final Map<String, dynamic>? customConfig;

  MultimodalConfig({
    this.id,
    required this.name,
    this.description,
    required this.enabled,
    this.version,
    this.createdAt,
    this.updatedAt,
    required this.textEnabled,
    this.textModel,
    this.textMaxLength,
    this.textLanguages,
    required this.imageEnabled,
    this.imageModel,
    this.imageMaxSize,
    this.imageSupportedFormats,
    required this.audioEnabled,
    this.audioModel,
    this.audioMaxDuration,
    this.audioSupportedFormats,
    required this.videoEnabled,
    this.videoModel,
    this.videoMaxDuration,
    this.videoMaxSize,
    required this.multimodalFusionEnabled,
    this.fusionMethod,
    this.crossModalWeighting,
    required this.cacheEnabled,
    this.cacheTtlHours,
    this.cacheMaxSize,
    this.concurrentWorkers,
    this.timeoutMs,
    this.batchSize,
    this.confidenceThreshold,
    this.fallbackEnabled,
    this.fallbackModel,
    required this.metricsEnabled,
    this.metricsIntervalMinutes,
    this.alertThresholdErrorRate,
    this.alertThresholdLatencyMs,
    required this.privacyEnabled,
    this.anonymizationEnabled,
    this.dataRetentionDays,
    this.customConfig,
  });

  factory MultimodalConfig.fromJson(Map<String, dynamic> json) {
    return MultimodalConfig(
      id: json['id'],
      name: json['name'] ?? '',
      description: json['description'],
      enabled: json['enabled'] ?? false,
      version: json['version'],
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : null,
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
      textEnabled: json['textEnabled'] ?? false,
      textModel: json['textModel'],
      textMaxLength: json['textMaxLength'],
      textLanguages: json['textLanguages'],
      imageEnabled: json['imageEnabled'] ?? false,
      imageModel: json['imageModel'],
      imageMaxSize: json['imageMaxSize'],
      imageSupportedFormats: json['imageSupportedFormats'],
      audioEnabled: json['audioEnabled'] ?? false,
      audioModel: json['audioModel'],
      audioMaxDuration: json['audioMaxDuration'],
      audioSupportedFormats: json['audioSupportedFormats'],
      videoEnabled: json['videoEnabled'] ?? false,
      videoModel: json['videoModel'],
      videoMaxDuration: json['videoMaxDuration'],
      videoMaxSize: json['videoMaxSize'],
      multimodalFusionEnabled: json['multimodalFusionEnabled'] ?? false,
      fusionMethod: json['fusionMethod'],
      crossModalWeighting: json['crossModalWeighting'],
      cacheEnabled: json['cacheEnabled'] ?? false,
      cacheTtlHours: json['cacheTtlHours'],
      cacheMaxSize: json['cacheMaxSize'],
      concurrentWorkers: json['concurrentWorkers'],
      timeoutMs: json['timeoutMs'],
      batchSize: json['batchSize'],
      confidenceThreshold: json['confidenceThreshold']?.toDouble(),
      fallbackEnabled: json['fallbackEnabled'],
      fallbackModel: json['fallbackModel'],
      metricsEnabled: json['metricsEnabled'] ?? false,
      metricsIntervalMinutes: json['metricsIntervalMinutes'],
      alertThresholdErrorRate: json['alertThresholdErrorRate']?.toDouble(),
      alertThresholdLatencyMs: json['alertThresholdLatencyMs'],
      privacyEnabled: json['privacyEnabled'] ?? false,
      anonymizationEnabled: json['anonymizationEnabled'],
      dataRetentionDays: json['dataRetentionDays'],
      customConfig: json['customConfig'] != null 
          ? Map<String, dynamic>.from(json['customConfig']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'enabled': enabled,
      'version': version,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'textEnabled': textEnabled,
      'textModel': textModel,
      'textMaxLength': textMaxLength,
      'textLanguages': textLanguages,
      'imageEnabled': imageEnabled,
      'imageModel': imageModel,
      'imageMaxSize': imageMaxSize,
      'imageSupportedFormats': imageSupportedFormats,
      'audioEnabled': audioEnabled,
      'audioModel': audioModel,
      'audioMaxDuration': audioMaxDuration,
      'audioSupportedFormats': audioSupportedFormats,
      'videoEnabled': videoEnabled,
      'videoModel': videoModel,
      'videoMaxDuration': videoMaxDuration,
      'videoMaxSize': videoMaxSize,
      'multimodalFusionEnabled': multimodalFusionEnabled,
      'fusionMethod': fusionMethod,
      'crossModalWeighting': crossModalWeighting,
      'cacheEnabled': cacheEnabled,
      'cacheTtlHours': cacheTtlHours,
      'cacheMaxSize': cacheMaxSize,
      'concurrentWorkers': concurrentWorkers,
      'timeoutMs': timeoutMs,
      'batchSize': batchSize,
      'confidenceThreshold': confidenceThreshold,
      'fallbackEnabled': fallbackEnabled,
      'fallbackModel': fallbackModel,
      'metricsEnabled': metricsEnabled,
      'metricsIntervalMinutes': metricsIntervalMinutes,
      'alertThresholdErrorRate': alertThresholdErrorRate,
      'alertThresholdLatencyMs': alertThresholdLatencyMs,
      'privacyEnabled': privacyEnabled,
      'anonymizationEnabled': anonymizationEnabled,
      'dataRetentionDays': dataRetentionDays,
      'customConfig': customConfig,
    };
  }
}

/// 分析请求
class AnalysisRequest {
  final String? sessionId;
  final int? userId;
  final int? messageId;
  final String contentType;
  final String? businessContext;
  final int? priority;
  final String? textContent;
  final String? imageUrl;
  final String? audioUrl;
  final String? videoUrl;

  AnalysisRequest({
    this.sessionId,
    this.userId,
    this.messageId,
    required this.contentType,
    this.businessContext,
    this.priority,
    this.textContent,
    this.imageUrl,
    this.audioUrl,
    this.videoUrl,
  });

  Map<String, dynamic> toJson() {
    return {
      'sessionId': sessionId,
      'userId': userId,
      'messageId': messageId,
      'contentType': contentType,
      'businessContext': businessContext,
      'priority': priority,
      'textContent': textContent,
      'imageUrl': imageUrl,
      'audioUrl': audioUrl,
      'videoUrl': videoUrl,
    };
  }
}

/// 分析结果
class AnalysisResult {
  final String requestId;
  final String? sessionId;
  final int? userId;
  final int? messageId;
  final String contentType;
  final String? contentHash;
  final String analysisStatus;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? completedAt;
  final Map<String, dynamic>? textAnalysis;
  final Map<String, dynamic>? imageAnalysis;
  final Map<String, dynamic>? audioAnalysis;
  final Map<String, dynamic>? videoAnalysis;
  final Map<String, dynamic>? multimodalFusion;
  final double? confidenceScore;
  final String? qualityRating;
  final int? processingTimeMs;
  final String? modelUsed;
  final double? costUnits;
  final String? errorMessage;
  final String? errorCode;
  final int? retryCount;

  AnalysisResult({
    required this.requestId,
    this.sessionId,
    this.userId,
    this.messageId,
    required this.contentType,
    this.contentHash,
    required this.analysisStatus,
    required this.createdAt,
    required this.updatedAt,
    this.completedAt,
    this.textAnalysis,
    this.imageAnalysis,
    this.audioAnalysis,
    this.videoAnalysis,
    this.multimodalFusion,
    this.confidenceScore,
    this.qualityRating,
    this.processingTimeMs,
    this.modelUsed,
    this.costUnits,
    this.errorMessage,
    this.errorCode,
    this.retryCount,
  });

  factory AnalysisResult.fromJson(Map<String, dynamic> json) {
    return AnalysisResult(
      requestId: json['requestId'] ?? '',
      sessionId: json['sessionId'],
      userId: json['userId'],
      messageId: json['messageId'],
      contentType: json['contentType'] ?? '',
      contentHash: json['contentHash'],
      analysisStatus: json['analysisStatus'] ?? '',
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt']) 
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt']) 
          : DateTime.now(),
      completedAt: json['completedAt'] != null 
          ? DateTime.parse(json['completedAt']) 
          : null,
      textAnalysis: json['textAnalysis'] != null 
          ? Map<String, dynamic>.from(json['textAnalysis']) 
          : null,
      imageAnalysis: json['imageAnalysis'] != null 
          ? Map<String, dynamic>.from(json['imageAnalysis']) 
          : null,
      audioAnalysis: json['audioAnalysis'] != null 
          ? Map<String, dynamic>.from(json['audioAnalysis']) 
          : null,
      videoAnalysis: json['videoAnalysis'] != null 
          ? Map<String, dynamic>.from(json['videoAnalysis']) 
          : null,
      multimodalFusion: json['multimodalFusion'] != null 
          ? Map<String, dynamic>.from(json['multimodalFusion']) 
          : null,
      confidenceScore: json['confidenceScore']?.toDouble(),
      qualityRating: json['qualityRating'],
      processingTimeMs: json['processingTimeMs'],
      modelUsed: json['modelUsed'],
      costUnits: json['costUnits']?.toDouble(),
      errorMessage: json['errorMessage'],
      errorCode: json['errorCode'],
      retryCount: json['retryCount'],
    );
  }

  bool get isCompleted => analysisStatus == 'completed';
  bool get isFailed => analysisStatus == 'failed';
  bool get isProcessing => analysisStatus == 'processing';
  bool get isPending => analysisStatus == 'pending';
}

/// 配置统计
class ConfigStats {
  final int totalConfigs;
  final int enabledConfigs;
  final int textAnalysisConfigs;
  final int imageAnalysisConfigs;
  final int audioAnalysisConfigs;
  final int videoAnalysisConfigs;
  final int fallbackEnabledConfigs;

  ConfigStats({
    required this.totalConfigs,
    required this.enabledConfigs,
    required this.textAnalysisConfigs,
    required this.imageAnalysisConfigs,
    required this.audioAnalysisConfigs,
    required this.videoAnalysisConfigs,
    required this.fallbackEnabledConfigs,
  });

  factory ConfigStats.fromJson(Map<String, dynamic> json) {
    return ConfigStats(
      totalConfigs: json['totalConfigs'] ?? 0,
      enabledConfigs: json['enabledConfigs'] ?? 0,
      textAnalysisConfigs: json['textAnalysisConfigs'] ?? 0,
      imageAnalysisConfigs: json['imageAnalysisConfigs'] ?? 0,
      audioAnalysisConfigs: json['audioAnalysisConfigs'] ?? 0,
      videoAnalysisConfigs: json['videoAnalysisConfigs'] ?? 0,
      fallbackEnabledConfigs: json['fallbackEnabledConfigs'] ?? 0,
    );
  }
}

/// 结果统计
class ResultStats {
  final int totalResults;
  final int completedResults;
  final int failedResults;
  final int processingResults;
  final int pendingResults;
  final double? averageProcessingTime;
  final double? averageConfidenceScore;
  final double? averageSentimentScore;
  final double? totalCost;

  ResultStats({
    required this.totalResults,
    required this.completedResults,
    required this.failedResults,
    required this.processingResults,
    required this.pendingResults,
    this.averageProcessingTime,
    this.averageConfidenceScore,
    this.averageSentimentScore,
    this.totalCost,
  });

  factory ResultStats.fromJson(Map<String, dynamic> json) {
    return ResultStats(
      totalResults: json['totalResults'] ?? 0,
      completedResults: json['completedResults'] ?? 0,
      failedResults: json['failedResults'] ?? 0,
      processingResults: json['processingResults'] ?? 0,
      pendingResults: json['pendingResults'] ?? 0,
      averageProcessingTime: json['averageProcessingTime']?.toDouble(),
      averageConfidenceScore: json['averageConfidenceScore']?.toDouble(),
      averageSentimentScore: json['averageSentimentScore']?.toDouble(),
      totalCost: json['totalCost']?.toDouble(),
    );
  }
}

// ==================== API 服务 ====================

class MultimodalApiService {
  final String baseUrl;
  final http.Client _client;

  MultimodalApiService({
    this.baseUrl = '/api/multimodal',
    http.Client? client,
  }) : _client = client ?? http.Client();

  Future<Map<String, dynamic>> _request(
    String endpoint, {
    String method = 'GET',
    Map<String, dynamic>? body,
  }) async {
    final url = Uri.parse('$baseUrl$endpoint');
    
    final response = await _client.request(
      method,
      url,
      headers: {'Content-Type': 'application/json'},
      body: body != null ? jsonEncode(body) : null,
    );

    if (response.statusCode != 200) {
      final errorData = jsonDecode(response.body);
      throw Exception(errorData['message'] ?? '请求失败');
    }

    final responseData = jsonDecode(response.body);
    if (!responseData['success']) {
      throw Exception(responseData['message'] ?? '请求失败');
    }

    return responseData;
  }

  // ==================== 配置管理 ====================

  Future<MultimodalConfig> createConfig(MultimodalConfig config) async {
    final response = await _request('/configs', method: 'POST', body: config.toJson());
    return MultimodalConfig.fromJson(response['data']);
  }

  Future<MultimodalConfig> updateConfig(int id, Map<String, dynamic> updates) async {
    final response = await _request('/configs/$id', method: 'PUT', body: updates);
    return MultimodalConfig.fromJson(response['data']);
  }

  Future<MultimodalConfig> getConfigById(int id) async {
    final response = await _request('/configs/$id');
    return MultimodalConfig.fromJson(response['data']);
  }

  Future<MultimodalConfig> getConfigByName(String name) async {
    final response = await _request('/configs/name/$name');
    return MultimodalConfig.fromJson(response['data']);
  }

  Future<List<MultimodalConfig>> getAllEnabledConfigs() async {
    final response = await _request('/configs/enabled');
    final data = response['data'] as List;
    return data.map((item) => MultimodalConfig.fromJson(item)).toList();
  }

  Future<MultimodalConfig> getDefaultConfig() async {
    final response = await _request('/configs/default');
    return MultimodalConfig.fromJson(response['data']);
  }

  Future<void> disableConfig(int id) async {
    await _request('/configs/$id/disable', method: 'POST');
  }

  Future<void> enableConfig(int id) async {
    await _request('/configs/$id/enable', method: 'POST');
  }

  Future<void> deleteConfig(int id) async {
    await _request('/configs/$id', method: 'DELETE');
  }

  Future<ConfigStats> getConfigStats() async {
    final response = await _request('/configs/stats');
    return ConfigStats.fromJson(response['data']);
  }

  // ==================== 分析结果 ====================

  Future<Map<String, dynamic>> analyze(AnalysisRequest request) async {
    final response = await _request('/analyze', method: 'POST', body: request.toJson());
    return response['data'];
  }

  Future<Map<String, dynamic>> getAnalysisStatus(String requestId) async {
    final response = await _request('/results/$requestId/status');
    return response['data'];
  }

  Future<AnalysisResult> getAnalysisResult(String requestId) async {
    final response = await _request('/results/$requestId');
    return AnalysisResult.fromJson(response['data']);
  }

  Future<AnalysisResult?> getAnalysisResultByMessageId(int messageId) async {
    final response = await _request('/results/by-message/$messageId');
    final data = response['data'];
    if (data == null) return null;
    return AnalysisResult.fromJson(data);
  }

  Future<List<Map<String, dynamic>>> getUserAnalysisHistory(
    int userId, {
    int limit = 20,
  }) async {
    final response = await _request('/users/$userId/history?limit=$limit');
    final data = response['data'] as List;
    return data.cast<Map<String, dynamic>>();
  }

  Future<List<Map<String, dynamic>>> getSessionAnalysisHistory(
    String sessionId, {
    int limit = 20,
  }) async {
    final response = await _request('/sessions/$sessionId/history?limit=$limit');
    final data = response['data'] as List;
    return data.cast<Map<String, dynamic>>();
  }

  Future<ResultStats> getResultStats() async {
    final response = await _request('/results/stats');
    return ResultStats.fromJson(response['data']);
  }

  Future<Map<String, dynamic>> cleanupExpiredCache() async {
    final response = await _request('/admin/cleanup-cache', method: 'POST');
    return response['data'];
  }

  Future<Map<String, dynamic>> retryFailedRequests() async {
    final response = await _request('/admin/retry-failed', method: 'POST');
    return response['data'];
  }

  // ==================== 便捷方法 ====================

  Future<String> analyzeText(
    String text, {
    String? sessionId,
    int? userId,
    int? messageId,
    int? priority,
  }) async {
    final response = await analyze(AnalysisRequest(
      contentType: 'text',
      textContent: text,
      sessionId: sessionId,
      userId: userId,
      messageId: messageId,
      priority: priority,
    ));
    return response['requestId'];
  }

  Future<String> analyzeImage(
    String imageUrl, {
    String? sessionId,
    int? userId,
    int? messageId,
    int? priority,
  }) async {
    final response = await analyze(AnalysisRequest(
      contentType: 'image',
      imageUrl: imageUrl,
      sessionId: sessionId,
      userId: userId,
      messageId: messageId,
      priority: priority,
    ));
    return response['requestId'];
  }

  Future<String> analyzeAudio(
    String audioUrl, {
    String? sessionId,
    int? userId,
    int? messageId,
    int? priority,
  }) async {
    final response = await analyze(AnalysisRequest(
      contentType: 'audio',
      audioUrl: audioUrl,
      sessionId: sessionId,
      userId: userId,
      messageId: messageId,
      priority: priority,
    ));
    return response['requestId'];
  }

  Future<String> analyzeVideo(
    String videoUrl, {
    String? sessionId,
    int? userId,
    int? messageId,
    int? priority,
  }) async {
    final response = await analyze(AnalysisRequest(
      contentType: 'video',
      videoUrl: videoUrl,
      sessionId: sessionId,
      userId: userId,
      messageId: messageId,
      priority: priority,
    ));
    return response['requestId'];
  }

  void dispose() {
    _client.close();
  }
}