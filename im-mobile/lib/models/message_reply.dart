/**
 * 消息引用/回复模型
 * 
 * 用于客户端存储和展示消息引用/回复功能
 * 
 * 功能支持：
 * - 单聊消息引用回复
 * - 群聊消息引用回复
 * - 引用预览（包含发送者和内容摘要）
 * - 引用层级显示（最多3层嵌套）
 * - 引用高亮显示
 * - 回复线程展示
 */
class MessageReply {
  final int? id;
  final String originalMsgId;
  final String replyMsgId;
  final int originalSenderId;
  final String? originalSenderNickname;
  final String? originalContentPreview;
  final int? originalMsgType;
  final DateTime? originalMsgTime;
  final int replyDepth;
  final String? replyChainId;
  final int chainDepth;
  final bool originalRecalled;
  final String? replyRemark;
  final bool highlight;
  final int chatType;
  final int chatId;
  final int replyUserId;
  final String? replyUserNickname;
  final String? replyContent;
  final int? replyMsgType;
  final bool deleted;
  final DateTime? createTime;
  final DateTime? updateTime;

  MessageReply({
    this.id,
    required this.originalMsgId,
    required this.replyMsgId,
    required this.originalSenderId,
    this.originalSenderNickname,
    this.originalContentPreview,
    this.originalMsgType,
    this.originalMsgTime,
    this.replyDepth = 0,
    this.replyChainId,
    this.chainDepth = 1,
    this.originalRecalled = false,
    this.replyRemark,
    this.highlight = false,
    required this.chatType,
    required this.chatId,
    required this.replyUserId,
    this.replyUserNickname,
    this.replyContent,
    this.replyMsgType = 1,
    this.deleted = false,
    this.createTime,
    this.updateTime,
  });

  factory MessageReply.fromJson(Map<String, dynamic> json) {
    return MessageReply(
      id: json['id'] as int?,
      originalMsgId: json['originalMsgId'] as String? ?? '',
      replyMsgId: json['replyMsgId'] as String? ?? '',
      originalSenderId: json['originalSenderId'] as int? ?? 0,
      originalSenderNickname: json['originalSenderNickname'] as String?,
      originalContentPreview: json['originalContentPreview'] as String?,
      originalMsgType: json['originalMsgType'] as int?,
      originalMsgTime: json['originalMsgTime'] != null
          ? DateTime.tryParse(json['originalMsgTime'] as String)
          : null,
      replyDepth: json['replyDepth'] as int? ?? 0,
      replyChainId: json['replyChainId'] as String?,
      chainDepth: json['chainDepth'] as int? ?? 1,
      originalRecalled: json['originalRecalled'] as bool? ?? false,
      replyRemark: json['replyRemark'] as String?,
      highlight: json['highlight'] as bool? ?? false,
      chatType: json['chatType'] as int? ?? 1,
      chatId: json['chatId'] as int? ?? 0,
      replyUserId: json['replyUserId'] as int? ?? 0,
      replyUserNickname: json['replyUserNickname'] as String?,
      replyContent: json['replyContent'] as String?,
      replyMsgType: json['replyMsgType'] as int?,
      deleted: json['deleted'] == 1 || json['deleted'] == true,
      createTime: json['createTime'] != null
          ? DateTime.tryParse(json['createTime'] as String)
          : null,
      updateTime: json['updateTime'] != null
          ? DateTime.tryParse(json['updateTime'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'originalMsgId': originalMsgId,
      'replyMsgId': replyMsgId,
      'originalSenderId': originalSenderId,
      'originalSenderNickname': originalSenderNickname,
      'originalContentPreview': originalContentPreview,
      'originalMsgType': originalMsgType,
      'originalMsgTime': originalMsgTime?.toIso8601String(),
      'replyDepth': replyDepth,
      'replyChainId': replyChainId,
      'chainDepth': chainDepth,
      'originalRecalled': originalRecalled,
      'replyRemark': replyRemark,
      'highlight': highlight,
      'chatType': chatType,
      'chatId': chatId,
      'replyUserId': replyUserId,
      'replyUserNickname': replyUserNickname,
      'replyContent': replyContent,
      'replyMsgType': replyMsgType,
      'deleted': deleted ? 1 : 0,
      'createTime': createTime?.toIso8601String(),
      'updateTime': updateTime?.toIso8601String(),
    };
  }

  MessageReply copyWith({
    int? id,
    String? originalMsgId,
    String? replyMsgId,
    int? originalSenderId,
    String? originalSenderNickname,
    String? originalContentPreview,
    int? originalMsgType,
    DateTime? originalMsgTime,
    int? replyDepth,
    String? replyChainId,
    int? chainDepth,
    bool? originalRecalled,
    String? replyRemark,
    bool? highlight,
    int? chatType,
    int? chatId,
    int? replyUserId,
    String? replyUserNickname,
    String? replyContent,
    int? replyMsgType,
    bool? deleted,
    DateTime? createTime,
    DateTime? updateTime,
  }) {
    return MessageReply(
      id: id ?? this.id,
      originalMsgId: originalMsgId ?? this.originalMsgId,
      replyMsgId: replyMsgId ?? this.replyMsgId,
      originalSenderId: originalSenderId ?? this.originalSenderId,
      originalSenderNickname: originalSenderNickname ?? this.originalSenderNickname,
      originalContentPreview: originalContentPreview ?? this.originalContentPreview,
      originalMsgType: originalMsgType ?? this.originalMsgType,
      originalMsgTime: originalMsgTime ?? this.originalMsgTime,
      replyDepth: replyDepth ?? this.replyDepth,
      replyChainId: replyChainId ?? this.replyChainId,
      chainDepth: chainDepth ?? this.chainDepth,
      originalRecalled: originalRecalled ?? this.originalRecalled,
      replyRemark: replyRemark ?? this.replyRemark,
      highlight: highlight ?? this.highlight,
      chatType: chatType ?? this.chatType,
      chatId: chatId ?? this.chatId,
      replyUserId: replyUserId ?? this.replyUserId,
      replyUserNickname: replyUserNickname ?? this.replyUserNickname,
      replyContent: replyContent ?? this.replyContent,
      replyMsgType: replyMsgType ?? this.replyMsgType,
      deleted: deleted ?? this.deleted,
      createTime: createTime ?? this.createTime,
      updateTime: updateTime ?? this.updateTime,
    );
  }

  /** 获取原消息类型的图标名称 */
  String get originalMsgTypeIcon {
    switch (originalMsgType) {
      case 1: return 'text';
      case 2: return 'image';
      case 3: return 'file';
      case 4: return 'audio';
      case 5: return 'video';
      case 6: return 'emoji';
      case 7: return 'location';
      default: return 'text';
    }
  }

  /** 获取回复消息类型的图标名称 */
  String get replyMsgTypeIcon {
    switch (replyMsgType) {
      case 1: return 'text';
      case 2: return 'image';
      case 3: return 'file';
      case 4: return 'audio';
      case 5: return 'video';
      case 6: return 'emoji';
      case 7: return 'location';
      default: return 'text';
    }
  }

  /** 是否是私聊 */
  bool get isPrivateChat => chatType == 1;

  /** 是否是群聊 */
  bool get isGroupChat => chatType == 2;

  @override
  String toString() {
    return 'MessageReply(id: $id, originalMsgId: $originalMsgId, replyMsgId: $replyMsgId, '
        'originalSenderId: $originalSenderId, replyUserId: $replyUserId, '
        'replyDepth: $replyDepth, deleted: $deleted)';
  }
}
