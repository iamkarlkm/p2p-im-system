import 'package:flutter/material.dart';

class AiRecommendationBar extends StatelessWidget {
  final Function(String) onRecommendationTap;

  const AiRecommendationBar({
    super.key,
    required this.onRecommendationTap,
  });

  final List<Map<String, dynamic>> _recommendations = const [
    {
      'icon': Icons.edit,
      'text': '帮我写一段',
      'color': Colors.blue,
    },
    {
      'icon': Icons.translate,
      'text': '翻译这段话',
      'color': Colors.green,
    },
    {
      'icon': Icons.code,
      'text': '写个代码',
      'color': Colors.orange,
    },
    {
      'icon': Icons.summarize,
      'text': '总结一下',
      'color': Colors.purple,
    },
    {
      'icon': Icons.lightbulb_outline,
      'text': '给我建议',
      'color': Colors.teal,
    },
    {
      'icon': Icons.calculate,
      'text': '计算一下',
      'color': Colors.red,
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12),
      decoration: BoxDecoration(
        color: Colors.grey.shade50,
        border: Border(
          top: BorderSide(color: Colors.grey.shade200),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Text(
              '快速开始',
              style: TextStyle(
                fontSize: 13,
                fontWeight: FontWeight.w500,
                color: Colors.grey.shade600,
              ),
            ),
          ),
          const SizedBox(height: 8),
          SizedBox(
            height: 40,
            child: ListView.separated(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _recommendations.length,
              separatorBuilder: (_, __) => const SizedBox(width: 8),
              itemBuilder: (context, index) {
                final item = _recommendations[index];
                return _buildChip(
                  icon: item['icon'] as IconData,
                  text: item['text'] as String,
                  color: item['color'] as Color,
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildChip({
    required IconData icon,
    required String text,
    required Color color,
  }) {
    return ActionChip(
      avatar: Icon(icon, size: 16, color: color),
      label: Text(text),
      backgroundColor: color.withOpacity(0.1),
      side: BorderSide(color: color.withOpacity(0.3)),
      labelStyle: TextStyle(color: color, fontSize: 13),
      padding: const EdgeInsets.symmetric(horizontal: 4),
      onPressed: () => onRecommendationTap(text),
    );
  }
}
