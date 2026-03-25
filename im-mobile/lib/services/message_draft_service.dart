import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

/// 消息草稿跨设备同步服务
/// 用于在移动端管理消息草稿的本地存储和云端同步
class MessageDraftService {
  static final MessageDraftService _instance = MessageDraftService._internal();
  factory MessageDraftService() => _instance;
  MessageDraftService._internal();

  final String _baseUrl = '/api/v1/drafts';
  final String _localStorageKey = 'im_drafts_cache';
  final List<MessageDraft> _pendingSyncQueue = [];
  bool _isSyncing = false;
  int _retryCount = 0;
  final int _maxRetries = 3;

  /// 保存或更新草稿（本地缓存 + 云端同步）
  Future<SyncResponse> saveDraft({
    required int userId,
    required String deviceId,
    required String conversationId,
    required String draftContent,
    String? draftType,
    bool autoSave = false,
    String? replyToMessageId,
    String? attachments,
    String? mentions,
    bool immediateSync = false,
  }) async {
    try {
      // 创建本地草稿对象
      final draft = MessageDraft(
        userId: userId,
        deviceId: deviceId,
        conversationId: conversationId,
        draftContent: draftContent,
        draftType: draftType ?? 'TEXT',
        replyToMessageId: replyToMessageId,
        attachments: attachments,
        mentions: mentions,
        localVersion: _generateVersion(),
        serverVersion: 0,
        lastUpdatedAt: DateTime.now().toIso8601String(),
        syncStatus: SyncStatus.pending,
        autoSave: autoSave,
        createdAt: DateTime.now().toIso8601String(),
        cleared: draftContent.trim().isEmpty,
        active: true,
      );

      // 保存到本地存储
      await _saveToLocalStorage(draft);

      // 立即同步或加入队列
      if (immediateSync) {
        return await _syncDraftToServer(draft);
      } else {
        _addToSyncQueue(draft);
        return SyncResponse(
          success: true,
          draft: draft,
        );
      }
    } catch (error) {
      print('保存草稿失败：$error');
      return SyncResponse(
        success: false,
        error: '保存草稿失败',
      );
    }
  }

  /// 获取指定会话的草稿
  Future<MessageDraft?> getDraft(int userId, String conversationId) async {
    try {
      // 先检查本地缓存
      final localDraft = await _getFromLocalStorage(userId, conversationId);
      if (localDraft != null) {
        return localDraft;
      }

      // 从服务器获取
      final response = await http.get(
        Uri.parse('$_baseUrl/$conversationId?userId=$userId'),
      );
      
      if (response.statusCode == 200) {
        final serverDraft = MessageDraft.fromJson(jsonDecode(response.body));
        // 保存到本地缓存
        await _saveToLocalStorage(serverDraft);
        return serverDraft;
      }
      
      return null;
    } catch (error) {
      print('获取草稿失败：$error');
      return null;
    }
  }

