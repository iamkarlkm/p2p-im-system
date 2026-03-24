import re

def parse_development_plan(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 查找所有功能块
    # 功能块以 "### X." 开头，包含功能描述
    pattern = r'### (\d+)\. (.+?)(?=###|\Z)'
    features = re.findall(pattern, content, re.DOTALL)
    
    completed = 0
    developing = 0
    pending = 0
    manual = 0
    total = 0
    
    feature_list = []
    
    for num, feature_text in features:
        total += 1
        # 查找状态
        status_match = re.search(r'状态:\s*(\S+)', feature_text)
        status = status_match.group(1) if status_match else "待开发"
        
        # 查找功能名称
        name_match = re.search(r'- \*\*功能描述\*\*:\s*(.+)', feature_text)
        name = name_match.group(1).strip() if name_match else f"功能{num}"
        
        # 分类状态
        if status == "已完成":
            completed += 1
            status_display = "✅ 已完成"
        elif status == "开发中":
            developing += 1
            status_display = "🔄 开发中"
        elif status == "待开发":
            pending += 1
            status_display = "📋 待开发"
        elif status == "待人工解决":
            manual += 1
            status_display = "⚠️ 待人工解决"
        else:
            pending += 1
            status_display = "📋 待开发"
        
        feature_list.append((name, status_display))
    
    return total, completed, developing, pending, manual, feature_list

if __name__ == "__main__":
    dev_plan = r"C:\Users\Administrator\.openclaw\workspace-clawd3\multi_agent\projects\development_plan.md"
    total, completed, developing, pending, manual, features = parse_development_plan(dev_plan)
    
    print("=== 开发功能状态统计 ===")
    print(f"总功能数: {total}个")
    print(f"✅ 已完成: {completed}个")
    print(f"🔄 开发中: {developing}个") 
    print(f"📋 待开发: {pending}个")
    print(f"⚠️ 待人工解决: {manual}个")
    
    print("\n=== 功能列表（部分） ===")
    for i, (name, status) in enumerate(features[:20], 1):
        print(f"{i}. {name} - {status}")