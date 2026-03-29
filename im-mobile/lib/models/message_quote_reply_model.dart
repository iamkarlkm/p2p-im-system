import 'package:mobx/mobx.dart';
import 'package:im_mobile/models/user_model.dart';

part 'message_quote_reply_model.g.dart';

/// 消息引用回复模型
/// 支持多级引用、引用链溯源、批量引用

enum QuoteType { single, multi, nested, forward }

enum QuoteStatus { active, edited, deleted, recalled }

class MessageQuoteReplyModel extends _MessageQuoteReplyModel with _$MessageQuoteReplyModel {
  MessageQuoteReplyModel({
    required int id,
    required int messageId,
    required int quotedMessageId,
    required int conversationId,
    required int senderId,
    String? senderName,
    String? senderAvatar,
    String? replyContent,
    int quoteLevel = 1,
    int? rootQuoteId,
    int? parentQuoteId,
    List<int> quoteChain = const [],
    QuoteType quoteType = QuoteType.single,
    bool includeOriginal = true,
    String? highlightKeywords,
    bool isBatchQuote = false,
    List<int> batchQuotedMessageIds = const [],
    QuoteStatus status = QuoteStatus.active,
    DateTime? createdAt,
    DateTime? updatedAt,
    QuotedMessageInfo? quotedMessageInfo,
    List<QuotedMessageInfo>? quoteChainDetails,
  }) : super(
    id: id,
    messageId: messageId,
    quotedMessageId: quotedMessageId,
    conversationId: conversationId,
    senderId: senderId,
    senderName: senderName,
    senderAvatar: senderAvatar,
    replyContent: replyContent,
    quoteLevel: quoteLevel,
    rootQuoteId: rootQuoteId,
    parentQuoteId: parentQuoteId,
    quoteChain: quoteChain,
    quoteType: quoteType,
    includeOriginal: includeOriginal,
    highlightKeywords: highlightKeywords,
    isBatchQuote: isBatchQuote,
    batchQuotedMessageIds: batchQuotedMessageIds,
    status: status,
    createdAt: createdAt,
    updatedAt: updatedAt,
    quotedMessageInfo: quotedMessageInfo,
    quoteChainDetails: quoteChainDetails,
  );

  factory MessageQuoteReplyModel.fromJson(Map<String, dynamic> json) {
    return MessageQuoteReplyModel(
      id: json['id'] as int,
      messageId: json['messageId'] as int,
      quotedMessageId: json['quotedMessageId'] as int,
      conversationId: json['conversationId'] as int,
      senderId: json['senderId'] as int,
      senderName: json['senderName'] as String?,
      senderAvatar: json['senderAvatar'] as String?,
      replyContent: json['replyContent'] as String?,
      quoteLevel: json['quoteLevel'] as int? ?? 1,
      rootQuoteId: json['rootQuoteId'] as int?,
      parentQuoteId: json['parentQuoteId'] as int?,
      quoteChain: (json['quoteChain'] as List<dynamic>?)
          ?.map((e) => e as int)
          .toList() ?? [],
      quoteType: _parseQuoteType(json['quoteType'] as String?),
      includeOriginal: json['includeOriginal'] as bool? ?? true,
      highlightKeywords: json['highlightKeywords'] as String?,
      isBatchQuote: json['isBatchQuote'] as bool? ?? false,
      batchQuotedMessageIds: (json['batchQuotedMessageIds'] as List<dynamic>?)
          ?.map((e) => e as int)
          .toList() ?? [],
      status: _parseQuoteStatus(json['status'] as String?),
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
      quotedMessageInfo: json['quotedMessageInfo'] != null
          ? QuotedMessageInfo.fromJson(json['quotedMessageInfo'] as Map<String, dynamic>)
          : null,
      quoteChainDetails: (json['quoteChainDetails'] as List<dynamic>?)
          ?.map((e) => QuotedMessageInfo.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  static QuoteType _parseQuoteType(String? type) {
    switch (type?.toUpperCase()) {
      case 'SINGLE': return QuoteType.single;
      case 'MULTI': return QuoteType.multi;
      case 'NESTED': return QuoteType.nested;
      case 'FORWARD': return QuoteType.forward;
      default: return QuoteType.single;
    }
  }

  static QuoteStatus _parseQuoteStatus(String? status) {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return QuoteStatus.active;
      case 'EDITED': return QuoteStatus.edited;
      case 'DELETED': return QuoteStatus.deleted;
      case 'RECALLED': return QuoteStatus.recalled;
      default: return QuoteStatus.active;
    }
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'quotedMessageId': quotedMessageId,
      'conversationId': conversationId,
      'senderId': senderId,
      'senderName': senderName,
      'senderAvatar': senderAvatar,
      'replyContent': replyContent,
      'quoteLevel': quoteLevel,
      'rootQuoteId': rootQuoteId,
      'parentQuoteId': parentQuoteId,
      'quoteChain': quoteChain,
      'quoteType': quoteType.name.toUpperCase(),
      'includeOriginal': includeOriginal,
      'highlightKeywords': highlightKeywords,
      'isBatchQuote': isBatchQuote,
      'batchQuotedMessageIds': batchQuotedMessageIds,
      'status': status.name.toUpperCase(),
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'quotedMessageInfo': quotedMessageInfo?.toJson(),
      'quoteChainDetails': quoteChainDetails?.map((e) => e.toJson()).toList(),
    };
  }
}

abstract class _MessageQuoteReplyModel with Store {
  @observable int id;
  @observable int messageId;
  @observable int quotedMessageId;
  @observable int conversationId;
  @observable int senderId;
  @observable String? senderName;
  @observable String? senderAvatar;
  @observable String? replyContent;
  @observable int quoteLevel;
  @observable int? rootQuoteId;
  @observable int? parentQuoteId;
  @observable List<int> quoteChain;
  @observable QuoteType quoteType;
  @observable bool includeOriginal;
  @observable String? highlightKeywords;
  @observable bool isBatchQuote;
  @observable List<int> batchQuotedMessageIds;
  @observable QuoteStatus status;
  @observable DateTime? createdAt;
  @observable DateTime? updatedAt;
  @observable QuotedMessageInfo? quotedMessageInfo;
  @observable List<QuotedMessageInfo>? quoteChainDetails;

