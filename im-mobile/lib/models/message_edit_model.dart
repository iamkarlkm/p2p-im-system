/**
 * 消息编辑系统 - 数据模型
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */

import 'package:freezed_annotation/freezed_annotation.dart';

part 'message_edit_model.freezed.dart';
part 'message_edit_model.g.dart';

/// 编辑类型枚举
enum EditType {
  @JsonValue('NORMAL')
  normal,
  @JsonValue('CORRECTION')
  correction,
  @JsonValue('FORMATTING')
  formatting,
  @JsonValue('CONTENT_UPDATE')
  contentUpdate,
  @JsonValue('REVERT')
  revert,
  @JsonValue('SYSTEM')
  system,
}

/// 差异段类型枚举
enum DiffType {
  @JsonValue('ADDED')
  added,
  @JsonValue('REMOVED')
  removed,
  @JsonValue('UNCHANGED')
  unchanged,
}

/// 编辑记录模型
@freezed
class MessageEdit with _$MessageEdit {
  const factory MessageEdit({
    required int id,
    required int messageId,
    required int userId,
    String? userNickname,
    String? userAvatar,
    required String originalContent,
    required String editedContent,
    String? editReason,
    required int editSequence,
    @Default(EditType.normal) EditType editType,
    required DateTime editedAt,
    bool? canEditFurther,
    int? remainingEditCount,
    int? editTimeLimitMinutes,
    @Default(true) bool? showEditMark,
    String? editMarkText,
    ContentChangeStats? contentChangeStats,
  }) = _MessageEdit;

  factory MessageEdit.fromJson(Map<String, dynamic> json) =>
      _$MessageEditFromJson(json);
}

/// 内容变化统计
@freezed
class ContentChangeStats with _$ContentChangeStats {
  const factory ContentChangeStats({
    required int originalLength,
    required int editedLength,
    required int changeLength,
    required double changePercentage,
    required bool increased,
    required bool decreased,
  }) = _ContentChangeStats;

  factory ContentChangeStats.fromJson(Map<String, dynamic> json) =>
      _$ContentChangeStatsFromJson(json);
}

/// 差异段模型
@freezed
class DiffSegment with _$DiffSegment {
  const factory DiffSegment({
    required DiffType type,
    required String content,
    required int startIndex,
    required int endIndex,
  }) = _DiffSegment;

  factory DiffSegment.fromJson(Map<String, dynamic> json) =>
      _$DiffSegmentFromJson(json);
}

/// 内容差异模型
@freezed
class ContentDiff with _$ContentDiff {
  const factory ContentDiff({
    required List<DiffSegment> segments,
    required int addedCount,
    required int removedCount,
    required int unchangedCount,
  }) = _ContentDiff;

  factory ContentDiff.fromJson(Map<String, dynamic> json) =>
      _$ContentDiffFromJson(json);
}

/// 用户摘要模型
@freezed
class UserSummary with _$UserSummary {
  const factory UserSummary({
    required int id,
    required String nickname,
    String? avatar,
  }) = _UserSummary;

  factory UserSummary.fromJson(Map<String, dynamic> json) =>
      _$UserSummaryFromJson(json);
}

/// 编辑历史项
@freezed
class EditHistoryItem with _$EditHistoryItem {
  const factory EditHistoryItem({
    required int editId,
    required int sequence,
    required String beforeContent,
    required String afterContent,
    String? editReason,
    required String editType,
    required DateTime editedAt,
    required UserSummary editedBy,
    int? editTimeMillis,
    ContentDiff? contentDiff,
  }) = _EditHistoryItem;

  factory EditHistoryItem.fromJson(Map<String, dynamic> json) =>
      _$EditHistoryItemFromJson(json);
}

/// 编辑统计
@freezed
class EditStatistics with _$EditStatistics {
  const factory EditStatistics({
    required int totalEdits,
    required int editsByOwner,
    required int editsByAdmin,
    required double averageEditIntervalMinutes,
    required int totalContentAdded,
    required int totalContentRemoved,
    String? mostActiveEditHour,
    List<String>? commonEditReasons,
  }) = _EditStatistics;

  factory EditStatistics.fromJson(Map<String, dynamic> json) =>
      _$EditStatisticsFromJson(json);
}

/// 编辑历史DTO
@freezed
class MessageEditHistory with _$MessageEditHistory {
  const factory MessageEditHistory({
    required int messageId,
    required String currentContent,
    required String originalContent,
    required int totalEditCount,
    required DateTime lastEditedAt,
    UserSummary? lastEditedBy,
    required bool canEdit,
    String? cannotEditReason,
    required List<EditHistoryItem> editHistory,
    required int editTimeWindowMinutes,
    required int maxEditCount,
    EditStatistics? statistics,
  }) = _MessageEditHistory;

  factory MessageEditHistory.fromJson(Map<String, dynamic> json) =>
      _$MessageEditHistoryFromJson(json);
}

/// 编辑请求
@freezed
class EditMessageRequest with _$EditMessageRequest {
  const factory EditMessageRequest({
    required int messageId,
    required String originalContent,
    required String editedContent,
    String? editReason,
    @Default(EditType.normal) EditType editType,
  }) = _EditMessageRequest;

  factory EditMessageRequest.fromJson(Map<String, dynamic> json) =>
      _$EditMessageRequestFromJson(json);
}

/// 可编辑检查结果
@freezed
class CanEditResult with _$CanEditResult {
  const factory CanEditResult({
    required bool canEdit,
    required String reason,
    int? remainingEditCount,
    int? editTimeLimitMinutes,
  }) = _CanEditResult;

  factory CanEditResult.fromJson(Map<String, dynamic> json) =>
      _$CanEditResultFromJson(json);
}

/// 编辑状态
@freezed
class EditState with _$EditState {
  const factory EditState({
    int? editingMessageId,
    @Default('') String originalContent,
    @Default('') String editedContent,
    @Default('') String editReason,
    @Default(EditType.normal) EditType editType,
    @Default(false) bool isSubmitting,
    String? error,
  }) = _EditState;
}

/// 编辑类型扩展
extension EditTypeExtension on EditType {
  String get label {
    switch (this) {
      case EditType.normal:
        return '普通编辑';
      case EditType.correction:
        return '纠错';
      case EditType.formatting:
        return '格式调整';
      case EditType.contentUpdate:
        return '内容更新';
      case EditType.revert:
        return '版本回滚';
      case EditType.system:
        return '系统编辑';
    }
  }

  String get color {
    switch (this) {
      case EditType.normal:
        return 'blue';
      case EditType.correction:
        return 'orange';
      case EditType.formatting:
        return 'purple';
      case EditType.contentUpdate:
        return 'green';
      case EditType.revert:
        return 'red';
      case EditType.system:
        return 'grey';
    }
  }
}

/// 差异类型扩展
extension DiffTypeExtension on DiffType {
  String get prefix {
    switch (this) {
      case DiffType.added:
        return '+';
      case DiffType.removed:
        return '-';
      case DiffType.unchanged:
        return ' ';
    }
  }

  int get colorValue {
    switch (this) {
      case DiffType.added:
        return 0xFF4CAF50; // Green
      case DiffType.removed:
        return 0xFFE57373; // Red
      case DiffType.unchanged:
        return 0xFF757575; // Grey
    }
  }
}
