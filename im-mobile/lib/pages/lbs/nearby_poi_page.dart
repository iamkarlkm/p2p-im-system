import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/poi_model.dart';
import '../../services/lbs/poi_service.dart';
import '../../widgets/lbs/poi_card.dart';
import '../../widgets/lbs/category_filter.dart';
import 'poi_detail_page.dart';

/// 附近商家/POI列表页面
/// 展示附近地点的列表视图，支持分类筛选和排序
class NearbyPOIPage extends StatefulWidget {
  const NearbyPOIPage({Key? key}) : super(key: key);

  @override
  State<NearbyPOIPage> createState() => _NearbyPOIPageState();
}

class _NearbyPOIPageState extends State<NearbyPOIPage> {
  late POIService _poiService;
  final ScrollController _scrollController = ScrollController();
  
  // 筛选状态
  POICategory? _selectedCategory;
  String _sortBy = 'distance'; // distance, rating, popularity

  @override
  void initState() {
    super.initState();
    _poiService = POIService();
    _scrollController.addListener(_onScroll);
    
    // 初始加载
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _poiService.refreshNearby();
    });
  }

  void _onScroll() {
    if (_scrollController.position.pixels >= 
        _scrollController.position.maxScrollExtent - 200) {
      _poiService.loadMore();
    }
  }

  void _onCategorySelected(POICategory? category) {
    setState(() => _selectedCategory = category);
    _poiService.searchNearby(
      category: category,
      refresh: true,
    );
  }

  void _onSortChanged(String sortBy) {
    setState(() => _sortBy = sortBy);
    // 重新排序当前列表
    _sortPOIs();
  }

  void _sortPOIs() {
    final pois = List<POI>.from(_poiService.nearbyPOIs);
    
    switch (_sortBy) {
      case 'distance':
        pois.sort((a, b) => a.distance.compareTo(b.distance));
        break;
      case 'rating':
        pois.sort((a, b) {
          final ratingA = a.rating ?? 0;
          final ratingB = b.rating ?? 0;
          return ratingB.compareTo(ratingA);
        });
        break;
      case 'popularity':
        // 使用reviewCount作为热度指标
        pois.sort((a, b) {
          final countA = a.reviewCount ?? 0;
          final countB = b.reviewCount ?? 0;
          return countB.compareTo(countA);
        });
        break;
    }
  }

  void _navigateToDetail(POI poi) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => POIDetailPage(poiId: poi.id),
      ),
    );
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _poiService,
      child: Scaffold(
        backgroundColor: Colors.grey[100],
        body: CustomScrollView(
          controller: _scrollController,
          slivers: [
            // 顶部AppBar
            _buildSliverAppBar(),
            
            // 分类筛选
            _buildCategoryFilter(),
            
            // 排序选项
            _buildSortBar(),
            
            // POI列表
            _buildPOIList(),
            
            // 加载更多指示器
            _buildLoadMoreIndicator(),
          ],
        ),
      ),
    );
  }

  Widget _buildSliverAppBar() {
    return SliverAppBar(
      expandedHeight: 120,
      floating: true,
      pinned: true,
      flexibleSpace: FlexibleSpaceBar(
        title: const Text('附近地点'),
        background: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [Colors.blue[700]!, Colors.blue[500]!],
            ),
          ),
        ),
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.search),
          onPressed: () {
            showSearch(
              context: context,
              delegate: POINearbySearchDelegate(_poiService),
            );
          },
        ),
        IconButton(
          icon: const Icon(Icons.map),
          onPressed: () => Navigator.pop(context),
        ),
      ],
    );
  }

  Widget _buildCategoryFilter() {
    return SliverToBoxAdapter(
      child: Container(
        height: 80,
        color: Colors.white,
        child: CategoryFilter(
          selectedCategory: _selectedCategory,
          onCategorySelected: _onCategorySelected,
        ),
      ),
    );
  }

  Widget _buildSortBar() {
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
          children: [
            const Text(
              '排序: ',
              style: TextStyle(color: Colors.grey),
            ),
            _buildSortChip('距离最近', 'distance'),
            const SizedBox(width: 8),
            _buildSortChip('评分最高', 'rating'),
            const SizedBox(width: 8),
            _buildSortChip('热度最高', 'popularity'),
          ],
        ),
      ),
    );
  }

  Widget _buildSortChip(String label, String value) {
    final isSelected = _sortBy == value;
    
    return ChoiceChip(
      label: Text(label),
      selected: isSelected,
      onSelected: (selected) {
        if (selected) _onSortChanged(value);
      },
      selectedColor: Colors.blue[100],
      labelStyle: TextStyle(
        color: isSelected ? Colors.blue[700] : Colors.grey[700],
        fontSize: 12,
      ),
    );
  }

  Widget _buildPOIList() {
    return Consumer<POIService>(
      builder: (context, service, child) {
        if (service.isLoading && service.nearbyPOIs.isEmpty) {
          return const SliverFillRemaining(
            child: Center(child: CircularProgressIndicator()),
          );
        }

        if (service.error != null && service.nearbyPOIs.isEmpty) {
          return SliverFillRemaining(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 48, color: Colors.grey),
                  const SizedBox(height: 16),
                  Text('加载失败: ${service.error}'),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () => service.refreshNearby(),
                    child: const Text('重试'),
                  ),
                ],
              ),
            ),
          );
        }

        if (service.nearbyPOIs.isEmpty) {
          return const SliverFillRemaining(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.place_outlined, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text(
                    '附近暂无地点',
                    style: TextStyle(color: Colors.grey[600], fontSize: 16),
                  ),
                ],
              ),
            ),
          );
        }

        final pois = service.nearbyPOIs;
        
        return SliverList(
          delegate: SliverChildBuilderDelegate(
            (context, index) {
              final poi = pois[index];
              return POICard(
                poi: poi,
                onTap: () => _navigateToDetail(poi),
                onFavoriteTap: () => _toggleFavorite(poi),
              );
            },
            childCount: pois.length,
          ),
        );
      },
    );
  }

  Widget _buildLoadMoreIndicator() {
    return SliverToBoxAdapter(
      child: Consumer<POIService>(
        builder: (context, service, child) {
          if (!service.isLoading) return const SizedBox.shrink();
          
          return Container(
            padding: const EdgeInsets.all(16),
            alignment: Alignment.center,
            child: const CircularProgressIndicator(strokeWidth: 2),
          );
        },
      ),
    );
  }

  Future<void> _toggleFavorite(POI poi) async {
    if (poi.isFavorite) {
      await _poiService.unfavoritePOI(poi.id);
    } else {
      await _poiService.favoritePOI(poi.id);
    }
  }
}

