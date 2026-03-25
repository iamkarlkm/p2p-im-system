import 'dart:convert';
import '../models/reaction.dart';
import 'api_service.dart';

class ReactionService {
  final ApiService _api = ApiService();

  Future<void> addReaction(String messageId, String emoji, {String type = 'EMOJI'}) async {
    await _api.post('/api/reactions/add', params: {
      'messageId': messageId,
      'userId': _api.currentUserId,
      'emoji': emoji,
      'type': type,
    });
  }

  Future<void> removeReaction(String messageId, String emoji) async {
    await _api.post('/api/reactions/remove', params: {
      'messageId': messageId,
      'userId': _api.currentUserId,
      'emoji': emoji,
    });
  }

  Future<List<ReactionWithUsers>> getReactions(String messageId) async {
    final resp = await _api.get('/api/reactions/message/$messageId');
    final List<dynamic> data = resp.data;
    return data.map((e) => ReactionWithUsers.fromJson(e)).toList();
  }

  Future<ReactionStats> getStats(String messageId) async {
    final resp = await _api.get('/api/reactions/stats/$messageId');
    return ReactionStats.fromJson(resp.data);
  }

  void subscribeToReactionUpdates(void Function(String, ReactionStats) callback) {
    _api.subscribe('reaction_update', (data) {
      callback(data['messageId'], ReactionStats.fromJson(data));
    });
  }
}
