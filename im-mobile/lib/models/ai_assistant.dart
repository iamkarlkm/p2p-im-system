/// AI助手能力类型
enum AIAssistantCapability {
  textGeneration,
  imageGeneration,
  imageAnalysis,
  audioTranscription,
  audioSynthesis,
  videoAnalysis,
  codeGeneration,
  translation,
  summarization,
  reasoning,
}

/// AI助手提供商
enum AIProvider {
  openai,
  claude,
  gemini,
  local,
  custom,
}

/// AI助手模型
class AIAssistantModel {
  final String id;
  final String name;
  final String description;
  final AIProvider provider;
  final List<AIAssistantCapability> capabilities;
  final Map<String, dynamic>? config;
  final String? avatarUrl;
  final String? systemPrompt;
  final int? maxTokens;
  final double? temperature;

  AIAssistantModel({
    required this.id,
    required this.name,
    required this.description,
    required this.provider,
    required this.capabilities,
    this.config,
    this.avatarUrl,
    this.systemPrompt,
    this.maxTokens,
    this.temperature,
  });

  factory AIAssistantModel.fromJson(Map<String, dynamic> json) {
    return AIAssistantModel(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      provider: AIProvider.values.firstWhere(
        (e) => e.name == json['provider'],
        orElse: () => AIProvider.openai,
      ),
      capabilities: (json['capabilities'] as List)
          .map((c) => AIAssistantCapability.values.firstWhere(
            (e) => e.name == c,
            orElse: () => AIAssistantCapability.textGeneration,
          ))
          .toList(),
      config: json['config'],
      avatarUrl: json['avatarUrl'],
      systemPrompt: json['systemPrompt'],
      maxTokens: json['maxTokens'],
      temperature: json['temperature']?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
    'description': description,
    'provider': provider.name,
    'capabilities': capabilities.map((c) => c.name).toList(),
    'config': config,
    'avatarUrl': avatarUrl,
    'systemPrompt': systemPrompt,
    'maxTokens': maxTokens,
    'temperature': temperature,
  };

  bool get supportsImages => capabilities.contains(AIAssistantCapability.imageAnalysis) ||
      capabilities.contains(AIAssistantCapability.imageGeneration);
  
  bool get supportsAudio => capabilities.contains(AIAssistantCapability.audioTranscription) ||
      capabilities.contains(AIAssistantCapability.audioSynthesis);
  
  bool get supportsVideo => capabilities.contains(AIAssistantCapability.videoAnalysis);
  
  bool get supportsCode => capabilities.contains(AIAssistantCapability.codeGeneration);
}

/// AI对话会话
class AIConversation {
  final String id;
  final String? title;
  final AIAssistantModel assistant;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int messageCount;
  final bool isPinned;
  final Map<String, dynamic>? metadata;

  AIConversation({
    required this.id,
    this.title,
    required this.assistant,
    required this.createdAt,
    required this.updatedAt,
    this.messageCount = 0,
    this.isPinned = false,
    this.metadata,
  });

  factory AIConversation.fromJson(Map<String, dynamic> json) {
    return AIConversation(
      id: json['id'],
      title: json['title'],
      assistant: AIAssistantModel.fromJson(json['assistant']),
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      messageCount: json['messageCount'] ?? 0,
      isPinned: json['isPinned'] ?? false,
      metadata: json['metadata'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'title': title,
    'assistant': assistant.toJson(),
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
    'messageCount': messageCount,
    'isPinned': isPinned,
    'metadata': metadata,
  };

  AIConversation copyWith({
    String? id,
    String? title,
    AIAssistantModel? assistant,
    DateTime? createdAt,
    DateTime? updatedAt,
    int? messageCount,
    bool? isPinned,
    Map<String, dynamic>? metadata,
  }) {
    return AIConversation(
      id: id ?? this.id,
      title: title ?? this.title,
      assistant: assistant ?? this.assistant,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      messageCount: messageCount ?? this.messageCount,
      isPinned: isPinned ?? this.isPinned,
      metadata: metadata ?? this.metadata,
    );
  }
}

/// 流式响应块
class StreamChunk {
  final String? content;
  final bool isDone;
  final String? error;
  final Map<String, dynamic>? metadata;

  StreamChunk({
    this.content,
    this.isDone = false,
    this.error,
    this.metadata,
  });
}
