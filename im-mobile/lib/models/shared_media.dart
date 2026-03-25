enum MediaType { IMAGE, VIDEO, AUDIO, FILE, LINK, VOICE }

enum LinkType { ARTICLE, VIDEO, PRODUCT, MUSIC, DOCUMENT, UNKNOWN }

enum AlbumType { IMAGE, VIDEO, AUDIO, FILE, ALL }

class SharedMedia {
  final int id;
  final String conversationId;
  final String messageId;
  final String senderId;
  final String? senderName;
  final String? senderAvatar;
  final MediaType mediaType;
  final String fileName;
  final String fileUrl;
  final String? thumbnailUrl;
  final int? fileSize;
  final String? mimeType;
  final int? width;
  final int? height;
  final int? duration;
  final String? description;
  final DateTime createdAt;
  final bool canDelete;

  SharedMedia({
    required this.id,
    required this.conversationId,
    required this.messageId,
    required this.senderId,
    this.senderName,
    this.senderAvatar,
    required this.mediaType,
    required this.fileName,
    required this.fileUrl,
    this.thumbnailUrl,
    this.fileSize,
    this.mimeType,
    this.width,
    this.height,
    this.duration,
    this.description,
    required this.createdAt,
    this.canDelete = true,
  });

  factory SharedMedia.fromJson(Map<String, dynamic> json) {
    return SharedMedia(
      id: json['id'] as int,
      conversationId: json['conversationId'] as String,
      messageId: json['messageId'] as String,
      senderId: json['senderId'] as String,
      senderName: json['senderName'] as String?,
      senderAvatar: json['senderAvatar'] as String?,
      mediaType: MediaType.values.firstWhere(
        (e) => e.name == json['mediaType'],
        orElse: () => MediaType.FILE,
      ),
      fileName: json['fileName'] as String,
      fileUrl: json['fileUrl'] as String,
      thumbnailUrl: json['thumbnailUrl'] as String?,
      fileSize: json['fileSize'] as int?,
      mimeType: json['mimeType'] as String?,
      width: json['width'] as int?,
      height: json['height'] as int?,
      duration: json['duration'] as int?,
      description: json['description'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      canDelete: json['canDelete'] as bool? ?? true,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'conversationId': conversationId,
    'messageId': messageId,
    'senderId': senderId,
    'senderName': senderName,
    'senderAvatar': senderAvatar,
    'mediaType': mediaType.name,
    'fileName': fileName,
    'fileUrl': fileUrl,
    'thumbnailUrl': thumbnailUrl,
    'fileSize': fileSize,
    'mimeType': mimeType,
    'width': width,
    'height': height,
    'duration': duration,
    'description': description,
    'createdAt': createdAt.toIso8601String(),
    'canDelete': canDelete,
  };
}

class MediaPage {
  final List<SharedMedia> items;
  final int page;
  final int size;
  final int total;
  final int totalPages;
  final MediaType? mediaType;
  final int? totalSize;
  final MediaStatistics? statistics;

  MediaPage({
    required this.items,
    required this.page,
    required this.size,
    required this.total,
    required this.totalPages,
    this.mediaType,
    this.totalSize,
    this.statistics,
  });

  factory MediaPage.fromJson(Map<String, dynamic> json) {
    return MediaPage(
      items: (json['items'] as List<dynamic>)
          .map((e) => SharedMedia.fromJson(e as Map<String, dynamic>))
          .toList(),
      page: json['page'] as int,
      size: json['size'] as int,
      total: json['total'] as int,
      totalPages: json['totalPages'] as int,
      mediaType: json['mediaType'] != null
          ? MediaType.values.firstWhere((e) => e.name == json['mediaType'])
          : null,
      totalSize: json['totalSize'] as int?,
      statistics: json['statistics'] != null
          ? MediaStatistics.fromJson(json['statistics'] as Map<String, dynamic>)
          : null,
    );
  }
}

class MediaStatistics {
  final int imageCount;
  final int videoCount;
  final int audioCount;
  final int fileCount;
  final int linkCount;
  final int totalSize;

  MediaStatistics({
    required this.imageCount,
    required this.videoCount,
    required this.audioCount,
    required this.fileCount,
    required this.linkCount,
    required this.totalSize,
  });

  factory MediaStatistics.fromJson(Map<String, dynamic> json) {
    return MediaStatistics(
      imageCount: json['imageCount'] as int? ?? 0,
      videoCount: json['videoCount'] as int? ?? 0,
      audioCount: json['audioCount'] as int? ?? 0,
      fileCount: json['fileCount'] as int? ?? 0,
      linkCount: json['linkCount'] as int? ?? 0,
      totalSize: json['totalSize'] as int? ?? 0,
    );
  }
}

class LinkPreview {
  final int id;
  final String conversationId;
  final String messageId;
  final String url;
  final String? title;
  final String? description;
  final String? image;
  final String? domain;
  final LinkType? linkType;
  final DateTime createdAt;

  LinkPreview({
    required this.id,
    required this.conversationId,
    required this.messageId,
    required this.url,
    this.title,
    this.description,
    this.image,
    this.domain,
    this.linkType,
    required this.createdAt,
  });

  factory LinkPreview.fromJson(Map<String, dynamic> json) {
    return LinkPreview(
      id: json['id'] as int,
      conversationId: json['conversationId'] as String,
      messageId: json['messageId'] as String,
      url: json['url'] as String,
      title: json['title'] as String?,
      description: json['description'] as String?,
      image: json['image'] as String?,
      domain: json['domain'] as String?,
      linkType: json['linkType'] != null
          ? LinkType.values.firstWhere((e) => e.name == json['linkType'], orElse: () => LinkType.UNKNOWN)
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }
}
