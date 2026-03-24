/**
 * 协作增强 AI 助手服务
 * 提供与后端协作 AI 功能的完整交互接口
 */

import 'dart:convert';
import 'package:http/http.dart' as http;

/// 协作类型枚举
enum CollaborationType {
  teamMeeting,
  projectDiscussion,
  brainstormSession,
  dailyStandup,
  retrospective,
  planningSession,
  codeReview,
  designReview,
  trainingSession,
  clientMeeting,
  oneOnOne,
  workshop,
  conference,
  other
}

/// 协作类型扩展
extension CollaborationTypeExtension on CollaborationType {
  String get value {
    switch (this) {
      case CollaborationType.teamMeeting:
        return 'TEAM_MEETING';
      case CollaborationType.projectDiscussion:
        return 'PROJECT_DISCUSSION';
      case CollaborationType.brainstormSession:
        return 'BRAINSTORM_SESSION';
      case CollaborationType.dailyStandup:
        return 'DAILY_STANDUP';
      case CollaborationType.retrospective:
        return 'RETROSPECTIVE';
      case CollaborationType.planningSession:
        return 'PLANNING_SESSION';
      case CollaborationType.codeReview:
        return 'CODE_REVIEW';
      case CollaborationType.designReview:
        return 'DESIGN_REVIEW';
      case CollaborationType.trainingSession:
        return 'TRAINING_SESSION';
      case CollaborationType.clientMeeting:
        return 'CLIENT_MEETING';
      case CollaborationType.oneOnOne:
        return 'ONE_ON_ONE';
      case CollaborationType.workshop:
        return 'WORKSHOP';
      case CollaborationType.conference:
        return 'CONFERENCE';
      case CollaborationType.other:
        return 'OTHER';
    }
  }

  static CollaborationType fromValue(String value) {
    switch (value) {
      case 'TEAM_MEETING':
        return CollaborationType.teamMeeting;
      case 'PROJECT_DISCUSSION':
        return CollaborationType.projectDiscussion;
      case 'BRAINSTORM_SESSION':
        return CollaborationType.brainstormSession;
      case 'DAILY_STANDUP':
        return CollaborationType.dailyStandup;
      case 'RETROSPECTIVE':
        return CollaborationType.retrospective;
      case 'PLANNING_SESSION':
        return CollaborationType.planningSession;
      case 'CODE_REVIEW':
        return CollaborationType.codeReview;
      case 'DESIGN_REVIEW':
        return CollaborationType.designReview;
      case 'TRAINING_SESSION':
        return CollaborationType.trainingSession;
      case 'CLIENT_MEETING':
        return CollaborationType.clientMeeting;
      case 'ONE_ON_ONE':
        return CollaborationType.oneOnOne;
      case 'WORKSHOP':
        return CollaborationType.workshop;
      case 'CONFERENCE':
        return CollaborationType.conference;
      case 'OTHER':
        return CollaborationType.other;
      default:
        return CollaborationType.other;
    }
  }

  String get label {
    switch (this) {
      case CollaborationType.teamMeeting:
        return '团队会议';
      case CollaborationType.projectDiscussion:
        return '项目讨论';
      case CollaborationType.brainstormSession:
        return '头脑风暴';
      case CollaborationType.dailyStandup:
        return '每日站会';
      case CollaborationType.retrospective:
        return '回顾会议';
      case CollaborationType.planningSession:
        return '规划会议';
      case CollaborationType.codeReview:
        return '代码评审';
      case CollaborationType.designReview:
        return '设计评审';
      case CollaborationType.trainingSession:
        return '培训会议';
      case CollaborationType.clientMeeting:
        return '客户会议';
      case CollaborationType.oneOnOne:
        return '一对一会议';
      case CollaborationType.workshop:
        return '研讨会';
      case CollaborationType.conference:
        return '会议';
      case CollaborationType.other:
        return '其他';
    }
  }
}

