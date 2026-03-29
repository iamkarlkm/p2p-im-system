import 'dart:async';

import 'package:flutter/material.dart';
import 'package:im_mobile/models/location/poi_model.dart';
import 'package:im_mobile/models/navigation/route_plan_model.dart';
import 'package:im_mobile/services/navigation/navigation_service.dart';
import 'package:im_mobile/utils/logger.dart';

/// 导航页面
/// 
/// 提供完整的导航界面，包括地图显示、路线信息、转向提示等
class NavigationPage extends StatefulWidget {
  final POIModel destination;
  final LatLng? startLocation;
  final NavigationMode mode;

  const NavigationPage({
    Key? key,
    required this.destination,
    this.startLocation,
    this.mode = NavigationMode.driving,
  }) : super(key: key);

  @override
  State<NavigationPage> createState() => _NavigationPageState();
}

class _NavigationPageState extends State<NavigationPage> {
  final NavigationService _navigationService = NavigationService();
  
  bool _isLoading = true;
  bool _hasError = false;
  String _errorMessage = '';
  
  List<RoutePlanModel> _routes = [];
  int _selectedRouteIndex = 0;
  
  StreamSubscription? _eventSubscription;

  @override
  void initState() {
    super.initState();
    _initializeNavigation();
  }

  @override
  void dispose() {
    _eventSubscription?.cancel();
    super.dispose();
  }

  Future<void> _initializeNavigation() async {
    try {
      setState(() {
        _isLoading = true;
        _hasError = false;
      });

      await _navigationService.initialize();
      
      // 规划路线
      final routes = await _navigationService.planRoute(
        destination: widget.destination,
        start: widget.startLocation,
      );

      if (routes.isEmpty) {
        setState(() {
          _hasError = true;
          _errorMessage = '未找到可用路线';
          _isLoading = false;
        });
        return;
      }

      setState(() {
        _routes = routes;
        _isLoading = false;
      });

      // 监听导航事件
      _eventSubscription = _navigationService.eventStream.listen(_handleNavigationEvent);
    } catch (e) {
      IMLogger.e('NavigationPage', '初始化导航失败', e);
      setState(() {
        _hasError = true;
        _errorMessage = '导航初始化失败: $e';
        _isLoading = false;
      });
    }
  }

