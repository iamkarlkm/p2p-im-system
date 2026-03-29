import 'dart:convert';
import 'dart:async';
import 'package:flutter/services.dart';

import '../models/mini_program_model.dart';

/// DSL解析器
/// 将小程序WXML/WXSS解析为DSL树
class DSLParser {
  
  /// 解析WXML内容
  DSLNode parse(String wxmlContent) {
    // 移除注释
    final cleanContent = _removeComments(wxmlContent);
    
    // 解析XML结构
    return _parseXML(cleanContent);
  }
  
  /// 移除HTML注释
  String _removeComments(String content) {
    return content.replaceAll(RegExp(r'<!--[\s\S]*?-->'), '');
  }
  
  /// 解析XML
  DSLNode _parseXML(String content) {
    final tokens = _tokenize(content);
    final root = _buildTree(tokens);
    return root;
  }
  
  /// 词法分析 - 将内容分词
  List<XMLToken> _tokenize(String content) {
    final tokens = <XMLToken>[];
    final buffer = StringBuffer();
    var i = 0;
    
    while (i < content.length) {
      final char = content[i];
      
      if (char == '<') {
        // 保存之前的文本
        if (buffer.isNotEmpty) {
          tokens.add(XMLToken.text(buffer.toString().trim()));
          buffer.clear();
        }
        
        // 找到标签结束
        final endIndex = content.indexOf('>', i);
        if (endIndex == -1) break;
        
        final tagContent = content.substring(i + 1, endIndex);
        
        if (tagContent.startsWith('/')) {
          // 结束标签
          tokens.add(XMLToken.closeTag(tagContent.substring(1)));
        } else if (tagContent.endsWith('/')) {
          // 自闭合标签
          tokens.add(XMLToken.selfClosingTag(tagContent.substring(0, tagContent.length - 1).trim()));
        } else {
          // 开始标签
          tokens.add(XMLToken.openTag(tagContent));
        }
        
        i = endIndex + 1;
      } else {
        buffer.write(char);
        i++;
      }
    }
    
    // 处理剩余文本
    if (buffer.isNotEmpty) {
      tokens.add(XMLToken.text(buffer.toString().trim()));
    }
    
    return tokens;
  }
  
  /// 构建语法树
  DSLNode _buildTree(List<XMLToken> tokens) {
    final stack = <_TreeNode>[];
    DSLNode? root;
    
    for (final token in tokens) {
      switch (token.type) {
        case XMLTokenType.openTag:
          final node = _TreeNode(
            tag: token.tag!,
            attributes: token.attributes ?? {},
          );
          
          if (stack.isNotEmpty) {
            stack.last.children.add(node);
          } else {
            root = node.toDSLNode();
          }
          
          // 非自闭合标签入栈
          if (!_isSelfClosingTag(token.tag!)) {
            stack.add(node);
          }
          break;
          
        case XMLTokenType.closeTag:
          // 出栈
          if (stack.isNotEmpty && stack.last.tag == token.tag) {
            final completed = stack.removeLast();
            if (stack.isEmpty) {
              root = completed.toDSLNode();
            }
          }
          break;
          
        case XMLTokenType.selfClosingTag:
          final node = _TreeNode(
            tag: token.tag!,
            attributes: token.attributes ?? {},
          );
          
          if (stack.isNotEmpty) {
            stack.last.children.add(node);
          } else if (root == null) {
            root = node.toDSLNode();
          }
          break;
          
        case XMLTokenType.text:
          if (stack.isNotEmpty && token.content!.isNotEmpty) {
            stack.last.textContent = token.content;
          }
          break;
      }
    }
    
    return root ?? DSLNode(tag: 'view', children: []);
  }
  
  /// 检查是否为自闭合标签
  bool _isSelfClosingTag(String tag) {
    final selfClosingTags = {'image', 'input', 'textarea', 'video', 'audio', 'canvas', 'icon'};
    return selfClosingTags.contains(tag);
  }
  
  /// 解析属性字符串
  Map<String, String> _parseAttributes(String attrString) {
    final attributes = <String, String>{};
    final regExp = RegExp(r'(\w[-\w]*)(?:\s*=\s*("[^"]*"|\'[^\']*\'|[^\s>]*))?');
    
    for (final match in regExp.allMatches(attrString)) {
      final name = match.group(1);
      var value = match.group(2);
      
      if (name != null) {
        if (value != null) {
          // 移除引号
          value = value.replaceAll(RegExp(r'^["\']|["\']$'), '');
        }
        attributes[name] = value ?? '';
      }
    }
    
    return attributes;
  }
}

/// XML词法单元
class XMLToken {
  final XMLTokenType type;
  final String? tag;
  final Map<String, String>? attributes;
  final String? content;
  
