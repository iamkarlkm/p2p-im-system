import 'package:dio/dio.dart';
import '../models/message_forward.dart';
import '../models/conversation.dart';

class ForwardService {
  static final _dio = Dio(BaseOptions(
    baseUrl: '/api/messages/forward',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
  ));

  static Future<ForwardResponse> forwardMessage(ForwardRequest request) async {
    try {
      final response = await _dio.post('/', data: request.toJson());
      return ForwardResponse.fromJson(response.data);
    } on DioException catch (e) {
      return ForwardResponse(
        success: false,
        message: e.message ?? 'Network error',
        forwardedAt: DateTime.now(),
      );
    }
  }

  static Future<List<MessageForward>> getForwardHistory(int messageId) async {
    try {
      final response = await _dio.get('/history/$messageId');
      return (response.data as List)
          .map((e) => MessageForward.fromJson(e))
          .toList();
    } on DioException {
      return [];
    }
  }

  static String buildMergedTitle(int count) {
    return 'Merged $count messages';
  }

  static Future<List<ForwardResponse>> forwardToMultiple(
    List<int> messageIds,
    List<Conversation> targets, {
    bool merged = false,
    String? title,
  }) async {
    final futures = targets.map((t) async {
      return await forwardMessage(ForwardRequest(
        messageIds: messageIds,
        targetConversationId: t.id,
        merged: merged && messageIds.length > 1,
        mergedTitle: merged ? (title ?? buildMergedTitle(messageIds.length)) : null,
      ));
    }).toList();
    return await Future.wait(futures);
  }
}
