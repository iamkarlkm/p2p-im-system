import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../models/location_model.dart';
import '../../services/lbs/location_service.dart';
import '../../services/api_service.dart';
import '../../widgets/lbs/poi_review_list.dart';
import '../chat/chat_page.dart';

/// 附近的人页面
/// 展示附近用户列表，支持地图和列表视图
class NearbyPeoplePage extends StatefulWidget {
  const NearbyPeoplePage({Key? key}) : super(key: key);

  @override
  State<NearbyPeoplePage> createState() => _NearbyPeoplePageState();
}

class _NearbyPeoplePageState extends State<NearbyPeoplePage> {
  final LocationService _locationService = LocationService();
  final ApiService _apiService = ApiService();
  
  // 状态
  bool _isLoading = true;
  String? _error;
  List<NearbyUser> _nearbyUsers = [];
  
  // 视图模式
  bool _isMapView = false;
  
  // 地图
  GoogleMapController? _mapController;
  Set<Marker> _markers = {};
  
  // 筛选
  double _maxDistance = 5000; // 5km
  String _sortBy = 'distance'; // distance, similarity, active

  @override
  void initState() {
    super.initState();
    _initialize();
  }

  Future<void> _initialize() async {
    // 初始化位置服务
    final initialized = await _locationService.initialize();
    if (!initialized) {
      setState(() {
        _error = '请开启位置权限';
        _isLoading = false;
      });
      return;
    }

    await _loadNearbyUsers();
  }

  Future<void> _loadNearbyUsers() async {
    setState(() => _isLoading = true);

    try {
      final position = await _locationService.getCurrentLocation();
      if (position == null) {
        setState(() {
          _error = '无法获取位置';
          _isLoading = false;
        });
        return;
      }

      final response = await _apiService.get('/lbs/people/nearby', params: {
        'latitude': position.latitude,
        'longitude': position.longitude,
        'radius': _maxDistance,
        'sortBy': _sortBy,
      });

      final data = jsonDecode(response.body);
      final List<dynamic> userList = data['data']['list'] ?? [];
      
      setState(() {
        _nearbyUsers = userList.map((json) => NearbyUser.fromJson(json)).toList();
        _isLoading = false;
      });

      _updateMapMarkers();
    } catch (e) {
      setState(() {
        _error = '加载失败: $e';
        _isLoading = false;
      });
    }
  }

  void _updateMapMarkers() {
    if (!_isMapView) return;

    final markers = <Marker>{};
    
    for (final user in _nearbyUsers) {
      final marker = Marker(
        markerId: MarkerId('user_${user.userId}'),
        position: LatLng(user.latitude, user.longitude),
        icon: BitmapDescriptor.defaultMarkerWithHue(
          user.isOnline 
              ? BitmapDescriptor.hueGreen 
              : BitmapDescriptor.hueOrange,
        ),
        infoWindow: InfoWindow(
          title: user.nickname ?? '用户${user.userId}',
          snippet: user.formattedDistance,
        ),
        onTap: () => _showUserBottomSheet(user),
      );
      markers.add(marker);
    }

    setState(() => _markers = markers);
  }

  void _showUserBottomSheet(NearbyUser user) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            NearbyUserItem(
              user: user,
              onMessageTap: () => _startChat(user),
            ),
          ],
        ),
      ),
    );
  }

  void _startChat(NearbyUser user) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ChatPage(
          userId: user.userId,
          userName: user.nickname,
        ),
      ),
    );
  }

  void _onSortChanged(String sortBy) {
    setState(() => _sortBy = sortBy);
    _loadNearbyUsers();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('附近的人'),
        actions: [
          IconButton(
            icon: Icon(_isMapView ? Icons.list : Icons.map),
            onPressed: () {
              setState(() => _isMapView = !_isMapView);
              if (_isMapView) _updateMapMarkers();
            },
          ),
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: _showFilterSheet,
          ),
        ],
      ),
      body: _isMapView ? _buildMapView() : _buildListView(),
    );
  }

  Widget _buildListView() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_error!),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadNearbyUsers,
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    if (_nearbyUsers.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.people_outline, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text('附近暂无用户', style: TextStyle(color: Colors.grey)),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadNearbyUsers,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _nearbyUsers.length,
        itemBuilder: (context, index) {
          final user = _nearbyUsers[index];
          return Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: NearbyUserItem(
              user: user,
              onTap: () => _showUserDetail(user),
              onMessageTap: () => _startChat(user),
            ),
          );
        },
      ),
    );
  }

  Widget _buildMapView() {
    return GoogleMap(
      initialCameraPosition: const CameraPosition(
        target: LatLng(39.9042, 116.4074),
        zoom: 14,
      ),
      markers: _markers,
      myLocationEnabled: true,
      myLocationButtonEnabled: true,
      onMapCreated: (controller) {
        _mapController = controller;
        _updateMapMarkers();
      },
    );
  }

  void _showUserDetail(NearbyUser user) {
    // 显示用户详情
  }

  void _showFilterSheet() {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              '排序方式',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              children: [
                _buildSortChip('距离最近', 'distance'),
                _buildSortChip('相似度最高', 'similarity'),
                _buildSortChip('最近活跃', 'active'),
              ],
            ),
            const SizedBox(height: 24),
            const Text(
              '距离范围',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            _buildDistanceSlider(),
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
        if (selected) {
          _onSortChanged(value);
          Navigator.pop(context);
        }
      },
    );
  }

  Widget _buildDistanceSlider() {
    return StatefulBuilder(
      builder: (context, setState) {
        return Column(
          children: [
            Slider(
              value: _maxDistance,
              min: 1000,
              max: 20000,
              divisions: 19,
              label: _maxDistance < 1000
                  ? '${_maxDistance.toInt()}m'
                  : '${(_maxDistance / 1000).toStringAsFixed(1)}km',
              onChanged: (value) => setState(() => _maxDistance = value),
              onChangeEnd: (value) {
                _maxDistance = value;
                _loadNearbyUsers();
              },
            ),
            Text(
              '最大距离: ${_maxDistance < 1000 ? '${_maxDistance.toInt()}m' : '${(_maxDistance / 1000).toStringAsFixed(1)}km'}',
            ),
          ],
        );
      },
    );
  }

  @override
  void dispose() {
    _mapController?.dispose();
    super.dispose();
  }
}

