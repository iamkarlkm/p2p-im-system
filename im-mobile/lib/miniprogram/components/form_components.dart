import 'package:flutter/material.dart';

import 'base_component.dart';

/// 表单组件
class FormComponent extends MiniBaseComponent {
  final _formKey = GlobalKey<FormState>();
  
  FormComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final reportSubmit = parseBoolAttribute('report-submit') ?? false;
    
    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ...?children,
          if (reportSubmit)
            ElevatedButton(
              onPressed: () {
                if (_formKey.currentState?.validate() ?? false) {
                  _formKey.currentState?.save();
                  onEvent?.call('submit', {});
                }
              },
              child: const Text('提交'),
            ),
        ],
      ),
    );
  }
}

/// 标签组件
class LabelComponent extends MiniBaseComponent {
  const LabelComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final forId = attributes['for']?.toString();
    
    return GestureDetector(
      onTap: () {
        // 触发关联的表单组件
        onEvent?.call('tap', {'for': forId});
      },
      child: DefaultTextStyle(
        style: TextStyle(
          fontSize: parseFontSize(),
          color: parseColor() ?? Colors.black87,
        ),
        child: buildChildren() ?? const SizedBox.shrink(),
      ),
    );
  }
}

/// 多行文本输入
class TextAreaComponent extends MiniBaseComponent {
  const TextAreaComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final value = attributes['value']?.toString() ?? '';
    final placeholder = attributes['placeholder']?.toString() ?? '';
    final disabled = parseBoolAttribute('disabled') ?? false;
    final maxlength = parseIntAttribute('maxlength');
    final autoHeight = parseBoolAttribute('auto-height') ?? false;
    final showConfirmBar = parseBoolAttribute('show-confirm-bar') ?? true;
    final selectionStart = parseIntAttribute('selection-start');
    final selectionEnd = parseIntAttribute('selection-end');
    final focus = parseBoolAttribute('focus') ?? false;
    
    return TextFormField(
      initialValue: value,
      enabled: !disabled,
      maxLines: autoHeight ? null : 4,
      minLines: 2,
      maxLength: maxlength,
      autofocus: focus,
      decoration: InputDecoration(
        hintText: placeholder,
        border: const OutlineInputBorder(),
        counterText: maxlength != null ? null : '',
      ),
      onChanged: (val) => onEvent?.call('input', {'value': val}),
      onFieldSubmitted: (val) => onEvent?.call('confirm', {'value': val}),
    );
  }
}

/// 开关组件
class SwitchComponent extends MiniBaseComponent {
  const SwitchComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final checked = parseBoolAttribute('checked') ?? false;
    final disabled = parseBoolAttribute('disabled') ?? false;
    final type = attributes['type']?.toString() ?? 'switch';
    final color = parseColor() ?? Colors.green;
    
    if (type == 'checkbox') {
      return Checkbox(
        value: checked,
        onChanged: disabled 
          ? null 
          : (value) => onEvent?.call('change', {'value': value}),
        activeColor: color,
      );
    }
    
    return Switch(
      value: checked,
      onChanged: disabled 
        ? null 
        : (value) => onEvent?.call('change', {'value': value}),
      activeColor: color,
    );
  }
}

/// 滑块组件
class SliderComponent extends MiniBaseComponent {
  const SliderComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final min = double.tryParse(attributes['min']?.toString() ?? '0') ?? 0;
    final max = double.tryParse(attributes['max']?.toString() ?? '100') ?? 100;
    final step = double.tryParse(attributes['step']?.toString() ?? '1') ?? 1;
    final disabled = parseBoolAttribute('disabled') ?? false;
    final value = double.tryParse(attributes['value']?.toString() ?? '0') ?? 0;
    final activeColor = parseColor();
    final backgroundColor = parseBackgroundColor();
    final blockSize = parseIntAttribute('block-size')?.toDouble() ?? 20;
    final showValue = parseBoolAttribute('show-value') ?? false;
    
    return Row(
      children: [
        Expanded(
          child: Slider(
            value: value.clamp(min, max),
            min: min,
            max: max,
            divisions: ((max - min) / step).round(),
            onChanged: disabled 
              ? null 
              : (val) => onEvent?.call('changing', {'value': val}),
            onChangeEnd: disabled 
              ? null 
              : (val) => onEvent?.call('change', {'value': val}),
            activeColor: activeColor,
            inactiveColor: backgroundColor,
          ),
        ),
        if (showValue)
          Text(
            value.toStringAsFixed(1),
            style: const TextStyle(fontSize: 14),
          ),
      ],
    );
  }
}

/// 单选框组
class RadioGroupComponent extends MiniBaseComponent {
  const RadioGroupComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    // RadioGroup只是容器，实际的radio由子组件处理
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children ?? [],
    );
  }
}

/// 单选框
class RadioComponent extends MiniBaseComponent {
  const RadioComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final value = attributes['value']?.toString() ?? '';
    final checked = parseBoolAttribute('checked') ?? false;
    final disabled = parseBoolAttribute('disabled') ?? false;
    final color = parseColor() ?? Colors.blue;
    