/// 协作 AI 配置模型
class CollaborationAIConfig {
  final int? id;
  final String sessionId;
  final String userId;
  final String? groupId;
  final CollaborationType collaborationType;
  final String? meetingMinutes;
  final String? projectProgress;
  final String? taskAssignments;
  final String? collaborationPatterns;
  final String? realtimeSuggestions;
  final String? efficiencyReport;
  final String? teamKnowledge;
  final String? bottleneckAnalysis;
  final String? roleAllocation;
  final String? meetingQuality;
  final bool enabled;
  final int aiConfidence;
  final int analysisFrequency;
  final bool autoGenerateMinutes;
  final bool trackProgress;
  final bool identifyTasks;
  final bool analyzePatterns;
  final bool provideSuggestions;
  final bool generateReport;
  final bool buildKnowledge;
  final bool identifyBottlenecks;
  final bool optimizeRoles;
  final bool assessMeetings;
  final List<String>? insights;
  final List<String>? recommendations;
  final Map<String, double>? performanceMetrics;
  final String? customSettings;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final DateTime? lastAnalysisAt;
  final DateTime? nextAnalysisAt;
  final int? version;

  CollaborationAIConfig({
    this.id,
    required this.sessionId,
    required this.userId,
    this.groupId,
    required this.collaborationType,
    this.meetingMinutes,
    this.projectProgress,
    this.taskAssignments,
    this.collaborationPatterns,
    this.realtimeSuggestions,
    this.efficiencyReport,
    this.teamKnowledge,
    this.bottleneckAnalysis,
    this.roleAllocation,
    this.meetingQuality,
    this.enabled = true,
    this.aiConfidence = 0,
    this.analysisFrequency = 60,
    this.autoGenerateMinutes = true,
    this.trackProgress = true,
    this.identifyTasks = true,
    this.analyzePatterns = true,
    this.provideSuggestions = true,
    this.generateReport = true,
    this.buildKnowledge = true,
    this.identifyBottlenecks = true,
    this.optimizeRoles = true,
    this.assessMeetings = true,
    this.insights,
    this.recommendations,
    this.performanceMetrics,
    this.customSettings,
    this.createdAt,
    this.updatedAt,
    this.lastAnalysisAt,
    this.nextAnalysisAt,
    this.version,
  });

