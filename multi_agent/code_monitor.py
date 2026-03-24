#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
代码统计脚本 - 统一口径
统计指定目录下所有代码文件
"""

import os
import sys
from pathlib import Path

def count_code_lines(directory_path):
    """统计指定目录下的代码行数和文件数"""
    
    # 定义要统计的文件类型
    file_extensions = {
        'Java': ['.java'],
        'JavaScript': ['.js', '.jsx'],
        'TypeScript': ['.ts', '.tsx'],
        'Dart': ['.dart'],
        'Rust': ['.rs'],
        'HTML': ['.html', '.htm'],
        'CSS': ['.css', '.scss', '.less']
    }
    
    # 反向映射：扩展名 -> 语言类型
    ext_to_lang = {}
    for lang, exts in file_extensions.items():
        for ext in exts:
            ext_to_lang[ext] = lang
    
    # 统计结果
    stats = {
        'total_files': 0,
        'total_lines': 0,
        'by_language': {lang: {'files': 0, 'lines': 0} for lang in file_extensions.keys()},
        'file_list': []
    }
    
    directory = Path(directory_path)
    if not directory.exists():
        print(f"目录不存在: {directory_path}")
        return stats
    
    for root, dirs, files in os.walk(directory_path):
        # 跳过node_modules等目录
        dirs[:] = [d for d in dirs if d not in ['node_modules', '.git', 'target', 'build', 'dist', '.idea', '.vscode']]
        
        for file in files:
            file_path = Path(root) / file
            ext = file_path.suffix.lower()
            
            # 检查文件扩展名是否在统计范围内
            language = None
            for lang, exts in file_extensions.items():
                if ext in exts:
                    language = lang
                    break
            
            if language:
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = f.readlines()
                        line_count = len(lines)
                        
                        stats['total_files'] += 1
                        stats['total_lines'] += line_count
                        stats['by_language'][language]['files'] += 1
                        stats['by_language'][language]['lines'] += line_count
                        
                        stats['file_list'].append({
                            'path': str(file_path),
                            'language': language,
                            'lines': line_count
                        })
                        
                except Exception as e:
                    print(f"读取文件时出错 {file_path}: {e}")
                    continue
    
    return stats

def format_stats(stats):
    """格式化统计结果"""
    output = []
    
    output.append(f"- 总文件数：{stats['total_files']}")
    output.append(f"- 总代码行数：{stats['total_lines']}")
    output.append("- 文件类型分布：")
    
    for lang, data in stats['by_language'].items():
        if data['files'] > 0:
            output.append(f"  - {lang}: {data['files']}个文件，{data['lines']}行")
    
    return '\n'.join(output)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python code_monitor.py <目录路径>")
        sys.exit(1)
    
    directory_path = sys.argv[1]
    print(f"正在统计目录: {directory_path}")
    
    stats = count_code_lines(directory_path)
    
    print("\n=== 代码统计结果 ===")
    print(format_stats(stats))
    
    # 保存详细结果到文件
    output_file = "code_stats_detailed.json"
    import json
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(stats, f, ensure_ascii=False, indent=2)
    
    print(f"\n详细统计已保存到: {output_file}")