    return RadioListTile<String>(
      value: value,
      groupValue: checked ? value : null,
      onChanged: disabled 
        ? null 
        : (val) => onEvent?.call('change', {'value': val}),
      activeColor: color,
      title: buildChildren(),
    );
  }
}

/// 复选框组
class CheckboxGroupComponent extends MiniBaseComponent {
  const CheckboxGroupComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children ?? [],
    );
  }
}

/// 复选框
class CheckboxComponent extends MiniBaseComponent {
  const CheckboxComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final value = attributes['value']?.toString() ?? '';
    final checked = parseBoolAttribute('checked') ?? false;
    final disabled = parseBoolAttribute('disabled') ?? false;
    final color = parseColor() ?? Colors.blue;
    
    return CheckboxListTile(
      value: checked,
      onChanged: disabled 
        ? null 
        : (val) => onEvent?.call('change', {'value': val, 'checkedValue': value}),
      activeColor: color,
      title: buildChildren(),
    );
  }
}

/// 选择器组件
class PickerComponent extends MiniBaseComponent {
  const PickerComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final mode = attributes['mode']?.toString() ?? 'selector';
    final disabled = parseBoolAttribute('disabled') ?? false;
    final range = attributes['range'] as List<dynamic>?;
    final rangeKey = attributes['range-key']?.toString();
    final value = parseIntAttribute('value') ?? 0;
    final start = attributes['start']?.toString();
    final end = attributes['end']?.toString();
    final fields = attributes['fields']?.toString() ?? 'day';
    
    String displayValue;
    if (range != null && value < range.length) {
      final item = range[value];
      displayValue = rangeKey != null && item is Map 
        ? item[rangeKey]?.toString() ?? '' 
        : item.toString();
    } else {
      displayValue = '请选择';
    }
    
    return InkWell(
      onTap: disabled 
        ? null 
        : () => _showPicker(context, mode, range, value),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey[400]!),
          borderRadius: BorderRadius.circular(4),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(displayValue),
            const Icon(Icons.arrow_drop_down),
          ],
        ),
      ),
    );
  }
  
  void _showPicker(BuildContext context, String mode, List<dynamic>? range, int currentValue) {
    if (mode == 'selector' && range != null) {
      showModalBottomSheet(
        context: context,
        builder: (context) => Container(
          height: 250,
          child: ListView.builder(
            itemCount: range.length,
            itemBuilder: (context, index) {
              return ListTile(
                title: Text(range[index].toString()),
                selected: index == currentValue,
                onTap: () {
                  Navigator.pop(context);
                  onEvent?.call('change', {'value': index});
                },
              );
            },
          ),
        ),
      );
    } else if (mode == 'date') {
      showDatePicker(
        context: context,
        initialDate: DateTime.now(),
        firstDate: DateTime(1900),
        lastDate: DateTime(2100),
      ).then((date) {
        if (date != null) {
          onEvent?.call('change', {'value': date.toIso8601String()});
        }
      });
    } else if (mode == 'time') {
      showTimePicker(
        context: context,
        initialTime: TimeOfDay.now(),
      ).then((time) {
        if (time != null) {
          onEvent?.call('change', {'value': '${time.hour}:${time.minute}'});
        }
      });
    }
  }
}

/// 选择器视图
class PickerViewComponent extends MiniBaseComponent {
  const PickerViewComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final value = attributes['value'] as List<int>?;
    final indicatorStyle = attributes['indicator-style']?.toString();
    final maskStyle = attributes['mask-style']?.toString();
    
    return Container(
      height: 200,
      child: Row(
        children: children?.map((child) => Expanded(child: child)).toList() ?? [],
      ),
    );
  }
}

/// 步进器组件
class StepperComponent extends MiniBaseComponent {
  const StepperComponent({
    super.key,
    required super.attributes,
    super.children,
    super.onEvent,
  });
  
  @override
  Widget build(BuildContext context) {
    final min = double.tryParse(attributes['min']?.toString() ?? '0') ?? 0;
    final max = double.tryParse(attributes['max']?.toString() ?? '999') ?? 999;
    final step = double.tryParse(attributes['step']?.toString() ?? '1') ?? 1;
    final disabled = parseBoolAttribute('disabled') ?? false;
    final value = double.tryParse(attributes['value']?.toString() ?? '0') ?? 0;
    
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          icon: const Icon(Icons.remove),
          onPressed: disabled || value <= min 
            ? null 
            : () => onEvent?.call('change', {'value': value - step}),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Text(
            value.toStringAsFixed(step < 1 ? 1 : 0),
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          ),
        ),
        IconButton(
          icon: const Icon(Icons.add),
          onPressed: disabled || value >= max 
            ? null 
            : () => onEvent?.call('change', {'value': value + step}),
        ),
      ],
    );
  }
}
