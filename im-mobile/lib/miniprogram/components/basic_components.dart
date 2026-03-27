import 'package:flutter/material.dart';

import 'base_component.dart';

/// 视图容器
class ViewComponent extends MiniBaseComponent {
  const ViewComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final width = parseWidth();
    final height = parseHeight();
    final padding = parsePadding();
    final margin = parseMargin();
    final bgColor = parseBackgroundColor();
    final borderRadius = parseBorderRadius();
    final border = parseBorder();
    final flex = parseFlex();
    final alignment = parseAlignment();
    
    Widget widget = Container(
      width: width,
      height: height,
      padding: padding,
      margin: margin,
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: borderRadius,
        border: border,
      ),
      alignment: alignment,
      child: buildChildren(),
    );
    
    // 处理点击事件
    if (attributes.containsKey('bindtap')) {
      widget = GestureDetector(
        onTap: () => onEvent?.call('tap', {}),
        child: widget,
      );
    }
    
    // 处理长按
    if (attributes.containsKey('bindlongpress')) {
      widget = GestureDetector(
        onLongPress: () => onEvent?.call('longpress', {}),
        child: widget,
      );
    }
    
    // 处理flex布局
    if (flex != null) {
      widget = Expanded(
        flex: flex,
        child: widget,
      );
    }
    
    return widget;
  }
}

/// 文本组件
class TextComponent extends MiniBaseComponent {
  const TextComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final text = parseTextContent();
    final fontSize = parseFontSize();
    final color = parseColor();
    final fontWeight = parseFontWeight();
    final textAlign = parseTextAlign();
    final maxLines = parseMaxLines();
    final lineHeight = parseLineHeight();
    final textDecoration = parseTextDecoration();
    
    return Text(
      text,
      textAlign: textAlign,
      maxLines: maxLines,
      overflow: maxLines != null ? TextOverflow.ellipsis : null,
      style: TextStyle(
        fontSize: fontSize,
        color: color,
        fontWeight: fontWeight,
        height: lineHeight,
        decoration: textDecoration,
      ),
    );
  }
  
  String parseTextContent() {
    return attributes['text']?.toString() ?? 
           attributes['value']?.toString() ?? '';
  }
  
  TextAlign? parseTextAlign() {
    final align = attributes['text-align'] ?? attributes['align'];
    switch (align) {
      case 'left': return TextAlign.left;
      case 'center': return TextAlign.center;
      case 'right': return TextAlign.right;
      case 'justify': return TextAlign.justify;
      default: return null;
    }
  }
  
  int? parseMaxLines() {
    final lines = attributes['max-lines'] ?? attributes['number-of-lines'];
    if (lines != null) {
      return int.tryParse(lines.toString());
    }
    return null;
  }
  
  double? parseLineHeight() {
    final height = attributes['line-height'];
    if (height != null) {
      final value = double.tryParse(height.toString());
      if (value != null && value > 10) {
        // 如果是像素值，转换为比例
        return value / parseFontSize();
      }
      return value;
    }
    return null;
  }
  
  TextDecoration? parseTextDecoration() {
    final decoration = attributes['text-decoration'] ?? attributes['decoration'];
    switch (decoration) {
      case 'underline': return TextDecoration.underline;
      case 'line-through': return TextDecoration.lineThrough;
      case 'overline': return TextDecoration.overline;
      default: return null;
    }
  }
}

/// 图片组件
class ImageComponent extends MiniBaseComponent {
  const ImageComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final src = attributes['src']?.toString() ?? '';
    final width = parseWidth();
    final height = parseHeight();
    final fit = parseObjectFit();
    final borderRadius = parseBorderRadius();
    final mode = attributes['mode']?.toString() ?? 'scaleToFill';
    
    Widget image;
    
    if (src.startsWith('http')) {
      image = Image.network(
        src,
        width: width,
        height: height,
        fit: fit,
        errorBuilder: (context, error, stackTrace) => _buildPlaceholder(),
        loadingBuilder: (context, child, loadingProgress) {
          if (loadingProgress == null) return child;
          return _buildLoadingIndicator(loadingProgress);
        },
      );
    } else if (src.startsWith('asset:')) {
      image = Image.asset(
        src.substring(6),
        width: width,
        height: height,
        fit: fit,
        errorBuilder: (context, error, stackTrace) => _buildPlaceholder(),
      );
    } else {
      // 本地文件或其他
      image = Container(
        width: width,
        height: height,
        color: Colors.grey[200],
        child: const Icon(Icons.image, color: Colors.grey),
      );
    }
    