  /// 获取用户所有草稿
  Future<List<MessageDraft>> getUserDrafts(int userId) async {
    try {
      // 先从本地获取
      final localDrafts = await _getAllFromLocalStorage(userId);
      
      // 从服务器获取最新数据
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId'),
      );
      
      if (response.statusCode == 200) {
        final List<dynamic> jsonList = jsonDecode(response.body);
        final serverDrafts = jsonList.map((json) => MessageDraft.fromJson(json)).toList();
        
        // 合并本地和服务器数据（以服务器为准解决冲突）
        final mergedDrafts = _mergeDrafts(localDrafts, serverDrafts);
        
        // 更新本地缓存
        await _updateLocalStorage(userId, mergedDrafts);
        
        return mergedDrafts;
      }
      
      return localDrafts;
    } catch (error) {
      print('获取用户草稿失败：$error');
      return [];
    }
  }

  /// 删除草稿
  Future<bool> deleteDraft(int userId, String conversationId) async {
    try {
      // 从本地删除
      await _removeFromLocalStorage(userId, conversationId);
      
      // 从服务器删除
      final response = await http.delete(
        Uri.parse('$_baseUrl/$conversationId?userId=$userId'),
      );
      
      return response.statusCode == 200;
    } catch (error) {
      print('删除草稿失败：$error');
      return false;
    }
  }

  /// 同步草稿到服务器
  Future<SyncResponse> _syncDraftToServer(MessageDraft draft) async {
    try {
      final response = await http.post(
        Uri.parse(
          '$_baseUrl/sync?userId=${draft.userId}&deviceId=${draft.deviceId}&conversationId=${draft.conversationId}&localVersion=${draft.localVersion}',
        ),
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: {'draftContent': draft.draftContent ?? ''},
      );

      if (response.statusCode == 409) {
        // 冲突
        final conflictedDraft = MessageDraft.fromJson(jsonDecode(response.body));
        return SyncResponse(
          success: false,
          conflict: true,
          draft: conflictedDraft,
        );
      }

      if (response.statusCode == 200) {
        final syncedDraft = MessageDraft.fromJson(jsonDecode(response.body));
        // 更新本地缓存
        await _saveToLocalStorage(syncedDraft);
        
        // 从同步队列中移除
        _removeFromSyncQueue(draft);
        
        return SyncResponse(
          success: true,
          draft: syncedDraft,
        );
      }
      
      return SyncResponse(
        success: false,
        error: '同步失败',
      );
    } catch (error) {
      print('同步草稿失败：$error');
      return SyncResponse(
        success: false,
        error: '网络错误',
      );
    }
  }

  /// 批量同步
  Future<List<SyncResponse>> batchSyncDrafts(int userId, List<BatchSyncItem> drafts) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/batch-sync?userId=$userId'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(drafts.map((item) => item.toJson()).toList()),
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = jsonDecode(response.body);
        final results = jsonList.map((json) => MessageDraft.fromJson(json)).toList();
        
        // 更新本地缓存
        for (final draft in results) {
          await _saveToLocalStorage(draft);
        }
        
        // 从同步队列中移除已同步的
        _removeFromSyncQueueByConversations(userId, drafts.map((d) => d.conversationId).toList());
        
        return results.map((draft) => SyncResponse(success: true, draft: draft)).toList();
      }
      
      return drafts.map((_) => SyncResponse(success: false, error: '批量同步失败')).toList();
    } catch (error) {
      print('批量同步失败：$error');
      return drafts.map((_) => SyncResponse(success: false, error: '网络错误')).toList();
    }
  }

  /// 解决冲突
  Future<SyncResponse> resolveConflict(int draftId, String resolvedContent, int newVersion) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/resolve-conflict/$draftId?resolvedContent=${Uri.encodeComponent(resolvedContent)}&newVersion=$newVersion'),
      );

      if (response.statusCode == 200) {
        final resolvedDraft = MessageDraft.fromJson(jsonDecode(response.body));
        // 更新本地缓存
        await _saveToLocalStorage(resolvedDraft);
        
        return SyncResponse(
          success: true,
          draft: resolvedDraft,
        );
      }
      
      return SyncResponse(
        success: false,
        error: '解决冲突失败',
      );
    } catch (error) {
      print('解决冲突失败：$error');
      return SyncResponse(
        success: false,
        error: '网络错误',
      );
    }
  }

  /// 获取草稿统计
  Future<DraftStatistics?> getDraftStatistics(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/statistics?userId=$userId'),
      );
      
      if (response.statusCode == 200) {
        return DraftStatistics.fromJson(jsonDecode(response.body));
      }
      return null;
    } catch (error) {
      print('获取统计失败：$error');
      return null;
    }
  }

  /// 清空用户所有草稿
  Future<ClearAllResponse> clearAllDrafts(int userId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/clear-all?userId=$userId'),
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        
        // 清除本地缓存
        await _clearAllLocalStorage(userId);
        
        return ClearAllResponse(
          success: json['success'] ?? false,
          clearedCount: json['clearedCount'] ?? 0,
          userId: userId,
          timestamp: DateTime.parse(json['timestamp']),
        );
      }
      
      return ClearAllResponse(
        success: false,
        clearedCount: 0,
        userId: userId,
        timestamp: DateTime.now(),
      );
    } catch (error) {
      print('清空草稿失败：$error');
      return ClearAllResponse(
        success: false,
        clearedCount: 0,
        userId: userId,
        timestamp: DateTime.now(),
      );
    }
  }

  /// 更新草稿活跃状态
  Future<bool> updateActiveStatus(int userId, String deviceId, String conversationId, bool active) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/active-status?userId=$userId&deviceId=$deviceId&conversationId=$conversationId&active=$active'),
      );
      return response.statusCode == 200;
    } catch (error) {
      print('更新活跃状态失败：$error');
      return false;
    }
  }

  /// 添加到同步队列
  void _addToSyncQueue(MessageDraft draft) {
    // 检查是否已经在队列中
    final existingIndex = _pendingSyncQueue.indexWhere((d) =>
      d.userId == draft.userId &&
      d.deviceId == draft.deviceId &&
      d.conversationId == draft.conversationId
    );
    
    if (existingIndex >= 0) {
      // 更新现有项
      _pendingSyncQueue[existingIndex] = draft;
    } else {
      // 添加新项
      _pendingSyncQueue.add(draft);
    }
    
    print('草稿已添加到同步队列，当前队列大小：${_pendingSyncQueue.length}');
  }

  /// 从同步队列中移除
  void _removeFromSyncQueue(MessageDraft draft) {
    _pendingSyncQueue.removeWhere((d) =>
      d.userId == draft.userId &&
      d.deviceId == draft.deviceId &&
      d.conversationId == draft.conversationId
    );
  }

  /// 从同步队列中移除多个
  void _removeFromSyncQueueByConversations(int userId, List<String> conversationIds) {
    _pendingSyncQueue.removeWhere((d) =>
      d.userId == userId && conversationIds.contains(d.conversationId)
    );
  }

  /// 保存到本地存储
  Future<void> _saveToLocalStorage(MessageDraft draft) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final key = 'draft_${draft.userId}_${draft.conversationId}';
      await prefs.setString(key, jsonEncode(draft.toJson()));
      
      // 更新索引
      await _updateDraftIndex(draft.userId, draft.conversationId);
    } catch (error) {
      print('保存到本地存储失败：$error');
    }
  }

  /// 从本地存储获取
  Future<MessageDraft?> _getFromLocalStorage(int userId, String conversationId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final key = 'draft_${userId}_${conversationId}';
      final data = prefs.getString(key);
      return data != null ? MessageDraft.fromJson(jsonDecode(data)) : null;
    } catch (error) {
      print('从本地存储获取失败：$error');
      return null;
    }
  }

  /// 获取用户所有本地草稿
  Future<List<MessageDraft>> _getAllFromLocalStorage(int userId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final drafts = <MessageDraft>[];
      final prefix = 'draft_${userId}_';
      
      for (final key in prefs.getKeys()) {
        if (key.startsWith(prefix)) {
          final data = prefs.getString(key);
          if (data != null) {
            drafts.add(MessageDraft.fromJson(jsonDecode(data)));
          }
        }
      }
      
      return drafts;
    } catch (error) {
      print('获取所有本地草稿失败：$error');
      return [];
    }
  }

  /// 从本地存储删除
  Future<void> _removeFromLocalStorage(int userId, String conversationId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final key = 'draft_${userId}_${conversationId}';
      await prefs.remove(key);
      
      // 更新索引
      await _removeFromDraftIndex(userId, conversationId);
    } catch (error) {
      print('从本地存储删除失败：$error');
    }
  }

  /// 更新本地存储
  Future<void> _updateLocalStorage(int userId, List<MessageDraft> drafts) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      
      // 清空用户所有旧数据
      final prefix = 'draft_${userId}_';
      for (final key in prefs.getKeys()) {
        if (key.startsWith(prefix)) {
          await prefs.remove(key);
        }
      }
      
      // 保存新数据
      for (final draft in drafts) {
        await _saveToLocalStorage(draft);
      }
    } catch (error) {
      print('更新本地存储失败：$error');
    }
  }

  /// 清空所有本地存储
  Future<void> _clearAllLocalStorage(int userId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      
      // 清空用户所有数据
      final prefix = 'draft_${userId}_';
      for (final key in prefs.getKeys()) {
        if (key.startsWith(prefix)) {
          await prefs.remove(key);
        }
      }
      
      // 清除索引
      await prefs.remove('draft_index_$userId');
    } catch (error) {
      print('清空本地存储失败：$error');
    }
  }

  /// 更新草稿索引
  Future<void> _updateDraftIndex(int userId, String conversationId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final indexKey = 'draft_index_$userId';
      final indexData = prefs.getString(indexKey);
      List<String> index = indexData != null ? jsonDecode(indexData).cast<String>() : [];
      
      if (!index.contains(conversationId)) {
        index.add(conversationId);
        await prefs.setString(indexKey, jsonEncode(index));
      }
    } catch (error) {
      print('更新草稿索引失败：$error');
    }
  }

  /// 从索引中移除
  Future<void> _removeFromDraftIndex(int userId, String conversationId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final indexKey = 'draft_index_$userId';
      final indexData = prefs.getString(indexKey);
      
      if (indexData != null) {
        List<String> index = jsonDecode(indexData).cast<String>();
        index = index.where((id) => id != conversationId).toList();
        await prefs.setString(indexKey, jsonEncode(index));
      }
    } catch (error) {
      print('从索引中移除失败：$error');
    }
  }

  /// 生成版本号
  int _generateVersion() {
    return DateTime.now().millisecondsSinceEpoch;
  }

  /// 合并草稿数据（解决冲突）
  List<MessageDraft> _mergeDrafts(List<MessageDraft> localDrafts, List<MessageDraft> serverDrafts) {
    final merged = <MessageDraft>[];
    final serverMap = <String, MessageDraft>{};
    
    // 以服务器数据为主
    for (final serverDraft in serverDrafts) {
      final key = '${serverDraft.userId}-${serverDraft.conversationId}';
      serverMap[key] = serverDraft;
      merged.add(serverDraft);
    }
    
    // 添加本地独有的草稿
    for (final localDraft in localDrafts) {
      final key = '${localDraft.userId}-${localDraft.conversationId}';
      if (!serverMap.containsKey(key)) {
        merged.add(localDraft);
      }
    }
    
    return merged;
  }
}

