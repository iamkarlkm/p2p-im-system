import 'package:flutter/material.dart';

import 'base_component.dart';

/// 布局容器组件
class LayoutContainerComponent extends MiniBaseComponent {
  const LayoutContainerComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final direction = attributes['direction']?.toString() ?? 'column';
    final justify = attributes['justify']?.toString() ?? 'start';
    final align = attributes['align']?.toString() ?? 'stretch';
    final wrap = parseBoolAttribute('wrap') ?? false;
    
    final mainAxisAlignment = _parseMainAxisAlignment(justify);
    final crossAxisAlignment = _parseCrossAxisAlignment(align);
    
    if (direction == 'row') {
      if (wrap) {
        return Wrap(
          direction: Axis.horizontal,
          alignment: _parseWrapAlignment(justify),
          crossAxisAlignment: _parseWrapCrossAlignment(align),
          children: children ?? [],
        );
      }
      return Row(
        mainAxisAlignment: mainAxisAlignment,
        crossAxisAlignment: crossAxisAlignment,
        children: children ?? [],
      );
    }
    
    return Column(
      mainAxisAlignment: mainAxisAlignment,
      crossAxisAlignment: crossAxisAlignment,
      children: children ?? [],
    );
  }
  
  MainAxisAlignment _parseMainAxisAlignment(String value) {
    switch (value) {
      case 'start': return MainAxisAlignment.start;
      case 'end': return MainAxisAlignment.end;
      case 'center': return MainAxisAlignment.center;
      case 'space-between': return MainAxisAlignment.spaceBetween;
      case 'space-around': return MainAxisAlignment.spaceAround;
      case 'space-evenly': return MainAxisAlignment.spaceEvenly;
      default: return MainAxisAlignment.start;
    }
  }
  
  CrossAxisAlignment _parseCrossAxisAlignment(String value) {
    switch (value) {
      case 'start': return CrossAxisAlignment.start;
      case 'end': return CrossAxisAlignment.end;
      case 'center': return CrossAxisAlignment.center;
      case 'stretch': return CrossAxisAlignment.stretch;
      case 'baseline': return CrossAxisAlignment.baseline;
      default: return CrossAxisAlignment.stretch;
    }
  }
  
  WrapAlignment _parseWrapAlignment(String value) {
    switch (value) {
      case 'start': return WrapAlignment.start;
      case 'end': return WrapAlignment.end;
      case 'center': return WrapAlignment.center;
      case 'space-between': return WrapAlignment.spaceBetween;
      case 'space-around': return WrapAlignment.spaceAround;
      case 'space-evenly': return WrapAlignment.spaceEvenly;
      default: return WrapAlignment.start;
    }
  }
  
  WrapCrossAlignment _parseWrapCrossAlignment(String value) {
    switch (value) {
      case 'start': return WrapCrossAlignment.start;
      case 'end': return WrapCrossAlignment.end;
      case 'center': return WrapCrossAlignment.center;
      default: return WrapCrossAlignment.start;
    }
  }
}

/// 可移动区域
class MovableAreaComponent extends MiniBaseComponent {
  const MovableAreaComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final width = parseWidth() ?? double.infinity;
    final height = parseHeight() ?? 200;
    
    return Container(
      width: width,
      height: height,
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: Stack(
        children: children ?? [],
      ),
    );
  }
}

/// 可移动视图
class MovableViewComponent extends MiniBaseComponent {
  const MovableViewComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final x = parseIntAttribute('x')?.toDouble() ?? 0;
    final y = parseIntAttribute('y')?.toDouble() ?? 0;
    final direction = attributes['direction']?.toString() ?? 'all';
    final disabled = parseBoolAttribute('disabled') ?? false;
    
    return Positioned(
      left: x,
      top: y,
      child: Draggable(
        axis: direction == 'horizontal' ? Axis.horizontal : 
              direction == 'vertical' ? Axis.vertical : null,
        ignoringFeedbackSemantics: disabled,
        onDragEnd: (details) {
          onEvent?.call('change', {
            'x': details.offset.dx,
            'y': details.offset.dy,
          });
        },
        feedback: Material(
          color: Colors.transparent,
          child: Opacity(
            opacity: 0.7,
            child: buildChildren() ?? const SizedBox.shrink(),
          ),
        ),
        childWhenDragging: const SizedBox.shrink(),
        child: buildChildren() ?? const SizedBox.shrink(),
      ),
    );
  }
}

/// 导航组件
class NavigatorComponent extends MiniBaseComponent {
  const NavigatorComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final url = attributes['url']?.toString() ?? '';
    final openType = attributes['open-type']?.toString() ?? 'navigate';
    final delta = parseIntAttribute('delta') ?? 1;
    final hoverClass = attributes['hover-class']?.toString();
    final hoverStopPropagation = parseBoolAttribute('hover-stop-propagation') ?? false;
    
