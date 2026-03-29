import 'package:im_mobile/models/scheduled_message_model.dart';
import 'package:im_mobile/services/api_service.dart';
import 'package:mobx/mobx.dart';

part 'scheduled_message_store.g.dart';

class ScheduledMessageStore = _ScheduledMessageStore with _$ScheduledMessageStore;

abstract class _ScheduledMessageStore with Store {
  final ApiService _apiService = ApiService();

  @observable
  ObservableList<ScheduledMessageModel> messages = ObservableList<ScheduledMessageModel>();

  @observable
  bool loading = false;

  @observable
  String? error;

  @observable
  int totalCount = 0;

  @observable
  int pendingCount = 0;

  @observable
  int page = 0;

  @observable
  int pageSize = 20;

  @observable
  ScheduledMessageStatus? filterStatus;

  @action
  Future<void> fetchMessages({bool reset = false}) async {
    if (reset) {
      page = 0;
      messages.clear();
    }

    loading = true;
    error = null;

    try {
      final params = <String, dynamic>{
        'page': page,
        'size': pageSize,
      };

      if (filterStatus != null) {
        params['status'] = filterStatus!.name;
      }

      final response = await _apiService.get('/scheduled-messages', queryParameters: params);
      
      final List<dynamic> data = response.data['data'];
      final newMessages = data.map((json) => ScheduledMessageModel.fromJson(json)).toList();

      if (reset) {
        messages = ObservableList.of(newMessages);
      } else {
        messages.addAll(newMessages);
      }
      
      totalCount = response.data['totalElements'];
      loading = false;
    } catch (e) {
      error = '获取定时消息失败: $e';
      loading = false;
    }
  }

  @action
  Future<bool> createMessage({
    required int receiverId,
    required String content,
    required DateTime scheduledTime,
  }) async {
    loading = true;
    error = null;

    try {
      final response = await _apiService.post('/scheduled-messages', data: {
        'receiverId': receiverId,
        'content': content,
        'scheduledTime': scheduledTime.toIso8601String(),
      });

      final newMessage = ScheduledMessageModel.fromJson(response.data['data']);
      messages.insert(0, newMessage);
      totalCount++;
      loading = false;
      return true;
    } catch (e) {
      error = '创建定时消息失败: $e';
      loading = false;
      return false;
    }
  }

  @action
  Future<bool> cancelMessage(int messageId) async {
    try {
      final response = await _apiService.post('/scheduled-messages/$messageId/cancel');
      final updatedMessage = ScheduledMessageModel.fromJson(response.data['data']);
      
      final index = messages.indexWhere((m) => m.id == messageId);
      if (index != -1) {
        messages[index] = updatedMessage;
      }
      
      return true;
    } catch (e) {
      error = '取消定时消息失败: $e';
      return false;
    }
  }

  @action
  Future<bool> deleteMessage(int messageId) async {
    try {
      await _apiService.delete('/scheduled-messages/$messageId');
      messages.removeWhere((m) => m.id == messageId);
      totalCount--;
      return true;
    } catch (e) {
      error = '删除定时消息失败: $e';
      return false;
    }
  }

  @action
  Future<void> fetchStats() async {
    try {
      final response = await _apiService.get('/scheduled-messages/stats');
      pendingCount = response.data['data']['pendingCount'];
    } catch (e) {
      print('获取统计信息失败: $e');
    }
  }

  @action
  void setFilterStatus(ScheduledMessageStatus? status) {
    filterStatus = status;
    fetchMessages(reset: true);
  }

  @action
  void loadMore() {
    if (messages.length < totalCount && !loading) {
      page++;
      fetchMessages();
    }
  }

  @action
  void clearError() {
    error = null;
  }
}
