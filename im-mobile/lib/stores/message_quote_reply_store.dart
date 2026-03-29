import 'package:mobx/mobx.dart';
import 'package:im_mobile/models/message_quote_reply_model.dart';
import 'package:im_mobile/services/quote_reply_service.dart';

part 'message_quote_reply_store.g.dart';

class MessageQuoteReplyStore = _MessageQuoteReplyStore with _$MessageQuoteReplyStore;

abstract class _MessageQuoteReplyStore with Store {
  final QuoteReplyService _service = QuoteReplyService();

  @observable ObservableList<MessageQuoteReplyModel> quoteReplies = ObservableList<MessageQuoteReplyModel>();

  @observable ObservableList<MessageQuoteReplyModel> conversationQuotes = ObservableList<MessageQuoteReplyModel>();

  @observable MessageQuoteReplyModel? currentQuoteReply;

  @observable MessageQuoteReplyModel? selectedQuoteForReply;

  @observable bool isLoading = false;

  @observable String? errorMessage;

  @observable int currentQuoteLevel = 1;

  @observable ObservableList<QuotedMessageInfo> quoteChainStack = ObservableList<QuotedMessageInfo>();

  @observable bool isBatchQuoteMode = false;

  @observable ObservableList<int> selectedMessageIds = ObservableList<int>();

  @computed
  List<MessageQuoteReplyModel> get activeQuotes =>
      quoteReplies.where((q) => q.isActive).toList();

  @computed
  List<MessageQuoteReplyModel> get nestedQuotes =>
      quoteReplies.where((q) => q.isNested).toList();

  @computed
  List<MessageQuoteReplyModel> get topLevelQuotes =>
      quoteReplies.where((q) => q.quoteLevel == 1).toList();

  @computed
  int get totalQuotes => quoteReplies.length;

  @computed
  bool get hasSelectedQuote => selectedQuoteForReply != null;

  @computed
  bool get hasQuoteChain => quoteChainStack.isNotEmpty;

  @computed
  bool get canSubmitBatchQuote => isBatchQuoteMode && selectedMessageIds.length >= 2;

  /// 创建单条引用回复
  @action
  Future<bool> createQuoteReply({
    required int quotedMessageId,
    required int conversationId,
    required String replyContent,
    bool includeOriginal = true,
    String? highlightKeywords,
  }) async {
    isLoading = true;
    errorMessage = null;
    try {
      final dto = await _service.createQuoteReply(
        quotedMessageId: quotedMessageId,
        conversationId: conversationId,
        replyContent: replyContent,
        parentQuoteId: selectedQuoteForReply?.id,
        includeOriginal: includeOriginal,
        highlightKeywords: highlightKeywords,
      );
      if (dto != null) {
        quoteReplies.insert(0, dto);
        clearSelectedQuote();
        return true;
      }
      return false;
    } catch (e) {
      errorMessage = '创建引用回复失败: $e';
      return false;
    } finally {
      isLoading = false;
    }
  }

  /// 创建批量引用回复
  @action
  Future<bool> createBatchQuoteReply({
    required int conversationId,
    required String replyContent,
    bool includeOriginal = true,
  }) async {
    if (selectedMessageIds.length < 2) {
      errorMessage = '批量引用至少需要选择2条消息';
      return false;
    }

    isLoading = true;
    errorMessage = null;
    try {
      final dto = await _service.createBatchQuoteReply(
        conversationId: conversationId,
        replyContent: replyContent,
        batchQuotedMessageIds: selectedMessageIds.toList(),
        includeOriginal: includeOriginal,
      );
      if (dto != null) {
        quoteReplies.insert(0, dto);
        exitBatchQuoteMode();
        return true;
      }
      return false;
    } catch (e) {
      errorMessage = '创建批量引用回复失败: $e';
      return false;
    } finally {
      isLoading = false;
    }
  }

