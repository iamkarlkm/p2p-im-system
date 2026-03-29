import 'package:flutter/material.dart';
import '../../models/poi_model.dart';

/// 分类筛选组件
/// 横向滚动的POI分类选择器
class CategoryFilter extends StatelessWidget {
  final POICategory? selectedCategory;
  final ValueChanged<POICategory?> onCategorySelected;

  const CategoryFilter({
    Key? key,
    this.selectedCategory,
    required this.onCategorySelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
      itemCount: POICategory.values.length + 1, // +1 for "全部"
      itemBuilder: (context, index) {
        if (index == 0) {
          return _buildCategoryItem(
            context,
            label: '全部',
            icon: Icons.apps,
            color: Colors.blue,
            isSelected: selectedCategory == null,
            onTap: () => onCategorySelected(null),
          );
        }
        
        final category = POICategory.values[index - 1];
        return _buildCategoryItem(
          context,
          label: category.label,
          icon: _getIconForCategory(category),
          color: _getColorForCategory(category),
          isSelected: selectedCategory == category,
          onTap: () => onCategorySelected(category),
        );
      },
    );
  }

  Widget _buildCategoryItem(
    BuildContext context, {
    required String label,
    required IconData icon,
    required Color color,
    required bool isSelected,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 4),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.15) : Colors.grey[100],
          borderRadius: BorderRadius.circular(20),
          border: isSelected 
              ? Border.all(color: color, width: 1.5)
              : null,
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon,
              size: 16,
              color: isSelected ? color : Colors.grey[600],
            ),
            const SizedBox(width: 6),
            Text(
              label,
              style: TextStyle(
                fontSize: 13,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                color: isSelected ? color : Colors.grey[700],
              ),
            ),
          ],
        ),
      ),
    );
  }

  IconData _getIconForCategory(POICategory category) {
    switch (category) {
      case POICategory.restaurant:
        return Icons.restaurant;
      case POICategory.shopping:
        return Icons.shopping_bag;
      case POICategory.entertainment:
        return Icons.movie;
      case POICategory.hotel:
        return Icons.hotel;
      case POICategory.scenic:
        return Icons.landscape;
      case POICategory.transport:
        return Icons.directions_bus;
      case POICategory.life:
        return Icons.local_service;
      case POICategory.medical:
        return Icons.local_hospital;
      case POICategory.education:
        return Icons.school;
      case POICategory.sports:
        return Icons.fitness_center;
      default:
        return Icons.place;
    }
  }

  Color _getColorForCategory(POICategory category) {
    switch (category) {
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
      case POICategory.transport:
        return Colors.indigo;
      case POICategory.life:
        return Colors.teal;
      case POICategory.medical:
        return Colors.red;
      case POICategory.education:
        return Colors.amber;
      case POICategory.sports:
        return Colors.deepOrange;
      default:
        return Colors.grey;
    }
  }
}

/// 场景推荐卡片
/// 用于展示场景化推荐（午餐、晚餐等）
class ScenarioRecommendationCard extends StatelessWidget {
  final String scenario;
  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
  final List<POI> pois;
  final VoidCallback? onTap;
  final Function(POI)? onPOITap;

  const ScenarioRecommendationCard({
    Key? key,
    required this.scenario,
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.pois,
    this.onTap,
    this.onPOITap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
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
          // 标题栏
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(
                    color: color.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(icon, color: color, size: 24),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        subtitle,
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ),
                TextButton(
                  onPressed: onTap,
                  child: const Text('查看全部'),
                ),
              ],
            ),
          ),
          
          // POI列表
          SizedBox(
            height: 180,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 12),
              itemCount: pois.take(5).length,
              itemBuilder: (context, index) {
                final poi = pois[index];
                return _buildScenarioPOICard(poi);
              },
            ),
          ),
          
          const SizedBox(height: 12),
        ],
      ),
    );
  }

  Widget _buildScenarioPOICard(POI poi) {
    return GestureDetector(
      onTap: () => onPOITap?.call(poi),
      child: Container(
        width: 140,
        margin: const EdgeInsets.symmetric(horizontal: 4),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          color: Colors.grey[50],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 图片
            ClipRRect(
              borderRadius: const BorderRadius.vertical(
                top: Radius.circular(12),
              ),
              child: poi.thumbnail != null
                  ? Image.network(
                      poi.thumbnail!,
                      height: 90,
                      width: double.infinity,
                      fit: BoxFit.cover,
                    )
                  : Container(
                      height: 90,
                      color: Colors.grey[200],
                      child: Center(
                        child: Icon(Icons.image, color: Colors.grey[400]),
                      ),
                    ),
            ),
            
            // 信息
            Padding(
              padding: const EdgeInsets.all(8),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    poi.name,
                    style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      if (poi.rating != null) ...[
                        Icon(Icons.star, size: 10, color: Colors.orange[700]),
                        const SizedBox(width: 2),
                        Text(
                          poi.rating!.toStringAsFixed(1),
                          style: TextStyle(
                            fontSize: 11,
                            color: Colors.grey[700],
                          ),
                        ),
                        const SizedBox(width: 4),
                      ],
                      Text(
                        poi.formattedDistance,
                        style: TextStyle(
                          fontSize: 11,
                          color: Colors.grey[600],
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
}

/// 营业状态标签
class BusinessStatusBadge extends StatelessWidget {
  final bool isOpen;
  final String? hoursText;

  const BusinessStatusBadge({
    Key? key,
    required this.isOpen,
    this.hoursText,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: isOpen ? Colors.green[50] : Colors.red[50],
        borderRadius: BorderRadius.circular(4),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 6,
            height: 6,
            decoration: BoxDecoration(
              color: isOpen ? Colors.green : Colors.red,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 6),
          Text(
            isOpen ? '营业中' : '已休息',
            style: TextStyle(
              fontSize: 12,
              color: isOpen ? Colors.green[700] : Colors.red[700],
              fontWeight: FontWeight.w500,
            ),
          ),
          if (hoursText != null) ...[
            const SizedBox(width: 8),
            Text(
              hoursText!,
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey[600],
              ),
            ),
          ],
        ],
      ),
    );
  }
}

/// 距离指示器
class DistanceIndicator extends StatelessWidget {
  final double distance; // 米
  final bool showIcon;

  const DistanceIndicator({
    Key? key,
    required this.distance,
    this.showIcon = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String text;
    if (distance < 1000) {
      text = '${distance.toInt()}m';
    } else {
      text = '${(distance / 1000).toStringAsFixed(1)}km';
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (showIcon) ...[
            Icon(Icons.near_me, size: 12, color: Colors.grey[600]),
            const SizedBox(width: 4),
          ],
          Text(
            text,
            style: TextStyle(
              fontSize: 12,
              color: Colors.grey[700],
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }
}

// 占位符
// ignore: constant_identifier_names
class Icons {
  static const String apps = 'apps';
  static const String restaurant = 'restaurant';
  static const String shopping_bag = 'shopping_bag';
  static const String movie = 'movie';
  static const String hotel = 'hotel';
  static const String landscape = 'landscape';
  static const String directions_bus = 'directions_bus';
  static const String local_service = 'local_service';
  static const String local_hospital = 'local_hospital';
  static const String school = 'school';
  static const String fitness_center = 'fitness_center';
  static const String place = 'place';
  static const String image = 'image';
  static const String star = 'star';
  static const String near_me = 'near_me';
}
