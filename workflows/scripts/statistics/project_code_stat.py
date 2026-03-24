#!/usr/bin/env python3
"""
项目代码统计脚本 - 专门统计 projects 目录下的代码
"""

import os
import json
from pathlib import Path

def analyze_project_structure(base_path="projects"):
    """分析项目结构并统计代码"""
    
    if not os.path.exists(base_path):
        print(f"错误: 目录 '{base_path}' 不存在")
        return None
    
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
    
    projects_stats = {}
    total_summary = {'files': 0, 'lines': 0}
    
    print("正在分析项目结构...")
    print("=" * 70)
    
    # 遍历每个项目
    for project_dir in os.listdir(base_path):
        project_path = os.path.join(base_path, project_dir)
        
        if not os.path.isdir(project_path):
            continue
        
        print(f"\n项目: {project_dir}")
        print("-" * 40)
        
        # 项目内统计
        project_stats = {}
        project_total_files = 0
        project_total_lines = 0
        
        for root, dirs, files in os.walk(project_path):
            # 忽略一些目录
            ignore_dirs = ['.git', '__pycache__', 'node_modules', 'target', 'build', 'dist']
            dirs[:] = [d for d in dirs if d not in ignore_dirs]
            
            for file in files:
                ext = os.path.splitext(file)[1].lower()
                file_type = file_types.get(ext, 'Other')
                
                if file_type not in project_stats:
                    project_stats[file_type] = {'count': 0, 'lines': 0}
                
                file_path = os.path.join(root, file)
                
                try:
                    # 统计行数
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        line_count = sum(1 for _ in f)
                    
                    project_stats[file_type]['count'] += 1
                    project_stats[file_type]['lines'] += line_count
                    
                    project_total_files += 1
                    project_total_lines += line_count
                    
                except (IOError, OSError, UnicodeDecodeError):
                    continue
        
        # 记录项目统计
        projects_stats[project_dir] = {
            'stats': project_stats,
            'total_files': project_total_files,
            'total_lines': project_total_lines,
            'path': project_path
        }
        
        # 更新总计
        total_summary['files'] += project_total_files
        total_summary['lines'] += project_total_lines
        
        # 打印项目统计
        print(f"  文件数: {project_total_files:,}")
        print(f"  代码行数: {project_total_lines:,}")
        
        # 打印前3种文件类型
        sorted_types = sorted(
            project_stats.items(),
            key=lambda x: x[1]['lines'],
            reverse=True
        )[:3]
        
        for file_type, data in sorted_types:
            print(f"  {file_type}: {data['count']}文件, {data['lines']:,}行")
    
    print("\n" + "=" * 70)
    print("项目代码统计总结:")
    print("-" * 70)
    print(f"项目总数: {len(projects_stats)}")
    print(f"总文件数: {total_summary['files']:,}")
    print(f"总代码行数: {total_summary['lines']:,}")
    print()
    
    # 按项目行数排序
    sorted_projects = sorted(
        projects_stats.items(),
        key=lambda x: x[1]['total_lines'],
        reverse=True
    )
    
    print("项目详情（按代码行数排序）:")
    print("-" * 70)
    print(f"{'项目名称':<25} {'文件数':<10} {'代码行数':<12} {'占比':<8}")
    print("-" * 70)
    
    for project_name, data in sorted_projects:
        files = data['total_files']
        lines = data['total_lines']
        percentage = (lines / total_summary['lines']) * 100 if total_summary['lines'] > 0 else 0
        
        print(f"{project_name:<25} {files:<10,} {lines:<12,} {percentage:>6.1f}%")
    
    print("-" * 70)
    
    # 按文件类型统计所有项目
    all_type_stats = {}
    for project_data in projects_stats.values():
        for file_type, type_data in project_data['stats'].items():
            if file_type not in all_type_stats:
                all_type_stats[file_type] = {'count': 0, 'lines': 0}
            
            all_type_stats[file_type]['count'] += type_data['count']
            all_type_stats[file_type]['lines'] += type_data['lines']
    
    print("\n所有项目文件类型统计:")
    print("-" * 70)
    print(f"{'文件类型':<20} {'文件数':<10} {'代码行数':<12} {'占比':<8}")
    print("-" * 70)
    
    sorted_all_types = sorted(
        all_type_stats.items(),
        key=lambda x: x[1]['lines'],
        reverse=True
    )
    
    for file_type, data in sorted_all_types:
        count = data['count']
        lines = data['lines']
        percentage = (lines / total_summary['lines']) * 100 if total_summary['lines'] > 0 else 0
        
        print(f"{file_type:<20} {count:<10,} {lines:<12,} {percentage:>6.1f}%")
    
    print("-" * 70)
    
    return {
        'projects': projects_stats,
        'summary': total_summary,
        'type_stats': all_type_stats
    }

def main():
    """主函数"""
    result = analyze_project_structure()
    
    if result:
        # 保存为JSON
        with open('project_statistics.json', 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=2)
        
        print(f"\n详细统计已保存到: project_statistics.json")
        
        # 创建简化的文本报告
        report_lines = [
            "项目代码统计报告",
            "=" * 50,
            f"统计时间: 2026-03-24",
            f"项目总数: {len(result['projects'])}",
            f"总文件数: {result['summary']['files']:,}",
            f"总代码行数: {result['summary']['lines']:,}",
            "",
            "各项目统计:",
            "-" * 50,
        ]
        
        sorted_projects = sorted(
            result['projects'].items(),
            key=lambda x: x[1]['total_lines'],
            reverse=True
        )
        
        for project_name, data in sorted_projects:
            report_lines.append(
                f"{project_name}: {data['total_files']:,}文件, {data['total_lines']:,}行"
            )
        
        report_lines.extend([
            "",
            "文件类型统计:",
            "-" * 50,
        ])
        
        sorted_types = sorted(
            result['type_stats'].items(),
            key=lambda x: x[1]['lines'],
            reverse=True
        )
        
        for file_type, data in sorted_types[:10]:  # 只显示前10种
            report_lines.append(
                f"{file_type}: {data['count']:,}文件, {data['lines']:,}行"
            )
        
        report_text = "\n".join(report_lines)
        
        with open('project_statistics.txt', 'w', encoding='utf-8') as f:
            f.write(report_text)
        
        print(f"简洁报告已保存到: project_statistics.txt")
        
        # 显示报告内容
        print("\n" + "=" * 50)
        print("简洁报告内容:")
        print("=" * 50)
        print(report_text)

if __name__ == "__main__":
    main()