  XMLToken._({
    required this.type,
    this.tag,
    this.attributes,
    this.content,
  });
  
  factory XMLToken.openTag(String tagContent) {
    final parts = tagContent.trim().split(RegExp(r'\s+'));
    final tag = parts[0];
    final attrString = parts.sublist(1).join(' ');
    
    return XMLToken._(
      type: XMLTokenType.openTag,
      tag: tag,
      attributes: _parseAttributes(attrString),
    );
  }
  
  factory XMLToken.closeTag(String tag) {
    return XMLToken._(
      type: XMLTokenType.closeTag,
      tag: tag.trim(),
    );
  }
  
  factory XMLToken.selfClosingTag(String tagContent) {
    final parts = tagContent.trim().split(RegExp(r'\s+'));
    final tag = parts[0];
    final attrString = parts.sublist(1).join(' ');
    
    return XMLToken._(
      type: XMLTokenType.selfClosingTag,
      tag: tag,
      attributes: _parseAttributes(attrString),
    );
  }
  
  factory XMLToken.text(String content) {
    return XMLToken._(
      type: XMLTokenType.text,
      content: content,
    );
  }
  
  static Map<String, String> _parseAttributes(String attrString) {
    final attributes = <String, String>{};
    final regExp = RegExp(r'(\w[-\w:]*)(?:\s*=\s*("[^"]*"|\'[^\']*\'|[^\s>]*))?');
    
    for (final match in regExp.allMatches(attrString)) {
      final name = match.group(1);
      var value = match.group(2);
      
      if (name != null) {
        if (value != null) {
          value = value.replaceAll(RegExp(r'^["\']|["\']$'), '');
        }
        attributes[name] = value ?? '';
      }
    }
    
    return attributes;
  }
}

enum XMLTokenType {
  openTag,
  closeTag,
  selfClosingTag,
  text,
}

/// 树节点（构建过程中使用）
class _TreeNode {
  final String tag;
  final Map<String, String> attributes;
  final List<_TreeNode> children;
  String? textContent;
  
  _TreeNode({
    required this.tag,
    required this.attributes,
  }) : children = [];
  
  DSLNode toDSLNode() {
    return DSLNode(
      tag: tag,
      attributes: attributes,
      children: children.map((c) => c.toDSLNode()).toList(),
      textContent: textContent,
    );
  }
}

/// DSL节点
class DSLNode {
  final String tag;
  final Map<String, String> attributes;
  final List<DSLNode> children;
  final String? textContent;
  
  DSLNode({
    required this.tag,
    this.attributes = const {},
    this.children = const [],
    this.textContent,
  });
  
  /// 转换为JSON
  Map<String, dynamic> toJson() => {
    'tag': tag,
    'attributes': attributes,
    'children': children.map((c) => c.toJson()).toList(),
    'textContent': textContent,
  };
  
  @override
  String toString() => jsonEncode(toJson());
}

/// WXSS样式解析器
class WXSSParser {
  
  /// 解析WXSS内容
  Map<String, Map<String, dynamic>> parse(String wxssContent) {
    final styles = <String, Map<String, dynamic>>{};
    
    // 移除注释
    final cleanContent = wxssContent.replaceAll(RegExp(r'/\*[\s\S]*?\*/'), '');
    
    // 解析CSS规则
    final ruleRegExp = RegExp(r'([^{]+)\{([^}]+)\}');
    
    for (final match in ruleRegExp.allMatches(cleanContent)) {
      final selector = match.group(1)!.trim();
      final declarations = match.group(2)!.trim();
      
      final styleMap = <String, dynamic>{};
      
      // 解析声明
      for (final decl in declarations.split(';')) {
        final parts = decl.trim().split(':');
        if (parts.length == 2) {
          final property = parts[0].trim();
          final value = parts[1].trim();
          styleMap[property] = _parseValue(value);
        }
      }
      
      styles[selector] = styleMap;
    }
    
    return styles;
  }
  
  /// 解析CSS值
  dynamic _parseValue(String value) {
    // 尝试解析数字
    if (RegExp(r'^\d+(\.\d+)?$').hasMatch(value)) {
      return double.parse(value);
    }
    
    // 尝试解析带单位的值
    final unitMatch = RegExp(r'^(\d+(?:\.\d+)?)(px|rpx|em|rem|%|vh|vw)$').firstMatch(value);
    if (unitMatch != null) {
      final num = double.parse(unitMatch.group(1)!);
      final unit = unitMatch.group(2)!;
      return {'value': num, 'unit': unit};
    }
    
    // 颜色值
    if (value.startsWith('#') || value.startsWith('rgb') || value.startsWith('hsl')) {
      return {'type': 'color', 'value': value};
    }
    
    return value;
  }
}
