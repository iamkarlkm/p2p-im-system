import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../models/location_model.dart';
import '../../services/lbs/location_service.dart';
import '../../services/api_service.dart';
import '../../utils/logger.dart';

/// 位置分享底部弹窗
/// 支持分享当前位置或实时位置
class LocationShareSheet extends StatefulWidget {
  final LatLng currentLocation;
  final String? address;

  const LocationShareSheet({
    Key? key,
    required this.currentLocation,
    this.address,
  }) : super(key: key);

  @override
  State<LocationShareSheet> createState() => _LocationShareSheetState();
}

class _LocationShareSheetState extends State<LocationShareSheet> {
  // 分享类型
  ShareType _shareType = ShareType.static;
  
  // 有效期选项
  int _durationMinutes = 60;
  final List<int> _durationOptions = [15, 30, 60, 120, 240];
  
  // 附加消息
  final TextEditingController _messageController = TextEditingController();
  
  // 分享中状态
  bool _isSharing = false;
  
  // 服务
  final ApiService _apiService = ApiService();

  Future<void> _shareLocation() async {
    setState(() => _isSharing = true);

    try {
      final expiresAt = DateTime.now().add(Duration(minutes: _durationMinutes));
      
      final request = {
        'latitude': widget.currentLocation.latitude,
        'longitude': widget.currentLocation.longitude,
        'address': widget.address,
        'message': _messageController.text.isNotEmpty 
            ? _messageController.text 
            : null,
        'expiresAt': expiresAt.toIso8601String(),
        'isLive': _shareType == ShareType.live,
        'durationMinutes': _shareType == ShareType.live ? _durationMinutes : null,
      };

      final response = await _apiService.post('/lbs/location/share', request);
      
      if (mounted) {
        Navigator.pop(context, true);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('位置分享成功')),
        );
      }
    } catch (e) {
      Logger.e('LocationShareSheet', '分享失败: $e');
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('分享失败: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _isSharing = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom + 16,
      ),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 拖动指示器
          Center(
            child: Container(
              margin: const EdgeInsets.only(top: 12),
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: Colors.grey[300],
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
          
          // 标题
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              '分享位置',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ),
          
          // 位置预览
          _buildLocationPreview(),
          
          const Divider(),
          
          // 分享类型选择
          _buildShareTypeSelector(),
          
          const Divider(),
          
          // 有效期选择
          _buildDurationSelector(),
          
          const Divider(),
          
          // 附加消息
          _buildMessageInput(),
          
          const SizedBox(height: 16),
          
          // 分享按钮
          _buildShareButton(),
        ],
      ),
    );
  }

  Widget _buildLocationPreview() {
    return Container(
      height: 120,
      margin: const EdgeInsets.symmetric(horizontal: 16),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: GoogleMap(
          initialCameraPosition: CameraPosition(
            target: widget.currentLocation,
            zoom: 16,
          ),
          markers: {
            Marker(
              markerId: const MarkerId('share_location'),
              position: widget.currentLocation,
            ),
          },
          zoomControlsEnabled: false,
          mapToolbarEnabled: false,
          myLocationButtonEnabled: false,
        ),
      ),
    );
  }

  Widget _buildShareTypeSelector() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '分享方式',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: _buildTypeCard(
                  title: '当前位置',
                  subtitle: '分享固定位置',
                  icon: Icons.location_pin,
                  isSelected: _shareType == ShareType.static,
                  onTap: () => setState(() => _shareType = ShareType.static),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildTypeCard(
                  title: '实时位置',
                  subtitle: '持续更新位置',
                  icon: Icons.route,
                  isSelected: _shareType == ShareType.live,
                  onTap: () => setState(() => _shareType = ShareType.live),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildTypeCard({
    required String title,
    required String subtitle,
    required IconData icon,
    required bool isSelected,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: isSelected ? Colors.blue[50] : Colors.grey[50],
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isSelected ? Colors.blue : Colors.grey[300]!,
            width: isSelected ? 2 : 1,
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: isSelected ? Colors.blue : Colors.grey),
            const SizedBox(height: 8),
            Text(
              title,
              style: TextStyle(
                fontWeight: FontWeight.bold,
                color: isSelected ? Colors.blue[700] : Colors.black87,
              ),
            ),
            Text(
              subtitle,
              style: TextStyle(fontSize: 12, color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDurationSelector() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            _shareType == ShareType.live ? '实时分享时长' : '位置有效期',
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            children: _durationOptions.map((minutes) {
              final isSelected = _durationMinutes == minutes;
              final label = minutes < 60 
                  ? '$minutes分钟' 
                  : '${minutes ~/ 60}小时';
              
              return ChoiceChip(
                label: Text(label),
                selected: isSelected,
                onSelected: (selected) {
                  if (selected) setState(() => _durationMinutes = minutes);
                },
                selectedColor: Colors.blue[100],
                labelStyle: TextStyle(
                  color: isSelected ? Colors.blue[700] : Colors.grey[700],
                ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildMessageInput() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: TextField(
        controller: _messageController,
        decoration: const InputDecoration(
          hintText: '添加留言（可选）',
          prefixIcon: Icon(Icons.message),
          border: OutlineInputBorder(),
          contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        ),
        maxLines: 2,
        maxLength: 100,
      ),
    );
  }

  Widget _buildShareButton() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: SizedBox(
        width: double.infinity,
        child: ElevatedButton(
          onPressed: _isSharing ? null : _shareLocation,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.blue,
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(vertical: 14),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
          ),
          child: _isSharing
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(
                    strokeWidth: 2,
                    valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                  ),
                )
              : const Text('发送位置', style: TextStyle(fontSize: 16)),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _messageController.dispose();
    super.dispose();
  }
}

enum ShareType { static, live }

// 占位符
// ignore: constant_identifier_names
class Icons {
  static const String location_pin = 'location_pin';
  static const String route = 'route';
  static const String message = 'message';
}
