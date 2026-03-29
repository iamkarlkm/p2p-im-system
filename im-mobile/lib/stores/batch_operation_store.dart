// 批量操作状态管理
import 'package:mobx/mobx.dart';
import '../models/batch_operation_request.dart';
import '../models/batch_operation_result.dart';
import '../models/batch_operation_type.dart';
import '../services/batch_operation_service.dart';

part 'batch_operation_store.g.dart';

class BatchOperationStore = _BatchOperationStore with _$BatchOperationStore;

abstract class _BatchOperationStore with Store {
  final BatchOperationService _service = BatchOperationService();

  @observable
  ObservableList<String> selectedMessageIds = ObservableList<String>();

  @observable
  BatchOperationType? currentOperation;

  @observable
  BatchOperationResult? operationResult;

  @observable
  ObservableList<BatchOperationResult> operationHistory = ObservableList<BatchOperationResult>();

  @observable
  bool isLoading = false;

  @observable
  String? error;

  @observable
  bool isBatchSelecting = false;

  @observable
  String? lastBatchOperationId;

  @observable
  ObservableMap<String, double> progressMap = ObservableMap<String, double>();

  // ============ 选择管理 ============

  @action
  void startBatchSelection() {
    isBatchSelecting = true;
    selectedMessageIds.clear();
  }

  @action
  void endBatchSelection() {
    isBatchSelecting = false;
    selectedMessageIds.clear();
  }

  @action
  void toggleMessageSelection(String messageId) {
    if (selectedMessageIds.contains(messageId)) {
      selectedMessageIds.remove(messageId);
    } else {
      selectedMessageIds.add(messageId);
    }
  }

  @action
  void selectMessage(String messageId) {
    if (!selectedMessageIds.contains(messageId)) {
      selectedMessageIds.add(messageId);
    }
  }

  @action
  void deselectMessage(String messageId) {
    selectedMessageIds.remove(messageId);
  }

  @action
  void selectAll(List<String> messageIds, {int? maxCount}) {
    final limit = maxCount ?? 100;
    selectedMessageIds.clear();
    selectedMessageIds.addAll(messageIds.take(limit));
  }

  @action
  void clearSelection() {
    selectedMessageIds.clear();
  }

  @computed
  bool get hasSelection => selectedMessageIds.isNotEmpty;

  @computed
  int get selectedCount => selectedMessageIds.length;

  bool isSelected(String messageId) => selectedMessageIds.contains(messageId);

  bool isOverLimit(BatchOperationType type) => 
      selectedCount > type.maxBatchSize;

  // ============ 批量操作执行 ============

  @action
  Future<BatchOperationResult?> executeBatchOperation(
    BatchOperationType operationType, {
    Map<String, dynamic>? additionalParams,
  }) async {
    if (selectedMessageIds.isEmpty) {
      error = '请先选择消息';
      return null;
    }

    if (selectedCount > operationType.maxBatchSize) {
      error = '批量操作最多支持 ${operationType.maxBatchSize} 条消息';
      return null;
    }

    isLoading = true;
    error = null;
    currentOperation = operationType;

    try {
      final request = BatchOperationRequest(
        messageIds: selectedMessageIds.toList(),
        operationType: operationType,
        asyncExecution: selectedCount > 50,
        additionalParams: additionalParams,
      );

      final result = await _service.executeBatchOperation(request);
      
      operationResult = result;
      lastBatchOperationId = result.batchId;
      
      if (result.isCompleted) {
        clearSelection();
      }
      
      isLoading = false;
      return result;
    } catch (e) {
      error = e.toString();
      isLoading = false;
      return null;
    }
  }

  @action
  Future<BatchOperationResult?> previewBatchOperation(
    BatchOperationType operationType, {
    Map<String, dynamic>? additionalParams,
  }) async {
    if (selectedMessageIds.isEmpty) return null;

    isLoading = true;
    try {
      final request = BatchOperationRequest(
        messageIds: selectedMessageIds.toList(),
        operationType: operationType,
        additionalParams: additionalParams,
      );

      final result = await _service.previewBatchOperation(request);
      isLoading = false;
      return result;
    } catch (e) {
      isLoading = false;
      return null;
    }
  }

  @action
  Future<BatchOperationResult?> getOperationResult(String batchId) async {
    try {
      final result = await _service.getBatchOperationResult(batchId);
      operationResult = result;
      return result;
    } catch (e) {
      return null;
    }
  }

  @action
  Future<bool> cancelBatchOperation(String batchId) async {
    try {
      return await _service.cancelBatchOperation(batchId);
    } catch (e) {
      return false;
    }
  }

  @action
  Future<void> loadOperationHistory({int page = 0, int size = 20}) async {
    isLoading = true;
    try {
      final history = await _service.getBatchOperationHistory(page: page, size: size);
      operationHistory.clear();
      operationHistory.addAll(history);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> refreshCurrentOperation() async {
    if (lastBatchOperationId != null) {
      await getOperationResult(lastBatchOperationId!);
    }
  }

  @action
  void clearResult() {
    operationResult = null;
    error = null;
  }

  // ============ 快捷操作 ============

  @action
  Future<BatchOperationResult?> quickForward(
    String targetConversationId, {
    bool keepOriginal = false,
  }) async {
    return executeBatchOperation(
      BatchOperationType.forward,
      additionalParams: {
        'targetConversationId': targetConversationId,
        'keepOriginal': keepOriginal,
      },
    );
  }

  @action
  Future<BatchOperationResult?> quickDelete({String? reason}) async {
    return executeBatchOperation(
      BatchOperationType.delete,
      additionalParams: {'reason': reason},
    );
  }

  @action
  Future<BatchOperationResult?> quickFavorite() async {
    return executeBatchOperation(BatchOperationType.favorite);
  }

  @action
  Future<BatchOperationResult?> quickRecall() async {
    return executeBatchOperation(BatchOperationType.recall);
  }

  @action
  Future<BatchOperationResult?> quickPin() async {
    return executeBatchOperation(BatchOperationType.pin);
  }

  @action
  Future<BatchOperationResult?> quickArchive() async {
    return executeBatchOperation(BatchOperationType.archive);
  }

  @action
  Future<BatchOperationResult?> quickMarkRead() async {
    return executeBatchOperation(BatchOperationType.markRead);
  }

  // ============ 计算属性 ============

  @computed
  bool get isOperating => operationResult?.isRunning ?? false;

  @computed
  bool get isOperationComplete => operationResult?.isCompleted ?? false;

  @computed
  double get operationProgress => operationResult?.progress ?? 0;
}
