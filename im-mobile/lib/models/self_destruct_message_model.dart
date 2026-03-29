import 'package:mobx/mobx.dart';

part 'self_destruct_message_model.g.dart';

/// 阅后即焚消息模型
/// 
/// 支持设置自动销毁时间的消息，对方阅读后倒计时销毁
/// 
/// @author IM Development Team
/// @since 1.0.0
class SelfDestructMessageModel = _SelfDestructMessageModel with _$SelfDestructMessageModel;

abstract class _SelfDestructMessageModel with Store {
  _SelfDestructMessageModel({
    this.id,
    this.conversationId,
    this.senderId,
    this.senderName,
    this.senderAvatar,
    this.receiverId,
    this.messageContent,
    this.contentType = ContentType.text,
    this.durationSeconds = 10,
    this.isRead = false,
    this.readAt,
    this.remainingSeconds,
    this.isDestroyed = false,
    this.destroyedAt,
    this.screenshotDetected = false,
    this.screenshotCount = 0,
    this.allowForward = false,
    this.allowScreenshot = false,
    this.blurPreview = true,
    this.notificationMessage,
    this.createdAt,
    this.canRead = true,
  });

  @observable
  String? id;

  @observable
  String? conversationId;

  @observable
  String? senderId;

  @observable
  String? senderName;

  @observable
  String? senderAvatar;

  @observable
  String? receiverId;

  @observable
  String? messageContent;

  @observable
  ContentType contentType;

  @observable
  int durationSeconds;

  @observable
  bool isRead;

  @observable
  String? readAt;

  @observable
  int? remainingSeconds;

  @observable
  bool isDestroyed;

  @observable
  String? destroyedAt;

  @observable
  bool screenshotDetected;

  @observable
  int screenshotCount;

  @observable
  bool allowForward;

  @observable
  bool allowScreenshot;

  @observable
  bool blurPreview;

  @observable
  String? notificationMessage;

  @observable
  String? createdAt;

  @observable
  bool canRead;

  /// 从JSON解析
  factory SelfDestructMessageModel.fromJson(Map<String, dynamic> json) {
    return SelfDestructMessageModel(
      id: json['id'] as String?,
      conversationId: json['conversationId'] as String?,
      senderId: json['senderId'] as String?,
      senderName: json['senderName'] as String?,
      senderAvatar: json['senderAvatar'] as String?,
      receiverId: json['receiverId'] as String?,
      messageContent: json['messageContent'] as String?,
      contentType: _parseContentType(json['contentType'] as String?),
      durationSeconds: json['durationSeconds'] as int? ?? 10,
      isRead: json['isRead'] as bool? ?? false,
      readAt: json['readAt'] as String?,
      remainingSeconds: json['remainingSeconds'] as int?,
      isDestroyed: json['isDestroyed'] as bool? ?? false,
      destroyedAt: json['destroyedAt'] as String?,
      screenshotDetected: json['screenshotDetected'] as bool? ?? false,
      screenshotCount: json['screenshotCount'] as int? ?? 0,
      allowForward: json['allowForward'] as bool? ?? false,
      allowScreenshot: json['allowScreenshot'] as bool? ?? false,
      blurPreview: json['blurPreview'] as bool? ?? true,
      notificationMessage: json['notificationMessage'] as String?,
      createdAt: json['createdAt'] as String?,
      canRead: json['canRead'] as bool? ?? true,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'conversationId': conversationId,
      'senderId': senderId,
      'senderName': senderName,
      'senderAvatar': senderAvatar,
      'receiverId': receiverId,
      'messageContent': messageContent,
      'contentType': contentType.name,
      'durationSeconds': durationSeconds,
      'isRead': isRead,
      'readAt': readAt,
      'remainingSeconds': remainingSeconds,
      'isDestroyed': isDestroyed,
      'destroyedAt': destroyedAt,
      'screenshotDetected': screenshotDetected,
      'screenshotCount': screenshotCount,
      'allowForward': allowForward,
      'allowScreenshot': allowScreenshot,
      'blurPreview': blurPreview,
      'notificationMessage': notificationMessage,
      'createdAt': createdAt,
      'canRead': canRead,
    };
  }

