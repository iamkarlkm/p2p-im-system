import 'package:flutter/material.dart';
import '../../models/poi_model.dart';

/// POI评价列表组件
class POIReviewList extends StatelessWidget {
  final List<POIReview> reviews;
  final VoidCallback? onLoadMore;

  const POIReviewList({
    Key? key,
    required this.reviews,
    this.onLoadMore,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ...reviews.map((review) => POIReviewItem(review: review)),
        if (onLoadMore != null)
          TextButton(
            onPressed: onLoadMore,
            child: const Text('加载更多评价'),
          ),
      ],
    );
  }
}

/// 单个评价项
class POIReviewItem extends StatelessWidget {
  final POIReview review;

  const POIReviewItem({
    Key? key,
    required this.review,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12),
      decoration: BoxDecoration(
        border: Border(
          bottom: BorderSide(color: Colors.grey[200]!),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 用户信息
          Row(
            children: [
              // 头像
              CircleAvatar(
                radius: 18,
                backgroundImage: review.userAvatar != null
                    ? NetworkImage(review.userAvatar!)
                    : null,
                backgroundColor: Colors.grey[200],
                child: review.userAvatar == null
                    ? const Icon(Icons.person, size: 18, color: Colors.grey)
                    : null,
              ),
              const SizedBox(width: 10),
              
              // 用户名和评分
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      review.userName ?? '匿名用户',
                      style: const TextStyle(
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    const SizedBox(height: 2),
                    Row(
                      children: [
                        // 星级
                        ...List.generate(5, (index) {
                          return Icon(
                            index < review.rating.floor()
                                ? Icons.star
                                : Icons.star_border,
                            size: 12,
                            color: Colors.orange,
                          );
                        }),
                      ],
                    ),
                  ],
                ),
              ),
              
              // 时间
              Text(
                review.formattedDate,
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey[500],
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 10),
          
          // 评价内容
          if (review.content != null && review.content!.isNotEmpty)
            Text(
              review.content!,
              style: TextStyle(
                color: Colors.grey[800],
                height: 1.5,
              ),
            ),
          
          // 评价图片
          if (review.images != null && review.images!.isNotEmpty) ...[
            const SizedBox(height: 10),
            SizedBox(
              height: 80,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: review.images!.length,
                itemBuilder: (context, index) {
                  return Container(
                    margin: const EdgeInsets.only(right: 8),
                    width: 80,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(8),
                      image: DecorationImage(
                        image: NetworkImage(review.images![index]),
                        fit: BoxFit.cover,
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
          
          // 有用按钮
          if (review.helpfulCount != null) ...[
            const SizedBox(height: 10),
            Row(
              children: [
                Icon(
                  Icons.thumb_up_outlined,
                  size: 14,
                  color: Colors.grey[500],
                ),
                const SizedBox(width: 4),
                Text(
                  '有用 ${review.helpfulCount}',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[600],
                  ),
                ),
              ],
            ),
          ],
        ],
      ),
    );
  }
}

/// 营业时间展示组件
class BusinessHoursWidget extends StatelessWidget {
  final BusinessHours hours;

  const BusinessHoursWidget({
    Key? key,
    required this.hours,
  }) : super(key: key);

  static const List<String> _weekdays = [
    '周一',
    '周二',
    '周三',
    '周四',
    '周五',
    '周六',
    '周日',
  ];

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ...hours.weeklyHours.asMap().entries.map((entry) {
          final index = entry.key;
          final day = entry.value;
          final isToday = index == DateTime.now().weekday - 1;
          
          return Container(
            padding: const EdgeInsets.symmetric(vertical: 6),
            decoration: isToday
                ? BoxDecoration(
                    color: Colors.blue.withOpacity(0.05),
                    borderRadius: BorderRadius.circular(4),
                  )
                : null,
            child: Row(
              children: [
                SizedBox(
                  width: 50,
                  child: Text(
                    _weekdays[index],
                    style: TextStyle(
                      fontWeight: isToday ? FontWeight.bold : FontWeight.normal,
                      color: isToday ? Colors.blue[700] : Colors.grey[700],
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                if (day.isClosed)
                  const Text(
                    '休息',
                    style: TextStyle(color: Colors.grey),
                  )
                else
                  Text(
                    '${day.openTime} - ${day.closeTime}',
                    style: TextStyle(
                      color: Colors.grey[800],
                    ),
                  ),
                if (isToday) ...[
                  const Spacer(),
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 6,
                      vertical: 2,
                    ),
                    decoration: BoxDecoration(
                      color: hours.isOpenNow
                          ? Colors.green[100]
                          : Colors.red[100],
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Text(
                      hours.isOpenNow ? '营业中' : '已休息',
                      style: TextStyle(
                        fontSize: 11,
                        color: hours.isOpenNow
                            ? Colors.green[700]
                            : Colors.red[700],
                      ),
                    ),
                  ),
                ],
              ],
            ),
          );
        }),
        if (hours.specialHours != null) ...[
          const SizedBox(height: 8),
          Text(
            hours.specialHours!,
            style: TextStyle(
              fontSize: 12,
              color: Colors.grey[600],
              fontStyle: FontStyle.italic,
            ),
          ),
        ],
      ],
    );
  }
}

/// 评分星级展示
class RatingStars extends StatelessWidget {
  final double rating;
  final double size;
  final bool showValue;

  const RatingStars({
    Key? key,
    required this.rating,
    this.size = 16,
    this.showValue = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        ...List.generate(5, (index) {
          final value = index + 1;
          IconData icon;
          Color color;
          
          if (rating >= value) {
            icon = Icons.star;
            color = Colors.orange;
          } else if (rating >= value - 0.5) {
            icon = Icons.star_half;
            color = Colors.orange;
          } else {
            icon = Icons.star_border;
            color = Colors.grey[400]!;
          }
          
          return Icon(icon, size: size, color: color);
        }),
        if (showValue) ...[
          const SizedBox(width: 6),
          Text(
            rating.toStringAsFixed(1),
            style: TextStyle(
              fontSize: size * 0.875,
              fontWeight: FontWeight.bold,
              color: Colors.orange[700],
            ),
          ),
        ],
      ],
    );
  }
}

/// 价格等级指示器
class PriceLevelIndicator extends StatelessWidget {
  final double? priceLevel;
  final String? priceRange;

