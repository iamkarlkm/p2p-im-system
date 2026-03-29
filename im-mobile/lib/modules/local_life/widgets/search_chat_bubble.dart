import 'package:flutter/material.dart';
import '../../models/conversation_message.dart';
import 'poi_answer_card.dart';

/// 对话消息气泡组件
/// 支持用户消息和AI回复的不同样式
/// 
/// Author: IM Development Team
/// Since: 2026-03-28
class SearchChatBubble extends StatelessWidget {
  final ConversationMessage message;
  final Function(String) onPoiTap;
  final Function(dynamic) onNavigateTap;

  const SearchChatBubble({
    Key? key,
    required this.message,
    required this.onPoiTap,
    required this.onNavigateTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final isUser = message.isUser;
    
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (!isUser) _buildAvatar(),
          const SizedBox(width: 8),
          Flexible(
            child: Column(
              crossAxisAlignment: isUser ? CrossAxisAlignment.end : CrossAxisAlignment.start,
              children: [
                _buildBubble(context, isUser),
                if (message.searchResults != null && message.searchResults!.isNotEmpty)
                  _buildSearchResults(),
                if (message.poiDetail != null)
                  PoiAnswerCard(
                    poi: message.poiDetail!,
                    onTap: () => onPoiTap(message.poiDetail!.id),
                    onNavigate: () => onNavigateTap(message.poiDetail),
                  ),
                if (message.suggestedQuestions != null)
                  _buildSuggestedQuestions(),
              ],
            ),
          ),
          const SizedBox(width: 8),
          if (isUser) _buildUserAvatar(),
        ],
      ),
    );
  }

  Widget _buildAvatar() {
    return CircleAvatar(
      backgroundColor: Colors.blue[100],
      child: Icon(Icons.smart_toy, color: Colors.blue[700]),
    );
  }

  Widget _buildUserAvatar() {
    return CircleAvatar(
      backgroundColor: Colors.green[100],
      child: Icon(Icons.person, color: Colors.green[700]),
    );
  }

  Widget _buildBubble(BuildContext context, bool isUser) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: isUser ? Colors.blue[500] : Colors.grey[200],
        borderRadius: BorderRadius.only(
          topLeft: const Radius.circular(16),
          topRight: const Radius.circular(16),
          bottomLeft: Radius.circular(isUser ? 16 : 4),
          bottomRight: Radius.circular(isUser ? 4 : 16),
        ),
      ),
      child: Text(
        message.text,
        style: TextStyle(
          color: isUser ? Colors.white : Colors.black87,
          fontSize: 15,
        ),
      ),
    );
  }

  Widget _buildSearchResults() {
    final results = message.searchResults!;
    
    return Container(
      margin: const EdgeInsets.only(top: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '为您找到 ${results.length} 个结果：',
            style: const TextStyle(
              fontSize: 13,
              color: Colors.grey,
            ),
          ),
          const SizedBox(height: 8),
          ...results.take(3).map((poi) => _buildPoiListItem(poi)),
          if (results.length > 3)
            TextButton(
              onPressed: () {
                // 查看更多结果
              },
              child: Text('查看全部 ${results.length} 个结果'),
            ),
        ],
      ),
    );
  }

  Widget _buildPoiListItem(dynamic poi) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: poi.images?.isNotEmpty == true
              ? Image.network(
                  poi.images[0],
                  width: 56,
                  height: 56,
                  fit: BoxFit.cover,
                )
              : Container(
                  width: 56,
                  height: 56,
                  color: Colors.grey[300],
                  child: const Icon(Icons.place, color: Colors.grey),
                ),
        ),
        title: Text(
          poi.name,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.star, size: 14, color: Colors.amber[600]),
                Text(' ${poi.rating}'),
                const SizedBox(width: 8),
                Text('${poi.distance}m'),
              ],
            ),
            Text(poi.category),
          ],
        ),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (poi.avgPrice != null)
              Text(
                '¥${poi.avgPrice}/人',
                style: TextStyle(
                  color: Colors.orange[700],
                  fontWeight: FontWeight.bold,
                ),
              ),
            const SizedBox(height: 4),
            if (poi.isOpenNow == true)
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: Colors.green[50],
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  '营业中',
                  style: TextStyle(
                    fontSize: 10,
                    color: Colors.green[700],
                  ),
                ),
              ),
          ],
        ),
        onTap: () => onPoiTap(poi.id),
      ),
    );
  }

  Widget _buildSuggestedQuestions() {
    final questions = message.suggestedQuestions!;
    
    return Container(
      margin: const EdgeInsets.only(top: 8),
      child: Wrap(
        spacing: 8,
        runSpacing: 8,
        children: questions.map((question) {
          return ActionChip(
            avatar: const Icon(Icons.lightbulb_outline, size: 16),
            label: Text(question),
            backgroundColor: Colors.amber[50],
            onPressed: () {
              // 发送建议问题
            },
          );
        }).toList(),
      ),
    );
  }
}