/// 消息草稿实体
class MessageDraft {
  final int? id;
  final int userId;
  final String deviceId;
  final String conversationId;
  final String? conversationType;
  final String? draftContent;
  final String draftType;
  final String? replyToMessageId;
  final String? attachments;
  final String? mentions;
  final int localVersion;
  final int serverVersion;
  final String lastUpdatedAt;
  final SyncStatus syncStatus;
  final String? conflictInfo;
  final bool autoSave;
  final String createdAt;
  final bool cleared;
  final int? cursorPosition;
  final String? selectionRange;
  final String? language;
  final String? imeState;
  final bool active;
  final String? contextInfo;
  final String? metadata;

  MessageDraft({
    this.id,
    required this.userId,
    required this.deviceId,
    required this.conversationId,
    this.conversationType,
    this.draftContent,
    this.draftType = 'TEXT',
    this.replyToMessageId,
    this.attachments,
    this.mentions,
    required this.localVersion,
    required this.serverVersion,
    required this.lastUpdatedAt,
    required this.syncStatus,
    this.conflictInfo,
    required this.autoSave,
    required this.createdAt,
    required this.cleared,
    this.cursorPosition,
    this.selectionRange,
    this.language,
    this.imeState,
    required this.active,
    this.contextInfo,
    this.metadata,
  });

