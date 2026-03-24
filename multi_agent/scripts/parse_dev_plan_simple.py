import os
import re

def parse_development_plan_simple(file_path):
    """简单解析开发计划文件"""
    if not os.path.exists(file_path):
        return [], 0, 0, 0, 0, 0, 0, 0
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 提取统计信息
    completed_match = re.search(r'已完成功能\s*:\s*(\d+)\s*个', content)
    completed_count = int(completed_match.group(1)) if completed_match else 0
    
    pending_match = re.search(r'待开发功能\s*:\s*(\d+)\s*个', content)
    pending_count = int(pending_match.group(1)) if pending_match else 0
    
    new_match = re.search(r'新增 (\d+) 个功能', content)
    new_count = int(new_match.group(1)) if new_match else 0
    
    # 统计各种状态
    completed_text = 0
    in_progress_text = 0
    pending_text = 0
    manual_text = 0
    
    # 从文本中查找状态
    completed_text = content.count("状态: 已完成") + content.count("状态: ✅")
    in_progress_text = content.count("状态: 开发中") + content.count("状态: 🔄")
    pending_text = content.count("状态: 待开发") + content.count("状态: 📋")
    manual_text = content.count("状态: 待人工解决") + content.count("状态: ⚠️")
    
    return completed_count, pending_count, new_count, completed_text, in_progress_text, pending_text, manual_text

def main():
    project_path = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects"
    dev_plan_path = os.path.join(project_path, "development_plan.md")
    
    completed_count, pending_count, new_count, completed_text, in_progress_text, pending_text, manual_text = parse_development_plan_simple(dev_plan_path)
    
    print("=== 开发计划解析结果 ===")
    print(f"已完成功能: {completed_count} 个 (从统计)")
    print(f"待开发功能: {pending_count} 个 (从统计)")
    print(f"新增功能: {new_count} 个")
    print("\n从文本匹配的状态:")
    print(f"  已完成: {completed_text} 个")
    print(f"  开发中: {in_progress_text} 个")
    print(f"  待开发: {pending_text} 个")
    print(f"  待人工解决: {manual_text} 个")
    
    # 使用文本匹配的状态，因为更准确
    return completed_text, in_progress_text, pending_text, manual_text

if __name__ == "__main__":
    main()