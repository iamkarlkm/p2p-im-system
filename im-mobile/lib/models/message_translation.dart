class MessageTranslation {
  final int? id;
  final int messageId;
  final int userId;
  final String sourceLang;
  final String targetLang;
  final String originalContent;
  final String translatedContent;
  final String? provider;
  final String? model;
  final DateTime translatedAt;
  final bool autoTranslated;
  final int? durationMs;

  MessageTranslation({
    this.id,
    required this.messageId,
    required this.userId,
    required this.sourceLang,
    required this.targetLang,
    required this.originalContent,
    required this.translatedContent,
    this.provider,
    this.model,
    required this.translatedAt,
    required this.autoTranslated,
    this.durationMs,
  });

  factory MessageTranslation.fromJson(Map<String, dynamic> json) {
    return MessageTranslation(
      id: json['id'],
      messageId: json['messageId'] ?? 0,
      userId: json['userId'] ?? 0,
      sourceLang: json['sourceLang'] ?? 'en',
      targetLang: json['targetLang'] ?? 'zh-CN',
      originalContent: json['originalContent'] ?? '',
      translatedContent: json['translatedContent'] ?? '',
      provider: json['provider'],
      model: json['model'],
      translatedAt: DateTime.tryParse(json['translatedAt'] ?? '') ?? DateTime.now(),
      autoTranslated: json['autoTranslated'] ?? false,
      durationMs: json['durationMs'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'userId': userId,
      'sourceLang': sourceLang,
      'targetLang': targetLang,
      'originalContent': originalContent,
      'translatedContent': translatedContent,
      'provider': provider,
      'model': model,
      'translatedAt': translatedAt.toIso8601String(),
      'autoTranslated': autoTranslated,
      'durationMs': durationMs,
    };
  }
}

class TranslationSettings {
  final int? id;
  final int userId;
  final bool autoTranslate;
  final String preferredTargetLang;
  final String? autoLangWhitelist;
  final String provider;
  final String? apiKey;
  final bool showOriginal;

  TranslationSettings({
    this.id,
    required this.userId,
    this.autoTranslate = false,
    this.preferredTargetLang = 'zh-CN',
    this.autoLangWhitelist,
    this.provider = 'OPENAI',
    this.apiKey,
    this.showOriginal = true,
  });

  factory TranslationSettings.fromJson(Map<String, dynamic> json) {
    return TranslationSettings(
      id: json['id'],
      userId: json['userId'] ?? 0,
      autoTranslate: json['autoTranslate'] ?? false,
      preferredTargetLang: json['preferredTargetLang'] ?? 'zh-CN',
      autoLangWhitelist: json['autoLangWhitelist'],
      provider: json['provider'] ?? 'OPENAI',
      apiKey: json['apiKey'],
      showOriginal: json['showOriginal'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'autoTranslate': autoTranslate,
      'preferredTargetLang': preferredTargetLang,
      'autoLangWhitelist': autoLangWhitelist,
      'provider': provider,
      'apiKey': apiKey,
      'showOriginal': showOriginal,
    };
  }

  TranslationSettings copyWith({
    int? id, int? userId, bool? autoTranslate, String? preferredTargetLang,
    String? autoLangWhitelist, String? provider, String? apiKey, bool? showOriginal,
  }) {
    return TranslationSettings(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      autoTranslate: autoTranslate ?? this.autoTranslate,
      preferredTargetLang: preferredTargetLang ?? this.preferredTargetLang,
      autoLangWhitelist: autoLangWhitelist ?? this.autoLangWhitelist,
      provider: provider ?? this.provider,
      apiKey: apiKey ?? this.apiKey,
      showOriginal: showOriginal ?? this.showOriginal,
    );
  }
}
