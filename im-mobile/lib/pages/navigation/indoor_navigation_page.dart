import 'package:flutter/material.dart';
import 'package:im_mobile/models/location/poi_model.dart';
import 'package:im_mobile/services/navigation/navigation_service.dart';

/// 室内导航页面
/// 
/// 提供商场、机场等室内场所的导航功能
class IndoorNavigationPage extends StatefulWidget {
  final POIModel building;
  final IndoorInfo indoorInfo;
  final POIModel? destination;

  const IndoorNavigationPage({
    Key? key,
    required this.building,
    required this.indoorInfo,
    this.destination,
  }) : super(key: key);

  @override
  State<IndoorNavigationPage> createState() => _IndoorNavigationPageState();
}

class _IndoorNavigationPageState extends State<IndoorNavigationPage> {
  String _currentFloor = '1';
  POIModel? _selectedDestination;
  List<LatLng>? _route;
  bool _isNavigating = false;

  @override
  void initState() {
    super.initState();
    _selectedDestination = widget.destination;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.building.name),
        actions: [
          if (_selectedDestination != null)
            IconButton(
              icon: Icon(_isNavigating ? Icons.stop : Icons.navigation),
              onPressed: _toggleNavigation,
            ),
        ],
      ),
      body: Column(
        children: [
          // 楼层选择器
          _buildFloorSelector(),
          
          // 室内地图区域
          Expanded(
            child: Stack(
              children: [
                // 地图占位
                Container(
                  color: Colors.grey[100],
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.map,
                          size: 64,
                          color: Colors.grey[400],
                        ),
                        const SizedBox(height: 16),
                        Text(
                          '${_getCurrentFloorName()}平面图',
                          style: TextStyle(
                            fontSize: 18,
                            color: Colors.grey[600],
                          ),
                        ),
                        if (_selectedDestination != null)
                          Padding(
                            padding: const EdgeInsets.only(top: 8),
                            child: Text(
                              '目的地: ${_selectedDestination!.name}',
                              style: TextStyle(
                                fontSize: 14,
                                color: Colors.blue[700],
                              ),
                            ),
                          ),
                      ],
                    ),
                  ),
                ),
                
                // 图例
                Positioned(
                  left: 16,
                  bottom: 16,
                  child: _buildMapLegend(),
                ),
              ],
            ),
          ),
          
          // 底部信息栏
          if (_selectedDestination != null)
            _buildDestinationInfo(),
        ],
      ),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          // 定位按钮
          FloatingActionButton.small(
            heroTag: 'locate',
            onPressed: () {
              // 定位到当前位置
            },
            child: const Icon(Icons.my_location),
          ),
          const SizedBox(height: 8),
          // 搜索按钮
          FloatingActionButton(
            heroTag: 'search',
            onPressed: _showDestinationSearch,
            child: const Icon(Icons.search),
          ),
        ],
      ),
    );
  }

  Widget _buildFloorSelector() {
    return Container(
      height: 60,
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 4,
          ),
        ],
      ),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: widget.indoorInfo.floors.length,
        itemBuilder: (context, index) {
          final floor = widget.indoorInfo.floors[index];
          final isSelected = floor.floorNumber == _currentFloor;
          
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: ChoiceChip(
              label: Text(floor.floorName),
              selected: isSelected,
              onSelected: (selected) {
                if (selected) {
                  setState(() {
                    _currentFloor = floor.floorNumber;
                  });
                }
              },
            ),
          );
        },
      ),
    );
  }

  Widget _buildMapLegend() {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 4,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          _buildLegendItem(Colors.blue, '当前位置'),
          _buildLegendItem(Colors.red, '目的地'),
          _buildLegendItem(Colors.green, '电梯/扶梯'),
          _buildLegendItem(Colors.orange, '楼梯'),
          _buildLegendItem(Colors.purple, '出入口'),
        ],
      ),
    );
  }

  Widget _buildLegendItem(Color color, String label) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 4),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 12,
            height: 12,
            decoration: BoxDecoration(
              color: color,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(width: 8),
          Text(
            label,
            style: const TextStyle(fontSize: 11),
          ),
        ],
      ),
    );
  }

  Widget _buildDestinationInfo() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 8,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  width: 48,
                  height: 48,
                  decoration: BoxDecoration(
                    color: Colors.blue[50],
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Center(
                    child: Text(
                      _selectedDestination!.categoryIcon,
                      style: const TextStyle(fontSize: 24),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        _selectedDestination!.name,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _selectedDestination!.fullAddress,
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    setState(() {
                      _selectedDestination = null;
                      _route = null;
                      _isNavigating = false;
                    });
                  },
                ),
              ],
            ),
            const SizedBox(height: 12),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _toggleNavigation,
                icon: Icon(_isNavigating ? Icons.stop : Icons.navigation),
                label: Text(_isNavigating ? '结束导航' : '开始室内导航'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getCurrentFloorName() {
    final floor = widget.indoorInfo.getFloor(_currentFloor);
    return floor?.floorName ?? '$_currentFloor层';
  }

  void _toggleNavigation() {
    setState(() {
      _isNavigating = !_isNavigating;
    });
    
    if (_isNavigating) {
      // 开始室内导航
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('室内导航已开启')),
      );
    }
  }

  void _showDestinationSearch() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.6,
        minChildSize: 0.3,
        maxChildSize: 0.9,
        expand: false,
        builder: (context, scrollController) {
          return IndoorDestinationSearch(
            building: widget.building,
            indoorInfo: widget.indoorInfo,
            scrollController: scrollController,
            onSelect: (destination) {
              setState(() {
                _selectedDestination = destination;
              });
              Navigator.of(context).pop();
            },
          );
        },
      ),
    );
  }
}

