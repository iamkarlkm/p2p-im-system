import 'package:dio/dio.dart';
import '../models/conversation_pin.dart';

class PinService {
  static final _dio = Dio(BaseOptions(
    baseUrl: '/api/conversations/pin',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
  ));

  static Future<PinConversationResponse> pin(PinConversationRequest request) async {
    try {
      final response = await _dio.post('/', data: request.toJson());
      return PinConversationResponse.fromJson(response.data);
    } on DioException catch (e) {
      return PinConversationResponse(success: false, message: e.message ?? 'Error');
    }
  }

  static Future<PinConversationResponse> unpin(int conversationId) async {
    try {
      final response = await _dio.delete('/$conversationId');
      return PinConversationResponse.fromJson(response.data);
    } on DioException catch (e) {
      return PinConversationResponse(success: false, message: e.message ?? 'Error');
    }
  }

  static Future<List<ConversationPin>> getPinned() async {
    try {
      final response = await _dio.get('/');
      return (response.data as List).map((e) => ConversationPin.fromJson(e)).toList();
    } on DioException {
      return [];
    }
  }

  static Future<PinConversationResponse> reorder(List<int> ids) async {
    try {
      final response = await _dio.put('/reorder', data: {'conversationIds': ids});
      return PinConversationResponse.fromJson(response.data);
    } on DioException catch (e) {
      return PinConversationResponse(success: false, message: e.message ?? 'Error');
    }
  }

  static Future<bool> isPinned(int conversationId) async {
    try {
      final response = await _dio.get('/$conversationId/status');
      return response.data['pinned'] ?? false;
    } on DioException {
      return false;
    }
  }
}
