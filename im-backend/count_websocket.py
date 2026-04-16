import os

base_path = 'C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/im-modular/im-service-websocket/src/main/java/com/im/service/websocket'
files = [
    'config/WebSocketConfig.java',
    'handler/MessageWebSocketHandler.java',
    'service/OnlineStatusService.java',
    'manager/WebSocketSessionManager.java',
    'interceptor/WebSocketAuthInterceptor.java',
    'model/WebSocketMessage.java'
]

stats = []
total_lines = 0
total_code = 0
total_comment = 0
total_blank = 0

for f in files:
    path = os.path.join(base_path, f)
    if os.path.exists(path):
        with open(path, 'r', encoding='utf-8') as file:
            lines = file.readlines()
            line_count = len(lines)
            code_count = 0
            comment_count = 0
            blank_count = 0
            
            for line in lines:
                stripped = line.strip()
                if not stripped:
                    blank_count += 1
                elif stripped.startswith('//') or stripped.startswith('/*') or stripped.startswith('*') or stripped.startswith('*/'):
                    comment_count += 1
                else:
                    code_count += 1
            
            stats.append({
                'file': f.split('/')[-1],
                'total': line_count,
                'code': code_count,
                'comment': comment_count,
                'blank': blank_count
            })
            total_lines += line_count
            total_code += code_count
            total_comment += comment_count
            total_blank += blank_count

print('=== im-service-websocket 代码统计 ===')
print(f'{"文件名":<35} {"总行":>6} {"代码":>6} {"注释":>6} {"空行":>6}')
print('-' * 65)
for s in stats:
    print(f"{s['file']:<35} {s['total']:>6} {s['code']:>6} {s['comment']:>6} {s['blank']:>6}")
print('-' * 65)
print(f'{"总计":<35} {total_lines:>6} {total_code:>6} {total_comment:>6} {total_blank:>6}')
print(f"\n代码占比: {total_code/total_lines*100:.1f}%")
print(f"注释占比: {total_comment/total_lines*100:.1f}%")

# Save to file
with open('C:/Users/Administrator/.openclaw/workspace-clawd3/multi_agent/projects/im-modular/WEBSOCKET_STATS.txt', 'w', encoding='utf-8') as f:
    f.write("# im-service-websocket 代码统计报告\n\n")
    f.write("| 文件名 | 总行 | 代码 | 注释 | 空行 |\n")
    f.write("|--------|------|------|------|------|\n")
    for s in stats:
        f.write(f"| {s['file']} | {s['total']} | {s['code']} | {s['comment']} | {s['blank']} |\n")
    f.write(f"| **总计** | **{total_lines}** | **{total_code}** | **{total_comment}** | **{total_blank}** |\n")
    f.write(f"\n## 统计摘要\n")
    f.write(f"- **总文件数**: {len(stats)}\n")
    f.write(f"- **总行数**: {total_lines}\n")
    f.write(f"- **代码行数**: {total_code} ({total_code/total_lines*100:.1f}%)\n")
    f.write(f"- **注释行数**: {total_comment} ({total_comment/total_lines*100:.1f}%)\n")
    f.write(f"- **空行数**: {total_blank} ({total_blank/total_lines*100:.1f}%)\n")
    
print("\n统计报告已保存到 WEBSOCKET_STATS.txt")
