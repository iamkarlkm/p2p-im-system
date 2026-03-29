import 'package:dio/dio.dart';
import '../models/scheduled_message_recall_model.dart';
import '../utils/http_client.dart';

/// 消息定时撤回API
class ScheduledMessageRecallApi {
  final Dio _dio = HttpClient.instance;
  static const String _basePath = '/scheduled-recall';

  /// 创建定时撤回任务
  Future<ApiResult<ScheduledMessageRecallModel>> createScheduledRecall({
    required int messageId,
    required int conversationId,
    required ConversationType conversationType,
    String? messageContent,
    required int scheduledSeconds,
    String? recallReason,
    bool notifyReceivers = true,
    String? customNotifyMessage,
    bool isCancelable = true,
  }) async {
    try {
      final response = await _dio.post(
        '$_basePath/create',
        data: {
          'messageId': messageId,
          'conversationId': conversationId,
          'conversationType': conversationType.name.toUpperCase(),
          'messageContent': messageContent,
          'scheduledSeconds': scheduledSeconds,
          'recallReason': recallReason,
          'notifyReceivers': notifyReceivers,
          'customNotifyMessage': customNotifyMessage,
          'isCancelable': isCancelable,
        },
      );

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final data = response.data['data'];
        return ApiResult(
          success: true,
          data: ScheduledMessageRecallModel.fromJson(data),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '创建失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 取消定时撤回任务
  Future<ApiResult<ScheduledMessageRecallModel>> cancelScheduledRecall(int id) async {
    try {
      final response = await _dio.post('$_basePath/$id/cancel');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final data = response.data['data'];
        return ApiResult(
          success: true,
          data: ScheduledMessageRecallModel.fromJson(data),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '取消失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 获取定时撤回详情
  Future<ApiResult<ScheduledMessageRecallModel>> getRecallDetail(int id) async {
    try {
      final response = await _dio.get('$_basePath/$id');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final data = response.data['data'];
        return ApiResult(
          success: true,
          data: ScheduledMessageRecallModel.fromJson(data),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '获取详情失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 根据消息ID获取定时撤回
  Future<ApiResult<ScheduledMessageRecallModel>> getByMessageId(int messageId) async {
    try {
      final response = await _dio.get('$_basePath/message/$messageId');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final data = response.data['data'];
        return ApiResult(
          success: true,
          data: ScheduledMessageRecallModel.fromJson(data),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '未找到定时撤回',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 获取用户的所有定时撤回
  Future<ApiResult<List<ScheduledMessageRecallModel>>> getUserRecalls() async {
    try {
      final response = await _dio.get('$_basePath/list');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final list = response.data['data'] as List;
        return ApiResult(
          success: true,
          data: list.map((e) => ScheduledMessageRecallModel.fromJson(e)).toList(),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '获取列表失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 获取用户待执行的定时撤回
  Future<ApiResult<List<ScheduledMessageRecallModel>>> getPendingRecalls() async {
    try {
      final response = await _dio.get('$_basePath/pending');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final list = response.data['data'] as List;
        return ApiResult(
          success: true,
          data: list.map((e) => ScheduledMessageRecallModel.fromJson(e)).toList(),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '获取待执行任务失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 获取用户定时撤回统计
  Future<ApiResult<Map<String, dynamic>>> getRecallStats() async {
    try {
      final response = await _dio.get('$_basePath/stats');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        return ApiResult(
          success: true,
          data: response.data['data'],
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '获取统计失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 检查消息是否已设置定时撤回
  Future<ApiResult<Map<String, dynamic>>> checkMessageScheduled(int messageId) async {
    try {
      final response = await _dio.get('$_basePath/check/$messageId');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        return ApiResult(
          success: true,
          data: response.data['data'],
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '检查失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 删除定时撤回任务
  Future<ApiResult<void>> deleteScheduledRecall(int id) async {
    try {
      final response = await _dio.delete('$_basePath/$id');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        return ApiResult(success: true);
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '删除失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 更新定时撤回时间
  Future<ApiResult<ScheduledMessageRecallModel>> updateScheduledTime(
    int id,
    int newSeconds,
  ) async {
    try {
      final response = await _dio.post(
        '$_basePath/$id/update-time',
        data: {'newSeconds': newSeconds},
      );

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final data = response.data['data'];
        return ApiResult(
          success: true,
          data: ScheduledMessageRecallModel.fromJson(data),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '更新时间失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 获取推荐的时间选项
  Future<ApiResult<List<int>>> getTimeOptions() async {
    try {
      final response = await _dio.get('$_basePath/time-options');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        final list = response.data['data'] as List;
        return ApiResult(
          success: true,
          data: list.map((e) => e as int).toList(),
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '获取时间选项失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 手动执行定时撤回（测试用）
  Future<ApiResult<Map<String, dynamic>>> executeRecall(int id) async {
    try {
      final response = await _dio.post('$_basePath/$id/execute');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        return ApiResult(
          success: true,
          data: response.data['data'],
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '执行失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }

  /// 批量执行到期任务（定时任务用）
  Future<ApiResult<Map<String, dynamic>>> batchExecute() async {
    try {
      final response = await _dio.post('$_basePath/batch-execute');

      if (response.statusCode == 200 && response.data['code'] == 200) {
        return ApiResult(
          success: true,
          data: response.data['data'],
        );
      }

      return ApiResult(
        success: false,
        message: response.data['message'] ?? '批量执行失败',
      );
    } catch (e) {
      return ApiResult(success: false, message: '网络错误: $e');
    }
  }
}

/// API结果包装类
class ApiResult<T> {
  final bool success;
  final String? message;
  final T? data;

  ApiResult({required this.success, this.message, this.data});
}
