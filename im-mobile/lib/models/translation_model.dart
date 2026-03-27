class TranslationRecord {
  final String id;
  final String sourceText;
  final String translatedText;
  final String sourceLanguage;
  final String targetLanguage;
  final DateTime timestamp;
  final bool isOffline;
  final bool isFavorite;
  final Map<String, dynamic>? metadata;
  
  TranslationRecord({
    required this.id,
    required this.sourceText,
    required this.translatedText,
    required this.sourceLanguage,
    required this.targetLanguage,
    required this.timestamp,
    this.isOffline = false,
    this.isFavorite = false,
    this.metadata,
  });
  
  factory TranslationRecord.fromJson(Map<String, dynamic> json) {
    return TranslationRecord(
      id: json['id'] ?? '',
      sourceText: json['sourceText'] ?? '',
      translatedText: json['translatedText'] ?? '',
      sourceLanguage: json['sourceLanguage'] ?? 'auto',
      targetLanguage: json['targetLanguage'] ?? 'zh-CN',
      timestamp: json['timestamp'] != null
          ? DateTime.parse(json['timestamp'])
          : DateTime.now(),
      isOffline: json['isOffline'] ?? false,
      isFavorite: json['isFavorite'] ?? false,
      metadata: json['metadata'],
    );
  }
  
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'sourceText': sourceText,
      'translatedText': translatedText,
      'sourceLanguage': sourceLanguage,
      'targetLanguage': targetLanguage,
      'timestamp': timestamp.toIso8601String(),
      'isOffline': isOffline,
      'isFavorite': isFavorite,
      'metadata': metadata,
    };
  }
  
  TranslationRecord copyWith({
    String? id,
    String? sourceText,
    String? translatedText,
    String? sourceLanguage,
    String? targetLanguage,
    DateTime? timestamp,
    bool? isOffline,
    bool? isFavorite,
    Map<String, dynamic>? metadata,
  }) {
    return TranslationRecord(
      id: id ?? this.id,
      sourceText: sourceText ?? this.sourceText,
      translatedText: translatedText ?? this.translatedText,
      sourceLanguage: sourceLanguage ?? this.sourceLanguage,
      targetLanguage: targetLanguage ?? this.targetLanguage,
      timestamp: timestamp ?? this.timestamp,
      isOffline: isOffline ?? this.isOffline,
      isFavorite: isFavorite ?? this.isFavorite,
      metadata: metadata ?? this.metadata,
    );
  }
  
  String get sourceLanguageName {
    return _languageNames[sourceLanguage] ?? sourceLanguage;
  }
  
  String get targetLanguageName {
    return _languageNames[targetLanguage] ?? targetLanguage;
  }
  
  String get displayTimestamp {
    final now = DateTime.now();
    final diff = now.difference(timestamp);
    
    if (diff.inMinutes < 1) {
      return '刚刚';
    } else if (diff.inHours < 1) {
      return '${diff.inMinutes}分钟前';
    } else if (diff.inDays < 1) {
      return '${diff.inHours}小时前';
    } else if (diff.inDays < 7) {
      return '${diff.inDays}天前';
    } else {
      return '${timestamp.month}月${timestamp.day}日';
    }
  }
  
  static const Map<String, String> _languageNames = {
    'auto': '自动检测',
    'zh-CN': '简体中文',
    'zh-TW': '繁體中文',
    'en': 'English',
    'ja': '日本語',
    'ko': '한국어',
    'fr': 'Français',
    'de': 'Deutsch',
    'es': 'Español',
    'ru': 'Русский',
    'ar': 'العربية',
    'pt': 'Português',
    'it': 'Italiano',
    'th': 'ไทย',
    'vi': 'Tiếng Việt',
    'id': 'Bahasa Indonesia',
  };
  
  @override
  String toString() {
    return 'TranslationRecord{id: $id, source: $sourceLanguage -> $targetLanguage}';
  }
  
  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is TranslationRecord && other.id == id;
  }
  
  @override
  int get hashCode => id.hashCode;
}
