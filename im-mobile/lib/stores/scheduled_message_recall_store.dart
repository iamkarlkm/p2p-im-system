import 'package:mobx/mobx.dart';
import '../models/scheduled_message_recall_model.dart';
import '../api/scheduled_message_recall_api.dart';

part 'scheduled_message_recall_store.g.dart';

class ScheduledMessageRecallStore = _ScheduledMessageRecallStoreBase
    with _$ScheduledMessageRecallStore;

/// 消息定时撤回状态管理Store
abstract class _ScheduledMessageRecallStoreBase with Store {
  final ScheduledMessageRecallApi _api = ScheduledMessageRecallApi();

  // ==================== Observable State ====================

  /// 用户的所有定时撤回任务
  @observable
  ObservableList<ScheduledMessageRecallModel> recalls = ObservableList<ScheduledMessageRecallModel>();

  /// 待执行的任务
  @observable
  ObservableList<ScheduledMessageRecallModel> pendingRecalls = ObservableList<ScheduledMessageRecallModel>();

  /// 当前选中的任务
  @observable
  ScheduledMessageRecallModel? selectedRecall;

  /// 加载状态
  @observable
  bool isLoading = false;

  /// 操作状态
  @observable
  bool isProcessing = false;

  /// 错误信息
  @observable
  String? errorMessage;

  /// 待执行数量
  @observable
  int pendingCount = 0;

  /// 总数量
  @observable
  int totalCount = 0;

  /// 推荐的时间选项
  @observable
  List<int> timeOptions = [];

  // ==================== Computed ====================

  /// 是否有待执行的任务
  @computed
  bool get hasPendingRecalls => pendingRecalls.isNotEmpty;

  /// 即将撤回的任务（剩余时间少于60秒）
  @computed
  List<ScheduledMessageRecallModel> get urgentRecalls {
    return pendingRecalls
        .where((r) => r.remainingTimeSeconds > 0 && r.remainingTimeSeconds <= 60)
        .toList();
  }

  /// 按状态分组的任务
  @computed
  Map<String, List<ScheduledMessageRecallModel>> get recallsByStatus {
    final groups = <String, List<ScheduledMessageRecallModel>>{};
    for (final recall in recalls) {
      final status = recall.status?.name ?? 'unknown';
      groups.putIfAbsent(status, () => []);
      groups[status]!.add(recall);
    }
    return groups;
  }

  // ==================== Actions ====================

