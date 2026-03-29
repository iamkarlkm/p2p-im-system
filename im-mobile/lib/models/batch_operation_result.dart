// 批量操作结果模型
import 'batch_operation_type.dart';
import 'package:json_annotation/json_annotation.dart';

part 'batch_operation_result.g.dart';

@JsonEnum(valueField: 'value')
enum BatchOperationStatus {
  pending('PENDING'),
  running('RUNNING'),
  completed('COMPLETED'),
  partialSuccess('PARTIAL_SUCCESS'),
  failed('FAILED'),
  cancelled('CANCELLED');

  final String value;
  const BatchOperationStatus(this.value);
  String toJson() => value;
}

@JsonEnum(valueField: 'value')
enum FailureReason {
  permissionDenied('PERMISSION_DENIED'),
  messageNotFound('MESSAGE_NOT_FOUND'),
  alreadyDeleted('ALREADY_DELETED'),
  recallTimeout('RECALL_TIMEOUT'),
  networkError('NETWORK_ERROR'),
  serverError('SERVER_ERROR'),
  validationError('VALIDATION_ERROR');

  final String value;
  const FailureReason(this.value);
  String toJson() => value;
}

@JsonEnum(valueField: 'value')
enum SkipReason {
  alreadyProcessed('ALREADY_PROCESSED'),
  noPermission('NO_PERMISSION'),
  filteredOut('FILTERED_OUT'),
  duplicateRequest('DUPLICATE_REQUEST');

  final String value;
  const SkipReason(this.value);
  String toJson() => value;
}

@JsonSerializable()
class FailedOperation {
  final String messageId;
  final String errorCode;
  final String errorMessage;
  final FailureReason reason;

  FailedOperation({
    required this.messageId,
    required this.errorCode,
    required this.errorMessage,
    required this.reason,
  });

  factory FailedOperation.fromJson(Map<String, dynamic> json) =>
      _$FailedOperationFromJson(json);
  Map<String, dynamic> toJson() => _$FailedOperationToJson(this);
}

@JsonSerializable()
class SkippedOperation {
  final String messageId;
  final SkipReason reason;
  final String description;

  SkippedOperation({
    required this.messageId,
    required this.reason,
    required this.description,
  });

  factory SkippedOperation.fromJson(Map<String, dynamic> json) =>
      _$SkippedOperationFromJson(json);
  Map<String, dynamic> toJson() => _$SkippedOperationToJson(this);
}

@JsonSerializable()
class BatchOperationResult {
  final String batchId;
  final BatchOperationType operationType;
  final int totalCount;
  final int successCount;
  final int failureCount;
  final int skippedCount;
  final List<String> successMessageIds;
  final List<FailedOperation> failures;
  final List<SkippedOperation> skipped;
  final DateTime startTime;
  final DateTime endTime;
  final int durationMs;
  final BatchOperationStatus status;
  final bool asyncExecution;
  final String? asyncTaskId;
  final String operatorId;
  final String? operatorName;
  final String? targetConversationId;
  final Map<String, dynamic>? extraData;
  final List<String>? generatedMessageIds;

  BatchOperationResult({
    required this.batchId,
    required this.operationType,
    required this.totalCount,
    required this.successCount,
    required this.failureCount,
    required this.skippedCount,
    required this.successMessageIds,
    required this.failures,
    required this.skipped,
    required this.startTime,
    required this.endTime,
    required this.durationMs,
    required this.status,
    required this.asyncExecution,
    this.asyncTaskId,
    required this.operatorId,
    this.operatorName,
    this.targetConversationId,
    this.extraData,
    this.generatedMessageIds,
  });

  factory BatchOperationResult.fromJson(Map<String, dynamic> json) =>
      _$BatchOperationResultFromJson(json);
  Map<String, dynamic> toJson() => _$BatchOperationResultToJson(this);

  double get progress => totalCount > 0 
      ? ((successCount + failureCount + skippedCount) / totalCount) * 100 
      : 0;

  bool get isCompleted => 
      status == BatchOperationStatus.completed ||
      status == BatchOperationStatus.partialSuccess ||
      status == BatchOperationStatus.failed;

  bool get isRunning => status == BatchOperationStatus.running;
  bool get isPending => status == BatchOperationStatus.pending;
  bool get isSuccessful => status == BatchOperationStatus.completed && successCount == totalCount;
  bool get hasPartialSuccess => status == BatchOperationStatus.partialSuccess;
  bool get hasFailures => failureCount > 0;
}
