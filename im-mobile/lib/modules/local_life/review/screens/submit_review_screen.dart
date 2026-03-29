import 'package:flutter/material.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';
import 'package:provider/provider.dart';
import '../providers/review_provider.dart';
import '../widgets/review_input_field.dart';
import '../widgets/review_photo_uploader.dart';
import '../widgets/rating_input_card.dart';

/// 提交评价页面
/// 支持多维度评分、图文评价、视频评价
class SubmitReviewScreen extends StatefulWidget {
  final String merchantId;
  final String merchantName;
  final String? orderId;

  const SubmitReviewScreen({
    Key? key,
    required this.merchantId,
    required this.merchantName,
    this.orderId,
  }) : super(key: key);

  @override
  State<SubmitReviewScreen> createState() => _SubmitReviewScreenState();
}

class _SubmitReviewScreenState extends State<SubmitReviewScreen> {
  double _overallRating = 5;
  double _tasteRating = 5;
  double _environmentRating = 5;
  double _serviceRating = 5;
  double _valueRating = 5;

  final TextEditingController _contentController = TextEditingController();
  final List<String> _selectedImages = [];
  String? _videoUrl;
  String? _videoCover;

  bool _isRecommended = true;
  bool _isAnonymous = false;
  double _consumeAmount = 0;

  final List<String> _availableTags = [
    '味道赞', '服务好', '环境优雅', '性价比高', '上菜快',
    '食材新鲜', '停车方便', '适合聚餐', '值得回购', '装修精美'
  ];
  final Set<String> _selectedTags = {};

  bool _isSubmitting = false;

