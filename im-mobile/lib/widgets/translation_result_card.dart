import 'package:flutter/material.dart';
import '../services/translation_service.dart';

class TranslationResultCard extends StatelessWidget {
  final TranslationResult result;
  final VoidCallback? onCopy;
  final VoidCallback? onSpeak;
  final bool isOffline;
  
  const TranslationResultCard({
    Key? key,
    required this.result,
    this.onCopy,
    this.onSpeak,
    this.isOffline = false,
  }) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          border: isOffline
              ? Border.all(color: Colors.orange.withOpacity(0.5), width: 2)
              : null,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: Colors.blue.withOpacity(0.1),
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(12),
                  topRight: Radius.circular(12),
                ),
              ),
              child: Row(
                children: [
                  const Icon(Icons.translate, size: 20, color: Colors.blue),
                  const SizedBox(width: 8),
                  const Text(
                    '翻译结果',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.blue,
                    ),
                  ),
                  const Spacer(),
                  if (isOffline)
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: Colors.orange.withOpacity(0.2),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.offline_bolt, size: 14, color: Colors.orange),
                          SizedBox(width: 4),
                          Text(
                            '离线',
                            style: TextStyle(
                              fontSize: 12,
                              color: Colors.orange,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ],
                      ),
                    ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Text(
                result.translatedText,
                style: const TextStyle(
                  fontSize: 16,
                  height: 1.5,
                ),
              ),
            ),
            const Divider(height: 1),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.copy),
                    onPressed: onCopy,
                    tooltip: '复制',
                  ),
                  IconButton(
                    icon: const Icon(Icons.volume_up),
                    onPressed: onSpeak,
                    tooltip: '朗读',
                  ),
                  const Spacer(),
                  IconButton(
                    icon: const Icon(Icons.share),
                    onPressed: () {},
                    tooltip: '分享',
                  ),
                  IconButton(
                    icon: const Icon(Icons.favorite_border),
                    onPressed: () {},
                    tooltip: '收藏',
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
