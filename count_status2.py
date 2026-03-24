import re

def count_feature_status(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    completed = len(re.findall(r'状态:\s*已完成', content))
    developing = len(re.findall(r'状态:\s*开发中', content))
    pending = len(re.findall(r'状态:\s*待开发', content))
    manual = len(re.findall(r'状态:\s*待人工解决', content))
    
    print("=== 开发功能状态统计 ===")
    print(f"已完成: {completed}个")
    print(f"开发中: {developing}个")
    print(f"待开发: {pending}个")
    print(f"待人工解决: {manual}个")
    
    return completed, developing, pending, manual

if __name__ == "__main__":
    dev_plan = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
    count_feature_status(dev_plan)