    if (borderRadius != null) {
      image = ClipRRect(
        borderRadius: borderRadius,
        child: image,
      );
    }
    
    // 处理点击事件
    if (attributes.containsKey('bindtap') || attributes.containsKey('binderror')) {
      image = GestureDetector(
        onTap: attributes.containsKey('bindtap') 
          ? () => onEvent?.call('tap', {'src': src})
          : null,
        child: image,
      );
    }
    
    return image;
  }
  
  BoxFit parseObjectFit() {
    final mode = attributes['mode']?.toString() ?? 'scaleToFill';
    switch (mode) {
      case 'scaleToFill': return BoxFit.fill;
      case 'aspectFit': return BoxFit.contain;
      case 'aspectFill': return BoxFit.cover;
      case 'widthFix': return BoxFit.fitWidth;
      case 'heightFix': return BoxFit.fitHeight;
      case 'center': return BoxFit.none;
      default: return BoxFit.cover;
    }
  }
  
  Widget _buildPlaceholder() {
    return Container(
      width: parseWidth(),
      height: parseHeight(),
      color: Colors.grey[200],
      child: const Icon(Icons.broken_image, color: Colors.grey),
    );
  }
  
  Widget _buildLoadingIndicator(ImageChunkEvent progress) {
    return Center(
      child: CircularProgressIndicator(
        value: progress.expectedTotalBytes != null
          ? progress.cumulativeBytesLoaded / progress.expectedTotalBytes!
          : null,
      ),
    );
  }
}

/// 按钮组件
class ButtonComponent extends MiniBaseComponent {
  const ButtonComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final type = attributes['type']?.toString() ?? 'default';
    final size = attributes['size']?.toString() ?? 'default';
    final disabled = parseBoolAttribute('disabled') ?? false;
    final loading = parseBoolAttribute('loading') ?? false;
    final plain = parseBoolAttribute('plain') ?? false;
    
    final buttonStyle = _getButtonStyle(type, plain);
    final buttonSize = _getButtonSize(size);
    
    Widget button = ElevatedButton(
      onPressed: disabled || loading ? null : () => onEvent?.call('tap', {}),
      style: buttonStyle,
      child: loading 
        ? Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              SizedBox(
                width: buttonSize == 'mini' ? 12 : 16,
                height: buttonSize == 'mini' ? 12 : 16,
                child: const CircularProgressIndicator(strokeWidth: 2),
              ),
              const SizedBox(width: 8),
              buildChildren() ?? const Text('加载中'),
            ],
          )
        : (buildChildren() ?? const Text('Button')),
    );
    
    if (buttonSize == 'mini') {
      button = SizedBox(
        width: 80,
        height: 32,
        child: button,
      );
    }
    
    return button;
  }
  
  ButtonStyle _getButtonStyle(String type, bool plain) {
    Color backgroundColor;
    Color foregroundColor;
    
    switch (type) {
      case 'primary':
        backgroundColor = plain ? Colors.transparent : Colors.blue;
        foregroundColor = plain ? Colors.blue : Colors.white;
        break;
      case 'warn':
        backgroundColor = plain ? Colors.transparent : Colors.red;
        foregroundColor = plain ? Colors.red : Colors.white;
        break;
      default:
        backgroundColor = plain ? Colors.transparent : Colors.grey[200]!;
        foregroundColor = plain ? Colors.black87 : Colors.black87;
    }
    
    return ElevatedButton.styleFrom(
      backgroundColor: backgroundColor,
      foregroundColor: foregroundColor,
      elevation: plain ? 0 : null,
      side: plain ? BorderSide(color: foregroundColor) : null,
    );
  }
  
  String _getButtonSize(String size) {
    return size == 'mini' ? 'mini' : 'default';
  }
}

/// 输入框组件
class InputComponent extends MiniBaseComponent {
  const InputComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final type = attributes['type']?.toString() ?? 'text';
    final placeholder = attributes['placeholder']?.toString() ?? '';
    final disabled = parseBoolAttribute('disabled') ?? false;
    final password = type == 'password' || (parseBoolAttribute('password') ?? false);
    final maxLength = parseIntAttribute('maxlength');
    final focus = parseBoolAttribute('focus') ?? false;
    
