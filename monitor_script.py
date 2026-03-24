import os
import glob
from datetime import datetime
import json

def count_lines_in_file(filepath):
    """统计文件行数"""
    try:
        with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
            return len(f.readlines())
    except:
        try:
            with open(filepath, 'r', encoding='gbk', errors='ignore') as f:
                return len(f.readlines())
        except:
            return 0

def count_code_volume(project_path):
    """统计代码量"""
    file_types = {
        'Java': ['*.java'],
        'JavaScript/TypeScript': ['*.js', '*.ts', '*.tsx', '*.jsx'],
        'Dart': ['*.dart'],
        'Rust': ['*.rs'],
        'HTML': ['*.html', '*.htm'],
        'CSS': ['*.css', '*.scss', '*.less']
    }
    
    stats = {
        'total_files': 0,
        'total_lines': 0,
        'file_distribution': {},
        'line_distribution': {}
    }
    
    for lang, patterns in file_types.items():
        lang_files = 0
        lang_lines = 0
        
        for pattern in patterns:
            search_pattern = os.path.join(project_path, '**', pattern)
            files = glob.glob(search_pattern, recursive=True)
            
            for file in files:
                lang_files += 1
                lines = count_lines_in_file(file)
                lang_lines += lines
        
        if lang_files > 0:
            stats['file_distribution'][lang] = lang_files
            stats['line_distribution'][lang] = lang_lines
            stats['total_files'] += lang_files
            stats['total_lines'] += lang_lines
    
    return stats

def check_architecture_updates(project_path):
    """检查架构设计更新"""
    roadmap_path = os.path.join(project_path, 'roadmap.md')
    
    if os.path.exists(roadmap_path):
        try:
            with open(roadmap_path, 'r', encoding='utf-8') as f:
                content = f.read()
                # 简单检查是否有更新（这里可以扩展为更复杂的检查）
                lines = content.split('\n')
                planning_lines = [line for line in lines if line.strip().startswith('-') or line.strip().startswith('*')]
                return {
                    'exists': True,
                    'planning_count': len(planning_lines),
                    'new_features': []  # 这里可以扩展为检测新功能点
                }
        except:
            return {'exists': False}
    return {'exists': False}

def check_knowledge_base(knowledge_path):
    """检查知识库情况"""
    if not os.path.exists(knowledge_path):
        return {'exists': False, 'new_files': 0, 'knowledge_points': []}
    
    # 获取所有文件
    all_files = []
    for root, dirs, files in os.walk(knowledge_path):
        for file in files:
            if file.endswith('.md') or file.endswith('.txt'):
                all_files.append(os.path.join(root, file))
    
    # 这里可以扩展为检查新增文件（需要历史记录）
    return {
        'exists': True,
        'total_files': len(all_files),
        'new_files': 0,  # 需要历史记录才能准确计算新增
        'knowledge_points': []  # 这里可以扩展为提取知识点
    }

def check_development_plan(project_path):
    """检查开发计划"""
    plan_path = os.path.join(project_path, 'development_plan.md')
    
    if not os.path.exists(plan_path):
        return {'exists': False}
    
    try:
        with open(plan_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        status_counts = {
            'completed': 0,
            'in_progress': 0,
            'pending': 0,
            'manual_resolve': 0
        }
        
        features = []
        lines = content.split('\n')
        
        for line in lines:
            line = line.strip()
            if '✅' in line:
                status_counts['completed'] += 1
                features.append({'name': line.replace('✅', '').strip(), 'status': '✅ 已完成'})
            elif '🔄' in line:
                status_counts['in_progress'] += 1
                features.append({'name': line.replace('🔄', '').strip(), 'status': '🔄 开发中'})
            elif '📋' in line:
                status_counts['pending'] += 1
                features.append({'name': line.replace('📋', '').strip(), 'status': '📋 待开发'})
            elif '⚠️' in line:
                status_counts['manual_resolve'] += 1
                features.append({'name': line.replace('⚠️', '').strip(), 'status': '⚠️ 待人工解决'})
        
        return {
            'exists': True,
            'features': features,
            'status_counts': status_counts
        }
    except:
        return {'exists': False}

def main():
    # 路径定义
    project_path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects'
    knowledge_path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge'
    log_path = r'C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs\code_volume_monitor.md'
    
    # 创建日志目录
    log_dir = os.path.dirname(log_path)
    os.makedirs(log_dir, exist_ok=True)
    
    # 执行监控
    current_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    
    print(f"开始监控任务 - {current_time}")
    print(f"项目路径: {project_path}")
    print(f"知识库路径: {knowledge_path}")
    
    # 1. 代码量监控
    print("正在统计代码量...")
    code_stats = count_code_volume(project_path)
    
    # 2. 架构设计情况
    print("正在检查架构设计...")
    arch_stats = check_architecture_updates(project_path)
    
    # 3. 学习情况
    print("正在检查知识库...")
    knowledge_stats = check_knowledge_base(knowledge_path)
    
    # 4. 开发功能列表
    print("正在检查开发计划...")
    dev_plan_stats = check_development_plan(project_path)
    
    # 生成报告
    report = f"""## 综合监控报告 - {current_time}

### 代码量统计（统一口径）
- 总文件数：{code_stats['total_files']}
- 总代码行数：{code_stats['total_lines']}
- 文件类型分布："""

    for lang, count in code_stats['file_distribution'].items():
        report += f"\n  - {lang}: {count}个文件 ({code_stats['line_distribution'].get(lang, 0)}行)"

    report += f"""

### 架构设计情况"""
    
    if arch_stats['exists']:
        report += f"""
- 规划中功能：{arch_stats['planning_count']}个
- 新增功能点：{', '.join(arch_stats['new_features']) if arch_stats['new_features'] else '无'}"""
    else:
        report += "\n- roadmap.md 文件未找到"

    report += f"""

### 学习情况"""
    
    if knowledge_stats['exists']:
        report += f"""
- 知识库文件总数：{knowledge_stats['total_files']}个
- 新增知识文件：{knowledge_stats['new_files']}个
- 新知识点：{', '.join(knowledge_stats['knowledge_points']) if knowledge_stats['knowledge_points'] else '无'}"""
    else:
        report += "\n- 知识库目录未找到"

    report += f"""

### 开发功能列表"""
    
    if dev_plan_stats['exists']:
        report += "\n| 功能名称 | 状态 |"
        report += "\n|----------|------|"
        
        for feature in dev_plan_stats['features']:
            report += f"\n| {feature['name'][:30]}... | {feature['status']} |"
        
        counts = dev_plan_stats['status_counts']
        report += f"""

状态统计：
- ✅ 已完成：{counts['completed']}个
- 🔄 开发中：{counts['in_progress']}个
- 📋 待开发：{counts['pending']}个
  - ⚠️ 待人工解决：{counts['manual_resolve']}个"""
    else:
        report += "\n- development_plan.md 文件未找到"

    report += f"""

### 总结
- 代码量变化：需要历史记录进行比较
- 功能模块变化：需要历史记录进行比较
- 知识库变化：需要历史记录进行比较

---
*监控执行时间：{current_time}*
*监控路径：{project_path}*"""

    # 保存报告
    with open(log_path, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"监控报告已保存到: {log_path}")
    
    # 返回报告内容
    return report

if __name__ == '__main__':
    result = main()
    print("\n" + "="*50)
    print("监控报告内容：")
    print("="*50)
    print(result)