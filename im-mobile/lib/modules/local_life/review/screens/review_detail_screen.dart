import 'package:flutter/material.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';
import 'package:provider/provider.dart';
import '../models/review_model.dart';
import '../providers/review_provider.dart';
import '../widgets/review_photo_gallery.dart';
import '../widgets/review_video_player.dart';
import '../widgets/merchant_reply_card.dart';
import '../widgets/review_action_bar.dart';

/// 评价详情页面
/// 展示单条评价的完整信息，包括多维度评分、图文内容、商家回复等
class ReviewDetailScreen extends StatefulWidget {
  final String reviewId;
  final String? merchantName;

  const ReviewDetailScreen({
    Key? key,
    required this.reviewId,
    this.merchantName,
  }) : super(key: key);

  @override
  State<ReviewDetailScreen> createState() => _ReviewDetailScreenState();
}

class _ReviewDetailScreenState extends State<ReviewDetailScreen> {
  bool _isLoading = true;
  ReviewDetail? _review;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadReviewDetail();
  }

  Future<void> _loadReviewDetail() async {
    try {
      final provider = context.read<ReviewProvider>();
      final review = await provider.getReviewDetail(widget.reviewId);
      setState(() {
        _review = review;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = '加载失败: $e';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      appBar: AppBar(
        title: Text(widget.merchantName ?? '评价详情'),
        centerTitle: true,
        elevation: 0,
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_errorMessage!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadReviewDetail,
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    if (_review == null) {
      return const Center(child: Text('评价不存在'));
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildUserHeader(),
          const SizedBox(height: 16),
          _buildRatingSection(),
          const SizedBox(height: 16),
          _buildReviewContent(),
          const SizedBox(height: 16),
          if (_review!.images.isNotEmpty) _buildImageGallery(),
          if (_review!.video != null) _buildVideoSection(),
          const SizedBox(height: 16),
          _buildTagsSection(),
          const SizedBox(height: 16),
          _buildDiningInfo(),
          const SizedBox(height: 24),
          ReviewActionBar(
            review: _review!,
            onLike: _handleLike,
            onReply: _handleReply,
            onShare: _handleShare,
          ),
          const SizedBox(height: 24),
          if (_review!.merchantReply != null)
            MerchantReplyCard(reply: _review!.merchantReply!),
          const SizedBox(height: 24),
          _buildRepliesSection(),
        ],
      ),
    );
  }

  Widget _buildUserHeader() {
    return Row(
      children: [
        CircleAvatar(
          radius: 24,
          backgroundImage: _review!.isAnonymous
              ? const AssetImage('assets/images/default_avatar.png')
              : NetworkImage(_review!.userAvatar) as ImageProvider,
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                _review!.isAnonymous ? '匿名用户' : _review!.userName,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                _review!.reviewTime,
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey[600],
                ),
              ),
            ],
          ),
        ),
        if (_review!.isHighQuality)
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
            decoration: BoxDecoration(
              color: Colors.orange[50],
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: Colors.orange[300]!),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(Icons.verified, size: 14, color: Colors.orange[700]),
                const SizedBox(width: 4),
                Text(
                  '优质评价',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.orange[700],
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
          ),
      ],
    );
  }

  Widget _buildRatingSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              RatingBarIndicator(
                rating: _review!.overallRating.toDouble(),
                itemBuilder: (context, index) =>
                    const Icon(Icons.star, color: Colors.amber),
                itemCount: 5,
                itemSize: 24,
                direction: Axis.horizontal,
              ),
              const SizedBox(width: 8),
              Text(
                '${_review!.overallRating}.0',
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Colors.amber,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          _buildDimensionRating('口味', _review!.tasteRating),
          _buildDimensionRating('环境', _review!.environmentRating),
          _buildDimensionRating('服务', _review!.serviceRating),
          _buildDimensionRating('性价比', _review!.valueRating),
        ],
      ),
    );
  }

  Widget _buildDimensionRating(String label, int rating) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          Text(
            label,
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[700],
            ),
          ),
          const SizedBox(width: 12),
          RatingBarIndicator(
            rating: rating.toDouble(),
            itemBuilder: (context, index) =>
                Icon(Icons.star, color: Colors.amber[400]),
            itemCount: 5,
            itemSize: 16,
          ),
          const SizedBox(width: 8),
          Text(
            '$rating.0',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[600],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildReviewContent() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        _review!.content,
        style: const TextStyle(
          fontSize: 15,
          height: 1.6,
          color: Colors.black87,
        ),
      ),
    );
  }

  Widget _buildImageGallery() {
    return ReviewPhotoGallery(
      images: _review!.images,
      onImageTap: (index) {
        // 打开图片预览
        Navigator.pushNamed(
          context,
          '/image_preview',
          arguments: {
            'images': _review!.images,
            'initialIndex': index,
          },
        );
      },
    );
  }

  Widget _buildVideoSection() {
    return ReviewVideoPlayer(
      videoUrl: _review!.video!.videoUrl,
      coverUrl: _review!.video!.coverUrl,
      duration: _review!.video!.duration,
    );
  }

  Widget _buildTagsSection() {
    if (_review!.tags.isEmpty) return const SizedBox.shrink();

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: _review!.tags.map((tag) {
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
            color: Colors.grey[100],
            borderRadius: BorderRadius.circular(16),
          ),
          child: Text(
            tag,
            style: TextStyle(
              fontSize: 13,
              color: Colors.grey[700],
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildDiningInfo() {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Icon(Icons.restaurant, size: 16, color: Colors.grey[600]),
          const SizedBox(width: 8),
          Text(
            '就餐日期: ${_review!.diningDate}',
            style: TextStyle(
              fontSize: 13,
              color: Colors.grey[600],
            ),
          ),
          const Spacer(),
          if (_review!.perCapita != null)
            Text(
              '人均 ¥${_review!.perCapita}',
              style: TextStyle(
                fontSize: 13,
                color: Colors.grey[600],
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildRepliesSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '评论 (${_review!.replyCount})',
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 16),
          if (_review!.replyCount > 0)
            TextButton(
              onPressed: () {
                Navigator.pushNamed(
                  context,
                  '/review_replies',
                  arguments: {'reviewId': widget.reviewId},
                );
              },
              child: const Text('查看全部评论'),
            )
          else
            const Center(
              child: Text(
                '暂无评论，快来抢沙发吧',
                style: TextStyle(color: Colors.grey),
              ),
            ),
        ],
      ),
    );
  }

  Future<void> _handleLike() async {
    try {
      final provider = context.read<ReviewProvider>();
      await provider.likeReview(widget.reviewId, !_review!.hasLiked);
      _loadReviewDetail();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('操作失败: $e')),
      );
    }
  }

  void _handleReply() {
    Navigator.pushNamed(
      context,
      '/reply_review',
      arguments: {'reviewId': widget.reviewId},
    );
  }

  void _handleShare() {
    // 分享评价
  }
}
