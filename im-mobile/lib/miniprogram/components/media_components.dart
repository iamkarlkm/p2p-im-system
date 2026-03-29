import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';

import 'base_component.dart';

/// 富文本组件
class RichTextComponent extends MiniBaseComponent {
  const RichTextComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final nodes = attributes['nodes'];
    final selectable = parseBoolAttribute('selectable') ?? false;
    
    if (nodes is String) {
      return SelectableText(
        nodes,
        enabled: selectable,
      );
    } else if (nodes is List) {
      return Text(
        nodes.map((n) => n.toString()).join(),
      );
    }
    
    return const SizedBox.shrink();
  }
}

/// 进度条组件
class ProgressComponent extends MiniBaseComponent {
  const ProgressComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final percent = double.tryParse(attributes['percent']?.toString() ?? '0') ?? 0;
    final showInfo = parseBoolAttribute('show-info') ?? true;
    final borderRadius = parseIntAttribute('border-radius')?.toDouble() ?? 0;
    final fontSize = parseFontSize();
    final color = parseColor() ?? Colors.green;
    final activeColor = parseColor() ?? Colors.green;
    final backgroundColor = parseBackgroundColor() ?? Colors.grey[300];
    final activeMode = attributes['active-mode']?.toString() ?? 'backwards';
    
    return Row(
      children: [
        Expanded(
          child: ClipRRect(
            borderRadius: BorderRadius.circular(borderRadius),
            child: LinearProgressIndicator(
              value: percent / 100,
              backgroundColor: backgroundColor,
              valueColor: AlwaysStoppedAnimation<Color>(activeColor),
              minHeight: 6,
            ),
          ),
        ),
        if (showInfo) ...[
          const SizedBox(width: 8),
          Text(
            '${percent.toStringAsFixed(1)}%',
            style: TextStyle(fontSize: fontSize),
          ),
        ],
      ],
    );
  }
}

/// 视频组件
class VideoComponent extends MiniBaseComponent {
  const VideoComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final src = attributes['src']?.toString() ?? '';
    final duration = parseIntAttribute('duration') ?? 0;
    final controls = parseBoolAttribute('controls') ?? true;
    final autoplay = parseBoolAttribute('autoplay') ?? false;
    final loop = parseBoolAttribute('loop') ?? false;
    final muted = parseBoolAttribute('muted') ?? false;
    final initialTime = parseIntAttribute('initial-time') ?? 0;
    final poster = attributes['poster']?.toString();
    final objectFit = attributes['object-fit']?.toString() ?? 'contain';
    final showMuteBtn = parseBoolAttribute('show-mute-btn') ?? false;
    final title = attributes['title']?.toString();
    final playBtnPosition = attributes['play-btn-position']?.toString() ?? 'bottom';
    final showScreenLockButton = parseBoolAttribute('show-screen-lock-button') ?? false;
    final showSnapshotButton = parseBoolAttribute('show-snapshot-button') ?? false;
    final showBackgroundPlaybackButton = parseBoolAttribute('show-background-playback-button') ?? false;
    final showProgress = parseBoolAttribute('show-progress') ?? true;
    final showFullscreenBtn = parseBoolAttribute('show-fullscreen-btn') ?? true;
    final showPlayBtn = parseBoolAttribute('show-play-btn') ?? true;
    final showCenterPlayBtn = parseBoolAttribute('show-center-play-btn') ?? true;
    final enableProgressGesture = parseBoolAttribute('enable-progress-gesture') ?? true;
    final enablePlayGesture = parseBoolAttribute('enable-play-gesture') ?? true;
    final showCastingButton = parseBoolAttribute('show-casting-button') ?? false;
    final width = parseWidth() ?? double.infinity;
    final height = parseHeight() ?? 225;
    
    if (src.isEmpty) {
      return Container(
        width: width,
        height: height,
        color: Colors.black,
        child: const Center(
          child: Icon(Icons.videocam_off, color: Colors.white, size: 48),
        ),
      );
    }
    
