import os
import re
from datetime import datetime

def parse_development_plan(file_path):
    """解析开发计划文件，提取功能状态"""
    if not os.path.exists(file_path):
        return [], 0, 0, 0, 0, 0
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 首先提取已完成功能数量
    completed_match = re.search(r'已完成功能\s*:\s*(\d+)\s*个', content)
    completed_count = int(completed_match.group(1)) if completed_match else 0
    
    # 提取待开发功能数量
    pending_match = re.search(r'待开发功能\s*:\s*(\d+)\s*个', content)
    pending_count = int(pending_match.group(1)) if pending_match else 0
    
    # 提取新增功能数量
    new_match = re.search(r'新增 (\d+) 个功能', content)
    new_count = int(new_match.group(1)) if new_match else 0
    
    # 提取功能列表
    features = []
    
    # 查找所有功能块
    # 格式: "### [数字]. [功能名称]" 或 "### [数字]. [功能描述]"
    pattern = r'### (\d+)\.\s+([^\n]+)([\s\S]*?)(?=### \d+\.|\*开发计划最后更新|\Z)'
    
    for match in re.finditer(pattern, content):
        func_num = match.group(1)
        func_name = match.group(2).strip()
        func_content = match.group(3)
        
        # 提取状态
        status = "❓ 未知"
        priority = "🟢 低"
        
        # 查找状态行
        status_match = re.search(r'状态\s*:\s*([^\n]+)', func_content)
        if status_match:
            status = status_match.group(1).strip()
        
        # 查找优先级
        priority_match = re.search(r'优先级\s*:\s*([^\n]+)', func_content)
        if priority_match:
            priority = priority_match.group(1).strip()
        
        # 简化为统一状态
        final_status = "📋 待开发"
        if "✅" in status or "已完成" in status or "完成" in status:
            final_status = "✅ 已完成"
        elif "🔄" in status or "开发中" in status:
            final_status = "🔄 开发中"
        elif "⚠️" in status or "待人工解决" in status:
            final_status = "⚠️ 待人工解决"
        
        features.append({
            'number': func_num,
            'name': func_name,
            'status': final_status,
            'priority': priority,
            'raw_status': status
        })
    
    return features, completed_count, pending_count, new_count

def main():
    project_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
    dev_plan_path = os.path.join(project_path, "development_plan.md")
    
    if not os.path.exists(dev_plan_path):
        print("开发计划文件不存在")
        return
    
    features, completed_count, pending_count, new_count = parse_development_plan(dev_plan_path)
    
    print(f"解析到 {len(features)} 个功能")
    print(f"已完成功能: {completed_count} 个")
    print(f"待开发功能: {pending_count} 个")
    print(f"新增功能: {new_count} 个")
    
    # 统计状态
    status_counts = {
        '✅ 已完成': 0,
        '🔄 开发中': 0,
        '📋 待开发': 0,
        '⚠️ 待人工解决': 0
    }
    
    for feature in features:
        status_counts[feature['status']] = status_counts.get(feature['status'], 0) + 1
    
    print("\n状态统计:")
    for status, count in status_counts.items():
        print(f"  {status}: {count} 个")
    
    # 输出前10个功能
    print("\n前10个功能:")
    for i, feature in enumerate(features[:10]):
        print(f"  {feature['number']}. {feature['name']} - {feature['status']}")

if __name__ == "__main__":
    main()