  _MessageQuoteReplyModel({
    required this.id,
    required this.messageId,
    required this.quotedMessageId,
    required this.conversationId,
    required this.senderId,
    this.senderName,
    this.senderAvatar,
    this.replyContent,
    this.quoteLevel = 1,
    this.rootQuoteId,
    this.parentQuoteId,
    this.quoteChain = const [],
    this.quoteType = QuoteType.single,
    this.includeOriginal = true,
    this.highlightKeywords,
    this.isBatchQuote = false,
    this.batchQuotedMessageIds = const [],
    this.status = QuoteStatus.active,
    this.createdAt,
    this.updatedAt,
    this.quotedMessageInfo,
    this.quoteChainDetails,
  });

  @computed
  bool get isNested => quoteLevel > 1;

  @computed
  bool get isActive => status == QuoteStatus.active;

  @computed
  bool get isDeleted => status == QuoteStatus.deleted;

  @computed
  bool get isRecalled => status == QuoteStatus.recalled;

  @computed
  bool get isEdited => status == QuoteStatus.edited;

  @computed
  String get displayQuoteType {
    switch (quoteType) {
      case QuoteType.single: return '单条引用';
      case QuoteType.multi: return '多条引用';
      case QuoteType.nested: return '嵌套引用';
      case QuoteType.forward: return '转发引用';
    }
  }

  @computed
  String get displayStatus {
    switch (status) {
      case QuoteStatus.active: return '正常';
      case QuoteStatus.edited: return '已编辑';
      case QuoteStatus.deleted: return '已删除';
      case QuoteStatus.recalled: return '已撤回';
    }
  }

  @action
  void updateContent(String newContent) {
    replyContent = newContent;
    status = QuoteStatus.edited;
    updatedAt = DateTime.now();
  }

  @action
  void markAsDeleted() {
    status = QuoteStatus.deleted;
    updatedAt = DateTime.now();
  }

  @action
  void markAsRecalled() {
    status = QuoteStatus.recalled;
    updatedAt = DateTime.now();
  }
}

/// 引用的原始消息信息
class QuotedMessageInfo {
  final int messageId;
  final int senderId;
  final String? senderName;
  final String? senderAvatar;
  final String? content;
  final String? messageType;
  final DateTime? sentAt;
  final List<String>? mediaUrls;

  QuotedMessageInfo({
    required this.messageId,
    required this.senderId,
    this.senderName,
    this.senderAvatar,
    this.content,
    this.messageType,
    this.sentAt,
    this.mediaUrls,
  });

  factory QuotedMessageInfo.fromJson(Map<String, dynamic> json) {
    return QuotedMessageInfo(
      messageId: json['messageId'] as int,
      senderId: json['senderId'] as int,
      senderName: json['senderName'] as String?,
      senderAvatar: json['senderAvatar'] as String?,
      content: json['content'] as String?,
      messageType: json['messageType'] as String?,
      sentAt: json['sentAt'] != null
          ? DateTime.parse(json['sentAt'] as String)
          : null,
      mediaUrls: (json['mediaUrls'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'senderId': senderId,
      'senderName': senderName,
      'senderAvatar': senderAvatar,
      'content': content,
      'messageType': messageType,
      'sentAt': sentAt?.toIso8601String(),
      'mediaUrls': mediaUrls,
    };
  }

  String get previewContent {
    if (content != null && content!.isNotEmpty) {
      return content!.length > 50 ? '${content!.substring(0, 50)}...' : content!;
    }
    return '[${messageType ?? '未知类型'}]';
  }
}
