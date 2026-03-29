/// 联邦学习 AI API 服务
/// 提供与联邦学习后端 API 的完整交互功能
/// 
/// @version 1.0
/// @created 2026-03-23

import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'federated_learning_ai_model.dart';

// ==================== 服务配置 ====================

const String _defaultBaseUrl = '/api/fl';
const Duration _defaultTimeout = Duration(seconds: 30);
const int _defaultRetryCount = 3;
const Duration _defaultRetryDelay = Duration(seconds: 1);

/// HTTP 客户端接口
abstract class HttpClient {
  Future<http.Response> get(Uri url, {Map<String, String>? headers});
  Future<http.Response> post(Uri url, {Map<String, String>? headers, dynamic body});
  Future<http.Response> put(Uri url, {Map<String, String>? headers, dynamic body});
  Future<http.Response> delete(Uri url, {Map<String, String>? headers});
}

/// 默认 HTTP 客户端实现
class DefaultHttpClient implements HttpClient {
  final Duration timeout;

  DefaultHttpClient({this.timeout = _defaultTimeout});

  @override
  Future<http.Response> get(Uri url, {Map<String, String>? headers}) async {
    return await http.get(url, headers: headers).timeout(timeout);
  }

  @override
  Future<http.Response> post(Uri url, {Map<String, String>? headers, dynamic body}) async {
    return await http.post(url, headers: headers, body: body).timeout(timeout);
  }

  @override
  Future<http.Response> put(Uri url, {Map<String, String>? headers, dynamic body}) async {
    return await http.put(url, headers: headers, body: body).timeout(timeout);
  }

  @override
  Future<http.Response> delete(Uri url, {Map<String, String>? headers}) async {
    return await http.delete(url, headers: headers).timeout(timeout);
  }
}

// ==================== API 服务类 ====================

class FederatedLearningAIService {
  final String baseUrl;
  final HttpClient _httpClient;
  final Duration timeout;
  final int retryCount;
  final Duration retryDelay;
  final Map<String, String> _defaultHeaders;

  FederatedLearningAIService({
    this.baseUrl = _defaultBaseUrl,
    HttpClient? httpClient,
    this.timeout = _defaultTimeout,
    this.retryCount = _defaultRetryCount,
    this.retryDelay = _defaultRetryDelay,
    Map<String, String>? customHeaders,
  })  : _httpClient = httpClient ?? DefaultHttpClient(timeout: timeout),
        _defaultHeaders = {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          ...?customHeaders,
        };

  // ==================== 通用请求方法 ====================

  /// 执行 GET 请求
  Future<Map<String, dynamic>> _get(String endpoint) async {
    final uri = Uri.parse('$baseUrl$endpoint');
    
    for (int attempt = 0; attempt < retryCount; attempt++) {
      try {
        final response = await _httpClient.get(uri, headers: _defaultHeaders);
        return _handleResponse(response);
      } catch (e) {
        if (attempt < retryCount - 1) {
          await Future.delayed(retryDelay * (attempt + 1));
        } else {
          rethrow;
        }
      }
    }
    
    throw Exception('Request failed after $retryCount attempts');
  }

  /// 执行 POST 请求
  Future<Map<String, dynamic>> _post(String endpoint, Map<String, dynamic> body) async {
    final uri = Uri.parse('$baseUrl$endpoint');
    
    for (int attempt = 0; attempt < retryCount; attempt++) {
      try {
        final response = await _httpClient.post(
          uri,
          headers: _defaultHeaders,
          body: jsonEncode(body),
        );
        return _handleResponse(response);
      } catch (e) {
        if (attempt < retryCount - 1) {
          await Future.delayed(retryDelay * (attempt + 1));
        } else {
          rethrow;
        }
      }
    }
    
    throw Exception('Request failed after $retryCount attempts');
  }

