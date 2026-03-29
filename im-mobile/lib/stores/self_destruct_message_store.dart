import 'dart:async';
import 'package:mobx/mobx.dart';
import '../api/self_destruct_message_api.dart';
import '../models/self_destruct_message_model.dart';

part 'self_destruct_message_store.g.dart';

/// 阅后即焚消息状态管理
/// 
/// @author IM Development Team
/// @since 1.0.0
class SelfDestructMessageStore = _SelfDestructMessageStore with _$SelfDestructMessageStore;

abstract class _SelfDestructMessageStore with Store {
  final SelfDestructMessageApi _api = SelfDestructMessageApi();

  // ========== 可观察状态 ==========

  @observable
  ObservableList<SelfDestructMessageModel> messages = ObservableList<SelfDestructMessageModel>();

  @observable
  ObservableList<SelfDestructMessageModel> sentMessages = ObservableList<SelfDestructMessageModel>();

  @observable
  ObservableList<SelfDestructMessageModel> receivedMessages = ObservableList<SelfDestructMessageModel>();

  @observable
  ObservableList<SelfDestructMessageModel> screenshotDetectedMessages = ObservableList<SelfDestructMessageModel>();

  @observable
  SelfDestructMessageModel? currentMessage;

  @observable
  SelfDestructConfig? config;

  @observable
  int unreadCount = 0;

  @observable
  bool isLoading = false;

  @observable
  String? errorMessage;

  @observable
  bool isReading = false;

  @observable
  int? countdownSeconds;

  // ========== 倒计时器 ==========

  Timer? _countdownTimer;

  // ========== 计算属性 ==========

  @computed
  List<SelfDestructMessageModel> get activeMessages => messages.where((m) => !m.isDestroyed).toList();

  @computed
  List<SelfDestructMessageModel> get destroyedMessages => messages.where((m) => m.isDestroyed).toList();

  @computed
  bool get hasUnread => unreadCount > 0;

  @computed
  bool get hasScreenshotDetected => screenshotDetectedMessages.isNotEmpty;

  // ========== 操作方法 ==========