  factory CollaborationAIConfig.fromJson(Map<String, dynamic> json) {
    return CollaborationAIConfig(
      id: json['id'],
      sessionId: json['sessionId'] ?? '',
      userId: json['userId'] ?? '',
      groupId: json['groupId'],
      collaborationType: CollaborationTypeExtension.fromValue(
        json['collaborationType'] ?? 'OTHER'
      ),
      meetingMinutes: json['meetingMinutes'],
      projectProgress: json['projectProgress'],
      taskAssignments: json['taskAssignments'],
      collaborationPatterns: json['collaborationPatterns'],
      realtimeSuggestions: json['realtimeSuggestions'],
      efficiencyReport: json['efficiencyReport'],
      teamKnowledge: json['teamKnowledge'],
      bottleneckAnalysis: json['bottleneckAnalysis'],
      roleAllocation: json['roleAllocation'],
      meetingQuality: json['meetingQuality'],
      enabled: json['enabled'] ?? true,
      aiConfidence: json['aiConfidence'] ?? 0,
      analysisFrequency: json['analysisFrequency'] ?? 60,
      autoGenerateMinutes: json['autoGenerateMinutes'] ?? true,
      trackProgress: json['trackProgress'] ?? true,
      identifyTasks: json['identifyTasks'] ?? true,
      analyzePatterns: json['analyzePatterns'] ?? true,
      provideSuggestions: json['provideSuggestions'] ?? true,
      generateReport: json['generateReport'] ?? true,
      buildKnowledge: json['buildKnowledge'] ?? true,
      identifyBottlenecks: json['identifyBottlenecks'] ?? true,
      optimizeRoles: json['optimizeRoles'] ?? true,
      assessMeetings: json['assessMeetings'] ?? true,
      insights: json['insights'] != null
          ? List<String>.from(json['insights'])
          : null,
      recommendations: json['recommendations'] != null
          ? List<String>.from(json['recommendations'])
          : null,
      performanceMetrics: json['performanceMetrics'] != null
          ? Map<String, double>.from(json['performanceMetrics'])
          : null,
      customSettings: json['customSettings'],
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : null,
      lastAnalysisAt: json['lastAnalysisAt'] != null
          ? DateTime.parse(json['lastAnalysisAt'])
          : null,
      nextAnalysisAt: json['nextAnalysisAt'] != null
          ? DateTime.parse(json['nextAnalysisAt'])
          : null,
      version: json['version'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'sessionId': sessionId,
      'userId': userId,
      if (groupId != null) 'groupId': groupId,
      'collaborationType': collaborationType.value,
      if (meetingMinutes != null) 'meetingMinutes': meetingMinutes,
      if (projectProgress != null) 'projectProgress': projectProgress,
      if (taskAssignments != null) 'taskAssignments': taskAssignments,
      if (collaborationPatterns != null)
        'collaborationPatterns': collaborationPatterns,
      if (realtimeSuggestions != null)
        'realtimeSuggestions': realtimeSuggestions,
      if (efficiencyReport != null) 'efficiencyReport': efficiencyReport,
      if (teamKnowledge != null) 'teamKnowledge': teamKnowledge,
      if (bottleneckAnalysis != null) 'bottleneckAnalysis': bottleneckAnalysis,
      if (roleAllocation != null) 'roleAllocation': roleAllocation,
      if (meetingQuality != null) 'meetingQuality': meetingQuality,
      'enabled': enabled,
      'aiConfidence': aiConfidence,
      'analysisFrequency': analysisFrequency,
      'autoGenerateMinutes': autoGenerateMinutes,
      'trackProgress': trackProgress,
      'identifyTasks': identifyTasks,
      'analyzePatterns': analyzePatterns,
      'provideSuggestions': provideSuggestions,
      'generateReport': generateReport,
      'buildKnowledge': buildKnowledge,
      'identifyBottlenecks': identifyBottlenecks,
      'optimizeRoles': optimizeRoles,
      'assessMeetings': assessMeetings,
      if (insights != null) 'insights': insights,
      if (recommendations != null) 'recommendations': recommendations,
      if (performanceMetrics != null) 'performanceMetrics': performanceMetrics,
      if (customSettings != null) 'customSettings': customSettings,
      if (createdAt != null) 'createdAt': createdAt?.toIso8601String(),
      if (updatedAt != null) 'updatedAt': updatedAt?.toIso8601String(),
      if (lastAnalysisAt != null)
        'lastAnalysisAt': lastAnalysisAt?.toIso8601String(),
      if (nextAnalysisAt != null)
        'nextAnalysisAt': nextAnalysisAt?.toIso8601String(),
      if (version != null) 'version': version,
    };
  }

  /// 检查是否需要分析
  bool get needsAnalysis {
    if (nextAnalysisAt == null) return false;
    return DateTime.now().isAfter(nextAnalysisAt!);
  }

  /// 获取启用的功能列表
  List<String> get enabledFeatures {
    final features = <String>[];
    if (autoGenerateMinutes) features.add('自动生成纪要');
    if (trackProgress) features.add('跟踪进度');
    if (identifyTasks) features.add('识别任务');
    if (analyzePatterns) features.add('分析模式');
    if (provideSuggestions) features.add('实时建议');
    if (generateReport) features.add('效率报告');
    if (buildKnowledge) features.add('构建知识库');
    if (identifyBottlenecks) features.add('识别瓶颈');
    if (optimizeRoles) features.add('优化角色');
    if (assessMeetings) features.add('评估会议');
    return features;
  }

  /// 获取 AI 置信度标签
  String get confidenceLabel {
    if (aiConfidence >= 80) return '高';
    if (aiConfidence >= 50) return '中';
    return '低';
  }
}

/// 创建协作 AI 配置请求
class CreateCollaborationAIRequest {
  final String sessionId;
  final String userId;
  final String? groupId;
  final CollaborationType collaborationType;
  final int? analysisFrequency;
  final bool? autoGenerateMinutes;
  final bool? trackProgress;
  final bool? identifyTasks;
  final bool? analyzePatterns;
  final bool? provideSuggestions;
  final bool? generateReport;
  final bool? buildKnowledge;
  final bool? identifyBottlenecks;
  final bool? optimizeRoles;
  final bool? assessMeetings;
  final Map<String, dynamic>? customSettings;

