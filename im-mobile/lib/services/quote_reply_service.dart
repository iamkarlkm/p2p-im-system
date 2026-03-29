import 'package:dio/dio.dart';
import 'package:im_mobile/models/message_quote_reply_model.dart';
import 'package:im_mobile/services/api_client.dart';

/// 消息引用回复服务
class QuoteReplyService {
  final Dio _dio = ApiClient().dio;

  /// 创建引用回复
  Future<MessageQuoteReplyModel?> createQuoteReply({
    required int quotedMessageId,
    required int conversationId,
    required String replyContent,
    int? parentQuoteId,
    List<int>? batchQuotedMessageIds,
    bool includeOriginal = true,
    String? highlightKeywords,
  }) async {
    try {
      final response = await _dio.post('/api/v1/quote-reply/create', data: {
        'quotedMessageId': quotedMessageId,
        'conversationId': conversationId,
        'replyContent': replyContent,
        'parentQuoteId': parentQuoteId,
        'batchQuotedMessageIds': batchQuotedMessageIds,
        'includeOriginal': includeOriginal,
        'highlightKeywords': highlightKeywords,
      });

      if (response.statusCode == 200 && response.data['success']) {
        return MessageQuoteReplyModel.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Create quote reply error: $e');
      rethrow;
    }
  }

  /// 创建批量引用回复
  Future<MessageQuoteReplyModel?> createBatchQuoteReply({
    required int conversationId,
    required String replyContent,
    required List<int> batchQuotedMessageIds,
    bool includeOriginal = true,
  }) async {
    try {
      final response = await _dio.post('/api/v1/quote-reply/create', data: {
        'conversationId': conversationId,
        'replyContent': replyContent,
        'batchQuotedMessageIds': batchQuotedMessageIds,
        'includeOriginal': includeOriginal,
      });

      if (response.statusCode == 200 && response.data['success']) {
        return MessageQuoteReplyModel.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Create batch quote reply error: $e');
      rethrow;
    }
  }

  /// 获取引用回复详情
  Future<MessageQuoteReplyModel?> getQuoteReplyById(int id) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/$id');
      if (response.statusCode == 200 && response.data['success']) {
        return MessageQuoteReplyModel.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Get quote reply error: $e');
      rethrow;
    }
  }

  /// 通过消息ID获取引用回复
  Future<MessageQuoteReplyModel?> getQuoteReplyByMessageId(int messageId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/by-message/$messageId');
      if (response.statusCode == 200 && response.data['success']) {
        return MessageQuoteReplyModel.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Get quote reply by message error: $e');
      rethrow;
    }
  }

  /// 获取会话的引用回复列表
  Future<List<MessageQuoteReplyModel>> getQuoteRepliesByConversation(int conversationId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/conversation/$conversationId');
      if (response.statusCode == 200 && response.data['success']) {
        final List<dynamic> data = response.data['data'];
        return data.map((json) => MessageQuoteReplyModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      print('Get conversation quotes error: $e');
      rethrow;
    }
  }

  /// 获取我的引用回复（分页）
  Future<List<MessageQuoteReplyModel>> getMyQuoteReplies(
    int conversationId, {
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _dio.get(
        '/api/v1/quote-reply/my/$conversationId',
        queryParameters: {'page': page, 'size': size},
      );
      if (response.statusCode == 200 && response.data['success']) {
        final pageData = response.data['data'];
        final List<dynamic> content = pageData['content'];
        return content.map((json) => MessageQuoteReplyModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      print('Get my quotes error: $e');
      rethrow;
    }
  }

  /// 更新引用回复
  Future<MessageQuoteReplyModel?> updateQuoteReply(int id, String newContent) async {
    try {
      final response = await _dio.put('/api/v1/quote-reply/$id', data: {
        'content': newContent,
      });
      if (response.statusCode == 200 && response.data['success']) {
        return MessageQuoteReplyModel.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Update quote reply error: $e');
      rethrow;
    }
  }

  /// 删除引用回复
  Future<bool> deleteQuoteReply(int id) async {
    try {
      final response = await _dio.delete('/api/v1/quote-reply/$id');
      return response.statusCode == 200 && response.data['success'];
    } catch (e) {
      print('Delete quote reply error: $e');
      rethrow;
    }
  }

  /// 获取引用树
  Future<List<MessageQuoteReplyModel>> getQuoteTree(int rootQuoteId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/tree/$rootQuoteId');
      if (response.statusCode == 200 && response.data['success']) {
        final List<dynamic> data = response.data['data'];
        return data.map((json) => MessageQuoteReplyModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      print('Get quote tree error: $e');
      rethrow;
    }
  }

  /// 获取嵌套引用回复
  Future<List<MessageQuoteReplyModel>> getNestedQuotes(int parentQuoteId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/nested/$parentQuoteId');
      if (response.statusCode == 200 && response.data['success']) {
        final List<dynamic> data = response.data['data'];
        return data.map((json) => MessageQuoteReplyModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      print('Get nested quotes error: $e');
      rethrow;
    }
  }

  /// 统计消息的引用数量
  Future<int> countQuotesByMessage(int messageId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/count/$messageId');
      if (response.statusCode == 200 && response.data['success']) {
        return response.data['data']['count'] as int;
      }
      return 0;
    } catch (e) {
      print('Count quotes error: $e');
      return 0;
    }
  }

  /// 获取消息的引用回复列表
  Future<List<MessageQuoteReplyModel>> getQuotesByMessage(int messageId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/by-quoted-message/$messageId');
      if (response.statusCode == 200 && response.data['success']) {
        final List<dynamic> data = response.data['data'];
        return data.map((json) => MessageQuoteReplyModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      print('Get quotes by message error: $e');
      rethrow;
    }
  }

  /// 获取引用链中包含某消息的回复
  Future<List<MessageQuoteReplyModel>> getQuotesContainingInChain(
    int conversationId,
    int messageId,
  ) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/chain/$conversationId/$messageId');
      if (response.statusCode == 200 && response.data['success']) {
        final List<dynamic> data = response.data['data'];
        return data.map((json) => MessageQuoteReplyModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      print('Get quotes in chain error: $e');
      rethrow;
    }
  }

  /// 撤回引用回复
  Future<MessageQuoteReplyModel?> recallQuoteReply(int id) async {
    try {
      final response = await _dio.post('/api/v1/quote-reply/$id/recall');
      if (response.statusCode == 200 && response.data['success']) {
        return MessageQuoteReplyModel.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      print('Recall quote reply error: $e');
      rethrow;
    }
  }

  /// 检查是否可以引用消息
  Future<bool> canQuoteMessage(int messageId) async {
    try {
      final response = await _dio.get('/api/v1/quote-reply/can-quote/$messageId');
      if (response.statusCode == 200 && response.data['success']) {
        return response.data['data']['canQuote'] as bool;
      }
      return false;
    } catch (e) {
      print('Check can quote error: $e');
      return false;
    }
  }
}
