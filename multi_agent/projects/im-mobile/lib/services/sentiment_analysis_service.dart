import 'dart:convert';
import 'package:http/http.dart' as http;

/// 情感分析结果模型
class SentimentAnalysisResult {
  final int id;
  final int messageId;
  final int conversationId;
  final int senderId;
  final DateTime analysisTime;
  final String primaryEmotion;
  final String secondaryEmotion;
  final double sentimentIntensity;
  final bool emergencyFlag;
  final String? emergencyReason;
  final double confidenceScore;
  final double? multimodalFusionScore;
  final String textEmotion;
  final String? audioEmotion;
  final String? visualEmotion;
  final double? baselineDeviation;
  final String modelVersion;
  final int processingLatencyMs;
  final DateTime createdAt;
  final DateTime updatedAt;

  SentimentAnalysisResult({
    required this.id,
    required this.messageId,
    required this.conversationId,
    required this.senderId,
    required this.analysisTime,
    required this.primaryEmotion,
    required this.secondaryEmotion,
    required this.sentimentIntensity,
    required this.emergencyFlag,
    this.emergencyReason,
    required this.confidenceScore,
    this.multimodalFusionScore,
    required this.textEmotion,
    this.audioEmotion,
    this.visualEmotion,
    this.baselineDeviation,
    required this.modelVersion,
    required this.processingLatencyMs,
    required this.createdAt,
    required this.updatedAt,
  });

  factory SentimentAnalysisResult.fromJson(Map<String, dynamic> json) {
    return SentimentAnalysisResult(
      id: json['id'] ?? 0,
      messageId: json['messageId'] ?? 0,
      conversationId: json['conversationId'] ?? 0,
      senderId: json['senderId'] ?? 0,
      analysisTime: DateTime.parse(json['analysisTime']),
      primaryEmotion: json['primaryEmotion'] ?? 'neutral',
      secondaryEmotion: json['secondaryEmotion'] ?? 'neutral',
      sentimentIntensity: (json['sentimentIntensity'] ?? 0.0).toDouble(),
      emergencyFlag: json['emergencyFlag'] ?? false,
      emergencyReason: json['emergencyReason'],
      confidenceScore: (json['confidenceScore'] ?? 0.0).toDouble(),
      multimodalFusionScore: json['multimodalFusionScore']?.toDouble(),
      textEmotion: json['textEmotion'] ?? 'neutral',
      audioEmotion: json['audioEmotion'],
      visualEmotion: json['visualEmotion'],
      baselineDeviation: json['baselineDeviation']?.toDouble(),
      modelVersion: json['modelVersion'] ?? 'unknown',
      processingLatencyMs: json['processingLatencyMs'] ?? 0,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'conversationId': conversationId,
      'senderId': senderId,
      'analysisTime': analysisTime.toIso8601String(),
      'primaryEmotion': primaryEmotion,
      'secondaryEmotion': secondaryEmotion,
      'sentimentIntensity': sentimentIntensity,
      'emergencyFlag': emergencyFlag,
      'emergencyReason': emergencyReason,
      'confidenceScore': confidenceScore,
      'multimodalFusionScore': multimodalFusionScore,
      'textEmotion': textEmotion,
      'audioEmotion': audioEmotion,
      'visualEmotion': visualEmotion,
      'baselineDeviation': baselineDeviation,
      'modelVersion': modelVersion,
      'processingLatencyMs': processingLatencyMs,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  @override
  String toString() {
    return 'SentimentAnalysisResult(id: $id, primaryEmotion: $primaryEmotion, intensity: $sentimentIntensity)';
  }
}

/// 情感分析请求模型
class SentimentAnalysisRequest {
  final int messageId;
  final int conversationId;
  final int senderId;
  final String messageText;
  final Map<String, dynamic>? context;

  SentimentAnalysisRequest({
    required this.messageId,
    required this.conversationId,
    required this.senderId,
    required this.messageText,
    this.context,
  });

  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'conversationId': conversationId,
      'senderId': senderId,
      'messageText': messageText,
      if (context != null) 'context': context,
    };
  }
}

/// API 响应模型
class ApiResponse<T> {
  final bool success;
  final T? data;
  final String? error;
  final String? message;

  ApiResponse({
    required this.success,
    this.data,
    this.error,
    this.message,
  });