  CreateCollaborationAIRequest({
    required this.sessionId,
    required this.userId,
    this.groupId,
    required this.collaborationType,
    this.analysisFrequency,
    this.autoGenerateMinutes,
    this.trackProgress,
    this.identifyTasks,
    this.analyzePatterns,
    this.provideSuggestions,
    this.generateReport,
    this.buildKnowledge,
    this.identifyBottlenecks,
    this.optimizeRoles,
    this.assessMeetings,
    this.customSettings,
  });

  Map<String, dynamic> toJson() {
    return {
      'sessionId': sessionId,
      'userId': userId,
      if (groupId != null) 'groupId': groupId,
      'collaborationType': collaborationType.value,
      if (analysisFrequency != null) 'analysisFrequency': analysisFrequency,
      if (autoGenerateMinutes != null)
        'autoGenerateMinutes': autoGenerateMinutes,
      if (trackProgress != null) 'trackProgress': trackProgress,
      if (identifyTasks != null) 'identifyTasks': identifyTasks,
      if (analyzePatterns != null) 'analyzePatterns': analyzePatterns,
      if (provideSuggestions != null) 'provideSuggestions': provideSuggestions,
      if (generateReport != null) 'generateReport': generateReport,
      if (buildKnowledge != null) 'buildKnowledge': buildKnowledge,
      if (identifyBottlenecks != null)
        'identifyBottlenecks': identifyBottlenecks,
      if (optimizeRoles != null) 'optimizeRoles': optimizeRoles,
      if (assessMeetings != null) 'assessMeetings': assessMeetings,
      if (customSettings != null) 'customSettings': customSettings,
    };
  }
}

/// 协作增强 AI 助手服务类
class CollaborationAIService {
  final String baseUrl;
  final http.Client _client;

  CollaborationAIService({
    this.baseUrl = '/api/v1/collaboration-ai',
    http.Client? client,
  }) : _client = client ?? http.Client();

  /// 创建新的协作 AI 配置
  Future<CollaborationAIConfig> createCollaborationAI(
    CreateCollaborationAIRequest request,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 201) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('创建失败：${response.statusCode}');
    }
  }

  /// 根据 ID 获取协作 AI 配置
  Future<CollaborationAIConfig> getCollaborationAI(int id) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/$id'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('获取失败：${response.statusCode}');
    }
  }

  /// 根据会话 ID 获取协作 AI 配置
  Future<CollaborationAIConfig> getCollaborationAIBySessionId(
    String sessionId,
  ) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/session/$sessionId'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('获取失败：${response.statusCode}');
    }
  }

  /// 获取用户的所有协作 AI 配置
  Future<List<CollaborationAIConfig>> getUserCollaborationAIs(
    String userId,
  ) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/user/$userId'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList
          .map((json) => CollaborationAIConfig.fromJson(json))
          .toList();
    } else {
      throw Exception('获取失败：${response.statusCode}');
    }
  }

  /// 获取群组的所有协作 AI 配置
  Future<List<CollaborationAIConfig>> getGroupCollaborationAIs(
    String groupId,
  ) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/group/$groupId'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList
          .map((json) => CollaborationAIConfig.fromJson(json))
          .toList();
    } else {
      throw Exception('获取失败：${response.statusCode}');
    }
  }

  /// 更新协作 AI 配置
  Future<CollaborationAIConfig> updateCollaborationAI(
    int id,
    Map<String, dynamic> updates,
  ) async {
    final response = await _client.put(
      Uri.parse('$baseUrl/$id'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(updates),
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('更新失败：${response.statusCode}');
    }
  }

  /// 删除协作 AI 配置
  Future<void> deleteCollaborationAI(int id) async {
    final response = await _client.delete(
      Uri.parse('$baseUrl/$id'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode != 204) {
      throw Exception('删除失败：${response.statusCode}');
    }
  }

  /// 分析协作会议并生成纪要
  Future<CollaborationAIConfig> analyzeMeetingAndGenerateMinutes(
    int id,
    String conversationContent,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$id/analyze-meeting'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'conversationContent': conversationContent}),
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('分析失败：${response.statusCode}');
    }
  }

  /// 跟踪项目进度
  Future<CollaborationAIConfig> trackProjectProgress(
    int id,
    Map<String, dynamic> projectData,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$id/track-progress'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(projectData),
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('跟踪失败：${response.statusCode}');
    }
  }

  /// 识别任务分配
  Future<CollaborationAIConfig> identifyTaskAssignments(
    int id,
    List<String> participantMessages,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$id/identify-tasks'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'participantMessages': participantMessages}),
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('识别失败：${response.statusCode}');
    }
  }

  /// 提供实时协作建议
  Future<CollaborationAIConfig> provideRealtimeSuggestions(
    int id,
    String currentContext,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$id/provide-suggestions'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'currentContext': currentContext}),
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('建议失败：${response.statusCode}');
    }
  }

  /// 生成个性化效率报告
  Future<CollaborationAIConfig> generateEfficiencyReport(int id) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$id/generate-report'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('生成报告失败：${response.statusCode}');
    }
  }

  /// 构建团队知识库
  Future<CollaborationAIConfig> buildTeamKnowledge(
    int id,
    List<String> knowledgeSources,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$id/build-knowledge'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'knowledgeSources': knowledgeSources}),
    );

    if (response.statusCode == 200) {
      return CollaborationAIConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('构建知识失败：${response.statusCode}');
    }
  }

  /// 获取系统健康状态
  Future<Map<String, dynamic>> getHealthStatus() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/health'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('获取健康状态失败：${response.statusCode}');
    }
  }

  /// 快速创建协作 AI 配置
  Future<CollaborationAIConfig> quickCreate({
    required String sessionId,
    required String userId,
    String? groupId,
    CollaborationType collaborationType = CollaborationType.teamMeeting,
  }) async {
    final request = CreateCollaborationAIRequest(
      sessionId: sessionId,
      userId: userId,
      groupId: groupId,
      collaborationType: collaborationType,
      analysisFrequency: 60,
      autoGenerateMinutes: true,
      trackProgress: true,
      identifyTasks: true,
      analyzePatterns: true,
      provideSuggestions: true,
      generateReport: true,
      buildKnowledge: true,
      identifyBottlenecks: true,
      optimizeRoles: true,
      assessMeetings: true,
    );

    return await createCollaborationAI(request);
  }

  @override
  void dispose() {
    _client.close();
  }
}

