/**
 * RichContentRenderer.tsx
 * Renders markdown/rich text messages in the chat UI
 * Supports: bold, italic, code, code blocks, links, mentions, quotes, lists
 */

import React, { useMemo } from 'react';

interface RichContentRendererProps {
  content: string;
  className?: string;
  onMentionClick?: (username: string) => void;
  onLinkClick?: (url: string) => void;
}

interface ParsedSegment {
  type: 'text' | 'bold' | 'italic' | 'code' | 'codeBlock' | 'link' | 'mention' | 'quote' | 'listItem' | 'newline';
  content: string;
  language?: string;
  url?: string;
  username?: string;
}

export const RichContentRenderer: React.FC<RichContentRendererProps> = ({
  content,
  className = '',
  onMentionClick,
  onLinkClick,
}) => {
  const parsed = useMemo(() => parseMarkdown(content), [content]);

  const renderSegment = (segment: ParsedSegment, index: number) => {
    switch (segment.type) {
      case 'bold':
        return <strong key={index}>{segment.content}</strong>;
      
      case 'italic':
        return <em key={index}>{segment.content}</em>;
      
      case 'code':
        return <code key={index} className="inline-code">{segment.content}</code>;
      
      case 'codeBlock':
        return (
          <pre key={index} className="code-block">
            <code className={segment.language ? `language-${segment.language}` : ''}>
              {segment.content}
            </code>
          </pre>
        );
      
      case 'link':
        return (
          <a
            key={index}
            href={segment.url}
            target="_blank"
            rel="noopener noreferrer"
            className="rich-link"
            onClick={(e) => {
              e.preventDefault();
              onLinkClick?.(segment.url || '');
            }}
          >
            {segment.content}
          </a>
        );
      
      case 'mention':
        return (
          <span
            key={index}
            className="mention"
            onClick={() => onMentionClick?.(segment.username || '')}
          >
            @{segment.username}
          </span>
        );
      
      case 'quote':
        return <blockquote key={index} className="rich-quote">{segment.content}</blockquote>;
      
      case 'listItem':
        return <li key={index} className="rich-list-item">{segment.content}</li>;
      
      case 'newline':
        return <br key={index} />;
      
      case 'text':
      default:
        return <span key={index}>{segment.content}</span>;
    }
  };

  return (
    <div className={`rich-content-renderer ${className}`} style={styles.container}>
      {parsed.map((segment, index) => renderSegment(segment, index))}
      <style>{cssStyles}</style>
    </div>
  );
};

function parseMarkdown(text: string): ParsedSegment[] {
  const segments: ParsedSegment[] = [];
  const lines = text.split('\n');
  
  let inList = false;
  let listItems: ParsedSegment[] = [];
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    
    // Quote block
    if (line.startsWith('> ')) {
      segments.push({ type: 'quote', content: line.substring(2) });
      continue;
    }
    
    // Unordered list
    if (line.match(/^[-*]\s+/)) {
      if (inList) {
        listItems.push({ type: 'listItem', content: parseInline(line.replace(/^[-*]\s+/, '')) });
      } else {
        if (listItems.length > 0) {
          segments.push(...listItems);
          listItems = [];
        }
        inList = true;
        listItems.push({ type: 'listItem', content: parseInline(line.replace(/^[-*]\s+/, '')) });
      }
      if (i < lines.length - 1 && !lines[i + 1].match(/^[-*]\s+/) && !lines[i + 1].match(/^\d+\.\s+/)) {
        segments.push(...listItems);
        listItems = [];
        inList = false;
      }
      continue;
    }
    
    // Ordered list
    if (line.match(/^\d+\.\s+/)) {
      if (inList) {
        listItems.push({ type: 'listItem', content: parseInline(line.replace(/^\d+\.\s+/, '')) });
      } else {
        if (listItems.length > 0) {
          segments.push(...listItems);
          listItems = [];
        }
        inList = true;
        listItems.push({ type: 'listItem', content: parseInline(line.replace(/^\d+\.\s+/, '')) });
      }
      if (i < lines.length - 1 && !lines[i + 1].match(/^[-*]\s+/) && !lines[i + 1].match(/^\d+\.\s+/)) {
        segments.push(...listItems);
        listItems = [];
        inList = false;
      }
      continue;
    }
    
    // Flush pending list
    if (listItems.length > 0) {
      segments.push(...listItems);
      listItems = [];
      inList = false;
    }
    
    // Code block (```...```)
    if (line.startsWith('```')) {
      const lang = line.substring(3).trim();
      const codeLines: string[] = [];
      let j = i + 1;
      while (j < lines.length && !lines[j].startsWith('```')) {
        codeLines.push(lines[j]);
        j++;
      }
      segments.push({ type: 'codeBlock', content: codeLines.join('\n'), language: lang });
      i = j;
      continue;
    }
    
    // Regular text line
    if (line.length > 0) {
      segments.push(...parseInlineToSegments(line));
    }
    
    // Add newline between lines (except for last line)
    if (i < lines.length - 1 && line.length > 0) {
      segments.push({ type: 'newline', content: '' });
    }
  }
  
  return segments;
}

