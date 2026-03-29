/**
 * 消息编辑服务
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */

import 'package:dio/dio.dart';
import '../models/message_edit_model.dart';
import '../models/pagination_model.dart';

class MessageEditService {
  final Dio _dio;
  static const String _basePath = '/api/v1/messages/edit';

  MessageEditService(this._dio);

  /// 编辑消息
  Future<MessageEdit> editMessage(EditMessageRequest request) async {
    final response = await _dio.post(
      _basePath,
      data: request.toJson(),
    );
    return MessageEdit.fromJson(response.data['data']);
  }

  /// 获取消息的编辑历史
  Future<MessageEditHistory> getEditHistory(int messageId) async {
    final response = await _dio.get(
      '$_basePath/history/$messageId',
    );
    return MessageEditHistory.fromJson(response.data['data']);
  }

  /// 分页获取编辑历史
  Future<PaginationResult<EditHistoryItem>> getEditHistoryPage(
    int messageId, {
    int page = 0,
    int size = 10,
  }) async {
    final response = await _dio.get(
      '$_basePath/history/$messageId/page',
      queryParameters: {'page': page, 'size': size},
    );
    
    final data = response.data['data'];
    return PaginationResult(
      items: (data['content'] as List)
          .map((e) => EditHistoryItem.fromJson(e))
          .toList(),
      totalPages: data['totalPages'],
      totalElements: data['totalElements'],
      currentPage: data['number'],
      size: data['size'],
    );
  }

  /// 检查是否可以编辑
  Future<CanEditResult> canEditMessage(int messageId) async {
    final response = await _dio.get(
      '$_basePath/can-edit/$messageId',
    );
    return CanEditResult.fromJson(response.data['data']);
  }

  /// 获取消息的编辑次数
  Future<int> getEditCount(int messageId) async {
    final response = await _dio.get(
      '$_basePath/count/$messageId',
    );
    return response.data['data'] as int;
  }

  /// 批量获取编辑次数
  Future<Map<int, int>> getEditCounts(List<int> messageIds) async {
    final response = await _dio.post(
      '$_basePath/counts',
      data: messageIds,
    );
    
    final data = response.data['data'] as Map<String, dynamic>;
    return data.map((key, value) => MapEntry(int.parse(key), value as int));
  }

  /// 回滚到指定版本
  Future<MessageEdit> revertToVersion(int messageId, int sequence) async {
    final response = await _dio.post(
      '$_basePath/revert/$messageId',
      queryParameters: {'sequence': sequence},
    );
    return MessageEdit.fromJson(response.data['data']);
  }

  /// 获取当前用户的编辑历史
  Future<PaginationResult<MessageEdit>> getMyEditHistory({
    int page = 0,
    int size = 20,
  }) async {
    final response = await _dio.get(
      '$_basePath/my-edits',
      queryParameters: {'page': page, 'size': size},
    );
    
    final data = response.data['data'];
    return PaginationResult(
      items: (data['content'] as List)
          .map((e) => MessageEdit.fromJson(e))
          .toList(),
      totalPages: data['totalPages'],
      totalElements: data['totalElements'],
      currentPage: data['number'],
      size: data['size'],
    );
  }

  /// 快速编辑（简化版）
  Future<MessageEdit> quickEdit(int messageId, String newContent) async {
    // 先获取当前历史
    final history = await getEditHistory(messageId);
    final currentContent = history.currentContent;

    final request = EditMessageRequest(
      messageId: messageId,
      originalContent: currentContent,
      editedContent: newContent,
      editType: EditType.normal,
    );

    return editMessage(request);
  }

  /// 检查并编辑
  Future<EditResult<MessageEdit>> checkAndEdit(EditMessageRequest request) async {
    try {
      // 先检查权限
      final canEdit = await canEditMessage(request.messageId);
      
      if (!canEdit.canEdit) {
        return EditResult.error(canEdit.reason);
      }

      // 执行编辑
      final result = await editMessage(request);
      return EditResult.success(result);
    } on DioException catch (e) {
      return EditResult.error(
        e.response?.data?['message'] ?? e.message ?? '编辑失败',
      );
    }
  }

  /// 批量预加载编辑次数
  Future<Map<int, int>> preloadEditCounts(List<int> messageIds) async {
    if (messageIds.isEmpty) return {};

    // 去重
    final uniqueIds = messageIds.toSet().toList();

    // 最多批量查询100个
    const batchSize = 100;
    final results = <int, int>{};

    for (var i = 0; i < uniqueIds.length; i += batchSize) {
      final batch = uniqueIds.sublist(
        i,
        i + batchSize > uniqueIds.length ? uniqueIds.length : i + batchSize,
      );
      final batchResults = await getEditCounts(batch);
      results.addAll(batchResults);
    }

    return results;
  }
}

/// 编辑结果封装
class EditResult<T> {
  final bool success;
  final T? data;
  final String? error;

  const EditResult._(this.success, this.data, this.error);

  factory EditResult.success(T data) => EditResult._(true, data, null);
  factory EditResult.error(String error) => EditResult._(false, null, error);

  bool get isSuccess => success;
  bool get isError => !success;
}

/// 分页结果
class PaginationResult<T> {
  final List<T> items;
  final int totalPages;
  final int totalElements;
  final int currentPage;
  final int size;

  const PaginationResult({
    required this.items,
    required this.totalPages,
    required this.totalElements,
    required this.currentPage,
    required this.size,
  });

  bool get hasNext => currentPage < totalPages - 1;
  bool get hasPrevious => currentPage > 0;
  bool get isEmpty => items.isEmpty;
  bool get isNotEmpty => items.isNotEmpty;
}