    return Container(
      width: width,
      height: height,
      color: Colors.black,
      child: Stack(
        alignment: Alignment.center,
        children: [
          if (poster != null)
            Image.network(
              poster,
              fit: BoxFit.cover,
              width: width,
              height: height,
            ),
          if (showPlayBtn || showCenterPlayBtn)
            IconButton(
              icon: const Icon(Icons.play_circle_outline, color: Colors.white, size: 64),
              onPressed: () => onEvent?.call('play', {}),
            ),
          if (controls)
            Positioned(
              bottom: 0,
              left: 0,
              right: 0,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                color: Colors.black54,
                child: Row(
                  children: [
                    IconButton(
                      icon: const Icon(Icons.play_arrow, color: Colors.white, size: 20),
                      onPressed: () {},
                      padding: EdgeInsets.zero,
                      constraints: const BoxConstraints(),
                    ),
                    if (showProgress)
                      Expanded(
                        child: Slider(
                          value: 0,
                          onChanged: enableProgressGesture ? (v) {} : null,
                          activeColor: Colors.white,
                          inactiveColor: Colors.white38,
                        ),
                      ),
                    if (showFullscreenBtn)
                      IconButton(
                        icon: const Icon(Icons.fullscreen, color: Colors.white, size: 20),
                        onPressed: () {},
                        padding: EdgeInsets.zero,
                        constraints: const BoxConstraints(),
                      ),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}

/// 音频组件
class AudioComponent extends MiniBaseComponent {
  const AudioComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final src = attributes['src']?.toString() ?? '';
    final loop = parseBoolAttribute('loop') ?? false;
    final controls = parseBoolAttribute('controls') ?? true;
    final poster = attributes['poster']?.toString();
    final name = attributes['name']?.toString() ?? '未知音频';
    final author = attributes['author']?.toString() ?? '未知作者';
    
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          if (poster != null)
            ClipRRect(
              borderRadius: BorderRadius.circular(4),
              child: Image.network(
                poster,
                width: 48,
                height: 48,
                fit: BoxFit.cover,
              ),
            ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                Text(
                  author,
                  style: TextStyle(color: Colors.grey[600], fontSize: 12),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
          IconButton(
            icon: const Icon(Icons.play_arrow),
            onPressed: () => onEvent?.call('play', {}),
          ),
        ],
      ),
    );
  }
}

/// 相机组件
class CameraComponent extends MiniBaseComponent {
  const CameraComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final mode = attributes['mode']?.toString() ?? 'normal';
    final resolution = attributes['resolution']?.toString() ?? 'medium';
    final devicePosition = attributes['device-position']?.toString() ?? 'back';
    final flash = attributes['flash']?.toString() ?? 'auto';
    final frameSize = attributes['frame-size']?.toString() ?? 'large';
    final width = parseWidth() ?? double.infinity;
    final height = parseHeight() ?? 300;
    
    return Container(
      width: width,
      height: height,
      color: Colors.black,
      child: Stack(
        alignment: Alignment.center,
        children: [
          const Center(
            child: Icon(Icons.camera_alt, color: Colors.white, size: 48),
          ),
          Positioned(
            bottom: 16,
            left: 0,
            right: 0,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(
                  icon: const Icon(Icons.cameraswitch, color: Colors.white),
                  onPressed: () => onEvent?.call('switch', {}),
                ),
                const SizedBox(width: 32),
                FloatingActionButton(
                  onPressed: () => onEvent?.call('takephoto', {}),
                  child: const Icon(Icons.camera),
                ),
                const SizedBox(width: 32),
                IconButton(
                  icon: const Icon(Icons.flash_on, color: Colors.white),
                  onPressed: () => onEvent?.call('flash', {}),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// 图片裁剪组件
class ImageCropperComponent extends MiniBaseComponent {
  const ImageCropperComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final src = attributes['src']?.toString();
    final width = parseWidth() ?? 300;
    final height = parseHeight() ?? 300;
    
    return Container(
      width: width,
      height: height,
      color: Colors.grey[900],
      child: src != null
        ? Image.network(
            src,
            fit: BoxFit.contain,
          )
        : const Center(
            child: Text('No image', style: TextStyle(color: Colors.white)),
          ),
    );
  }
}

/// 地图组件
class MapComponent extends MiniBaseComponent {
  final dynamic markers;
  
  const MapComponent({
    super.key,
    required super.attributes,
    this.markers,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final longitude = double.tryParse(attributes['longitude']?.toString() ?? '0') ?? 0;
    final latitude = double.tryParse(attributes['latitude']?.toString() ?? '0') ?? 0;
    final scale = parseIntAttribute('scale') ?? 16;
    final markers = attributes['markers'] as List<dynamic>?;
    final polyline = attributes['polyline'] as List<dynamic>?;
    final circles = attributes['circles'] as List<dynamic>?;
    final controls = attributes['controls'] as List<dynamic>?;
    final includePoints = attributes['include-points'] as List<dynamic>?;
    final showLocation = parseBoolAttribute('show-location') ?? false;
    final showCompass = parseBoolAttribute('show-compass') ?? false;
    final showScale = parseBoolAttribute('show-scale') ?? false;
    final enableOverlooking = parseBoolAttribute('enable-overlooking') ?? false;
    final enableZoom = parseBoolAttribute('enable-zoom') ?? true;
    final enableScroll = parseBoolAttribute('enable-scroll') ?? true;
    final enableRotate = parseBoolAttribute('enable-rotate') ?? false;
    final enableSatellite = parseBoolAttribute('enable-satellite') ?? false;
    final enableTraffic = parseBoolAttribute('enable-traffic') ?? false;
    final enablePoi = parseBoolAttribute('enable-poi') ?? true;
    final enableBuilding = parseBoolAttribute('enable-building') ?? true;
    final width = parseWidth() ?? double.infinity;
    final height = parseHeight() ?? 300;
    
    return Container(
      width: width,
      height: height,
      color: Colors.grey[300],
      child: Stack(
        alignment: Alignment.center,
        children: [
          const Icon(Icons.map, size: 64, color: Colors.grey),
          if (showLocation)
            const Positioned(
              right: 16,
              bottom: 80,
              child: FloatingActionButton.small(
                onPressed: null,
                child: Icon(Icons.my_location),
              ),
            ),
        ],
      ),
    );
  }
}

/// 画布组件
class CanvasComponent extends MiniBaseComponent {
  const CanvasComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final type = attributes['type']?.toString() ?? '2d';
    final width = parseWidth() ?? 300;
    final height = parseHeight() ?? 150;
    final disableScroll = parseBoolAttribute('disable-scroll') ?? false;
    
    return GestureDetector(
      onTapDown: (details) => onEvent?.call('touchstart', {
        'x': details.localPosition.dx,
        'y': details.localPosition.dy,
      }),
      onTapUp: (details) => onEvent?.call('touchend', {
        'x': details.localPosition.dx,
        'y': details.localPosition.dy,
      }),
      child: Container(
        width: width,
        height: height,
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey),
        ),
        child: CustomPaint(
          size: Size(width, height),
          painter: CanvasPainter(),
        ),
      ),
    );
  }
}

class CanvasPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.white
      ..style = PaintingStyle.fill;
    
    canvas.drawRect(Offset.zero & size, paint);
  }
  
  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

/// 开放数据组件
class OpenDataComponent extends MiniBaseComponent {
  const OpenDataComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final type = attributes['type']?.toString() ?? 'userAvatarUrl';
    final defaultText = attributes['default-text']?.toString();
    final defaultAvatar = attributes['default-avatar']?.toString();
    final useEmptyValue = parseBoolAttribute('use-empty-value') ?? false;
    
    switch (type) {
      case 'userAvatarUrl':
        return CircleAvatar(
          backgroundImage: defaultAvatar != null ? NetworkImage(defaultAvatar) : null,
          child: defaultAvatar == null ? const Icon(Icons.person) : null,
        );
      case 'userNickName':
        return Text(defaultText ?? '用户昵称');
      case 'userGender':
        return Text(defaultText ?? '未知');
      case 'userCity':
      case 'userProvince':
      case 'userCountry':
        return Text(defaultText ?? '未知地区');
      default:
        return const SizedBox.shrink();
    }
  }
}

/// WebView组件
class WebViewComponent extends MiniBaseComponent {
  const WebViewComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final src = attributes['src']?.toString() ?? '';
    final width = parseWidth() ?? double.infinity;
    final height = parseHeight() ?? 300;
    
    return Container(
      width: width,
      height: height,
      color: Colors.white,
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            color: Colors.grey[200],
            child: Row(
              children: [
                const Icon(Icons.lock, size: 16, color: Colors.green),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    src,
                    style: const TextStyle(fontSize: 12),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
          ),
          const Expanded(
            child: Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(Icons.web, size: 48, color: Colors.grey),
                  SizedBox(height: 8),
                  Text('WebView Content', style: TextStyle(color: Colors.grey)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

/// 广告组件
class AdComponent extends MiniBaseComponent {
  const AdComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final unitId = attributes['unit-id']?.toString() ?? '';
    final adType = attributes['ad-type']?.toString() ?? 'banner';
    final adTheme = attributes['ad-theme']?.toString() ?? 'white';
    final adSize = attributes['ad-size']?.toString() ?? 'normal';
    
    return Container(
      height: adType == 'banner' ? 80 : 200,
      color: adTheme == 'white' ? Colors.grey[100] : Colors.grey[800],
      child: Center(
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.campaign, color: adTheme == 'white' ? Colors.grey : Colors.white70),
            const SizedBox(width: 8),
            Text(
              '广告',
              style: TextStyle(
                color: adTheme == 'white' ? Colors.grey : Colors.white70,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// 自定义广告组件
class AdCustomComponent extends MiniBaseComponent {
  const AdCustomComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final unitId = attributes['unit-id']?.toString() ?? '';
    final width = parseWidth() ?? double.infinity;
    final height = parseHeight() ?? 200;
    
    return Container(
      width: width,
      height: height,
      color: Colors.grey[100],
      child: const Center(
        child: Text('自定义广告'),
      ),
    );
  }
}

/// 公众号关注组件
class OfficialAccountComponent extends MiniBaseComponent {
  const OfficialAccountComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: const CircleAvatar(
        child: Icon(Icons.account_circle),
      ),
      title: const Text('公众号'),
      subtitle: const Text('点击关注'),
      trailing: ElevatedButton(
        onPressed: () => onEvent?.call('tap', {}),
        child: const Text('关注'),
      ),
    );
  }
}

/// 公众号文章组件
class OpenArticleComponent extends MiniBaseComponent {
  const OpenArticleComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final title = attributes['title']?.toString() ?? '文章标题';
    final thumbUrl = attributes['thumb-url']?.toString();
    
    return Card(
      child: InkWell(
        onTap: () => onEvent?.call('tap', {}),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              if (thumbUrl != null)
                ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: Image.network(
                    thumbUrl,
                    width: 60,
                    height: 60,
                    fit: BoxFit.cover,
                  ),
                ),
              const SizedBox(width: 12),
              Expanded(
                child: Text(
                  title,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              const Icon(Icons.arrow_forward_ios, size: 16),
            ],
          ),
        ),
      ),
    );
  }
}
