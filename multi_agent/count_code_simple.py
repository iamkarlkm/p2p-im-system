import os
import glob
from pathlib import Path

# 代码目录
code_dir = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'

# 文件扩展名
extensions = {
    'Java': ['*.java'],
    'JavaScript/TypeScript': ['*.js', '*.ts', '*.tsx', '*.jsx'],
    'Dart': ['*.dart'],
    'Rust': ['*.rs'],
    'HTML/CSS': ['*.html', '*.css']
}

# 统计结果
stats = {}
total_files = 0
total_lines = 0

for category, exts in extensions.items():
    stats[category] = {'files': 0, 'lines': 0}
    
    for ext in exts:
        # 递归查找文件
        for file_path in Path(code_dir).rglob(ext):
            if file_path.is_file():
                stats[category]['files'] += 1
                total_files += 1
                
                # 统计行数
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = sum(1 for _ in f)
                        stats[category]['lines'] += lines
                        total_lines += lines
                except:
                    pass

print("=== 代码统计结果 ===")
print(f"总文件数: {total_files}")
print(f"总代码行数: {total_lines}")
print("按类型分布:")
for category, data in stats.items():
    if data['files'] > 0:
        print(f"  {category}: {data['files']} 个文件, {data['lines']} 行")