  /// 获取配置
  @action
  Future<void> fetchConfig() async {
    try {
      isLoading = true;
      errorMessage = null;
      config = await _api.getConfig();
    } catch (e) {
      errorMessage = '获取配置失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 获取会话消息
  @action
  Future<void> fetchMessages(String conversationId, {int page = 0, int size = 20}) async {
    try {
      isLoading = true;
      errorMessage = null;
      final result = await _api.getMessagesByConversation(conversationId, page: page, size: size);
      if (page == 0) {
        messages.clear();
      }
      messages.addAll(result);
    } catch (e) {
      errorMessage = '获取消息失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 获取发送的消息
  @action
  Future<void> fetchSentMessages() async {
    try {
      isLoading = true;
      errorMessage = null;
      final result = await _api.getSentMessages();
      sentMessages.clear();
      sentMessages.addAll(result);
    } catch (e) {
      errorMessage = '获取发送消息失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 获取接收的消息
  @action
  Future<void> fetchReceivedMessages() async {
    try {
      isLoading = true;
      errorMessage = null;
      final result = await _api.getReceivedMessages();
      receivedMessages.clear();
      receivedMessages.addAll(result);
    } catch (e) {
      errorMessage = '获取接收消息失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 获取未读数量
  @action
  Future<void> fetchUnreadCount() async {
    try {
      final count = await _api.getUnreadCount();
      unreadCount = count;
    } catch (e) {
      errorMessage = '获取未读数量失败: $e';
    }
  }

  /// 获取会话未读数量
  @action
  Future<int> fetchUnreadCountByConversation(String conversationId) async {
    try {
      return await _api.getUnreadCountByConversation(conversationId);
    } catch (e) {
      errorMessage = '获取未读数量失败: $e';
      return 0;
    }
  }

  /// 创建消息
  @action
  Future<SelfDestructMessageModel?> createMessage(SelfDestructCreateRequest request) async {
    try {
      isLoading = true;
      errorMessage = null;
      final message = await _api.createMessage(request);
      messages.insert(0, message);
      sentMessages.insert(0, message);
      return message;
    } catch (e) {
      errorMessage = '创建消息失败: $e';
      return null;
    } finally {
      isLoading = false;
    }
  }

  /// 阅读消息
  @action
  Future<SelfDestructReadResponse?> readMessage(String messageId) async {
    try {
      isReading = true;
      errorMessage = null;
      final response = await _api.readMessage(messageId);
      
      // 更新消息状态
      final index = messages.indexWhere((m) => m.id == messageId);
      if (index != -1) {
        messages[index].markAsRead();
        messages[index].updateRemainingSeconds(response.remainingSeconds);
      }

      // 开始倒计时
      countdownSeconds = response.remainingSeconds;
      _startCountdown(messageId);

      return response;
    } catch (e) {
      errorMessage = '阅读消息失败: $e';
      return null;
    } finally {
      isReading = false;
    }
  }

  /// 开始倒计时
  void _startCountdown(String messageId) {
    _countdownTimer?.cancel();
    _countdownTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (countdownSeconds == null || countdownSeconds! <= 0) {
        timer.cancel();
        _destroyMessage(messageId);
      } else {
        countdownSeconds = countdownSeconds! - 1;
        final index = messages.indexWhere((m) => m.id == messageId);
        if (index != -1) {
          messages[index].updateRemainingSeconds(countdownSeconds!);
        }
      }
    });
  }

  /// 销毁消息
  void _destroyMessage(String messageId) {
    final index = messages.indexWhere((m) => m.id == messageId);
    if (index != -1) {
      messages[index].markAsDestroyed();
    }
    _api.destroyMessage(messageId).catchError((e) {
      // 忽略错误，可能已经被销毁
    });
  }

  /// 手动销毁消息
  @action
  Future<bool> destroyMessage(String messageId) async {
    try {
      await _api.destroyMessage(messageId);
      final index = messages.indexWhere((m) => m.id == messageId);
      if (index != -1) {
        messages[index].markAsDestroyed();
      }
      _countdownTimer?.cancel();
      return true;
    } catch (e) {
      errorMessage = '销毁消息失败: $e';
      return false;
    }
  }

  /// 删除消息
  @action
  Future<bool> deleteMessage(String messageId) async {
    try {
      await _api.deleteMessage(messageId);
      messages.removeWhere((m) => m.id == messageId);
      sentMessages.removeWhere((m) => m.id == messageId);
      return true;
    } catch (e) {
      errorMessage = '删除消息失败: $e';
      return false;
    }
  }

  /// 检测截图
  @action
  Future<ScreenshotDetectResponse?> detectScreenshot(String messageId) async {
    try {
      final response = await _api.detectScreenshot(messageId);
      final index = messages.indexWhere((m) => m.id == messageId);
      if (index != -1) {
        messages[index].recordScreenshot();
      }
      return response;
    } catch (e) {
      errorMessage = '截图检测失败: $e';
      return null;
    }
  }

  /// 获取被截图的消息列表
  @action
  Future<void> fetchScreenshotDetectedMessages() async {
    try {
      isLoading = true;
      errorMessage = null;
      final result = await _api.getScreenshotDetectedMessages();
      screenshotDetectedMessages.clear();
      screenshotDetectedMessages.addAll(result);
    } catch (e) {
      errorMessage = '获取截图检测消息失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 检查消息是否已销毁
  @action
  Future<bool> isMessageDestroyed(String messageId) async {
    try {
      return await _api.isMessageDestroyed(messageId);
    } catch (e) {
      return true;
    }
  }

  /// 获取剩余秒数
  @action
  Future<int> getRemainingSeconds(String messageId) async {
    try {
      return await _api.getRemainingSeconds(messageId);
    } catch (e) {
      return 0;
    }
  }

  /// 添加新消息（从WebSocket接收）
  @action
  void addNewMessage(SelfDestructMessageModel message) {
    if (!messages.any((m) => m.id == message.id)) {
      messages.insert(0, message);
      receivedMessages.insert(0, message);
      unreadCount++;
    }
  }

  /// 更新消息（从WebSocket接收）
  @action
  void updateMessage(SelfDestructMessageModel message) {
    final index = messages.indexWhere((m) => m.id == message.id);
    if (index != -1) {
      messages[index] = message;
    }
  }

  /// 清除错误
  @action
  void clearError() {
    errorMessage = null;
  }

  /// 清除当前消息
  @action
  void clearCurrentMessage() {
    currentMessage = null;
    countdownSeconds = null;
    _countdownTimer?.cancel();
  }

  /// 重置状态
  @action
  void reset() {
    messages.clear();
    sentMessages.clear();
    receivedMessages.clear();
    screenshotDetectedMessages.clear();
    currentMessage = null;
    config = null;
    unreadCount = 0;
    errorMessage = null;
    countdownSeconds = null;
    _countdownTimer?.cancel();
    _countdownTimer = null;
  }

  /// 销毁
  void dispose() {
    _countdownTimer?.cancel();
  }
}