  @override
  void dispose() {
    _contentController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      appBar: AppBar(
        title: const Text('发表评价'),
        centerTitle: true,
        elevation: 0,
        actions: [
          TextButton(
            onPressed: _isSubmitting ? null : _submitReview,
            child: _isSubmitting
                ? const SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                      valueColor: AlwaysStoppedAnimation(Colors.white),
                    ),
                  )
                : const Text(
                    '发布',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildMerchantHeader(),
            const SizedBox(height: 16),
            _buildOverallRatingCard(),
            const SizedBox(height: 16),
            _buildDimensionRatings(),
            const SizedBox(height: 16),
            _buildContentInput(),
            const SizedBox(height: 16),
            _buildPhotoUploader(),
            const SizedBox(height: 16),
            _buildTagSelector(),
            const SizedBox(height: 16),
            _buildConsumeInfo(),
            const SizedBox(height: 16),
            _buildOptionsSection(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildMerchantHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              color: Colors.grey[200],
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.store, size: 32, color: Colors.grey),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  widget.merchantName,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  '分享你的消费体验，帮助更多人',
                  style: TextStyle(
                    fontSize: 13,
                    color: Colors.grey[600],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOverallRatingCard() {
    return RatingInputCard(
      title: '总体评价',
      rating: _overallRating,
      onRatingUpdate: (rating) {
        setState(() => _overallRating = rating);
      },
      size: 40,
    );
  }

  Widget _buildDimensionRatings() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '分项评分',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 16),
          _buildDimensionRating('口味', _tasteRating, (v) => _tasteRating = v),
          _buildDimensionRating('环境', _environmentRating, (v) => _environmentRating = v),
          _buildDimensionRating('服务', _serviceRating, (v) => _serviceRating = v),
          _buildDimensionRating('性价比', _valueRating, (v) => _valueRating = v),
        ],
      ),
    );
  }

  Widget _buildDimensionRating(
    String label,
    double rating,
    Function(double) onUpdate,
  ) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          SizedBox(
            width: 60,
            child: Text(
              label,
              style: TextStyle(
                fontSize: 15,
                color: Colors.grey[700],
              ),
            ),
          ),
          RatingBar.builder(
            initialRating: rating,
            minRating: 1,
            direction: Axis.horizontal,
            allowHalfRating: true,
            itemCount: 5,
            itemSize: 28,
            itemBuilder: (context, _) =>
                const Icon(Icons.star, color: Colors.amber),
            onRatingUpdate: (value) {
              setState(() => onUpdate(value));
            },
          ),
          const SizedBox(width: 12),
          Text(
            rating.toStringAsFixed(1),
            style: TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w600,
              color: Colors.amber[700],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildContentInput() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '评价内容',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          ReviewInputField(
            controller: _contentController,
            hintText: '分享你的用餐体验，口味如何？服务怎么样？环境满意吗？',
            maxLength: 2000,
            minLines: 5,
          ),
        ],
      ),
    );
  }

  Widget _buildPhotoUploader() {
    return ReviewPhotoUploader(
      images: _selectedImages,
      videoUrl: _videoUrl,
      onImageAdded: (path) {
        setState(() => _selectedImages.add(path));
      },
      onImageRemoved: (index) {
        setState(() => _selectedImages.removeAt(index));
      },
      onVideoAdded: (url, cover) {
        setState(() {
          _videoUrl = url;
          _videoCover = cover;
        });
      },
      onVideoRemoved: () {
        setState(() {
          _videoUrl = null;
          _videoCover = null;
        });
      },
    );
  }

  Widget _buildTagSelector() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '添加标签',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 10,
            runSpacing: 10,
            children: _availableTags.map((tag) {
              final isSelected = _selectedTags.contains(tag);
              return GestureDetector(
                onTap: () {
                  setState(() {
                    if (isSelected) {
                      _selectedTags.remove(tag);
                    } else {
                      _selectedTags.add(tag);
                    }
                  });
                },
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  decoration: BoxDecoration(
                    color: isSelected ? Colors.orange[50] : Colors.grey[100],
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(
                      color: isSelected ? Colors.orange[300]! : Colors.transparent,
                    ),
                  ),
                  child: Text(
                    tag,
                    style: TextStyle(
                      fontSize: 14,
                      color: isSelected ? Colors.orange[700] : Colors.grey[700],
                      fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                    ),
                  ),
        ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildConsumeInfo() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '消费信息',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Text(
                '人均消费',
                style: TextStyle(
                  fontSize: 15,
                  color: Colors.grey[700],
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: TextField(
                  keyboardType: TextInputType.number,
                  decoration: InputDecoration(
                    hintText: '¥0.00',
                    prefixText: '¥',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                    contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                  ),
                  onChanged: (value) {
                    _consumeAmount = double.tryParse(value) ?? 0;
                  },
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildOptionsSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: [
          SwitchListTile(
            title: const Text('推荐这家店铺'),
            subtitle: const Text('你的评价将标记为推荐'),
            value: _isRecommended,
            onChanged: (value) => setState(() => _isRecommended = value),
            activeColor: Colors.orange,
          ),
          const Divider(),
          SwitchListTile(
            title: const Text('匿名评价'),
            subtitle: const Text('隐藏你的真实身份'),
            value: _isAnonymous,
            onChanged: (value) => setState(() => _isAnonymous = value),
            activeColor: Colors.orange,
          ),
        ],
      ),
    );
  }

  Future<void> _submitReview() async {
    if (_contentController.text.trim().length < 5) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('评价内容至少需要5个字')),
      );
      return;
    }

    setState(() => _isSubmitting = true);

    try {
      final provider = context.read<ReviewProvider>();
      await provider.submitReview(
        merchantId: widget.merchantId,
        orderId: widget.orderId,
        overallRating: _overallRating.toInt(),
        tasteRating: _tasteRating.toInt(),
        environmentRating: _environmentRating.toInt(),
        serviceRating: _serviceRating.toInt(),
        valueRating: _valueRating.toInt(),
        content: _contentController.text,
        images: _selectedImages,
        videoUrl: _videoUrl,
        tags: _selectedTags.toList(),
        consumeAmount: _consumeAmount,
        anonymous: _isAnonymous,
        recommended: _isRecommended,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('评价发布成功！')),
        );
        Navigator.pop(context, true);
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('发布失败: $e')),
      );
    } finally {
      setState(() => _isSubmitting = false);
    }
  }
}
