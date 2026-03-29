import 'package:dio/dio.dart';
import '../config/api_config.dart';
import '../models/self_destruct_message_model.dart';
import 'api_client.dart';

/// 阅后即焚消息API
/// 
/// @author IM Development Team
/// @since 1.0.0
class SelfDestructMessageApi {
  final Dio _dio = ApiClient().dio;

  /// 获取配置
  Future<SelfDestructConfig> getConfig() async {
    final response = await _dio.get('${ApiConfig.baseUrl}/self-destruct-messages/config');
    return SelfDestructConfig.fromJson(response.data);
  }

  /// 创建消息
  Future<SelfDestructMessageModel> createMessage(SelfDestructCreateRequest request) async {
    final response = await _dio.post(
      '${ApiConfig.baseUrl}/self-destruct-messages',
      data: request.toJson(),
    );
    return SelfDestructMessageModel.fromJson(response.data);
  }

  /// 阅读消息
  Future<SelfDestructReadResponse> readMessage(String messageId) async {
    final response = await _dio.post(
      '${ApiConfig.baseUrl}/self-destruct-messages/$messageId/read',
    );
    return SelfDestructReadResponse.fromJson(response.data);
  }

  /// 获取消息详情
  Future<SelfDestructMessageModel> getMessage(String messageId) async {
    final response = await _dio.get('${ApiConfig.baseUrl}/self-destruct-messages/$messageId');
    return SelfDestructMessageModel.fromJson(response.data);
  }

  /// 获取会话消息
  Future<List<SelfDestructMessageModel>> getMessagesByConversation(
    String conversationId, {
    int page = 0,
    int size = 20,
  }) async {
    final response = await _dio.get(
      '${ApiConfig.baseUrl}/self-destruct-messages/conversation/$conversationId',
      queryParameters: {'page': page, 'size': size},
    );
    return (response.data as List)
        .map((json) => SelfDestructMessageModel.fromJson(json))
        .toList();
  }

  /// 获取发送的消息
  Future<List<SelfDestructMessageModel>> getSentMessages() async {
    final response = await _dio.get('${ApiConfig.baseUrl}/self-destruct-messages/sent');
    return (response.data as List)
        .map((json) => SelfDestructMessageModel.fromJson(json))
        .toList();
  }

  /// 获取接收的消息
  Future<List<SelfDestructMessageModel>> getReceivedMessages() async {
    final response = await _dio.get('${ApiConfig.baseUrl}/self-destruct-messages/received');
    return (response.data as List)
        .map((json) => SelfDestructMessageModel.fromJson(json))
        .toList();
  }

  /// 获取未读数量
  Future<int> getUnreadCount() async {
    final response = await _dio.get('${ApiConfig.baseUrl}/self-destruct-messages/unread/count');
    return response.data['count'] as int;
  }

  /// 获取会话未读数量
  Future<int> getUnreadCountByConversation(String conversationId) async {
    final response = await _dio.get(
      '${ApiConfig.baseUrl}/self-destruct-messages/conversation/$conversationId/unread/count',
    );
    return response.data['count'] as int;
  }

  /// 删除消息
  Future<void> deleteMessage(String messageId) async {
    await _dio.delete('${ApiConfig.baseUrl}/self-destruct-messages/$messageId');
  }

  /// 销毁消息
  Future<void> destroyMessage(String messageId) async {
    await _dio.post('${ApiConfig.baseUrl}/self-destruct-messages/$messageId/destroy');
  }

  /// 检测截图
  Future<ScreenshotDetectResponse> detectScreenshot(String messageId) async {
    final response = await _dio.post(
      '${ApiConfig.baseUrl}/self-destruct-messages/$messageId/screenshot-detect',
      data: {'messageId': messageId},
    );
    return ScreenshotDetectResponse.fromJson(response.data);
  }

  /// 获取被截图的消息列表
  Future<List<SelfDestructMessageModel>> getScreenshotDetectedMessages() async {
    final response = await _dio.get('${ApiConfig.baseUrl}/self-destruct-messages/screenshot-detected');
    return (response.data as List)
        .map((json) => SelfDestructMessageModel.fromJson(json))
        .toList();
  }

  /// 检查消息是否已销毁
  Future<bool> isMessageDestroyed(String messageId) async {
    final response = await _dio.get(
      '${ApiConfig.baseUrl}/self-destruct-messages/$messageId/destroyed',
    );
    return response.data['destroyed'] as bool;
  }

  /// 获取剩余秒数
  Future<int> getRemainingSeconds(String messageId) async {
    final response = await _dio.get(
      '${ApiConfig.baseUrl}/self-destruct-messages/$messageId/remaining-seconds',
    );
    return response.data['remainingSeconds'] as int;
  }
}
