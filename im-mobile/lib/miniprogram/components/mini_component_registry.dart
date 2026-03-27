import 'package:flutter/material.dart';

import 'basic_components.dart';
import 'layout_components.dart';
import 'form_components.dart';
import 'media_components.dart';

/// 组件注册表
/// 管理所有小程序组件的构建器
class MiniComponentRegistry {
  
  static final MiniComponentRegistry _instance = MiniComponentRegistry._internal();
  static MiniComponentRegistry get instance => _instance;
  
  final Map<String, ComponentBuilder> _builders = {};
  
  MiniComponentRegistry._internal() {
    _registerDefaultComponents();
  }
  
  /// 注册组件构建器
  void register(String name, ComponentBuilder builder) {
    _builders[name] = builder;
    // 也注册短名称
    if (name.startsWith('mini-')) {
      _builders[name.substring(5)] = builder;
    }
  }
  
  /// 获取组件构建器
  ComponentBuilder? getBuilder(String name) {
    return _builders[name] ?? _builders['mini-$name'];
  }
  
  /// 注销组件
  void unregister(String name) {
    _builders.remove(name);
    _builders.remove('mini-$name');
  }
  
  /// 检查组件是否存在
  bool hasComponent(String name) {
    return _builders.containsKey(name) || _builders.containsKey('mini-$name');
  }
  
  /// 获取所有注册的组件名称
  List<String> get registeredNames => _builders.keys.toList();
  
  /// 注册默认组件
  void _registerDefaultComponents() {
    // ============ 基础组件 ============
    
    // 视图容器
    register('view', (params) => ViewComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    // 文本
    register('text', (params) => TextComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 图片
    register('image', (params) => ImageComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 图标
    register('icon', (params) => IconComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 按钮
    register('button', (params) => ButtonComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    // 富文本
    register('rich-text', (params) => RichTextComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 进度条
    register('progress', (params) => ProgressComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // ============ 布局组件 ============
    
    // 滚动视图
    register('scroll-view', (params) => ScrollViewComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    // 滑块视图（轮播）
    register('swiper', (params) => SwiperComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('swiper-item', (params) => Container(child: params.buildChildren()));
    
    // 可移动视图
    register('movable-area', (params) => MovableAreaComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('movable-view', (params) => MovableViewComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    // 覆盖在原生组件之上的视图
    register('cover-view', (params) => ViewComponent(
      attributes: {...params.attributes, 'cover': 'true'},
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('cover-image', (params) => ImageComponent(
      attributes: {...params.attributes, 'cover': 'true'},
      onEvent: params.onEvent,
    ));
    
    // ============ 表单组件 ============
    
    // 输入框
    register('input', (params) => InputComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 多行输入框
    register('textarea', (params) => TextAreaComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 开关
    register('switch', (params) => SwitchComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 滑动选择器
    register('slider', (params) => SliderComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 单选框组
    register('radio-group', (params) => RadioGroupComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('radio', (params) => RadioComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 复选框组
    register('checkbox-group', (params) => CheckboxGroupComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('checkbox', (params) => CheckboxComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 选择器
    register('picker', (params) => PickerComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    register('picker-view', (params) => PickerViewComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('picker-view-column', (params) => Container(child: params.buildChildren()));
    
    // 表单
    register('form', (params) => FormComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('label', (params) => LabelComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    // ============ 媒体组件 ============
    
    // 视频
    register('video', (params) => VideoComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 音频
    register('audio', (params) => AudioComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 相机
    register('camera', (params) => CameraComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 图片裁剪
    register('image-cropper', (params) => ImageCropperComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // ============ 地图组件 ============
    
    register('map', (params) => MapComponent(
      attributes: params.attributes,
      markers: params.attributes['markers'],
      onEvent: params.onEvent,
    ));
    
    // ============ 画布组件 ============
    
    register('canvas', (params) => CanvasComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // ============ 开放能力组件 ============
    
    // 打开APP
    register('open-data', (params) => OpenDataComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 网页容器
    register('web-view', (params) => WebViewComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 广告
    register('ad', (params) => AdComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 原生广告
    register('ad-custom', (params) => AdCustomComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 关注公众号
    register('official-account', (params) => OfficialAccountComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // 打开公众号文章
    register('open-article', (params) => OpenArticleComponent(
      attributes: params.attributes,
      onEvent: params.onEvent,
    ));
    
    // ============ 导航组件 ============
    
    register('navigator', (params) => NavigatorComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    // ============ 列表组件 ============
    
    register('list', (params) => ListComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
    
    register('list-item', (params) => ListItemComponent(
      attributes: params.attributes,
      children: params.children,
      onEvent: params.onEvent,
    ));
  }
}

/// 组件构建参数
class ComponentParams {
  final Map<String, dynamic> attributes;
  final List<Widget>? children;
  final void Function(String event, dynamic detail)? onEvent;
  final Map<String, dynamic>? data;
  
  const ComponentParams({
    required this.attributes,
    this.children,
    this.onEvent,
    this.data,
  });
  
  Widget? buildChildren() {
    if (children == null || children!.isEmpty) return null;
    if (children!.length == 1) return children!.first;
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children!,
    );
  }
}

/// 组件构建器类型
typedef ComponentBuilder = Widget Function(ComponentParams params);
