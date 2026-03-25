import 'package:flutter/material.dart';

/// RichContentRenderer - Renders markdown/rich text in Flutter mobile UI
/// Supports: bold, italic, code, code blocks, links, mentions, quotes, lists

class RichContentRenderer extends StatelessWidget {
  final String content;
  final TextStyle? style;
  final void Function(String username)? onMentionTap;
  final void Function(String url)? onLinkTap;

  const RichContentRenderer({
    super.key,
    required this.content,
    this.style,
    this.onMentionTap,
    this.onLinkTap,
  });

  @override
  Widget build(BuildContext context) {
    final segments = _parseContent(content);
    return Wrap(
      children: segments.map((seg) => _buildSegment(context, seg)).toList(),
    );
  }

  Widget _buildSegment(BuildContext context, _Segment seg) {
    final baseStyle = style ?? Theme.of(context).textTheme.bodyMedium;

    switch (seg.type) {
      case _SegmentType.bold:
        return Text(seg.content, style: baseStyle?.copyWith(fontWeight: FontWeight.bold));

      case _SegmentType.italic:
        return Text(seg.content, style: baseStyle?.copyWith(fontStyle: FontStyle.italic));

      case _SegmentType.code:
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
          decoration: BoxDecoration(
            color: Colors.grey.shade200,
            borderRadius: BorderRadius.circular(4),
          ),
          child: Text(seg.content,
            style: baseStyle?.copyWith(
              fontFamily: 'monospace',
              fontSize: (baseStyle.fontSize ?? 14) * 0.9,
              color: Colors.pink.shade700,
            ),
          ),
        );

      case _SegmentType.codeBlock:
        return Container(
          width: double.infinity,
          margin: const EdgeInsets.symmetric(vertical: 8),
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: const Color(0xFF1E1E1E),
            borderRadius: BorderRadius.circular(8),
          ),
          child: SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Text(seg.content,
              style: const TextStyle(
                fontFamily: 'monospace',
                fontSize: 13,
                color: Color(0xFFD4D4D4),
              ),
            ),
          ),
        );

      case _SegmentType.link:
        return GestureDetector(
          onTap: () => onLinkTap?.call(seg.url ?? ''),
          child: Text(seg.content,
            style: baseStyle?.copyWith(
              color: Colors.blue,
              decoration: TextDecoration.underline,
            ),
          ),
        );

      case _SegmentType.mention:
        return GestureDetector(
          onTap: () => onMentionTap?.call(seg.username ?? ''),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            decoration: BoxDecoration(
              color: Colors.blue.withOpacity(0.1),
              borderRadius: BorderRadius.circular(4),
            ),
            child: Text('@${seg.username}',
              style: baseStyle?.copyWith(
                color: Colors.blue,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        );

      case _SegmentType.quote:
        return Container(
          width: double.infinity,
          margin: const EdgeInsets.symmetric(vertical: 4),
          padding: const EdgeInsets.only(left: 12),
          decoration: const BoxDecoration(
            border: Border(left: BorderSide(color: Colors.grey, width: 3)),
          ),
          child: Text(seg.content,
            style: baseStyle?.copyWith(
              color: Colors.grey.shade600,
              fontStyle: FontStyle.italic,
            ),
          ),
        );

      case _SegmentType.newline:
        return const SizedBox(height: 8);

      case _SegmentType.listItem:
        return Padding(
          padding: const EdgeInsets.only(left: 16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('• '),
              Expanded(child: Text(seg.content, style: baseStyle)),
            ],
          ),
        );

      case _SegmentType.text:
      default:
        return Text(seg.content, style: baseStyle);
    }
  }

  List<_Segment> _parseContent(String text) {
    final segments = <_Segment>[];
    final lines = text.split('\n');

    for (int i = 0; i < lines.length; i++) {
      final line = lines[i];

      // Quote block
      if (line.startsWith('> ')) {
        segments.add(_Segment(_SegmentType.quote, line.substring(2)));
        continue;
      }

      // Unordered list
      if (line.startsWith('- ') || line.startsWith('* ')) {
        segments.add(_Segment(_SegmentType.listItem, line.substring(2)));
        if (i < lines.length - 1) segments.add(_Segment(_SegmentType.newline));
        continue;
      }

      // Ordered list
      final orderedMatch = RegExp(r'^\d+\.\s+(.*)').firstMatch(line);
      if (orderedMatch != null) {
        segments.add(_Segment(_SegmentType.listItem, orderedMatch.group(1) ?? ''));
        if (i < lines.length - 1) segments.add(_Segment(_SegmentType.newline));
        continue;
      }

      // Code block
      if (line.startsWith('```')) {
        final lang = line.substring(3).trim();
        final codeLines = <String>[];
        int j = i + 1;
        while (j < lines.length && !lines[j].startsWith('```')) {
          codeLines.add(lines[j]);
          j++;
        }
        segments.add(_Segment(_SegmentType.codeBlock, codeLines.join('\n'), language: lang));
        i = j;
        if (i < lines.length - 1) segments.add(_Segment(_SegmentType.newline));
        continue;
      }

      // Parse inline elements
      if (line.isNotEmpty) {
        segments.addAll(_parseInline(line));
      }

      if (i < lines.length - 1) {
        segments.add(_Segment(_SegmentType.newline));
      }
    }

    return segments;
  }

  List<_Segment> _parseInline(String text) {
    final segments = <_Segment>[];
    int i = 0;

    while (i < text.length) {
      // Code block ```
      if (text.substring(i).startsWith('```')) {
        final end = text.indexOf('```', i + 3);
        final code = end > i + 3 ? text.substring(i + 3, end) : text.substring(i + 3);
        segments.add(_Segment(_SegmentType.codeBlock, code));
        i = end > i + 3 ? end + 3 : text.length;
        continue;
      }

      // Inline code `
      if (text[i] == '`') {
        final end = text.indexOf('`', i + 1);
        final code = end > i ? text.substring(i + 1, end) : text.substring(i + 1);
        segments.add(_Segment(_SegmentType.code, code));
        i = end > i ? end + 1 : text.length;
        continue;
      }

      // Bold ** or __
      if (i + 1 < text.length && (text.substring(i, i + 2) == '**' || text.substring(i, i + 2) == '__')) {
        final delim = text.substring(i, i + 2);
        final end = text.indexOf(delim, i + 2);
        final bold = end > i + 2 ? text.substring(i + 2, end) : text.substring(i + 2);
        segments.add(_Segment(_SegmentType.bold, bold));
        i = end > i + 2 ? end + 2 : text.length;
        continue;
      }

      // Italic
      if (text[i] == '*' || text[i] == '_') {
        final end = text.indexOf(text[i], i + 1);
        if (end > i && text.substring(i, i + 2) != '**' && text.substring(i, i + 2) != '__') {
          final italic = text.substring(i + 1, end);
          segments.add(_Segment(_SegmentType.italic, italic));
          i = end + 1;
          continue;
        }
      }

      // Mention
      if (text[i] == '@') {
        int end = i + 1;
        while (end < text.length && _isAlphanumeric(text[end])) end++;
        final username = text.substring(i + 1, end);
        segments.add(_Segment(_SegmentType.mention, '@$username', username: username));
        i = end;
        continue;
      }

      // Link [text](url)
      if (text[i] == '[') {
        final bracketEnd = text.indexOf(']', i);
        final parenStart = text.indexOf('(', bracketEnd + 1);
        final parenEnd = text.indexOf(')', parenStart + 1);
        if (bracketEnd > i && parenStart == bracketEnd + 1 && parenEnd > parenStart) {
          final linkText = text.substring(i + 1, bracketEnd);
          final url = text.substring(parenStart + 1, parenEnd);
          segments.add(_Segment(_SegmentType.link, linkText, url: url));
          i = parenEnd + 1;
          continue;
        }
      }

      // Regular text
      int j = i;
      while (j < text.length && !_isSpecial(text[j])) j++;
      if (j > i) {
        segments.add(_Segment(_SegmentType.text, text.substring(i, j)));
        i = j;
      } else {
        segments.add(_Segment(_SegmentType.text, text[i]));
        i++;
      }
    }

    return segments;
  }

  bool _isAlphanumeric(String c) {
    return RegExp(r'[a-zA-Z0-9_]').hasMatch(c);
  }

  bool _isSpecial(String c) {
    return '`*_@[]()'.contains(c);
  }
}

enum _SegmentType {
  text, bold, italic, code, codeBlock, link, mention, quote, newline, listItem
}

class _Segment {
  final _SegmentType type;
  final String content;
  final String? language;
  final String? url;
  final String? username;

  _Segment(this.type, this.content, {this.language, this.url, this.username});
}