  /// 执行 PUT 请求
  Future<Map<String, dynamic>> _put(String endpoint, Map<String, dynamic> body) async {
    final uri = Uri.parse('$baseUrl$endpoint');
    
    for (int attempt = 0; attempt < retryCount; attempt++) {
      try {
        final response = await _httpClient.put(
          uri,
          headers: _defaultHeaders,
          body: jsonEncode(body),
        );
        return _handleResponse(response);
      } catch (e) {
        if (attempt < retryCount - 1) {
          await Future.delayed(retryDelay * (attempt + 1));
        } else {
          rethrow;
        }
      }
    }
    
    throw Exception('Request failed after $retryCount attempts');
  }

  /// 执行 DELETE 请求
  Future<Map<String, dynamic>> _delete(String endpoint) async {
    final uri = Uri.parse('$baseUrl$endpoint');
    
    for (int attempt = 0; attempt < retryCount; attempt++) {
      try {
        final response = await _httpClient.delete(uri, headers: _defaultHeaders);
        return _handleResponse(response);
      } catch (e) {
        if (attempt < retryCount - 1) {
          await Future.delayed(retryDelay * (attempt + 1));
        } else {
          rethrow;
        }
      }
    }
    
    throw Exception('Request failed after $retryCount attempts');
  }

  /// 处理 HTTP 响应
  Map<String, dynamic> _handleResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      throw HttpException(
        'HTTP ${response.statusCode}: ${response.reasonPhrase}',
        uri: response.request?.url,
      );
    }
  }

  // ==================== 服务器管理 API ====================

  /// 注册联邦学习服务器
  Future<Map<String, dynamic>> registerServer({
    required String serverName,
    required String serverUrl,
    required String region,
    String? serverType,
  }) async {
    final response = await _post('/servers/register', {
      'serverName': serverName,
      'serverUrl': serverUrl,
      'region': region,
      if (serverType != null) 'serverType': serverType,
    });
    return response;
  }

  /// 获取服务器状态
  Future<Map<String, dynamic>> getServerStatus(String serverId) async {
    return await _get('/servers/$serverId/status');
  }

  /// 更新服务器配置
  Future<Map<String, dynamic>> updateServerConfig(
    String serverId,
    Map<String, dynamic> config,
  ) async {
    return await _put('/servers/$serverId/config', config);
  }

  /// 注销服务器
  Future<Map<String, dynamic>> unregisterServer(String serverId) async {
    return await _delete('/servers/$serverId');
  }

  // ==================== 模型管理 API ====================

  /// 初始化全局模型
  Future<Map<String, dynamic>> initializeModel({
    required String serverId,
    required String modelType,
    String? language,
    required String modelName,
  }) async {
    return await _post('/models/init', {
      'serverId': serverId,
      'modelType': modelType,
      if (language != null) 'language': language,
      'modelName': modelName,
    });
  }

  /// 开始训练轮次
  Future<Map<String, dynamic>> startTrainingRound(
    String modelId, {
    int? targetClientCount,
  }) async {
    return await _post('/models/$modelId/round/start', {
      if (targetClientCount != null) 'targetClientCount': targetClientCount,
    });
  }

  /// 获取训练轮次状态
  Future<Map<String, dynamic>> getTrainingRoundStatus(
    String modelId,
    String roundId,
  ) async {
    return await _get('/models/$modelId/round/$roundId');
  }

  /// 执行模型聚合
  Future<Map<String, dynamic>> aggregateModelUpdates(
    String modelId,
    String roundId,
  ) async {
    return await _post('/models/$modelId/round/$roundId/aggregate', {});
  }

  /// 获取模型版本
  Future<Map<String, dynamic>> getModelVersion(
    String modelId,
    String versionId,
  ) async {
    return await _get('/models/$modelId/version/$versionId');
  }

  /// 分发模型
  Future<Map<String, dynamic>> distributeModel(
    String modelId,
    String versionId,
    List<String> clientIds,
  ) async {
    return await _post('/models/$modelId/version/$versionId/distribute', {
      'clientIds': clientIds,
    });
  }

  // ==================== 客户端管理 API ====================

  /// 获取在线客户端列表
  Future<Map<String, dynamic>> getOnlineClients({String? serverId}) async {
    final endpoint = serverId != null 
        ? '/clients/online?serverId=$serverId'
        : '/clients/online';
    return await _get(endpoint);
  }

  /// 选择客户端参与训练
  Future<Map<String, dynamic>> selectClient(
    String clientId, {
    required String modelId,
    required String roundId,
  }) async {
    return await _post('/clients/$clientId/select', {
      'modelId': modelId,
      'roundId': roundId,
    });
  }

  /// 获取客户端更新
  Future<Map<String, dynamic>> getClientUpdates(
    String clientId, {
    required String modelId,
    required int round,
  }) async {
    return await _get('/clients/$clientId/updates?modelId=$modelId&round=$round');
  }

  /// 验证客户端更新
  Future<Map<String, dynamic>> verifyClientUpdate(
    String clientId, {
    required String updateId,
    required String signature,
  }) async {
    return await _post('/clients/$clientId/updates/verify', {
      'updateId': updateId,
      'signature': signature,
    });
  }

  // ==================== 隐私保护 API ====================

  /// 获取隐私预算状态
  Future<Map<String, dynamic>> getPrivacyBudget(String serverId) async {
    return await _get('/privacy/$serverId/budget');
  }

  /// 分配隐私预算
  Future<Map<String, dynamic>> allocatePrivacyBudget(
    String serverId, {
    required double epsilonPerRound,
  }) async {
    return await _post('/privacy/$serverId/budget/allocate', {
      'epsilonPerRound': epsilonPerRound,
    });
  }

  // ==================== 性能监控 API ====================

  /// 获取模型性能指标
  Future<Map<String, dynamic>> getModelPerformanceMetrics(String modelId) async {
    return await _get('/performance/$modelId/metrics');
  }

  /// 优化模型性能
  Future<Map<String, dynamic>> optimizeModelPerformance(
    String modelId, {
    String? optimizationType,
  }) async {
    return await _post('/performance/$modelId/optimize', {
      if (optimizationType != null) 'optimizationType': optimizationType,
    });
  }

  // ==================== AI 功能 API ====================

  /// 获取智能回复建议
  Future<SmartReplyResponse> getSmartReplySuggestions({
    required String userId,
    required String message,
    String? context,
    String? language,
    int? maxSuggestions,
  }) async {
    final response = await _post('/ai/smart-reply', {
      'userId': userId,
      'message': message,
      if (context != null) 'context': context,
      if (language != null) 'language': language,
      if (maxSuggestions != null) 'maxSuggestions': maxSuggestions,
    });
    
    final data = response['data'] as Map<String, dynamic>;
    return SmartReplyResponse.fromJson(data);
  }

  /// 检测垃圾消息
  Future<SpamDetectionResponse> detectSpam({
    required String userId,
    required String message,
    required String sender,
    Map<String, dynamic>? metadata,
  }) async {
    final response = await _post('/ai/spam-detection', {
      'userId': userId,
      'message': message,
      'sender': sender,
      if (metadata != null) 'metadata': metadata,
    });
    
    final data = response['data'] as Map<String, dynamic>;
    return SpamDetectionResponse.fromJson(data);
  }

  /// 分析消息情感
  Future<SentimentAnalysisResponse> analyzeSentiment({
    required String userId,
    required String message,
    String? language,
  }) async {
    final response = await _post('/ai/sentiment-analysis', {
      'userId': userId,
      'message': message,
      if (language != null) 'language': language,
    });
    
    final data = response['data'] as Map<String, dynamic>;
    return SentimentAnalysisResponse.fromJson(data);
  }

  /// 分类消息
  Future<Map<String, dynamic>> categorizeMessage({
    required String userId,
    required String message,
  }) async {
    return await _post('/ai/message-categorization', {
      'userId': userId,
      'message': message,
    });
  }

  // ==================== 工具方法 ====================

  /// 健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    return await _get('/health');
  }

  /// 上传客户端模型更新
  Future<Map<String, dynamic>> uploadClientUpdate(
    String modelId,
    String clientId,
    FLModelUpdate update,
  ) async {
    return await _post('/models/$modelId/clients/$clientId/updates', update.toJson());
  }

  /// 下载全局模型
  Future<Uint8List> downloadModel(String modelId, {String? version}) async {
    final uri = Uri.parse(
      '$baseUrl/models/$modelId/download${version != null ? '?version=$version' : ''}',
    );
    
    final response = await _httpClient.get(uri, headers: _defaultHeaders);
    
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return response.bodyBytes;
    } else {
      throw HttpException(
        'HTTP ${response.statusCode}: ${response.reasonPhrase}',
        uri: uri,
      );
    }
  }

  /// 保存模型到文件
  Future<File> downloadModelToFile(
    String modelId,
    String filePath, {
    String? version,
  }) async {
    final bytes = await downloadModel(modelId, version: version);
    final file = File(filePath);
    await file.writeAsBytes(bytes);
    return file;
  }
}