  void _handleNavigationEvent(NavigationEvent event) {
    if (!mounted) return;

    if (event is RouteRecalculatedEvent) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('路线已重新规划')),
      );
    } else if (event is RouteDeviatedEvent) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('已偏离路线，正在重新规划...')),
      );
    } else if (event is NavigationCompletedEvent) {
      _showNavigationCompleteDialog();
    }
  }

  void _showNavigationCompleteDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Text('到达目的地'),
        content: Text('您已到达 ${widget.destination.name}'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              Navigator.of(context).pop();
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  Future<void> _startNavigation() async {
    if (_routes.isEmpty) return;
    
    final route = _routes[_selectedRouteIndex];
    
    await _navigationService.startNavigation(route: route);
    
    if (mounted) {
      Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => NavigationInProgressPage(
            navigationService: _navigationService,
            destination: widget.destination,
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('路线规划'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _initializeNavigation,
          ),
        ],
      ),
      body: _buildBody(),
      bottomNavigationBar: _buildBottomBar(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text('正在规划路线...'),
          ],
        ),
      );
    }

    if (_hasError) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64, color: Colors.red),
            const SizedBox(height: 16),
            Text(_errorMessage),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _initializeNavigation,
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    return Column(
      children: [
        // 地图区域（简化版）
        Expanded(
          flex: 3,
          child: Container(
            color: Colors.grey[200],
            child: const Center(
              child: Text('地图区域', style: TextStyle(fontSize: 18)),
            ),
          ),
        ),
        
        // 路线列表
        Expanded(
          flex: 2,
          child: ListView.builder(
            itemCount: _routes.length,
            itemBuilder: (context, index) {
              final route = _routes[index];
              final isSelected = index == _selectedRouteIndex;
              
              return RouteCard(
                route: route,
                isSelected: isSelected,
                onTap: () => setState(() => _selectedRouteIndex = index),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildBottomBar() {
    if (_isLoading || _hasError) return const SizedBox.shrink();

    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: ElevatedButton.icon(
          onPressed: _startNavigation,
          icon: const Icon(Icons.navigation),
          label: const Text('开始导航', style: TextStyle(fontSize: 18)),
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(double.infinity, 56),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
          ),
        ),
      ),
    );
  }
}

/// 路线卡片
class RouteCard extends StatelessWidget {
  final RoutePlanModel route;
  final bool isSelected;
  final VoidCallback onTap;

  const RouteCard({
    Key? key,
    required this.route,
    required this.isSelected,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      color: isSelected ? Colors.blue[50] : null,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: isSelected
            ? const BorderSide(color: Colors.blue, width: 2)
            : BorderSide.none,
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  // 路线标签
                  if (route.tags.isNotEmpty)
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: Colors.blue,
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        route.tags.first,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 12,
                        ),
                      ),
                    ),
                  const Spacer(),
                  // 路况图标
                  Text(
                    route.trafficIcon,
                    style: const TextStyle(fontSize: 20),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  // 距离
                  Text(
                    route.formattedDistance,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(width: 16),
                  // 时间
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        route.formattedDuration,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      Text(
                        '预计${route.formattedArrivalTime}到达',
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                  const Spacer(),
                  // 费用
                  if (route.hasTolls)
                    Text(
                      '过路费¥${route.tollCost.toStringAsFixed(0)}',
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.orange[700],
                      ),
                    ),
                ],
              ),
              const SizedBox(height: 8),
              // 路线摘要
              Text(
                route.summary,
                style: TextStyle(
                  fontSize: 13,
                  color: Colors.grey[600],
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// 导航进行中页面
class NavigationInProgressPage extends StatefulWidget {
  final NavigationService navigationService;
  final POIModel destination;

  const NavigationInProgressPage({
    Key? key,
    required this.navigationService,
    required this.destination,
  }) : super(key: key);

  @override
  State<NavigationInProgressPage> createState() => _NavigationInProgressPageState();
}

class _NavigationInProgressPageState extends State<NavigationInProgressPage> {
  @override
  void initState() {
    super.initState();
    widget.navigationService.addListener(_onNavigationUpdate);
  }

  @override
  void dispose() {
    widget.navigationService.removeListener(_onNavigationUpdate);
    super.dispose();
  }

  void _onNavigationUpdate() {
    if (mounted) {
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    final route = widget.navigationService.currentRoute;
    final stepIndex = widget.navigationService.currentStepIndex;
    
    if (route == null) {
      return const Scaffold(
        body: Center(child: Text('导航信息加载中...')),
      );
    }

    final currentStep = stepIndex < route.steps.length
        ? route.steps[stepIndex]
        : null;

    return Scaffold(
      body: Stack(
        children: [
          // 地图区域
          Container(
            color: Colors.grey[200],
            child: const Center(
              child: Text('导航地图', style: TextStyle(fontSize: 18)),
            ),
          ),
          
          // 顶部信息栏
          SafeArea(
            child: Container(
              margin: const EdgeInsets.all(16),
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.1),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (currentStep != null) ...[
                    // 转向图标和指示
                    Row(
                      children: [
                        Container(
                          width: 64,
                          height: 64,
                          decoration: BoxDecoration(
                            color: Colors.blue[100],
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Center(
                            child: Text(
                              currentStep.actionIcon,
                              style: const TextStyle(fontSize: 32),
                            ),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                currentStep.actionDescription,
                                style: const TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                currentStep.instruction,
                                style: TextStyle(
                                  fontSize: 14,
                                  color: Colors.grey[600],
                                ),
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const Divider(height: 24),
                  ],
                  
                  // 距离和时间信息
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _buildInfoItem(
                        icon: Icons.straighten,
                        value: '${(widget.navigationService.remainingDistance / 1000).toStringAsFixed(1)}',
                        unit: '公里',
                      ),
                      _buildInfoItem(
                        icon: Icons.access_time,
                        value: '${(widget.navigationService.remainingDuration / 60).ceil()}',
                        unit: '分钟',
                      ),
                      _buildInfoItem(
                        icon: Icons.flag,
                        value: widget.destination.name,
                        unit: '目的地',
                        isSmall: true,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          
          // 底部操作栏
          Positioned(
            left: 0,
            right: 0,
            bottom: 0,
            child: SafeArea(
              child: Container(
                margin: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () {
                          widget.navigationService.stopNavigation();
                          Navigator.of(context).pop();
                        },
                        icon: const Icon(Icons.close),
                        label: const Text('结束导航'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.red,
                          minimumSize: const Size(double.infinity, 48),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: ElevatedButton.icon(
                        onPressed: () {
                          // 切换地图视角
                        },
                        icon: const Icon(Icons.map),
                        label: const Text('地图视图'),
                        style: ElevatedButton.styleFrom(
                          minimumSize: const Size(double.infinity, 48),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoItem({
    required IconData icon,
    required String value,
    required String unit,
    bool isSmall = false,
  }) {
    return Column(
      children: [
        Icon(icon, color: Colors.blue, size: 24),
        const SizedBox(height: 4),
        Text(
          value,
          style: TextStyle(
            fontSize: isSmall ? 14 : 20,
            fontWeight: FontWeight.bold,
          ),
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        Text(
          unit,
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }
}