  /// 创建请求转换为JSON
  Map<String, dynamic> toCreateRequestJson() {
    return {
      'conversationId': conversationId,
      'receiverId': receiverId,
      'messageContent': messageContent,
      'contentType': contentType.name,
      'durationSeconds': durationSeconds,
      'allowForward': allowForward,
      'allowScreenshot': allowScreenshot,
      'blurPreview': blurPreview,
      'notificationMessage': notificationMessage,
    };
  }

  /// 标记为已读
  @action
  void markAsRead() {
    isRead = true;
    readAt = DateTime.now().toIso8601String();
    remainingSeconds = durationSeconds;
  }

  /// 标记为已销毁
  @action
  void markAsDestroyed() {
    isDestroyed = true;
    destroyedAt = DateTime.now().toIso8601String();
    messageContent = null;
    remainingSeconds = 0;
  }

  /// 更新剩余时间
  @action
  void updateRemainingSeconds(int seconds) {
    remainingSeconds = seconds;
    if (seconds <= 0) {
      markAsDestroyed();
    }
  }

  /// 记录截图
  @action
  void recordScreenshot() {
    screenshotDetected = true;
    screenshotCount++;
  }

  /// 检查是否可以阅读
  bool get canViewContent {
    return !isDestroyed && (isRead ? (remainingSeconds != null && remainingSeconds! > 0) : true);
  }

  /// 获取显示内容
  String get displayContent {
    if (isDestroyed) {
      return '消息已销毁';
    }
    if (!isRead) {
      return notificationMessage ?? '你收到了一条阅后即焚消息';
    }
    return messageContent ?? '';
  }

  /// 获取状态文本
  String get statusText {
    if (isDestroyed) {
      return '已销毁';
    }
    if (isRead) {
      return '已读 · ${remainingSeconds}s后销毁';
    }
    return '未读';
  }

  /// 检查是否过期
  bool get isExpired {
    return isDestroyed || (isRead && (remainingSeconds == null || remainingSeconds! <= 0));
  }

  /// 复制
  SelfDestructMessageModel copyWith({
    String? id,
    String? conversationId,
    String? senderId,
    String? senderName,
    String? senderAvatar,
    String? receiverId,
    String? messageContent,
    ContentType? contentType,
    int? durationSeconds,
    bool? isRead,
    String? readAt,
    int? remainingSeconds,
    bool? isDestroyed,
    String? destroyedAt,
    bool? screenshotDetected,
    int? screenshotCount,
    bool? allowForward,
    bool? allowScreenshot,
    bool? blurPreview,
    String? notificationMessage,
    String? createdAt,
    bool? canRead,
  }) {
    return SelfDestructMessageModel(
      id: id ?? this.id,
      conversationId: conversationId ?? this.conversationId,
      senderId: senderId ?? this.senderId,
      senderName: senderName ?? this.senderName,
      senderAvatar: senderAvatar ?? this.senderAvatar,
      receiverId: receiverId ?? this.receiverId,
      messageContent: messageContent ?? this.messageContent,
      contentType: contentType ?? this.contentType,
      durationSeconds: durationSeconds ?? this.durationSeconds,
      isRead: isRead ?? this.isRead,
      readAt: readAt ?? this.readAt,
      remainingSeconds: remainingSeconds ?? this.remainingSeconds,
      isDestroyed: isDestroyed ?? this.isDestroyed,
      destroyedAt: destroyedAt ?? this.destroyedAt,
      screenshotDetected: screenshotDetected ?? this.screenshotDetected,
      screenshotCount: screenshotCount ?? this.screenshotCount,
      allowForward: allowForward ?? this.allowForward,
      allowScreenshot: allowScreenshot ?? this.allowScreenshot,
      blurPreview: blurPreview ?? this.blurPreview,
      notificationMessage: notificationMessage ?? this.notificationMessage,
      createdAt: createdAt ?? this.createdAt,
      canRead: canRead ?? this.canRead,
    );
  }

