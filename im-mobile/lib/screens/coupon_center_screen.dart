import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/coupon_model.dart';
import '../providers/coupon_provider.dart';
import '../services/location_service.dart';
import '../widgets/coupon_card.dart';
import '../widgets/loading_widget.dart';
import '../widgets/error_widget.dart';

/// 领券中心页面
/// 
/// 展示附近可领取的优惠券，支持按分类筛选和排序
/// 
/// @author IM Development Team
/// @version 1.0
/// @since 2026-03-28
class CouponCenterScreen extends ConsumerStatefulWidget {
  const CouponCenterScreen({Key? key}) : super(key: key);

  @override
  ConsumerState<CouponCenterScreen> createState() => _CouponCenterScreenState();
}

class _CouponCenterScreenState extends ConsumerState<CouponCenterScreen> 
    with SingleTickerProviderStateMixin {
  
  late TabController _tabController;
  final TextEditingController _searchController = TextEditingController();
  
  // 当前位置
  double? _currentLat;
  double? _currentLng;
  
  // 筛选条件
  String _selectedCategory = 'ALL';
  String _sortBy = 'DISTANCE';
  int _currentPage = 0;
  
  // 分类列表
  final List<Map<String, String>> _categories = [
    {'code': 'ALL', 'name': '全部'},
    {'code': 'FOOD', 'name': '美食'},
    {'code': 'ENTERTAINMENT', 'name': '娱乐'},
    {'code': 'SHOPPING', 'name': '购物'},
    {'code': 'BEAUTY', 'name': '丽人'},
    {'code': 'SPORTS', 'name': '运动'},
    {'code': 'TRAVEL', 'name': '旅游'},
    {'code': 'EDUCATION', 'name': '教育'},
  ];
  
  // 排序选项
  final List<Map<String, String>> _sortOptions = [
    {'code': 'DISTANCE', 'name': '距离最近'},
    {'code': 'HOT', 'name': '热度最高'},
    {'code': 'NEW', 'name': '最新发布'},
    {'code': 'DISCOUNT', 'name': '优惠最大'},
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _initLocation();
  }

  @override
  void dispose() {
    _tabController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  /// 初始化位置信息
  Future<void> _initLocation() async {
    try {
      final position = await LocationService.getCurrentPosition();
      setState(() {
        _currentLat = position.latitude;
        _currentLng = position.longitude;
      });
      _loadCoupons();
    } catch (e) {
      // 使用默认位置
      setState(() {
        _currentLat = 31.2304;
        _currentLng = 121.4737;
      });
      _loadCoupons();
    }
  }

  /// 加载优惠券列表
  Future<void> _loadCoupons({bool refresh = false}) async {
    if (_currentLat == null || _currentLng == null) return;
    
    if (refresh) {
      _currentPage = 0;
    }
    
    await ref.read(nearbyCouponsProvider.notifier).loadNearbyCoupons(
      lat: _currentLat!,
      lng: _currentLng!,
      category: _selectedCategory == 'ALL' ? null : _selectedCategory,
      sortBy: _sortBy,
      page: _currentPage,
    );
  }

  /// 领取优惠券
  Future<void> _claimCoupon(String couponId) async {
    try {
      await ref.read(couponActionProvider.notifier).claimCoupon(couponId);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('领取成功'));
        );
        _loadCoupons(refresh: true);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('领取失败: $e'));
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final couponState = ref.watch(nearbyCouponsProvider);
    
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      body: NestedScrollView(
        headerSliverBuilder: (context, innerBoxIsScrolled) {
          return [
            // 顶部标题栏
            SliverAppBar(
              floating: true,
              pinned: true,
              elevation: 0,
              backgroundColor: Colors.white,
              title: const Text(
                '领券中心',
                style: TextStyle(
                  color: Colors.black87,
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
              centerTitle: true,
              bottom: TabBar(
                controller: _tabController,
                labelColor: Theme.of(context).primaryColor,
                unselectedLabelColor: Colors.grey,
                indicatorColor: Theme.of(context).primaryColor,
                tabs: const [
                  Tab(text: '附近好券'),
                  Tab(text: '我的券包'),
                ],
              ),
            ),
          ];
        },
        body: TabBarView(
          controller: _tabController,
          children: [
            // 附近好券
            _buildNearbyCouponsTab(couponState),
            // 我的券包
            const MyCouponsScreen(),
          ],
        ),
      ),
    );
  }

  /// 附近好券Tab
  Widget _buildNearbyCouponsTab(CouponListState state) {
    return Column(
      children: [
        // 搜索栏
        _buildSearchBar(),
        // 分类筛选
        _buildCategoryFilter(),
        // 排序栏
        _buildSortBar(),
        // 优惠券列表
        Expanded(
          child: _buildCouponList(state),
        ),
      ],
    );
  }

  /// 搜索栏
  Widget _buildSearchBar() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      color: Colors.white,
      child: Container(
        height: 40,
        decoration: BoxDecoration(
          color: const Color(0xFFF5F5F5),
          borderRadius: BorderRadius.circular(20),
        ),
        child: TextField(
          controller: _searchController,
          decoration: InputDecoration(
            hintText: '搜索商家或优惠券',
            hintStyle: const TextStyle(color: Colors.grey, fontSize: 14),
            prefixIcon: const Icon(Icons.search, color: Colors.grey, size: 20),
            suffixIcon: _searchController.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear, color: Colors.grey, size: 18),
                    onPressed: () {
                      _searchController.clear();
                      _loadCoupons(refresh: true);
                    },
                  )
                : null,
            border: InputBorder.none,
            contentPadding: const EdgeInsets.symmetric(vertical: 10),
          ),
          onSubmitted: (value) {
            // 执行搜索
            _loadCoupons(refresh: true);
          },
        ),
      ),
    );
  }

  /// 分类筛选
  Widget _buildCategoryFilter() {
    return Container(
      height: 48,
      color: Colors.white,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 12),
        itemCount: _categories.length,
        itemBuilder: (context, index) {
          final category = _categories[index];
          final isSelected = _selectedCategory == category['code'];
          
          return GestureDetector(
            onTap: () {
              setState(() {
                _selectedCategory = category['code']!;
              });
              _loadCoupons(refresh: true);
            },
            child: Container(
              margin: const EdgeInsets.symmetric(horizontal: 4, vertical: 8),
              padding: const EdgeInsets.symmetric(horizontal: 16),
              decoration: BoxDecoration(
                color: isSelected 
                    ? Theme.of(context).primaryColor.withOpacity(0.1)
                    : const Color(0xFFF5F5F5),
                borderRadius: BorderRadius.circular(16),
                border: isSelected
                    ? Border.all(color: Theme.of(context).primaryColor, width: 1)
                    : null,
              ),
              alignment: Alignment.center,
              child: Text(
                category['name']!,
                style: TextStyle(
                  color: isSelected 
                      ? Theme.of(context).primaryColor
                      : Colors.black87,
                  fontSize: 13,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  /// 排序栏
  Widget _buildSortBar() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      color: Colors.white,
      child: Row(
        children: [
          const Icon(
            Icons.location_on,
            size: 14,
            color: Colors.grey,
          ),
          const SizedBox(width: 4),
          const Text(
            '附近',
            style: TextStyle(fontSize: 12, color: Colors.grey),
          ),
          const Spacer(),
          // 排序选项
          PopupMenuButton<String>(
            child: Row(
              children: [
                Text(
                  _sortOptions.firstWhere(
                    (opt) => opt['code'] == _sortBy,
                    orElse: () => _sortOptions[0],
                  )['name']!,
                  style: const TextStyle(fontSize: 12, color: Colors.grey),
                ),
                const Icon(Icons.arrow_drop_down, size: 16, color: Colors.grey),
              ],
            ),
            onSelected: (value) {
              setState(() {
                _sortBy = value;
              });
              _loadCoupons(refresh: true);
            },
            itemBuilder: (context) {
              return _sortOptions.map((opt) {
                return PopupMenuItem(
                  value: opt['code'],
                  child: Text(opt['name']!),
                );
              }).toList();
            },
          ),
        ],
      ),
    );
  }

  /// 优惠券列表
  Widget _buildCouponList(CouponListState state) {
    if (state.isLoading && state.coupons.isEmpty) {
      return const LoadingWidget();
    }
    
    if (state.error != null && state.coupons.isEmpty) {
      return ErrorWidgetWithRetry(
        message: state.error!,
        onRetry: () => _loadCoupons(refresh: true),
      );
    }
    
    if (state.coupons.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.local_offer_outlined, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              '附近暂无优惠券',
              style: TextStyle(color: Colors.grey, fontSize: 14),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => _loadCoupons(refresh: true),
      child: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: state.coupons.length + (state.hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == state.coupons.length) {
            // 加载更多
            _currentPage++;
            _loadCoupons();
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(16),
                child: CircularProgressIndicator(strokeWidth: 2),
              ),
            );
          }
          
          final coupon = state.coupons[index];
          return CouponCard(
            coupon: coupon,
            onClaim: () => _claimCoupon(coupon.couponId),
            onTap: () => _showCouponDetail(coupon),
          );
        },
      ),
    );
  }

  /// 显示优惠券详情
  void _showCouponDetail(CouponModel coupon) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => CouponDetailSheet(coupon: coupon),
    );
  }
}
