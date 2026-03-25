package com.im.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RichMessageContent {
    
    private String rawContent;
    private String htmlContent;
    private List<ContentSegment> segments;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContentSegment {
        private SegmentType type;
        private String content;
        private String language;
        private int startIndex;
        private int endIndex;
        
        public enum SegmentType {
            TEXT, BOLD, ITALIC, CODE, CODE_BLOCK, 
            LINK, MENTION, EMOJI, LIST, QUOTE, NEWLINE
        }
    }
    
    public static RichMessageContent parse(String raw) {
        if (raw == null || raw.isEmpty()) {
            return RichMessageContent.builder()
                    .rawContent("")
                    .htmlContent("")
                    .segments(new ArrayList<>())
                    .build();
        }
        
        List<ContentSegment> segments = new ArrayList<>();
        StringBuilder html = new StringBuilder();
        String[] lines = raw.split("\n", -1);
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            if (i > 0) html.append("<br>");
            
            if (line.startsWith("> ")) {
                html.append("<blockquote>").append(escapeHtml(line.substring(2))).append("</blockquote>");
            } else if (line.matches("^[-*]\\s+.*")) {
                String item = line.replaceFirst("^[-*]\\s+", "");
                html.append("<li>").append(parseInlineElements(item)).append("</li>");
            } else if (line.matches("^\\d+\\.\\s+.*")) {
                String item = line.replaceFirst("^\\d+\\.\\s+", "");
                html.append("<li>").append(parseInlineElements(item)).append("</li>");
            } else {
                html.append(parseInlineElements(line));
            }
        }
        
        return RichMessageContent.builder()
                .rawContent(raw)
                .htmlContent(html.toString())
                .segments(segments)
                .build();
    }
    
    private static String parseInlineElements(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        
        while (i < text.length()) {
            if (text.charAt(i) == '`' && i + 1 < text.length() && text.charAt(i + 1) == '`' && i + 2 < text.length() && text.charAt(i + 2) == '`') {
                int end = findCodeBlockEnd(text, i + 3);
                String code = text.substring(i + 3, end);
                String lang = "";
                int langIdx = code.indexOf('\n');
                if (langIdx > 0 && !code.substring(0, langIdx).contains(" ")) {
                    lang = code.substring(0, langIdx).trim();
                    code = code.substring(langIdx + 1);
                }
                sb.append("<pre><code class=\"language-").append(lang).append("\">")
                  .append(escapeHtml(code)).append("</code></pre>");
                i = end + 3;
            } else if (text.charAt(i) == '`') {
                int end = findInlineEnd(text, i + 1, '`');
                sb.append("<code>").append(escapeHtml(text.substring(i + 1, end))).append("</code>");
                i = end + 1;
            } else if (text.charAt(i) == '*' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
                int end = findMatchingEnd(text, i + 2, "**");
                sb.append("<strong>").append(escapeHtml(text.substring(i + 2, end))).append("</strong>");
                i = end + 2;
            } else if (text.charAt(i) == '_' && i + 1 < text.length() && text.charAt(i + 1) == '_') {
                int end = findMatchingEnd(text, i + 2, "__");
                sb.append("<strong>").append(escapeHtml(text.substring(i + 2, end))).append("</strong>");
                i = end + 2;
            } else if (text.charAt(i) == '*') {
                int end = findMatchingEnd(text, i + 1, "*");
                sb.append("<em>").append(escapeHtml(text.substring(i + 1, end))).append("</em>");
                i = end + 1;
            } else if (text.charAt(i) == '@' && i + 1 < text.length() && isAlphanumeric(text.charAt(i + 1))) {
                int end = i + 1;
                while (end < text.length() && (isAlphanumeric(text.charAt(end)) || text.charAt(end) == '_')) end++;
                String mention = text.substring(i, end);
                sb.append("<span class=\"mention\" data-user=\"").append(mention.substring(1)).append("\">@").append(mention.substring(1)).append("</span>");
                i = end;
            } else if (text.charAt(i) == '[') {
                int bracketEnd = text.indexOf(']', i);
                int parenStart = bracketEnd >= 0 ? text.indexOf('(', bracketEnd) : -1;
                int parenEnd = parenStart >= 0 ? text.indexOf(')', parenStart) : -1;
                if (bracketEnd > i && parenStart == bracketEnd + 1 && parenEnd > parenStart) {
                    String linkText = text.substring(i + 1, bracketEnd);
                    String url = text.substring(parenStart + 1, parenEnd);
                    sb.append("<a href=\"").append(escapeHtml(url)).append("\" target=\"_blank\">").append(escapeHtml(linkText)).append("</a>");
                    i = parenEnd + 1;
                } else {
                    sb.append(escapeHtml(String.valueOf(text.charAt(i++))));
                }
            } else {
                sb.append(escapeHtml(String.valueOf(text.charAt(i++))));
            }
        }
        
        return sb.toString();
    }
    
    private static int findCodeBlockEnd(String text, int start) {
        int end = start;
        while (end + 2 < text.length()) {
            if (text.charAt(end) == '`' && text.charAt(end + 1) == '`' && text.charAt(end + 2) == '`') {
                return end;
            }
            if (text.charAt(end) == '\n') {
                int nextLine = text.indexOf('\n', end + 1);
                if (nextLine < 0) nextLine = text.length();
                end = nextLine;
            } else {
                end++;
            }
        }
        return text.length();
    }
    
    private static int findInlineEnd(String text, int start, String delimiter) {
        int pos = text.indexOf(delimiter, start);
        return pos > start ? pos : text.length();
    }
    
    private static int findMatchingEnd(String text, int start, String delimiter) {
        int pos = text.indexOf(delimiter, start);
        return pos > start ? pos : text.length();
    }
    
    private static boolean isAlphanumeric(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
    
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    public static String supportedMarkdown() {
        return "Supported: **bold**, *italic*, `code`, ```code block```, [text](url), @mentions, > quotes, - lists";
    }
}