  /// 获取会话的引用回复列表
  @action
  Future<void> loadConversationQuotes(int conversationId) async {
    isLoading = true;
    errorMessage = null;
    try {
      final list = await _service.getQuoteRepliesByConversation(conversationId);
      conversationQuotes = ObservableList.of(list);
    } catch (e) {
      errorMessage = '加载引用回复列表失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 获取引用树
  @action
  Future<void> loadQuoteTree(int rootQuoteId) async {
    isLoading = true;
    errorMessage = null;
    try {
      final list = await _service.getQuoteTree(rootQuoteId);
      quoteReplies = ObservableList.of(list);
    } catch (e) {
      errorMessage = '加载引用树失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 获取消息的引用回复
  @action
  Future<void> loadQuotesByMessage(int messageId) async {
    isLoading = true;
    errorMessage = null;
    try {
      final list = await _service.getQuotesByMessage(messageId);
      quoteReplies = ObservableList.of(list);
    } catch (e) {
      errorMessage = '加载消息引用回复失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 更新引用回复
  @action
  Future<bool> updateQuoteReply(int id, String newContent) async {
    isLoading = true;
    errorMessage = null;
    try {
      final dto = await _service.updateQuoteReply(id, newContent);
      if (dto != null) {
        final index = quoteReplies.indexWhere((q) => q.id == id);
        if (index >= 0) {
          quoteReplies[index] = dto;
        }
        if (currentQuoteReply?.id == id) {
          currentQuoteReply = dto;
        }
        return true;
      }
      return false;
    } catch (e) {
      errorMessage = '更新引用回复失败: $e';
      return false;
    } finally {
      isLoading = false;
    }
  }

  /// 删除引用回复
  @action
  Future<bool> deleteQuoteReply(int id) async {
    isLoading = true;
    errorMessage = null;
    try {
      final success = await _service.deleteQuoteReply(id);
      if (success) {
        quoteReplies.removeWhere((q) => q.id == id);
        conversationQuotes.removeWhere((q) => q.id == id);
        if (currentQuoteReply?.id == id) {
          currentQuoteReply = null;
        }
        return true;
      }
      return false;
    } catch (e) {
      errorMessage = '删除引用回复失败: $e';
      return false;
    } finally {
      isLoading = false;
    }
  }

  /// 撤回引用回复
  @action
  Future<bool> recallQuoteReply(int id) async {
    isLoading = true;
    errorMessage = null;
    try {
      final dto = await _service.recallQuoteReply(id);
      if (dto != null) {
        final index = quoteReplies.indexWhere((q) => q.id == id);
        if (index >= 0) {
          quoteReplies[index] = dto;
        }
        return true;
      }
      return false;
    } catch (e) {
      errorMessage = '撤回引用回复失败: $e';
      return false;
    } finally {
      isLoading = false;
    }
  }

  /// 选择要引用的消息
  @action
  void selectQuoteForReply(MessageQuoteReplyModel quote) {
    selectedQuoteForReply = quote;
    if (quote.quotedMessageInfo != null) {
      quoteChainStack.add(quote.quotedMessageInfo!);
    }
    currentQuoteLevel = quote.quoteLevel + 1;
  }

  /// 清除选中的引用
  @action
  void clearSelectedQuote() {
    selectedQuoteForReply = null;
    quoteChainStack.clear();
    currentQuoteLevel = 1;
  }

  /// 进入批量引用模式
  @action
  void enterBatchQuoteMode() {
    isBatchQuoteMode = true;
    selectedMessageIds.clear();
  }

  /// 退出批量引用模式
  @action
  void exitBatchQuoteMode() {
    isBatchQuoteMode = false;
    selectedMessageIds.clear();
  }

  /// 切换消息选中状态
  @action
  void toggleMessageSelection(int messageId) {
    if (selectedMessageIds.contains(messageId)) {
      selectedMessageIds.remove(messageId);
    } else {
      selectedMessageIds.add(messageId);
    }
  }

  /// 检查消息是否已选中
  @action
  bool isMessageSelected(int messageId) {
    return selectedMessageIds.contains(messageId);
  }

  /// 获取引用链
  @action
  List<QuotedMessageInfo> getQuoteChain(int quoteId) {
    final quote = quoteReplies.firstWhere((q) => q.id == quoteId, orElse: () => throw Exception('Quote not found'));
    return quote.quoteChainDetails ?? [];
  }

  /// 获取嵌套引用回复
  @action
  Future<void> loadNestedQuotes(int parentQuoteId) async {
    isLoading = true;
    errorMessage = null;
    try {
      final list = await _service.getNestedQuotes(parentQuoteId);
      for (final dto in list) {
        if (!quoteReplies.any((q) => q.id == dto.id)) {
          quoteReplies.add(dto);
        }
      }
    } catch (e) {
      errorMessage = '加载嵌套引用回复失败: $e';
    } finally {
      isLoading = false;
    }
  }

  /// 检查是否可以引用消息
  @action
  Future<bool> canQuoteMessage(int messageId) async {
    try {
      return await _service.canQuoteMessage(messageId);
    } catch (e) {
      return false;
    }
  }

  /// 获取引用统计
  @action
  Future<int> getQuoteCount(int messageId) async {
    try {
      return await _service.countQuotesByMessage(messageId);
    } catch (e) {
      return 0;
    }
  }

  /// 清除错误
  @action
  void clearError() {
    errorMessage = null;
  }

  /// 重置状态
  @action
  void reset() {
    quoteReplies.clear();
    conversationQuotes.clear();
    currentQuoteReply = null;
    selectedQuoteForReply = null;
    quoteChainStack.clear();
    selectedMessageIds.clear();
    isBatchQuoteMode = false;
    currentQuoteLevel = 1;
    errorMessage = null;
  }
}