/// 位置分享历史页面
class LocationShareHistoryPage extends StatefulWidget {
  const LocationShareHistoryPage({Key? key}) : super(key: key);

  @override
  State<LocationShareHistoryPage> createState() => _LocationShareHistoryPageState();
}

class _LocationShareHistoryPageState extends State<LocationShareHistoryPage> {
  final ApiService _apiService = ApiService();
  bool _isLoading = true;
  List<LocationShare> _shares = [];
  List<LiveLocationSession> _liveSessions = [];

  @override
  void initState() {
    super.initState();
    _loadShares();
  }

  Future<void> _loadShares() async {
    setState(() => _isLoading = true);

    try {
      // 加载静态分享
      final response = await _apiService.get('/lbs/location/shares');
      final data = jsonDecode(response.body);
      final List<dynamic> shareList = data['data']['shares'] ?? [];
      
      // 加载实时分享
      final liveResponse = await _apiService.get('/lbs/location/live-sessions');
      final liveData = jsonDecode(liveResponse.body);
      final List<dynamic> sessionList = liveData['data']['sessions'] ?? [];

      setState(() {
        _shares = shareList.map((json) => LocationShare.fromJson(json)).toList();
        _liveSessions = sessionList
            .map((json) => LiveLocationSession.fromJson(json))
            .toList();
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _stopSharing(String shareId) async {
    try {
      await _apiService.post('/lbs/location/share/$shareId/stop', {});
      _loadShares();
    } catch (e) {
      // 处理错误
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('位置分享'),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadShares,
              child: ListView(
                children: [
                  // 实时分享
                  if (_liveSessions.isNotEmpty) ...[
                    _buildSectionHeader('实时位置分享'),
                    ..._liveSessions.map(_buildLiveSessionCard),
                  ],
                  
                  // 静态位置分享
                  if (_shares.isNotEmpty) ...[
                    _buildSectionHeader('位置分享记录'),
                    ..._shares.map(_buildShareCard),
                  ],
                  
                  if (_shares.isEmpty && _liveSessions.isEmpty)
                    const Center(
                      child: Padding(
                        padding: EdgeInsets.all(48),
                        child: Text(
                          '暂无位置分享记录',
                          style: TextStyle(color: Colors.grey),
                        ),
                      ),
                    ),
                ],
              ),
            ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
      child: Text(
        title,
        style: TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.bold,
          color: Colors.grey[700],
        ),
      ),
    );
  }

  Widget _buildLiveSessionCard(LiveLocationSession session) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      child: ListTile(
        leading: const Icon(Icons.location_on, color: Colors.green),
        title: Text('实时分享给 ${session.userName ?? '用户'}'),
        subtitle: Text('剩余 ${_formatDuration(session.remainingTime)}'),
        trailing: TextButton(
          onPressed: () => _stopSharing(session.id),
          child: const Text('停止'),
        ),
      ),
    );
  }

  Widget _buildShareCard(LocationShare share) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      child: ListTile(
        leading: const Icon(Icons.location_on, color: Colors.blue),
        title: Text(share.address ?? '位置分享'),
        subtitle: Text(
          share.isExpired 
              ? '已过期' 
              : '剩余 ${share.remainingMinutes}分钟',
        ),
        trailing: share.isExpired
            ? null
            : TextButton(
                onPressed: () => _stopSharing(share.id),
                child: const Text('停止'),
              ),
      ),
    );
  }

  String _formatDuration(Duration duration) {
    final hours = duration.inHours;
    final minutes = duration.inMinutes % 60;
    if (hours > 0) {
      return '${hours}小时${minutes}分钟';
    }
    return '${minutes}分钟';
  }
}

// 占位符
// ignore: constant_identifier_names
class Icons {
  static const String list = 'list';
  static const String map = 'map';
  static const String filter_list = 'filter_list';
  static const String people_outline = 'people_outline';
  static const String location_on = 'location_on';
}
