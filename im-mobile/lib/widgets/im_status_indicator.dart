import 'package:flutter/material.dart';
import '../im/im_client.dart';

/// IM状态指示器
class IMStatusIndicator extends StatelessWidget {
  final IMClient imClient;

  const IMStatusIndicator({Key? key, required this.imClient}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<ConnectionStatus>(
      stream: imClient.statusStream,
      initialData: imClient.status,
      builder: (context, snapshot) {
        final status = snapshot.data ?? ConnectionStatus.disconnected;
        return _buildStatusWidget(status);
      },
    );
  }

  Widget _buildStatusWidget(ConnectionStatus status) {
    switch (status) {
      case ConnectionStatus.connected:
        return _statusDot(Colors.green, '已连接');
      case ConnectionStatus.connecting:
        return _statusDot(Colors.orange, '连接中...');
      case ConnectionStatus.error:
        return _statusDot(Colors.red, '连接错误');
      case ConnectionStatus.disconnected:
        return _statusDot(Colors.grey, '未连接');
    }
  }

  Widget _statusDot(Color color, String text) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 8,
          height: 8,
          decoration: BoxDecoration(
            color: color,
            shape: BoxShape.circle,
          ),
        ),
        SizedBox(width: 4),
        Text(
          text,
          style: TextStyle(fontSize: 12, color: color),
        ),
      ],
    );
  }
}
