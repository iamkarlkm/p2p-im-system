#!/usr/bin/env python3
"""
工作空间分析脚本 - 全面分析整个工作空间
"""

import os
import json
from datetime import datetime
import sys

def analyze_workspace(root_path="."):
    """分析整个工作空间"""
    
    root_path = os.path.abspath(root_path)
    
    print("[TARGET] 工作空间分析报告")
    print("=" * 80)
    print(f"分析目录: {root_path}")
    print(f"分析时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # 1. 目录结构分析
    print("[FOLDER] 目录结构分析")
    print("-" * 80)
    
    dir_stats = {}
    file_stats = {}
    total_dirs = 0
    total_files = 0
    
    # 遍历工作空间
    for root, dirs, files in os.walk(root_path):
        # 计算目录深度
        depth = root[len(root_path):].count(os.sep)
        
        # 忽略隐藏目录和虚拟环境
        ignore_patterns = ['.git', '__pycache__', '.idea', '.vscode', 'node_modules', 
                          'target', 'build', 'dist', 'venv', '.venv', 'env']
        dirs[:] = [d for d in dirs if not any(ignore in d for ignore in ignore_patterns)]
        
        # 统计目录
        if root not in dir_stats:
            dir_stats[root] = len(dirs) + len(files)
            total_dirs += 1
        
        # 统计文件
        for file in files:
            total_files += 1
            ext = os.path.splitext(file)[1].lower()
            file_stats[ext] = file_stats.get(ext, 0) + 1
    
    print(f"总目录数: {total_dirs:,}")
    print(f"总文件数: {total_files:,}")
    
    # 显示前10种文件扩展名
    sorted_exts = sorted(file_stats.items(), key=lambda x: x[1], reverse=True)[:10]
    print("\n[FILE] 主要文件类型:")
    for ext, count in sorted_exts:
        percentage = (count / total_files) * 100 if total_files > 0 else 0
        ext_display = ext if ext else "无扩展名"
        print(f"  {ext_display:<10} {count:<6,} 文件 ({percentage:>5.1f}%)")
    
    print()
    
    # 2. 代码分析
    print("[CODE] 代码文件分析")
    print("-" * 80)
    
    # 代码文件类型定义
    code_extensions = {
        '.java': 'Java',
        '.js': 'JavaScript',
        '.ts': 'TypeScript',
        '.jsx': 'JSX',
        '.tsx': 'TSX',
        '.py': 'Python',
        '.dart': 'Dart',
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
    }
    
    markup_extensions = {
        '.html': 'HTML',
        '.css': 'CSS',
        '.md': 'Markdown',
        '.xml': 'XML',
        '.json': 'JSON',
        '.yml': 'YAML',
        '.yaml': 'YAML',
    }
    
    script_extensions = {
        '.sh': 'Shell Script',
        '.ps1': 'PowerShell',
        '.bat': 'Batch',
    }
    
    code_stats = {}
    total_code_lines = 0
    total_code_files = 0
    
    # 统计代码文件
    for root, dirs, files in os.walk(root_path):
        # 继续忽略隐藏目录
        dirs[:] = [d for d in dirs if not d.startswith('.')]
        
        for file in files:
            ext = os.path.splitext(file)[1].lower()
            
            # 检查是否为代码文件
            file_type = None
            if ext in code_extensions:
                file_type = code_extensions[ext]
            elif ext in markup_extensions:
                file_type = markup_extensions[ext]
            elif ext in script_extensions:
                file_type = script_extensions[ext]
            
            if file_type:
                file_path = os.path.join(root, file)
                
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        line_count = sum(1 for _ in f)
                except:
                    line_count = 0
                
                # 更新统计
                if file_type not in code_stats:
                    code_stats[file_type] = {'files': 0, 'lines': 0, 'avg_lines': 0}
                
                code_stats[file_type]['files'] += 1
                code_stats[file_type]['lines'] += line_count
                
                total_code_files += 1
                total_code_lines += line_count
    
    # 计算平均行数
    for file_type in code_stats:
        if code_stats[file_type]['files'] > 0:
            code_stats[file_type]['avg_lines'] = code_stats[file_type]['lines'] / code_stats[file_type]['files']
    
    # 显示代码统计
    print(f"代码文件总数: {total_code_files:,}")
    print(f"总代码行数: {total_code_lines:,}")
    
    if total_code_files > 0:
        avg_lines_per_file = total_code_lines / total_code_files
        print(f"平均每文件行数: {avg_lines_per_file:,.1f}")
    
    # 按行数排序显示
    print("\n[CHART] 代码类型分布:")
    sorted_code_stats = sorted(
        code_stats.items(),
        key=lambda x: x[1]['lines'],
        reverse=True
    )
    
    for file_type, stats in sorted_code_stats[:15]:  # 显示前15种
        percentage = (stats['lines'] / total_code_lines) * 100 if total_code_lines > 0 else 0
        print(f"  {file_type:<18} {stats['files']:>4,} 文件, {stats['lines']:>8,} 行 ({percentage:>5.1f}%)")
    
    print()
    
    # 3. 项目分析
    print("[PACKAGE] 项目分析")
    print("-" * 80)
    
    projects_dir = os.path.join(root_path, "projects")
    if os.path.exists(projects_dir):
        projects = []
        for item in os.listdir(projects_dir):
            item_path = os.path.join(projects_dir, item)
            if os.path.isdir(item_path):
                projects.append(item)
        
        print(f"发现项目数: {len(projects)}")
        if projects:
            print(f"项目列表: {', '.join(projects)}")
            
            # 分析每个项目
            print("\n[MAGNIFY] 各项目详情:")
            for project in projects:
                project_path = os.path.join(projects_dir, project)
                
                # 统计项目文件
                project_files = 0
                project_lines = 0
                
                for root, dirs, files in os.walk(project_path):
                    dirs[:] = [d for d in dirs if not d.startswith('.')]
                    
                    for file in files:
                        ext = os.path.splitext(file)[1].lower()
                        
                        # 只统计特定文件类型
                        if ext in code_extensions or ext in markup_extensions or ext in script_extensions:
                            project_files += 1
                            
                            try:
                                file_path = os.path.join(root, file)
                                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                                    project_lines += sum(1 for _ in f)
                            except:
                                pass
                
                print(f"  {project:<20} {project_files:>4} 文件, {project_lines:>6,} 行")
    else:
        print("未发现 projects 目录")
    
    print()
    
    # 4. 生成报告
    print("[CHART_UP] 总结")
    print("-" * 80)
    
    # 计算代码密度
    if total_files > 0:
        code_density = (total_code_files / total_files) * 100
        print(f"代码文件占比: {code_density:.1f}%")
    
    # 计算平均行数
    if total_code_files > 0:
        print(f"平均代码文件大小: {total_code_lines / total_code_files:.1f} 行")
    
    # 识别主要技术栈
    if code_stats:
        top_languages = sorted(code_stats.items(), key=lambda x: x[1]['lines'], reverse=True)[:3]
        print(f"主要技术栈: {', '.join([lang for lang, _ in top_languages])}")
    
    print()
    
    # 保存结果
    result = {
        "timestamp": datetime.now().isoformat(),
        "root_path": root_path,
        "summary": {
            "total_directories": total_dirs,
            "total_files": total_files,
            "total_code_files": total_code_files,
            "total_code_lines": total_code_lines,
        },
        "file_types": dict(sorted_exts),
        "code_statistics": {
            file_type: {
                "files": stats["files"],
                "lines": stats["lines"],
                "avg_lines": stats["avg_lines"]
            } for file_type, stats in code_stats.items()
        },
        "projects": projects if 'projects' in locals() else []
    }
    
    # 保存为JSON
    output_file = "workspace_analysis.json"
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    
    print(f"[CHECK] 详细分析已保存到: {output_file}")
    
    # 创建简洁的文本报告
    text_report = [
        "=" * 60,
        "工作空间分析报告",
        "=" * 60,
        f"目录: {os.path.basename(root_path)}",
        f"时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
        "",
        "[CHART] 总体统计",
        f"总目录数: {total_dirs:,}",
        f"总文件数: {total_files:,}",
        f"代码文件数: {total_code_files:,}",
        f"代码总行数: {total_code_lines:,}",
        "",
        "[CODE] 主要技术栈",
    ]
    
    for file_type, stats in sorted_code_stats[:5]:
        text_report.append(f"- {file_type}: {stats['files']:,}文件, {stats['lines']:,}行")
    
    text_report.extend([
        "",
        "[PACKAGE] 项目",
        f"发现项目数: {len(projects) if 'projects' in locals() else 0}",
    ])
    
    if 'projects' in locals() and projects:
        for project in projects:
            text_report.append(f"- {project}")
    
    text_report.append("=" * 60)
    
    text_output = "\n".join(text_report)
    
    text_file = "workspace_analysis.txt"
    with open(text_file, 'w', encoding='utf-8') as f:
        f.write(text_output)
    
    print(f"[CHECK] 简洁报告已保存到: {text_file}")
    
    # 显示简洁报告
    print("\n" + "=" * 60)
    print(text_output)
    
    return result

def main():
    """主函数"""
    # 检查命令行参数
    if len(sys.argv) > 1:
        target_dir = sys.argv[1]
    else:
        target_dir = "."
    
    if not os.path.exists(target_dir):
        print(f"错误: 目录 '{target_dir}' 不存在")
        return
    
    analyze_workspace(target_dir)

if __name__ == "__main__":
    main()