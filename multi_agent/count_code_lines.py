import os
import sys
from pathlib import Path

def count_code_files_and_lines(projects_path):
    """统计代码文件数量和行数"""
    
    # 定义文件类型映射
    file_types = {
        'Java': ['.java'],
        'JavaScript': ['.js', '.ts', '.tsx', '.jsx'],
        'Dart': ['.dart'],
        'Rust': ['.rs'],
        'HTML/CSS': ['.html', '.css']
    }
    
    stats = {}
    for file_type, extensions in file_types.items():
        stats[file_type] = {'files': 0, 'lines': 0}
    
    total_files = 0
    total_lines = 0
    
    # 遍历所有文件
    for root, dirs, files in os.walk(projects_path):
        for file in files:
            file_path = os.path.join(root, file)
            ext = os.path.splitext(file)[1].lower()
            
            # 确定文件类型
            file_type = None
            for ft, exts in file_types.items():
                if ext in exts:
                    file_type = ft
                    break
            
            if file_type:
                # 统计行数
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = sum(1 for _ in f)
                except:
                    try:
                        with open(file_path, 'r', encoding='gbk', errors='ignore') as f:
                            lines = sum(1 for _ in f)
                    except:
                        lines = 0
                
                stats[file_type]['files'] += 1
                stats[file_type]['lines'] += lines
                total_files += 1
                total_lines += lines
    
    return total_files, total_lines, stats

if __name__ == '__main__':
    projects_path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
    
    if not os.path.exists(projects_path):
        print(f"路径不存在: {projects_path}")
        sys.exit(1)
    
    total_files, total_lines, stats = count_code_files_and_lines(projects_path)
    
    print(f"总文件数: {total_files}")
    print(f"总代码行数: {total_lines}")
    print("\n文件类型分布:")
    
    for file_type in ['Java', 'JavaScript', 'Dart', 'Rust', 'HTML/CSS']:
        if file_type in stats:
            files = stats[file_type]['files']
            lines = stats[file_type]['lines']
            print(f"  {file_type}: {files}个文件 ({lines}行)")