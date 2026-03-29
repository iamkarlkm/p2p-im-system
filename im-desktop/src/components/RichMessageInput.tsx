/**
 * RichMessageInput.tsx
 * Text input component with markdown toolbar and preview
 * Supports: bold, italic, code, code blocks, links, mentions, emoji
 */

import React, { useState, useRef, useCallback } from 'react';

interface RichMessageInputProps {
  onSend?: (content: string) => void;
  placeholder?: string;
  maxLength?: number;
  className?: string;
}

interface ToolbarAction {
  label: string;
  icon: string;
  syntax: string;
  wrap?: boolean;
}

const TOOLBAR_ACTIONS: ToolbarAction[] = [
  { label: 'Bold', icon: 'B', syntax: '**text**', wrap: true },
  { label: 'Italic', icon: 'I', syntax: '*text*', wrap: true },
  { label: 'Code', icon: '<>', syntax: '`code`', wrap: true },
  { label: 'Code Block', icon: '{}', syntax: '```\ncode\n```', wrap: false },
  { label: 'Link', icon: '🔗', syntax: '[text](url)', wrap: false },
  { label: 'Mention', icon: '@', syntax: '@username', wrap: false },
  { label: 'Quote', icon: '"', syntax: '> quote', wrap: false },
  { label: 'List', icon: '•', syntax: '- item', wrap: false },
];

export const RichMessageInput: React.FC<RichMessageInputProps> = ({
  onSend,
  placeholder = 'Type a message... (Markdown supported)',
  maxLength = 10000,
  className = '',
}) => {
  const [value, setValue] = useState('');
  const [showPreview, setShowPreview] = useState(false);
  const [charCount, setCharCount] = useState(0);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const insertMarkdown = useCallback((action: ToolbarAction) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selected = value.substring(start, end);
    let newText = '';
    let newCursorPos = start;

    if (action.wrap && selected.length > 0) {
      // Wrap selected text
      newText = value.substring(0, start) + action.syntax.replace('text', selected) + value.substring(end);
      newCursorPos = start + action.syntax.indexOf('text');
    } else if (action.wrap) {
      // Insert placeholder
      newText = value.substring(0, start) + action.syntax + value.substring(end);
      newCursorPos = start + action.syntax.indexOf('text');
    } else {
      // Insert at cursor
      newText = value.substring(0, start) + '\n' + action.syntax + '\n' + value.substring(end);
      newCursorPos = start + 1 + action.syntax.length;
    }

    setValue(newText);
    setCharCount(newText.length);

    setTimeout(() => {
      textarea.focus();
      if (action.wrap && selected.length === 0) {
        textarea.setSelectionRange(newCursorPos, newCursorPos + 4);
      } else {
        textarea.setSelectionRange(newCursorPos, newCursorPos);
      }
    }, 0);
  }, [value]);

  const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
      e.preventDefault();
      handleSend();
      return;
    }
    
    if (e.key === 'Tab') {
      e.preventDefault();
      const textarea = e.currentTarget;
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const newValue = value.substring(0, start) + '  ' + value.substring(end);
      setValue(newValue);
      setTimeout(() => {
        textarea.setSelectionRange(start + 2, start + 2);
      }, 0);
    }
  }, [value]);

  const handleSend = useCallback(() => {
    if (value.trim().length === 0) return;
    if (value.length > maxLength) {
      alert(`Message exceeds ${maxLength} characters`);
      return;
    }
    onSend?.(value);
    setValue('');
    setCharCount(0);
  }, [value, maxLength, onSend]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const newValue = e.target.value;
    if (newValue.length <= maxLength) {
      setValue(newValue);
      setCharCount(newValue.length);
    }
  }, [maxLength]);

  return (
    <div className={`rich-message-input ${className}`} style={styles.container}>
      {/* Toolbar */}
      <div style={styles.toolbar}>
        <div style={styles.toolbarLeft}>
          {TOOLBAR_ACTIONS.map((action) => (
            <button
              key={action.label}
              type="button"
              onClick={() => insertMarkdown(action)}
              title={action.label}
              style={styles.toolbarBtn}
              onMouseEnter={(e) => (e.currentTarget.style.background = '#e9ecef')}
              onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}
            >
              {action.icon}
            </button>
          ))}
        </div>
        <div style={styles.toolbarRight}>
          <button
            type="button"
            onClick={() => setShowPreview(!showPreview)}
            style={{
              ...styles.toolbarBtn,
              fontWeight: showPreview ? 'bold' : 'normal',
              color: showPreview ? '#0d6efd' : '#666',
            }}
            title="Toggle Preview"
          >
            Preview
          </button>
        </div>
      </div>

      {/* Preview Mode */}
      {showPreview && (
        <div style={styles.preview}>
          <div style={styles.previewLabel}>Preview:</div>
          <div style={styles.previewContent}>
            <MarkdownPreview content={value} />
          </div>
        </div>
      )}

      {/* Input Area */}
      <textarea
        ref={textareaRef}
        value={value}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        placeholder={placeholder}
        style={styles.textarea}
        rows={4}
      />

      {/* Footer */}
      <div style={styles.footer}>
        <span style={styles.hint}>
          Supports **bold**, *italic*, `code`, ```code blocks```, [links](url), @mentions
        </span>
        <div style={styles.footerRight}>
          <span style={{
            ...styles.charCount,
            color: charCount > maxLength * 0.9 ? '#dc3545' : '#666',
          }}>
            {charCount}/{maxLength}
          </span>
          <button
            type="button"
            onClick={handleSend}
            disabled={value.trim().length === 0}
            style={{
              ...styles.sendBtn,
              opacity: value.trim().length === 0 ? 0.5 : 1,
            }}
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
};

