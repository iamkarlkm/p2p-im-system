import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';
import '../../models/poi_model.dart';
import '../../models/location_model.dart';
import '../../services/lbs/location_service.dart';
import '../../services/lbs/poi_service.dart';
import '../../widgets/lbs/poi_card.dart';
import '../../widgets/lbs/location_share_sheet.dart';
import '../poi/poi_detail_page.dart';
import '../poi/nearby_poi_page.dart';

/// 地图页面 - 核心LBS功能入口
/// 集成地图展示、当前位置、附近POI、位置分享
class MapPage extends StatefulWidget {
  const MapPage({Key? key}) : super(key: key);

  @override
  State<MapPage> createState() => _MapPageState();
}

class _MapPageState extends State<MapPage> {
  GoogleMapController? _mapController;
  
  // 地图状态
  LatLng? _currentPosition;
  Set<Marker> _markers = {};
  Set<Circle> _circles = {};
  bool _isMapReady = false;
  
  // 服务
  late LocationService _locationService;
  late POIService _poiService;
  
  // 流订阅
  StreamSubscription<UserLocation>? _locationSubscription;
  StreamSubscription<List<POI>>? _poiSubscription;

  // 配置
  static const double _defaultZoom = 15.0;
  static const double _defaultRadius = 2000; // 2km

  @override
  void initState() {
    super.initState();
    _locationService = LocationService();
    _poiService = POIService();
    _initialize();
  }

  Future<void> _initialize() async {
    // 初始化位置服务
    final initialized = await _locationService.initialize();
    if (!initialized) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('请开启位置权限')),
        );
      }
      return;
    }

    // 开始定位追踪
    await _locationService.startTracking();
    
    // 监听位置更新
    _locationSubscription = _locationService.locationUpdates.listen(_onLocationUpdate);
    
    // 监听POI更新
    _poiSubscription = _poiService.nearbyUpdates.listen(_onPOIUpdate);
    
    // 获取初始位置
    final position = await _locationService.getCurrentLocation();
    if (position != null && mounted) {
      setState(() {
        _currentPosition = position;
      });
      _moveCameraToPosition(position);
      _loadNearbyPOIs(position);
    }
  }

  void _onLocationUpdate(UserLocation location) {
    if (!mounted) return;
    
    setState(() {
      _currentPosition = LatLng(location.latitude, location.longitude);
    });
    
    // 更新当前位置标记
    _updateCurrentLocationMarker();
  }

  void _onPOIUpdate(List<POI> pois) {
    if (!mounted) return;
    _updatePOIMarkers(pois);
  }

  void _updateCurrentLocationMarker() {
    if (_currentPosition == null) return;
    
    final marker = Marker(
      markerId: const MarkerId('current_location'),
      position: _currentPosition!,
      icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure),
      infoWindow: const InfoWindow(title: '我的位置'),
    );

    setState(() {
      _markers.removeWhere((m) => m.markerId.value == 'current_location');
      _markers.add(marker);
    });
  }

  void _updatePOIMarkers(List<POI> pois) {
    final newMarkers = <Marker>{};
    
    // 保留当前位置标记
    final currentMarker = _markers.where(
      (m) => m.markerId.value == 'current_location'
    ).toSet();
    newMarkers.addAll(currentMarker);
    
    // 添加POI标记
    for (final poi in pois.take(20)) { // 最多显示20个
      final marker = Marker(
        markerId: MarkerId('poi_${poi.id}'),
        position: LatLng(poi.latitude, poi.longitude),
        icon: BitmapDescriptor.defaultMarkerWithHue(
          _getMarkerHue(poi.category)
        ),
        infoWindow: InfoWindow(
          title: poi.name,
          snippet: '${poi.formattedDistance} · ${poi.formattedRating}',
        ),
        onTap: () => _onPOITap(poi),
      );
      newMarkers.add(marker);
    }

    setState(() {
      _markers = newMarkers;
    });
  }

  double _getMarkerHue(POICategory category) {
    switch (category) {
      case POICategory.restaurant:
        return BitmapDescriptor.hueOrange;
      case POICategory.shopping:
        return BitmapDescriptor.hueRose;
      case POICategory.entertainment:
        return BitmapDescriptor.hueViolet;
      case POICategory.hotel:
        return BitmapDescriptor.hueBlue;
      case POICategory.scenic:
        return BitmapDescriptor.hueGreen;
      default:
        return BitmapDescriptor.hueRed;
    }
  }

  void _onPOITap(POI poi) {
    _poiService.selectPOI(poi);
    showModalBottomSheet(
      context: context,
      builder: (context) => POIPreviewCard(
        poi: poi,
        onTap: () => _navigateToPOIDetail(poi),
      ),
    );
  }

  void _navigateToPOIDetail(POI poi) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => POIDetailPage(poiId: poi.id),
      ),
    );
  }

  Future<void> _loadNearbyPOIs(LatLng position) async {
    await _poiService.searchNearby(
      location: position,
      radius: _defaultRadius,
    );
  }

  void _moveCameraToPosition(LatLng position) {
    _mapController?.animateCamera(
      CameraUpdate.newLatLngZoom(position, _defaultZoom),
    );
  }

  void _onMyLocationPressed() {
    if (_currentPosition != null) {
      _moveCameraToPosition(_currentPosition!);
    }
  }

  void _onNearbyPressed() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const NearbyPOIPage()),
    );
  }

  void _onShareLocationPressed() {
    if (_currentPosition == null) return;
    
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => LocationShareSheet(
        currentLocation: _currentPosition!,
        address: '', // TODO: 反向地理编码获取地址
      ),
    );
  }

  void _onSearchPressed() {
    showSearch(
      context: context,
      delegate: POISearchDelegate(_poiService, _currentPosition),
    );
  }

  @override
  void dispose() {
    _locationSubscription?.cancel();
    _poiSubscription?.cancel();
    _mapController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: _locationService),
        ChangeNotifierProvider.value(value: _poiService),
      ],
      child: Scaffold(
        body: Stack(
          children: [
            // 地图
            GoogleMap(
              initialCameraPosition: const CameraPosition(
                target: LatLng(39.9042, 116.4074), // 北京默认
                zoom: _defaultZoom,
              ),
              markers: _markers,
              circles: _circles,
              myLocationEnabled: true,
              myLocationButtonEnabled: false,
              zoomControlsEnabled: false,
              mapToolbarEnabled: false,
              onMapCreated: (controller) {
                _mapController = controller;
                setState(() => _isMapReady = true);
              },
            ),
            
            // 顶部搜索栏
            Positioned(
              top: MediaQuery.of(context).padding.top + 16,
              left: 16,
              right: 16,
              child: _buildSearchBar(),
            ),
            
            // 底部功能按钮
            Positioned(
              right: 16,
              bottom: 120,
              child: _buildActionButtons(),
            ),
            
            // 底部附近列表入口
            Positioned(
              left: 16,
              right: 16,
              bottom: 16,
              child: _buildNearbyButton(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSearchBar() {
    return GestureDetector(
      onTap: _onSearchPressed,
      child: Container(
        height: 48,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(24),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Row(
          children: [
            const SizedBox(width: 16),
            const Icon(Icons.search, color: Colors.grey),
            const SizedBox(width: 12),
            const Expanded(
              child: Text(
                '搜索附近商家、地点',
                style: TextStyle(color: Colors.grey, fontSize: 15),
              ),
            ),
            Container(
              margin: const EdgeInsets.all(6),
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: Colors.blue.withOpacity(0.1),
                borderRadius: BorderRadius.circular(16),
              ),
              child: const Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(Icons.tune, size: 16, color: Colors.blue),
                  SizedBox(width: 4),
                  Text('筛选', style: TextStyle(color: Colors.blue, fontSize: 12)),
                ],
              ),
            ),
            const SizedBox(width: 8),
          ],
        ),
      ),
    );
  }

  Widget _buildActionButtons() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // 我的位置
        _buildFloatingButton(
          icon: Icons.my_location,
          onPressed: _onMyLocationPressed,
        ),
        const SizedBox(height: 12),
        // 分享位置
        _buildFloatingButton(
          icon: Icons.share_location,
          onPressed: _onShareLocationPressed,
          color: Colors.blue,
        ),
      ],
    );
  }

  Widget _buildFloatingButton({
    required IconData icon,
    required VoidCallback onPressed,
    Color color = Colors.white,
  }) {
    return FloatingActionButton.small(
      onPressed: onPressed,
      backgroundColor: color,
      elevation: 4,
      child: Icon(icon, color: color == Colors.white ? Colors.black87 : Colors.white),
    );
  }

  Widget _buildNearbyButton() {
    return GestureDetector(
      onTap: _onNearbyPressed,
      child: Container(
        height: 56,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(28),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.place, color: Colors.blue),
            const SizedBox(width: 8),
            Consumer<POIService>(
              builder: (context, service, child) {
                final count = service.nearbyPOIs.length;
                return Text(
                  count > 0 ? '查看附近 $count 个地点' : '查看附近地点',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w500,
                  ),
                );
              },
            ),
            const SizedBox(width: 8),
            const Icon(Icons.arrow_forward_ios, size: 14, color: Colors.grey),
          ],
        ),
      ),
    );
  }
}

