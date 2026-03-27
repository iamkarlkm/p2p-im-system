import 'package:flutter/material.dart';

/// 基础组件抽象类
abstract class MiniBaseComponent extends StatelessWidget {
  final Map<String, dynamic> attributes;
  final List<Widget>? children;
  final void Function(String event, dynamic detail)? onEvent;
  final Map<String, dynamic>? data;
  
  const MiniBaseComponent({
    super.key,
    required this.attributes,
    this.children,
    this.onEvent,
    this.data,
  });
  
  /// 构建子组件
  Widget? buildChildren() {
    if (children == null || children!.isEmpty) {
      return null;
    }
    if (children!.length == 1) {
      return children!.first;
    }
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children!,
    );
  }
  
  // ============ 属性解析方法 ============
  
  /// 解析宽度
  double? parseWidth() {
    final width = attributes['width'];
    if (width == null) return null;
    return _parseSize(width);
  }
  
  /// 解析高度
  double? parseHeight() {
    final height = attributes['height'];
    if (height == null) return null;
    return _parseSize(height);
  }
  
  double? _parseSize(dynamic value) {
    if (value == null) return null;
    if (value is num) return value.toDouble();
    
    final str = value.toString();
    
    // rpx单位处理
    if (str.endsWith('rpx')) {
      final num = double.tryParse(str.replaceAll('rpx', ''));
      if (num != null) {
        // 假设屏幕宽度750rpx
        return num * 0.5; // 简化的rpx转换
      }
    }
    
    // px单位
    if (str.endsWith('px')) {
      return double.tryParse(str.replaceAll('px', ''));
    }
    
    // %单位（返回null，需要父组件处理）
    if (str.endsWith('%')) return null;
    
    // 纯数字
    return double.tryParse(str);
  }
  
  /// 解析padding
  EdgeInsets? parsePadding() {
    final padding = attributes['padding'];
    if (padding == null) return null;
    
    if (padding is String) {
      final values = padding.split(' ').map((s) => double.tryParse(s) ?? 0.0).toList();
      
      if (values.length == 1) {
        return EdgeInsets.all(values[0]);
      } else if (values.length == 2) {
        return EdgeInsets.symmetric(vertical: values[0], horizontal: values[1]);
      } else if (values.length == 4) {
        return EdgeInsets.fromLTRB(values[1], values[0], values[3], values[2]);
      }
    }
    
    return null;
  }
  
  /// 解析margin
  EdgeInsets? parseMargin() {
    final margin = attributes['margin'];
    if (margin == null) return null;
    
    if (margin is String) {
      final values = margin.split(' ').map((s) => double.tryParse(s) ?? 0.0).toList();
      
      if (values.length == 1) {
        return EdgeInsets.all(values[0]);
      } else if (values.length == 2) {
        return EdgeInsets.symmetric(vertical: values[0], horizontal: values[1]);
      } else if (values.length == 4) {
        return EdgeInsets.fromLTRB(values[1], values[0], values[3], values[2]);
      }
    }
    
    return null;
  }
  
  /// 解析背景颜色
  Color? parseBackgroundColor() {
    final bg = attributes['background-color'] ?? attributes['bgcolor'];
    return _parseColor(bg);
  }
  
  /// 解析文本颜色
  Color? parseColor() {
    final color = attributes['color'] ?? attributes['text-color'];
    return _parseColor(color);
  }
  
  Color? _parseColor(dynamic value) {
    if (value == null) return null;
    if (value is Color) return value;
    
    final str = value.toString();
    
    // 十六进制颜色
    if (str.startsWith('#')) {
      String hex = str.substring(1);
      if (hex.length == 3) {
        hex = hex.split('').map((c) => '$c$c').join();
      }
      if (hex.length == 6) {
        return Color(int.parse('FF$hex', radix: 16));
      } else if (hex.length == 8) {
        return Color(int.parse(hex, radix: 16));
      }
    }
    
    // RGB/RGBA
    final rgbMatch = RegExp(r'rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*([\d.]+))?\)').firstMatch(str);
    if (rgbMatch != null) {
      final r = int.parse(rgbMatch.group(1)!);
      final g = int.parse(rgbMatch.group(2)!);
      final b = int.parse(rgbMatch.group(3)!);
      final a = double.tryParse(rgbMatch.group(4) ?? '1') ?? 1.0;
      return Color.fromRGBO(r, g, b, a);
    }
    
    // 命名颜色
    switch (str) {
      case 'black': return Colors.black;
      case 'white': return Colors.white;
      case 'red': return Colors.red;
      case 'green': return Colors.green;
      case 'blue': return Colors.blue;
      case 'yellow': return Colors.yellow;
      case 'orange': return Colors.orange;
      case 'purple': return Colors.purple;
      case 'pink': return Colors.pink;
      case 'grey': case 'gray': return Colors.grey;
      case 'transparent': return Colors.transparent;
      default: return null;
    }
  }
  
  /// 解析圆角
  BorderRadius? parseBorderRadius() {
    final radius = attributes['border-radius'];
    if (radius == null) return null;
    
    final value = double.tryParse(radius.toString());
    if (value != null) {
      return BorderRadius.circular(value);
    }
    
    return null;
  }
  
  /// 解析边框
  Border? parseBorder() {
    final border = attributes['border'];
    final borderWidth = attributes['border-width'];
    final borderColor = attributes['border-color'];
    
    if (border == null && borderWidth == null) return null;
    
    final width = double.tryParse(borderWidth?.toString() ?? '1') ?? 1;
    final color = _parseColor(borderColor) ?? Colors.black;
    
    return Border.all(width: width, color: color);
  }
  
  /// 解析flex
  int? parseFlex() {
    final flex = attributes['flex'];
    if (flex == null) return null;
    return int.tryParse(flex.toString());
  }
  
  /// 解析对齐方式
  Alignment? parseAlignment() {
    final align = attributes['align'] ?? attributes['text-align'];
    switch (align) {
      case 'left': return Alignment.centerLeft;
      case 'center': return Alignment.center;
      case 'right': return Alignment.centerRight;
      case 'top': return Alignment.topCenter;
      case 'bottom': return Alignment.bottomCenter;
      case 'top-left': return Alignment.topLeft;
      case 'top-right': return Alignment.topRight;
      case 'bottom-left': return Alignment.bottomLeft;
      case 'bottom-right': return Alignment.bottomRight;
      default: return null;
    }
  }
  
  /// 解析字体大小
  double parseFontSize() {
    final size = attributes['font-size'] ?? attributes['size'];
    if (size == null) return 14.0;
    
    if (size is num) return size.toDouble();
    
    final str = size.toString();
    if (str.endsWith('rpx')) {
      final num = double.tryParse(str.replaceAll('rpx', ''));
      if (num != null) return num * 0.5;
    }
    if (str.endsWith('px')) {
      return double.tryParse(str.replaceAll('px', '')) ?? 14.0;
    }
    
    return double.tryParse(str) ?? 14.0;
  }
  
  /// 解析字体粗细
  FontWeight? parseFontWeight() {
    final weight = attributes['font-weight'] ?? attributes['weight'];
    if (weight == null) return null;
    
    if (weight is num) {
      return FontWeight.values.firstWhere(
        (w) => w.value == weight.toInt(),
        orElse: () => FontWeight.normal,
      );
    }
    
    switch (weight.toString()) {
      case 'normal': return FontWeight.normal;
      case 'bold': return FontWeight.bold;
      case '100': return FontWeight.w100;
      case '200': return FontWeight.w200;
      case '300': return FontWeight.w300;
      case '400': return FontWeight.w400;
      case '500': return FontWeight.w500;
      case '600': return FontWeight.w600;
      case '700': return FontWeight.w700;
      case '800': return FontWeight.w800;
      case '900': return FontWeight.w900;
      default: return null;
    }
  }
  
  /// 解析布尔属性
  bool? parseBoolAttribute(String name) {
    final value = attributes[name];
    if (value == null) return null;
    if (value is bool) return value;
    if (value is String) return value.toLowerCase() == 'true';
    return null;
  }
  
  /// 解析整型属性
  int? parseIntAttribute(String name) {
    final value = attributes[name];
    if (value == null) return null;
    if (value is int) return value;
    if (value is String) return int.tryParse(value);
    return null;
  }
  
  /// 解析数据绑定表达式
  dynamic bindData(String expression) {
    if (data == null) return null;
    
    // 简单实现：{{key}} 格式
    final regExp = RegExp(r'\{\{(.+?)\}\}');
    final match = regExp.firstMatch(expression);
    
    if (match != null) {
      final key = match.group(1)!.trim();
      return _getNestedValue(data!, key);
    }
    
    return expression;
  }
  
  dynamic _getNestedValue(Map<String, dynamic> map, String key) {
    final keys = key.split('.');
    dynamic value = map;
    
    for (final k in keys) {
      if (value is Map<String, dynamic>) {
        value = value[k];
      } else {
        return null;
      }
    }
    
    return value;
  }
}