    return GestureDetector(
      onTap: () {
        switch (openType) {
          case 'navigate':
          case 'navigateTo':
            onEvent?.call('tap', {'type': 'navigateTo', 'url': url});
            break;
          case 'redirect':
          case 'redirectTo':
            onEvent?.call('tap', {'type': 'redirectTo', 'url': url});
            break;
          case 'switchTab':
            onEvent?.call('tap', {'type': 'switchTab', 'url': url});
            break;
          case 'reLaunch':
            onEvent?.call('tap', {'type': 'reLaunch', 'url': url});
            break;
          case 'navigateBack':
            onEvent?.call('tap', {'type': 'navigateBack', 'delta': delta});
            break;
          case 'exit':
            onEvent?.call('tap', {'type': 'exit'});
            break;
        }
      },
      child: Container(
        decoration: hoverClass != null ? BoxDecoration(
          color: hoverClass == 'none' ? null : Colors.grey[200],
        ) : null,
        child: buildChildren(),
      ),
    );
  }
}

/// 列表组件
class ListComponent extends MiniBaseComponent {
  const ListComponent({
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
    final enableBackToTop = parseBoolAttribute('enable-back-to-top') ?? false;
    
    if (scrollX) {
      return SingleChildScrollView(
        scrollDirection: Axis.horizontal,
        child: Row(
          children: children ?? [],
        ),
      );
    }
    
    return NotificationListener<ScrollNotification>(
      onNotification: (notification) {
        if (notification is ScrollEndNotification) {
          final metrics = notification.metrics;
          if (metrics.atEdge) {
            if (metrics.pixels == 0) {
              onEvent?.call('scrolltoupper', {});
            } else {
              onEvent?.call('scrolltolower', {});
            }
          }
        }
        return false;
      },
      child: SingleChildScrollView(
        child: Column(
          children: children ?? [],
        ),
      ),
    );
  }
}

/// 列表项组件
class ListItemComponent extends MiniBaseComponent {
  const ListItemComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: parsePadding() ?? const EdgeInsets.all(12),
      margin: parseMargin(),
      decoration: BoxDecoration(
        border: Border(
          bottom: BorderSide(color: Colors.grey[300]!, width: 0.5),
        ),
      ),
      child: buildChildren(),
    );
  }
}

/// 分割线组件
class DividerComponent extends MiniBaseComponent {
  const DividerComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final color = parseColor() ?? Colors.grey[300];
    final height = parseHeight() ?? 1;
    final margin = parseMargin();
    
    return Container(
      height: height,
      margin: margin,
      color: color,
    );
  }
}

/// 间距组件
class SpacerComponent extends MiniBaseComponent {
  const SpacerComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final width = parseWidth();
    final height = parseHeight();
    final flex = parseFlex();
    
    if (flex != null) {
      return Spacer(flex: flex);
    }
    
    return SizedBox(
      width: width,
      height: height,
    );
  }
}

/// 卡片组件
class CardComponent extends MiniBaseComponent {
  const CardComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final elevation = parseIntAttribute('elevation')?.toDouble() ?? 1;
    final borderRadius = parseBorderRadius() ?? BorderRadius.circular(8);
    final bgColor = parseBackgroundColor() ?? Colors.white;
    final margin = parseMargin() ?? const EdgeInsets.all(8);
    final padding = parsePadding() ?? const EdgeInsets.all(16);
    
    return Card(
      elevation: elevation,
      margin: margin,
      shape: RoundedRectangleBorder(borderRadius: borderRadius),
      color: bgColor,
      child: Padding(
        padding: padding,
        child: buildChildren(),
      ),
    );
  }
}

/// 网格组件
class GridComponent extends MiniBaseComponent {
  const GridComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final columns = parseIntAttribute('columns') ?? 2;
    final spacing = parseIntAttribute('spacing')?.toDouble() ?? 8;
    final childAspectRatio = double.tryParse(attributes['aspect-ratio']?.toString() ?? '1') ?? 1;
    
    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: columns,
      mainAxisSpacing: spacing,
      crossAxisSpacing: spacing,
      childAspectRatio: childAspectRatio,
      children: children ?? [],
    );
  }
}

/// 展开面板组件
class ExpansionPanelComponent extends MiniBaseComponent {
  const ExpansionPanelComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final initiallyExpanded = parseBoolAttribute('expanded') ?? false;
    final title = attributes['title']?.toString() ?? '';
    
    return StatefulBuilder(
      builder: (context, setState) {
        var expanded = initiallyExpanded;
        
        return ExpansionTile(
          title: Text(title),
          initiallyExpanded: expanded,
          onExpansionChanged: (value) {
            expanded = value;
            onEvent?.call('change', {'expanded': value});
          },
          children: [
            Padding(
              padding: const EdgeInsets.all(16),
              child: buildChildren(),
            ),
          ],
        );
      },
    );
  }
}

/// 标签页组件
class TabsComponent extends MiniBaseComponent {
  const TabsComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final current = parseIntAttribute('current') ?? 0;
    final tabPosition = attributes['tab-position']?.toString() ?? 'top';
    final tabs = (attributes['tabs'] as List<String>?) ?? [];
    
    return DefaultTabController(
      length: tabs.length,
      initialIndex: current,
      child: Column(
        children: [
          if (tabPosition == 'top')
            TabBar(
              tabs: tabs.map((t) => Tab(text: t)).toList(),
              onTap: (index) => onEvent?.call('change', {'index': index}),
            ),
          Expanded(
            child: TabBarView(
              children: children ?? [],
            ),
          ),
          if (tabPosition == 'bottom')
            TabBar(
              tabs: tabs.map((t) => Tab(text: t)).toList(),
              onTap: (index) => onEvent?.call('change', {'index': index}),
            ),
        ],
      ),
    );
  }
}
