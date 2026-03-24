import os
import glob
from datetime import datetime

def count_code_lines(file_path):
    """统计文件行数"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return sum(1 for _ in f)
    except:
        try:
            with open(file_path, 'r', encoding='gbk') as f:
                return sum(1 for _ in f)
        except:
            return 0

def main():
    project_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
    knowledge_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\knowledge"
    logs_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\logs"
    
    # 确保日志目录存在
    os.makedirs(logs_path, exist_ok=True)
    
    # 定义文件类型映射
    file_types = {
        '.java': 'Java',
        '.js': 'JavaScript',
        '.ts': 'TypeScript',
        '.tsx': 'TypeScript (React)',
        '.jsx': 'JavaScript (React)',
        '.dart': 'Dart',
        '.rs': 'Rust',
        '.html': 'HTML',
        '.css': 'CSS',
        '.xml': 'XML'
    }
    
    print("开始统计代码量...")
    print(f"项目路径: {project_path}")
    
    # 统计代码文件
    stats = {}
    total_files = 0
    total_lines = 0
    
    for ext, type_name in file_types.items():
        pattern = os.path.join(project_path, '**', f'*{ext}')
        files = glob.glob(pattern, recursive=True)
        
        if files:
            file_count = len(files)
            line_count = sum(count_code_lines(f) for f in files)
            
            stats[type_name] = {
                'file_count': file_count,
                'line_count': line_count
            }
            
            total_files += file_count
            total_lines += line_count
            
            print(f"  {type_name}: {file_count} 个文件, {line_count} 行")
    
    # 检查架构设计情况
    roadmap_path = os.path.join(project_path, "roadmap.md")
    planning_count = 0
    new_features = []
    
    if os.path.exists(roadmap_path):
        print(f"检查架构设计文件: {roadmap_path}")
        try:
            with open(roadmap_path, 'r', encoding='utf-8') as f:
                roadmap_content = f.read()
            
            last_modified = datetime.fromtimestamp(os.path.getmtime(roadmap_path))
            
            # 简单分析规划中功能
            lines = roadmap_content.split('\n')
            for line in lines:
                if any(marker in line for marker in ['- [', '* ', '+ ']):
                    planning_count += 1
                if any(keyword in line for keyword in ['新增', '新功能', 'new feature']):
                    new_features.append(line.strip())
            
            print(f"  规划中功能: {planning_count} 个")
        except Exception as e:
            print(f"  读取架构设计文件时出错: {e}")
    
    # 检查学习情况
    new_knowledge_files = []
    new_knowledge_count = 0
    
    if os.path.exists(knowledge_path):
        print(f"检查知识库目录: {knowledge_path}")
        try:
            # 获取最近24小时修改的文件
            cutoff_time = datetime.now().timestamp() - 24 * 3600
            for root, dirs, files in os.walk(knowledge_path):
                for file in files:
                    file_path = os.path.join(root, file)
                    if os.path.getmtime(file_path) > cutoff_time:
                        new_knowledge_files.append(file)
                        new_knowledge_count += 1
            
            print(f"  最近24小时新增/修改的文件: {new_knowledge_count} 个")
        except Exception as e:
            print(f"  检查知识库时出错: {e}")
    
    # 检查开发功能列表
    dev_plan_path = os.path.join(project_path, "development_plan.md")
    completed_count = 0
    in_progress_count = 0
    pending_count = 0
    manual_resolve_count = 0
    feature_list = []
    
    if os.path.exists(dev_plan_path):
        print(f"读取开发功能列表: {dev_plan_path}")
        try:
            with open(dev_plan_path, 'r', encoding='utf-8') as f:
                dev_plan_content = f.read()
            
            lines = dev_plan_content.split('\n')
            for line in lines:
                # 检查表格行
                if line.strip().startswith('|') and '|' in line:
                    parts = [p.strip() for p in line.split('|')]
                    if len(parts) >= 3 and parts[1] and parts[2]:
                        feature_name = parts[1]
                        status = parts[2]
                        
                        feature_list.append({
                            'name': feature_name,
                            'status': status
                        })
                        
                        if '✅' in status or '完成' in status:
                            completed_count += 1
                        elif '🔄' in status or '开发中' in status:
                            in_progress_count += 1
                        elif '📋' in status or '待开发' in status:
                            pending_count += 1
                        elif '⚠️' in status or '待人工解决' in status:
                            manual_resolve_count += 1
            
            print(f"  开发功能总数: {len(feature_list)} 个")
        except Exception as e:
            print(f"  读取开发计划时出错: {e}")
    
    # 生成报告
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    report = f"""## 综合监控报告 - {current_time}

### 代码量统计（统一口径）
- 总文件数：{total_files}
- 总代码行数：{total_lines}
- 文件类型分布："""
    
    for type_name, data in stats.items():
        report += f"\n  - {type_name}: {data['file_count']}个文件, {data['line_count']}行"
    
    report += f"""

### 架构设计情况
- 规划中功能：{planning_count} 个
- 新增功能点："""
    
    if new_features:
        for feature in new_features[:5]:  # 只显示前5个
            report += f"\n  - {feature}"
        if len(new_features) > 5:
            report += f"\n  - ... (还有 {len(new_features)-5} 个)"
    else:
        report += "\n  - 无新增功能点"
    
    report += f"""

### 学习情况
- 新增知识文件：{new_knowledge_count} 个"""
    
    if new_knowledge_files:
        report += "\n- 新知识点："
        for file in new_knowledge_files[:5]:  # 只显示前5个
            report += f"\n  - {file}"
        if len(new_knowledge_files) > 5:
            report += f"\n  - ... (还有 {len(new_knowledge_files)-5} 个)"
    else:
        report += "\n- 新知识点：无新增"
    
    report += f"""

### 开发功能列表
| 功能名称 | 状态 |
|----------|------|
"""
    
    for feature in feature_list:
        report += f"| {feature['name']} | {feature['status']} |\n"
    
    report += f"""
状态统计：
- ✅ 已完成：{completed_count} 个
- 🔄 开发中：{in_progress_count} 个
- 📋 待开发：{pending_count} 个
  - ⚠️ 待人工解决：{manual_resolve_count} 个

### 总结
- 代码量变化：需要与上次统计数据对比
- 功能模块变化：需要对比分析
- 知识库变化：最近24小时新增/修改 {new_knowledge_count} 个文件

---
*统计路径：{project_path}*
*知识库路径：{knowledge_path}*
"""
    
    # 保存报告
    output_path = os.path.join(logs_path, "code_volume_monitor.md")
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"\n报告已保存到: {output_path}")
    
    # 显示报告摘要
    print("\n=== 监控报告摘要 ===")
    print(f"代码量: {total_files} 个文件, {total_lines} 行")
    print(f"开发功能: ✅{completed_count} 🔄{in_progress_count} 📋{pending_count} ⚠️{manual_resolve_count}")
    print(f"架构设计: {planning_count} 个规划功能")
    print(f"知识库: {new_knowledge_count} 个新文件")

if __name__ == "__main__":
    main()