  const PriceLevelIndicator({
    Key? key,
    this.priceLevel,
    this.priceRange,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (priceRange != null) {
      return Container(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
        decoration: BoxDecoration(
          color: Colors.grey[100],
          borderRadius: BorderRadius.circular(4),
        ),
        child: Text(
          '人均 $priceRange',
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[700],
          ),
        ),
      );
    }

    if (priceLevel == null) return const SizedBox.shrink();

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        ...List.generate(4, (index) {
          return Text(
            '¥',
            style: TextStyle(
              fontSize: 12,
              color: index < priceLevel!.floor()
                  ? Colors.orange[700]
                  : Colors.grey[400],
              fontWeight: FontWeight.bold,
            ),
          );
        }),
      ],
    );
  }
}

/// 附近的人列表项
class NearbyUserItem extends StatelessWidget {
  final NearbyUser user;
  final VoidCallback? onTap;
  final VoidCallback? onMessageTap;

  const NearbyUserItem({
    Key? key,
    required this.user,
    this.onTap,
    this.onMessageTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.04),
              blurRadius: 8,
            ),
          ],
        ),
        child: Row(
          children: [
            // 头像
            Stack(
              children: [
                CircleAvatar(
                  radius: 28,
                  backgroundImage: user.avatar != null
                      ? NetworkImage(user.avatar!)
                      : null,
                  backgroundColor: Colors.grey[200],
                  child: user.avatar == null
                      ? const Icon(Icons.person, size: 28)
                      : null,
                ),
                if (user.isOnline)
                  Positioned(
                    right: 0,
                    bottom: 0,
                    child: Container(
                      width: 12,
                      height: 12,
                      decoration: BoxDecoration(
                        color: Colors.green,
                        shape: BoxShape.circle,
                        border: Border.all(color: Colors.white, width: 2),
                      ),
                    ),
                  ),
              ],
            ),
            const SizedBox(width: 12),
            
            // 信息
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    user.nickname ?? '用户${user.userId}',
                    style: const TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 2,
                        ),
                        decoration: BoxDecoration(
                          color: Colors.grey[100],
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Text(
                          user.formattedDistance,
                          style: TextStyle(
                            fontSize: 11,
                            color: Colors.grey[700],
                          ),
                        ),
                      ),
                      if (user.similarity != null) ...[
                        const SizedBox(width: 8),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.blue[50],
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: Text(
                            '${(user.similarity! * 100).toInt()}%匹配',
                            style: TextStyle(
                              fontSize: 11,
                              color: Colors.blue[700],
                            ),
                          ),
                        ),
                      ],
                    ],
                  ),
                ],
              ),
            ),
            
            // 发消息按钮
            IconButton(
              onPressed: onMessageTap,
              icon: const Icon(Icons.message, color: Colors.blue),
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
  static const String person = 'person';
  static const String message = 'message';
  static const String thumb_up_outlined = 'thumb_up_outlined';
  static const String star = 'star';
  static const String star_border = 'star_border';
  static const String star_half = 'star_half';
}
