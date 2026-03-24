#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
代码统计脚本 - 统一口径统计所有代码文件
"""

import os
import json
from datetime import datetime
from pathlib import Path
import sys

# 定义要统计的文件类型
CODE_EXTENSIONS = {
    'Java': ['.java'],
    'JavaScript/TypeScript': ['.js', '.ts', '.tsx', '.jsx'],
    'Dart': ['.dart'],
    'Rust': ['.rs'],
    'HTML': ['.html'],
    'CSS': ['.css']
}

def count_lines(file_path):
    """统计文件行数"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return sum(1 for _ in f)
    except UnicodeDecodeError:
        try:
            with open(file_path, 'r', encoding='gbk') as f:
                return sum(1 for _ in f)
        except:
            return 0
    except Exception as e:
        return 0

def get_file_extension(filename):
    """获取文件扩展名"""
    return os.path.splitext(filename)[1].lower()

def categorize_file(file_path):
    """将文件归类到相应的语言类别"""
    ext = get_file_extension(file_path)
    for lang, exts in CODE_EXTENSIONS.items():
        if ext in exts:
            return lang
    return None

def scan_directory(root_dir):
    """扫描目录并统计代码"""
    stats = {
        'total_files': 0,
        'total_lines': 0,
        'by_language': {},
        'by_project': {},
        'files': []
    }
    
    # 初始化语言统计
    for lang in CODE_EXTENSIONS.keys():
        stats['by_language'][lang] = {'files': 0, 'lines': 0}
    
    # 递归扫描目录
    for root, dirs, files in os.walk(root_dir):
        # 获取相对路径用于项目分类
        rel_path = os.path.relpath(root, root_dir)
        if rel_path == '.':
            project_name = 'root'
        else:
            project_name = rel_path.split(os.sep)[0] if os.sep in rel_path else rel_path
        
        if project_name not in stats['by_project']:
            stats['by_project'][project_name] = {'files': 0, 'lines': 0}
        
        for file in files:
            file_path = os.path.join(root, file)
            lang = categorize_file(file)
            
            if lang:
                # 统计文件
                lines = count_lines(file_path)
                stats['total_files'] += 1
                stats['total_lines'] += lines
                stats['by_language'][lang]['files'] += 1
                stats['by_language'][lang]['lines'] += lines
                stats['by_project'][project_name]['files'] += 1
                stats['by_project'][project_name]['lines'] += lines
                
                stats['files'].append({
                    'path': file_path,
                    'language': lang,
                    'lines': lines
                })
    
    return stats

def main():
    # 设置监控目录
    project_dir = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
    knowledge_dir = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
    
    print(f"正在扫描项目目录: {project_dir}")
    
    # 检查目录是否存在
    if not os.path.exists(project_dir):
        print(f"错误: 项目目录不存在 - {project_dir}")
        return
    
    # 统计代码量
    stats = scan_directory(project_dir)
    
    # 输出统计结果
    print(f"\n=== 代码量统计 ===")
    print(f"总文件数: {stats['total_files']}")
    print(f"总代码行数: {stats['total_lines']}")
    print(f"\n文件类型分布:")
    for lang, data in stats['by_language'].items():
        if data['files'] > 0:
            print(f"  - {lang}: {data['files']}个文件, {data['lines']}行")
    
    print(f"\n项目分布:")
    for project, data in stats['by_project'].items():
        if data['files'] > 0:
            print(f"  - {project}: {data['files']}个文件, {data['lines']}行")
    
    # 保存统计结果到JSON文件
    output_file = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_stats.json"
    os.makedirs(os.path.dirname(output_file), exist_ok=True)
    
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump({
            'timestamp': datetime.now().isoformat(),
            'stats': stats
        }, f, ensure_ascii=False, indent=2)
    
    print(f"\n统计结果已保存到: {output_file}")
    
    # 返回统计结果
    return stats

if __name__ == "__main__":
    main()