  factory MessageDraft.fromJson(Map<String, dynamic> json) {
    return MessageDraft(
      id: json['id'] as int?,
      userId: json['userId'] as int,
      deviceId: json['deviceId'] as String,
      conversationId: json['conversationId'] as String,
      conversationType: json['conversationType'] as String?,
      draftContent: json['draftContent'] as String?,
      draftType: json['draftType'] as String? ?? 'TEXT',
      replyToMessageId: json['replyToMessageId'] as String?,
      attachments: json['attachments'] as String?,
      mentions: json['mentions'] as String?,
      localVersion: (json['localVersion'] as num?)?.toInt() ?? 0,
      serverVersion: (json['serverVersion'] as num?)?.toInt() ?? 0,
      lastUpdatedAt: json['lastUpdatedAt'] as String,
      syncStatus: SyncStatus.fromString(json['syncStatus'] as String? ?? 'PENDING'),
      conflictInfo: json['conflictInfo'] as String?,
      autoSave: json['autoSave'] as bool? ?? false,
      createdAt: json['createdAt'] as String,
      cleared: json['cleared'] as bool? ?? false,
      cursorPosition: json['cursorPosition'] as int?,
      selectionRange: json['selectionRange'] as String?,
      language: json['language'] as String?,
      imeState: json['imeState'] as String?,
      active: json['active'] as bool? ?? false,
      contextInfo: json['contextInfo'] as String?,
      metadata: json['metadata'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'deviceId': deviceId,
      'conversationId': conversationId,
      'conversationType': conversationType,
      'draftContent': draftContent,
      'draftType': draftType,
      'replyToMessageId': replyToMessageId,
      'attachments': attachments,
      'mentions': mentions,
      'localVersion': localVersion,
      'serverVersion': serverVersion,
      'lastUpdatedAt': lastUpdatedAt,
      'syncStatus': syncStatus.toString(),
      'conflictInfo': conflictInfo,
      'autoSave': autoSave,
      'createdAt': createdAt,
      'cleared': cleared,
      'cursorPosition': cursorPosition,
      'selectionRange': selectionRange,
      'language': language,
      'imeState': imeState,
      'active': active,
      'contextInfo': contextInfo,
      'metadata': metadata,
    };
  }
}

/// 同步状态枚举
enum SyncStatus {
  pending,
  syncing,
  synced,
  conflict,
  error,
}

extension SyncStatusExtension on SyncStatus {
  static SyncStatus fromString(String value) {
    switch (value.toUpperCase()) {
      case 'PENDING':
        return SyncStatus.pending;
      case 'SYNCING':
        return SyncStatus.syncing;
      case 'SYNCED':
        return SyncStatus.synced;
      case 'CONFLICT':
        return SyncStatus.conflict;
      case 'ERROR':
        return SyncStatus.error;
      default:
        return SyncStatus.pending;
    }
  }

