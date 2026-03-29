import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/live/live_room_model.dart';
import '../../models/live/live_product_model.dart';
import '../../services/live/mini_program_live_service.dart';
import '../../services/live/live_commerce_service.dart';
import '../../widgets/live/live_player_widget.dart';
import '../../widgets/live/live_chat_widget.dart';
import '../../widgets/live/live_product_list_widget.dart';
import '../../widgets/live/live_gift_widget.dart';

/// 直播间页面（观众端）
class LiveRoomPage extends StatefulWidget {
  final String roomId;

  const LiveRoomPage({
    Key? key,
    required this.roomId,
  }) : super(key: key);

  @override
  State<LiveRoomPage> createState() => _LiveRoomPageState();
}

class _LiveRoomPageState extends State<LiveRoomPage> {
  late MiniProgramLiveService _liveService;
  late LiveCommerceService _commerceService;
  
  bool _showChat = true;
  bool _showProducts = false;
  bool _isLiking = false;

  @override
  void initState() {
    super.initState();
    _liveService = MiniProgramLiveService();
    _commerceService = LiveCommerceService();
    
    // 开始播放
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _liveService.startPlaying(widget.roomId);
    });
  }

  @override
  void dispose() {
    _liveService.stopPlaying();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: _liveService),
        ChangeNotifierProvider.value(value: _commerceService),
      ],
      child: Scaffold(
        body: Consumer<MiniProgramLiveService>(
          builder: (context, liveService, child) {
            if (liveService.currentStatus == LiveRoomStatus.error) {
              return _buildErrorView(liveService.errorMessage);
            }

            return Stack(
              children: [
                // 直播播放器
                LivePlayerWidget(
                  roomId: widget.roomId,
                  onError: (error) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('播放错误: $error')),
                    );
                  },
                ),

                // 顶部信息栏
                _buildTopBar(liveService),

                // 右侧功能区
                _buildRightActions(liveService),

                // 底部聊天区
                if (_showChat) _buildChatArea(),

                // 商品列表
                if (_showProducts) _buildProductPanel(),

                // 加载指示器
                if (liveService.currentStatus == LiveRoomStatus.connecting)
                  Center(child: CircularProgressIndicator()),
              ],
            );
          },
        ),
      ),
    );
  }

  /// 构建错误视图
  Widget _buildErrorView(String? error) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 64, color: Colors.grey),
          SizedBox(height: 16),
          Text(
            '直播加载失败',
            style: TextStyle(fontSize: 18, color: Colors.grey[700]),
          ),
          if (error != null)
            Padding(
              padding: EdgeInsets.all(16),
              child: Text(
                error,
                style: TextStyle(fontSize: 14, color: Colors.grey[600]),
                textAlign: TextAlign.center,
              ),
            ),
          SizedBox(height: 24),
          ElevatedButton(
            onPressed: () => _liveService.startPlaying(widget.roomId),
            child: Text('重试'),
          ),
          SizedBox(height: 12),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('返回'),
          ),
        ],
      ),
    );
  }

  /// 构建顶部栏
  Widget _buildTopBar(MiniProgramLiveService liveService) {
    final room = liveService.currentRoom;
    
    return Positioned(
      top: MediaQuery.of(context).padding.top,
      left: 0,
      right: 0,
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
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
              // 返回按钮
              IconButton(
                icon: Icon(Icons.arrow_back, color: Colors.white),
                onPressed: () => Navigator.pop(context),
              ),

              // 主播信息
              if (room != null) ...[
                // 头像
                CircleAvatar(
                  radius: 20,
                  backgroundImage: room.streamerAvatar != null
                      ? NetworkImage(room.streamerAvatar!)
                      : null,
                  child: room.streamerAvatar == null
                      ? Icon(Icons.person, color: Colors.white)
                      : null,
                ),
                SizedBox(width: 8),

                // 主播名和在线人数
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        room.streamerName,
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        '${liveService.onlineCount} 人在看',
                        style: TextStyle(
                          color: Colors.white70,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),

                // 关注按钮
                ElevatedButton(
                  onPressed: () {
                    // TODO: 关注主播
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.pink,
                    foregroundColor: Colors.white,
                    padding: EdgeInsets.symmetric(horizontal: 16),
                    minimumSize: Size(60, 32),
                  ),
                  child: Text('关注'),
                ),
              ],

              SizedBox(width: 8),

              // 更多选项
              IconButton(
                icon: Icon(Icons.more_vert, color: Colors.white),
                onPressed: () {
                  _showMoreOptions();
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// 构建右侧功能区
  Widget _buildRightActions(MiniProgramLiveService liveService) {
    return Positioned(
      right: 8,
      bottom: 120,
      child: Column(
        children: [
          // 点赞按钮
          _buildActionButton(
            icon: Icons.favorite,
            color: _isLiking ? Colors.red : Colors.white,
            label: '${liveService.likeCount}',
            onTap: () {
              liveService.sendLike();
              setState(() => _isLiking = true);
              Future.delayed(Duration(milliseconds: 200), () {
                if (mounted) setState(() => _isLiking = false);
              });
            },
          ),
          SizedBox(height: 16),

          // 商品按钮
          _buildActionButton(
            icon: Icons.shopping_bag,
            color: _showProducts ? Colors.yellow : Colors.white,
            label: '商品',
            badge: _commerceService.getCartItemCount(widget.roomId) > 0
                ? _commerceService.getCartItemCount(widget.roomId).toString()
                : null,
            onTap: () {
              setState(() {
                _showProducts = !_showProducts;
              });
            },
          ),
          SizedBox(height: 16),

          // 购物车按钮
          _buildActionButton(
            icon: Icons.shopping_cart,
            color: Colors.white,
            label: '购物车',
            badge: _commerceService.getCartItemCount(widget.roomId) > 0
                ? _commerceService.getCartItemCount(widget.roomId).toString()
                : null,
            onTap: () {
              _showCart();
            },
          ),
          SizedBox(height: 16),

          // 礼物按钮
          _buildActionButton(
            icon: Icons.card_giftcard,
            color: Colors.white,
            label: '礼物',
            onTap: () {
              _showGiftPanel();
            },
          ),
          SizedBox(height: 16),

          // 分享按钮
          _buildActionButton(
            icon: Icons.share,
            color: Colors.white,
            label: '分享',
            onTap: () {
              _shareLive();
            },
          ),
        ],
      ),
    );
  }

  /// 构建功能按钮
  Widget _buildActionButton({
    required IconData icon,
    required Color color,
    required String label,
    String? badge,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Column(
        children: [
          Stack(
            children: [
              Container(
                width: 44,
                height: 44,
                decoration: BoxDecoration(
                  color: Colors.black38,
                  shape: BoxShape.circle,
                ),
                child: Icon(icon, color: color, size: 24),
              ),
              if (badge != null)
                Positioned(
                  right: 0,
                  top: 0,
                  child: Container(
                    padding: EdgeInsets.all(4),
                    decoration: BoxDecoration(
                      color: Colors.red,
                      shape: BoxShape.circle,
                    ),
                    constraints: BoxConstraints(minWidth: 18, minHeight: 18),
                    child: Text(
                      badge,
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
            ],
          ),
          SizedBox(height: 4),
          Text(
            label,
            style: TextStyle(color: Colors.white, fontSize: 11),
          ),
        ],
      ),
    );
  }

  /// 构建聊天区域
  Widget _buildChatArea() {
    return Positioned(
      left: 0,
      bottom: 0,
      width: MediaQuery.of(context).size.width * 0.75,
      height: 200,
      child: LiveChatWidget(
        roomId: widget.roomId,
        onSendMessage: (message) {
          _liveService.sendChatMessage(message);
        },
      ),
    );
  }

  /// 构建商品面板
  Widget _buildProductPanel() {
    return Positioned(
      right: 0,
      bottom: 0,
      width: MediaQuery.of(context).size.width * 0.85,
      height: MediaQuery.of(context).size.height * 0.6,
      child: LiveProductListWidget(
        roomId: widget.roomId,
        products: _liveService.products,
        currentProduct: _liveService.currentProduct,
        onAddToCart: (product) async {
          try {
            await _commerceService.addToCart(widget.roomId, product);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('已添加到购物车')),
            );
          } catch (e) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(e.toString())),
            );
          }
        },
        onBuyNow: (product) {
          _quickBuy(product);
        },
      ),
    );
  }

  /// 显示购物车
  void _showCart() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return DraggableScrollableSheet(
          initialChildSize: 0.6,
          maxChildSize: 0.9,
          minChildSize: 0.3,
          builder: (context, scrollController) {
            return Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
              ),
              child: Column(
                children: [
                  // 标题栏
                  Container(
                    padding: EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      border: Border(
                        bottom: BorderSide(color: Colors.grey[300]!),
                      ),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          '购物车 (${_commerceService.getCartItemCount(widget.roomId)})',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        TextButton(
                          onPressed: () {
                            _commerceService.clearCart(widget.roomId);
                            Navigator.pop(context);
                          },
                          child: Text('清空'),
                        ),
                      ],
                    ),
                  ),

                  // 商品列表
                  Expanded(
                    child: ListView.builder(
                      controller: scrollController,
                      itemCount: _commerceService.getCartItems(widget.roomId).length,
                      itemBuilder: (context, index) {
                        final item = _commerceService.getCartItems(widget.roomId)[index];
                        return ListTile(
                          leading: item.product.images?.isNotEmpty == true
                              ? Image.network(
                                  item.product.images!.first,
                                  width: 60,
                                  height: 60,
                                  fit: BoxFit.cover,
                                )
                              : Container(
                                  width: 60,
                                  height: 60,
                                  color: Colors.grey[300],
                                  child: Icon(Icons.image),
                                ),
                          title: Text(item.product.name),
                          subtitle: Text('¥${item.product.price} x ${item.quantity}'),
                          trailing: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              IconButton(
                                icon: Icon(Icons.remove_circle_outline),
                                onPressed: () {
                                  _commerceService.updateCartQuantity(
                                    widget.roomId,
                                    item.product.id,
                                    item.quantity - 1,
                                  );
                                },
                              ),
                              Text('${item.quantity}'),
                              IconButton(
                                icon: Icon(Icons.add_circle_outline),
                                onPressed: () {
                                  _commerceService.updateCartQuantity(
                                    widget.roomId,
                                    item.product.id,
                                    item.quantity + 1,
                                  );
                                },
                              ),
                            ],
                          ),
                        );
                      },
                    ),
                  ),

                  // 结算栏
                  Container(
                    padding: EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      border: Border(
                        top: BorderSide(color: Colors.grey[300]!),
                      ),
                    ),
                    child: Row(
                      children: [
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('合计'),
                              Text(
                                '¥${_commerceService.getCartTotalAmount(widget.roomId).toStringAsFixed(2)}',
                                style: TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.red,
                                ),
                              ),
                            ],
                          ),
                        ),
                        ElevatedButton(
                          onPressed: () {
                            _createOrder();
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.red,
                            foregroundColor: Colors.white,
                            padding: EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                          ),
                          child: Text('去结算'),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  /// 快速购买
  void _quickBuy(LiveProductModel product) {
    // 直接进入订单确认页
    Navigator.pushNamed(
      context,
      '/live/order-confirm',
      arguments: {
        'roomId': widget.roomId,
        'product': product,
        'quantity': 1,
      },
    );
  }

  /// 创建订单
  void _createOrder() {
    Navigator.pushNamed(
      context,
      '/live/order-confirm',
      arguments: {
        'roomId': widget.roomId,
        'cartItems': _commerceService.getCartItems(widget.roomId),
      },
    );
  }

  /// 显示礼物面板
  void _showGiftPanel() {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return LiveGiftWidget(
          onSendGift: (gift) async {
            try {
              await _liveService.sendGift(gift);
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('送出 ${gift.name}')),
              );
            } catch (e) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('送礼失败: $e')),
              );
            }
          },
        );
      },
    );
  }

  /// 分享直播
  void _shareLive() {
    // TODO: 实现分享功能
    showModalBottomSheet(
      context: context,
      builder: (context) {
        return Container(
          padding: EdgeInsets.all(16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text('分享到', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  _buildShareOption(Icons.wechat, '微信', Colors.green),
                  _buildShareOption(Icons.circle, '朋友圈', Colors.green),
                  _buildShareOption(Icons.link, '复制链接', Colors.blue),
                  _buildShareOption(Icons.qr_code, '二维码', Colors.purple),
                ],
              ),
            ],
          ),
        );
      },
    );
  }

  /// 构建分享选项
  Widget _buildShareOption(IconData icon, String label, Color color) {
    return Column(
      children: [
        CircleAvatar(
          radius: 28,
          backgroundColor: color.withOpacity(0.1),
          child: Icon(icon, color: color),
        ),
        SizedBox(height: 8),
        Text(label, style: TextStyle(fontSize: 12)),
      ],
    );
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
                leading: Icon(Icons.report_problem_outlined),
                title: Text('举报'),
                onTap: () {
                  Navigator.pop(context);
                  // TODO: 举报
                },
              ),
              ListTile(
                leading: Icon(Icons.block),
                title: Text('屏蔽'),
                onTap: () {
                  Navigator.pop(context);
                  // TODO: 屏蔽
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