const MarkdownPreview: React.FC<{ content: string }> = ({ content }) => {
  const html = parseSimpleMarkdown(content);
  return <div style={styles.previewInner} dangerouslySetInnerHTML={{ __html: html }} />;
};

function parseSimpleMarkdown(text: string): string {
  if (!text) return '<span style="color:#999">Nothing to preview</span>';
  
  let html = escapeHtml(text);
  
  // Code blocks
  html = html.replace(/```(\w*)\n([\s\S]*?)```/g, (_, lang, code) => {
    return `<pre style="background:#1e1e1e;color:#d4d4d4;padding:12px;border-radius:8px;overflow-x:auto;margin:8px 0"><code>${code}</code></pre>`;
  });
  
  // Inline code
  html = html.replace(/`([^`]+)`/g, '<code style="background:#f0f0f0;padding:2px 6px;border-radius:4px;color:#d63384;font-family:monospace">$1</code>');
  
  // Bold ** or __
  html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  html = html.replace(/__([^_]+)__/g, '<strong>$1</strong>');
  
  // Italic
  html = html.replace(/\*([^*]+)\*/g, '<em>$1</em>');
  
  // Links
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" style="color:#0d6efd">$1</a>');
  
  // Mentions
  html = html.replace(/@(\w+)/g, '<span style="color:#0d6efd;font-weight:500">@$1</span>');
  
  // Quotes
  html = html.replace(/^&gt;\s+(.+)$/gm, '<blockquote style="border-left:3px solid #ccc;padding-left:12px;color:#666;font-style:italic">$1</blockquote>');
  
  // Line breaks
  html = html.replace(/\n/g, '<br>');
  
  return html;
}

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    border: '1px solid #ddd',
    borderRadius: '8px',
    background: '#fff',
    padding: '8px',
  },
  toolbar: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottom: '1px solid #eee',
    paddingBottom: '6px',
    marginBottom: '6px',
  },
  toolbarLeft: {
    display: 'flex',
    gap: '2px',
  },
  toolbarRight: {
    display: 'flex',
    gap: '4px',
  },
  toolbarBtn: {
    background: 'transparent',
    border: 'none',
    padding: '4px 8px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    color: '#333',
  },
  preview: {
    background: '#f8f9fa',
    borderRadius: '4px',
    padding: '8px',
    marginBottom: '8px',
    maxHeight: '200px',
    overflow: 'auto',
  },
  previewLabel: {
    fontSize: '11px',
    color: '#999',
    marginBottom: '4px',
    textTransform: 'uppercase',
  },
  previewContent: {},
  previewInner: {
    fontSize: '14px',
    lineHeight: '1.5',
  },
  textarea: {
    width: '100%',
    border: 'none',
    outline: 'none',
    resize: 'none',
    fontSize: '14px',
    fontFamily: "'Segoe UI', sans-serif",
    lineHeight: '1.5',
    padding: '4px 8px',
    background: 'transparent',
  },
  footer: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '4px',
    paddingTop: '4px',
    borderTop: '1px solid #eee',
  },
  hint: {
    fontSize: '11px',
    color: '#999',
  },
  footerRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  charCount: {
    fontSize: '12px',
  },
  sendBtn: {
    background: '#0d6efd',
    color: '#fff',
    border: 'none',
    padding: '6px 16px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: 500,
  },
};

export default RichMessageInput;
