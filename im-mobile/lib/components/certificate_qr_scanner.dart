import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';

/// 证书二维码扫描器
/// 用于扫描和导入证书二维码
class CertificateQrScanner extends StatefulWidget {
  const CertificateQrScanner({Key? key}) : super(key: key);

  @override
  State<CertificateQrScanner> createState() => _CertificateQrScannerState();
}

class _CertificateQrScannerState extends State<CertificateQrScanner> 
    with SingleTickerProviderStateMixin {
  MobileScannerController? _controller;
  bool _isScanning = true;
  bool _isProcessing = false;
  String? _lastScanResult;
  
  // 扫描动画
  late AnimationController _animationController;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _initScanner();
    _initAnimation();
  }

  void _initScanner() {
    try {
      _controller = MobileScannerController(
        detectionSpeed: DetectionSpeed.normal,
        facing: CameraFacing.back,
        torchEnabled: false,
      );
    } catch (e) {
      // 相机初始化失败
    }
  }

  void _initAnimation() {
    _animationController = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );
    
    _animation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: Curves.easeInOut,
      ),
    );
    
    _animationController.repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller?.dispose();
    _animationController.dispose();
    super.dispose();
  }

  void _onDetect(BarcodeCapture capture) {
    if (!_isScanning || _isProcessing) return;
    
    final barcodes = capture.barcodes;
    if (barcodes.isEmpty) return;
    
    final barcode = barcodes.first;
    final rawValue = barcode.rawValue;
    
    if (rawValue == null || rawValue == _lastScanResult) return;
    
    setState(() {
      _isProcessing = true;
      _lastScanResult = rawValue;
    });
    
    // 暂停扫描
    _controller?.stop();
    
    // 处理扫描结果
    _processScanResult(rawValue);
  }

  void _processScanResult(String data) {
    // 尝试解析证书数据
    _showResultDialog(data);
  }

  void _showResultDialog(String data) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.qr_code_scanner, color: Colors.green),
            SizedBox(width: 8),
            Text('扫描成功'),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('扫描到的数据:', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                data.length > 200 ? '${data.substring(0, 200)}...' : data,
                style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
              ),
            ),
            const SizedBox(height: 16),
            const Text('如何处理此数据?'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _resumeScanning();
            },
            child: const Text('继续扫描'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              Navigator.pop(context, data);
            },
            child: const Text('使用此数据'),
          ),
        ],
      ),
    );
  }

  void _resumeScanning() {
    setState(() {
      _isProcessing = false;
    });
    _controller?.start();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('扫描证书二维码'),
        actions: [
          // 手电筒开关
          IconButton(
            icon: ValueListenableBuilder(
              valueListenable: _controller?.torchState ?? ValueNotifier(TorchState.off),
              builder: (context, state, child) {
                return Icon(
                  state == TorchState.on ? Icons.flash_on : Icons.flash_off,
                );
              },
            ),
            onPressed: () => _controller?.toggleTorch(),
          ),
          // 切换摄像头
          IconButton(
            icon: const Icon(Icons.flip_camera_ios),
            onPressed: () => _controller?.switchCamera(),
          ),
        ],
      ),
      body: Stack(
        fit: StackFit.expand,
        children: [
          // 相机预览
          if (_controller != null)
            MobileScanner(
              controller: _controller!,
              onDetect: _onDetect,
            )
          else
            _buildNoCameraView(),
          
          // 扫描覆盖层
          if (_controller != null)
            _buildScanOverlay(),
          
          // 底部控制栏
          if (_controller != null)
            _buildBottomControls(),
        ],
      ),
    );
  }

  Widget _buildNoCameraView() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.camera_alt, size: 64, color: Colors.grey[400]),
          const SizedBox(height: 16),
          Text(
            '无法访问相机',
            style: TextStyle(
              fontSize: 18,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '请检查相机权限设置',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[500],
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: _manualInput,
            icon: const Icon(Icons.edit),
            label: const Text('手动输入'),
          ),
        ],
      ),
    );
  }

  Widget _buildScanOverlay() {
    return Stack(
      fit: StackFit.expand,
      children: [
        // 半透明遮罩
        ColorFiltered(
          colorFilter: ColorFilter.mode(
            Colors.black.withOpacity(0.5),
            BlendMode.srcOut,
          ),
          child: Stack(
            fit: StackFit.expand,
            children: [
              Container(
                decoration: const BoxDecoration(
                  color: Colors.black,
                  backgroundBlendMode: BlendMode.dstOut,
                ),
              ),
              // 扫描框
              Center(
                child: Container(
                  width: 250,
                  height: 250,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
              ),
            ],
          ),
        ),
        
        // 扫描动画线
        Center(
          child: AnimatedBuilder(
            animation: _animation,
            builder: (context, child) {
              return Container(
                width: 250,
                height: 2,
                margin: EdgeInsets.only(
                  top: (_animation.value - 0.5) * 240,
                ),
                decoration: BoxDecoration(
                  color: Colors.green,
                  boxShadow: [
                    BoxShadow(
                      color: Colors.green.withOpacity(0.5),
                      blurRadius: 8,
                      spreadRadius: 2,
                    ),
                  ],
                ),
              );
            },
          ),
        ),
        
        // 角标
        Center(
          child: SizedBox(
            width: 250,
            height: 250,
            child: Stack(
              children: [
                // 左上角
                Positioned(
                  left: 0,
                  top: 0,
                  child: _buildCorner(true, true),
                ),
                // 右上角
                Positioned(
                  right: 0,
                  top: 0,
                  child: _buildCorner(false, true),
                ),
                // 左下角
                Positioned(
                  left: 0,
                  bottom: 0,
                  child: _buildCorner(true, false),
                ),
                // 右下角
                Positioned(
                  right: 0,
                  bottom: 0,
                  child: _buildCorner(false, false),
                ),
              ],
            ),
          ),
        ),
        
        // 提示文字
        Positioned(
          bottom: 120,
          left: 0,
          right: 0,
          child: Center(
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              decoration: BoxDecoration(
                color: Colors.black54,
                borderRadius: BorderRadius.circular(20),
              ),
              child: const Text(
                '将二维码对准扫描框',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildCorner(bool isLeft, bool isTop) {
    return Container(
      width: 30,
      height: 30,
      decoration: BoxDecoration(
        border: Border(
          left: isLeft ? const BorderSide(color: Colors.green, width: 3) : BorderSide.none,
          right: !isLeft ? const BorderSide(color: Colors.green, width: 3) : BorderSide.none,
          top: isTop ? const BorderSide(color: Colors.green, width: 3) : BorderSide.none,
          bottom: !isTop ? const BorderSide(color: Colors.green, width: 3) : BorderSide.none,
        ),
      ),
    );
  }

  Widget _buildBottomControls() {
    return Positioned(
      bottom: 0,
      left: 0,
      right: 0,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.7),
          borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
        ),
        child: SafeArea(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              // 图库选择
              _buildControlButton(
                icon: Icons.photo_library,
                label: '图库',
                onTap: _pickFromGallery,
              ),
              
              // 开始/停止扫描
              _buildControlButton(
                icon: _isScanning ? Icons.stop : Icons.play_arrow,
                label: _isScanning ? '停止' : '开始',
                onTap: _toggleScanning,
                isPrimary: true,
              ),
              
              // 手动输入
              _buildControlButton(
                icon: Icons.edit,
                label: '手动输入',
                onTap: _manualInput,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildControlButton({
    required IconData icon,
    required String label,
    required VoidCallback onTap,
    bool isPrimary = false,
  }) {
    return InkWell(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: isPrimary ? Colors.green : Colors.white24,
              shape: BoxShape.circle,
            ),
            child: Icon(
              icon,
              color: Colors.white,
              size: isPrimary ? 32 : 24,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  void _toggleScanning() {
    setState(() {
      _isScanning = !_isScanning;
    });
    
    if (_isScanning) {
      _controller?.start();
    } else {
      _controller?.stop();
    }
  }

  Future<void> _pickFromGallery() async {
    // TODO: 实现从图库选择二维码图片
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('图库选择功能开发中...')),
    );
  }

  void _manualInput() {
    final controller = TextEditingController();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('手动输入证书数据'),
        content: TextField(
          controller: controller,
          maxLines: 5,
          decoration: const InputDecoration(
            hintText: '粘贴证书数据或扫描二维码内容...',
            border: OutlineInputBorder(),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              final data = controller.text.trim();
              Navigator.pop(context);
              if (data.isNotEmpty) {
                _processScanResult(data);
              }
            },
            child: const Text('确认'),
          ),
        ],
      ),
    );
  }
}
