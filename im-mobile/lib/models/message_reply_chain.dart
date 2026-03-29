// Message Reply Chain Model for im-mobile

class MessageReplyChain {
  final int id;
  final int conversationId;
  final int rootMessageId;
  final int parentMessageId;
  final int userId;
  final String userNickname;
  final int depth;
  final String branchPath;
  final bool isBranchNode;
  final bool isRoot;
  final bool isLeaf;
  final DateTime createdAt;
  final DateTime updatedAt;
  final List<ReplyChainNode> branchNodes;
  final MessageContext? context;

  MessageReplyChain({
    required this.id,
    required this.conversationId,
    required this.rootMessageId,
    required this.parentMessageId,
    required this.userId,
    required this.userNickname,
    required this.depth,
    required this.branchPath,
    required this.isBranchNode,
    required this.isRoot,
    required this.isLeaf,
    required this.createdAt,
    required this.updatedAt,
    this.branchNodes = const [],
    this.context,
  });

  factory MessageReplyChain.fromJson(Map<String, dynamic> json) {
    return MessageReplyChain(
      id: json['id'] ?? 0,
      conversationId: json['conversationId'] ?? 0,
      rootMessageId: json['rootMessageId'] ?? 0,
      parentMessageId: json['parentMessageId'] ?? 0,
      userId: json['userId'] ?? 0,
      userNickname: json['userNickname'] ?? '',
      depth: json['depth'] ?? 0,
      branchPath: json['branchPath'] ?? '',
      isBranchNode: json['isBranchNode'] ?? false,
      isRoot: json['isRoot'] ?? false,
      isLeaf: json['isLeaf'] ?? true,
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
      updatedAt: DateTime.tryParse(json['updatedAt'] ?? '') ?? DateTime.now(),
      branchNodes: (json['branchNodes'] as List?)
              ?.map((e) => ReplyChainNode.fromJson(e))
              .toList() ??
          [],
      context: json['context'] != null ? MessageContext.fromJson(json['context']) : null,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'conversationId': conversationId,
        'rootMessageId': rootMessageId,
        'parentMessageId': parentMessageId,
        'userId': userId,
        'userNickname': userNickname,
        'depth': depth,
        'branchPath': branchPath,
        'isBranchNode': isBranchNode,
        'isRoot': isRoot,
        'isLeaf': isLeaf,
        'createdAt': createdAt.toIso8601String(),
        'updatedAt': updatedAt.toIso8601String(),
        'branchNodes': branchNodes.map((e) => e.toJson()).toList(),
      };
}

class ReplyChainNode {
  final int id;
  final int messageId;
  final int userId;
  final String userNickname;
  final String contentPreview;
  final String messageType;
  final int positionInBranch;
  final DateTime createdAt;

  ReplyChainNode({
    required this.id,
    required this.messageId,
    required this.userId,
    required this.userNickname,
    required this.contentPreview,
    required this.messageType,
    required this.positionInBranch,
    required this.createdAt,
  });

  factory ReplyChainNode.fromJson(Map<String, dynamic> json) {
    return ReplyChainNode(
      id: json['id'] ?? 0,
      messageId: json['messageId'] ?? 0,
      userId: json['userId'] ?? 0,
      userNickname: json['userNickname'] ?? '',
      contentPreview: json['contentPreview'] ?? '',
      messageType: json['messageType'] ?? 'text',
      positionInBranch: json['positionInBranch'] ?? 0,
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'messageId': messageId,
        'userId': userId,
        'userNickname': userNickname,
        'contentPreview': contentPreview,
        'messageType': messageType,
        'positionInBranch': positionInBranch,
        'createdAt': createdAt.toIso8601String(),
      };
}

class MessageContext {
  final int messageId;
  final String content;
  final String senderName;
  final String messageType;
  final DateTime timestamp;
  final String? thumbnailUrl;

  MessageContext({
    required this.messageId,
    required this.content,
    required this.senderName,
    required this.messageType,
    required this.timestamp,
    this.thumbnailUrl,
  });

  factory MessageContext.fromJson(Map<String, dynamic> json) {
    return MessageContext(
      messageId: json['messageId'] ?? 0,
      content: json['content'] ?? '',
      senderName: json['senderName'] ?? '',
      messageType: json['messageType'] ?? 'text',
      timestamp: DateTime.tryParse(json['timestamp'] ?? '') ?? DateTime.now(),
      thumbnailUrl: json['thumbnailUrl'],
    );
  }
}

class ReplyChainRequest {
  final int conversationId;
  final int rootMessageId;
  final int parentMessageId;
  final int? depth;
  final String? branchPath;

  ReplyChainRequest({
    required this.conversationId,
    required this.rootMessageId,
    required this.parentMessageId,
    this.depth,
    this.branchPath,
  });

  Map<String, dynamic> toJson() => {
        'conversationId': conversationId,
        'rootMessageId': rootMessageId,
        'parentMessageId': parentMessageId,
        if (depth != null) 'depth': depth,
        if (branchPath != null) 'branchPath': branchPath,
      };
}