    TextInputType keyboardType;
    switch (type) {
      case 'number': keyboardType = TextInputType.number; break;
      case 'digit': keyboardType = const TextInputType.numberWithOptions(decimal: true); break;
      case 'idcard': keyboardType = TextInputType.text; break;
      case 'tel': keyboardType = TextInputType.phone; break;
      case 'safe-password': keyboardType = TextInputType.visiblePassword; break;
      default: keyboardType = TextInputType.text;
    }
    
    return TextField(
      enabled: !disabled,
      obscureText: password,
      maxLength: maxLength,
      autofocus: focus,
      keyboardType: keyboardType,
      decoration: InputDecoration(
        hintText: placeholder,
        border: const OutlineInputBorder(),
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      ),
      onChanged: (value) => onEvent?.call('input', {'value': value}),
      onSubmitted: (value) => onEvent?.call('confirm', {'value': value}),
      onEditingComplete: () => onEvent?.call('blur', {}),
    );
  }
}

/// 滚动视图组件
class ScrollViewComponent extends MiniBaseComponent {
  const ScrollViewComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final scrollX = parseBoolAttribute('scroll-x') ?? false;
    final scrollY = parseBoolAttribute('scroll-y') ?? false;
    final upperThreshold = parseIntAttribute('upper-threshold') ?? 50;
    final lowerThreshold = parseIntAttribute('lower-threshold') ?? 50;
    final scrollTop = parseIntAttribute('scroll-top');
    final scrollLeft = parseIntAttribute('scroll-left');
    final scrollIntoView = attributes['scroll-into-view']?.toString();
    final scrollWithAnimation = parseBoolAttribute('scroll-with-animation') ?? false;
    final enableBackToTop = parseBoolAttribute('enable-back-to-top') ?? false;
    
    if (scrollX) {
      return SingleChildScrollView(
        scrollDirection: Axis.horizontal,
        child: buildChildren(),
      );
    }
    
    return SingleChildScrollView(
      scrollDirection: Axis.vertical,
      child: buildChildren(),
    );
  }
}

/// 滑块视图容器（轮播）
class SwiperComponent extends MiniBaseComponent {
  const SwiperComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final indicatorDots = parseBoolAttribute('indicator-dots') ?? false;
    final autoplay = parseBoolAttribute('autoplay') ?? false;
    final current = parseIntAttribute('current') ?? 0;
    final interval = parseIntAttribute('interval') ?? 5000;
    final duration = parseIntAttribute('duration') ?? 500;
    final circular = parseBoolAttribute('circular') ?? false;
    final vertical = parseBoolAttribute('vertical') ?? false;
    
    // 提取swiper-item
    final items = <Widget>[];
    if (children != null) {
      for (final child in children!) {
        items.add(child);
      }
    }
    
    if (items.isEmpty) {
      return const SizedBox.shrink();
    }
    
    return PageView(
      scrollDirection: vertical ? Axis.vertical : Axis.horizontal,
      controller: PageController(initialPage: current),
      onPageChanged: (index) => onEvent?.call('change', {'current': index}),
      children: items,
    );
  }
}

/// 图标组件
class IconComponent extends MiniBaseComponent {
  static const Map<String, IconData> _iconMap = {
    'success': Icons.check_circle,
    'success_no_circle': Icons.check,
    'info': Icons.info,
    'warn': Icons.warning,
    'waiting': Icons.hourglass_empty,
    'cancel': Icons.cancel,
    'download': Icons.download,
    'search': Icons.search,
    'clear': Icons.clear,
    'close': Icons.close,
    'arrow': Icons.arrow_forward,
    'arrow-left': Icons.arrow_back,
    'arrow-right': Icons.arrow_forward,
    'arrow-up': Icons.arrow_upward,
    'arrow-down': Icons.arrow_downward,
    'home': Icons.home,
    'user': Icons.person,
    'settings': Icons.settings,
    'more': Icons.more_vert,
    'add': Icons.add,
    'minus': Icons.remove,
  };
  
  const IconComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final type = attributes['type']?.toString() ?? 'success';
    final size = parseFontSize();
    final color = parseColor();
    
    final iconData = _iconMap[type] ?? Icons.help_outline;
    
    return Icon(
      iconData,
      size: size,
      color: color,
    );
  }
}
