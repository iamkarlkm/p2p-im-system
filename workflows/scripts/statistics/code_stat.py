#!/usr/bin/env python3
"""
简洁的代码统计脚本 - 统计整个工作空间的代码文件
"""

import os
import json
from pathlib import Path

def count_code_lines(directory):
    """统计指定目录下的代码文件"""
    # 文件类型映射
    file_types = {
        '.java': 'Java',
        '.js': 'JavaScript',
        '.ts': 'TypeScript',
        '.jsx': 'JavaScript React',
        '.tsx': 'TypeScript React',
        '.dart': 'Dart',
        '.py': 'Python',
        '.rs': 'Rust',
        '.go': 'Go',
        '.cpp': 'C++',
        '.c': 'C',
        '.cs': 'C#',
        '.php': 'PHP',
        '.rb': 'Ruby',
        '.swift': 'Swift',
        '.kt': 'Kotlin',
        '.scala': 'Scala',
        '.html': 'HTML',
        '.css': 'CSS',
        '.sql': 'SQL',
        '.sh': 'Shell Script',
        '.ps1': 'PowerShell',
        '.bat': 'Batch',
        '.md': 'Markdown',
        '.json': 'JSON',
        '.xml': 'XML',
        '.yml': 'YAML',
        '.yaml': 'YAML',
        '.txt': 'Text',
    }
    
    stats = {}
    total_files = 0
    total_lines = 0
    
    for root, dirs, files in os.walk(directory):
        # 忽略一些目录
        ignore_dirs = ['.git', '__pycache__', 'node_modules', 'target', 'build', 'dist', '.idea', '.vscode']
        dirs[:] = [d for d in dirs if d not in ignore_dirs]
        
        for file in files:
            ext = os.path.splitext(file)[1].lower()
            
            # 确定文件类型
            file_type = file_types.get(ext, 'Other')
            
            if file_type not in stats:
                stats[file_type] = {'count': 0, 'lines': 0, 'files': []}
            
            file_path = os.path.join(root, file)
            
            try:
                # 统计行数
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    line_count = sum(1 for _ in f)
                
                stats[file_type]['count'] += 1
                stats[file_type]['lines'] += line_count
                stats[file_type]['files'].append(file_path)
                
                total_files += 1
                total_lines += line_count
                
            except (IOError, OSError, UnicodeDecodeError):
                # 跳过无法读取的文件
                continue
    
    return {
        'total_files': total_files,
        'total_lines': total_lines,
        'stats': stats,
        'directory': os.path.abspath(directory)
    }

def print_statistics(result):
    """打印统计结果"""
    print("=" * 60)
    print("代码统计报告")
    print("=" * 60)
    print(f"统计目录: {result['directory']}")
    print(f"总文件数: {result['total_files']:,}")
    print(f"总代码行数: {result['total_lines']:,}")
    print()
    
    # 按行数排序
    sorted_stats = sorted(
        result['stats'].items(),
        key=lambda x: x[1]['lines'],
        reverse=True
    )
    
    print("文件类型统计:")
    print("-" * 60)
    print(f"{'类型':<20} {'文件数':<10} {'代码行数':<12} {'占比':<8}")
    print("-" * 60)
    
    for file_type, data in sorted_stats:
        count = data['count']
        lines = data['lines']
        percentage = (lines / result['total_lines']) * 100 if result['total_lines'] > 0 else 0
        
        print(f"{file_type:<20} {count:<10,} {lines:<12,} {percentage:>6.1f}%")
    
    print("-" * 60)
    
    # 打印前10大文件（如果有的话）
    all_files = []
    for file_type, data in result['stats'].items():
        for file_path in data['files']:
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    line_count = sum(1 for _ in f)
                all_files.append((file_path, line_count))
            except:
                continue
    
    if all_files:
        all_files.sort(key=lambda x: x[1], reverse=True)
        print("\n最大文件（按行数）:")
        print("-" * 60)
        for i, (file_path, line_count) in enumerate(all_files[:10], 1):
            rel_path = os.path.relpath(file_path, result['directory'])
            print(f"{i:2}. {rel_path:<60} {line_count:>6,}行")
    
    return result

def save_to_json(result, output_file="code_statistics.json"):
    """保存为JSON文件"""
    # 清理文件列表以避免输出过大
    clean_result = {
        'total_files': result['total_files'],
        'total_lines': result['total_lines'],
        'stats': {},
        'directory': result['directory'],
        'timestamp': os.path.getctime(__file__)
    }
    
    for file_type, data in result['stats'].items():
        clean_result['stats'][file_type] = {
            'count': data['count'],
            'lines': data['lines'],
            'file_count': len(data['files'])
        }
    
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(clean_result, f, ensure_ascii=False, indent=2)
    
    print(f"\nJSON结果已保存到: {output_file}")
    return clean_result

def main():
    """主函数"""
    import sys
    
    # 设置要统计的目录
    if len(sys.argv) > 1:
        target_dir = sys.argv[1]
    else:
        target_dir = os.getcwd()  # 当前工作目录
    
    if not os.path.exists(target_dir):
        print(f"错误: 目录不存在 - {target_dir}")
        sys.exit(1)
    
    print(f"正在统计目录: {target_dir}")
    print("这可能需要一些时间，请稍候...")
    
    # 执行统计
    result = count_code_lines(target_dir)
    
    # 打印结果
    print_statistics(result)
    
    # 保存为JSON
    save_to_json(result)
    
    return result

if __name__ == "__main__":
    main()