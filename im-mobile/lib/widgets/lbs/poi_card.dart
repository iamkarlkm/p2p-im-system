import 'package:flutter/material.dart';
import '../../models/poi_model.dart';

/// POI卡片组件
/// 用于列表中展示POI信息
class POICard extends StatelessWidget {
  final POI poi;
  final VoidCallback? onTap;
  final VoidCallback? onFavoriteTap;

  const POICard({
    Key? key,
    required this.poi,
    this.onTap,
    this.onFavoriteTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.04),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 图片区域
            if (poi.thumbnail != null || (poi.images?.isNotEmpty ?? false))
              _buildImageSection()
            else
              _buildPlaceholderImage(),
            
            // 信息区域
            Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // 标题行
                  Row(
                    children: [
                      // 分类图标
                      Container(
                        padding: const EdgeInsets.all(4),
                        decoration: BoxDecoration(
                          color: _getCategoryColor().withOpacity(0.1),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Icon(
                          _getCategoryIcon(),
                          size: 14,
                          color: _getCategoryColor(),
                        ),
                      ),
                      const SizedBox(width: 8),
                      
                      // 名称
                      Expanded(
                        child: Text(
                          poi.name,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      
                      // 收藏按钮
                      if (onFavoriteTap != null)
                        GestureDetector(
                          onTap: onFavoriteTap,
                          child: Icon(
                            poi.isFavorite 
                                ? Icons.favorite 
                                : Icons.favorite_border,
                            size: 20,
                            color: poi.isFavorite ? Colors.red : Colors.grey,
                          ),
                        ),
                    ],
                  ),
                  
                  const SizedBox(height: 8),
                  
                  // 评分和距离
                  Row(
                    children: [
                      // 评分
                      if (poi.rating != null) ...[
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6, 
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.orange[50],
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(
                                Icons.star, 
                                size: 12, 
                                color: Colors.orange[700],
                              ),
                              const SizedBox(width: 2),
                              Text(
                                poi.rating!.toStringAsFixed(1),
                                style: TextStyle(
                                  fontSize: 11,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.orange[700],
                                ),
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(width: 8),
                      ],
                      
                      // 评价数
                      if (poi.reviewCount != null)
                        Text(
                          '${poi.reviewCount}条评价',
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey[600],
                          ),
                        ),
                      
                      const Spacer(),
                      
                      // 距离
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 8, 
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: Colors.grey[100],
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          poi.formattedDistance,
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey[700],
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                    ],
                  ),
                  
                  const SizedBox(height: 8),
                  
                  // 地址
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
                          poi.address ?? '暂无地址',
                          style: TextStyle(
                            fontSize: 13,
                            color: Colors.grey[600],
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                  
                  // 标签
                  if (poi.tags != null && poi.tags!.isNotEmpty) ...[
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 6,
                      children: poi.tags!.take(3).map((tag) {
                        return Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6, 
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.grey[100],
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            tag,
                            style: TextStyle(
                              fontSize: 11,
                              color: Colors.grey[700],
                            ),
                          ),
                        );
                      }).toList(),
                    ),
                  ],
                  
                  // 营业状态
                  if (poi.isOpenNow != null) ...[
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Container(
                          width: 6,
                          height: 6,
                          decoration: BoxDecoration(
                            color: poi.isOpenNow! ? Colors.green : Colors.red,
                            shape: BoxShape.circle,
                          ),
                        ),
                        const SizedBox(width: 6),
                        Text(
                          poi.isOpenNow! ? '营业中' : '已休息',
                          style: TextStyle(
                            fontSize: 12,
                            color: poi.isOpenNow! 
                                ? Colors.green[700] 
                                : Colors.red[700],
                          ),
                        ),
                        if (poi.businessHours != null) ...[
                          const SizedBox(width: 8),
                          Text(
                            poi.businessHours!.todayHours,
                            style: TextStyle(
                              fontSize: 12,
                              color: Colors.grey[600],
                            ),
                          ),
                        ],
                      ],
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImageSection() {
    return ClipRRect(
      borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
      child: AspectRatio(
        aspectRatio: 16 / 9,
        child: Image.network(
          poi.thumbnail ?? poi.images!.first,
          fit: BoxFit.cover,
          errorBuilder: (_, __, ___) => _buildPlaceholderImage(),
        ),
      ),
    );
  }

  Widget _buildPlaceholderImage() {
    return Container(
      height: 120,
      decoration: BoxDecoration(
        color: Colors.grey[200],
        borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
      ),
      child: Center(
        child: Icon(Icons.image, size: 48, color: Colors.grey[400]),
      ),
    );
  }

  Color _getCategoryColor() {
    // 实际使用时应根据category返回对应颜色
    switch (poi.category) {
      case POICategory.restaurant:
        return Colors.orange;
      case POICategory.shopping:
        return Colors.pink;
      case POICategory.entertainment:
        return Colors.purple;
      case POICategory.hotel:
        return Colors.blue;
      case POICategory.scenic:
        return Colors.green;
      default:
        return Colors.grey;
    }
  }

  IconData _getCategoryIcon() {
    // 实际使用时应根据category返回对应图标
    return Icons.place;
  }
}

/// POI列表项（简洁版）
class POIListTile extends StatelessWidget {
  final POI poi;
  final VoidCallback? onTap;

  const POIListTile({
    Key? key,
    required this.poi,
    this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Container(
        width: 48,
        height: 48,
        decoration: BoxDecoration(
          color: Colors.grey[200],
          borderRadius: BorderRadius.circular(8),
          image: poi.thumbnail != null
              ? DecorationImage(
                  image: NetworkImage(poi.thumbnail!),
                  fit: BoxFit.cover,
                )
              : null,
        ),
        child: poi.thumbnail == null
            ? Icon(Icons.place, color: Colors.grey[400])
            : null,
      ),
      title: Text(
        poi.name,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      subtitle: Row(
        children: [
          if (poi.rating != null) ...[
            Icon(Icons.star, size: 12, color: Colors.orange[700]),
            const SizedBox(width: 2),
            Text(poi.rating!.toStringAsFixed(1)),
            const SizedBox(width: 8),
          ],
          Text(
            poi.formattedDistance,
            style: TextStyle(color: Colors.grey[600]),
          ),
        ],
      ),
      trailing: Text(
        poi.category.label,
        style: TextStyle(fontSize: 12, color: Colors.grey[600]),
      ),
      onTap: onTap,
    );
  }
}

/// POI预览卡片（底部弹窗用）
class POIPreviewCard extends StatelessWidget {
  final POI poi;
  final VoidCallback? onTap;

  const POIPreviewCard({
    Key? key,
    required this.poi,
    this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 拖动条
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.grey[300],
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
            const SizedBox(height: 16),
            
            Row(
              children: [
                // 缩略图
                if (poi.thumbnail != null)
                  ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.network(
                      poi.thumbnail!,
                      width: 80,
                      height: 80,
                      fit: BoxFit.cover,
                    ),
                  )
                else
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      color: Colors.grey[200],
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Icon(Icons.place, color: Colors.grey[400]),
                  ),
                const SizedBox(width: 12),
                
                // 信息
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        poi.name,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        poi.category.label,
                        style: TextStyle(
                          fontSize: 13,
                          color: Colors.grey[600],
                        ),
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          if (poi.rating != null) ...[
                            Icon(Icons.star, size: 14, color: Colors.orange),
                            const SizedBox(width: 2),
                            Text(poi.rating!.toStringAsFixed(1)),
                            const SizedBox(width: 8),
                          ],
                          Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 8, 
                              vertical: 2,
                            ),
                            decoration: BoxDecoration(
                              color: Colors.grey[100],
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: Text(
                              poi.formattedDistance,
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.grey[700],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                
                // 箭头
                const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

// 占位符
// ignore: constant_identifier_names
class Icons {
  static const String favorite = 'favorite';
  static const String favorite_border = 'favorite_border';
  static const String star = 'star';
  static const String location_on = 'location_on';
  static const String image = 'image';
  static const String place = 'place';
  static const String arrow_forward_ios = 'arrow_forward_ios';
}
