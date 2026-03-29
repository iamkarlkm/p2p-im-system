import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../models/poi_model.dart';
import '../../services/lbs/poi_service.dart';
import '../../services/lbs/location_service.dart';
import '../../widgets/lbs/poi_review_list.dart';
import '../../widgets/lbs/business_hours_widget.dart';

/// POI详情页面
/// 展示商家/地点的详细信息、评价、营业时间等
class POIDetailPage extends StatefulWidget {
  final String poiId;
  
  const POIDetailPage({
    Key? key,
    required this.poiId,
  }) : super(key: key);

  @override
  State<POIDetailPage> createState() => _POIDetailPageState();
}

class _POIDetailPageState extends State<POIDetailPage> {
  late POIService _poiService;
  POI? _poi;
  bool _isLoading = true;
  String? _error;
  
  // 评价列表
  List<POIReview> _reviews = [];
  bool _isLoadingReviews = false;

  @override
  void initState() {
    super.initState();
    _poiService = POIService();
    _loadPOIDetail();
  }

  Future<void> _loadPOIDetail() async {
    setState(() => _isLoading = true);
    
    try {
      final poi = await _poiService.getPOIDetail(widget.poiId);
      if (poi != null) {
        setState(() => _poi = poi);
        _loadReviews();
      } else {
        setState(() => _error = 'POI不存在');
      }
    } catch (e) {
      setState(() => _error = '加载失败: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _loadReviews() async {
    setState(() => _isLoadingReviews = true);
    
    try {
      final reviews = await _poiService.getPOIReviews(widget.poiId);
      setState(() => _reviews = reviews);
    } catch (e) {
      // 评价加载失败不影响主页面
    } finally {
      setState(() => _isLoadingReviews = false);
    }
  }

  Future<void> _toggleFavorite() async {
    if (_poi == null) return;
    
    if (_poi!.isFavorite) {
      await _poiService.unfavoritePOI(_poi!.id);
    } else {
      await _poiService.favoritePOI(_poi!.id);
    }
    
    // 刷新详情
    _loadPOIDetail();
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (_error != null || _poi == null) {
      return Scaffold(
        appBar: AppBar(),
        body: Center(child: Text(_error ?? '加载失败')),
      );
    }

    final poi = _poi!;
    
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          // 顶部图片区域
          _buildImageHeader(poi),
          
          // 基本信息
          _buildBasicInfo(poi),
          
          // 操作按钮
          _buildActionButtons(poi),
          
          // 营业时间
          if (poi.businessHours != null)
            _buildBusinessHours(poi.businessHours!),
          
          // 标签
          if (poi.tags != null && poi.tags!.isNotEmpty)
            _buildTags(poi.tags!),
          
          // 简介
          if (poi.description != null)
            _buildDescription(poi.description!),
          
          // 地图
          _buildMap(poi),
          
          // 评价
          _buildReviewsSection(),
          
          const SliverToBoxAdapter(child: SizedBox(height: 32)),
        ],
      ),
      bottomNavigationBar: _buildBottomBar(poi),
    );
  }

  Widget _buildImageHeader(POI poi) {
    return SliverAppBar(
      expandedHeight: 240,
      pinned: true,
      flexibleSpace: FlexibleSpaceBar(
        background: poi.images != null && poi.images!.isNotEmpty
            ? PageView.builder(
                itemCount: poi.images!.length,
                itemBuilder: (context, index) {
                  return Image.network(
                    poi.images![index],
                    fit: BoxFit.cover,
                    errorBuilder: (_, __, ___) => _buildPlaceholderImage(),
                  );
                },
              )
            : _buildPlaceholderImage(),
      ),
      actions: [
        IconButton(
          icon: Icon(
            poi.isFavorite ? Icons.favorite : Icons.favorite_border,
            color: poi.isFavorite ? Colors.red : Colors.white,
          ),
          onPressed: _toggleFavorite,
        ),
        IconButton(
          icon: const Icon(Icons.share, color: Colors.white),
          onPressed: () => _sharePOI(poi),
        ),
      ],
    );
  }

  Widget _buildPlaceholderImage() {
    return Container(
      color: Colors.grey[300],
      child: const Center(
        child: Icon(Icons.image, size: 64, color: Colors.grey),
      ),
    );
  }

  Widget _buildBasicInfo(POI poi) {
    return SliverToBoxAdapter(
      child: Container(
        padding: const EdgeInsets.all(16),
        color: Colors.white,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 分类标签
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.blue[50],
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    poi.category.label,
                    style: TextStyle(
                      color: Colors.blue[700],
                      fontSize: 12,
                    ),
                  ),
                ),
                if (poi.subCategory != null) ...[
                  const SizedBox(width: 8),
                  Text(
                    poi.subCategory!,
                    style: TextStyle(color: Colors.grey[600], fontSize: 12),
                  ),
                ],
              ],
            ),
            
            const SizedBox(height: 12),
            
            // 名称
            Text(
              poi.name,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            
            const SizedBox(height: 8),
            
            // 评分和距离
            Row(
              children: [
                // 评分
                if (poi.rating != null) ...[
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(
                      color: Colors.orange[100],
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(Icons.star, size: 14, color: Colors.orange[700]),
                        const SizedBox(width: 2),
                        Text(
                          poi.rating!.toStringAsFixed(1),
                          style: TextStyle(
                            color: Colors.orange[700],
                            fontWeight: FontWeight.bold,
                            fontSize: 12,
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
                    style: TextStyle(color: Colors.grey[600], fontSize: 13),
                  ),
                
                const Spacer(),
                
                // 距离
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.grey[100],
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    poi.formattedDistance,
                    style: TextStyle(
                      color: Colors.grey[700],
                      fontSize: 12,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ],
            ),
            
            const SizedBox(height: 12),
            
            // 地址
            Row(
              children: [
                Icon(Icons.location_on, size: 16, color: Colors.grey[500]),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    poi.address ?? '暂无地址信息',
                    style: TextStyle(color: Colors.grey[700]),
                  ),
                ),
              ],
            ),
            
            // 电话
            if (poi.phone != null) ...[
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(Icons.phone, size: 16, color: Colors.grey[500]),
                  const SizedBox(width: 8),
                  Text(
                    poi.phone!,
                    style: TextStyle(color: Colors.blue[700]),
                  ),
                ],
              ),
            ],
            
            // 价格
            if (poi.priceRange != null) ...[
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(Icons.attach_money, size: 16, color: Colors.grey[500]),
                  const SizedBox(width: 8),
                  Text(
                    '人均 ${poi.priceRange}',
                    style: TextStyle(color: Colors.grey[700]),
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildActionButtons(POI poi) {
    return SliverToBoxAdapter(
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border(
            bottom: BorderSide(color: Colors.grey[200]!),
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            _buildActionButton(
              icon: Icons.directions,
              label: '导航',
              onTap: () => _openNavigation(poi),
            ),
            _buildActionButton(
              icon: Icons.phone,
              label: '电话',
              onTap: poi.phone != null ? () => _makeCall(poi.phone!) : null,
            ),
            _buildActionButton(
              icon: Icons.message,
              label: '咨询',
              onTap: () => _startChat(poi),
            ),
            _buildActionButton(
              icon: Icons.favorite_border,
              label: '收藏',
              onTap: _toggleFavorite,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActionButton({
    required IconData icon,
    required String label,
    VoidCallback? onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Opacity(
        opacity: onTap != null ? 1.0 : 0.5,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 48,
              height: 48,
              decoration: BoxDecoration(
                color: Colors.blue[50],
                shape: BoxShape.circle,
              ),
              child: Icon(icon, color: Colors.blue[700]),
            ),
            const SizedBox(height: 4),
            Text(label, style: TextStyle(color: Colors.grey[700], fontSize: 12)),
          ],
        ),
      ),
    );
  }

  Widget _buildBusinessHours(BusinessHours hours) {
    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.only(top: 8),
        padding: const EdgeInsets.all(16),
        color: Colors.white,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Text(
                  '营业时间',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: hours.isOpenNow ? Colors.green[100] : Colors.red[100],
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    hours.isOpenNow ? '营业中' : '已休息',
                    style: TextStyle(
                      color: hours.isOpenNow ? Colors.green[700] : Colors.red[700],
                      fontSize: 11,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            BusinessHoursWidget(hours: hours),
          ],
        ),
      ),
    );
  }

  Widget _buildTags(List<String> tags) {
    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.only(top: 8),
        padding: const EdgeInsets.all(16),
        color: Colors.white,
        child: Wrap(
          spacing: 8,
          runSpacing: 8,
          children: tags.map((tag) {
            return Chip(
              label: Text(tag),
              backgroundColor: Colors.grey[100],
              padding: EdgeInsets.zero,
              materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
            );
          }).toList(),
        ),
      ),
    );
  }

  Widget _buildDescription(String description) {
    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.only(top: 8),
        padding: const EdgeInsets.all(16),
        color: Colors.white,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              '简介',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            Text(
              description,
              style: TextStyle(
                color: Colors.grey[700],
                height: 1.5,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMap(POI poi) {
    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.only(top: 8),
        height: 200,
        child: GoogleMap(
          initialCameraPosition: CameraPosition(
            target: LatLng(poi.latitude, poi.longitude),
            zoom: 16,
          ),
          markers: {
            Marker(
              markerId: MarkerId(poi.id),
              position: LatLng(poi.latitude, poi.longitude),
              infoWindow: InfoWindow(title: poi.name),
            ),
          },
          zoomControlsEnabled: false,
          mapToolbarEnabled: false,
          myLocationButtonEnabled: false,
        ),
      ),
    );
  }

  Widget _buildReviewsSection() {
    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.only(top: 8),
        padding: const EdgeInsets.all(16),
        color: Colors.white,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  '用户评价',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                if (_reviews.isNotEmpty)
                  TextButton(
                    onPressed: () {},
                    child: const Text('查看全部'),
                  ),
              ],
            ),
            const SizedBox(height: 12),
            if (_isLoadingReviews)
              const Center(child: CircularProgressIndicator())
            else if (_reviews.isEmpty)
              const Center(
                child: Padding(
                  padding: EdgeInsets.all(24),
                  child: Text('暂无评价', style: TextStyle(color: Colors.grey)),
                ),
              )
            else
              POIReviewList(
                reviews: _reviews.take(3).toList(),
                onLoadMore: _loadReviews,
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildBottomBar(POI poi) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 8,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          children: [
            Expanded(
              child: ElevatedButton.icon(
                onPressed: () => _openNavigation(poi),
                icon: const Icon(Icons.directions),
                label: const Text('导航去这里'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 12),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _sharePOI(POI poi) {
    // 分享功能
  }

  void _openNavigation(POI poi) {
    // 打开导航
  }

  void _makeCall(String phone) {
    // 拨打电话
  }

  void _startChat(POI poi) {
    // 开始咨询
  }
}

// 占位符
// ignore: constant_identifier_names
class Icons {
  static const String favorite = 'favorite';
  static const String favorite_border = 'favorite_border';
  static const String share = 'share';
  static const String image = 'image';
  static const String star = 'star';
  static const String location_on = 'location_on';
  static const String phone = 'phone';
  static const String attach_money = 'attach_money';
  static const String directions = 'directions';
  static const String message = 'message';
  static const String favorite_border_outlined = 'favorite_border_outlined';
}
