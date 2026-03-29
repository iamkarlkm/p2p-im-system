import 'package:flutter/material.dart';
import 'package:im_mobile/models/location/poi_model.dart';
import 'package:im_mobile/services/location/location_service.dart';
import 'package:im_mobile/services/navigation/navigation_service.dart';

/// POI搜索页面
/// 
/// 提供附近地点搜索、商户查找、停车场查询等功能
class POISearchPage extends StatefulWidget {
  final String? initialKeyword;
  final String? categoryCode;

  const POISearchPage({
    Key? key,
    this.initialKeyword,
    this.categoryCode,
  }) : super(key: key);

  @override
  State<POISearchPage> createState() => _POISearchPageState();
}

class _POISearchPageState extends State<POISearchPage> {
  final TextEditingController _searchController = TextEditingController();
  final LocationService _locationService = LocationService();
  
  bool _isLoading = false;
  List<POIModel> _results = [];
  String? _errorMessage;
  
  // 搜索历史
  List<String> _searchHistory = [
    '火锅店',
    '电影院',
    '加油站',
    '停车场',
    'ATM',
  ];
  
  // 热门分类
  final List<Map<String, dynamic>> _categories = [
    {'name': '餐饮', 'icon': Icons.restaurant, 'code': 'food'},
    {'name': '酒店', 'icon': Icons.hotel, 'code': 'hotel'},
    {'name': '购物', 'icon': Icons.shopping_bag, 'code': 'shopping'},
    {'name': '娱乐', 'icon': Icons.movie, 'code': 'entertainment'},
    {'name': '景点', 'icon': Icons.park, 'code': 'scenic'},
    {'name': '交通', 'icon': Icons.train, 'code': 'transport'},
    {'name': '银行', 'icon': Icons.account_balance, 'code': 'bank'},
    {'name': '医院', 'icon': Icons.local_hospital, 'code': 'medical'},
  ];

  @override
  void initState() {
    super.initState();
    if (widget.initialKeyword != null) {
      _searchController.text = widget.initialKeyword!;
      _performSearch(widget.initialKeyword!);
    }
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _performSearch(String keyword) async {
    if (keyword.trim().isEmpty) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // 获取当前位置
      final location = await _locationService.getCurrentLocation();
      
      // 模拟搜索结果
      await Future.delayed(const Duration(milliseconds: 500));
      
      final mockResults = _generateMockResults(keyword, location);
      
      setState(() {
        _results = mockResults;
        _isLoading = false;
      });

      // 添加到搜索历史
      if (!_searchHistory.contains(keyword)) {
        setState(() {
          _searchHistory.insert(0, keyword);
          if (_searchHistory.length > 10) {
            _searchHistory = _searchHistory.sublist(0, 10);
          }
        });
      }
    } catch (e) {
      setState(() {
        _errorMessage = '搜索失败: $e';
        _isLoading = false;
      });
    }
  }

  List<POIModel> _generateMockResults(String keyword, LatLng center) {
    final results = <POIModel>[];
    final random = DateTime.now().millisecond;
    
    for (int i = 0; i < 10; i++) {
      final distance = (i + 1) * 150.0 + (random % 100);
      results.add(POIModel(
        id: 'poi_$i',
        name: '$keyword ${i + 1}号店',
        location: LatLng(
          center.latitude + (i * 0.001),
          center.longitude + (i * 0.001),
        ),
        address: '朝阳区建国路${i + 1}号',
        categoryCode: 'food',
        categoryName: '餐饮',
        rating: 4.0 + (i % 10) / 10,
        averageCost: 50.0 + (i * 10),
        distance: distance,
        tags: i % 3 == 0 ? ['网红', '推荐'] : [],
      ));
    }
    
    return results;
  }

  void _navigateToPOI(POIModel poi) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => POIDetailPage(poi: poi),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: TextField(
          controller: _searchController,
          autofocus: widget.initialKeyword == null,
          decoration: InputDecoration(
            hintText: '搜索地点、商户...',
            border: InputBorder.none,
            suffixIcon: _searchController.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear),
                    onPressed: () {
                      _searchController.clear();
                      setState(() {
                        _results = [];
                      });
                    },
                  )
                : null,
          ),
          onSubmitted: _performSearch,
          textInputAction: TextInputAction.search,
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () => _performSearch(_searchController.text),
          ),
        ],
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
            const Icon(Icons.error_outline, size: 64, color: Colors.grey),
            const SizedBox(height: 16),
            Text(_errorMessage!),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => _performSearch(_searchController.text),
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    if (_results.isNotEmpty) {
      return ListView.builder(
        itemCount: _results.length,
        itemBuilder: (context, index) {
          final poi = _results[index];
          return POIListItem(
            poi: poi,
            onTap: () => _navigateToPOI(poi),
          );
        },
      );
    }

    // 显示搜索历史和分类
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 搜索历史
          if (_searchHistory.isNotEmpty) ...[
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  '搜索历史',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                TextButton(
                  onPressed: () {
                    setState(() {
                      _searchHistory.clear();
                    });
                  },
                  child: const Text('清除'),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: _searchHistory.map((keyword) {
                return ActionChip(
                  label: Text(keyword),
                  onPressed: () {
                    _searchController.text = keyword;
                    _performSearch(keyword);
                  },
                );
              }).toList(),
            ),
            const SizedBox(height: 24),
          ],

          // 热门分类
          const Text(
            '热门分类',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          GridView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 4,
              childAspectRatio: 1,
              crossAxisSpacing: 16,
              mainAxisSpacing: 16,
            ),
            itemCount: _categories.length,
            itemBuilder: (context, index) {
              final category = _categories[index];
              return GestureDetector(
                onTap: () {
                  _searchController.text = category['name'];
                  _performSearch(category['name']);
                },
                child: Column(
                  children: [
                    Container(
                      width: 48,
                      height: 48,
                      decoration: BoxDecoration(
                        color: Colors.blue[50],
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Icon(
                        category['icon'] as IconData,
                        color: Colors.blue,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      category['name'] as String,
                      style: const TextStyle(fontSize: 12),
                    ),
                  ],
                ),
              );
            },
          ),

          const SizedBox(height: 24),

          // 附近推荐
          const Text(
            '附近推荐',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          Card(
            child: ListTile(
              leading: Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: Colors.orange[50],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Icon(Icons.local_fire_department, color: Colors.orange),
              ),
              title: const Text('附近热门'),
              subtitle: const Text('发现周边热门商户'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                _performSearch('热门');
              },
            ),
          ),
        ],
      ),
    );
  }
}

