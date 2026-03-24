import os
import sys
import datetime
from pathlib import Path

# 配置路径
BASE_DIR = Path(r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent")
PROJECTS_DIR = BASE_DIR / "projects"
KNOWLEDGE_DIR = BASE_DIR / "knowledge"
LOGS_DIR = BASE_DIR / "logs"

# 确保日志目录存在
LOGS_DIR.mkdir(exist_ok=True)

# 文件类型映射
FILE_TYPES = {
    'java': ['.java'],
    'javascript': ['.js', '.ts', '.tsx', '.jsx'],
    'dart': ['.dart'],
    'rust': ['.rs'],
    'html_css': ['.html', '.css']
}

def count_code_files(directory):
    """统计代码文件数量和行数"""
    stats = {
        'total_files': 0,
        'total_lines': 0,
        'by_type': {key: {'files': 0, 'lines': 0} for key in FILE_TYPES.keys()}
    }
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            file_path = os.path.join(root, file)
            ext = os.path.splitext(file)[1].lower()
            
            # 检查文件类型
            file_type = None
            for type_name, extensions in FILE_TYPES.items():
                if ext in extensions:
                    file_type = type_name
                    break
            
            if file_type:
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        lines = sum(1 for _ in f)
                    
                    stats['total_files'] += 1
                    stats['total_lines'] += lines
                    stats['by_type'][file_type]['files'] += 1
                    stats['by_type'][file_type]['lines'] += lines
                except Exception as e:
                    # 跳过无法读取的文件
                    continue
    
    return stats

def check_roadmap():
    """检查roadmap.md更新情况"""
    roadmap_path = PROJECTS_DIR / "roadmap.md"
    if not roadmap_path.exists():
        return {"exists": False, "planning_features": 0, "new_features": []}
    
    try:
        with open(roadmap_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 简单统计规划中功能（通过TODO或规划标记）
        planning_features = content.count('TODO') + content.count('规划') + content.count('待实现')
        
        # 寻找新功能点（这是一个简化版本，实际可能需要更复杂的解析）
        new_features = []
        lines = content.split('\n')
        for i, line in enumerate(lines):
            if '新增' in line or '新功能' in line or 'feature' in line.lower():
                # 获取上下文
                context = line.strip()
                if i > 0:
                    context = lines[i-1].strip() + " | " + context
                if i < len(lines)-1:
                    context = context + " | " + lines[i+1].strip()
                new_features.append(context[:100])  # 限制长度
        
        return {
            "exists": True,
            "planning_features": planning_features,
            "new_features": new_features[:5]  # 只取前5个
        }
    except Exception as e:
        return {"exists": False, "error": str(e)}

def check_knowledge():
    """检查知识库更新"""
    if not KNOWLEDGE_DIR.exists():
        return {"exists": False, "new_files": 0, "new_points": []}
    
    try:
        # 获取知识库文件列表
        knowledge_files = list(KNOWLEDGE_DIR.glob("*.md"))
        
        # 检查是否有学习日志
        learning_log_path = KNOWLEDGE_DIR / "learning_log.md"
        new_points = []
        
        if learning_log_path.exists():
            try:
                with open(learning_log_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # 查找最近的学习点（最后10行）
                lines = content.split('\n')
                recent_lines = lines[-20:] if len(lines) > 20 else lines
                for line in recent_lines:
                    if line.strip() and '学习' in line or '了解' in line or '研究' in line:
                        new_points.append(line.strip()[:80])
            except:
                pass
        
        return {
            "exists": True,
            "total_files": len(knowledge_files),
            "new_points": new_points[:5]  # 只取前5个
        }
    except Exception as e:
        return {"exists": False, "error": str(e)}

def check_development_plan():
    """读取开发计划文件"""
    plan_path = PROJECTS_DIR / "development_plan.md"
    if not plan_path.exists():
        return {"exists": False}
    
    try:
        with open(plan_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 解析功能状态
        features = []
        status_counts = {
            'completed': 0,
            'in_progress': 0,
            'pending': 0,
            'manual_resolution': 0
        }
        
        lines = content.split('\n')
        current_feature = None
        
        for line in lines:
            line = line.strip()
            
            # 检测功能行
            if line.startswith('- [ ]') or line.startswith('- [x]') or line.startswith('- [!]'):
                # 提取状态和名称
                if '- [x]' in line:
                    status = '✅ 已完成'
                    status_counts['completed'] += 1
                elif '- [!]' in line:
                    status = '⚠️ 待人工解决'
                    status_counts['manual_resolution'] += 1
                elif '- [ ]' in line and '进行中' in line:
                    status = '🔄 开发中'
                    status_counts['in_progress'] += 1
                else:
                    status = '📋 待开发'
                    status_counts['pending'] += 1
                
                # 提取功能名称
                name = line.replace('- [x]', '').replace('- [ ]', '').replace('- [!]', '').strip()
                if '进行中' in name:
                    name = name.replace('进行中', '').strip()
                
                if name:
                    features.append({"name": name, "status": status})
        
        return {
            "exists": True,
            "features": features,
            "status_counts": status_counts
        }
    except Exception as e:
        return {"exists": False, "error": str(e)}

def generate_report():
    """生成监控报告"""
    current_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    print(f"开始监控任务: {current_time}")
    
    # 1. 代码量统计
    print("1. 正在统计代码量...")
    code_stats = count_code_files(PROJECTS_DIR)
    
    # 2. 检查架构设计
    print("2. 正在检查架构设计...")
    roadmap_info = check_roadmap()
    
    # 3. 检查学习情况
    print("3. 正在检查学习情况...")
    knowledge_info = check_knowledge()
    
    # 4. 检查开发计划
    print("4. 正在检查开发计划...")
    plan_info = check_development_plan()
    
    # 生成报告
    report = f"""## 综合监控报告 - {current_time}

### 代码量统计（统一口径）
- 总文件数：{code_stats['total_files']}个
- 总代码行数：{code_stats['total_lines']}行
- 文件类型分布：
  - Java: {code_stats['by_type']['java']['files']}个文件，{code_stats['by_type']['java']['lines']}行
  - JavaScript/TypeScript: {code_stats['by_type']['javascript']['files']}个文件，{code_stats['by_type']['javascript']['lines']}行
  - Dart: {code_stats['by_type']['dart']['files']}个文件，{code_stats['by_type']['dart']['lines']}行
  - Rust: {code_stats['by_type']['rust']['files']}个文件，{code_stats['by_type']['rust']['lines']}行
  - HTML/CSS: {code_stats['by_type']['html_css']['files']}个文件，{code_stats['by_type']['html_css']['lines']}行

### 架构设计情况
"""
    
    if roadmap_info['exists']:
        report += f"""- roadmap.md文件：存在
- 规划中功能：{roadmap_info['planning_features']}个
- 新增功能点：{len(roadmap_info['new_features'])}个
"""
        if roadmap_info['new_features']:
            report += "  - " + "\n  - ".join(roadmap_info['new_features'])
    else:
        report += "- roadmap.md文件：不存在或无法读取\n"
    
    report += "\n### 学习情况\n"
    
    if knowledge_info['exists']:
        report += f"""- 知识库文件总数：{knowledge_info['total_files']}个
- 新知识点：{len(knowledge_info['new_points'])}个
"""
        if knowledge_info['new_points']:
            report += "  - " + "\n  - ".join(knowledge_info['new_points'])
    else:
        report += "- 知识库目录不存在或无法访问\n"
    
    report += "\n### 开发功能列表\n"
    
    if plan_info['exists'] and 'features' in plan_info:
        report += "| 功能名称 | 状态 |\n|----------|------|\n"
        for feature in plan_info['features'][:15]:  # 只显示前15个
            report += f"| {feature['name'][:50]} | {feature['status']} |\n"
        
        counts = plan_info['status_counts']
        report += f"""
状态统计：
- ✅ 已完成：{counts['completed']}个
- 🔄 开发中：{counts['in_progress']}个
- 📋 待开发：{counts['pending']}个
  - ⚠️ 待人工解决：{counts['manual_resolution']}个
"""
    else:
        report += "- development_plan.md文件不存在或无法解析\n"
    
    report += f"""
### 总结
- 代码量：总计 {code_stats['total_files']} 个文件，{code_stats['total_lines']} 行代码
- 功能模块：开发计划包含 {len(plan_info.get('features', [])) if plan_info.get('exists') else 0} 个功能项
- 知识库：{knowledge_info.get('total_files', 0) if knowledge_info.get('exists') else 0} 个知识文件
- 监控目录：{PROJECTS_DIR}
- 知识库目录：{KNOWLEDGE_DIR}
"""
    
    return report

def save_report(report):
    """保存报告到文件"""
    log_file = LOGS_DIR / "code_volume_monitor.md"
    
    # 读取现有内容（如果存在）
    existing_content = ""
    if log_file.exists():
        try:
            with open(log_file, 'r', encoding='utf-8') as f:
                existing_content = f.read()
        except:
            pass
    
    # 添加分隔线和时间戳
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    new_entry = f"\n{'='*80}\n{report}\n{'='*80}\n"
    
    # 保存（保留最近的历史）
    with open(log_file, 'w', encoding='utf-8') as f:
        # 保留最近10次监控记录
        separator = '=' * 80
        entries = existing_content.split(separator)
        recent_entries = entries[-10:] if len(entries) > 10 else entries
        f.write(separator.join(recent_entries) + new_entry)
    
    return log_file

if __name__ == "__main__":
    try:
        report = generate_report()
        log_file = save_report(report)
        print(f"监控报告已生成并保存到: {log_file}")
        print(report)
    except Exception as e:
        error_msg = f"监控任务执行失败: {str(e)}"
        print(error_msg)
        
        # 保存错误信息
        log_file = LOGS_DIR / "code_volume_monitor.md"
        timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        error_report = f"\n{'='*80}\n## 监控错误 - {timestamp}\n{error_msg}\n{'='*80}\n"
        
        try:
            if log_file.exists():
                with open(log_file, 'a', encoding='utf-8') as f:
                    f.write(error_report)
            else:
                with open(log_file, 'w', encoding='utf-8') as f:
                    f.write(error_report)
        except:
            pass