/// POI搜索委托
class POISearchDelegate extends SearchDelegate<String> {
  final POIService _poiService;
  final LatLng? _currentPosition;

  POISearchDelegate(this._poiService, this._currentPosition);

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
    return _buildSearchResults();
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    return _buildSearchResults();
  }

  Widget _buildSearchResults() {
    if (query.isEmpty) {
      return _buildSuggestions();
    }

    // 执行搜索
    _poiService.searchNearby(
      location: _currentPosition,
      keyword: query,
    );

    return Consumer<POIService>(
      builder: (context, service, child) {
        if (service.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }

        if (service.error != null) {
          return Center(child: Text('错误: ${service.error}'));
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

  Widget _buildSuggestions() {
    final suggestions = [
      '餐厅',
      '咖啡馆',
      '酒店',
      '商场',
      '电影院',
      '加油站',
    ];

    return ListView(
      children: [
        const Padding(
          padding: EdgeInsets.all(16),
          child: Text(
            '热门搜索',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
        ),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: suggestions.map((s) {
            return ActionChip(
              label: Text(s),
              onPressed: () {
                query = s;
                showResults(context);
              },
            );
          }).toList(),
        ),
      ],
    );
  }
}

// 图标占位符
// ignore: constant_identifier_names
class Icons {
  static const String search = 'search';
  static const String tune = 'tune';
  static const String my_location = 'my_location';
  static const String share_location = 'share_location';
  static const String place = 'place';
  static const String arrow_forward_ios = 'arrow_forward_ios';
  static const String clear = 'clear';
  static const String arrow_back = 'arrow_back';
}
