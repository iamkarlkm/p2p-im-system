import 'dart:convert';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../models/live/live_room_model.dart';
import '../../utils/logger.dart';

/// 直播播放器组件
/// 封装小程序直播播放器功能
class LivePlayerWidget extends StatefulWidget {
  final String roomId;
  final Function(String)? onError;

  const LivePlayerWidget({
    Key? key,
    required this.roomId,
    this.onError,
  }) : super(key: key);

  @override
  State<LivePlayerWidget> createState() => _LivePlayerWidgetState();
}

class _LivePlayerWidgetState extends State<LivePlayerWidget> {
  static const platform = MethodChannel('com.im.live/player');
  bool _isInitialized = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _initializePlayer();
  }

  @override
  void dispose() {
    _destroyPlayer();
    super.dispose();
  }

  Future<void> _initializePlayer() async {
    try {
      await platform.invokeMethod('initialize', {
        'roomId': widget.roomId,
      });
      setState(() => _isInitialized = true);
    } on PlatformException catch (e) {
      Logger.error('LivePlayerWidget', 'Failed to initialize player', e);
      setState(() => _errorMessage = e.message);
      widget.onError?.call(e.message ?? '初始化失败');
    }
  }

  Future<void> _destroyPlayer() async {
    try {
      await platform.invokeMethod('destroy');
    } on PlatformException catch (e) {
      Logger.error('LivePlayerWidget', 'Failed to destroy player', e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_errorMessage != null) {
      return Container(
        color: Colors.black,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline, color: Colors.white54, size: 48),
              SizedBox(height: 16),
              Text(
                '播放错误',
                style: TextStyle(color: Colors.white54),
              ),
              SizedBox(height: 8),
              Text(
                _errorMessage!,
                style: TextStyle(color: Colors.white38, fontSize: 12),
              ),
            ],
          ),
        ),
      );
    }

    // 使用AndroidView/UiKitView嵌入原生播放器
    return Container(
      color: Colors.black,
      child: Center(
        child: _isInitialized
            ? AndroidView(
                viewType: 'com.im.live/LivePlayerView',
                creationParams: {'roomId': widget.roomId},
                creationParamsCodec: StandardMessageCodec(),
              )
            : CircularProgressIndicator(),
      ),
    );
  }
}

/// 直播聊天组件
class LiveChatWidget extends StatefulWidget {
  final String roomId;
  final Function(String)? onSendMessage;

  const LiveChatWidget({
    Key? key,
    required this.roomId,
    this.onSendMessage,
  }) : super(key: key);

  @override
  State<LiveChatWidget> createState() => _LiveChatWidgetState();
}

class _LiveChatWidgetState extends State<LiveChatWidget> {
  final List<LiveMessageModel> _messages = [];
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _textController = TextEditingController();
  bool _showInput = false;

  @override
  void dispose() {
    _scrollController.dispose();
    _textController.dispose();
    super.dispose();
  }

