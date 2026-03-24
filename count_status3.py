import re

def count_feature_status(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 注意：中文冒号后面可能有空格
    completed = len(re.findall(r'状态:\s*已完成', content))
    developing = len(re.findall(r'状态:\s*开发中', content))
    pending = len(re.findall(r'状态:\s*待开发', content))
    manual = len(re.findall(r'状态:\s*待人工解决', content))
    
    print("=== 开发功能状态统计 ===")
    print(f"已完成: {completed}个")
    print(f"开发中: {developing}个")
    print(f"待开发: {pending}个")
    print(f"待人工解决: {manual}个")
    
    # 尝试手动搜索更多状态
    print("\n=== 详细搜索 ===")
    status_pattern = r'状态:\s*(.*?)(?=\s|$)'
    all_status = re.findall(status_pattern, content)
    status_counts = {}
    for status in all_status:
        if status not in status_counts:
            status_counts[status] = 0
        status_counts[status] += 1
    
    for status, count in status_counts.items():
        print(f"{status}: {count}个")
    
    return completed, developing, pending, manual

if __name__ == "__main__":
    dev_plan = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
    count_feature_status(dev_plan)