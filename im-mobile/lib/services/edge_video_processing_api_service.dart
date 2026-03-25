/// 边缘视频处理 API 服务
/// 提供与后端边缘计算 API 的交互能力

import 'dart:async';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'edge_video_processing_model.dart';

/// API 配置
class ApiConfig {
  static const String baseUrl = '/api/v1/edge-video';
  static const int timeoutSeconds = 30;
  static const int retryAttempts = 3;
  static const int retryDelayMs = 1000;
  static const int pollingIntervalMs = 2000;
}

/// 边缘视频处理 API 服务类
class EdgeVideoProcessingApiService {
  final String? baseUrl;
  final String? authToken;
  Timer? _pollingTimer;

  EdgeVideoProcessingApiService({
    this.baseUrl,
    this.authToken,
  });

  String get _base => baseUrl ?? ApiConfig.baseUrl;

  /// 创建视频处理任务
  Future<Map<String, dynamic>> createTask({
    required String sessionId,
    required String userId,
    required MediaType mediaType,
    required String inputSource,
    ProcessingOptions? processingOptions,
  }) async {
    try {
      final response = await _post(
        '/tasks',
        {
          'sessionId': sessionId,
          'userId': userId,
          'mediaType': _mediaTypeToString(mediaType),
          'inputSource': inputSource,
          'processingOptions': processingOptions?.toJson() ??
              ProcessingOptions.defaultOptions.toJson(),
        },
      );

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to create task');
      }
    } catch (e) {
      throw EdgeApiException('Error creating task: $e');
    }
  }

  /// 获取任务状态
  Future<EdgeVideoProcessingTask> getTaskStatus(String taskId) async {
    try {
      final response = await _get('/tasks/$taskId/status');

      if (response['success'] == true) {
        return EdgeVideoProcessingTask.fromJson(response['data']);
      } else {
        throw EdgeApiException(response['error'] ?? 'Task not found');
      }
    } catch (e) {
      throw EdgeApiException('Error getting task status: $e');
    }
  }

  /// 获取用户的所有任务
  Future<List<EdgeVideoProcessingTask>> getUserTasks(String userId) async {
    try {
      final response = await _get('/users/$userId/tasks');

      if (response['success'] == true) {
        final List tasks = response['data']['tasks'] ?? [];
        return tasks
            .map((t) => EdgeVideoProcessingTask.fromJson(t))
            .toList();
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get tasks');
      }
    } catch (e) {
      throw EdgeApiException('Error getting user tasks: $e');
    }
  }

  /// 获取会话的所有任务
  Future<List<EdgeVideoProcessingTask>> getSessionTasks(String sessionId) async {
    try {
      final response = await _get('/sessions/$sessionId/tasks');

      if (response['success'] == true) {
        final List tasks = response['data']['tasks'] ?? [];
        return tasks
            .map((t) => EdgeVideoProcessingTask.fromJson(t))
            .toList();
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get tasks');
      }
    } catch (e) {
      throw EdgeApiException('Error getting session tasks: $e');
    }
  }

  /// 取消任务
  Future<bool> cancelTask(String taskId) async {
    try {
      final response = await _post('/tasks/$taskId/cancel', {});
      return response['success'] == true;
    } catch (e) {
      print('Error cancelling task: $e');
      return false;
    }
  }

  /// 暂停任务
  Future<bool> pauseTask(String taskId) async {
    try {
      final response = await _post('/tasks/$taskId/pause', {});
      return response['success'] == true;
    } catch (e) {
      print('Error pausing task: $e');
      return false;
    }
  }

  /// 恢复任务
  Future<bool> resumeTask(String taskId) async {
    try {
      final response = await _post('/tasks/$taskId/resume', {});
      return response['success'] == true;
    } catch (e) {
      print('Error resuming task: $e');
      return false;
    }
  }

  /// 轮询任务状态直到完成
  Stream<EdgeVideoProcessingTask> pollTaskStatus(String taskId) {
    final controller = StreamController<EdgeVideoProcessingTask>.broadcast();

    void poll() async {
      try {
        final task = await getTaskStatus(taskId);
        controller.add(task);

        if (_isTerminalStatus(task.processingStatus)) {
          controller.close();
        }
      } catch (e) {
        controller.addError(e);
        controller.close();
      }
    }

    // 立即执行一次
    poll();

    // 定时轮询
    _pollingTimer?.cancel();
    _pollingTimer = Timer.periodic(
      Duration(milliseconds: ApiConfig.pollingIntervalMs),
      (_) => poll(),
    );

    return controller.stream;
  }

  /// 获取系统统计信息
  Future<SystemStatistics> getSystemStatistics() async {
    try {
      final response = await _get('/statistics');

      if (response['success'] == true) {
        return SystemStatistics.fromJson(response['data']);
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get statistics');
      }
    } catch (e) {
      throw EdgeApiException('Error getting statistics: $e');
    }
  }

  /// 获取支持的编解码器
  Future<Map<String, dynamic>> getSupportedCodecs() async {
    try {
      final response = await _get('/supported-codecs');

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get codecs');
      }
    } catch (e) {
      throw EdgeApiException('Error getting codecs: $e');
    }
  }

  /// 获取 AI 增强选项
  Future<Map<String, dynamic>> getAiEnhancements() async {
    try {
      final response = await _get('/ai-enhancements');

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get enhancements');
      }
    } catch (e) {
      throw EdgeApiException('Error getting enhancements: $e');
    }
  }

  /// 获取带宽优化选项
  Future<Map<String, dynamic>> getBandwidthOptimizationOptions() async {
    try {
      final response = await _get('/bandwidth-optimization');

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get optimization options');
      }
    } catch (e) {
      throw EdgeApiException('Error getting optimization options: $e');
    }
  }

  /// 获取系统配置
  Future<Map<String, dynamic>> getConfiguration() async {
    try {
      final response = await _get('/configuration');

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Failed to get configuration');
      }
    } catch (e) {
      throw EdgeApiException('Error getting configuration: $e');
    }
  }

  /// 健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    try {
      final response = await _get('/health');

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Health check failed');
      }
    } catch (e) {
      throw EdgeApiException('Health check failed: $e');
    }
  }

  /// 清理过期任务
  Future<bool> cleanupExpiredTasks({int daysToKeep = 30}) async {
    try {
      final response = await _post('/cleanup', {}, queryParams: {'daysToKeep': daysToKeep.toString()});
      return response['success'] == true;
    } catch (e) {
      print('Error cleaning up tasks: $e');
      return false;
    }
  }

  /// 测试节点连接
  Future<Map<String, dynamic>> testNodeConnection(String nodeId) async {
    try {
      final response = await _post('/nodes/$nodeId/test-connection', {});

      if (response['success'] == true) {
        return response['data'];
      } else {
        throw EdgeApiException(response['error'] ?? 'Connection test failed');
      }
    } catch (e) {
      throw EdgeApiException('Error testing node connection: $e');
    }
  }

  /// 创建快速处理任务（使用默认选项）
  Future<Map<String, dynamic>> createQuickTask({
    required String sessionId,
    required String userId,
    required MediaType mediaType,
    required String inputSource,
  }) {
    return createTask(
      sessionId: sessionId,
      userId: userId,
      mediaType: mediaType,
      inputSource: inputSource,
      processingOptions: ProcessingOptions.defaultOptions,
    );
  }

  /// 创建高清处理任务
  Future<Map<String, dynamic>> createHDTtask({
    required String sessionId,
    required String userId,
    required String inputSource,
    bool enableAI = false,
  }) {
    return createTask(
      sessionId: sessionId,
      userId: userId,
      mediaType: MediaType.videoWithAudio,
      inputSource: inputSource,
      processingOptions: ProcessingOptions.hdOptions,
    );
  }

  /// 创建低带宽优化任务
  Future<Map<String, dynamic>> createLowBandwidthTask({
    required String sessionId,
    required String userId,
    required String inputSource,
  }) {
    return createTask(
      sessionId: sessionId,
      userId: userId,
      mediaType: MediaType.videoWithAudio,
      inputSource: inputSource,
      processingOptions: ProcessingOptions.lowBandwidthOptions,
    );
  }

  /// 停止轮询
  void stopPolling() {
    _pollingTimer?.cancel();
    _pollingTimer = null;
  }

  /// 释放资源
  void dispose() {
    stopPolling();
  }

  // ========== 内部方法 ==========

  bool _isTerminalStatus(ProcessingStatus status) {
    return status == ProcessingStatus.completed ||
        status == ProcessingStatus.failed ||
        status == ProcessingStatus.cancelled ||
        status == ProcessingStatus.timeout;
  }

  String _mediaTypeToString(MediaType mediaType) {
    return mediaType.toString().split('.').last;
  }

  Future<Map<String, dynamic>> _get(
    String path, {
    Map<String, String>? queryParams,
  }) async {
    return await _request('GET', path, null, queryParams: queryParams);
  }

  Future<Map<String, dynamic>> _post(
    String path,
    Map<String, dynamic> body, {
    Map<String, String>? queryParams,
  }) async {
    return await _request('POST', path, body, queryParams: queryParams);
  }

  Future<Map<String, dynamic>> _request(
    String method,
    String path,
    Map<String, dynamic>? body, {
    Map<String, String>? queryParams,
  }) async {
    final url = Uri.parse('$_base$path').replace(queryParameters: queryParams);

    final headers = {
      'Content-Type': 'application/json',
    };

    if (authToken != null) {
      headers['Authorization'] = 'Bearer $authToken';
    }

    http.Response response;

    if (method == 'GET') {
      response = await http.get(url, headers: headers).timeout(
        Duration(seconds: ApiConfig.timeoutSeconds),
      );
    } else if (method == 'POST') {
      response = await http.post(
        url,
        headers: headers,
        body: jsonEncode(body),
      ).timeout(
        Duration(seconds: ApiConfig.timeoutSeconds),
      );
    } else {
      throw EdgeApiException('Unsupported HTTP method: $method');
    }

    if (response.statusCode >= 200 && response.statusCode < 300) {
      return jsonDecode(response.body);
    } else {
      throw EdgeApiException(
        'HTTP ${response.statusCode}: ${response.reasonPhrase}',
      );
    }
  }
}

/// API 异常类
class EdgeApiException implements Exception {
  final String message;
  final int? statusCode;

  EdgeApiException(this.message, {this.statusCode});

  @override
  String toString() => 'EdgeApiException: $message';
}

/// 导出单例实例（可选）
EdgeVideoProcessingApiService? _instance;

EdgeVideoProcessingApiService get edgeVideoProcessingApiService {
  _instance ??= EdgeVideoProcessingApiService();
  return _instance!;
}
