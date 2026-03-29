import 'package:flutter/material.dart';
import '../../models/live/live_room_model.dart';
import '../../models/live/live_product_model.dart';
import '../../services/live/mini_program_live_service.dart';
import '../../services/live/live_commerce_service.dart';

/// 主播端直播间页面
class LiveStreamerPage extends StatefulWidget {
  final String? roomId; // 传入roomId则进入已有直播间，否则创建新直播间

  const LiveStreamerPage({
    Key? key,
    this.roomId,
  }) : super(key: key);

  @override
  State<LiveStreamerPage> createState() => _LiveStreamerPageState();
}

class _LiveStreamerPageState extends State<LiveStreamerPage> {
  final MiniProgramLiveService _liveService = MiniProgramLiveService();
  final LiveCommerceService _commerceService = LiveCommerceService();

  bool _isBeautyEnabled = true;
  int _beautyLevel = 3;
  bool _isMuted = false;
  bool _showProductPanel = false;
  bool _showDataPanel = false;

  @override
  void initState() {
    super.initState();
    
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _initializeLive();
    });
  }

  Future<void> _initializeLive() async {
    if (widget.roomId != null) {
      // 继续已有直播
      // TODO: 恢复直播状态
    }
  }

  @override
  void dispose() {
    _liveService.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          // 预览画面
          Container(
            color: Colors.black,
            child: Center(
              child: Text(
                '摄像头预览区域',
                style: TextStyle(color: Colors.white54),
              ),
            ),
          ),

          // 顶部状态栏
          _buildTopStatusBar(),

          // 右侧控制栏
          _buildRightControls(),

          // 底部功能区
          _buildBottomControls(),

          // 商品管理面板
          if (_showProductPanel) _buildProductManagementPanel(),

          // 数据面板
          if (_showDataPanel) _buildDataPanel(),
        ],
      ),
    );
  }

  /// 构建顶部状态栏
  Widget _buildTopStatusBar() {
    return Positioned(
      top: MediaQuery.of(context).padding.top,
      left: 0,
      right: 0,
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Colors.black54, Colors.transparent],
          ),
        ),
        child: SafeArea(
          child: Row(
            children: [
              // 关闭按钮
              IconButton(
                icon: Icon(Icons.close, color: Colors.white),
                onPressed: () => _showEndLiveDialog(),
              ),

              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // 直播时长
                    ValueListenableBuilder(
                      valueListenable: _liveService,
                      builder: (context, value, child) {
                        final duration = _liveService.liveDuration;
                        return Text(
                          '${duration.inHours.toString().padLeft(2, '0')}:${(duration.inMinutes % 60).toString().padLeft(2, '0')}:${(duration.inSeconds % 60).toString().padLeft(2, '0')}',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        );
                      },
                    ),
                    // 在线人数
                    Text(
                      '${_liveService.onlineCount} 在线',
                      style: TextStyle(color: Colors.white70, fontSize: 12),
                    ),
                  ],
                ),
              ),

              // 网络状态
              Row(
                children: [
                  Icon(Icons.signal_cellular_alt, color: Colors.green, size: 18),
                  SizedBox(width: 4),
                  Text('网络良好', style: TextStyle(color: Colors.white70, fontSize: 12)),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// 构建右侧控制栏
  Widget _buildRightControls() {
    return Positioned(
      right: 16,
      top: MediaQuery.of(context).padding.top + 80,
      child: Column(
        children: [
          // 切换摄像头
          _buildControlButton(
            icon: Icons.flip_camera_ios,
            onTap: () => _liveService.switchCamera(),
          ),
          SizedBox(height: 12),

          // 美颜开关
          _buildControlButton(
            icon: _isBeautyEnabled ? Icons.face : Icons.face_retouching_off,
            color: _isBeautyEnabled ? Colors.pink : Colors.white,
            onTap: () {
              setState(() => _isBeautyEnabled = !_isBeautyEnabled);
              _liveService.toggleBeauty(_isBeautyEnabled);
            },
          ),
          SizedBox(height: 12),

          // 美颜级别
          if (_isBeautyEnabled)
            Container(
              padding: EdgeInsets.symmetric(vertical: 8, horizontal: 4),
              decoration: BoxDecoration(
                color: Colors.black54,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [1, 2, 3, 4, 5].map((level) {
                  return GestureDetector(
                    onTap: () {
                      setState(() => _beautyLevel = level);
                      _liveService.setBeautyLevel(level);
                    },
                    child: Container(
                      width: 32,
                      height: 32,
                      margin: EdgeInsets.symmetric(vertical: 2),
                      decoration: BoxDecoration(
                        color: _beautyLevel == level ? Colors.pink : Colors.transparent,
                        shape: BoxShape.circle,
                      ),
                      child: Center(
                        child: Text(
                          '$level',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 12,
                            fontWeight: _beautyLevel == level ? FontWeight.bold : FontWeight.normal,
                          ),
                        ),
                      ),
                    ),
                  );
                }).toList(),
              ),
            ),
          if (_isBeautyEnabled) SizedBox(height: 12),

          // 静音开关
          _buildControlButton(
            icon: _isMuted ? Icons.mic_off : Icons.mic,
            color: _isMuted ? Colors.red : Colors.white,
            onTap: () {
              setState(() => _isMuted = !_isMuted);
              _liveService.toggleMute(_isMuted);
            },
          ),
          SizedBox(height: 12),

          // 闪光灯
          _buildControlButton(
            icon: Icons.flash_on,
            onTap: () => _liveService.toggleFlash(),
          ),
        ],
      ),
    );
  }

  /// 构建控制按钮
  Widget _buildControlButton({
    required IconData icon,
    Color color = Colors.white,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 44,
        height: 44,
        decoration: BoxDecoration(
          color: Colors.black54,
          shape: BoxShape.circle,
        ),
        child: Icon(icon, color: color, size: 24),
      ),
    );
  }

  /// 构建底部控制栏
  Widget _buildBottomControls() {
    return Positioned(
      left: 0,
      right: 0,
      bottom: 0,
      child: Container(
        padding: EdgeInsets.all(16),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.bottomCenter,
            end: Alignment.topCenter,
            colors: [Colors.black87, Colors.transparent],
          ),
        ),
        child: SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // 功能按钮行
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // 商品
                  _buildBottomButton(
                    icon: Icons.shopping_bag,
                    label: '商品',
                    onTap: () {
                      setState(() {
                        _showProductPanel = !_showProductPanel;
                        _showDataPanel = false;
                      });
                    },
                  ),

                  // 数据
                  _buildBottomButton(
                    icon: Icons.bar_chart,
                    label: '数据',
                    onTap: () {
                      setState(() {
                        _showDataPanel = !_showDataPanel;
                        _showProductPanel = false;
                      });
                    },
                  ),

                  // 开始/暂停直播按钮
                  if (_liveService.currentStatus == LiveRoomStatus.idle)
                    _buildStartButton()
                  else if (_liveService.currentStatus == LiveRoomStatus.pushing)
                    _buildPauseButton()
                  else if (_liveService.currentStatus == LiveRoomStatus.paused)
                    _buildResumeButton(),

                  // 互动
                  _buildBottomButton(
                    icon: Icons.chat,
                    label: '互动',
                    onTap: () {
                      _showInteractionPanel();
                    },
                  ),

                  // 更多
                  _buildBottomButton(
                    icon: Icons.more_horiz,
                    label: '更多',
                    onTap: () {
                      _showMoreOptions();
                    },
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// 构建底部按钮
  Widget _buildBottomButton({
    required IconData icon,
    required String label,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, color: Colors.white, size: 28),
          SizedBox(height: 4),
          Text(label, style: TextStyle(color: Colors.white, fontSize: 12)),
        ],
      ),
    );
  }

  /// 构建开始直播按钮
  Widget _buildStartButton() {
    return GestureDetector(
      onTap: () => _startLive(),
      child: Container(
        width: 72,
        height: 72,
        decoration: BoxDecoration(
          color: Colors.red,
          shape: BoxShape.circle,
          boxShadow: [
            BoxShadow(
              color: Colors.red.withOpacity(0.5),
              blurRadius: 10,
              spreadRadius: 2,
            ),
          ],
        ),
        child: Center(
          child: Text(
            '开始\n直播',
            textAlign: TextAlign.center,
            style: TextStyle(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }

  /// 构建暂停按钮
  Widget _buildPauseButton() {
    return GestureDetector(
      onTap: () => _liveService.pauseLive(),
      child: Container(
        width: 72,
        height: 72,
        decoration: BoxDecoration(
          color: Colors.orange,
          shape: BoxShape.circle,
        ),
        child: Center(
          child: Icon(Icons.pause, color: Colors.white, size: 32),
        ),
      ),
    );
  }

  /// 构建恢复按钮
  Widget _buildResumeButton() {
    return GestureDetector(
      onTap: () => _liveService.resumeLive(),
      child: Container(
        width: 72,
        height: 72,
        decoration: BoxDecoration(
          color: Colors.green,
          shape: BoxShape.circle,
        ),
        child: Center(
          child: Icon(Icons.play_arrow, color: Colors.white, size: 32),
        ),
      ),
    );
  }

  /// 构建商品管理面板
  Widget _buildProductManagementPanel() {
    return Positioned(
      left: 0,
      right: 0,
      bottom: 100,
      height: 300,
      child: Container(
        padding: EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
        ),
        child: Column(
          children: [
            // 标题栏
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '商品管理',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                IconButton(
                  icon: Icon(Icons.close),
                  onPressed: () => setState(() => _showProductPanel = false),
                ),
              ],
            ),

            // 当前讲解商品
            if (_liveService.currentProduct != null)
              Container(
                padding: EdgeInsets.all(12),
                margin: EdgeInsets.only(bottom: 12),
                decoration: BoxDecoration(
                  color: Colors.pink.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.pink),
                ),
                child: Row(
                  children: [
                    Icon(Icons.play_circle_outline, color: Colors.pink),
                    SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        '当前讲解: ${_liveService.currentProduct!.name}',
                        style: TextStyle(color: Colors.pink),
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        _liveService.switchCurrentProduct('');
                      },
                      child: Text('结束讲解'),
                    ),
                  ],
                ),
              ),

            // 商品列表
            Expanded(
              child: ListView.builder(
                itemCount: _liveService.products.length,
                itemBuilder: (context, index) {
                  final product = _liveService.products[index];
                  final isCurrent = _liveService.currentProduct?.id == product.id;

                  return ListTile(
                    leading: product.images?.isNotEmpty == true
                        ? Image.network(
                            product.images!.first,
                            width: 50,
                            height: 50,
                            fit: BoxFit.cover,
                          )
                        : Container(
                            width: 50,
                            height: 50,
                            color: Colors.grey[300],
                          ),
                    title: Text(product.name),
                    subtitle: Text('¥${product.price}'),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        if (!isCurrent)
                          TextButton(
                            onPressed: () {
                              _liveService.switchCurrentProduct(product.id);
                            },
                            child: Text('讲解'),
                          )
                        else
                          Container(
                            padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                            decoration: BoxDecoration(
                              color: Colors.pink,
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: Text(
                              '讲解中',
                              style: TextStyle(color: Colors.white, fontSize: 12),
                            ),
                          ),
                        IconButton(
                          icon: Icon(Icons.delete_outline, color: Colors.grey),
                          onPressed: () {
                            _liveService.removeProduct(product.id);
                          },
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),

            // 添加商品按钮
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: () {
                  _showAddProductDialog();
                },
                icon: Icon(Icons.add),
                label: Text('添加商品'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// 构建数据面板
  Widget _buildDataPanel() {
    return Positioned(
      left: 0,
      right: 0,
      bottom: 100,
      height: 250,
      child: Container(
        padding: EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
        ),
        child: Column(
          children: [
            // 标题栏
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '实时数据',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                IconButton(
                  icon: Icon(Icons.close),
                  onPressed: () => setState(() => _showDataPanel = false),
                ),
              ],
            ),

            // 数据网格
            Expanded(
              child: GridView.count(
                crossAxisCount: 3,
                childAspectRatio: 1.5,
                children: [
                  _buildDataItem('观看人数', '${_liveService.onlineCount}'),
                  _buildDataItem('点赞数', '${_liveService.likeCount}'),
                  _buildDataItem('评论数', '0'),
                  _buildDataItem('礼物价值', '¥0'),
                  _buildDataItem('订单数', '0'),
                  _buildDataItem('销售额', '¥0'),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// 构建数据项
  Widget _buildDataItem(String label, String value) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          value,
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Colors.black87,
          ),
        ),
        SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(fontSize: 12, color: Colors.grey[600]),
        ),
      ],
    );
  }

  /// 开始直播
  Future<void> _startLive() async {
    // 检查是否有直播间
    if (_liveService.currentRoom == null) {
      // 创建直播间
      final title = '我的直播';
      final description = '欢迎大家来到我的直播间！';
      
      try {
        await _liveService.createLiveRoom(
          title: title,
          description: description,
        );
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('创建直播间失败: $e')),
        );
        return;
      }
    }

    // 开始推流
    try {
      await _liveService.startPushing();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('直播已开始')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('开始直播失败: $e')),
      );
    }
  }

  /// 显示结束直播对话框
  void _showEndLiveDialog() {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('结束直播'),
          content: Text('确定要结束直播吗？'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text('取消'),
            ),
            ElevatedButton(
              onPressed: () async {
                Navigator.pop(context);
                await _liveService.stopPushing();
                Navigator.pop(context);
              },
              style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
              child: Text('结束'),
            ),
          ],
        );
      },
    );
  }

  /// 显示添加商品对话框
  void _showAddProductDialog() {
    // TODO: 实现添加商品功能
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('添加商品'),
          content: Text('从商品库选择商品'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: Text('取消'),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
                // TODO: 跳转到商品选择页面
              },
              child: Text('去选择'),
            ),
          ],
        );
      },
    );
  }

  /// 显示互动面板
  void _showInteractionPanel() {
    // TODO: 实现互动面板
  }

  /// 显示更多选项
  void _showMoreOptions() {
    showModalBottomSheet(
      context: context,
      builder: (context) {
        return SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ListTile(
                leading: Icon(Icons.settings),
                title: Text('直播设置'),
                onTap: () {
                  Navigator.pop(context);
                  // TODO: 直播设置
                },
              ),
              ListTile(
                leading: Icon(Icons.block),
                title: Text('屏蔽词设置'),
                onTap: () {
                  Navigator.pop(context);
                  // TODO: 屏蔽词设置
                },
              ),
              ListTile(
                leading: Icon(Icons.help_outline),
                title: Text('帮助'),
                onTap: () {
                  Navigator.pop(context);
                  // TODO: 帮助
                },
              ),
            ],
          ),
        );
      },
    );
  }
}
