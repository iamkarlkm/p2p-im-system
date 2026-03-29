import 'package:mobx/mobx.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/message_reaction_model.dart';
import '../services/message_reaction_service.dart';

part 'message_reaction_store.g.dart';

/// 消息表情回应状态管理
class MessageReactionStore = _MessageReactionStore with _$MessageReactionStore;

abstract class _MessageReactionStore with Store {
  final MessageReactionService _service = MessageReactionService();

  @observable
  ObservableMap<int, ReactionSummary> reactionSummaries = ObservableMap<int, ReactionSummary>();

  @observable
  ObservableMap<int, List<MessageReaction>> messageReactions = ObservableMap<int, List<MessageReaction>>();

  @observable
  ObservableList<String> frequentlyUsedEmojis = ObservableList<String>();

  @observable
  int currentUserId = 0;

  @observable
  bool isLoading = false;

  @observable
  String? error;

  _MessageReactionStore() {
    _loadFrequentlyUsedEmojis();
  }

  /// 设置当前用户ID
  @action
  void setCurrentUserId(int userId) {
    currentUserId = userId;
  }

  /// 加载消息的表情回应汇总
  @action
  Future<void> loadReactionSummary(int messageId, int conversationId) async {
    if (currentUserId == 0) return;

    isLoading = true;
    error = null;

    try {
      final summary = await _service.getReactionSummary(messageId, currentUserId);
      reactionSummaries[messageId] = summary;
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  /// 批量加载消息的表情回应汇总
  @action
  Future<void> loadReactionSummaries(List<int> messageIds) async {
    if (currentUserId == 0 || messageIds.isEmpty) return;

    try {
      final summaries = await _service.getReactionSummaries(messageIds, currentUserId);
      for (final summary in summaries) {
        reactionSummaries[summary.messageId] = summary;
      }
    } catch (e) {
      print('Failed to load reaction summaries: $e');
    }
  }

  /// 添加表情回应
  @action
  Future<void> addReaction(int messageId, int conversationId, String emojiCode) async {
    if (currentUserId == 0) return;

    error = null;

    try {
      final request = AddReactionRequest(
        messageId: messageId,
        userId: currentUserId,
        conversationId: conversationId,
        emojiCode: emojiCode,
      );

      await _service.addReaction(request);
      _recordEmojiUsage(emojiCode);
      
      // 刷新汇总
      await loadReactionSummary(messageId, conversationId);
    } catch (e) {
      error = e.toString();
      throw e;
    }
  }

  /// 切换表情回应
  @action
  Future<void> toggleReaction(int messageId, int conversationId, String emojiCode) async {
    if (currentUserId == 0) return;

    error = null;

    try {
      final request = AddReactionRequest(
        messageId: messageId,
        userId: currentUserId,
        conversationId: conversationId,
        emojiCode: emojiCode,
      );

      final result = await _service.toggleReaction(request);
      if (result != null) {
        _recordEmojiUsage(emojiCode);
      }
      
      // 刷新汇总
      await loadReactionSummary(messageId, conversationId);
    } catch (e) {
      error = e.toString();
      throw e;
    }
  }

  /// 移除表情回应
  @action
  Future<void> removeReaction(int messageId, String emojiCode) async {
    if (currentUserId == 0) return;

    try {
      await _service.removeReaction(messageId, currentUserId, emojiCode);
    } catch (e) {
      error = e.toString();
      throw e;
    }
  }

  /// 移除所有回应
  @action
  Future<void> removeAllReactions(int messageId) async {
    if (currentUserId == 0) return;

    try {
      await _service.removeAllReactions(messageId, currentUserId);
      reactionSummaries.remove(messageId);
    } catch (e) {
      error = e.toString();
      throw e;
    }
  }

  /// 获取消息的回应汇总
  @computed
  ReactionSummary? getReactionSummary(int messageId) {
    return reactionSummaries[messageId];
  }

  /// 检查用户是否回应了消息
  @computed
  bool hasUserReacted(int messageId) {
    return reactionSummaries[messageId]?.hasCurrentUserReacted ?? false;
  }

  /// 获取用户的表情回应
  String? getCurrentUserReaction(int messageId) {
    return reactionSummaries[messageId]?.currentUserEmoji;
  }

  /// 获取常用表情
  List<String> getFrequentlyUsedEmojis() {
    if (frequentlyUsedEmojis.isNotEmpty) {
      return frequentlyUsedEmojis.toList();
    }
    return commonEmojis.take(8).toList();
  }

  /// 记录表情使用
  void _recordEmojiUsage(String emojiCode) {
    // 去重并移到开头
    frequentlyUsedEmojis.remove(emojiCode);
    frequentlyUsedEmojis.insert(0, emojiCode);
    
    // 限制数量
    if (frequentlyUsedEmojis.length > 16) {
      frequentlyUsedEmojis.removeLast();
    }

    _saveFrequentlyUsedEmojis();
  }

  /// 加载常用表情
  Future<void> _loadFrequentlyUsedEmojis() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final stored = prefs.getStringList('im_frequent_emojis') ?? [];
      frequentlyUsedEmojis = ObservableList.of(stored);
    } catch (e) {
      frequentlyUsedEmojis = ObservableList<String>();
    }
  }

  /// 保存常用表情
  Future<void> _saveFrequentlyUsedEmojis() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setStringList('im_frequent_emojis', frequentlyUsedEmojis.toList());
    } catch (e) {
      // Ignore storage errors
    }
  }

  /// 清空消息状态
  @action
  void clearMessageState(int messageId) {
    reactionSummaries.remove(messageId);
    messageReactions.remove(messageId);
  }

  /// 清空所有状态
  @action
  void clearAllStates() {
    reactionSummaries.clear();
    messageReactions.clear();
    error = null;
  }

  void dispose() {
    _service.dispose();
  }
}
