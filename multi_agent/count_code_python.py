#!/usr/bin/env python3
import os
import sys
from pathlib import Path

# 监控目录
code_dir = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
knowledge_dir = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"

# 文件类型扩展名
extensions = {
    ".java": "Java",
    ".js": "JavaScript",
    ".ts": "JavaScript", 
    ".tsx": "JavaScript",
    ".jsx": "JavaScript",
    ".dart": "Dart",
    ".rs": "Rust",
    ".html": "HTML/CSS",
    ".css": "HTML/CSS"
}

# 初始化计数器
total_files = 0
total_lines = 0
type_counts = {
    "Java": {"count": 0, "lines": 0},
    "JavaScript": {"count": 0, "lines": 0},
    "Dart": {"count": 0, "lines": 0},
    "Rust": {"count": 0, "lines": 0},
    "HTML/CSS": {"count": 0, "lines": 0}
}

print(f"正在扫描目录: {code_dir}")
print(f"要统计的文件类型: {', '.join(extensions.keys())}")

# 递归查找所有文件
for root, dirs, files in os.walk(code_dir):
    for file in files:
        file_ext = Path(file).suffix.lower()
        if file_ext in extensions:
            file_type = extensions[file_ext]
            total_files += 1
            type_counts[file_type]["count"] += 1
            
            # 统计行数
            file_path = os.path.join(root, file)
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    line_count = sum(1 for _ in f)
                total_lines += line_count
                type_counts[file_type]["lines"] += line_count
            except Exception as e:
                print(f"读取文件 {file_path} 时出错: {e}")

# 输出结果
print(f"\n代码统计结果:")
print(f"总文件数: {total_files}")
print(f"总代码行数: {total_lines}")
print(f"\n文件类型分布:")
for file_type, stats in type_counts.items():
    print(f"{file_type}: {stats['count']} 个文件, {stats['lines']} 行")

# 保存到临时文件
stats_content = f"""总文件数: {total_files}
总代码行数: {total_lines}
---
Java: {type_counts['Java']['count']} 个文件, {type_counts['Java']['lines']} 行
JavaScript: {type_counts['JavaScript']['count']} 个文件, {type_counts['JavaScript']['lines']} 行
Dart: {type_counts['Dart']['count']} 个文件, {type_counts['Dart']['lines']} 行
Rust: {type_counts['Rust']['count']} 个文件, {type_counts['Rust']['lines']} 行
HTML/CSS: {type_counts['HTML/CSS']['count']} 个文件, {type_counts['HTML/CSS']['lines']} 行
"""

temp_file = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\temp_code_stats.txt"
with open(temp_file, 'w', encoding='utf-8') as f:
    f.write(stats_content)

print(f"\n统计结果已保存到: {temp_file}")

# 返回统计结果
print("\n统计完成!")
print(f"{{")
print(f"  'totalFiles': {total_files},")
print(f"  'totalLines': {total_lines},")
for file_type, stats in type_counts.items():
    print(f"  '{file_type.lower()}Count': {stats['count']},")
    print(f"  '{file_type.lower()}Lines': {stats['lines']},")
print(f"}}")