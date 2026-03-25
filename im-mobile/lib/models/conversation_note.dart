class Note {
  final int id;
  final String userId;
  final String conversationId;
  final String content;
  final String? quotedMessageId;
  final String? quotedMessageContent;
  final List<TagInfo> tags;
  final DateTime createdAt;
  final DateTime updatedAt;

  Note({
    required this.id,
    required this.userId,
    required this.conversationId,
    required this.content,
    this.quotedMessageId,
    this.quotedMessageContent,
    required this.tags,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Note.fromJson(Map<String, dynamic> json) {
    return Note(
      id: json['id'] as int,
      userId: json['userId'] as String,
      conversationId: json['conversationId'] as String,
      content: json['content'] as String,
      quotedMessageId: json['quotedMessageId'] as String?,
      quotedMessageContent: json['quotedMessageContent'] as String?,
      tags: (json['tags'] as List<dynamic>?)
          ?.map((e) => TagInfo.fromJson(e as Map<String, dynamic>))
          .toList() ?? [],
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );
  }
}

class TagInfo {
  final int id;
  final String name;
  final String color;

  TagInfo({required this.id, required this.name, required this.color});

  factory TagInfo.fromJson(Map<String, dynamic> json) {
    return TagInfo(
      id: json['id'] as int,
      name: json['name'] as String,
      color: json['color'] as String,
    );
  }
}

class NotePage {
  final List<Note> items;
  final int page;
  final int size;
  final int total;
  final int totalPages;

  NotePage({
    required this.items,
    required this.page,
    required this.size,
    required this.total,
    required this.totalPages,
  });

  factory NotePage.fromJson(Map<String, dynamic> json) {
    return NotePage(
      items: (json['items'] as List<dynamic>)
          .map((e) => Note.fromJson(e as Map<String, dynamic>))
          .toList(),
      page: json['page'] as int,
      size: json['size'] as int,
      total: json['total'] as int,
      totalPages: json['totalPages'] as int,
    );
  }
}

class NoteRequest {
  final int? id;
  final String conversationId;
  final String content;
  final String? quotedMessageId;
  final String? quotedMessageContent;
  final List<int>? tagIds;

  NoteRequest({
    this.id,
    required this.conversationId,
    required this.content,
    this.quotedMessageId,
    this.quotedMessageContent,
    this.tagIds,
  });

  Map<String, dynamic> toJson() => {
    if (id != null) 'id': id,
    'conversationId': conversationId,
    'content': content,
    if (quotedMessageId != null) 'quotedMessageId': quotedMessageId,
    if (quotedMessageContent != null) 'quotedMessageContent': quotedMessageContent,
    if (tagIds != null) 'tagIds': tagIds,
  };
}