/// POI列表项
class POIListItem extends StatelessWidget {
  final POIModel poi;
  final VoidCallback onTap;

  const POIListItem({
    Key? key,
    required this.poi,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              // 图标
              Container(
                width: 56,
                height: 56,
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Center(
                  child: Text(
                    poi.categoryIcon,
                    style: const TextStyle(fontSize: 28),
                  ),
                ),
              ),
              const SizedBox(width: 16),
              
              // 信息
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
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
                        if (poi.tags.isNotEmpty)
                          Container(
                            margin: const EdgeInsets.only(left: 8),
                            padding: const EdgeInsets.symmetric(
                              horizontal: 6,
                              vertical: 2,
                            ),
                            decoration: BoxDecoration(
                              color: Colors.orange[100],
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: Text(
                              poi.tags.first,
                              style: TextStyle(
                                fontSize: 10,
                                color: Colors.orange[800],
                              ),
                            ),
                          ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    
                    // 评分和人均
                    Row(
                      children: [
                        ...poi.ratingStars.map((isFilled) {
                          return Icon(
                            isFilled ? Icons.star : Icons.star_border,
                            size: 14,
                            color: Colors.orange,
                          );
                        }).toList(),
                        const SizedBox(width: 4),
                        Text(
                          poi.formattedRating,
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey[600],
                          ),
                        ),
                        if (poi.averageCost != null) ...[
                          const SizedBox(width: 8),
                          Text(
                            poi.formattedAverageCost,
                            style: TextStyle(
                              fontSize: 12,
                              color: Colors.grey[600],
                            ),
                          ),
                        ],
                      ],
                    ),
                    const SizedBox(height: 4),
                    
                    // 地址
                    Text(
                      poi.fullAddress,
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.grey[600],
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              ),
              
              // 距离
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    poi.formattedDistance,
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                      color: Colors.blue,
                    ),
                  ),
                  const SizedBox(height: 8),
                  ElevatedButton(
                    onPressed: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (context) => NavigationPage(
                            destination: poi,
                          ),
                        ),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 12),
                      minimumSize: const Size(0, 32),
                    ),
                    child: const Text('导航'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// POI详情页面
class POIDetailPage extends StatelessWidget {
  final POIModel poi;

  const POIDetailPage({
    Key? key,
    required this.poi,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 200,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              title: Text(poi.name),
              background: Container(
                color: Colors.blue[100],
                child: Center(
                  child: Text(
                    poi.categoryIcon,
                    style: const TextStyle(fontSize: 80),
                  ),
                ),
              ),
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // 基本信息
                  Row(
                    children: [
                      ...poi.ratingStars.map((isFilled) {
                        return Icon(
                          isFilled ? Icons.star : Icons.star_border,
                          size: 20,
                          color: Colors.orange,
                        );
                      }).toList(),
                      const SizedBox(width: 8),
                      Text(
                        poi.formattedRating,
                        style: const TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  
                  if (poi.averageCost != null)
                    Text(
                      poi.formattedAverageCost,
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey[600],
                      ),
                    ),
                  
                  const Divider(height: 32),
                  
                  // 地址
                  ListTile(
                    leading: const Icon(Icons.location_on),
                    title: const Text('地址'),
                    subtitle: Text(poi.fullAddress),
                    trailing: Text(
                      poi.formattedDistance,
                      style: const TextStyle(color: Colors.blue),
                    ),
                  ),
                  
                  // 电话
                  if (poi.phone != null)
                    ListTile(
                      leading: const Icon(Icons.phone),
                      title: const Text('电话'),
                      subtitle: Text(poi.phone!),
                      trailing: const Icon(Icons.call),
                      onTap: () {
                        // 拨打电话
                      },
                    ),
                  
                  // 营业时间
                  if (poi.businessHours != null)
                    ListTile(
                      leading: const Icon(Icons.access_time),
                      title: const Text('营业时间'),
                      subtitle: Text(poi.businessHours!),
                      trailing: Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 8,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: poi.isOpen ? Colors.green[100] : Colors.grey[200],
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(
                          poi.businessStatus,
                          style: TextStyle(
                            color: poi.isOpen ? Colors.green[800] : Colors.grey[600],
                            fontSize: 12,
                          ),
                        ),
                      ),
                    ),
                  
                  const SizedBox(height: 16),
                  
                  // 操作按钮
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: () {
                            Navigator.of(context).push(
                              MaterialPageRoute(
                                builder: (context) => NavigationPage(
                                  destination: poi,
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.navigation),
                          label: const Text('导航去这里'),
                          style: ElevatedButton.styleFrom(
                            minimumSize: const Size(double.infinity, 48),
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