  void _addMessage(LiveMessageModel message) {
    setState(() {
      _messages.add(message);
      // 只保留最近100条消息
      if (_messages.length > 100) {
        _messages.removeAt(0);
      }
    });
    
    // 滚动到底部
    Future.delayed(Duration(milliseconds: 100), () {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: Duration(milliseconds: 200),
          curve: Curves.easeOut,
        );
      }
    });
  }

  void _sendMessage() {
    final text = _textController.text.trim();
    if (text.isEmpty) return;

    widget.onSendMessage?.call(text);
    _textController.clear();
    
    // 添加自己的消息到列表
    _addMessage(LiveMessageModel(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      roomId: widget.roomId,
      type: LiveMessageType.chat,
      userName: '我',
      content: text,
      timestamp: DateTime.now(),
    ));
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // 消息列表
        Expanded(
          child: GestureDetector(
            onTap: () {
              setState(() => _showInput = false);
              FocusScope.of(context).unfocus();
            },
            child: Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.bottomCenter,
                  end: Alignment.topCenter,
                  colors: [Colors.black54, Colors.transparent],
                ),
              ),
              child: ListView.builder(
                controller: _scrollController,
                padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                itemCount: _messages.length,
                itemBuilder: (context, index) {
                  final message = _messages[index];
                  return _buildMessageItem(message);
                },
              ),
            ),
          ),
        ),

        // 输入区域
        if (_showInput)
          Container(
            padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            color: Colors.black87,
            child: SafeArea(
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _textController,
                      style: TextStyle(color: Colors.white),
                      decoration: InputDecoration(
                        hintText: '说点什么...',
                        hintStyle: TextStyle(color: Colors.white54),
                        filled: true,
                        fillColor: Colors.white12,
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(20),
                          borderSide: BorderSide.none,
                        ),
                        contentPadding: EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 12,
                        ),
                      ),
                      onSubmitted: (_) => _sendMessage(),
                    ),
                  ),
                  SizedBox(width: 8),
                  IconButton(
                    icon: Icon(Icons.send, color: Colors.pink),
                    onPressed: _sendMessage,
                  ),
                ],
              ),
            ),
          )
        else
          GestureDetector(
            onTap: () => setState(() => _showInput = true),
            child: Container(
              padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              margin: EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.black54,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(Icons.chat_bubble_outline, color: Colors.white70, size: 18),
                  SizedBox(width: 8),
                  Text(
                    '说点什么...',
                    style: TextStyle(color: Colors.white70),
                  ),
                ],
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildMessageItem(LiveMessageModel message) {
    Color textColor = Colors.white;
    
    switch (message.type) {
      case LiveMessageType.enter:
        textColor = Colors.green;
        break;
      case LiveMessageType.gift:
        textColor = Colors.yellow;
        break;
      case LiveMessageType.system:
        textColor = Colors.orange;
        break;
      default:
        textColor = Colors.white;
    }

    return Container(
      margin: EdgeInsets.only(bottom: 4),
      child: RichText(
        text: TextSpan(
          children: [
            if (message.userName != null)
              TextSpan(
                text: '${message.userName}: ',
                style: TextStyle(
                  color: Colors.pink,
                  fontSize: 13,
                  fontWeight: FontWeight.w500,
                ),
              ),
            TextSpan(
              text: message.displayText,
              style: TextStyle(color: textColor, fontSize: 13),
            ),
          ],
        ),
      ),
    );
  }
}

/// 直播商品列表组件
class LiveProductListWidget extends StatelessWidget {
  final String roomId;
  final List<LiveProductModel> products;
  final LiveProductModel? currentProduct;
  final Function(LiveProductModel)? onAddToCart;
  final Function(LiveProductModel)? onBuyNow;