  @override
  String toString() {
    switch (this) {
      case SyncStatus.pending:
        return 'PENDING';
      case SyncStatus.syncing:
        return 'SYNCING';
      case SyncStatus.synced:
        return 'SYNCED';
      case SyncStatus.conflict:
        return 'CONFLICT';
      case SyncStatus.error:
        return 'ERROR';
    }
  }
}

/// 同步响应
class SyncResponse {
  final bool success;
  final MessageDraft? draft;
  final String? error;
  final bool? conflict;

  SyncResponse({
    required this.success,
    this.draft,
    this.error,
    this.conflict,
  });
}

/// 批量同步项
class BatchSyncItem {
  final String deviceId;
  final String conversationId;
  final String draftContent;
  final int localVersion;

  BatchSyncItem({
    required this.deviceId,
    required this.conversationId,
    required this.draftContent,
    required this.localVersion,
  });

  Map<String, dynamic> toJson() {
    return {
      'deviceId': deviceId,
      'conversationId': conversationId,
      'draftContent': draftContent,
      'localVersion': localVersion,
    };
  }
}

/// 草稿统计
class DraftStatistics {
  final int totalDrafts;
  final int pendingSync;
  final int conflicts;
  final String lastUpdated;

  DraftStatistics({
    required this.totalDrafts,
    required this.pendingSync,
    required this.conflicts,
    required this.lastUpdated,
  });

  factory DraftStatistics.fromJson(Map<String, dynamic> json) {
    return DraftStatistics(
      totalDrafts: (json['totalDrafts'] as num?)?.toInt() ?? 0,
      pendingSync: (json['pendingSync'] as num?)?.toInt() ?? 0,
      conflicts: (json['conflicts'] as num?)?.toInt() ?? 0,
      lastUpdated: json['lastUpdated'] as String,
    );
  }
}

/// 清空所有响应
class ClearAllResponse {
  final bool success;
  final int clearedCount;
  final int userId;
  final DateTime timestamp;

  ClearAllResponse({
    required this.success,
    required this.clearedCount,
    required this.userId,
    required this.timestamp,
  });
}
