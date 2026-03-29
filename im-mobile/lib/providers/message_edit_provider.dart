/**
 * 消息编辑 Provider
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */

import 'package:flutter/foundation.dart';
import '../models/message_edit_model.dart';
import '../services/message_edit_service.dart';

/// 消息编辑状态 Provider
class MessageEditProvider extends ChangeNotifier {
  final MessageEditService _editService;

  MessageEditProvider(this._editService);

  // 状态
  EditState _state = const EditState();
  final Map<int, MessageEditHistory> _editHistories = {};
  final Map<int, int> _editCounts = {};
  final Map<int, CanEditResult> _canEditResults = {};
  bool _isLoadingHistory = false;
  bool _isCheckingPermission = false;

  // Getters
  EditState get state => _state;
  bool get isEditing => _state.editingMessageId != null;
  int? get editingMessageId => _state.editingMessageId;
  String get editedContent => _state.editedContent;
  String get editReason => _state.editReason;
  EditType get editType => _state.editType;
  bool get isSubmitting => _state.isSubmitting;
  String? get error => _state.error;
  bool get isLoadingHistory => _isLoadingHistory;
  bool get isCheckingPermission => _isCheckingPermission;

  /// 开始编辑
  void startEditing(int messageId, String originalContent) {
    _state = EditState(
      editingMessageId: messageId,
      originalContent: originalContent,
      editedContent: originalContent,
    );
    notifyListeners();
  }

  /// 更新编辑内容
  void updateEditedContent(String content) {
    _state = _state.copyWith(editedContent: content, error: null);
    notifyListeners();
  }

  /// 更新编辑原因
  void updateEditReason(String reason) {
    _state = _state.copyWith(editReason: reason);
    notifyListeners();
  }

  /// 更新编辑类型
  void updateEditType(EditType type) {
    _state = _state.copyWith(editType: type);
    notifyListeners();
  }

  /// 取消编辑
  void cancelEditing() {
    _state = const EditState();
    notifyListeners();
  }

  /// 提交编辑
  Future<bool> submitEdit() async {
    if (_state.editingMessageId == null) return false;

    _state = _state.copyWith(isSubmitting: true, error: null);
    notifyListeners();

    try {
      final request = EditMessageRequest(
        messageId: _state.editingMessageId!,
        originalContent: _state.originalContent,
        editedContent: _state.editedContent.trim(),
        editReason: _state.editReason.trim().isEmpty ? null : _state.editReason.trim(),
        editType: _state.editType,
      );

      final result = await _editService.editMessage(request);

      // 更新编辑次数
      _editCounts[result.messageId] = result.editSequence;

      _state = const EditState();
      notifyListeners();
      return true;
    } catch (e) {
      _state = _state.copyWith(
        isSubmitting: false,
        error: e.toString(),
      );
      notifyListeners();
      return false;
    }
  }

  /// 获取编辑历史
  Future<MessageEditHistory?> fetchEditHistory(int messageId) async {
    _isLoadingHistory = true;
    notifyListeners();

    try {
      final history = await _editService.getEditHistory(messageId);
      _editHistories[messageId] = history;
      _isLoadingHistory = false;
      notifyListeners();
      return history;
    } catch (e) {
      _isLoadingHistory = false;
      notifyListeners();
      return null;
    }
  }

  /// 检查是否可以编辑
  Future<CanEditResult?> checkCanEdit(int messageId) async {
    _isCheckingPermission = true;
    notifyListeners();

    try {
      final result = await _editService.canEditMessage(messageId);
      _canEditResults[messageId] = result;
      _isCheckingPermission = false;
      notifyListeners();
      return result;
    } catch (e) {
      _isCheckingPermission = false;
      notifyListeners();
      return null;
    }
  }

  /// 批量获取编辑次数
  Future<void> fetchEditCounts(List<int> messageIds) async {
    if (messageIds.isEmpty) return;

    try {
      final counts = await _editService.preloadEditCounts(messageIds);
      _editCounts.addAll(counts);
      notifyListeners();
    } catch (e) {
      debugPrint('Failed to fetch edit counts: $e');
    }
  }

  /// 回滚到指定版本
  Future<bool> revertToVersion(int messageId, int sequence) async {
    try {
      final result = await _editService.revertToVersion(messageId, sequence);
      
      // 更新编辑次数
      _editCounts[messageId] = result.editSequence;
      // 清除历史缓存
      _editHistories.remove(messageId);
      
      notifyListeners();
      return true;
    } catch (e) {
      debugPrint('Failed to revert: $e');
      return false;
    }
  }

  /// 获取编辑历史
  MessageEditHistory? getEditHistory(int messageId) {
    return _editHistories[messageId];
  }

  /// 获取编辑次数
  int getEditCount(int messageId) {
    return _editCounts[messageId] ?? 0;
  }

  /// 获取可编辑结果
  CanEditResult? getCanEditResult(int messageId) {
    return _canEditResults[messageId];
  }

  /// 是否可以编辑
  bool? canEdit(int messageId) {
    return _canEditResults[messageId]?.canEdit;
  }

  /// 重置
  void reset() {
    _state = const EditState();
    _editHistories.clear();
    _editCounts.clear();
    _canEditResults.clear();
    _isLoadingHistory = false;
    _isCheckingPermission = false;
    notifyListeners();
  }

  /// 清除错误
  void clearError() {
    _state = _state.copyWith(error: null);
    notifyListeners();
  }
}

/// 编辑历史展示 Provider
class EditHistoryProvider extends ChangeNotifier {
  final MessageEditService _editService;

  EditHistoryProvider(this._editService);

  MessageEditHistory? _history;
  bool _isLoading = false;
  String? _error;
  int _currentPage = 0;
  final int _pageSize = 10;

  MessageEditHistory? get history => _history;
  bool get isLoading => _isLoading;
  String? get error => _error;
  List<EditHistoryItem> get items => _history?.editHistory ?? [];

  /// 加载编辑历史
  Future<void> loadHistory(int messageId) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _history = await _editService.getEditHistory(messageId);
      _currentPage = 0;
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 加载更多
  Future<void> loadMore(int messageId) async {
    if (_isLoading) return;

    _isLoading = true;
    notifyListeners();

    try {
      final result = await _editService.getEditHistoryPage(
        messageId,
        page: _currentPage + 1,
        size: _pageSize,
      );

      if (result.items.isNotEmpty) {
        _currentPage++;
        // 这里简化处理，实际应该合并列表
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 刷新
  Future<void> refresh(int messageId) async {
    _currentPage = 0;
    await loadHistory(messageId);
  }

  /// 清除
  void clear() {
    _history = null;
    _error = null;
    _currentPage = 0;
    notifyListeners();
  }
}
