class Bot {
  final String id;
  final String name;
  final String description;
  final String avatarUrl;
  final String ownerId;
  final String botType;
  final String? aiProvider;
  final String? aiModel;
  final String? webhookUrl;
  final List<String> slashCommands;
  final bool enabled;
  final bool globalEnabled;
  final List<String> allowedGroupIds;
  final BotConfig? config;
  final DateTime createdAt;
  final DateTime updatedAt;

  Bot({
    required this.id,
    required this.name,
    required this.description,
    required this.avatarUrl,
    required this.ownerId,
    required this.botType,
    this.aiProvider,
    this.aiModel,
    this.webhookUrl,
    required this.slashCommands,
    required this.enabled,
    required this.globalEnabled,
    required this.allowedGroupIds,
    this.config,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Bot.fromJson(Map<String, dynamic> json) {
    return Bot(
      id: json['id'] as String,
      name: json['name'] as String,
      description: json['description'] as String? ?? '',
      avatarUrl: json['avatarUrl'] as String? ?? '',
      ownerId: json['ownerId'] as String,
      botType: json['botType'] as String,
      aiProvider: json['aiProvider'] as String?,
      aiModel: json['aiModel'] as String?,
      webhookUrl: json['webhookUrl'] as String?,
      slashCommands: (json['slashCommands'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList() ?? [],
      enabled: json['enabled'] as bool? ?? true,
      globalEnabled: json['globalEnabled'] as bool? ?? false,
      allowedGroupIds: (json['allowedGroupIds'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList() ?? [],
      config: json['config'] != null
          ? BotConfig.fromJson(json['config'] as Map<String, dynamic>)
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'avatarUrl': avatarUrl,
      'ownerId': ownerId,
      'botType': botType,
      'aiProvider': aiProvider,
      'aiModel': aiModel,
      'webhookUrl': webhookUrl,
      'slashCommands': slashCommands,
      'enabled': enabled,
      'globalEnabled': globalEnabled,
      'allowedGroupIds': allowedGroupIds,
      'config': config?.toJson(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  Bot copyWith({
    String? id,
    String? name,
    String? description,
    String? avatarUrl,
    String? ownerId,
    String? botType,
    String? aiProvider,
    String? aiModel,
    String? webhookUrl,
    List<String>? slashCommands,
    bool? enabled,
    bool? globalEnabled,
    List<String>? allowedGroupIds,
    BotConfig? config,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return Bot(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      avatarUrl: avatarUrl ?? this.avatarUrl,
      ownerId: ownerId ?? this.ownerId,
      botType: botType ?? this.botType,
      aiProvider: aiProvider ?? this.aiProvider,
      aiModel: aiModel ?? this.aiModel,
      webhookUrl: webhookUrl ?? this.webhookUrl,
      slashCommands: slashCommands ?? this.slashCommands,
      enabled: enabled ?? this.enabled,
      globalEnabled: globalEnabled ?? this.globalEnabled,
      allowedGroupIds: allowedGroupIds ?? this.allowedGroupIds,
      config: config ?? this.config,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  String get botTypeLabel {
    switch (botType) {
      case 'AI':
        return '🤖 AI Bot';
      case 'WEBHOOK':
        return '🔗 Webhook Bot';
      case 'SCRIPTED':
        return '📝 Scripted Bot';
      default:
        return '❓ Unknown';
    }
  }
}

class BotConfig {
  final int maxTokens;
  final double temperature;
  final String systemPrompt;
  final int maxHistoryMessages;
  final int responseTimeoutSeconds;
  final bool streamEnabled;
  final String apiKey;
  final String apiEndpoint;
  final int retryAttempts;
  final int rateLimitPerMinute;

  BotConfig({
    required this.maxTokens,
    required this.temperature,
    required this.systemPrompt,
    required this.maxHistoryMessages,
    required this.responseTimeoutSeconds,
    required this.streamEnabled,
    required this.apiKey,
    required this.apiEndpoint,
    required this.retryAttempts,
    required this.rateLimitPerMinute,
  });

  factory BotConfig.fromJson(Map<String, dynamic> json) {
    return BotConfig(
      maxTokens: json['maxTokens'] as int? ?? 1000,
      temperature: (json['temperature'] as num?)?.toDouble() ?? 0.7,
      systemPrompt: json['systemPrompt'] as String? ?? '',
      maxHistoryMessages: json['maxHistoryMessages'] as int? ?? 10,
      responseTimeoutSeconds: json['responseTimeoutSeconds'] as int? ?? 30,
      streamEnabled: json['streamEnabled'] as bool? ?? false,
      apiKey: json['apiKey'] as String? ?? '',
      apiEndpoint: json['apiEndpoint'] as String? ?? '',
      retryAttempts: json['retryAttempts'] as int? ?? 3,
      rateLimitPerMinute: json['rateLimitPerMinute'] as int? ?? 60,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'maxTokens': maxTokens,
      'temperature': temperature,
      'systemPrompt': systemPrompt,
      'maxHistoryMessages': maxHistoryMessages,
      'responseTimeoutSeconds': responseTimeoutSeconds,
      'streamEnabled': streamEnabled,
      'apiKey': apiKey,
      'apiEndpoint': apiEndpoint,
      'retryAttempts': retryAttempts,
      'rateLimitPerMinute': rateLimitPerMinute,
    };
  }
}

class BotMessage {
  final String id;
  final String botId;
  final String senderId;
  final String content;
  final String messageType;
  final DateTime timestamp;
  final Map<String, dynamic>? metadata;

  BotMessage({
    required this.id,
    required this.botId,
    required this.senderId,
    required this.content,
    required this.messageType,
    required this.timestamp,
    this.metadata,
  });

  factory BotMessage.fromJson(Map<String, dynamic> json) {
    return BotMessage(
      id: json['id'] as String,
      botId: json['botId'] as String,
      senderId: json['senderId'] as String,
      content: json['content'] as String,
      messageType: json['messageType'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      metadata: json['metadata'] as Map<String, dynamic>?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'botId': botId,
      'senderId': senderId,
      'content': content,
      'messageType': messageType,
      'timestamp': timestamp.toIso8601String(),
      'metadata': metadata,
    };
  }
}