// ==================== 工具扩展类 ====================

/// 联邦学习客户端状态管理器
class FLClientStateManager {
  final FederatedLearningAIService service;
  
  String? _currentServerId;
  String? _currentModelId;
  int _currentRound = 0;
  bool _isTraining = false;
  DateTime? _lastSyncTime;

  FLClientStateManager(this.service);

  String? get currentServerId => _currentServerId;
  String? get currentModelId => _currentModelId;
  int get currentRound => _currentRound;
  bool get isTraining => _isTraining;
  DateTime? get lastSyncTime => _lastSyncTime;

  /// 初始化客户端
  Future<void> initialize({
    required String serverId,
    required String modelId,
  }) async {
    _currentServerId = serverId;
    _currentModelId = modelId;
    _currentRound = 0;
    _isTraining = false;
    
    final status = await service.getServerStatus(serverId);
    print('Connected to server: ${status['serverName']}');
    
    _lastSyncTime = DateTime.now();
  }

  /// 加入训练轮次
  Future<void> joinTrainingRound() async {
    if (_currentModelId == null) {
      throw StateError('Model not initialized. Call initialize() first.');
    }
    
    final response = await service.selectClient(
      'client_current',
      modelId: _currentModelId!,
      roundId: 'round_${_currentRound + 1}',
    );
    
    if (response['selected'] == true) {
      _isTraining = true;
      _currentRound++;
      print('Joined training round ${_currentRound}');
    }
  }