  /// 加载用户的所有定时撤回任务
  @action
  Future<void> loadUserRecalls() async {
    isLoading = true;
    errorMessage = null;
    
    try {
      final result = await _api.getUserRecalls();
      if (result.success && result.data != null) {
        recalls = ObservableList.of(result.data!);
        _updatePendingRecalls();
      } else {
        errorMessage = result.message ?? '加载失败';
      }
    } catch (e) {
      errorMessage = '加载定时撤回任务失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 加载待执行的任务
  @action
  Future<void> loadPendingRecalls() async {
    try {
      final result = await _api.getPendingRecalls();
      if (result.success && result.data != null) {
        pendingRecalls = ObservableList.of(result.data!);
      }
    } catch (e) {
      errorMessage = '加载待执行任务失败: $e';
    }
  }

  /// 加载统计数据
  @action
  Future<void> loadStats() async {
    try {
      final result = await _api.getRecallStats();
      if (result.success && result.data != null) {
        pendingCount = result.data!['pendingCount'] ?? 0;
        totalCount = result.data!['totalCount'] ?? 0;
      }
    } catch (e) {
      // 静默处理统计加载失败
    }
  }

  /// 创建定时撤回任务
  @action
  Future<bool> createScheduledRecall({
    required int messageId,
    required int conversationId,
    required ConversationType conversationType,
    String? messageContent,
    required int scheduledSeconds,
    String? recallReason,
    bool notifyReceivers = true,
    String? customNotifyMessage,
    bool isCancelable = true,
  }) async {
    isProcessing = true;
    errorMessage = null;
    
    try {
      final result = await _api.createScheduledRecall(
        messageId: messageId,
        conversationId: conversationId,
        conversationType: conversationType,
        messageContent: messageContent,
        scheduledSeconds: scheduledSeconds,
        recallReason: recallReason,
        notifyReceivers: notifyReceivers,
        customNotifyMessage: customNotifyMessage,
        isCancelable: isCancelable,
      );
      
      if (result.success && result.data != null) {
        recalls.insert(0, result.data!);
        if (result.data!.status == RecallStatus.pending) {
          pendingRecalls.add(result.data!);
        }
        pendingCount++;
        totalCount++;
        return true;
      } else {
        errorMessage = result.message ?? '创建失败';
        return false;
      }
    } catch (e) {
      errorMessage = '创建定时撤回任务失败: $e';
      return false;
    } finally {
      isProcessing = false;
    }
  }

  /// 取消定时撤回任务
  @action
  Future<bool> cancelRecall(int id) async {
    isProcessing = true;
    errorMessage = null;
    
    try {
      final result = await _api.cancelScheduledRecall(id);
      
      if (result.success && result.data != null) {
        // 更新本地数据
        final index = recalls.indexWhere((r) => r.id == id);
        if (index != -1) {
          recalls[index] = result.data!;
        }
        _updatePendingRecalls();
        pendingCount = pendingCount > 0 ? pendingCount - 1 : 0;
        return true;
      } else {
        errorMessage = result.message ?? '取消失败';
        return false;
      }
    } catch (e) {
      errorMessage = '取消定时撤回任务失败: $e';
      return false;
    } finally {
      isProcessing = false;
    }
  }

  /// 更新定时撤回时间
  @action
  Future<bool> updateScheduledTime(int id, int newSeconds) async {
    isProcessing = true;
    errorMessage = null;
    
    try {
      final result = await _api.updateScheduledTime(id, newSeconds);
      
      if (result.success && result.data != null) {
        final index = recalls.indexWhere((r) => r.id == id);
        if (index != -1) {
          recalls[index] = result.data!;
        }
        _updatePendingRecalls();
        return true;
      } else {
        errorMessage = result.message ?? '更新时间失败';
        return false;
      }
    } catch (e) {
      errorMessage = '更新定时撤回时间失败: $e';
      return false;
    } finally {
      isProcessing = false;
    }
  }

  /// 删除定时撤回任务
  @action
  Future<bool> deleteRecall(int id) async {
    isProcessing = true;
    errorMessage = null;
    
    try {
      final result = await _api.deleteScheduledRecall(id);
      
      if (result.success) {
        recalls.removeWhere((r) => r.id == id);
        _updatePendingRecalls();
        totalCount--;
        return true;
      } else {
        errorMessage = result.message ?? '删除失败';
        return false;
      }
    } catch (e) {
      errorMessage = '删除定时撤回任务失败: $e';
      return false;
    } finally {
      isProcessing = false;
    }
  }

  /// 检查消息是否已设置定时撤回
  @action
  Future<bool> checkMessageScheduled(int messageId) async {
    try {
      final result = await _api.checkMessageScheduled(messageId);
      return result.data?['isScheduled'] ?? false;
    } catch (e) {
      return false;
    }
  }

  /// 加载推荐的时间选项
  @action
  Future<void> loadTimeOptions() async {
    try {
      final result = await _api.getTimeOptions();
      if (result.success && result.data != null) {
        timeOptions = result.data!;
      }
    } catch (e) {
      // 使用默认选项
      timeOptions = ScheduledMessageRecallModel.recommendedTimeOptions;
    }
  }

  /// 选择任务
  @action
  void selectRecall(ScheduledMessageRecallModel? recall) {
    selectedRecall = recall;
  }

  /// 清除错误信息
  @action
  void clearError() {
    errorMessage = null;
  }

  /// 刷新所有数据
  @action
  Future<void> refreshAll() async {
    await Future.wait([
      loadUserRecalls(),
      loadPendingRecalls(),
      loadStats(),
      loadTimeOptions(),
    ]);
  }

  /// 更新待执行任务列表
  void _updatePendingRecalls() {
    pendingRecalls = ObservableList.of(
      recalls.where((r) => r.status == RecallStatus.pending),
    );
  }

  /// 快速创建定时撤回（使用默认设置）
  @action
  Future<bool> quickScheduleRecall({
    required int messageId,
    required int conversationId,
    required ConversationType conversationType,
    String? messageContent,
    int scheduledSeconds = 60,
  }) async {
    return createScheduledRecall(
      messageId: messageId,
      conversationId: conversationId,
      conversationType: conversationType,
      messageContent: messageContent,
      scheduledSeconds: scheduledSeconds,
      notifyReceivers: true,
      isCancelable: true,
    );
  }

  /// 取消所有待执行的任务
  @action
  Future<int> cancelAllPending() async {
    int cancelledCount = 0;
    final toCancel = List<ScheduledMessageRecallModel>.from(pendingRecalls);
    
    for (final recall in toCancel) {
      if (recall.id != null && await cancelRecall(recall.id!)) {
        cancelledCount++;
      }
    }
    
    return cancelledCount;
  }

  /// 获取任务详情
  @action
  Future<ScheduledMessageRecallModel?> getRecallDetail(int id) async {
    try {
      final result = await _api.getRecallDetail(id);
      if (result.success && result.data != null) {
        selectedRecall = result.data;
        return result.data;
      }
    } catch (e) {
      errorMessage = '获取任务详情失败: $e';
    }
    return null;
  }
}

/// API响应包装类
class ApiResult<T> {
  final bool success;
  final String? message;
  final T? data;

  ApiResult({required this.success, this.message, this.data});
}
