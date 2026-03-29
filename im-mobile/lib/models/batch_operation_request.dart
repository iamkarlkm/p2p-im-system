// 批量操作请求模型
import 'batch_operation_type.dart';
import 'package:json_annotation/json_annotation.dart';

part 'batch_operation_request.g.dart';

@JsonSerializable()
class BatchOperationRequest {
  final List<String> messageIds;
  final BatchOperationType operationType;
  final String? targetConversationId;
  final List<String>? targetUserIds;
  final bool? keepOriginal;
  final Map<String, dynamic>? additionalParams;
  final String? reason;
  final bool? deleteAfterMove;
  final bool? asyncExecution;
  final DateTime? scheduledTime;

  BatchOperationRequest({
    required this.messageIds,
    required this.operationType,
    this.targetConversationId,
    this.targetUserIds,
    this.keepOriginal,
    this.additionalParams,
    this.reason,
    this.deleteAfterMove,
    this.asyncExecution,
    this.scheduledTime,
  });

  factory BatchOperationRequest.fromJson(Map<String, dynamic> json) =>
      _$BatchOperationRequestFromJson(json);

  Map<String, dynamic> toJson() => _$BatchOperationRequestToJson(this);

  BatchOperationRequest copyWith({
    List<String>? messageIds,
    BatchOperationType? operationType,
    String? targetConversationId,
    List<String>? targetUserIds,
    bool? keepOriginal,
    Map<String, dynamic>? additionalParams,
    String? reason,
    bool? deleteAfterMove,
    bool? asyncExecution,
    DateTime? scheduledTime,
  }) {
    return BatchOperationRequest(
      messageIds: messageIds ?? this.messageIds,
      operationType: operationType ?? this.operationType,
      targetConversationId: targetConversationId ?? this.targetConversationId,
      targetUserIds: targetUserIds ?? this.targetUserIds,
      keepOriginal: keepOriginal ?? this.keepOriginal,
      additionalParams: additionalParams ?? this.additionalParams,
      reason: reason ?? this.reason,
      deleteAfterMove: deleteAfterMove ?? this.deleteAfterMove,
      asyncExecution: asyncExecution ?? this.asyncExecution,
      scheduledTime: scheduledTime ?? this.scheduledTime,
    );
  }
}
