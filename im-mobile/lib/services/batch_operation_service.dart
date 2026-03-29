// 批量操作服务
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../models/batch_operation_request.dart';
import '../models/batch_operation_result.dart';
import '../models/batch_operation_type.dart';
import 'auth_service.dart';

class BatchOperationService {
  static final BatchOperationService _instance = BatchOperationService._internal();
  factory BatchOperationService() => _instance;
  BatchOperationService._internal();

  final AuthService _authService = AuthService();
  final String _baseUrl = ApiConfig.baseUrl;

  Future<BatchOperationResult> executeBatchOperation(
    BatchOperationRequest request,
  ) async {
    final token = await _authService.getToken();
    final response = await http.post(
      Uri.parse('$_baseUrl/batch-operations'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return BatchOperationResult.fromJson(data['data']);
    } else {
      throw Exception('执行批量操作失败: ${response.statusCode}');
    }
  }

  Future<BatchOperationResult> previewBatchOperation(
    BatchOperationRequest request,
  ) async {
    final token = await _authService.getToken();
    final response = await http.post(
      Uri.parse('$_baseUrl/batch-operations/preview'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return BatchOperationResult.fromJson(data['data']);
    } else {
      throw Exception('预览批量操作失败: ${response.statusCode}');
    }
  }

  Future<BatchOperationResult> getBatchOperationResult(String batchId) async {
    final token = await _authService.getToken();
    final response = await http.get(
      Uri.parse('$_baseUrl/batch-operations/$batchId'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return BatchOperationResult.fromJson(data['data']);
    } else {
      throw Exception('获取批量操作结果失败: ${response.statusCode}');
    }
  }

  Future<bool> cancelBatchOperation(String batchId) async {
    final token = await _authService.getToken();
    final response = await http.post(
      Uri.parse('$_baseUrl/batch-operations/$batchId/cancel'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['data'] ?? false;
    } else {
      throw Exception('取消批量操作失败: ${response.statusCode}');
    }
  }

  Future<List<BatchOperationResult>> getBatchOperationHistory({
    int page = 0,
    int size = 20,
  }) async {
    final token = await _authService.getToken();
    final response = await http.get(
      Uri.parse('$_baseUrl/batch-operations/history?page=$page&size=$size'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'];
      return list.map((e) => BatchOperationResult.fromJson(e)).toList();
    } else {
      throw Exception('获取批量操作历史失败: ${response.statusCode}');
    }
  }

  // 快捷操作方法
  Future<BatchOperationResult> batchForward(
    List<String> messageIds,
    String targetConversationId, {
    bool keepOriginal = false,
  }) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.forward,
      targetConversationId: targetConversationId,
      keepOriginal: keepOriginal,
    ));
  }

  Future<BatchOperationResult> batchDelete(
    List<String> messageIds, {
    String? reason,
  }) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.delete,
      reason: reason,
    ));
  }

  Future<BatchOperationResult> batchRecall(List<String> messageIds) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.recall,
    ));
  }

  Future<BatchOperationResult> batchFavorite(List<String> messageIds) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.favorite,
    ));
  }

  Future<BatchOperationResult> batchPin(List<String> messageIds) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.pin,
    ));
  }

  Future<BatchOperationResult> batchMarkRead(List<String> messageIds) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.markRead,
    ));
  }

  Future<BatchOperationResult> batchArchive(List<String> messageIds) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.archive,
    ));
  }

  Future<BatchOperationResult> batchAddTag(
    List<String> messageIds,
    String tagId,
  ) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.addTag,
      additionalParams: {'tagId': tagId},
    ));
  }

  Future<BatchOperationResult> batchExport(
    List<String> messageIds, {
    String format = 'json',
  }) async {
    return executeBatchOperation(BatchOperationRequest(
      messageIds: messageIds,
      operationType: BatchOperationType.export,
      additionalParams: {'format': format},
      asyncExecution: messageIds.length > 50,
    ));
  }
}