  const LiveProductListWidget({
    Key? key,
    required this.roomId,
    required this.products,
    this.currentProduct,
    this.onAddToCart,
    this.onBuyNow,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
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
                  '直播商品 (${products.length})',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                if (currentProduct != null)
                  Container(
                    padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: Colors.pink,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      '讲解中',
                      style: TextStyle(color: Colors.white, fontSize: 12),
                    ),
                  ),
              ],
            ),
          ),

          // 当前讲解商品（如果有）
          if (currentProduct != null)
            Container(
              padding: EdgeInsets.all(12),
              margin: EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.pink.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.pink),
              ),
              child: Row(
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.circular(4),
                    child: currentProduct!.images?.isNotEmpty == true
                        ? Image.network(
                            currentProduct!.images!.first,
                            width: 80,
                            height: 80,
                            fit: BoxFit.cover,
                          )
                        : Container(
                            width: 80,
                            height: 80,
                            color: Colors.grey[300],
                            child: Icon(Icons.image),
                          ),
                  ),
                  SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '正在讲解',
                          style: TextStyle(color: Colors.pink, fontSize: 12),
                        ),
                        Text(
                          currentProduct!.name,
                          style: TextStyle(fontWeight: FontWeight.bold),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        SizedBox(height: 4),
                        Text(
                          '¥${currentProduct!.price}',
                          style: TextStyle(
                            color: Colors.red,
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Column(
                    children: [
                      ElevatedButton(
                        onPressed: () => onBuyNow?.call(currentProduct!),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.red,
                          foregroundColor: Colors.white,
                        ),
                        child: Text('立即抢'),
                      ),
                      TextButton(
                        onPressed: () => onAddToCart?.call(currentProduct!),
                        child: Text('加购物车'),
                      ),
                    ],
                  ),
                ],
              ),
            ),

          // 商品列表
          Expanded(
            child: ListView.builder(
              itemCount: products.length,
              itemBuilder: (context, index) {
                final product = products[index];
                final isCurrent = currentProduct?.id == product.id;

                return Container(
                  decoration: BoxDecoration(
                    color: isCurrent ? Colors.pink.withOpacity(0.05) : null,
                  ),
                  child: ListTile(
                    leading: ClipRRect(
                      borderRadius: BorderRadius.circular(4),
                      child: product.images?.isNotEmpty == true
                          ? Image.network(
                              product.images!.first,
                              width: 60,
                              height: 60,
                              fit: BoxFit.cover,
                            )
                          : Container(
                              width: 60,
                              height: 60,
                              color: Colors.grey[300],
                            ),
                    ),
                    title: Text(
                      product.name,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '¥${product.price}',
                          style: TextStyle(
                            color: Colors.red,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        if (product.originalPrice != null)
                          Text(
                            '¥${product.originalPrice}',
                            style: TextStyle(
                              decoration: TextDecoration.lineThrough,
                              fontSize: 12,
                            ),
                          ),
                      ],
                    ),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: Icon(Icons.shopping_cart_outlined),
                          onPressed: () => onAddToCart?.call(product),
                        ),
                        ElevatedButton(
                          onPressed: () => onBuyNow?.call(product),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.red,
                            foregroundColor: Colors.white,
                            minimumSize: Size(60, 36),
                          ),
                          child: Text('买'),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

/// 直播礼物组件
class LiveGiftWidget extends StatelessWidget {
  final Function(LiveGiftModel)? onSendGift;

  const LiveGiftWidget({
    Key? key,
    this.onSendGift,
  }) : super(key: key);

  // 模拟礼物数据
  List<LiveGiftModel> get _gifts => [
    LiveGiftModel(
      id: '1',
      name: '爱心',
      icon: 'assets/gifts/heart.png',
      price: 1.0,
    ),
    LiveGiftModel(
      id: '2',
      name: '玫瑰',
      icon: 'assets/gifts/rose.png',
      price: 10.0,
    ),
    LiveGiftModel(
      id: '3',
      name: '火箭',
      icon: 'assets/gifts/rocket.png',
      price: 100.0,
    ),
    LiveGiftModel(
      id: '4',
      name: '跑车',
      icon: 'assets/gifts/car.png',
      price: 500.0,
    ),
    LiveGiftModel(
      id: '5',
      name: '游艇',
      icon: 'assets/gifts/yacht.png',
      price: 1000.0,
    ),
    LiveGiftModel(
      id: '6',
      name: '飞机',
      icon: 'assets/gifts/plane.png',
      price: 2000.0,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
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
                  '送礼物',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                IconButton(
                  icon: Icon(Icons.close),
                  onPressed: () => Navigator.pop(context),
                ),
              ],
            ),
          ),

          // 礼物网格
          GridView.builder(
            shrinkWrap: true,
            physics: NeverScrollableScrollPhysics(),
            padding: EdgeInsets.all(16),
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 4,
              childAspectRatio: 0.8,
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
            ),
            itemCount: _gifts.length,
            itemBuilder: (context, index) {
              final gift = _gifts[index];
              return GestureDetector(
                onTap: () => onSendGift?.call(gift),
                child: Container(
                  decoration: BoxDecoration(
                    color: Colors.grey[100],
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.card_giftcard, size: 40, color: Colors.pink),
                      SizedBox(height: 8),
                      Text(
                        gift.name,
                        style: TextStyle(fontSize: 12),
                      ),
                      SizedBox(height: 4),
                      Text(
                        '¥${gift.price}',
                        style: TextStyle(
                          color: Colors.red,
                          fontSize: 12,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          ),

          // 余额提示
          Container(
            padding: EdgeInsets.all(16),
            decoration: BoxDecoration(
              border: Border(
                top: BorderSide(color: Colors.grey[300]!),
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('余额: ¥1000.00'),
                TextButton(
                  onPressed: () {
                    // TODO: 充值
                  },
                  child: Text('充值'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