function parseInline(text: string): string {
  return parseInlineToSegments(text)
    .map(seg => seg.content)
    .join('');
}

function parseInlineToSegments(text: string): ParsedSegment[] {
  const segments: ParsedSegment[] = [];
  let i = 0;
  
  while (i < text.length) {
    // Code block ```
    if (text.slice(i, i + 3) === '```') {
      const end = text.indexOf('```', i + 3);
      const code = end > i + 3 ? text.slice(i + 3, end) : text.slice(i + 3);
      segments.push({ type: 'codeBlock', content: code });
      i = end > i + 3 ? end + 3 : text.length;
      continue;
    }
    
    // Inline code `
    if (text[i] === '`') {
      const end = text.indexOf('`', i + 1);
      const code = end > i ? text.slice(i + 1, end) : text.slice(i + 1);
      segments.push({ type: 'code', content: code });
      i = end > i ? end + 1 : text.length;
      continue;
    }
    
    // Bold ** or __
    if ((text.slice(i, i + 2) === '**' || text.slice(i, i + 2) === '__') && i + 2 < text.length) {
      const delim = text.slice(i, i + 2);
      const end = text.indexOf(delim, i + 2);
      const bold = end > i + 2 ? text.slice(i + 2, end) : text.slice(i + 2);
      segments.push({ type: 'bold', content: bold });
      i = end > i + 2 ? end + 2 : text.length;
      continue;
    }
    
    // Italic * or _
    if ((text[i] === '*' || text[i] === '_') && i + 1 < text.length) {
      const end = findMatchingEnd(text, i + 1, text[i]);
      const italic = text.slice(i + 1, end);
      segments.push({ type: 'italic', content: italic });
      i = end + 1;
      continue;
    }
    
    // Mention @username
    if (text[i] === '@') {
      let end = i + 1;
      while (end < text.length && isAlphanumeric(text[end])) end++;
      const username = text.slice(i + 1, end);
      segments.push({ type: 'mention', content: '@' + username, username });
      i = end;
      continue;
    }
    
    // Link [text](url)
    if (text[i] === '[') {
      const bracketEnd = text.indexOf(']', i);
      const parenStart = text.indexOf('(', bracketEnd + 1);
      const parenEnd = text.indexOf(')', parenStart + 1);
      if (bracketEnd > i && parenStart === bracketEnd + 1 && parenEnd > parenStart) {
        const linkText = text.slice(i + 1, bracketEnd);
        const url = text.slice(parenStart + 1, parenEnd);
        segments.push({ type: 'link', content: linkText, url });
        i = parenEnd + 1;
        continue;
      }
    }
    
    // Regular text
    let j = i;
    while (j < text.length && !isSpecial(text[j])) j++;
    if (j > i) {
      segments.push({ type: 'text', content: text.slice(i, j) });
      i = j;
    } else {
      segments.push({ type: 'text', content: text[i] });
      i++;
    }
  }
  
  return segments;
}

function findMatchingEnd(text: string, start: number, delimiter: string): number {
  let depth = 1;
  for (let i = start; i < text.length; i++) {
    if (text[i] === delimiter[0] && text.slice(i, i + delimiter.length) === delimiter) {
      return i;
    }
  }
  return text.length;
}

function isAlphanumeric(c: string): boolean {
  return /[a-zA-Z0-9_]/.test(c);
}

function isSpecial(c: string): boolean {
  return c === '`' || c === '*' || c === '_' || c === '@' || c === '[' || c === ']' || c === '(' || c === ')';
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    wordBreak: 'break-word',
    lineHeight: '1.5',
  },
};

const cssStyles = `
.rich-content-renderer .inline-code {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 0.9em;
  color: #d63384;
}
.rich-content-renderer .code-block {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 0.85em;
  line-height: 1.4;
}
.rich-content-renderer .rich-link {
  color: #0d6efd;
  textDecoration: 'underline';
  cursor: pointer;
}
.rich-content-renderer .mention {
  color: #0d6efd;
  fontWeight: 500;
  cursor: pointer;
  background: rgba(13, 110, 253, 0.1);
  padding: 0 4px;
  borderRadius: 4px;
}
.rich-content-renderer .rich-quote {
  border-left: 3px solid #ccc;
  margin: 4px 0;
  padding-left: 12px;
  color: #666;
  font-style: italic;
}
.rich-content-renderer .rich-list-item {
  margin-left: 20px;
}
`;

export default RichContentRenderer;