  /// 解析内容类型
  static ContentType _parseContentType(String? type) {
    switch (type?.toUpperCase()) {
      case 'TEXT':
        return ContentType.text;
      case 'IMAGE':
        return ContentType.image;
      case 'VIDEO':
        return ContentType.video;
      case 'AUDIO':
        return ContentType.audio;
      case 'FILE':
        return ContentType.file;
      case 'LOCATION':
        return ContentType.location;
      default:
        return ContentType.text;
    }
  }
}

/// 内容类型枚举
enum ContentType {
  text,
  image,
  video,
  audio,
  file,
  location,
}

/// 阅读响应模型
class SelfDestructReadResponse {
  final String messageId;
  final String? messageContent;
  final int remainingSeconds;
  final int durationSeconds;
  final bool allowScreenshot;

  SelfDestructReadResponse({
    required this.messageId,
    this.messageContent,
    required this.remainingSeconds,
    required this.durationSeconds,
    required this.allowScreenshot,
  });

  factory SelfDestructReadResponse.fromJson(Map<String, dynamic> json) {
    return SelfDestructReadResponse(
      messageId: json['messageId'] as String,
      messageContent: json['messageContent'] as String?,
      remainingSeconds: json['remainingSeconds'] as int,
      durationSeconds: json['durationSeconds'] as int,
      allowScreenshot: json['allowScreenshot'] as bool,
    );
  }
}

/// 配置选项模型
class SelfDestructConfig {
  final List<int> durationOptions;
  final int defaultDuration;
  final bool defaultBlurPreview;
  final bool defaultAllowScreenshot;
  final bool defaultAllowForward;
  final List<String> contentTypes;

  SelfDestructConfig({
    required this.durationOptions,
    required this.defaultDuration,
    required this.defaultBlurPreview,
    required this.defaultAllowScreenshot,
    required this.defaultAllowForward,
    required this.contentTypes,
  });

  factory SelfDestructConfig.fromJson(Map<String, dynamic> json) {
    return SelfDestructConfig(
      durationOptions: (json['durationOptions'] as List<dynamic>).map((e) => e as int).toList(),
      defaultDuration: json['defaultDuration'] as int,
      defaultBlurPreview: json['defaultBlurPreview'] as bool,
      defaultAllowScreenshot: json['defaultAllowScreenshot'] as bool,
      defaultAllowForward: json['defaultAllowForward'] as bool,
      contentTypes: (json['contentTypes'] as List<dynamic>).map((e) => e as String).toList(),
    );
  }
}

/// 创建请求模型
class SelfDestructCreateRequest {
  String conversationId;
  String receiverId;
  String messageContent;
  ContentType contentType;
  int durationSeconds;
  bool allowForward;
  bool allowScreenshot;
  bool blurPreview;
  String? notificationMessage;

  SelfDestructCreateRequest({
    required this.conversationId,
    required this.receiverId,
    required this.messageContent,
    this.contentType = ContentType.text,
    this.durationSeconds = 10,
    this.allowForward = false,
    this.allowScreenshot = false,
    this.blurPreview = true,
    this.notificationMessage,
  });

  Map<String, dynamic> toJson() {
    return {
      'conversationId': conversationId,
      'receiverId': receiverId,
      'messageContent': messageContent,
      'contentType': contentType.name,
      'durationSeconds': durationSeconds,
      'allowForward': allowForward,
      'allowScreenshot': allowScreenshot,
      'blurPreview': blurPreview,
      'notificationMessage': notificationMessage,
    };
  }
}

/// 截图检测响应模型
class ScreenshotDetectResponse {
  final bool detected;
  final int totalCount;
  final String warningMessage;

  ScreenshotDetectResponse({
    required this.detected,
    required this.totalCount,
    required this.warningMessage,
  });

  factory ScreenshotDetectResponse.fromJson(Map<String, dynamic> json) {
    return ScreenshotDetectResponse(
      detected: json['detected'] as bool,
      totalCount: json['totalCount'] as int,
      warningMessage: json['warningMessage'] as String,
    );
  }
}