  /// 上传模型更新
  Future<void> uploadUpdate(FLModelUpdate update) async {
    if (_currentModelId == null) {
      throw StateError('Model not initialized');
    }
    
    await service.uploadClientUpdate(_currentModelId!, 'client_current', update);
    _isTraining = false;
    _lastSyncTime = DateTime.now();
    print('Model update uploaded successfully');
  }

  /// 获取智能回复
  Future<SmartReplyResponse> getSmartReply({
    required String message,
    String? context,
    String? language,
  }) async {
    return await service.getSmartReplySuggestions(
      userId: 'user_current',
      message: message,
      context: context,
      language: language,
      maxSuggestions: 5,
    );
  }

  /// 检测垃圾消息
  Future<SpamDetectionResponse> checkSpam({
    required String message,
    required String sender,
  }) async {
    return await service.detectSpam(
      userId: 'user_current',
      message: message,
      sender: sender,
    );
  }

  /// 分析情感
  Future<SentimentAnalysisResponse> analyzeEmotion({
    required String message,
    String? language,
  }) async {
    return await service.analyzeSentiment(
      userId: 'user_current',
      message: message,
      language: language,
    );
  }

  /// 重置状态
  void reset() {
    _currentServerId = null;
    _currentModelId = null;
    _currentRound = 0;
    _isTraining = false;
    _lastSyncTime = null;
  }
}

// ==================== 导出单例实例 ====================

/// 默认 API 服务实例
final flAIService = FederatedLearningAIService();

/// 默认客户端状态管理器
final flClientStateManager = FLClientStateManager(flAIService);
