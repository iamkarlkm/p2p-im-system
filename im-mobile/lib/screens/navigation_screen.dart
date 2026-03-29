import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';

import '../models/navigation_models.dart';
import '../providers/navigation_provider.dart';
import '../services/navigation_service.dart';
import '../widgets/navigation_bottom_panel.dart';
import '../widgets/navigation_top_panel.dart';
import '../widgets/route_overview_map.dart';

/// 导航页面
/// Navigation Screen
class NavigationScreen extends StatefulWidget {
  final RoutePlan routePlan;
  final bool isSimulated;

  const NavigationScreen({
    Key? key,
    required this.routePlan,
    this.isSimulated = false,
  }) : super(key: key);

  @override
  State<NavigationScreen> createState() => _NavigationScreenState();
}

class _NavigationScreenState extends State<NavigationScreen>
    with WidgetsBindingObserver {
  GoogleMapController? _mapController;
  NavigationProvider? _navigationProvider;
  bool _isMapReady = false;
  bool _showOverview = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);
    SystemChrome.setEnabledSystemUIMode(
      SystemUiMode.immersiveSticky,
    );

    _initNavigation();
  }

  Future<void> _initNavigation() async {
    _navigationProvider = context.read<NavigationProvider>();
    await _navigationProvider!.startNavigation(
      routePlan: widget.routePlan,
      isSimulated: widget.isSimulated,
    );
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _mapController?.dispose();
    _navigationProvider?.endNavigation();
    SystemChrome.setEnabledSystemUIMode(
      SystemUiMode.edgeToEdge,
    );
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
      DeviceOrientation.landscapeLeft,
      DeviceOrientation.landscapeRight,
    ]);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused) {
      _navigationProvider?.pauseNavigation();
    } else if (state == AppLifecycleState.resumed) {
      _navigationProvider?.resumeNavigation();
    }
  }

  void _onMapCreated(GoogleMapController controller) {
    _mapController = controller;
    setState(() {
      _isMapReady = true;
    });
    _fitRouteBounds();
  }

  void _fitRouteBounds() {
    if (_mapController == null || widget.routePlan.routePoints.isEmpty) return;

    final bounds = _calculateBounds(widget.routePlan.routePoints);
    _mapController!.animateCamera(
      CameraUpdate.newLatLngBounds(bounds, 50),
    );
  }

  LatLngBounds _calculateBounds(List<LatLng> points) {
    double minLat = points.first.latitude;
    double maxLat = points.first.latitude;
    double minLng = points.first.longitude;
    double maxLng = points.first.longitude;

    for (var point in points) {
      if (point.latitude < minLat) minLat = point.latitude;
      if (point.latitude > maxLat) maxLat = point.latitude;
      if (point.longitude < minLng) minLng = point.longitude;
      if (point.longitude > maxLng) maxLng = point.longitude;
    }

    return LatLngBounds(
      southwest: LatLng(minLat, minLng),
      northeast: LatLng(maxLat, maxLng),
    );
  }

  void _toggleOverview() {
    setState(() {
      _showOverview = !_showOverview;
    });
    if (_showOverview) {
      _fitRouteBounds();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: Consumer<NavigationProvider>(
        builder: (context, provider, child) {
          return Stack(
            children: [
              // 地图层
              _buildMap(provider),

              // 路线概览（全览模式）
              if (_showOverview)
                RouteOverviewMap(
                  routePlan: widget.routePlan,
                  currentPosition: provider.currentPosition,
                  onClose: _toggleOverview,
                ),

              // 顶部导航信息面板
              if (!_showOverview)
                NavigationTopPanel(
                  navStatus: provider.navigationStatus,
                  onExit: () => _showExitDialog(context),
                ),

              // 底部操作面板
              if (!_showOverview)
                NavigationBottomPanel(
                  navStatus: provider.navigationStatus,
                  onOverviewPressed: _toggleOverview,
                  onVoiceToggle: provider.toggleVoice,
                  isVoiceEnabled: provider.isVoiceEnabled,
                ),

              // 转向提示（全屏）
              if (provider.showTurnHint && !_showOverview)
                _buildTurnHint(provider),

              // 偏航提示
              if (provider.isOffRoute && !_showOverview)
                _buildReroutePrompt(provider),

              // 加载指示器
              if (provider.isLoading)
                const Center(
                  child: CircularProgressIndicator(
                    color: Colors.white,
                  ),
                ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildMap(NavigationProvider provider) {
    return GoogleMap(
      mapType: MapType.normal,
      myLocationEnabled: true,
      myLocationButtonEnabled: false,
      compassEnabled: false,
      mapToolbarEnabled: false,
      zoomControlsEnabled: false,
      tiltGesturesEnabled: true,
      rotateGesturesEnabled: true,
      scrollGesturesEnabled: true,
      zoomGesturesEnabled: true,
      initialCameraPosition: CameraPosition(
        target: widget.routePlan.origin,
        zoom: 18,
        tilt: 45,
      ),
      onMapCreated: _onMapCreated,
      polylines: {
        Polyline(
          polylineId: const PolylineId('route'),
          points: widget.routePlan.routePoints,
          color: Colors.blue,
          width: 8,
          patterns: provider.trafficSegments
              .map((s) => PatternItem.dash(s.length.toDouble()))
              .toList(),
        ),
      },
      markers: {
        Marker(
          markerId: const MarkerId('destination'),
          position: widget.routePlan.destination,
          icon: BitmapDescriptor.defaultMarkerWithHue(
            BitmapDescriptor.hueRed,
          ),
          infoWindow: InfoWindow(
            title: widget.routePlan.destinationName,
          ),
        ),
      },
    );
  }

  Widget _buildTurnHint(NavigationProvider provider) {
    return Positioned.fill(
      child: Container(
        color: Colors.black.withOpacity(0.8),
        child: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                provider.turnIcon,
                size: 120,
                color: Colors.blue,
              ),
              const SizedBox(height: 24),
              Text(
                provider.turnInstruction,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              Text(
                '${provider.remainingDistanceText}后',
                style: TextStyle(
                  color: Colors.grey[400],
                  fontSize: 20,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildReroutePrompt(NavigationProvider provider) {
    return Positioned(
      top: 120,
      left: 16,
      right: 16,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.orange.withOpacity(0.95),
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.3),
              blurRadius: 8,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Row(
          children: [
            const Icon(Icons.warning_amber_rounded, color: Colors.white),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Text(
                    '已偏航',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    '正在重新规划路线...',
                    style: TextStyle(
                      color: Colors.white.withOpacity(0.9),
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ),
            if (provider.isRerouting)
              const SizedBox(
                width: 24,
                height: 24,
                child: CircularProgressIndicator(
                  color: Colors.white,
                  strokeWidth: 2,
                ),
              ),
          ],
        ),
      ),
    );
  }

  void _showExitDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('结束导航'),
        content: const Text('确定要结束当前导航吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              Navigator.pop(context);
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
            ),
            child: const Text('结束'),
          ),
        ],
      ),
    );
  }
}