  factory ApiResponse.fromJson(Map<String, dynamic> json, T? Function(Map<String, dynamic>)? fromJson) {
    return ApiResponse(
      success: json['success'] ?? false,
      data: json['data'] != null && fromJson != null ? fromJson(json['data']) : null,
      error: json['error'],
      message: json['message'],
    );
  }
}

/// 情感分析服务 - 基于深度学习的情感分析系统
class SentimentAnalysisApiService {
  final String baseURL;
  final http.Client _client;

  SentimentAnalysisApiService({this.baseURL = 'http://localhost:8080/api/v1/sentiment', http.Client? client})
      : _client = client ?? http.Client();

  /// 分析单条消息的情感
  Future<ApiResponse<SentimentAnalysisResult>> analyzeMessage({
    required int messageId,
    required int conversationId,
    required int senderId,
    required String messageText,
    Map<String, dynamic>? context,
  }) async {
    try {
      final response = await _client.post(
        Uri.parse('$baseURL/analyze'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'messageId': messageId,
          'conversationId': conversationId,
          'senderId': senderId,
          'messageText': messageText,
          if (context != null) 'context': context,
        }),
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true) {
        return ApiResponse(
          success: true,
          message: '情感分析成功',
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '情感分析失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取消息情感分析结果
  Future<ApiResponse<SentimentAnalysisResult>> getAnalysisByMessageId(int messageId) async {
    try {
      final response = await _client.get(
        Uri.parse('$baseURL/message/$messageId'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true && data['analysis'] != null) {
        return ApiResponse(
          success: true,
          data: SentimentAnalysisResult.fromJson(data['analysis']),
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '未找到分析结果',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取会话情感分析历史
  Future<ApiResponse<List<SentimentAnalysisResult>>> getConversationAnalysis({
    required int conversationId,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _client.get(
        Uri.parse('$baseURL/conversation/$conversationId?page=$page&size=$size'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true && data['analyses'] != null) {
        final analyses = (data['analyses'] as List)
            .map((item) => SentimentAnalysisResult.fromJson(item))
            .toList();
        return ApiResponse(
          success: true,
          data: analyses,
          message: '找到 ${analyses.length} 条分析记录',
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '获取会话分析历史失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取用户情感分析历史
  Future<ApiResponse<List<SentimentAnalysisResult>>> getUserAnalysis(int userId) async {
    try {
      final response = await _client.get(
        Uri.parse('$baseURL/user/$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true && data['analyses'] != null) {
        final analyses = (data['analyses'] as List)
            .map((item) => SentimentAnalysisResult.fromJson(item))
            .toList();
        return ApiResponse(
          success: true,
          data: analyses,
          message: '找到 ${analyses.length} 条分析记录',
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '获取用户分析历史失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取情感趋势分析
  Future<ApiResponse<Map<String, dynamic>>> getSentimentTrend({
    required int conversationId,
    required DateTime startTime,
    required DateTime endTime,
  }) async {
    try {
      final response = await _client.get(
        Uri.parse('$baseURL/trend/$conversationId?startTime=${startTime.toIso8601String()}&endTime=${endTime.toIso8601String()}'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true) {
        return ApiResponse(
          success: true,
          data: data['trendAnalysis'] as Map<String, dynamic>,
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '获取情感趋势分析失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取紧急情绪检测
  Future<ApiResponse<List<SentimentAnalysisResult>>> getEmergencyEmotions() async {
    try {
      final response = await _client.get(
        Uri.parse('$baseURL/emergency'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true && data['emergencies'] != null) {
        final emergencies = (data['emergencies'] as List)
            .map((item) => SentimentAnalysisResult.fromJson(item))
            .toList();
        return ApiResponse(
          success: true,
          data: emergencies,
          message: '检测到 ${emergencies.length} 条紧急情绪',
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '获取紧急情绪检测失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取用户情感基线
  Future<ApiResponse<Map<String, dynamic>>> getUserBaseline({
    required int userId,
    DateTime? startTime,
    DateTime? endTime,
  }) async {
    try {
      final params = <String, String>{};
      if (startTime != null) params['startTime'] = startTime.toIso8601String();
      if (endTime != null) params['endTime'] = endTime.toIso8601String();

      final queryString = params.entries.map((e) => '${e.key}=${e.value}').join('&');
      final url = '$baseURL/baseline/$userId${queryString.isNotEmpty ? '?$queryString' : ''}';

      final response = await _client.get(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true) {
        return ApiResponse(
          success: true,
          data: data['baseline'] as Map<String, dynamic>,
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '获取用户情感基线失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 获取情感统计信息
  Future<ApiResponse<Map<String, dynamic>>> getStatistics({
    DateTime? startTime,
    DateTime? endTime,
  }) async {
    try {
      final params = <String, String>{};
      if (startTime != null) params['startTime'] = startTime.toIso8601String();
      if (endTime != null) params['endTime'] = endTime.toIso8601String();

      final queryString = params.entries.map((e) => '${e.key}=${e.value}').join('&');
      final url = '$baseURL/statistics${queryString.isNotEmpty ? '?$queryString' : ''}';

      final response = await _client.get(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true) {
        return ApiResponse(
          success: true,
          data: data['statistics'] as Map<String, dynamic>,
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '获取情感统计信息失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 健康检查
  Future<ApiResponse<Map<String, dynamic>>> healthCheck() async {
    try {
      final response = await _client.get(
        Uri.parse('$baseURL/health'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['status'] == 'UP') {
        return ApiResponse(
          success: true,
          data: data as Map<String, dynamic>,
          message: '情感分析系统健康',
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '系统异常',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 清理旧记录
  Future<ApiResponse<int>> cleanupOldRecords(DateTime cutoffTime) async {
    try {
      final response = await _client.delete(
        Uri.parse('$baseURL/cleanup?cutoffTime=${cutoffTime.toIso8601String()}'),
        headers: {'Content-Type': 'application/json'},
      );

      final data = jsonDecode(response.body);
      if (data['success'] == true) {
        return ApiResponse(
          success: true,
          data: data['deletedCount'] as int,
          message: data['message'] as String?,
        );
      } else {
        return ApiResponse(
          success: false,
          error: data['error'] ?? '清理旧记录失败',
        );
      }
    } catch (e) {
      return ApiResponse(
        success: false,
        error: '网络错误：${e.toString()}',
      );
    }
  }

  /// 工具函数：格式化情感分析结果
  String formatSentimentResult(SentimentAnalysisResult analysis) {
    String intensityText;
    if (analysis.sentimentIntensity > 0.7) {
      intensityText = '强烈';
    } else if (analysis.sentimentIntensity > 0.4) {
      intensityText = '中等';
    } else if (analysis.sentimentIntensity > 0.2) {
      intensityText = '轻微';
    } else {
      intensityText = '微弱';
    }

    StringBuffer result = StringBuffer();
    result.writeln('情感分析结果：');
    result.writeln('- 主要情感：${_getEmotionName(analysis.primaryEmotion)} ${_getEmotionIcon(analysis.primaryEmotion)}');
    result.writeln('- 次要情感：${_getEmotionName(analysis.secondaryEmotion)}');
    result.writeln('- 情感强度：$intensityText (${(analysis.sentimentIntensity * 100).toStringAsFixed(1)}%)');
    result.writeln('- 置信度：${(analysis.confidenceScore * 100).toStringAsFixed(1)}%');
    result.writeln('- 模型版本：${analysis.modelVersion}');
    result.writeln('- 分析耗时：${analysis.processingLatencyMs}ms');

    if (analysis.emergencyFlag) {
      result.writeln('⚠️ 紧急情绪警报：${analysis.emergencyReason}');
    }

    if (analysis.baselineDeviation != null) {
      String deviationText = analysis.baselineDeviation! > 0 ? '高于' : '低于';
      result.writeln('📊 情感基线：$deviationText 基线 ${(analysis.baselineDeviation!.abs() * 100).toStringAsFixed(1)}%');
    }

    return result.toString();
  }

  String _getEmotionName(String emotion) {
    final names = {
      'joy': '快乐',
      'sadness': '悲伤',
      'anger': '愤怒',
      'fear': '恐惧',
      'surprise': '惊讶',
      'disgust': '厌恶',
      'neutral': '中性',
    };
    return names[emotion] ?? emotion;
  }

  String _getEmotionIcon(String emotion) {
    final icons = {
      'joy': '😊',
      'sadness': '😢',
      'anger': '😠',
      'fear': '😨',
      'surprise': '😲',
      'disgust': '🤢',
      'neutral': '😐',
    };
    return icons[emotion] ?? '❓';
  }

  void dispose() {
    _client.close();
  }
}