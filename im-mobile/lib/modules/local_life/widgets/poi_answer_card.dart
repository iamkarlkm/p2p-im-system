import 'package:flutter/material.dart';

/// POI问答卡片组件
/// 展示单个POI的详细信息，支持导航和收藏
/// 
/// Author: IM Development Team
/// Since: 2026-03-28
class PoiAnswerCard extends StatelessWidget {
  final dynamic poi;
  final VoidCallback onTap;
  final VoidCallback onNavigate;
  final VoidCallback? onFavorite;
  final bool isFavorite;

  const PoiAnswerCard({
    Key? key,
    required this.poi,
    required this.onTap,
    required this.onNavigate,
    this.onFavorite,
    this.isFavorite = false,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8),
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 图片区域
            _buildImageSection(),
            
            // 信息区域
            Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // 标题行
                  Row(
                    children: [
                      Expanded(
                        child: Text(
                          poi.name ?? '未知商户',
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      if (onFavorite != null)
                        IconButton(
                          icon: Icon(
                            isFavorite ? Icons.favorite : Icons.favorite_border,
                            color: isFavorite ? Colors.red : Colors.grey,
                          ),
                          onPressed: onFavorite,
                        ),
                    ],
                  ),
                  
                  const SizedBox(height: 8),
                  
                  // 评分和距离
                  Row(
                    children: [
                      // 评分
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 2,
                        ),
                        decoration: BoxDecoration(
                          color: Colors.amber[50],
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(
                              Icons.star,
                              size: 14,
                              color: Colors.amber[600],
                            ),
                            const SizedBox(width: 2),
                            Text(
                              '${poi.rating ?? 0.0}',
                              style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.bold,
                                color: Colors.amber[800],
                              ),
                            ),
                            if (poi.ratingCount != null)
                              Text(
                                ' (${poi.ratingCount})',
                                style: TextStyle(
                                  fontSize: 10,
                                  color: Colors.grey[600],
                                ),
                              ),
                          ],
                        ),
                      ),
                      
                      const SizedBox(width: 8),
                      
                      // 距离
                      if (poi.distance != null)
                        Text(
                          '${_formatDistance(poi.distance)}',
                          style: TextStyle(
                            fontSize: 13,
                            color: Colors.grey[600],
                          ),
                        ),
                      
                      const Spacer(),
                      
                      // 人均消费
                      if (poi.avgPrice != null)
                        Text(
                          '¥${poi.avgPrice}/人',
                          style: TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                            color: Colors.orange[700],
                          ),
                        ),
                    ],
                  ),
                  
                  const SizedBox(height: 8),
                  
                  // 分类和标签
                  Wrap(
                    spacing: 6,
                    runSpacing: 6,
                    children: [
                      // 主分类
                      if (poi.category != null)
                        Chip(
                          label: Text(
                            poi.category,
                            style: const TextStyle(fontSize: 11),
                          ),
                          padding: EdgeInsets.zero,
                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                          backgroundColor: Colors.blue[50],
                        ),
                      
                      // 营业状态
                      if (poi.isOpenNow != null)
                        Chip(
                          label: Text(
                            poi.isOpenNow ? '营业中' : '已打烊',
                            style: TextStyle(
                              fontSize: 11,
                              color: poi.isOpenNow ? Colors.green[700] : Colors.grey[700],
                            ),
                          ),
                          padding: EdgeInsets.zero,
                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                          backgroundColor: poi.isOpenNow ? Colors.green[50] : Colors.grey[200],
                        ),
                      
                      // 特色标签
                      if (poi.tags != null)
                        ...poi.tags.take(3).map((tag) => Chip(
                          label: Text(
                            tag,
                            style: const TextStyle(fontSize: 11),
                          ),
                          padding: EdgeInsets.zero,
                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                        )),
                    ],
                  ),
                  
                  const SizedBox(height: 8),
                  
                  // 地址
                  if (poi.address != null)
                    Row(
                      children: [
                        Icon(
                          Icons.location_on,
                          size: 14,
                          color: Colors.grey[500],
                        ),
                        const SizedBox(width: 4),
                        Expanded(
                          child: Text(
                            poi.address,
                            style: TextStyle(
                              fontSize: 12,
                              color: Colors.grey[600],
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      ],
                    ),
                  
                  const SizedBox(height: 12),
                  
                  // 推荐理由
                  if (poi.recommendReason != null)
                    Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: Colors.blue[50],
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            Icons.thumb_up,
                            size: 14,
                            color: Colors.blue[600],
                          ),
                          const SizedBox(width: 4),
                          Expanded(
                            child: Text(
                              poi.recommendReason,
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.blue[800],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  
                  const SizedBox(height: 12),
                  
                  // 操作按钮
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: onNavigate,
                          icon: const Icon(Icons.navigation, size: 18),
                          label: const Text('导航'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.blue,
                            foregroundColor: Colors.white,
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: onTap,
                          icon: const Icon(Icons.info_outline, size: 18),
                          label: const Text('详情'),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImageSection() {
    if (poi.images == null || poi.images.isEmpty) {
      return Container(
        height: 160,
        decoration: BoxDecoration(
          color: Colors.grey[300],
          borderRadius: const BorderRadius.vertical(
            top: Radius.circular(12),
          ),
        ),
        child: const Center(
          child: Icon(Icons.image, size: 48, color: Colors.grey),
        ),
      );
    }

    return ClipRRect(
      borderRadius: const BorderRadius.vertical(
        top: Radius.circular(12),
      ),
      child: Image.network(
        poi.images[0],
        height: 160,
        width: double.infinity,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) {
          return Container(
            height: 160,
            color: Colors.grey[300],
            child: const Center(
              child: Icon(Icons.broken_image, color: Colors.grey),
            ),
          );
        },
      ),
    );
  }

  String _formatDistance(int? distance) {
    if (distance == null) return '';
    if (distance < 1000) {
      return '${distance}m';
    } else {
      return '${(distance / 1000).toStringAsFixed(1)}km';
    }
  }
}