/// 附近搜索委托
class POINearbySearchDelegate extends SearchDelegate<String> {
  final POIService _poiService;

  POINearbySearchDelegate(this._poiService);

  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      if (query.isNotEmpty)
        IconButton(
          icon: const Icon(Icons.clear),
          onPressed: () => query = '',
        ),
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: const Icon(Icons.arrow_back),
      onPressed: () => close(context, ''),
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    _poiService.searchNearby(keyword: query);
    return _buildResultsList();
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    return _buildSuggestionsList();
  }

  Widget _buildResultsList() {
    return Consumer<POIService>(
      builder: (context, service, child) {
        if (service.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }

        final pois = service.nearbyPOIs;
        if (pois.isEmpty) {
          return const Center(child: Text('未找到相关地点'));
        }

        return ListView.builder(
          itemCount: pois.length,
          itemBuilder: (context, index) {
            final poi = pois[index];
            return POIListTile(
              poi: poi,
              onTap: () => close(context, poi.id),
            );
          },
        );
      },
    );
  }

  Widget _buildSuggestionsList() {
    final history = ['火锅', '星巴克', '电影院', '便利店'];
    
    return ListView(
      children: [
        const ListTile(
          title: Text('搜索历史', style: TextStyle(fontWeight: FontWeight.bold)),
        ),
        ...history.map((h) => ListTile(
          leading: const Icon(Icons.history),
          title: Text(h),
          onTap: () {
            query = h;
            showResults(context);
          },
        )),
      ],
    );
  }
}

// 占位符类
// ignore: constant_identifier_names
class Icons {
  static const String search = 'search';
  static const String map = 'map';
  static const String error_outline = 'error_outline';
  static const String place_outlined = 'place_outlined';
  static const String history = 'history';
  static const String clear = 'clear';
  static const String arrow_back = 'arrow_back';
}
