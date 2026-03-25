import 'package:flutter/material.dart';
import '../models/reaction.dart';
import '../services/reaction_service.dart';

class ReactionBubble extends StatelessWidget {
  final String messageId;
  final ReactionStats? stats;
  final VoidCallback onTap;

  const ReactionBubble({
    super.key,
    required this.messageId,
    this.stats,
    required this.onTap,
  });

  static const quickReactions = ['👍', '❤️', '😂', '😮', '😢', '🙏'];

  @override
  Widget build(BuildContext context) {
    final svc = ReactionService();
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Wrap(
          spacing: 4,
          children: quickReactions.map((emoji) {
            final count = stats?.counts[emoji] ?? 0;
            return InkWell(
              onTap: () => svc.addReaction(messageId, emoji),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: count > 0 ? Colors.blue.withAlpha(30) : Colors.grey.withAlpha(30),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: Colors.grey.shade300),
                ),
                child: Text('$emoji $count', style: const TextStyle(fontSize: 14)),
              ),
            );
          }).toList(),
        ),
        if (stats != null && stats!.counts.isNotEmpty)
          Padding(
            padding: const EdgeInsets.only(top: 4),
            child: Wrap(
              spacing: 6,
              children: stats!.counts.entries
                  .where((e) => e.value > 0)
                  .map((e) => Chip(
                        label: Text('${e.key} ${e.value}'),
                        materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                        visualDensity: VisualDensity.compact,
                      ))
                  .toList(),
            ),
          ),
      ],
    );
  }
}

class ReactionSheet extends StatelessWidget {
  final String messageId;
  final List<ReactionWithUsers> reactions;
  final ReactionService _svc = ReactionService();

  ReactionSheet({super.key, required this.messageId, required this.reactions});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Reactions', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: ReactionBubble.quickReactions.map((emoji) {
              final r = reactions.where((e) => e.emoji == emoji).firstOrNull;
              return ActionChip(
                label: Text('$emoji ${r?.count ?? 0}'),
                onPressed: () async {
                  await _svc.addReaction(messageId, emoji);
                  if (context.mounted) Navigator.pop(context);
                },
              );
            }).toList(),
          ),
          const SizedBox(height: 16),
          if (reactions.isNotEmpty) ...[
            const Text('People who reacted:', style: TextStyle(fontWeight: FontWeight.w500)),
            const SizedBox(height: 8),
            ...reactions.map((r) => ListTile(
                  dense: true,
                  leading: CircleAvatar(child: Text(r.emoji)),
                  title: Text('${r.emoji} — ${r.count} people'),
                  subtitle: Text(r.userIds.take(3).join(', ')),
                )),
          ],
        ],
      ),
    );
  }
}
