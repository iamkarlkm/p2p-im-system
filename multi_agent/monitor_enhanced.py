import os
import glob
import datetime
from pathlib import Path
import re

def count_code_lines(directory):
    """统计指定目录下所有代码文件的行数"""
    file_extensions = {
        'Java': ['.java'],
        'JavaScript': ['.js', '.ts', '.tsx', '.jsx'],
        'Dart': ['.dart'],
        'Rust': ['.rs'],
        'HTML/CSS': ['.html', '.css']
    }
    
    results = {
        'total_files': 0,
        'total_lines': 0,
        'by_type': {category: {'files': 0, 'lines': 0} for category in file_extensions}
    }
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            file_path = os.path.join(root, file)
            ext = os.path.splitext(file)[1].lower()
            
            # 检查文件类型
            found_type = None
            for category, exts in file_extensions.items():
                if ext in exts:
                    found_type = category
                    break
            
            if found_type:
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = f.readlines()
                        line_count = len(lines)
                        
                        results['total_files'] += 1
                        results['total_lines'] += line_count
                        results['by_type'][found_type]['files'] += 1
                        results['by_type'][found_type]['lines'] += line_count
                except Exception as e:
                    print(f"无法读取文件 {file_path}: {e}")
    
    return results

def check_roadmap(roadmap_path):
    """检查roadmap.md更新情况"""
    try:
        with open(roadmap_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # 简单分析roadmap内容
        planning_count = 0
        new_features = []
        
        # 查找规划中的功能
        lines = content.split('\n')
        in_planning_section = False
        
        for line in lines:
            if line.startswith('## ') and ('规划中' in line or '计划中' in line or '后续规划' in line):
                in_planning_section = True
                continue
            elif line.startswith('## ') and in_planning_section:
                break
                
            if in_planning_section and line.strip():
                if line.startswith('### ') or line.startswith('- '):
                    new_features.append(line.strip())
                    planning_count += 1
        
        return {
            'has_update': True,
            'planning_count': planning_count,
            'new_features': new_features[:10]  # 最多显示10个
        }
    except FileNotFoundError:
        return {'has_update': False, 'planning_count': 0, 'new_features': []}

def check_knowledge(knowledge_dir):
    """检查知识库更新"""
    try:
        # 获取知识库文件列表
        knowledge_files = []
        for root, dirs, files in os.walk(knowledge_dir):
            for file in files:
                if file.endswith('.md'):
                    file_path = os.path.join(root, file)
                    knowledge_files.append({
                        'name': file,
                        'path': file_path,
                        'ctime': os.path.getctime(file_path)
                    })
        
        # 按创建时间排序，找到最新的文件
        knowledge_files.sort(key=lambda x: x['ctime'], reverse=True)
        
        new_files = []
        new_knowledge = []
        
        # 假设最近24小时内创建的文件为新增文件
        cutoff_time = datetime.datetime.now().timestamp() - 24*3600
        for kf in knowledge_files:
            if kf['ctime'] > cutoff_time:
                new_files.append(kf['name'])
                try:
                    with open(kf['path'], 'r', encoding='utf-8') as f:
                        content = f.read()
                        # 提取标题作为知识点
                        title = content.split('\n')[0] if content else kf['name']
                        if title.startswith('#'):
                            title = title.lstrip('#').strip()
                        new_knowledge.append(f"{kf['name']}: {title[:100]}")
                except:
                    new_knowledge.append(f"{kf['name']}: [文件内容无法读取]")
        
        return {
            'total_files': len(knowledge_files),
            'new_files': new_files,
            'new_knowledge': new_knowledge
        }
    except Exception as e:
        return {'total_files': 0, 'new_files': [], 'new_knowledge': [], 'error': str(e)}

def check_development_plan(plan_path):
    """读取开发计划并统计功能状态"""
    try:
        with open(plan_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 分析功能状态 - 使用更精确的匹配
        status_counts = {
            '✅ 已完成': 0,
            '🔄 开发中': 0,
            '📋 待开发': 0,
            '⚠️ 待人工解决': 0
        }
        
        features = []
        
        # 使用正则表达式查找功能块
        # 匹配功能编号和状态
        pattern = r'### (\d+)\.\s+(.*?)\s+([✅🔴🟡🟢🔄📋⚠️]+.*?)(?=\n### \d+\.|\n---|\n\*\*|\Z)'
        matches = re.findall(pattern, content, re.DOTALL)
        
        for match in matches:
            func_num = match[0]
            func_name = match[1].strip()
            func_content = match[2]
            
            # 确定状态
            status = '📋 待开发'  # 默认
            
            if '✅' in func_content or '已完成' in func_content:
                status = '✅ 已完成'
                status_counts['✅ 已完成'] += 1
            elif '🔄' in func_content or '开发中' in func_content:
                status = '🔄 开发中'
                status_counts['🔄 开发中'] += 1
            elif '📋' in func_content or '待开发' in func_content:
                status = '📋 待开发'
                status_counts['📋 待开发'] += 1
            elif '⚠️' in func_content or '待人工解决' in func_content:
                status = '⚠️ 待人工解决'
                status_counts['⚠️ 待人工解决'] += 1
            elif '已完成' in func_content or '完成' in func_content:
                status = '✅ 已完成'
                status_counts['✅ 已完成'] += 1
            
            features.append((f"#{func_num} {func_name}", status))
        
        # 如果正则没找到，尝试查找功能统计
        if not features:
            # 查找已完成功能统计
            pattern = r'总已完成功能:\s*(\d+)'
            match = re.search(pattern, content)
            if match:
                total_done = int(match.group(1))
                status_counts['✅ 已完成'] = total_done
            
            # 查找待开发功能统计
            pattern = r'待开发功能:\s*(\d+)'
            match = re.search(pattern, content)
            if match:
                total_todo = int(match.group(1))
                status_counts['📋 待开发'] = total_todo
        
        return {
            'features': features,
            'status_counts': status_counts
        }
    except FileNotFoundError:
        return {'features': [], 'status_counts': status_counts}
    except Exception as e:
        return {'features': [], 'status_counts': status_counts, 'error': str(e)}

def main():
    # 目录配置
    base_dir = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent"
    projects_dir = os.path.join(base_dir, "projects")
    knowledge_dir = os.path.join(base_dir, "knowledge")
    roadmap_path = os.path.join(projects_dir, "roadmap.md")
    development_plan_path = os.path.join(projects_dir, "development_plan.md")
    log_path = os.path.join(base_dir, "logs", "code_volume_monitor.md")
    
    print(f"开始监控任务...")
    print(f"项目目录: {projects_dir}")
    print(f"知识库目录: {knowledge_dir}")
    
    # 1. 统计代码量
    print("统计代码量...")
    code_stats = count_code_lines(projects_dir)
    
    # 2. 检查架构设计
    print("检查架构设计...")
    roadmap_info = check_roadmap(roadmap_path)
    
    # 3. 检查学习情况
    print("检查学习情况...")
    knowledge_info = check_knowledge(knowledge_dir)
    
    # 4. 检查开发功能列表
    print("检查开发功能列表...")
    dev_plan_info = check_development_plan(development_plan_path)
    
    # 5. 生成报告
    print("生成监控报告...")
    current_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    # 读取上一次的监控报告以比较变化
    previous_lines = 0
    if os.path.exists(log_path):
        try:
            with open(log_path, 'r', encoding='utf-8') as f:
                previous_content = f.read()
                # 尝试提取上一次的代码行数
                match = re.search(r'总代码行数：(\d+)行', previous_content)
                if match:
                    previous_lines = int(match.group(1))
        except:
            pass
    
    # 计算变化
    lines_change = code_stats['total_lines'] - previous_lines
    lines_change_text = f"{'+' if lines_change > 0 else ''}{lines_change}行" if lines_change != 0 else "无变化"
    
    # 生成Markdown报告
    report = f"""## 综合监控报告 - {current_time}

### 代码量统计（统一口径）
- 总文件数：{code_stats['total_files']}个
- 总代码行数：{code_stats['total_lines']}行
- 文件类型分布：
  - Java: {code_stats['by_type']['Java']['files']}个文件
  - JavaScript: {code_stats['by_type']['JavaScript']['files']}个文件
  - Dart: {code_stats['by_type']['Dart']['files']}个文件
  - Rust: {code_stats['by_type']['Rust']['files']}个文件
  - HTML/CSS: {code_stats['by_type']['HTML/CSS']['files']}个文件

### 架构设计情况
- 规划中功能：{roadmap_info['planning_count']}个
- 新增功能点：
"""
    
    if roadmap_info['new_features']:
        for feature in roadmap_info['new_features'][:5]:  # 最多显示5个
            report += f"  - {feature}\n"
    else:
        report += "  - 无新增功能点\n"
    
    report += f"""
### 学习情况
- 知识库总文件数：{knowledge_info['total_files']}个
- 新增知识文件：{len(knowledge_info['new_files'])}个
"""
    
    if knowledge_info['new_files']:
        report += "  - " + "\n  - ".join(knowledge_info['new_files']) + "\n"
    
    report += "- 新知识点：\n"
    if knowledge_info['new_knowledge']:
        for knowledge in knowledge_info['new_knowledge'][:3]:  # 最多显示3个
            report += f"  - {knowledge}\n"
    else:
        report += "  - 无新知识点\n"
    
    report += """
### 开发功能列表
| 功能名称 | 状态 |
|----------|------|
"""
    
    # 添加功能表格
    for feature_name, status in dev_plan_info['features'][:15]:  # 最多显示15个
        report += f"| {feature_name} | {status} |\n"
    
    if len(dev_plan_info['features']) > 15:
        report += f"| ... (共{len(dev_plan_info['features'])}个功能) | ... |\n"
    elif not dev_plan_info['features']:
        report += "| (未解析到功能列表) | |\n"
    
    total_features = sum(dev_plan_info['status_counts'].values())
    report += f"""
状态统计：
- ✅ 已完成：{dev_plan_info['status_counts']['✅ 已完成']}个
- 🔄 开发中：{dev_plan_info['status_counts']['🔄 开发中']}个
- 📋 待开发：{dev_plan_info['status_counts']['📋 待开发']}个
  - ⚠️ 待人工解决：{dev_plan_info['status_counts']['⚠️ 待人工解决']}个
- 总计：{total_features}个功能

### 总结
- 代码量变化：{lines_change_text}
- 功能模块变化：共{total_features}个功能
- 知识库变化：新增{len(knowledge_info['new_files'])}个文件

---
*监控时间：{current_time}*
*监控目录：{projects_dir}*
"""
    
    # 保存报告
    with open(log_path, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"监控报告已保存到: {log_path}")
    
    # 返回摘要
    return {
        'code_files': code_stats['total_files'],
        'code_lines': code_stats['total_lines'],
        'planning_features': roadmap_info['planning_count'],
        'knowledge_files': knowledge_info['total_files'],
        'new_knowledge_files': len(knowledge_info['new_files']),
        'total_features': total_features,
        'completed_features': dev_plan_info['status_counts']['✅ 已完成'],
        'developing_features': dev_plan_info['status_counts']['🔄 开发中'],
        'todo_features': dev_plan_info['status_counts']['📋 待开发'],
        'manual_features': dev_plan_info['status_counts']['⚠️ 待人工解决'],
        'lines_change': lines_change_text
    }

if __name__ == "__main__":
    result = main()
    print("\n监控摘要:")
    print(f"- 代码文件: {result['code_files']}个")
    print(f"- 代码行数: {result['code_lines']}行")
    print(f"- 规划功能: {result['planning_features']}个")
    print(f"- 知识文件: {result['knowledge_files']}个 (新增{result['new_knowledge_files']}个)")
    print(f"- 功能总数: {result['total_features']}个")
    print(f"  - 已完成: {result['completed_features']}个")
    print(f"  - 开发中: {result['developing_features']}个")
    print(f"  - 待开发: {result['todo_features']}个")
    print(f"  - 待人工: {result['manual_features']}个")
    print(f"- 代码变化: {result['lines_change']}")