/// 室内目的地搜索
class IndoorDestinationSearch extends StatefulWidget {
  final POIModel building;
  final IndoorInfo indoorInfo;
  final ScrollController scrollController;
  final Function(POIModel) onSelect;

  const IndoorDestinationSearch({
    Key? key,
    required this.building,
    required this.indoorInfo,
    required this.scrollController,
    required this.onSelect,
  }) : super(key: key);

  @override
  State<IndoorDestinationSearch> createState() => _IndoorDestinationSearchState();
}

class _IndoorDestinationSearchState extends State<IndoorDestinationSearch> {
  final TextEditingController _searchController = TextEditingController();
  List<POIModel> _results = [];

  // 模拟室内商铺数据
  List<POIModel> get _mockShops => [
    POIModel(
      id: 'shop_1',
      name: '星巴克咖啡',
      location: widget.building.location,
      categoryCode: 'food',
      categoryName: '餐饮',
    ),
    POIModel(
      id: 'shop_2',
      name: '优衣库',
      location: widget.building.location,
      categoryCode: 'shopping',
      categoryName: '购物',
    ),
    POIModel(
      id: 'shop_3',
      name: '海底捞火锅',
      location: widget.building.location,
      categoryCode: 'food',
      categoryName: '餐饮',
    ),
    POIModel(
      id: 'shop_4',
      name: '苹果专卖店',
      location: widget.building.location,
      categoryCode: 'shopping',
      categoryName: '购物',
    ),
    POIModel(
      id: 'shop_5',
      name: '万达影城',
      location: widget.building.location,
      categoryCode: 'entertainment',
      categoryName: '娱乐',
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // 搜索栏
        Padding(
          padding: const EdgeInsets.all(16),
          child: TextField(
            controller: _searchController,
            decoration: InputDecoration(
              hintText: '搜索商铺、设施...',
              prefixIcon: const Icon(Icons.search),
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
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            onChanged: (value) {
              if (value.isNotEmpty) {
                setState(() {
                  _results = _mockShops
                      .where((shop) => shop.name.contains(value))
                      .toList();
                });
              } else {
                setState(() {
                  _results = [];
                });
              }
            },
          ),
        ),
        
        // 分类筛选
        SizedBox(
          height: 40,
          child: ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            children: [
              _buildCategoryChip('全部', true),
              _buildCategoryChip('餐饮', false),
              _buildCategoryChip('购物', false),
              _buildCategoryChip('娱乐', false),
              _buildCategoryChip('服务', false),
            ],
          ),
        ),
        
        const Divider(),
        
        // 搜索结果
        Expanded(
          child: _results.isEmpty
              ? _buildQuickAccess()
              : ListView.builder(
                  controller: widget.scrollController,
                  itemCount: _results.length,
                  itemBuilder: (context, index) {
                    final shop = _results[index];
                    return ListTile(
                      leading: Container(
                        width: 40,
                        height: 40,
                        decoration: BoxDecoration(
                          color: Colors.blue[50],
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Center(
                          child: Text(shop.categoryIcon),
                        ),
                      ),
                      title: Text(shop.name),
                      subtitle: Text(shop.categoryName ?? ''),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () => widget.onSelect(shop),
                    );
                  },
                ),
        ),
      ],
    );
  }

  Widget _buildCategoryChip(String label, bool isSelected) {
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: FilterChip(
        label: Text(label),
        selected: isSelected,
        onSelected: (selected) {
          // 筛选分类
        },
      ),
    );
  }

  Widget _buildQuickAccess() {
    return ListView(
      controller: widget.scrollController,
      padding: const EdgeInsets.all(16),
      children: [
        const Text(
          '快捷入口',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 12),
        
        // 常用设施
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            _buildQuickAccessChip('🚻', '卫生间'),
            _buildQuickAccessChip('🛗', '电梯'),
            _buildQuickAccessChip('🚪', '出入口'),
            _buildQuickAccessChip('🏧', 'ATM'),
            _buildQuickAccessChip('🛍️', '服务台'),
            _buildQuickAccessChip('🅿️', '停车场'),
          ],
        ),
        
        const SizedBox(height: 24),
        
        const Text(
          '热门商户',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 12),
        
        ..._mockShops.take(5).map((shop) {
          return ListTile(
            leading: Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Center(child: Text(shop.categoryIcon)),
            ),
            title: Text(shop.name),
            subtitle: Text(shop.categoryName ?? ''),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => widget.onSelect(shop),
          );
        }).toList(),
      ],
    );
  }

  Widget _buildQuickAccessChip(String icon, String label) {
    return ActionChip(
      avatar: Text(icon),
      label: Text(label),
      onPressed: () {
        // 快速选择
      },
    );
  }
}
