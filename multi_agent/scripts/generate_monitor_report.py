#!/usr/bin/env python3
"""
代码量统计脚本
统计 C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\ 目录下所有代码文件
"""

import os
import sys
from pathlib import Path
from collections import defaultdict
from datetime import datetime

# 目标目录
PROJECTS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "projects")
KNOWLEDGE_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "knowledge")

# 需要统计的文件类型
CODE_EXTENSIONS = {
    'Java': ['.java'],
    'JavaScript/TypeScript': ['.js', '.ts', '.tsx', '.jsx'],
    'Dart': ['.dart'],
    'Rust': ['.rs'],
    'HTML/CSS': ['.html', '.css']
}

def count_code_files(directory):
    """统计指定目录下的代码文件"""
    stats = {
        'total_files': 0,
        'total_lines': 0,
        'by_extension': defaultdict(int),
        'by_type': defaultdict(lambda: {'files': 0, 'lines': 0})
    }
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            file_path = os.path.join(root, file)
            _, ext = os.path.splitext(file)
            ext = ext.lower()
            
            # 检查文件类型
            file_type = None
            for type_name, extensions in CODE_EXTENSIONS.items():
                if ext in extensions:
                    file_type = type_name
                    break
            
            if file_type:
                stats['total_files'] += 1
                stats['by_extension'][ext] += 1
                stats['by_type'][file_type]['files'] += 1
                
                # 统计行数
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = sum(1 for line in f)
                        stats['total_lines'] += lines
                        stats['by_type'][file_type]['lines'] += lines
                except Exception as e:
                    print(f"Error reading {file_path}: {e}")
                    continue
    
    return stats

def get_knowledge_stats(directory):
    """获取知识库统计"""
    if not os.path.exists(directory):
        return {'total_files': 0, 'files': []}
    
    files = []
    for root, dirs, file_list in os.walk(directory):
        for file in file_list:
            if file.endswith('.md'):
                file_path = os.path.join(root, file)
                files.append(file_path)
    
    return {'total_files': len(files), 'files': files}

def get_roadmap_info():
    """获取路线图信息"""
    roadmap_path = os.path.join(PROJECTS_DIR, 'roadmap.md')
    if os.path.exists(roadmap_path):
        try:
            with open(roadmap_path, 'r', encoding='utf-8') as f:
                content = f.read()
                # 简单统计功能数量（统计标题数量）
                features = [line for line in content.split('\n') if line.strip().startswith('#')]
                return {
                    'exists': True,
                    'feature_count': len(features),
                    'content': content[:1000] + '...' if len(content) > 1000 else content
                }
        except Exception as e:
            return {'exists': False, 'error': str(e)}
    return {'exists': False}

def get_development_plan_status():
    """获取开发计划状态"""
    dev_plan_path = os.path.join(PROJECTS_DIR, 'development_plan.md')
    if not os.path.exists(dev_plan_path):
        return {'exists': False, 'features': []}
    
    try:
        with open(dev_plan_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        features = []
        lines = content.split('\n')
        for i, line in enumerate(lines):
            line = line.strip()
            if line:
                if '✅' in line:
                    status = '✅ 已完成'
                elif '🔄' in line:
                    status = '🔄 开发中'
                elif '📋' in line:
                    status = '📋 待开发'
                elif '⚠️' in line:
                    status = '⚠️ 待人工解决'
                else:
                    continue
                
                feature_name = line.replace('✅', '').replace('🔄', '').replace('📋', '').replace('⚠️', '').strip()
                if feature_name:
                    features.append({'name': feature_name, 'status': status})
        
        # 统计状态
        status_counts = {
            '✅ 已完成': 0,
            '🔄 开发中': 0,
            '📋 待开发': 0,
            '⚠️ 待人工解决': 0
        }
        
        for feature in features:
            if feature['status'] in status_counts:
                status_counts[feature['status']] += 1
        
        return {
            'exists': True,
            'features': features,
            'status_counts': status_counts
        }
    
    except Exception as e:
        return {'exists': False, 'error': str(e)}

def generate_report():
    """生成监控报告"""
    # 统计代码量
    code_stats = count_code_files(PROJECTS_DIR)
    
    # 获取知识库统计
    knowledge_stats = get_knowledge_stats(KNOWLEDGE_DIR)
    
    # 获取路线图信息
    roadmap_info = get_roadmap_info()
    
    # 获取开发计划状态
    dev_plan_status = get_development_plan_status()
    
    # 生成报告
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    report = f"""## 综合监控报告 - {current_time}

### 代码量统计（统一口径）
- 总文件数：{code_stats['total_files']}
- 总代码行数：{code_stats['total_lines']}
- 文件类型分布：
"""
    
    for file_type, type_stats in code_stats['by_type'].items():
        report += f"  - {file_type}: {type_stats['files']}个文件，{type_stats['lines']}行\n"
    
    report += f"""
### 架构设计情况
"""
    if roadmap_info['exists']:
        report += f"- 规划中功能：{roadmap_info['feature_count']}个\n"
        report += "- 最近更新：已检测到roadmap.md文件\n"
    else:
        report += "- 规划中功能：未找到roadmap.md文件\n"
    
    report += f"""
### 学习情况
- 知识库总文件数：{knowledge_stats['total_files']}个
- 最近新增文件：{min(len(knowledge_stats['files']), 3)}个文件已列出
"""
    if knowledge_stats['files']:
        for i, file_path in enumerate(knowledge_stats['files'][:3]):
            file_name = os.path.basename(file_path)
            report += f"  - {file_name}\n"
    
    report += f"""
### 开发功能列表
"""
    if dev_plan_status['exists'] and dev_plan_status['features']:
        report += "| 功能名称 | 状态 |\n|----------|------|\n"
        for feature in dev_plan_status['features'][:15]:  # 显示前15个功能
            report += f"| {feature['name']} | {feature['status']} |\n"
        
        counts = dev_plan_status['status_counts']
        report += f"""
状态统计：
- ✅ 已完成：{counts['✅ 已完成']}个
- 🔄 开发中：{counts['🔄 开发中']}个
- 📋 待开发：{counts['📋 待开发']}个
  - ⚠️ 待人工解决：{counts['⚠️ 待人工解决']}个
"""
    else:
        report += "- 未找到development_plan.md文件或文件为空\n"
    
    report += f"""
### 总结
- 代码量统计：{code_stats['total_files']}个文件，{code_stats['total_lines']}行代码
- 项目状态：{'开发活跃' if code_stats['total_files'] > 0 else '初始状态'}
- 知识库进度：{knowledge_stats['total_files']}个知识文件
- 功能完成度：{(counts['✅ 已完成'] / max(1, sum(counts.values()))) * 100:.1f}% 已完成
"""
    
    return report

if __name__ == "__main__":
    try:
        report = generate_report()
        
        # 保存报告
        log_dir = os.path.join(PROJECTS_DIR, "..", "logs")
        os.makedirs(log_dir, exist_ok=True)
        
        log_file = os.path.join(log_dir, "code_volume_monitor.md")
        with open(log_file, 'w', encoding='utf-8') as f:
            f.write(report)
        
        print("监控报告已生成：")
        print(report)
        
    except Exception as e:
        print(f"生成监控报告时出错：{e}")
        sys.exit(1)