/// 工具函数类
class CollaborationAIUtils {
  /// 格式化 AI 置信度
  static String formatConfidence(int confidence) {
    if (confidence >= 80) return '高 ($confidence%)';
    if (confidence >= 50) return '中 ($confidence%)';
    return '低 ($confidence%)';
  }

  /// 格式化分析频率
  static String formatAnalysisFrequency(int minutes) {
    if (minutes < 60) return '$minutes 分钟';
    if (minutes < 1440) return '${(minutes / 60).floor()}小时';
    return '${(minutes / 1440).floor()}天';
  }

  /// 计算配置完整度
  static int calculateCompleteness(CollaborationAIConfig config) {
    int totalFields = 20;
    int filledFields = 0;

    if (config.sessionId.isNotEmpty) filledFields++;
    if (config.userId.isNotEmpty) filledFields++;
    if (config.groupId != null) filledFields++;
    if (config.meetingMinutes != null) filledFields++;
    if (config.projectProgress != null) filledFields++;
    if (config.taskAssignments != null) filledFields++;
    if (config.collaborationPatterns != null) filledFields++;
    if (config.realtimeSuggestions != null) filledFields++;
    if (config.efficiencyReport != null) filledFields++;
    if (config.teamKnowledge != null) filledFields++;
    if (config.bottleneckAnalysis != null) filledFields++;
    if (config.roleAllocation != null) filledFields++;
    if (config.meetingQuality != null) filledFields++;
    if (config.insights != null && config.insights!.isNotEmpty) filledFields++;
    if (config.recommendations != null &&
        config.recommendations!.isNotEmpty) filledFields++;
    if (config.performanceMetrics != null &&
        config.performanceMetrics!.isNotEmpty) filledFields++;
    if (config.customSettings != null) filledFields++;
    if (config.lastAnalysisAt != null) filledFields++;
    if (config.nextAnalysisAt != null) filledFields++;

    return ((filledFields / totalFields) * 100).round();
  }
}
